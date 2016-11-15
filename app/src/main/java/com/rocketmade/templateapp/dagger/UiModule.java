package com.rocketmade.templateapp.dagger;

import com.rocketmade.templateapp.controllers.activities.IntroActivity;
import com.rocketmade.templateapp.common.AppContainer;
import com.rocketmade.templateapp.ui.ActivityHierarchyServer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by eliasbagley on 2/2/15.
 */
@Module(
        injects = {
                IntroActivity.class
        },
        complete = false,
        library = true
)
public class UiModule {
    @Provides @Singleton
    AppContainer provideAppContiner() {
        return AppContainer.DEFAULT;
    }

    @Provides @Singleton
    ActivityHierarchyServer provideActivityHierarchyServer() {
        return ActivityHierarchyServer.NONE;
    }
}

