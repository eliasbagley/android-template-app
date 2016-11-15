package com.rocketmade.templateapp.utils;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by eliasbagley on 1/29/16.
 */
public class CyclerTest {

    Cycler<String> cycler;

    @Before
    public void init() {
        cycler = new Cycler<>("1", "2", "3", "4");
    }

    @Test
    public void testOnNext() {
        assertThat(cycler.next(), is("1"));
    }


    @Test
    public void testCyclesThoughItems() {
        cycler.next(); // returns "1"
        cycler.next(); // returns "2"
        cycler.next(); // returns "3"
        cycler.next(); // returns "4"
        String next = cycler.next(); // returns "1"

        assertThat(next, is("1"));
    }
}
