package com.rocketmade.templateapp.common.drawer;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by eliasbagley on 4/6/15.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {



    }
}
