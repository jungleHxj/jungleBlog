package com.jungle.blog.service.impl;

import com.jungle.blog.dao.mapper.TagMapper;
import com.jungle.blog.dao.pojo.Tag;
import com.jungle.blog.service.TagService;
import com.jungle.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    // 两种方法
    //private final TagMapper tagMapper;
    //public TagServiceImpl(TagMapper tagMapper){
    //    this.tagMapper = tagMapper;
    //}

    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        //mybatisplus 无法进行多表查询？
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);
        return convertList(tags);
    }

    private List<TagVo> convertList(List<Tag> records) {
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag record : records){
            tagVoList.add(copy(record));
        }
        return  tagVoList;
    }

    private TagVo copy(Tag tag) {
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag, tagVo);
        return tagVo;
    }
}
