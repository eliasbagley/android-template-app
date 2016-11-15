package com.rocketmade.templateapp.network;

import android.app.Application;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.rocketmade.templateapp.dagger.APIModule;
import com.rocketmade.templateapp.data.AnimationSpeed;
import com.rocketmade.templateapp.data.ApiEndpoint;
import com.rocketmade.templateapp.data.ApiEndpoints;
import com.rocketmade.templateapp.data.CaptureIntents;
import com.rocketmade.templateapp.data.DebugIntentFactory;
import com.rocketmade.templateapp.data.IntentFactory;
import com.rocketmade.templateapp.data.IsMockMode;
import com.rocketmade.templateapp.data.MockRequestHandler;
import com.rocketmade.templateapp.data.NetworkDelay;
import com.rocketmade.templateapp.data.NetworkFailurePercent;
import com.rocketmade.templateapp.data.NetworkVariancePercent;
import com.rocketmade.templateapp.data.PicassoDebugging;
import com.rocketmade.templateapp.data.PixelGridEnabled;
import com.rocketmade.templateapp.data.PixelRatioEnabled;
import com.rocketmade.templateapp.data.ScalpelEnabled;
import com.rocketmade.templateapp.data.ScalpelWireframeEnabled;
import com.rocketmade.templateapp.data.SeenDebugDrawer;
import com.rocketmade.templateapp.data.api.MockArticleAPI;
import com.rocketmade.templateapp.data.prefs.InetSocketAddressPreferenceAdapter;
import com.rocketmade.templateapp.network.api.ArticleAPI;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.net.InetSocketAddress;

import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Retrofit;
import retrofit.mock.CallBehaviorAdapter;
import retrofit.mock.MockRetrofit;
import retrofit.mock.NetworkBehavior;
import retrofit.mock.RxJavaBehaviorAdapter;
import timber.log.Timber;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by eliasbagley on 2/2/15.
 */
@Module(
        complete = false,
        library = true,
        overrides = true
)
public final class DebugAPIModule {
    private static final int DEFAULT_ANIMATION_SPEED = 1; // 1x (normal) speed.
    private static final boolean DEFAULT_PICASSO_DEBUGGING = false; // Debug indicators displayed
    private static final boolean DEFAULT_PIXEL_GRID_ENABLED = false; // No pixel grid overlay.
    private static final boolean DEFAULT_PIXEL_RATIO_ENABLED = false; // No pixel ratio overlay.
    private static final boolean DEFAULT_SCALPEL_ENABLED = false; // No crazy 3D view tree.
    private static final boolean DEFAULT_SCALPEL_WIREFRAME_ENABLED = false; // Draw views by default.
    private static final boolean DEFAULT_SEEN_DEBUG_DRAWER = false; // Show debug drawer first time.
    private static final boolean DEFAULT_CAPTURE_INTENTS = true; // Capture external intents.

    @Provides @Singleton HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Timber.tag("OkHttp").v(message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return loggingInterceptor;
    }

    @Provides @Singleton @Named("Api") OkHttpClient provideApiClient(OkHttpClient client,
                                                                     OauthInterceptor oauthInterceptor, HttpLoggingInterceptor loggingInterceptor) {
        client = APIModule.createApiClient(client, oauthInterceptor);
        client.interceptors().add(loggingInterceptor);
        return client;
    }

    @Provides @Singleton
    NetworkBehavior provideBehavior(@NetworkDelay Preference<Long> networkDelay,
                                                         @NetworkFailurePercent Preference<Integer> networkFailurePercent,
                                                         @NetworkVariancePercent Preference<Integer> networkVariancePercent) {
        NetworkBehavior behavior = NetworkBehavior.create();
        behavior.setDelay(networkDelay.get(), MILLISECONDS);
        behavior.setFailurePercent(networkFailurePercent.get());
        behavior.setVariancePercent(networkVariancePercent.get());
        return behavior;
    }

