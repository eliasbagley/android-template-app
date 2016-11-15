package com.rocketmade.templateapp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import com.rocketmade.templateapp.common.interfaces.Listable;
import com.rocketmade.templateapp.views.ArticleListViewItem;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import lombok.ToString;
import rx.Observable;

/**
 * Created by eliasbagley on 11/24/15.
 */

@ToString
@ObjectMappable
public final class Article extends BaseModel implements Listable {
    //region json keys and column names
    public static final String TABLE_NAME       = "Articles";
    public static final String ID               = "article_id";
    public static final String TITLE            = "article_title";
    public static final String IMAGE            = "article_hero_image";
    public static final String OUTLET_ID        = "article_outlet_id";
    public static final String OUTLET           = "article_outlet";
    public static final String PUBLICATION_DATE = "article_publication_date";
    //endregion

    @SerializedName("id")
    @Column(ID)
    public int id;

    @SerializedName("title")
    @Column(TITLE)
    public String title;

    @SerializedName("hero_image")
    @Column(IMAGE)
    public String image;

    @SerializedName("outlet_id")
    @Column(OUTLET_ID)
    public int outletId;

    public Outlet outlet;

    @SerializedName("publication_date")
    @Column(PUBLICATION_DATE)
    public String publicationDate;

    public LocalDate getPublicationDate() {
        return DateTimeFormatter.ISO_LOCAL_DATE.parse(publicationDate, LocalDate.FROM);
    }

    public Article() {
    }

    // region Listable interface

    @Override
    public View newView(Context context) {
        return new ArticleListViewItem(context);
    }

    @Override
    public void bindView(View view, Context context) {
        ArticleListViewItem itemView = (ArticleListViewItem) view;
        itemView.bind(this);
    }


    //endregion

    // region builder

    public static ArticleBuilder builder() {
        return new ArticleBuilder();
    }

    public static class ArticleBuilder {
        private Article article = new Article();

        public ArticleBuilder() {
        }

        public ArticleBuilder id(int id) {
            article.id = id;
            return this;
        }

        public ArticleBuilder title(String title) {
            article.title = title;
            return this;
        }

        public ArticleBuilder image(String image) {
            article.image = image;
            return this;
        }

        public ArticleBuilder outlet(Outlet outlet) {
            article.outlet = outlet;
            article.outletId = outlet.id;
            return this;
        }

        public ArticleBuilder publicationDate(String date) {
            article.publicationDate = date;
            return this;
        }

        public Article build() {
            return article;
        }
    }

    //endregion

    //region dao boilerplate

    public ContentValues contentValues() {
        return ArticleMapper.contentValues()
                .id(id)
                .title(title)
                .image(image)
                .outletId(outletId)
                .publicationDate(publicationDate)
                .build();

    }

    public static Article fromCursor(Cursor cursor) {
        Article article = ArticleMapper.MAPPER.call(cursor);
        article.outlet = Outlet.fromCursor(cursor);

        return article;
    }

    //endregion
}

