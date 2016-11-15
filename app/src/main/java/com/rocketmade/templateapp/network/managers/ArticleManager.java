package com.rocketmade.templateapp.network.managers;

import com.rocketmade.templateapp.data.dao.ArticleDao;
import com.rocketmade.templateapp.data.dao.OutletDao;
import com.rocketmade.templateapp.models.Article;
import com.rocketmade.templateapp.models.Outlet;
import com.rocketmade.templateapp.network.api.ArticleAPI;
import com.rocketmade.templateapp.network.serviceresponse.ArticlesServiceResponse;
import com.rocketmade.templateapp.utils.Paginator;

import java.util.List;

import javax.inject.Inject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import timber.log.Timber;

/**
 * Created by eliasbagley on 11/24/15.
 */
public class ArticleManager extends BaseAPIManager {
    private ArticleAPI _articleAPI;

    private ArticleDao _articleDao;
    private OutletDao _outletDao;

    @Inject
    ArticleManager(ArticleAPI articleAPI, ArticleDao articleDao, OutletDao outletDao) {
        _articleAPI = articleAPI;
        _articleDao = articleDao;
        _outletDao = outletDao;
    }


    public Paginator.PageableRequest getArticles(int page, Callback<ArticlesServiceResponse> callback) {
        return new Paginator.PageableRequest(getArticlesCall(page), getArticlesPageableCallback(callback));
    }

    private Call getArticlesCall(int page) {
        return _articleAPI.articles(page);
    }

    private Callback<ArticlesServiceResponse> getArticlesPageableCallback(Callback<ArticlesServiceResponse> callback) {
        return new Callback<ArticlesServiceResponse>() {
            @Override
            public void onResponse(Response<ArticlesServiceResponse> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    List<Outlet> outlets = response.body().getOutlets();
                    List<Article> articles = response.body().getArticles();

                    // First save the outlets
                    _outletDao.saveOutlets(outlets);

                    // Then the articles
                    _articleDao.saveArticles(articles);
                }

                callback.onResponse(response, retrofit);
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.d(t.getMessage());
                callback.onFailure(t);
            }
        };
    }
}
