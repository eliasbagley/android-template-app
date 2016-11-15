package com.rocketmade.templateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rocketmade.templateapp.common.interfaces.Listable;
import com.rocketmade.templateapp.views.ListLoadingView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eliasbagley on 11/28/15.
 */
public final class ListableAdapter<T extends Listable> extends LoadingViewBindableAdapter {

    private List<T> list = new ArrayList<>();

    public ListableAdapter(Context context) {
        super(context);
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void appendList(List<T> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        Listable model = getItem(position);
        return model.newView(getContext());
    }

    @Override
    public void bindView(Object item, int position, View view) {
        Listable model = (Listable) item;
        model.bindView(view, getContext());
    }

    @Override
    public View newLoadingView(LayoutInflater inflater) {
        return new ListLoadingView(getContext());
    }

    @Override
    public int getViewCount() {
        return list.size();
    }
}
