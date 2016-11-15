package com.rocketmade.templateapp.common.interfaces;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

/**
 * Created by eliasbagley on 11/24/15.
 */

// Have models implement this, and combine with GenericCursorAdapter
public interface CursorAdapterListable {
    View newView(Context context);
    void bindView(View view, Context context, Cursor cursor);
}
