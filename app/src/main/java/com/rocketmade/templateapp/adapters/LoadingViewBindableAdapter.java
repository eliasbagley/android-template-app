package com.rocketmade.templateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import hugo.weaving.DebugLog;

/**
 * Created by eliasbagley on 12/3/15.
 */
public abstract class LoadingViewBindableAdapter<T> extends BaseAdapter {
    private final   Context        context;
    private final LayoutInflater inflater;
    private boolean loading = false;
    private View loadingView;

    public LoadingViewBindableAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.loadingView = newLoadingView(inflater);
    }

    public Context getContext() {
        return context;
    }

    protected int loadingViewCount() {
        return loading ? 1 : 0;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }

    private boolean isLastCell(int position) {
        return (position == getCount() - 1);
    }

    private boolean isLoadingCell(int position) {
        return isLastCell(position) && loading;
    }

    private boolean isLoadingView(View view) {
        return view == loadingView;
    }

    @Override
    public int getCount() {
        return getViewCount() + loadingViewCount();
    }

    @Override
    public abstract T getItem(int position);

    @Override public final View getView(int position, View view, ViewGroup container) {
        if (isLoadingCell(position)) {
            return loadingView;
        }

        // Create a new view if it's null or if it's trying to recycle loading view
        if (view == null || isLoadingView(view)) {
            view = newView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newView result must not be null.");
            }
        }

        bindView(getItem(position), position, view);
        return view;
    }

    /** Create a new instance of a view for the specified position. */
    public abstract View newView(LayoutInflater inflater, int position, ViewGroup container);

    /** Bind the data for the specified {@code position} to the view. */
    public abstract void bindView(T item, int position, View view);

    public abstract View newLoadingView(LayoutInflater inflater);

    // different than getCount() because it takes into account the loadingView count
    public abstract int getViewCount();

    @Override public final View getDropDownView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = newDropDownView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newDropDownView result must not be null.");
            }
        }
        bindDropDownView(getItem(position), position, view);
        return view;
    }

    /** Create a new instance of a drop-down view for the specified position. */
    public View newDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
        return newView(inflater, position, container);
    }

    /** Bind the data for the specified {@code position} to the drop-down view. */
    public void bindDropDownView(T item, int position, View view) {
        bindView(item, position, view);
    }
}

