package com.jungle.blog.service;

import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.params.CommentParam;

public interface CommentsService {

    /**
     * 根据文章id，查询所有的评论列表
     * @param id
     * @return
     */
    Result commentsByArticleId(Long id);

    /**
     * 添加评论
     * @param commentParam
     * @return
     */
    Result comment(CommentParam commentParam);
}
