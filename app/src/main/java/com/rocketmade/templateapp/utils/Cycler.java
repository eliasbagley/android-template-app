package com.rocketmade.templateapp.utils;

import java.util.Arrays;
import java.util.List;

/**
 * calling next() will infintely cycle through the items passed in
 * Created by eliasbagley on 1/13/16.
 */
public class Cycler<T> {

    private List<T> items;
    private int i;

    public Cycler(List<T> items) {
        this(items, 0);
    }

    public Cycler(T... args) {
        this(Arrays.asList(args));
    }

    private Cycler(List<T> items, int i) {
        this.items = items;
        this. i = i;
    }

    //region public methods

    public T next() {
        int nextIndex = nextIndex();
        T item = items.get(nextIndex);
        incrementIndex();
        return item;
    }

    //endregion

    //region private helper methods

    private int nextIndex() {
        return i % items.size();
    }

    private void incrementIndex(){
        i = (i + 1) % items.size();
    }

    //endregion

}
