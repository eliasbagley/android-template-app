package com.rocketmade.templateapp.dagger;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

import com.rocketmade.templateapp.MyApp;
import com.rocketmade.templateapp.controllers.activities.ArticleDetailActivity;
import com.rocketmade.templateapp.controllers.activities.IntroActivity;
import com.rocketmade.templateapp.controllers.activities.DrawerActivity;
import com.rocketmade.templateapp.controllers.fragments.ArticleFragment;
import com.rocketmade.templateapp.utils.AsyncMainThreadBus;
import com.rocketmade.templateapp.views.ArticleListViewItem;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by eliasbagley on 2/2/15.
 */

@Module(
        includes = {
                DataModule.class,
                UiModule.class
        },
        injects = {
                //NOTE: If any class is being injected, ie MyApp.injectActivity(this), it must be listed here.
                ArticleDetailActivity.class,
                ArticleListViewItem.class,
                IntroActivity.class,
                DrawerActivity.class,
                ArticleFragment.class,
                MyApp.class,
        },
        library = true
)

public class AppModule {
    private final MyApp app;

    public AppModule(MyApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    Bus provideBus() {
        return new AsyncMainThreadBus();
    }


    @Provides
    @Singleton
    NotificationManager provideNotificationManager() {
        return (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
