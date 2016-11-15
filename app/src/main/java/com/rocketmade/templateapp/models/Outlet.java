package com.rocketmade.templateapp.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;

import org.parceler.Parcel;

import lombok.ToString;

/**
 * Created by eliasbagley on 11/25/15.
 */

@ToString
@ObjectMappable
public class Outlet extends BaseModel {
    //region json keys and column names
    public static final String TABLE_NAME = "Outlets";
    public static final String ID         = "outlet_id";
    public static final String NAME       = "outlet_name";
    public static final String IMAGE      = "outlet_profile_image";
    //endregion

    @SerializedName("id")
    @Column(ID)
    public int id;

    @SerializedName("name")
    @Column(NAME)
    public String name;

    @SerializedName("profile_image")
    @Column(IMAGE)
    public String image;


    //region builder

    public static OutletBuilder builder() {
        return new OutletBuilder();
    }

    public static class OutletBuilder {
        private Outlet outlet = new Outlet();

        public OutletBuilder() {
        }

        public OutletBuilder id(int id) {
            outlet.id = id;
            return this;
        }

        public OutletBuilder name(String name) {
            outlet.name = name;
            return this;
        }

        public OutletBuilder image(String image) {
            outlet.image = image;
            return this;
        }

        public Outlet build() {
            return outlet;
        }
    }

    //endregion

    public ContentValues contentValues() {
        return OutletMapper.contentValues()
                .id(id)
                .name(name)
                .image(image)
                .build();

    }

    public static Outlet fromCursor(Cursor cursor) {
        return OutletMapper.MAPPER.call(cursor);
    }
}
