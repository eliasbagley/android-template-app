package com.rocketmade.templateapp.ui;

import android.animation.ValueAnimator;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.f2prateek.rx.preferences.Preference;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.rocketmade.templateapp.BuildConfig;
import com.rocketmade.templateapp.R;
import com.rocketmade.templateapp.data.AnimationSpeed;
import com.rocketmade.templateapp.data.ApiEndpoint;
import com.rocketmade.templateapp.data.ApiEndpoints;
import com.rocketmade.templateapp.data.CaptureIntents;
import com.rocketmade.templateapp.data.Injector;
import com.rocketmade.templateapp.data.IsMockMode;
import com.rocketmade.templateapp.data.LumberYard;
import com.rocketmade.templateapp.data.NetworkDelay;
import com.rocketmade.templateapp.data.NetworkFailurePercent;
import com.rocketmade.templateapp.data.NetworkVariancePercent;
import com.rocketmade.templateapp.data.PicassoDebugging;
import com.rocketmade.templateapp.data.PixelGridEnabled;
import com.rocketmade.templateapp.data.PixelRatioEnabled;
import com.rocketmade.templateapp.data.ScalpelEnabled;
import com.rocketmade.templateapp.data.ScalpelWireframeEnabled;
import com.rocketmade.templateapp.data.api.MockArticleAPI;
import com.rocketmade.templateapp.data.api.MockArticlesServiceResponse;
import com.rocketmade.templateapp.data.dao.ArticleDao;
import com.rocketmade.templateapp.data.dao.MasterDao;
import com.rocketmade.templateapp.data.dao.OutletDao;
import com.rocketmade.templateapp.data.prefs.InetSocketAddressPreferenceAdapter;
import com.rocketmade.templateapp.ui.logs.LogsDialog;
import com.rocketmade.templateapp.util.Keyboards;
import com.rocketmade.templateapp.utils.StringUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;

import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAccessor;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.mock.NetworkBehavior;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;
import static com.rocketmade.templateapp.utils.Utils.snackbar;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.threeten.bp.format.DateTimeFormatter.ISO_INSTANT;

