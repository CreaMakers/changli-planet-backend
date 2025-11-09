package com.creamakers.websystem.controller;

import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.service.FreshNewsCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/web/fresh_news/comments")
public class FreshNewsCommentController {
    @Autowired
    private FreshNewsCommentService freshNewsCommentService;
    /**
     * 删除特定评论
     * @param commentId     评论id
     * @param isParent      是否是父评论
     * @return              结果
     */
    @DeleteMapping(value = "/{comment_id}/{is_parent}")
    public ResultVo<Void> deleteComment(@PathVariable("comment_id") Long commentId,
                                        @PathVariable("is_parent") Integer isParent){
        return freshNewsCommentService.deleteComment(commentId,isParent);
    }
}
