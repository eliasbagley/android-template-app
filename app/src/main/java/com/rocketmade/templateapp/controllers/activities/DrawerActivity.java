package com.rocketmade.templateapp.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rocketmade.templateapp.R;
import com.rocketmade.templateapp.common.drawer.DrawerAdapter;
import com.rocketmade.templateapp.common.drawer.DrawerItem;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;

public class DrawerActivity extends BaseActivity {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, DrawerActivity.class);
        activity.startActivity(intent);
    }

    @Inject Bus _bus;

    @Bind(R.id.drawer_layout)          DrawerLayout _drawer;
    @Bind(R.id.navigation_drawer_list) ListView     _drawerList;

    private ActionBarDrawerToggle _drawerToggle;

    @Override
    protected void inflate() {
        setContentView(R.layout.activity_main_drawer);
    }

    @Override
    protected void initialize(Context context) {
        setup();
    }

    @Override
    protected boolean hidesActionBar() {
        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        _drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setup() {
        setupActionBar();
        setupDrawer();
        initializeFragment();
    }

    private void initializeFragment() {
        Fragment fragment = DrawerItem.getFragment(DrawerItem.DEFAULT);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void setupActionBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_dark_material_dark)));
    }

    private void setupDrawer() {
        _drawerToggle = new ActionBarDrawerToggle(this, _drawer, R.string.open, R.string.closed) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        _drawer.setDrawerListener(_drawerToggle);

        final DrawerAdapter adapter = new DrawerAdapter(this);
        _drawerList.setAdapter(adapter);
        _drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                adapter.selectPosition(position);
                _drawerList.setItemChecked(position, true);
                _drawer.closeDrawers();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = DrawerItem.getFragment(adapter.getItem(position));

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit();
                    }
                }, 250); // this is used to make it so that loading the fragment doesn't cause the drawer close animation to stutter
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    //region bus subscriptions

    //endregion


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