public final class DebugView extends FrameLayout {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.US).withZone(ZoneId.systemDefault());

    @Bind(R.id.debug_contextual_title) View         contextualTitleView;
    @Bind(R.id.debug_contextual_list)  LinearLayout contextualListView;

    @Bind(R.id.debug_network_endpoint)      Spinner endpointView;
    @Bind(R.id.debug_network_endpoint_edit) View    endpointEditView;
    @Bind(R.id.debug_network_delay)         Spinner networkDelayView;
    @Bind(R.id.debug_network_variance)      Spinner networkVarianceView;
    @Bind(R.id.debug_network_error)         Spinner networkErrorView;
    @Bind(R.id.debug_network_proxy)         Spinner networkProxyView;
    @Bind(R.id.debug_network_logging)       Spinner networkLoggingView;

    @Bind(R.id.debug_capture_intents)   Switch  captureIntentsView;
    @Bind(R.id.debug_articles_response) Spinner repositoriesResponseView;

    @Bind(R.id.debug_ui_animation_speed)   Spinner uiAnimationSpeedView;
    @Bind(R.id.debug_ui_pixel_grid)        Switch  uiPixelGridView;
    @Bind(R.id.debug_ui_pixel_ratio)       Switch  uiPixelRatioView;
    @Bind(R.id.debug_ui_scalpel)           Switch  uiScalpelView;
    @Bind(R.id.debug_ui_scalpel_wireframe) Switch  uiScalpelWireframeView;

    @Bind(R.id.debug_build_name) TextView buildNameView;
    @Bind(R.id.debug_build_code) TextView buildCodeView;
    @Bind(R.id.debug_build_sha)  TextView buildShaView;
    @Bind(R.id.debug_build_date) TextView buildDateView;

    @Bind(R.id.debug_device_make)       TextView deviceMakeView;
    @Bind(R.id.debug_device_model)      TextView deviceModelView;
    @Bind(R.id.debug_device_resolution) TextView deviceResolutionView;
    @Bind(R.id.debug_device_density)    TextView deviceDensityView;
    @Bind(R.id.debug_device_release)    TextView deviceReleaseView;
    @Bind(R.id.debug_device_api)        TextView deviceApiView;

    @Bind(R.id.debug_picasso_indicators)        Switch   picassoIndicatorView;
    @Bind(R.id.debug_picasso_cache_size)        TextView picassoCacheSizeView;
    @Bind(R.id.debug_picasso_cache_hit)         TextView picassoCacheHitView;
    @Bind(R.id.debug_picasso_cache_miss)        TextView picassoCacheMissView;
    @Bind(R.id.debug_picasso_decoded)           TextView picassoDecodedView;
    @Bind(R.id.debug_picasso_decoded_total)     TextView picassoDecodedTotalView;
    @Bind(R.id.debug_picasso_decoded_avg)       TextView picassoDecodedAvgView;
    @Bind(R.id.debug_picasso_transformed)       TextView picassoTransformedView;
    @Bind(R.id.debug_picasso_transformed_total) TextView picassoTransformedTotalView;
    @Bind(R.id.debug_picasso_transformed_avg)   TextView picassoTransformedAvgView;

    @Bind(R.id.debug_okhttp_cache_max_size)      TextView okHttpCacheMaxSizeView;
    @Bind(R.id.debug_okhttp_cache_write_error)   TextView okHttpCacheWriteErrorView;
    @Bind(R.id.debug_okhttp_cache_request_count) TextView okHttpCacheRequestCountView;
    @Bind(R.id.debug_okhttp_cache_network_count) TextView okHttpCacheNetworkCountView;
    @Bind(R.id.debug_okhttp_cache_hit_count)     TextView okHttpCacheHitCountView;

    @Inject                          OkHttpClient                  client;
    @Inject @Named("Api")            OkHttpClient                  apiClient;
    @Inject                          Picasso                       picasso;
    @Inject                          LumberYard                    lumberYard;
    @Inject @IsMockMode              boolean                       isMockMode;
    @Inject @ApiEndpoint             Preference<String>            networkEndpoint;
    @Inject                          Preference<InetSocketAddress> networkProxyAddress;
    @Inject @CaptureIntents          Preference<Boolean>           captureIntents;
    @Inject @AnimationSpeed          Preference<Integer>           animationSpeed;
    @Inject @PicassoDebugging        Preference<Boolean>           picassoDebugging;
    @Inject @PixelGridEnabled        Preference<Boolean>           pixelGridEnabled;
    @Inject @PixelRatioEnabled       Preference<Boolean>           pixelRatioEnabled;
    @Inject @ScalpelEnabled          Preference<Boolean>           scalpelEnabled;
    @Inject @ScalpelWireframeEnabled Preference<Boolean>           scalpelWireframeEnabled;
    @Inject                         NetworkBehavior     behavior;
    @Inject @NetworkDelay           Preference<Long>    networkDelay;
    @Inject @NetworkFailurePercent  Preference<Integer> networkFailurePercent;
    @Inject @NetworkVariancePercent Preference<Integer> networkVariancePercent;
    @Inject                         MockArticleAPI      mockArticleAPI;
    @Inject                         Application         app;
    @Inject                         MasterDao           _masterDao;
//  @Inject                          Set<ContextualDebugActions.DebugAction> debugActions;

//  private final ContextualDebugActions contextualDebugActions;

    public DebugView(Context context) {
        this(context, null);
    }

    public DebugView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Injector.obtain(context).inject(this);

        // Inflate all of the controls and inject them.
        LayoutInflater.from(context).inflate(R.layout.debug_view_content, this);
        ButterKnife.bind(this);

//    contextualDebugActions = new ContextualDebugActions(this, debugActions);

        setupNetworkSection();
        setupMockBehaviorSection();
        setupUserInterfaceSection();
        setupBuildSection();
        setupDeviceSection();
        setupPicassoSection();
        setupOkHttpCacheSection();
    }

