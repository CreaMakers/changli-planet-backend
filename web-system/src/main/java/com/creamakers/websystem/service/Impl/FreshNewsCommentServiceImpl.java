package com.creamakers.websystem.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.creamakers.websystem.constants.CommonConst;
import com.creamakers.websystem.dao.FreshNewsChildCommentMapper;
import com.creamakers.websystem.dao.FreshNewsFatherCommentMapper;
import com.creamakers.websystem.domain.dto.FreshNewsChildComment;
import com.creamakers.websystem.domain.dto.FreshNewsFatherComment;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.service.FreshNewsCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FreshNewsCommentServiceImpl implements FreshNewsCommentService {
    @Autowired
    private FreshNewsFatherCommentMapper freshNewsFatherCommentMapper;

    @Autowired
    private FreshNewsChildCommentMapper freshNewsChildCommentMapper;

    @Override
    public boolean deleteCommentByFreshNewsId(Long freshNewsId) {
        // 删除子评论
        UpdateWrapper<FreshNewsChildComment> childCommentQueryWrapper = new UpdateWrapper<>();
        childCommentQueryWrapper
                .eq("fresh_news_id",freshNewsId)
                .eq("is_deleted",0)
                .set("is_deleted",1);
        freshNewsChildCommentMapper.update(childCommentQueryWrapper);
        // 删除父评论
        UpdateWrapper<FreshNewsFatherComment> fatherCommentQueryWrapper = new UpdateWrapper<>();
        fatherCommentQueryWrapper
                .eq("fresh_news_id",freshNewsId)
                .eq("is_deleted",0)
                .set("is_deleted",1);
        freshNewsFatherCommentMapper.update(fatherCommentQueryWrapper);
        return true;
    }

    @Override
    public ResultVo<Void> deleteComment(Long commentId, Integer isParent) {
        if (isParent == 0) {
            // 删除子评论
            UpdateWrapper<FreshNewsChildComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper
                    .eq("id", commentId)
                    .set("is_deleted", 1);
            int result = freshNewsChildCommentMapper.update(updateWrapper);

            if (result <= 0) {
                return ResultVo.fail(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }

            // 子评论删除成功,更新父评论子评论数量
            FreshNewsChildComment childComment = freshNewsChildCommentMapper.selectById(commentId);
            FreshNewsFatherComment fatherComment = freshNewsFatherCommentMapper.selectById(childComment.getFatherCommentId());
            // 检查父评论是否存在
            if (fatherComment == null) {
                return ResultVo.fail(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }
            // 更新父评论子评论数量
            int updated = freshNewsFatherCommentMapper.updateById(fatherComment.setChildCount(fatherComment.getChildCount() - 1));
            if (updated <= 0) {
                // 更新父评论子评论数量失败,回滚子评论删除
                throw new RuntimeException(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
            }
            // 子评论删除成功，返回成功结果
            return ResultVo.success();
        } else {
            // 删除父评论
            UpdateWrapper<FreshNewsFatherComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper
                    .eq("id", commentId)
                    .set("is_deleted", 1);
            int result = freshNewsFatherCommentMapper.update(updateWrapper);

            if (result <= 0) {
                // 父评论删除失败
                return ResultVo.fail(CommonConst.COMMENT_DELETE_FAILED_MESSAGE);
            }

            try {
                //获取子评论个数
                Integer childCount = freshNewsFatherCommentMapper.selectById(commentId).getChildCount();

                // 删除关联子评论
                UpdateWrapper<FreshNewsChildComment> childUpdateWrapper = new UpdateWrapper<>();
                childUpdateWrapper
                        .eq("father_comment_id", commentId)
                        .eq("is_deleted", 0)
                        .set("is_deleted", 1);
                int childUpdate = freshNewsChildCommentMapper.update(childUpdateWrapper);

                if (childUpdate == childCount) {
                    // 关联子评论删除成功
                    return ResultVo.success();
                } else {
                    // 关联子评论删除失败，回滚父评论删除
                    throw new RuntimeException(CommonConst.CHILD_COMMENT_DELETE_FAILED_MESSAGE);
                }
            } catch (Exception e) {
                // 关联子评论删除失败，回滚父评论删除
                throw new RuntimeException(e);
            }
        }
    }
}
