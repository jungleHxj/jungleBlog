package com.jungle.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jungle.blog.dao.mapper.TagMapper;
import com.jungle.blog.dao.pojo.Tag;
import com.jungle.blog.service.TagService;
import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    @Override
    public Result hots(int limit) {
        /**
         * 1、标签所拥有的文章数量最多，就是最热标签
         * 2、查询， 根据tag_id分组计数，从大到小排列取前limit个
         */
        List<Long> tagIds = tagMapper.findHotsTagIds(limit);
        if(CollectionUtils.isEmpty(tagIds)){
            return Result.success(Collections.emptyList());
        }

        // 需求的是 tagId 和 tagName    Tag对象
        List<Tag> tagList = tagMapper.findTagsByTagIds(tagIds);

        return Result.success(tagList);
    }

    @Override
    public Result findAll() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId, Tag::getTagName);
        List<Tag> tags = this.tagMapper.selectList(queryWrapper);
        return Result.success(convertList(tags));
    }

    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        List<Tag> tags = this.tagMapper.selectList(queryWrapper);
        return Result.success(convertList(tags));
    }

    @Override
    public Result findDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);

        return Result.success(copy(tag));
    }
}
