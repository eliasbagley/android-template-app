package com.rocketmade.templateapp.utils;
import android.support.annotation.VisibleForTesting;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.AbsListView;

import java.util.HashMap;
import java.util.Map;

import lombok.Value;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created by eliasbagley on 11/24/15.
 */
public class Paginator implements AbsListView.OnScrollListener {
    private static int STARTING_PAGE = 1;

    @Value
    public static class PageableRequest {
        Call     call;
        Callback callback;
    }

    public interface Pageable {
        PageableRequest pageableRequestForPage(int page);

        void isLoading(boolean loadingMore);
    }


    // Page state
    private int                   _currentPage = STARTING_PAGE;
    private Map<Integer, Boolean> pages        = new HashMap<>(); // Keeps track of which pages have been loaded

    // Paginator items
    private int pageSize;
    private boolean loading = false;
    private SwipeRefreshLayout refreshControl;
    private Pageable           pageable;
    private boolean            userScrolled;
    private PublishSubject<Boolean> loadingSubject = PublishSubject.create();


    // Request
    private PageableRequest request;


    public Paginator(int pageSize) {
        this(pageSize, null, null);
    }

    public Paginator(int pageSize, Pageable pageable) {
        this(pageSize, null, pageable);
    }

    public Paginator(int pageSize, SwipeRefreshLayout refreshControl, Pageable pageable) {
        this.pageSize = pageSize;
        this.pageable = pageable;
        this.refreshControl = refreshControl;
        subscribeToLoadingEvents();

        // Trigger the first call when created
        loadPage(_currentPage);
    }

    // Clear up any references we are holding onto
    public void cleanup() {
        Timber.d("Clearing paginator");

        Observable.just(request.getCall())
                .observeOn(Schedulers.io())
                .subscribe(call -> {
                    Timber.d("Call %s cancelled", call);
                    call.cancel();
                });

        setLoading(false);
        request = null;
        refreshControl = null;
        pageable = null;
        loadingSubject = null;
    }


    boolean refreshing = false;

    public void refresh() {
        refreshing = true;
        _currentPage = STARTING_PAGE;
        pages = new HashMap<>();
        loadPage(_currentPage);
    }

    public int currentPage() {
        return _currentPage;
    }

    public synchronized void setLoading(boolean loading) {
        this.loading = loading;

        if (loadingSubject != null) {
            loadingSubject.onNext(loading);
        }

        if (pageable != null) {
            pageable.isLoading(loading);
        }

        if (refreshControl != null) {
            // Only show the refresh control from a blank refresh
            if (refreshing) {
                refreshControl.setRefreshing(loading);
                refreshing = loading;
            }
        }
    }

    private void subscribeToLoadingEvents() {
        loadingSubject
                .take(2) // take a true, then a false so loading indicator is only shown once
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loading -> {
                    if (refreshControl != null) {
                        refreshControl.setRefreshing(loading);
                    }
                });
    }

    //region scroll listener

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ||
                scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            userScrolled = true;
        } else {
            userScrolled = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (shouldLoadNextPage(firstVisibleItem)) {
            loadPage(_currentPage);
        }
    }

    //endregion

    //region private methods

    private boolean shouldLoadNextPage(int firstVisibleItem) {
        return (firstVisibleItem >= (numberOfItemsLoaded() / 2) && loading == false);
    }

    private int numberOfItemsLoaded() {
        return pageSize * (_currentPage - 1);
    }

    private void loadPage(int page) {
        if (loading) {
            return;
        }

        request = pageable.pageableRequestForPage(page);
        enqueue(request.getCall(), request.getCallback());
    }

    @VisibleForTesting
    public boolean alreadyLoadedPage(int page) {
        Boolean alreadyLoaded = pages.get(page);
        if (alreadyLoaded == null || alreadyLoaded == false) {
            return false;
        }
        return true;
    }

    public void enqueue(Call call, Callback callback) {
        setLoading(true);

        int page = currentPage();


        if (alreadyLoadedPage(page)) {
            return;
        }

        // make call
        Timber.i("Loading page: " + page);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    _currentPage++;
                }

                setLoading(false);
                pages.put(page, response.isSuccess());

                if (callback != null) {
                    callback.onResponse(response, retrofit);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                setLoading(false);
                pages.put(page, false);

                if (callback != null) {
                    callback.onFailure(t);
                }

            }
        });
    }
}

