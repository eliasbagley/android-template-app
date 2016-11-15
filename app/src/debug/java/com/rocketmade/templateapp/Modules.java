package com.rocketmade.templateapp;

import com.rocketmade.templateapp.dagger.AppModule;

/**
 * Created by eliasbagley on 4/2/15.
 */
final class Modules {
    static Object[] list(MyApp app) {
        return new Object[] {
                new AppModule(app),
                new DebugAppModule(),
        };
    }

    private Modules() {
        // No instances.
    }
}

