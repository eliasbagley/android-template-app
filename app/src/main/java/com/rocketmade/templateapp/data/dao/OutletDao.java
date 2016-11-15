package com.rocketmade.templateapp.data.dao;

import android.database.sqlite.SQLiteDatabase;

import com.hannesdorfmann.sqlbrite.dao.Dao;
import com.rocketmade.templateapp.models.Outlet;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

/**
 * Created by eliasbagley on 12/1/15.
 */
public class OutletDao extends Dao {
    @Override
    public void createTable(SQLiteDatabase database) {
        CREATE_TABLE(
                Outlet.TABLE_NAME,
                Outlet.ID + " INTEGER PRIMARY KEY NOT NULL",
                Outlet.NAME + " TEXT NOT NULL",
                Outlet.IMAGE + " TEXT NOT NULL"
        ).execute(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveOutlets(List<Outlet> outlets) {
        BriteDatabase.Transaction transaction = newTransaction();
        try {
            for (Outlet outlet : outlets) {
                insert(Outlet.TABLE_NAME, outlet.contentValues(), SQLiteDatabase.CONFLICT_REPLACE);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void deleteAll() {
        delete(Outlet.TABLE_NAME);
    }
}
