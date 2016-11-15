package com.rocketmade.templateapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.rocketmade.templateapp.models.Article;
import com.rocketmade.templateapp.views.ArticleListViewItem;

/**
 * Created by eliasbagley on 11/24/15.
 */
public class ArticleAdapter extends CursorAdapter {
    public ArticleAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public ArticleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public Article getArticle(int position) {
        return Article.fromCursor((Cursor)getItem(position));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return new ArticleListViewItem(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((ArticleListViewItem) view).bind(Article.fromCursor(cursor));
    }
}



