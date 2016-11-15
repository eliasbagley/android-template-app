package com.rocketmade.templateapp.network.api;

import com.rocketmade.templateapp.network.serviceresponse.ArticlesServiceResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by eliasbagley on 11/24/15.
 */
public interface ArticleAPI {
    @GET("articles.json")
    Call<ArticlesServiceResponse> articles(@Query("page") int page);
}
