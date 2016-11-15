package com.rocketmade.templateapp.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hannesdorfmann.sqlbrite.dao.DaoManager;
import com.rocketmade.templateapp.data.IntentFactory;
import com.rocketmade.templateapp.data.dao.ArticleDao;
import com.rocketmade.templateapp.data.dao.OutletDao;
import com.rocketmade.templateapp.network.AccessToken;
import com.rocketmade.templateapp.network.OauthInterceptor;
import com.rocketmade.templateapp.network.api.ArticleAPI;
import com.rocketmade.templateapp.utils.Constants;
import com.rocketmade.templateapp.utils.DateConverters.InstantConverter;
import com.rocketmade.templateapp.utils.DateConverters.LocalDateConverter;
import com.rocketmade.templateapp.utils.NullStringToEmptyAdapterFactory;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;

import java.io.File;
import java.lang.reflect.Type;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import timber.log.Timber;

import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by eliasbagley on 2/2/15.
 */

@Module(
        library = true,
        complete = false
)
public class APIModule {
    public static final int DISK_CACHE_SIZE = (int) MEGABYTES.toBytes(50);

    private static String baseUrl = null;
    public static String getBaseUrl() {
        return baseUrl;
    }

    public static final HttpUrl PRODUCTION_API_URL = HttpUrl.parse(Constants.PRODUCTION_BASE_URL);
    public static final HttpUrl STAGING_API_URL = HttpUrl.parse(Constants.STAGING_BASE_URL);
    public static final HttpUrl DEBUG_API_URL = HttpUrl.parse(Constants.DEBUG_BASE_URL);

    @Provides
    @Singleton
    public HttpUrl provideBaseUrl() {
        return PRODUCTION_API_URL;
    }

    @Provides
    @Singleton
    Clock provideClock() {
        return Clock.systemDefaultZone();
    }

    @Provides
    @Singleton
    IntentFactory provideIntentFactory() {
        return IntentFactory.REAL;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    @Provides @Singleton @Named("Api")
    OkHttpClient provideApiClient(OkHttpClient client, OauthInterceptor oauthInterceptor) {
        return createApiClient(client, oauthInterceptor);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        //Note there is a bug in GSON 2.3.1 that can cause a stack overflow when working with RealmObjects.
        // To work around this, use the ExclusionStrategy below or downgrade to 1.7.1
        // See more here: https://code.google.com/p/google-gson/issues/detail?id=440

        Type LOCAL_DATE_TYPE = new TypeToken<LocalDate>() {}.getType();
        Type INSTANT_TYPE = new TypeToken<Instant>() {}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<>())
                .registerTypeAdapter(LOCAL_DATE_TYPE, new LocalDateConverter())
                .registerTypeAdapter(INSTANT_TYPE, new InstantConverter())
                .create();

        return gson;
    }

    @Provides
    @Singleton
    GsonConverterFactory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(HttpUrl baseUrl, OkHttpClient client, GsonConverterFactory gsonFactory) {
        this.baseUrl = baseUrl.toString();

        Retrofit restAdapter = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(gsonFactory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return restAdapter;
    }

    @Provides @Singleton
    Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttpDownloader(client))
                .listener((picasso, uri, e) -> Timber.e(e, "Failed to load image: %s", uri))
                .build();
    }

    //endregion

    public static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        client.networkInterceptors().add(new StethoInterceptor());

        return client;
    }

    public static OkHttpClient createApiClient(OkHttpClient client, OauthInterceptor oauthInterceptor) {
        client = client.clone();
        client.interceptors().add(oauthInterceptor);
        return client;
    }

    @Provides @Singleton @AccessToken
    Preference<String> provideAccessToken(RxSharedPreferences prefs) {
        return prefs.getString("access-token");
    }

    //region API provides

    @Provides @Singleton
    public ArticleAPI provideArticleApi(Retrofit retrofit) {
        return retrofit.create(ArticleAPI.class);
    }


    //endregion
}

