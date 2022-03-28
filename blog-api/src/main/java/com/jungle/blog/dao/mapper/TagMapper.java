package com.jungle.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jungle.blog.dao.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Mapper
@Repository
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章id查询标签列表
     * @param articleId
     * @return
     */

    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 查询最热的标签  前limit条
     * @param limit
     * @return
     */
    List<Long> findHotsTagIds(int limit);

    /**
     * 根据 tagId 查询标签列表
     * @param tagIds
     * @return
     */
    List<Tag> findTagsByTagIds(List<Long> tagIds);
}
