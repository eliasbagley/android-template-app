package com.rocketmade.templateapp.common.interfaces;

import android.content.Context;
import android.view.View;

/**
 * Created by eliasbagley on 11/24/15.
 */

//Must be overridden by subclass in order to be displayed in a listview with {@link com.rocketmade.templateapp.adapters.GenericListAdapter}
// This is currently returning a placeholder
public interface Listable {
    View newView(Context context);
    void bindView(View view, Context context);
}
