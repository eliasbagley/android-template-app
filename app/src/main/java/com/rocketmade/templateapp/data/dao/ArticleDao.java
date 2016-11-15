package com.rocketmade.templateapp.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hannesdorfmann.sqlbrite.dao.Dao;
import com.rocketmade.templateapp.models.Article;
import com.rocketmade.templateapp.models.Outlet;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import hugo.weaving.DebugLog;
import retrofit.http.DELETE;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by eliasbagley on 12/1/15.
 */

public class ArticleDao extends Dao {
    @Override
    public void createTable(SQLiteDatabase database) {
        CREATE_TABLE(
                Article.TABLE_NAME,
                Article.ID + " INTEGER PRIMARY KEY NOT NULL",
                Article.TITLE + " TEXT NOT NULL",
                Article.IMAGE + " TEXT NOT NULL",
                Article.OUTLET_ID + " INTEGER NOT NULL",
                Article.PUBLICATION_DATE + " TEXT NOT NULL"
        ).execute(database);
    }

    @DebugLog
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //region dao methods

    // Get articles for article feed ordered by date and title
    public Observable<List<Article>> getArticles() {
        return query
                (
                        SELECT(Article.ID + " AS _id, *") // Cursor adapter expects id to be named _id, so we need to alias it in all select calls
                                .FROM(Article.TABLE_NAME)
                                .INNER_JOIN(Outlet.TABLE_NAME)
                                .ON(String.format("%s.%s = %s.%s", Outlet.TABLE_NAME, Outlet.ID, Article.TABLE_NAME, Article.OUTLET_ID))
                                .ORDER_BY(String.format("%s desc, %s asc", Article.PUBLICATION_DATE, Article.TITLE))
                )
                .run()
                .mapToList(cursor -> Article.fromCursor(cursor));

    }


    // Example for filtering across a relationship
    public Observable<Cursor> getUSAAArticles() {
        return query
                (
                        SELECT(Article.ID + " AS _id, *") // Cursor adapter expects id to be named _id, so we need to alias it in all select calls
                                .FROM(Article.TABLE_NAME)
                                .INNER_JOIN(Outlet.TABLE_NAME)
                                .ON(String.format("%s.%s = %s.%s", Outlet.TABLE_NAME, Outlet.ID, Article.TABLE_NAME, Article.OUTLET_ID))
                                .WHERE(String.format("%s.%s = '%s'", Outlet.TABLE_NAME, Outlet.NAME, "USSA"))
                                .ORDER_BY(String.format("%s desc, %s asc", Article.PUBLICATION_DATE, Article.TITLE))
                )
                .run()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(query -> {
                    return query.run();
                });

    }

    public Observable<Article> getArticle(int id) {
        return query
                (
                        SELECT("*")
                                .FROM(Article.TABLE_NAME)
                                .INNER_JOIN(Outlet.TABLE_NAME)
                                .ON(String.format("%s.%s = %s.%s", Outlet.TABLE_NAME, Outlet.ID, Article.TABLE_NAME, Article.OUTLET_ID))
                                .WHERE(String.format("%s = %d", Article.ID, id))
                ).run()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(query -> {
                    Cursor cursor = query.run();
                    cursor.moveToFirst();
                    Article article = Article.fromCursor(cursor);
                    cursor.close();
                    return article;
                });
    }

    public void saveArticles(List<Article> articles) {
        BriteDatabase.Transaction transaction = newTransaction();
        try {
            for (Article article : articles) {
                insert(Article.TABLE_NAME, article.contentValues(), SQLiteDatabase.CONFLICT_REPLACE);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void deleteAll() {
        delete(Article.TABLE_NAME);
    }
}
//endregion
