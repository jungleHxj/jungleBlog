package com.jungle.blog.service;

import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.TagVo;

import java.util.List;

public interface TagService {

    List<TagVo> findTagsByArticleId(Long articleId);

    Result hots(int limit);
}
