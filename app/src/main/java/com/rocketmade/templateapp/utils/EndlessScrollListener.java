package com.rocketmade.templateapp.utils;

/**
 * Created by eliasbagley on 2/10/15.
 */

import android.widget.AbsListView;

/**
 * Created by eliasbagley on 8/18/14.
 */
public abstract class EndlessScrollListener implements AbsListView.OnScrollListener, InfiniteScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;

    // Item count known to the scroller, used to track loading status
    private int knownItemCount;

    // Has the user scrolled
    private boolean userScrolled = false;

    // Whether or not it is loading
    private boolean loading = false;

    // Initialize the list to a specific stage
    public EndlessScrollListener(int visibleThreshold, int knownItemCount) {
        this(visibleThreshold, knownItemCount, false);
    }

    public EndlessScrollListener(int knownItemCount) {
        this.knownItemCount = knownItemCount;
    }

    // Initialize the list to a specific stage,
    // loading is tricky. If when this listener is created, loading has finished, then it should
    // be set to false. If the list is waiting to be populated, it should be true.
    // Otherwise known item count can not be initialized properly.
    public EndlessScrollListener(int visibleThreshold, int knownItemCount, boolean loading) {
        this.visibleThreshold = visibleThreshold;
        this.knownItemCount = knownItemCount;
        this.loading = loading;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount)
    {
        if (!userScrolled) {
            return;
        }

        // If it is loading and the total item count is more than the known item count, it means loading
        // has just ended and we need to reset the known item count.
        if (loading && (totalItemCount > knownItemCount)) {
            loading = false;
            knownItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) {
            loading = true;
            onLoadMore(totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ||
                scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            userScrolled = true;
        } else {
            userScrolled = false;
        }
    }
}


