package com.rocketmade.templateapp;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.codemonkeylabs.fpslibrary.TinyDancer;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.hannesdorfmann.sqlbrite.dao.DaoManager;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.rocketmade.templateapp.data.Injector;
import com.rocketmade.templateapp.data.LumberYard;
import com.rocketmade.templateapp.ui.ActivityHierarchyServer;
import com.rocketmade.templateapp.utils.Utils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by eliasbagley on 2/2/15.
 */
public class MyApp extends Application {
    public static Context     appContext;
    public static MyApp       app;
    private       ObjectGraph objectGraph;

    @Inject Bus                     bus;
    @Inject ActivityHierarchyServer activityHierarchyServer;
    @Inject LumberYard              lumberYard;
    @Inject DaoManager              daoManager;

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidThreeTen.init(this);
        Iconify.with(new FontAwesomeModule());
        LeakCanary.install(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (BuildConfig.DEBUG) {
            enableStrictMode();

            Timber.plant(new Timber.DebugTree());

            Stetho.initializeWithDefaults(this);

            TinyDancer.create()
                    .redFlagPercentage(.1f) // set red indicator for 10%
                    .startingXPosition((int) (Utils.getScreenWidth(getBaseContext()) * 0.70)) // start 70% of way to right
                    .startingYPosition(50)
                    .show(this);
        } else {
            Fabric.with(this, new Crashlytics.Builder().disabled(BuildConfig.DEBUG).build(), new Crashlytics());
        }


        appContext = getBaseContext();
        app = this;

        initialize();

        lumberYard.cleanUp();
        Timber.plant(lumberYard.tree());

        registerActivityLifecycleCallbacks(activityHierarchyServer);
    }

    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls()
                .penaltyFlashScreen()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    public void initialize() {
        buildObjectGraphAndInject();
        bus.register(this);
    }

    private void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

    public static MyApp get(Context context) {
        return (MyApp) context.getApplicationContext();
    }

    public static void injectActivity(Activity activity) {
        ButterKnife.bind(activity);
        get(activity).inject(activity);
    }

    public static void injectFragment(Fragment fragment) {
        app.inject(fragment);
    }

    public static void injectFragment(Fragment fragment, View view) {
        app.inject(fragment);
        ButterKnife.bind(fragment, view);
    }


    public static void injectView(View view) {
        get(view.getContext()).inject(view);
    }

    public static void injectService(Service service) {
        app.inject(service);
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }

    public static ProgressDialog loadingHud;

    public static void showLoadingHUD(Context context) {
        showLoadingHUD(context, "Loading...");
    }

    public static void showLoadingHUD(Context context, String message) {
        if (loadingHud != null) {
            return;
        }
        loadingHud = new ProgressDialog(context);
        loadingHud.setMessage(message);
        loadingHud.setCancelable(false);
        loadingHud.show();
    }

    public static void hideLoadingHUD() {
        if (loadingHud != null) {
            loadingHud.dismiss();
            loadingHud = null;
        }
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return objectGraph;
        }
        return super.getSystemService(name);
    }
}
