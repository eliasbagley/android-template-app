package com.rocketmade.templateapp.common.drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rocketmade.templateapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by eliasbagley on 4/6/15.
 */


public class DrawerItemView extends RelativeLayout {

    @Bind(R.id.drawer_item_icon)  ImageView _drawerIcon;
    @Bind(R.id.drawer_item_title) TextView  _drawerTitle;

    //region constructors

    public DrawerItemView(Context context) {
        super(context);
        initialize(context);
    }

    public DrawerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public DrawerItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_drawer_item, this);
        ButterKnife.bind(this);
    }

    //endregion

    //region public methods

    public void populateWithDrawerItem(DrawerItem drawerItem, boolean active) {
        if (active) {
            _drawerIcon.setBackground(getResources().getDrawable(drawerItem.getActiveIconId()));
        } else {
            _drawerIcon.setBackground(getResources().getDrawable(drawerItem.getInactiveIconId()));
        }
        _drawerTitle.setText(drawerItem.getTitle());
    }

    //endregion
}
