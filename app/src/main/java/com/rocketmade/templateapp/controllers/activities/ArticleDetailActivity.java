package com.rocketmade.templateapp.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.rocketmade.templateapp.R;
import com.rocketmade.templateapp.data.dao.ArticleDao;
import com.rocketmade.templateapp.models.Article;

import javax.inject.Inject;

import butterknife.Bind;
import rx.Subscription;
import timber.log.Timber;

public class ArticleDetailActivity extends BaseActivity {
    private static final String EXTRA_ARTICLE_ID = "EXTRA_ARTICLE_ID";

    @Inject ArticleDao _articleDao;

    @Bind(R.id.article_title) TextView _articleTitle;

    private Article _article;
    private Subscription _articleSubscription;

    public static void show(Activity activity, int articleId) {
        Intent intent = new Intent(activity, ArticleDetailActivity.class);
        intent.putExtra(EXTRA_ARTICLE_ID, articleId);
        activity.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void inflate() {
        setContentView(R.layout.activity_article_detail);
    }

    @Override
    protected void initialize(Context context) {
        int articleId = getIntent().getIntExtra(EXTRA_ARTICLE_ID, -1);
        if (articleId == -1) {
            new AssertionError("You must pass an Article ID through the intent");
        }

        compositeSubscription.add(
                _articleDao.getArticle(articleId).subscribe(article -> {
                    _article = article;
                    setup();
                })
        );
    }

    private void setup() {
        Timber.d(_article.toString());
        _articleTitle.setText(_article.title);
    }
}
