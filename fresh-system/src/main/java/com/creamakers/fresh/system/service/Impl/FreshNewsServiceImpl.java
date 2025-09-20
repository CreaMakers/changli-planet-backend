package com.creamakers.fresh.system.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.fresh.system.dao.FreshNewsLikesMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.dao.TagsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.Tags;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsResp;
import com.creamakers.fresh.system.service.FreshNewsService;
import com.creamakers.fresh.system.service.word.WordService;
import com.creamakers.fresh.system.utils.HUAWEIOBSUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.creamakers.fresh.system.constants.CommonConst.*;

@Service
public class FreshNewsServiceImpl implements FreshNewsService {
    @Autowired
    private FreshNewsMapper freshNewsMapper;
    @Autowired
    private FreshNewsLikesMapper freshNewsLikesMapper;
    @Autowired
    private TagsMapper tagsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WordService wordService;
    @Override
    @Transactional
    public ResultVo<FreshNewsResp> createFreshNews(List<MultipartFile> images, FreshNewsRequest freshNewsRequest) throws IOException {
        if (StringUtil.isEmpty(freshNewsRequest.getContent())) {
            return ResultVo.fail(FRESH_NEWS_CONTENT_CANNOT_BE_EMPTY_MESSAGE);
        }
        if (!CollectionUtils.isEmpty(images) && images.size() > 9) {
            return ResultVo.fail(IMAGE_COUNT_EXCEEDS_LIMIT_MESSAGE);
        }

        // 敏感词检查
        if(wordService.check(freshNewsRequest.getTitle())){
            // 标题包含敏感词，进行替换
            freshNewsRequest.setTitle(wordService.replace(freshNewsRequest.getTitle()));
        }
        if(wordService.check(freshNewsRequest.getContent())){
            // 内容包含敏感词，进行替换
            freshNewsRequest.setContent(wordService.replace(freshNewsRequest.getContent()));
        }
        if(wordService.check(freshNewsRequest.getTags())){
            // 标签包含敏感词，进行替换
            freshNewsRequest.setTags(wordService.replace(freshNewsRequest.getTags()));
        }

        // 新增标签的逻辑
        if (!StringUtil.isEmpty(freshNewsRequest.getTags())) {
            String[] tags = freshNewsRequest.getTags().split("#");
            for (int i = 1; i < tags.length; i++) {
                String tag = tags[i];

                // 先检查 Redis 中是否存在该标签
                Boolean exists = redisTemplate.hasKey("tags:" + tag); // Redis key 前缀可以自定义

                if (exists == null || !exists) {
                    // Redis 中没有这个标签，插入到 Redis 和数据库
                    redisTemplate.opsForValue().set("tags:" + tag, tag); // 在 Redis 中保存标签

                    // 插入数据库
                    Tags tags1 = new Tags();
                    tags1.setName(tag);
                    tagsMapper.insert(tags1);
                }
            }
        }

        // 图片上传逻辑
        StringBuilder urls = new StringBuilder();
        if (!CollectionUtils.isEmpty(images)) {
            for (MultipartFile image : images) {
                String s = HUAWEIOBSUtil.uploadImage(image, UUID.randomUUID().toString());
                if (urls.length() > 0) {
                    urls.append(",");
                }
                urls.append(s);
            }
        }
        String finalUrls = urls.toString();

        // 创建 FreshNews 实体并插入数据库
        FreshNews freshNews = new FreshNews();
        freshNews.setUserId(freshNewsRequest.getUserId())
                .setTitle(freshNewsRequest.getTitle())
                .setImages(finalUrls)
                .setContent(freshNewsRequest.getContent())
                .setTags(freshNewsRequest.getTags())
                .setLiked(0)
                .setIsDeleted(0)
                .setFavoritesCount(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setAllowComments(freshNewsRequest.getAllowComments());

        int rows = freshNewsMapper.insert(freshNews);
        if (rows > 0) {
            // 获取新鲜事的 ID
            Long freshNewsId = freshNews.getFreshNewsId();

            // 将新鲜事 ID 加入到 Redis 中对应标签的集合
            if (!StringUtil.isEmpty(freshNewsRequest.getTags())) {
                String[] tags = freshNewsRequest.getTags().split("#");
                for (int i = 1; i < tags.length; i++) {
                    String tag = tags[i];

                    // 将新鲜事 ID 加入 Redis 中该标签的集合
                    redisTemplate.opsForSet().add("tags:" + tag, String.valueOf(freshNewsId)); // 将新鲜事 ID 加入 Redis 集合
                }
            }
            // 返回创建的新鲜事响应
            FreshNewsResp freshNewsResp = new FreshNewsResp();
            BeanUtils.copyProperties(freshNews, freshNewsResp);
            return ResultVo.success(freshNewsResp);
        } else {
            return ResultVo.fail(CREATE_FRESH_NEWS_FAILED_MESSAGE);
        }
    }


    @Override
    public ResultVo<FreshNewsDetailResp> getFreshNewsById(Long freshNewsId) {
        FreshNews freshNewsDetail = freshNewsMapper.selectById(freshNewsId);
        if (freshNewsDetail != null) {
            FreshNewsDetailResp freshNewsDetailResp = new FreshNewsDetailResp();

            // 使用 BeanUtils 复制属性
            BeanUtils.copyProperties(freshNewsDetail, freshNewsDetailResp);

            // 处理 images 字段，将逗号分隔的字符串转换为数组
            if (freshNewsDetail.getImages() != null && !freshNewsDetail.getImages().isEmpty()) {
                String[] imageArray = freshNewsDetail.getImages().split(",");
                freshNewsDetailResp.setImages(Arrays.asList(imageArray));  // 转换为 List
            } else {
                freshNewsDetailResp.setImages(new ArrayList<>());  // 空字符串或 null 时返回空数组
            }

            // 处理 tags 字段，将逗号分隔的字符串转换为数组
            if (freshNewsDetail.getTags() != null && !freshNewsDetail.getTags().isEmpty()) {
                String[] tagArray = freshNewsDetail.getTags().split(",");
                freshNewsDetailResp.setTags(Arrays.asList(tagArray));  // 转换为 List
            } else {
                freshNewsDetailResp.setTags(new ArrayList<>());  // 空字符串或 null 时返回空数组
            }

            return ResultVo.success(freshNewsDetailResp);
        } else {
            return ResultVo.fail( FRESH_NEWS_NOT_FOUND_MESSAGE);
        }
    }

    @Override
    public ResultVo<List<FreshNewsResp>> getAllFreshNews(Integer page, Integer pageSize) {
        // 创建分页对象
        Page<FreshNews> pageParam = new Page<>(page, pageSize);

        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam, new QueryWrapper<FreshNews>().eq("is_deleted", 0).orderByDesc("create_time"));

        List<FreshNews> records = pageResult.getRecords();

        List<FreshNewsResp> freshNewsRespList = records.stream()
                    .map(freshNews -> {
                        FreshNewsResp resp = new FreshNewsResp();
                        BeanUtils.copyProperties(freshNews, resp);
                        return resp;
                    })
                    .collect(Collectors.toList());
        return ResultVo.success(freshNewsRespList);
    }

