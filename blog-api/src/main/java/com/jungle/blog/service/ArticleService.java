package com.jungle.blog.service;

import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.params.PageParams;

public interface ArticleService {

    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);
}
