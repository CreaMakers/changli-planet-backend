package com.creamakers.fresh.system.service.Impl;


import com.creamakers.fresh.system.constants.RedisKeyConstant;
import com.creamakers.fresh.system.context.UserContext;
import com.creamakers.fresh.system.dao.FreshNewsLikesMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.dao.TagsMapper;
import com.creamakers.fresh.system.dao.UserAdminMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.Tags;
import com.creamakers.fresh.system.domain.dto.UserAdmin;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.domain.vo.request.FreshNewsRequest;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsDetailResp;
import com.creamakers.fresh.system.domain.vo.response.FreshNewsResp;
import com.creamakers.fresh.system.service.FreshNewsCheckService;
import com.creamakers.fresh.system.service.FreshNewsService;
import com.creamakers.fresh.system.service.word.WordService;
import com.creamakers.fresh.system.utils.HUAWEIOBSUtil;
import com.creamakers.fresh.system.utils.RedisUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.creamakers.fresh.system.constants.CommonConst.*;

@Slf4j
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
    @Autowired//新鲜事图片审核
    private FreshNewsCheckService freshNewsCheckService;
    @Autowired
    private UserAdminMapper userAdminMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${planet.freshNews.needAdmin}")
    private boolean needAdmin;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo<FreshNewsResp> createFreshNews(List<MultipartFile> images, FreshNewsRequest freshNewsRequest) throws IOException {
        if (StringUtil.isEmpty(freshNewsRequest.getContent())) {
            return ResultVo.fail(FRESH_NEWS_CONTENT_CANNOT_BE_EMPTY_MESSAGE);
        }
        if (!CollectionUtils.isEmpty(images) && images.size() > 9) {
            return ResultVo.fail(IMAGE_COUNT_EXCEEDS_LIMIT_MESSAGE);
        }
        // 检查用户是否是管理员
        UserAdmin userAdmin = userAdminMapper.selectById(freshNewsRequest.getUserId());
        if(needAdmin && (userAdmin == null || userAdmin.getIsAdmin() == 0)){
            return ResultVo.fail(UNAUTHORIZED,FRESH_NEWS_NOT_ADMIN);
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
        if (!CollectionUtils.isEmpty(images) && !images.getFirst().getOriginalFilename().isEmpty()) {
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
                .setAddress(freshNewsRequest.getAddress())
                .setImages(finalUrls.isEmpty() ? "" : IMAGE_NEED_CHECK)
                .setContent(freshNewsRequest.getContent())
                .setTags(freshNewsRequest.getTags())
                .setLiked(0L)
                .setIsDeleted(0)
                .setFavoritesCount(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setAllowComments(freshNewsRequest.getAllowComments())
                .setIsActive(finalUrls.isEmpty() ? 1 : 0);

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

            if(!finalUrls.isEmpty()){
                // 图片非空时，上传华为云OBS，图片路径提交审核，返回"图片正在审核中"，审核通过再添加图片路径
                try {
                    boolean check = freshNewsCheckService.addFreshNewsCheck(freshNews, finalUrls);
                } catch (Exception e) {
                    throw new IOException(e);
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
        log.info("根据新鲜事ID查询新鲜事: {}",freshNewsId);
        FreshNewsDetailResp freshNewsDetailResp = redisUtil.queryWithPassThrough(RedisKeyConstant.FRESH_NEWS_CACHE, freshNewsId,
                                                                                FreshNewsDetailResp.class,
                                                                                1L, TimeUnit.DAYS,
                                                                                this::queryFreshNewsDetailRespById);
        if(freshNewsDetailResp == null){
            return ResultVo.fail(NOT_FOUND,FRESH_NEWS_NOT_FOUND_MESSAGE);
        }else {
            //合并redis中的点赞数
            Integer redisLiked = (Integer) redisTemplate.opsForValue().get( RedisKeyConstant.LIKE_NEWS_NUM+freshNewsId);
            Long dbLike = freshNewsDetailResp.getLiked();
            freshNewsDetailResp.setLiked(redisLiked==null ? dbLike : redisLiked+dbLike);
            return ResultVo.success(freshNewsDetailResp);
        }

//        FreshNews freshNewsDetail = freshNewsMapper.selectById(freshNewsId);
//        if (freshNewsDetail != null) {
//            FreshNewsDetailResp freshNewsDetailResp = new FreshNewsDetailResp();
//
//            // 使用 BeanUtils 复制属性
//            BeanUtils.copyProperties(freshNewsDetail, freshNewsDetailResp);
//
//            // 处理 images 字段，将逗号分隔的字符串转换为数组
//            if (freshNewsDetail.getImages() != null && !freshNewsDetail.getImages().isEmpty()) {
//                String[] imageArray = freshNewsDetail.getImages().split(",");
//                freshNewsDetailResp.setImages(Arrays.asList(imageArray));  // 转换为 List
//            } else {
//                freshNewsDetailResp.setImages(new ArrayList<>());  // 空字符串或 null 时返回空数组
//            }
//
//            // 处理 tags 字段，将逗号分隔的字符串转换为数组
//            if (freshNewsDetail.getTags() != null && !freshNewsDetail.getTags().isEmpty()) {
//                String[] tagArray = freshNewsDetail.getTags().split(",");
//                freshNewsDetailResp.setTags(Arrays.asList(tagArray));  // 转换为 List
//            } else {
//                freshNewsDetailResp.setTags(new ArrayList<>());  // 空字符串或 null 时返回空数组
//            }
//
//            //合并redis中的点赞数
//            Integer redisLiked = (Integer) redisTemplate.opsForValue().get( RedisKeyConstant.LIKE_NEWS_NUM+freshNewsId);
//            Long dbLike = freshNewsDetailResp.getLiked();
//            freshNewsDetailResp.setLiked(redisLiked==null ? dbLike : redisLiked+dbLike);
//
//            return ResultVo.success(freshNewsDetailResp);
//        } else {
//            return ResultVo.fail( FRESH_NEWS_NOT_FOUND_MESSAGE);
//        }
    }

    //数据库查询新鲜事
    private FreshNewsDetailResp queryFreshNewsDetailRespById(Long freshNewsId) {
        log.info("根据新鲜事ID数据库查询新鲜事: {}",freshNewsId);
        FreshNews freshNewsDetail = freshNewsMapper.selectById(freshNewsId);
        if (freshNewsDetail == null) {
            return null;
        }

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

        return freshNewsDetailResp;
    }

    private FreshNewsResp transformToResp(FreshNewsDetailResp freshNewsDetailResp){
        FreshNewsResp freshNewsResp = new FreshNewsResp();
        BeanUtils.copyProperties(freshNewsDetailResp, freshNewsResp);
        // 处理 images 字段，将数组转换为逗号分隔的字符串
        String imagesStr = String.join(",", freshNewsDetailResp.getImages());
        freshNewsResp.setImages(imagesStr);
        // 处理 tags 字段，将数组转换为逗号分隔的字符串
        String tagsStr = String.join(",", freshNewsDetailResp.getTags());
        freshNewsResp.setTags(tagsStr);
        return freshNewsResp;
    }

    @Override
    public ResultVo<List<FreshNewsResp>> getAllFreshNews(Integer page, Integer pageSize) {
        // 分页查询新鲜事id列表
        List<Long> idlist = freshNewsMapper.selectFreshNewsIdsByPageOrderByCreateTime((page-1)*pageSize, pageSize);
        log.info("分页查询新鲜事id列表: {}",idlist);

        List<FreshNewsResp> freshNewsDetailRespList = new ArrayList<>(idlist.size());
        for (Long id : idlist) {
            FreshNewsDetailResp freshNewsDetailResp = redisUtil.queryWithPassThrough(RedisKeyConstant.FRESH_NEWS_CACHE, id,
                    FreshNewsDetailResp.class,
                    1L, TimeUnit.DAYS,
                    this::queryFreshNewsDetailRespById);
            if(freshNewsDetailResp != null){
                //合并redis中的点赞数
                Integer redisLiked = (Integer) redisTemplate.opsForValue().get( RedisKeyConstant.LIKE_NEWS_NUM+id);
                Long dbLike = freshNewsDetailResp.getLiked();
                freshNewsDetailResp.setLiked(redisLiked==null ? dbLike : redisLiked+dbLike);

                freshNewsDetailRespList.add(transformToResp(freshNewsDetailResp));
            }
        }

        return ResultVo.success(freshNewsDetailRespList);

//        // 创建分页对象
//        Page<FreshNews> pageParam = new Page<>(page, pageSize);
//
//        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam, new QueryWrapper<FreshNews>().eq("is_deleted", 0).eq("is_active", 1).orderByDesc("create_time"));
//
//        List<FreshNews> records = pageResult.getRecords();
//
//        List<FreshNewsResp> freshNewsRespList = records.stream()
//                    .map(freshNews -> {
//                        FreshNewsResp resp = new FreshNewsResp();
//                        BeanUtils.copyProperties(freshNews, resp);
//
//                        //合并redis中的点赞数
//                        Integer redisLiked = (Integer) redisTemplate.opsForValue().get( RedisKeyConstant.LIKE_NEWS_NUM+resp.getFreshNewsId());
//                        Long dbLike = resp.getLiked();
//                        resp.setLiked(redisLiked==null ? dbLike : redisLiked+dbLike);
//
//                        return resp;
//                    })
//                    .collect(Collectors.toList());
//        return ResultVo.success(freshNewsRespList);
    }

    @Override
    public ResultVo<List<FreshNewsResp>> getAllByLikes(Integer page, Integer pageSize) {
        log.info("根据点赞数分页查询新鲜事,分页参数: {}, {}",page,pageSize);
        List<FreshNewsResp> FreshNewsDetailRespList = getAllFreshNews(page, pageSize).getData();
        // 按点赞数排序
        FreshNewsDetailRespList.sort(Comparator.comparingLong(FreshNewsResp::getLiked).reversed());
        return ResultVo.success(FreshNewsDetailRespList);

//        // 创建分页对象
//        Page<FreshNews> pageParam = new Page<>(page, pageSize);
//
//        // 执行分页查询，过滤已删除的记录和未通过审核的记录，并按点赞数降序排列
//        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam,
//                new QueryWrapper<FreshNews>().eq("is_deleted", 0).eq("is_active", 1).orderByDesc("liked", "create_time"));
//
//        // 获取查询结果
//        List<FreshNews> records = pageResult.getRecords();
//
//        // 转换查询结果为响应对象
//        List<FreshNewsResp> freshNewsRespList = records.stream()
//                .map(freshNews -> {
//                    FreshNewsResp resp = new FreshNewsResp();
//                    BeanUtils.copyProperties(freshNews, resp);
//
//                    //合并redis中的点赞数
//                    Integer redisLiked = (Integer) redisTemplate.opsForValue().get(RedisKeyConstant.LIKE_NEWS_NUM + resp.getFreshNewsId());
//                    Long dbLike = resp.getLiked();
//                    resp.setLiked(redisLiked == null ? dbLike : redisLiked + dbLike);
//
//                    return resp;
//                })
//                .sorted(Comparator.comparingLong(FreshNewsResp::getLiked).reversed())//根据点赞数排序
//                .collect(Collectors.toList());
//        // 返回分页结果
//        return ResultVo.success(freshNewsRespList);
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

        List<FreshNewsResp> freshNewsDetailRespList = new ArrayList<>(freshNewsIdList.size());
        for (Long id : freshNewsIdList) {
            FreshNewsDetailResp freshNewsDetailResp = getFreshNewsById(id).getData();
            if (freshNewsDetailResp != null) {
                freshNewsDetailRespList.add(transformToResp(freshNewsDetailResp));
            }
        }
        return ResultVo.success(freshNewsDetailRespList);

//        Set<String> freshNewsIds = redisTemplate.opsForSet().members("tags:" + tag);
//        if (freshNewsIds == null || freshNewsIds.isEmpty()) {
//            return ResultVo.fail(NO_FRESH_NEWS_UNDER_TAG_MESSAGE);
//        }
//        List<Long> freshNewsIdList = freshNewsIds.stream()
//                .map(Long::parseLong)
//                .collect(Collectors.toList());
//        Page<FreshNews> pageParam = new Page<>(page, pageSize);
//        Page<FreshNews> pageResult = freshNewsMapper.selectPage(pageParam,
//                new QueryWrapper<FreshNews>()
//                        .eq("is_deleted", 0)
//                        .eq("is_active", 1)
//                        .in("fresh_news_id", freshNewsIdList)
//                        .orderByDesc("create_time")
//        );
//        List<FreshNews> records = pageResult.getRecords();
//        List<FreshNewsResp> freshNewsRespList = records.stream()
//                .map(freshNews -> {
//                    FreshNewsResp resp = new FreshNewsResp();
//                    BeanUtils.copyProperties(freshNews, resp);
//
//                    //合并redis中的点赞数
//                    Integer redisLiked = (Integer) redisTemplate.opsForValue().get(RedisKeyConstant.LIKE_NEWS_NUM + ":" + resp.getFreshNewsId());
//                    Long dbLike = resp.getLiked();
//                    resp.setLiked(redisLiked == null ? dbLike : redisLiked + dbLike);
//
//                    return resp;
//                })
//                .collect(Collectors.toList());
//        return ResultVo.success(freshNewsRespList);
    }

    @Override
    public ResultVo<FreshNewsResp> deleteFreshNews(Long freshNewsId) {
        FreshNews freshNews = freshNewsMapper.selectById(freshNewsId);
        if (freshNews == null) {
            return ResultVo.fail(FRESH_NEWS_NOT_FOUND_MESSAGE);
        }

        //判断用户是否是新鲜事的作者
        if(!UserContext.getUserId().equals(freshNews.getUserId())){
            //不是作者，返回错误
            return ResultVo.fail(NOT_FRESH_NEWS_AUTHOR);
        }

        FreshNewsResp resp = new FreshNewsResp();
        BeanUtils.copyProperties(freshNews, resp);

        freshNewsMapper.updateById(freshNews.setIsDeleted(1));
        redisUtil.deleteValue(RedisKeyConstant.FRESH_NEWS_CACHE + freshNewsId);//删除缓存

        return ResultVo.success(resp);
    }
}
