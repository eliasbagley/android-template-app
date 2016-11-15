package com.rocketmade.templateapp.controllers.fragments;

import android.content.Context;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rocketmade.templateapp.R;
import com.rocketmade.templateapp.controllers.activities.ArticleDetailActivity;
import com.rocketmade.templateapp.adapters.ListableAdapter;
import com.rocketmade.templateapp.data.dao.ArticleDao;
import com.rocketmade.templateapp.models.Article;
import com.rocketmade.templateapp.network.api.ArticleAPI;
import com.rocketmade.templateapp.network.managers.ArticleManager;
import com.rocketmade.templateapp.network.serviceresponse.ArticlesServiceResponse;
import com.rocketmade.templateapp.utils.Paginator;


import javax.inject.Inject;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.rocketmade.templateapp.utils.Utils.snackbar;

public class ArticleFragment extends BaseFragment {
    private static final int ARTICLE_PAGE_SIZE = 10;

    @Inject ArticleManager _articleManager;
    @Inject ArticleAPI     _articleAPI;
    @Inject ArticleDao     _articleDao;

    @Bind(R.id.article_list)    ListView           _articleList;
    @Bind(R.id.refresh_control) SwipeRefreshLayout _refreshControl;

    private ListableAdapter _adapter;
    private Paginator _paginator;

    public static ArticleFragment newInstance() {
        ArticleFragment fragment = new ArticleFragment();
        Bundle       args     = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    protected void initialize(Context context) {
        createAdapterAndBindList();
        subscribeForArticles();
        setupListView();
        setupRefreshControl();
        setupPaginator();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cleanup();
    }

    private void cleanup() {
        if (_paginator != null) {
            _paginator.cleanup();
        }
    }


    private void setupPaginator() {
        Callback callback = new Callback<ArticlesServiceResponse>() {
            @Override
            public void onResponse(Response<ArticlesServiceResponse> response, Retrofit retrofit) {
                if (!response.isSuccess()) {
                    snackbar(getActivity(), "Failed getting articles. Please try again.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                snackbar(getActivity(), "Failed: " + t.getMessage());
            }
        };

        Paginator.Pageable pageable= new Paginator.Pageable() {
            @Override
            public Paginator.PageableRequest pageableRequestForPage(int page) {
                return _articleManager.getArticles(page, callback);
            }

            @Override
            public void isLoading(boolean loading) {
                _adapter.setLoading(loading);

            }
        };

        _paginator = new Paginator(ARTICLE_PAGE_SIZE, _refreshControl, pageable);
        _articleList.setOnScrollListener(_paginator);
    }

    private void setupListView() {
        _articleList.setOnItemClickListener((parent, view, position, id) -> {
            Article article = (Article) _adapter.getItem(position);
            ArticleDetailActivity.show(getActivity(), article.id);
        });
    }

    private void setupRefreshControl() {
        _refreshControl.setOnRefreshListener(() -> _paginator.refresh());
    }

    private void createAdapterAndBindList() {
        _adapter = new ListableAdapter(getActivity());
        _articleList.setAdapter(_adapter);
    }

    private void subscribeForArticles() {
        compositeSubscription.add(_articleDao.getArticles().subscribe(list -> {
            _adapter.setList(list);
        }));
    }
}
