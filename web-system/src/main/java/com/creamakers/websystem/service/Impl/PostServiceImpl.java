package com.creamakers.websystem.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creamakers.websystem.dao.PostMapper;
import com.creamakers.websystem.domain.dto.Post;
import com.creamakers.websystem.domain.vo.ResultVo;
import com.creamakers.websystem.domain.vo.response.PostResp;
import com.creamakers.websystem.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.creamakers.websystem.constants.CommonConst.DATA_DELETE_FAILED_NOT_FOUND;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;
    @Override
    public ResultVo<List<PostResp>> getAllPosts(Integer page, Integer pageSize) {
        Page<Post> pageParam = new Page<>(page,pageSize);
        Page<Post> Page = postMapper.selectPage(pageParam, new QueryWrapper<Post>().eq("is_deleted", 0));
        List<Post> records = Page.getRecords();
        List<PostResp> list = records.stream().map(this::convertToPostResp).toList();
        return ResultVo.success(list);
    }

    @Override
    public ResultVo<PostResp> getPostById(Long postId) {
        Post post = postMapper.selectById(postId);
        PostResp postResp = convertToPostResp(post);
        return ResultVo.success(postResp);
    }

    @Override
    public ResultVo<Void> deletePostById(Long postId) {
        int i = postMapper.deleteById(postId);
        if(i<1) return ResultVo.fail(DATA_DELETE_FAILED_NOT_FOUND);
        return ResultVo.success();
    }

    private PostResp convertToPostResp(Post post) {
        PostResp postResp = new PostResp();
        BeanUtil.copyProperties(post,postResp);
        return postResp;
    }
}