//  public ContextualDebugActions getContextualDebugActions() {
//    return contextualDebugActions;
//  }

    public void onDrawerOpened() {
        refreshPicassoStats();
        refreshOkHttpCacheStats();
    }

    private void setupNetworkSection() {
        final ApiEndpoints currentEndpoint = ApiEndpoints.from(networkEndpoint.get());
        final EnumAdapter<ApiEndpoints> endpointAdapter =
                new EnumAdapter<>(getContext(), ApiEndpoints.class);
        endpointView.setAdapter(endpointAdapter);
        endpointView.setSelection(currentEndpoint.ordinal());

        RxAdapterView.itemSelections(endpointView)
                .map(endpointAdapter::getItem)
                .filter(item -> item != currentEndpoint)
                .subscribe(selected -> {
                    if (selected == ApiEndpoints.CUSTOM) {
                        Timber.d("Custom network endpoint selected. Prompting for URL.");
                        showCustomEndpointDialog(currentEndpoint.ordinal(), "http://");
                    } else {
                        setEndpointAndRelaunch(selected.url);
                    }
                });

        final NetworkDelayAdapter delayAdapter = new NetworkDelayAdapter(getContext());
        networkDelayView.setAdapter(delayAdapter);
        networkDelayView.setSelection(
                NetworkDelayAdapter.getPositionForValue(behavior.delay(MILLISECONDS)));

        RxAdapterView.itemSelections(networkDelayView)
                .map(delayAdapter::getItem)
                .filter(item -> item != behavior.delay(MILLISECONDS))
                .subscribe(selected -> {
                    Timber.d("Setting network delay to %sms", selected);
                    behavior.setDelay(selected, MILLISECONDS);
                    networkDelay.set(selected);
                });

        final NetworkVarianceAdapter varianceAdapter = new NetworkVarianceAdapter(getContext());
        networkVarianceView.setAdapter(varianceAdapter);
        networkVarianceView.setSelection(
                NetworkVarianceAdapter.getPositionForValue(behavior.variancePercent()));

        RxAdapterView.itemSelections(networkVarianceView)
                .map(varianceAdapter::getItem)
                .filter(item -> item != behavior.variancePercent())
                .subscribe(selected -> {
                    Timber.d("Setting network variance to %s%%", selected);
                    behavior.setVariancePercent(selected);
                    networkVariancePercent.set(selected);
                });

        final NetworkErrorAdapter errorAdapter = new NetworkErrorAdapter(getContext());
        networkErrorView.setAdapter(errorAdapter);
        networkErrorView.setSelection(
                NetworkErrorAdapter.getPositionForValue(behavior.failurePercent()));

        RxAdapterView.itemSelections(networkErrorView)
                .map(errorAdapter::getItem)
                .filter(item -> item != behavior.failurePercent())
                .subscribe(selected -> {
                    Timber.d("Setting network error to %s%%", selected);
                    behavior.setFailurePercent(selected);
                    networkFailurePercent.set(selected);
                });

        int                currentProxyPosition = networkProxyAddress.isSet() ? ProxyAdapter.PROXY : ProxyAdapter.NONE;
        final ProxyAdapter proxyAdapter         = new ProxyAdapter(getContext(), networkProxyAddress);
        networkProxyView.setAdapter(proxyAdapter);
        networkProxyView.setSelection(currentProxyPosition);

        RxAdapterView.itemSelections(networkProxyView)
                .filter(position -> !networkProxyAddress.isSet() || position != ProxyAdapter.PROXY)
                .subscribe(position -> {
                    if (position == ProxyAdapter.NONE) {
                        Timber.d("Clearing network proxy");
                        // TODO: Keep the custom proxy around so you can easily switch back and forth.
                        networkProxyAddress.delete();
                        client.setProxy(null);
                        apiClient.setProxy(null);
                    } else if (networkProxyAddress.isSet() && position == ProxyAdapter.PROXY) {
                        Timber.d("Ignoring re-selection of network proxy %s", networkProxyAddress.get());
                    } else {
                        Timber.d("New network proxy selected. Prompting for host.");
                        showNewNetworkProxyDialog(proxyAdapter);
                    }
                });

        // Only show the endpoint editor when a custom endpoint is in use.
        endpointEditView.setVisibility(currentEndpoint == ApiEndpoints.CUSTOM ? VISIBLE : GONE);

        if (currentEndpoint == ApiEndpoints.MOCK_MODE) {
            // Disable network proxy if we are in mock mode.
            networkProxyView.setEnabled(false);
            networkLoggingView.setEnabled(false);
            networkDelayView.setEnabled(true);
            networkVarianceView.setEnabled(true);
            networkErrorView.setEnabled(true);
        } else {
            // Disable network controls if we are not in mock mode.
            networkDelayView.setEnabled(false);
            networkVarianceView.setEnabled(false);
            networkErrorView.setEnabled(false);
        }

        // We use the JSON rest adapter as the source of truth for the log level.
        //final EnumAdapter<RestAdapter.LogLevel> loggingAdapter =
        //    new EnumAdapter<>(getContext(), RestAdapter.LogLevel.class);
        //networkLoggingView.setAdapter(loggingAdapter);
        //networkLoggingView.setSelection(retrofit.getLogLevel().ordinal());
        //networkLoggingView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //  @Override
        //  public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        //    RestAdapter.LogLevel selected = loggingAdapter.getItem(position);
        //    if (selected != retrofit.getLogLevel()) {
        //      Timber.d("Setting logging level to %s", selected);
        //      retrofit.setLogLevel(selected);
        //    } else {
        //      Timber.d("Ignoring re-selection of logging level " + selected);
        //    }
        //  }
        //
        //  @Override public void onNothingSelected(AdapterView<?> adapterView) {
        //  }
        //});
    }

    @OnClick(R.id.debug_network_endpoint_edit)
    void onEditEndpointClicked() {
        Timber.d("Prompting to edit custom endpoint URL.");
        // Pass in the currently selected position since we are merely editing.
        showCustomEndpointDialog(endpointView.getSelectedItemPosition(), networkEndpoint.get());
    }

    private void setupMockBehaviorSection() {
        captureIntentsView.setEnabled(isMockMode);
        captureIntentsView.setChecked(captureIntents.get());
        captureIntentsView.setOnCheckedChangeListener((compoundButton, b) -> {
            Timber.d("Capture intents set to %s", b);
            captureIntents.set(b);
        });

        configureResponseSpinner(repositoriesResponseView, MockArticlesServiceResponse.class);
    }

    /**
     * Populates a {@code Spinner} with the values of an {@code enum} and binds it to the value set
     * in
     * the mock service.
     */
    private <T extends Enum<T>> void configureResponseSpinner(Spinner spinner,
                                                              final Class<T> responseClass) {
        final EnumAdapter<T> adapter = new EnumAdapter<>(getContext(), responseClass);
        spinner.setEnabled(isMockMode);
        spinner.setAdapter(adapter);
        spinner.setSelection(mockArticleAPI.getResponse(responseClass).ordinal());

        RxAdapterView.itemSelections(spinner)
                .map(adapter::getItem)
                .filter(item -> item != mockArticleAPI.getResponse(responseClass))
                .subscribe(selected -> {
                    Timber.d("Setting %s to %s", responseClass.getSimpleName(), selected);
                    mockArticleAPI.setResponse(responseClass, selected);
                });
    }

    private void setupUserInterfaceSection() {
        final AnimationSpeedAdapter speedAdapter = new AnimationSpeedAdapter(getContext());
        uiAnimationSpeedView.setAdapter(speedAdapter);
        final int animationSpeedValue = animationSpeed.get();
        uiAnimationSpeedView.setSelection(
                AnimationSpeedAdapter.getPositionForValue(animationSpeedValue));

        RxAdapterView.itemSelections(uiAnimationSpeedView)
                .map(speedAdapter::getItem)
                .filter(item -> item != animationSpeed.get())
                .subscribe(selected -> {
                    Timber.d("Setting animation speed to %sx", selected);
                    animationSpeed.set(selected);
                    applyAnimationSpeed(selected);
                });
        // Ensure the animation speed value is always applied across app restarts.
        post(() -> applyAnimationSpeed(animationSpeedValue));

        boolean gridEnabled = pixelGridEnabled.get();
        uiPixelGridView.setChecked(gridEnabled);
        uiPixelRatioView.setEnabled(gridEnabled);
        uiPixelGridView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Timber.d("Setting pixel grid overlay enabled to " + isChecked);
            pixelGridEnabled.set(isChecked);
            uiPixelRatioView.setEnabled(isChecked);
        });

        uiPixelRatioView.setChecked(pixelRatioEnabled.get());
        uiPixelRatioView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Timber.d("Setting pixel scale overlay enabled to " + isChecked);
            pixelRatioEnabled.set(isChecked);
        });

        uiScalpelView.setChecked(scalpelEnabled.get());
        uiScalpelWireframeView.setEnabled(scalpelEnabled.get());
        uiScalpelView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Timber.d("Setting scalpel interaction enabled to " + isChecked);
            scalpelEnabled.set(isChecked);
            uiScalpelWireframeView.setEnabled(isChecked);
        });

        uiScalpelWireframeView.setChecked(scalpelWireframeEnabled.get());
        uiScalpelWireframeView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Timber.d("Setting scalpel wireframe enabled to " + isChecked);
            scalpelWireframeEnabled.set(isChecked);
        });
    }

    @OnClick(R.id.debug_delete_database)
    void clearDatabase() {
        snackbar(this, "Clearing Database");
        _masterDao.deleteAll();
    }

    @OnClick(R.id.debug_logs_show)
    void showLogs() {
        new LogsDialog(new ContextThemeWrapper(getContext(), R.style.Theme_U2020), lumberYard).show();
    }

    private void setupBuildSection() {
        buildNameView.setText(BuildConfig.VERSION_NAME);
        buildCodeView.setText(String.valueOf(BuildConfig.VERSION_CODE));
        buildShaView.setText(BuildConfig.GIT_SHA);

        if (BuildConfig.BUILD_TIME.equalsIgnoreCase("debug")) {
            buildDateView.setText(BuildConfig.BUILD_TIME); // Don't break incremental builds
        } else {
            TemporalAccessor buildTime = ISO_INSTANT.parse(BuildConfig.BUILD_TIME);
            buildDateView.setText(DATE_DISPLAY_FORMAT.format(buildTime));
        }
    }

    private void setupDeviceSection() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        String         densityBucket  = getDensityString(displayMetrics);
        deviceMakeView.setText(StringUtils.truncateAt(Build.MANUFACTURER, 20));
        deviceModelView.setText(StringUtils.truncateAt(Build.MODEL, 20));
        deviceResolutionView.setText(displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
        deviceDensityView.setText(displayMetrics.densityDpi + "dpi (" + densityBucket + ")");
        deviceReleaseView.setText(Build.VERSION.RELEASE);
        deviceApiView.setText(String.valueOf(Build.VERSION.SDK_INT));
    }

    private void setupPicassoSection() {
        boolean picassoDebuggingValue = picassoDebugging.get();
        picasso.setIndicatorsEnabled(picassoDebuggingValue);
        picassoIndicatorView.setChecked(picassoDebuggingValue);
        picassoIndicatorView.setOnCheckedChangeListener((button, isChecked) -> {
            Timber.d("Setting Picasso debugging to " + isChecked);
            picasso.setIndicatorsEnabled(isChecked);
            picassoDebugging.set(isChecked);
        });

        refreshPicassoStats();
    }

    private void refreshPicassoStats() {
        StatsSnapshot snapshot   = picasso.getSnapshot();
        String        size       = getSizeString(snapshot.size);
        String        total      = getSizeString(snapshot.maxSize);
        int           percentage = (int) ((1f * snapshot.size / snapshot.maxSize) * 100);
        picassoCacheSizeView.setText(size + " / " + total + " (" + percentage + "%)");
        picassoCacheHitView.setText(String.valueOf(snapshot.cacheHits));
        picassoCacheMissView.setText(String.valueOf(snapshot.cacheMisses));
        picassoDecodedView.setText(String.valueOf(snapshot.originalBitmapCount));
        picassoDecodedTotalView.setText(getSizeString(snapshot.totalOriginalBitmapSize));
        picassoDecodedAvgView.setText(getSizeString(snapshot.averageOriginalBitmapSize));
        picassoTransformedView.setText(String.valueOf(snapshot.transformedBitmapCount));
        picassoTransformedTotalView.setText(getSizeString(snapshot.totalTransformedBitmapSize));
        picassoTransformedAvgView.setText(getSizeString(snapshot.averageTransformedBitmapSize));
    }

    private void setupOkHttpCacheSection() {
        Cache cache = client.getCache(); // Shares the cache with apiClient, so no need to check both.
        okHttpCacheMaxSizeView.setText(getSizeString(cache.getMaxSize()));

        refreshOkHttpCacheStats();
    }

    private void refreshOkHttpCacheStats() {
        Cache cache      = client.getCache(); // Shares the cache with apiClient, so no need to check both.
        int   writeTotal = cache.getWriteSuccessCount() + cache.getWriteAbortCount();
        int   percentage = (int) ((1f * cache.getWriteAbortCount() / writeTotal) * 100);
        okHttpCacheWriteErrorView.setText(
                cache.getWriteAbortCount() + " / " + writeTotal + " (" + percentage + "%)");
        okHttpCacheRequestCountView.setText(String.valueOf(cache.getRequestCount()));
        okHttpCacheNetworkCountView.setText(String.valueOf(cache.getNetworkCount()));
        okHttpCacheHitCountView.setText(String.valueOf(cache.getHitCount()));
    }

    private void applyAnimationSpeed(int multiplier) {
        try {
            Method method = ValueAnimator.class.getDeclaredMethod("setDurationScale", float.class);
            method.invoke(null, (float) multiplier);
        } catch (Exception e) {
            throw new RuntimeException("Unable to apply animation speed.", e);
        }
    }

    private static String getDensityString(DisplayMetrics displayMetrics) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return String.valueOf(displayMetrics.densityDpi);
        }
    }

    private static String getSizeString(long bytes) {
        String[] units = new String[]{"B", "KB", "MB", "GB"};
        int      unit  = 0;
        while (bytes >= 1024) {
            bytes /= 1024;
            unit += 1;
        }
        return bytes + units[unit];
    }

    private void showNewNetworkProxyDialog(final ProxyAdapter proxyAdapter) {
        final int originalSelection = networkProxyAddress.isSet() ? ProxyAdapter.PROXY : ProxyAdapter.NONE;

        View           view     = LayoutInflater.from(app).inflate(R.layout.debug_drawer_network_proxy, null);
        final EditText hostView = findById(view, R.id.debug_drawer_network_proxy_host);

        if (networkProxyAddress.isSet()) {
            String host = networkProxyAddress.get().getHostName();
            hostView.setText(host); // Set the current host.
            hostView.setSelection(0, host.length()); // Pre-select it for editing.

            // Show the keyboard. Post this to the next frame when the dialog has been attached.
            hostView.post(() -> Keyboards.showKeyboard(hostView));
        }

        new AlertDialog.Builder(getContext()) //
                .setTitle("Set Network Proxy")
                .setView(view)
                .setNegativeButton("Cancel", (dialog, i) -> {
                    networkProxyView.setSelection(originalSelection);
                    dialog.cancel();
                })
                .setPositiveButton("Use", (dialog, i) -> {
                    String in = hostView.getText().toString();
                    InetSocketAddress address = InetSocketAddressPreferenceAdapter.parse(in);
                    if (address != null) {
                        networkProxyAddress.set(address); // Persist across restarts.
                        proxyAdapter.notifyDataSetChanged(); // Tell the spinner to update.
                        networkProxyView.setSelection(ProxyAdapter.PROXY); // And show the proxy.

                        Proxy proxy = InetSocketAddressPreferenceAdapter.createProxy(address);
                        client.setProxy(proxy);
                        apiClient.setProxy(proxy);
                    } else {
                        networkProxyView.setSelection(originalSelection);
                    }
                })
                .setOnCancelListener(dialogInterface -> networkProxyView.setSelection(originalSelection))
                .show();
    }

    private void showCustomEndpointDialog(final int originalSelection, String defaultUrl) {
        View           view = LayoutInflater.from(app).inflate(R.layout.debug_drawer_network_endpoint, null);
        final EditText url  = findById(view, R.id.debug_drawer_network_endpoint_url);
        url.setText(defaultUrl);
        url.setSelection(url.length());

        new AlertDialog.Builder(getContext()) //
                .setTitle("Set Network Endpoint")
                .setView(view)
                .setNegativeButton("Cancel", (dialog, i) -> {
                    endpointView.setSelection(originalSelection);
                    dialog.cancel();
                })
                .setPositiveButton("Use", (dialog, i) -> {
                    String theUrl = url.getText().toString();
                    if (!StringUtils.isBlank(theUrl)) {
                        setEndpointAndRelaunch(theUrl);
                    } else {
                        endpointView.setSelection(originalSelection);
                    }
                })
                .setOnCancelListener((dialogInterface) -> {
                    endpointView.setSelection(originalSelection);
                })
                .show();
    }

    private void setEndpointAndRelaunch(String endpoint) {
        Timber.d("Setting network endpoint to %s", endpoint);
        networkEndpoint.set(endpoint);

        clearDatabase();

        ProcessPhoenix.triggerRebirth(getContext());
    }
}

