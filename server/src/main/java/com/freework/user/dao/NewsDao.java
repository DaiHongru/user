package com.freework.user.dao;

import com.freework.common.loadon.entity.News;

import java.util.List;

/**
 * @author daihongru
 */
public interface NewsDao {
    /**
     * 添加一条新的纪录
     *
     * @param news
     * @return
     */
    int insert(News news);

    /**
     * 根据条件查询简历
     *
     * @param news
     * @return
     */
    List<News> queryByRequirement(News news);

    /**
     * 更新记录
     *
     * @param news
     * @return
     */
    int update(News news);
}
