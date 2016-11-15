package com.rocketmade.templateapp.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Created by eliasbagley on 2/4/15.
 * http://stackoverflow.com/questions/9483348/gson-treat-null-as-empty-string/24252578#24252578
 */
public class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

        Class<T> rawType = (Class<T>) type.getRawType();
        if (rawType != String.class) {
            return null;
        }
        return (TypeAdapter<T>) new StringAdapter();
    }
}

