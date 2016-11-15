package com.rocketmade.templateapp.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.rocketmade.templateapp.MyApp;
import com.rocketmade.templateapp.common.AppContainer;
import com.rocketmade.templateapp.utils.ScopedBus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.ObjectGraph;
import icepick.Icepick;
import retrofit.Call;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by eliasbagley on 2/2/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Inject        ScopedBus        scopedBus;
    @Inject public AppContainer     appContainer; // For debug drawer

    private ObjectGraph scopedObjectGraph;
    protected boolean               automaticallyManageBus = true; //Automatically registers/unregisters/pauses/resumes bus when necessary
    protected CompositeSubscription compositeSubscription  = new CompositeSubscription(); // For auto unsubscribing all subscriptions added to it on destroy
    protected Set<Call>             calls                  = new HashSet<>(); // For auto cancelling network calls on destroy

    protected ScopedBus getBus() {
        return scopedBus;
    }

    //The onCreate process has been simplified so subclasses only need to implement 2 methods: inflate and initialize
    //Inflate : Should only be used to inflate the layout
    //Initialize : Does all the initialization after the activity has been inflated & injected
    //Subclasses don't need to override the onCreate() method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflate();

        //Inject
        MyApp.injectActivity(this); //Injects butterknife & dagger object graph
        scopedBus.register(this);

        initialize(this);

        createScopedActivityGraph();

        if (hidesActionBar()) {
            getSupportActionBar().hide();
        }

        if (isPortraitOnly()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    protected boolean isPortraitOnly() {
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
        Observable.from(calls).subscribe(call -> call.cancel());
    }

    protected boolean hidesActionBar() {
        return false;
    }

    protected void addCall(Call call) {
        calls.add(call);
    }

    private void createScopedActivityGraph() {
        List<Object> modules = getModules();
        if (modules != null) {
            scopedObjectGraph = MyApp.get(this).createScopedGraph(modules.toArray());
        }
    }

    protected Activity getThis() {
        return this;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (automaticallyManageBus) {
            scopedBus.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (automaticallyManageBus) {
            scopedBus.resume();
        }
    }

    // Override this to add modules to an activities scoped object graph
    protected List<Object> getModules() {
        return null;
    }

    //This makes the back button work
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addFragment(@IdRes int containerViewId,
                            @NonNull Fragment fragment,
                            @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    public void replaceFragment(@IdRes int containerViewId,
                                @NonNull Fragment fragment,
                                @NonNull String fragmentTag,
                                @Nullable String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    public void removeFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }


    //region overrides - these must be overriden by subclass

    protected abstract void inflate();

    protected abstract void initialize(Context context);

    //endregion

    //region helper methods

    protected void recreateCompositeSubscription() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = new CompositeSubscription();
        }
    }

    //endregion
}

