package com.rocketmade.templateapp.ui;



import com.rocketmade.templateapp.common.AppContainer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by eliasbagley on 8/7/14.
 */

@Module(
        injects = {
                DebugAppContainer.class,
                DebugView.class
        },
        complete = false,
        library = true,
        overrides = true
)
public class DebugUiModule {
    @Provides @Singleton
    AppContainer provideAppContainer(DebugAppContainer debugAppContainer) {
        return debugAppContainer;
    }

    @Provides @Singleton ActivityHierarchyServer provideActivityHierarchyServer() {
        return new SocketActivityHierarchyServer();
    }
}
