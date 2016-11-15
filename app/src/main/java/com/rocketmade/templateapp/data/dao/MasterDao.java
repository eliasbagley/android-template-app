package com.rocketmade.templateapp.data.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by eliasbagley on 12/10/15.
 * <p>
 * This class is just a large wrapper around all the other DAO objects to make it easy to nuke the DB
 */

@ToString
@Singleton
@Getter
public class MasterDao {

    ArticleDao  articles;
    OutletDao   outlets;

    @Inject
    public MasterDao(ArticleDao articles, OutletDao outlets) {
        this.articles = articles;
        this.outlets = outlets;
    }

    public void deleteAll() {
        articles.deleteAll();
        outlets.deleteAll();
    }
}

