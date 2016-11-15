package com.rocketmade.templateapp.utils;

import lombok.Value;

/**
 * Created by eliasbagley on 11/25/15.
 */

/*
 Example: new Predicate("age", "<", 23")
 */

@Value
public class Predicate {
    private String property;
    private String comparator;
    private String value;
}
