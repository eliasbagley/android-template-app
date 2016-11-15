package com.rocketmade.templateapp.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.rocketmade.templateapp.MyApp;
import com.rocketmade.templateapp.R;
import com.rocketmade.templateapp.models.Article;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by eliasbagley on 11/24/15.
 */
public class ArticleListViewItem extends BaseView {
    @Inject Picasso _picasso;

    @Bind(R.id.name_label)       TextView  _nameLabel;
    @Bind(R.id.date_label)       TextView  _dateLabel;
    @Bind(R.id.background_image) ImageView _backgroundImage;
    @Bind(R.id.outlet_image)     ImageView _outletImage;


    public ArticleListViewItem(Context context) {
        super(context);
    }

    @Override
    protected void inflate(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_user_list_item, this);
        ButterKnife.bind(this);
        MyApp.injectView(this);
    }

    public void bind(Article article) {
        _nameLabel.setText(article.title);
        _dateLabel.setText(article.publicationDate);

        _picasso.load(article.image).into(_backgroundImage);
        _picasso.load(article.outlet.image).into(_outletImage);
    }

}
