package com.rocketmade.templateapp.common.drawer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rocketmade.templateapp.utils.ThreadPreconditions;

/**
 * Created by eliasbagley on 4/6/15.
 */
public class DrawerAdapter extends BaseAdapter {

    private DrawerItem _selectedItem = DrawerItem.DEFAULT;

    private Context _context;

    public DrawerAdapter(Context context) {
       _context = context;
    }

    public void selectPosition(int position) {
        ThreadPreconditions.checkOnMainThread();
        _selectedItem = getItem(position);
        notifyDataSetChanged();
    }

    //region base adapter methods

    @Override
    public int getCount() {
        return DrawerItem.values().length;
    }

    @Override
    public DrawerItem getItem(int position) {
        return DrawerItem.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemView view;

        if (convertView == null) {
            view = new DrawerItemView(_context);
        } else {
            view = (DrawerItemView) convertView;
        }

        DrawerItem item = getItem(position);
        boolean active = item == _selectedItem;

        view.populateWithDrawerItem(item, active);

        return view;
    }

    //endregion
}
