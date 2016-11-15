package com.rocketmade.templateapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import lombok.Value;

/**
 * Created by eliasbagley on 11/25/15.
 */


//TODO: restarting the loader for live filtering
//TODO: supporting multiple filters
//TODO: supporting filters across a relationship

// An API wrapper for LoaderCallback
public class LoaderBuilder {
    public enum Order {
        ASCENDING("ASC"),
        DESCENDING("DESC");

        String value;

        Order(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    @Value
    class SortOrder {
        String property;
        Order  order;

        public String toString() {
            return property + " " + order;
        }
    }

    @Inject
    public LoaderBuilder() {

    }

    @NonNull private  Uri         uri;
    @NonNull private  CursorAdapter adapter;
    @Nullable private Integer       limit;
    @Nullable private Integer       offset;
    @Nullable private Predicate     predicate;
    @NonNull private List<SortOrder> sortOrders = new ArrayList<>();


    //region builder methods

    public LoaderBuilder sortBy(String sortProperty) {
        return sortBy(sortProperty, Order.ASCENDING);
    }

    public LoaderBuilder sortBy(String sortProperty, Order order) {
        SortOrder sortOrder = new SortOrder(sortProperty, order);
        this.sortOrders.add(sortOrder);
        return this;
    }

    public LoaderBuilder uri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public LoaderBuilder adapter(CursorAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    // Max number of objects for the loader to return
    public LoaderBuilder limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    // The offset for where to start loading
    public LoaderBuilder offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public LoaderBuilder filter(Predicate predicate) {
        this.predicate = predicate;
        return this;
    }

    //endregion

    public LoaderManager.LoaderCallbacks<Cursor> build(Context context) {
        if (uri == null) {
            throw new AssertionError("Uri must not be null");
        }
        if (adapter == null) {
            throw new AssertionError("Adapter must not be null");
        }

        return new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                // Build the sort order list in to a SQL string
                String orderBy = sortOrderListToSQLString(sortOrders);

                // Set the limit and offset
                //TODO: decouple from orderBy property
                if (limit != null && offset != null) {
                    if (orderBy != null) {
                        orderBy += " LIMIT " + offset + "," + limit;
                    }
                }

                // Filtering
                String[] selectionArgs = null;
                String   selection     = null;
                if (predicate != null) {
                    selection = predicate.getProperty() + predicate.getComparator() + "?";
                    selectionArgs = new String[]{predicate.getValue()};
                }

                return new CursorLoader(context,
                        uri,
                        null, selection, selectionArgs, orderBy);
            }

            @DebugLog
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.changeCursor(cursor);
            }

            @DebugLog
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.swapCursor(null);
            }
        };
    }

    //region private helper methods

    @Nullable
    private static String sortOrderListToSQLString(@NonNull List<SortOrder> sortOrders) {
        String orderBy = null;

        if (sortOrders.size() > 0) {
            orderBy = "";
        }

        for (int i = 0; i < sortOrders.size(); i++) {
            SortOrder sortOrder = sortOrders.get(i);
            orderBy += sortOrder.toString();

            // if last item in the list, don't add the comma
            if (i != (sortOrders.size() - 1)) {
                orderBy += ", ";
            }
        }

        return orderBy;
    }

    //endregion
}
