package com.jungle.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jungle.blog.dao.mapper.ArticleMapper;
import com.jungle.blog.dao.pojo.Article;
import com.jungle.blog.service.ArticleService;
import com.jungle.blog.service.SysUserService;
import com.jungle.blog.service.TagService;
import com.jungle.blog.vo.ArticleVo;
import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    // 此处在对应mapper类上添加，@Repository 可消除错误提示
    @Autowired
    private ArticleMapper articleMapper;

    // 两种方法
    //private final ArticleMapper articleMapper;
    //public ArticleServiceImpl(ArticleMapper articleMapper){
    //    this.articleMapper = articleMapper;
    //}

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;


    @Override
    public Result listArticle(PageParams pageParams) {
        /**
         * 1、分页查询 article 数据库表
         */
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        // 是否置顶 进行排序
        // order by create_date desc
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);

        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = articlePage.getRecords();

        // 能直接返回吗？  很明显不能
        List<ArticleVo> articleVoList = convertList(records, true, true);
        return Result.success(articleVoList);
    }

    private List<ArticleVo> convertList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor));
        }

        return articleVoList;
    }

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        // 并不是所有的接口，都需要标签，作者信息
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }

        return articleVo;
    }
}