    @Override
    public ResultVo<List<FreshNewsResp>> getAllByLikes(Integer page, Integer pageSize) {
        // 创建分页对象
        Page<FreshNews> pageParam = new Page<>(page, pageSize);

        // 执行分页查询，过滤已删除的记录，并按点赞数降序排列
        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam,
                new QueryWrapper<FreshNews>().eq("is_deleted", 0).orderByDesc("liked", "create_time"));

        // 获取查询结果
        List<FreshNews> records = pageResult.getRecords();

        // 转换查询结果为响应对象
        List<FreshNewsResp> freshNewsRespList = records.stream()
                .map(freshNews -> {
                    FreshNewsResp resp = new FreshNewsResp();
                    BeanUtils.copyProperties(freshNews, resp);
                    return resp;
                })
                .collect(Collectors.toList());

        // 返回分页结果
        return ResultVo.success(freshNewsRespList);
    }


    @Override
    public ResultVo<List<FreshNewsResp>> getByTag(String tag, Integer page, Integer pageSize) {
        Set<String> freshNewsIds = redisTemplate.opsForSet().members("tags:" + tag);
        if (freshNewsIds == null || freshNewsIds.isEmpty()) {
            return ResultVo.fail(NO_FRESH_NEWS_UNDER_TAG_MESSAGE);
        }
        List<Long> freshNewsIdList = freshNewsIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        Page<FreshNews> pageParam = new Page<>(page, pageSize);
        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam,
                new QueryWrapper<FreshNews>()
                        .eq("is_deleted", 0)
                        .in("fresh_news_id", freshNewsIdList)
                        .orderByDesc("create_time")
        );
        List<FreshNews> records = pageResult.getRecords();
        List<FreshNewsResp> freshNewsRespList = records.stream()
                .map(freshNews -> {
                    FreshNewsResp resp = new FreshNewsResp();
                    BeanUtils.copyProperties(freshNews, resp);
                    return resp;
                })
                .collect(Collectors.toList());
        return ResultVo.success(freshNewsRespList);
    }

    @Override
    public ResultVo<FreshNewsResp> deleteFreshNews(Long freshNewsId) {

        if (freshNewsMapper.selectById(freshNewsId) == null) {
            return ResultVo.fail(FRESH_NEWS_NOT_FOUND_MESSAGE);
        }

        FreshNews freshNews = freshNewsMapper.selectById(freshNewsId);
        FreshNewsResp resp = new FreshNewsResp();
        BeanUtils.copyProperties(freshNews, resp);

        freshNewsMapper.deleteById(freshNewsId);

        return ResultVo.success(resp);
    }
}