    @Provides @Singleton
    MockRetrofit provideMockRetrofit(NetworkBehavior behavior, Retrofit retrofit) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return new MockRetrofit(behavior, new CallBehaviorAdapter(retrofit, executor));
    }

    @Provides @Singleton
    HttpUrl provideHttpUrl(@ApiEndpoint Preference<String> apiEndpoint) {
        return HttpUrl.parse(apiEndpoint.get());
    }

    @Provides @Singleton
    RxSharedPreferences provideRxSharedPreferences(SharedPreferences prefs) {
        return RxSharedPreferences.create(prefs);
    }

    @Provides @Singleton
    IntentFactory provideIntentFactory(@IsMockMode boolean isMockMode,
                                                            @CaptureIntents Preference<Boolean> captureIntents) {
        return new DebugIntentFactory(IntentFactory.REAL, isMockMode, captureIntents);
    }

  @Provides @Singleton OkHttpClient provideOkHttpClient(Application app,
      Preference<InetSocketAddress> networkProxyAddress) {
    OkHttpClient client = APIModule.createOkHttpClient(app);
    client.setSslSocketFactory(createBadSslSocketFactory());
    client.setProxy(InetSocketAddressPreferenceAdapter.createProxy(networkProxyAddress.get()));
    return client;
  }

  @Provides @Singleton @AccessToken Preference<String> provideAccessToken(RxSharedPreferences prefs,
      @ApiEndpoint Preference<String> endpoint) {
    // Return an endpoint-specific preference.
    return prefs.getString("access-token-" + endpoint.get());
  }

    @Provides @Singleton @ApiEndpoint
    Preference<String> provideEndpointPreference(RxSharedPreferences preferences) {
        return preferences.getString("debug_endpoint", ApiEndpoints.PRODUCTION.url);
    }

    @Provides @Singleton @IsMockMode
    boolean provideIsMockMode(@ApiEndpoint Preference<String> endpoint) {
        // Running in an instrumentation forces mock mode.
        return ApiEndpoints.isMockMode(endpoint.get());
    }

    @Provides @Singleton @NetworkDelay
    Preference<Long> provideNetworkDelay(RxSharedPreferences preferences) {
        return preferences.getLong("debug_network_delay", 2000l);
    }

    @Provides @Singleton @NetworkFailurePercent
    Preference<Integer> provideNetworkFailurePercent(RxSharedPreferences preferences) {
        return preferences.getInteger("debug_network_failure_percent", 3);
    }

    @Provides @Singleton @NetworkVariancePercent
    Preference<Integer> provideNetworkVariancePercent(RxSharedPreferences preferences) {
        return preferences.getInteger("debug_network_variance_percent", 40);
    }

    @Provides @Singleton
    Preference<InetSocketAddress> provideNetworkProxyAddress(RxSharedPreferences preferences) {
        return preferences.getObject("debug_network_proxy",
                InetSocketAddressPreferenceAdapter.INSTANCE);
    }

    @Provides @Singleton @CaptureIntents
    Preference<Boolean> provideCaptureIntentsPreference(RxSharedPreferences preferences) {
        return preferences.getBoolean("debug_capture_intents", DEFAULT_CAPTURE_INTENTS);
    }

    @Provides @Singleton @AnimationSpeed
    Preference<Integer> provideAnimationSpeed(RxSharedPreferences preferences) {
        return preferences.getInteger("debug_animation_speed", DEFAULT_ANIMATION_SPEED);
    }

    @Provides @Singleton @PicassoDebugging
    Preference<Boolean> providePicassoDebugging(RxSharedPreferences preferences) {
        return preferences.getBoolean("debug_picasso_debugging", DEFAULT_PICASSO_DEBUGGING);
    }

    @Provides @Singleton @PixelGridEnabled
    Preference<Boolean> providePixelGridEnabled(RxSharedPreferences preferences) {
        return preferences.getBoolean("debug_pixel_grid_enabled", DEFAULT_PIXEL_GRID_ENABLED);
    }

    @Provides @Singleton @PixelRatioEnabled
    Preference<Boolean> providePixelRatioEnabled(RxSharedPreferences preferences) {
        return preferences.getBoolean("debug_pixel_ratio_enabled", DEFAULT_PIXEL_RATIO_ENABLED);
    }

    @Provides @Singleton @SeenDebugDrawer
    Preference<Boolean> provideSeenDebugDrawer(RxSharedPreferences preferences) {
        return preferences.getBoolean("debug_seen_debug_drawer", DEFAULT_SEEN_DEBUG_DRAWER);
    }

    @Provides @Singleton @ScalpelEnabled
    Preference<Boolean> provideScalpelEnabled(RxSharedPreferences preferences) {
        return preferences.getBoolean("debug_scalpel_enabled", DEFAULT_SCALPEL_ENABLED);
    }

    @Provides @Singleton @ScalpelWireframeEnabled
    Preference<Boolean> provideScalpelWireframeEnabled(RxSharedPreferences preferences) {
        return preferences.getBoolean("debug_scalpel_wireframe_drawer",
                DEFAULT_SCALPEL_WIREFRAME_ENABLED);
    }

  @Provides @Singleton
  Picasso providePicasso(OkHttpClient client, NetworkBehavior behavior,
      @IsMockMode boolean isMockMode, Application app) {
    Picasso.Builder builder = new Picasso.Builder(app).downloader(new OkHttpDownloader(client));
    if (isMockMode) {
      builder.addRequestHandler(new MockRequestHandler(behavior, app.getAssets()));
    }
    builder.listener((picasso, uri, exception) -> {
      Timber.e(exception, "Error while loading image " + uri);
    });
    return builder.build();
  }

    private static SSLSocketFactory createBadSslSocketFactory() {
        try {
            // Construct SSLSocketFactory that accepts any cert.
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager() {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            context.init(null, new TrustManager[] { permissive }, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    //region mock API endpoints

    @Provides @Singleton
    ArticleAPI provideMockArticleAPI(Retrofit retrofit, MockRetrofit mockRetrofit, @IsMockMode boolean isMockMode, MockArticleAPI mockService) {
        if (isMockMode) {
            Timber.d("Returning mock Article API");
            return mockRetrofit.create(ArticleAPI.class, mockService);
        }
        return retrofit.create(ArticleAPI.class);
    }

    //endregion
}

