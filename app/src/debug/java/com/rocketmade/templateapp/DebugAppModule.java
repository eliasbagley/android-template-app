package com.rocketmade.templateapp;

import com.rocketmade.templateapp.annotations.IsInstrumentationTest;
import com.rocketmade.templateapp.network.DebugAPIModule;
import com.rocketmade.templateapp.ui.DebugUiModule;
import com.rocketmade.templateapp.dagger.AppModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by eliasbagley on 4/2/15.
 */
@Module(
        addsTo = AppModule.class,
        includes = {
                DebugUiModule.class,
                DebugAPIModule.class
        },
        overrides = true
)
public final class DebugAppModule {
    // Low-tech flag to force certain debug build behaviors when running in an instrumentation test.
    // This value is used in the creation of singletons so it must be set before the graph is created.
    static boolean instrumentationTest = false;

//    @Provides @Singleton
//    @IsInstrumentationTest
//    boolean provideIsInstrumentationTest() {
//            return instrumentationTest;
//    }
}

