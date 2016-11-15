package com.rocketmade.templateapp;

import android.app.Activity;
import android.content.SharedPreferences;

import com.rocketmade.templateapp.utils.Constants;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by eliasbagley on 2/2/15.
 */

@Singleton
public class Session {
    private static final String SESSION_AUTH_KEY_NAME = "access_token";

    private SharedPreferences.Editor _editor;
    private SharedPreferences        _pref;
    //private UserModel               _user;
    private String                   _accessToken;
    private Bus                      _bus;

    @Inject
    public Session(Bus bus) {
        initialize();
        _bus = bus;
        _bus.register(this);
    }

    public synchronized void initialize() {
        _pref = MyApp.appContext.getSharedPreferences(Constants.SESSION_CACHE_NAME, 0);
        _editor = _pref.edit();

        _accessToken = _pref.getString(SESSION_AUTH_KEY_NAME, null);
    }


    //region getters and setters

    public synchronized boolean isSignedIn() {
        return getAccessToken() != null;
    }

    public String getAccessToken() {
        return _accessToken;
    }

    public synchronized void setAccessToken(String accessToken) {
        _accessToken = accessToken;
        _editor.putString(SESSION_AUTH_KEY_NAME, accessToken);
        _editor.commit();
    }

    //endregion

    public synchronized void signOut(Activity activity) {
        setAccessToken(null);
    }

}
