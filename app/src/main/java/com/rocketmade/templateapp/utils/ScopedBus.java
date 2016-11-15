package com.rocketmade.templateapp.utils;

import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by eliasbagley on 2/2/15.
 */
public class ScopedBus {
    private final Bus bus;

    @Inject
    ScopedBus(Bus bus) {
        this.bus = bus;
    }

    private final Set<Object> objects = new HashSet<Object>();
    private boolean active;

    public void register(Object obj) {
        objects.add(obj);
        if (active) {
            bus.register(obj);
        }
    }

    public void unregister(Object obj) {
        objects.remove(obj);
        if (active) {
            bus.unregister(obj);
        }
    }

    public void post(Object event) {
        bus.post(event);
    }

    public void pause() {
        active = false;
        for (Object obj : objects) {
            bus.unregister(obj);
        }
    }

    public void resume() {
        active = true;
        for (Object obj : objects) {
            bus.register(obj);
        }
    }
}

