package com.rocketmade.templateapp;

import com.rocketmade.templateapp.dagger.AppModule;

final class Modules {
    static Object[] list(MyApp app) {
        return new Object[] {
                new AppModule(app)
        };
    }

    private Modules() {
        // No instances.
    }
}
