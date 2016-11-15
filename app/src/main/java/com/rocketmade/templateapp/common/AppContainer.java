package com.rocketmade.templateapp.common;

/**
 * Created by eliasbagley on 2/2/15.
 */

import android.app.Activity;
import android.view.ViewGroup;

import static butterknife.ButterKnife.findById;


/** An indirection which allows controlling the root container used for each activity. */
public interface AppContainer {
    /** The root {@link ViewGroup} into which the activity should place its contents. */
    ViewGroup bind(Activity activity);

    /** An {@link AppContainer} which returns the normal activity content view. */
    AppContainer DEFAULT = activity -> findById(activity, android.R.id.content);
}
