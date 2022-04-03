package com.jungle.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jungle.blog.dao.dos.Archives;
import com.jungle.blog.dao.mapper.ArticleBodyMapper;
import com.jungle.blog.dao.mapper.ArticleMapper;
import com.jungle.blog.dao.mapper.ArticleTagMapper;
import com.jungle.blog.dao.pojo.Article;
import com.jungle.blog.dao.pojo.ArticleBody;
import com.jungle.blog.dao.pojo.ArticleTag;
import com.jungle.blog.dao.pojo.SysUser;
import com.jungle.blog.service.*;
import com.jungle.blog.util.UserThreadLocal;
import com.jungle.blog.vo.ArticleBodyVo;
import com.jungle.blog.vo.ArticleVo;
import com.jungle.blog.vo.Result;
import com.jungle.blog.vo.TagVo;
import com.jungle.blog.vo.params.ArticleParam;
import com.jungle.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ArticleTagMapper articleTagMapper;

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

        // 根据分类筛选
        if(pageParams.getCategoryId() != null){
            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
        }

        // 根据标签筛选
        List<Long> articleIdList = new ArrayList<>();
        if(pageParams.getTagId() != null){
            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }
            if(articleIdList.size() > 0){
                queryWrapper.in(Article::getId,articleIdList);
            }
        }

        // 是否置顶 进行排序
        // order by create_date desc
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);

        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = articlePage.getRecords();

        // 能直接返回吗？  很明显不能
        List<ArticleVo> articleVoList = convertList(records, true, true);
        return Result.success(articleVoList);
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        // limit后有空格
        queryWrapper.last("limit " + limit);
        // select id, title from ms_article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(convertList(articles, false, false));
    }

    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        // limit后有空格
        queryWrapper.last("limit " + limit);
        // select id, title from ms_article order by create_date desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(convertList(articles, false, false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList= articleMapper.listArchives();

        return Result.success(archivesList);
    }

    @Autowired
    private ThreadService threadService;

    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1、根据id查询 文章信息
         * 2、根据bodyId和categoryId  做关联查询
         */
        Article article = this.articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article,true,true,true,true);

        // 查看完文章了，新增阅读数，有没有问题呢？
        // 查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加 写锁，阻塞其他读操作，性能就会比较低
        // 更新  增加了此次接口的 耗时
        // 如果一旦更新出问题，不能影响查看文章的操作
        // 使用线程池   可以把更新操作，扔到线程池中执行，与主线程不相关了
        threadService.updateArticleViewCount(articleMapper,article);

        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParam articleParam) {
        // 此接口要加入到登录拦截器中
        SysUser sysUser = UserThreadLocal.get();

        /**
         * 1、发布文章  目的  构建Article对象
         * 2、作者id   当前登录用户
         * 3、标签  要将标签加入到  关联列表中
         * 4、内容存储   article   bodyId
         */

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setCategoryId(articleParam.getCategory().getId());
        article.setCreateDate(System.currentTimeMillis());
        article.setCommentCounts(0);
        article.setViewCounts(0);
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setWeight(Article.Article_Common);
        article.setBodyId(-1L);

        // 插入后会生成一个文章id
        this.articleMapper.insert(article);

        // tags
        List<TagVo> tags = articleParam.getTags();
        if(tags != null){
            for(TagVo tag : tags){
                Long articleId = article.getId();
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(tag.getId());
                articleTagMapper.insert(articleTag);
            }
        }

        // body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);

        // 更新bodyId
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        Map<String,String> map = new HashMap<>();
        map.put("id",article.getId().toString());

        return Result.success(map);
    }

    private List<ArticleVo> convertList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor,false,false));
        }

        return articleVoList;
    }

    private List<ArticleVo> convertList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor,isBody,isCategory));
        }

        return articleVoList;
    }

    @Autowired
    private CategoryService categoryService;

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
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
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findaArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }


        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    private ArticleBodyVo findaArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());

        return articleBodyVo;
    }

}
