package com.rocketmade.templateapp.common.drawer;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.rocketmade.templateapp.R;
import com.rocketmade.templateapp.controllers.fragments.ArticleFragment;

/**
 * Created by eliasbagley on 4/6/15.
 */

public enum DrawerItem {
    DEFAULT(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "Articles");

    private int _inactiveIconId;
    private int _activeIconId;
    private String _title;

    DrawerItem(@DrawableRes int inactiveIconId, @DrawableRes int activeIconId, String title) {
        _inactiveIconId = inactiveIconId;
        _activeIconId = activeIconId;
        _title = title;
    }

    @DrawableRes
    public int getInactiveIconId() {
        return _inactiveIconId;
    }

    @DrawableRes
    public int getActiveIconId() {
        return _activeIconId;
    }

    @NonNull
    public String getTitle() {
        return _title;
    }

    @NonNull
    public static Fragment getFragment(@NonNull DrawerItem item) {
        switch (item) {
            case DEFAULT:
                return ArticleFragment.newInstance();
            default:
                throw new IllegalStateException("Unhandled case in DrawerItem.getFragment()");
        }
    }

}

