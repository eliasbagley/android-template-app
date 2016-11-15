package com.rocketmade.templateapp.views;

import android.content.Context;
import android.view.LayoutInflater;

import com.rocketmade.templateapp.R;


/**
 * Created by eliasbagley on 12/3/15.
 */
public class ListLoadingView extends BaseView {
    public ListLoadingView(Context context) {
        super(context);
    }

    @Override
    protected void inflate(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_loading, this);
    }
}
