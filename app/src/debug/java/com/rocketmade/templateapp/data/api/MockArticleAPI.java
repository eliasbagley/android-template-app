package com.rocketmade.templateapp.data.api;

import android.content.SharedPreferences;

import com.rocketmade.templateapp.network.api.ArticleAPI;
import com.rocketmade.templateapp.network.serviceresponse.ArticlesServiceResponse;
import com.rocketmade.templateapp.utils.EnumPreferences;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import hugo.weaving.DebugLog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Query;

@Singleton
public final class MockArticleAPI implements ArticleAPI {
  private final SharedPreferences preferences;
  private final Map<Class<? extends Enum<?>>, Enum<?>> responses = new LinkedHashMap<>();

  private Retrofit retrofit;

  @Inject
  MockArticleAPI(SharedPreferences preferences, Retrofit retrofit) {
    this.preferences = preferences;
    this.retrofit = retrofit;

    // Initialize mock responses.
    loadResponse(MockArticlesServiceResponse.class, MockArticlesServiceResponse.SUCCESS);
  }

  /**
   * Initializes the current response for {@code responseClass} from {@code SharedPreferences}, or
   * uses {@code defaultValue} if a response was not found.
   */
  private <T extends Enum<T>> void loadResponse(Class<T> responseClass, T defaultValue) {
    responses.put(responseClass, EnumPreferences.getEnumValue(preferences, responseClass, //
            responseClass.getCanonicalName(), defaultValue));
  }

  public <T extends Enum<T>> T getResponse(Class<T> responseClass) {
    return responseClass.cast(responses.get(responseClass));
  }

  public <T extends Enum<T>> void setResponse(Class<T> responseClass, T value) {
    responses.put(responseClass, value);
    EnumPreferences.saveEnumValue(preferences, responseClass.getCanonicalName(), value);
  }

  //region overrides

  @DebugLog
  @Override
  public Call<ArticlesServiceResponse> articles(@Query("page") int page) {
    ArticlesServiceResponse response = getResponse(MockArticlesServiceResponse.class).response;

    return new Call<ArticlesServiceResponse>() {
      @Override
      public Response<ArticlesServiceResponse> execute() throws IOException {
        return Response.success(response);
      }

      @Override
      public void enqueue(Callback<ArticlesServiceResponse> callback) {
        callback.onResponse(Response.success(response), retrofit);
      }

      @Override
      public void cancel() {
      }

      @Override
      public Call<ArticlesServiceResponse> clone() {
        return null;
      }
    };
  }
}
