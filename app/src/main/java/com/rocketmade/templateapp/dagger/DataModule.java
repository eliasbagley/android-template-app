package com.rocketmade.templateapp.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.hannesdorfmann.sqlbrite.dao.DaoManager;
import com.rocketmade.templateapp.data.dao.ArticleDao;
import com.rocketmade.templateapp.data.dao.OutletDao;
import com.rocketmade.templateapp.utils.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by eliasbagley on 2/23/16.
 */
@Module(
        includes = {
                APIModule.class
        },
        complete= false,
        library = true
)
public class DataModule {
    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    RxSharedPreferences provideRxSharedPreferences(SharedPreferences prefs) {
        return RxSharedPreferences.create(prefs);
    }

    @Provides
    @Singleton
    public ArticleDao provideArticleDao() {
        return new ArticleDao();
    }

    @Provides
    @Singleton
    public OutletDao provideOutletDao() {
        return new OutletDao();
    }


    @Provides @Singleton
    public DaoManager provideDaoManager(Application context, ArticleDao articleDao, OutletDao outletDao) {
        int dbVersion = 1;
        DaoManager daoManager = new DaoManager(context, "TemplateApp.db", dbVersion,
                articleDao, outletDao);
        daoManager.setLogging(true);

        return daoManager;
    }
}

