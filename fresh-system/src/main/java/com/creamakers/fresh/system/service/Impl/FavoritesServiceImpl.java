package com.creamakers.fresh.system.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.fresh.system.dao.FreshNewsFavoritesMapper;
import com.creamakers.fresh.system.dao.FreshNewsMapper;
import com.creamakers.fresh.system.domain.dto.FreshNews;
import com.creamakers.fresh.system.domain.dto.FreshNewsFavorites;
import com.creamakers.fresh.system.domain.vo.ResultVo;
import com.creamakers.fresh.system.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {
    @Autowired
    private FreshNewsMapper freshNewsMapper;
    @Autowired
    private FreshNewsFavoritesMapper freshNewsFavoritesMapper;
    @Override
    public ResultVo<Void> addFavorite(Long userId, Long newsId) {
        // 检查是否已经收藏
        FreshNewsFavorites freshNewsFavorites1 = freshNewsFavoritesMapper.selectOne(
                new QueryWrapper<FreshNewsFavorites>()
                        .eq("user_id", userId)
                        .eq("news_id", newsId)
                        .eq("is_deleted", 0)
        );

        if (freshNewsFavorites1 !=null) {
            return ResultVo.fail("您已经收藏过此新鲜事");
        }

        // 插入新的收藏记录
        FreshNewsFavorites freshNewsFavorites = new FreshNewsFavorites();
        freshNewsFavorites.setUserId(Math.toIntExact(userId));
        freshNewsFavorites.setNewsId(newsId);
        int rows = freshNewsFavoritesMapper.insert(freshNewsFavorites);

        if (rows > 0) {
            // 更新对应的新鲜事收藏数
            FreshNews freshNews = freshNewsMapper.selectById(newsId);
            if (freshNews != null) {
                freshNews.setFavoritesCount(freshNews.getFavoritesCount() + 1);
                freshNewsMapper.updateById(freshNews);
            }
            return ResultVo.success();
        } else {
            return ResultVo.fail("收藏失败");
        }
    }

    @Override
    public ResultVo<List<FreshNewsFavorites>> listFavorites(Long userId, Integer page, Integer pageSize) {
        // 创建分页对象
        Page<FreshNewsFavorites> pageParam = new Page<>(page, pageSize);

        // 执行分页查询，过滤已删除的记录
        Page<FreshNewsFavorites> pageResult = freshNewsFavoritesMapper.selectPage(pageParam,
                new QueryWrapper<FreshNewsFavorites>()
                        .eq("user_id", userId)
                        .eq("is_deleted", 0));

        List<FreshNewsFavorites> records = pageResult.getRecords();

        return ResultVo.success(records);
    }

    @Override
    public ResultVo<Void> deleteFavorite(Long userId, Long newsId) {
        // 查找收藏记录
        FreshNewsFavorites freshNewsFavorites = freshNewsFavoritesMapper.selectOne(
                new QueryWrapper<FreshNewsFavorites>()
                        .eq("user_id", userId)
                        .eq("news_id", newsId)
                        .eq("is_deleted", 0)
        );

        if (freshNewsFavorites == null) {
            return ResultVo.fail("您尚未收藏此新鲜事");
        }

        // 标记收藏记录为已删除
        freshNewsFavorites.setIsDeleted(true);
        int rows = freshNewsFavoritesMapper.updateById(freshNewsFavorites);

        if (rows > 0) {
            // 更新对应的新鲜事收藏数
            FreshNews freshNews = freshNewsMapper.selectById(newsId);
            if (freshNews != null && freshNews.getFavoritesCount() > 0) {
                freshNews.setFavoritesCount(freshNews.getFavoritesCount() - 1);
                freshNewsMapper.updateById(freshNews);
            }
            return ResultVo.success();
        } else {
            return ResultVo.fail("取消收藏失败");
        }
    }
}
