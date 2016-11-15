package com.rocketmade.templateapp.controllers.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rocketmade.templateapp.R;
import com.rocketmade.templateapp.data.Injector;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.ObjectGraph;

public class IntroActivity extends BaseActivity {
    private ObjectGraph  activityGraph;

    @Bind(R.id.activity_intro_title_tv) TextView _titleTv;

    @OnClick(R.id.activity_intro_continue_btn)
    public void continueClicked() {
        DrawerActivity.show(getThis());
    }

    @Override
    protected void initialize(Context context) {

        int    stringId = context.getApplicationInfo().labelRes;
        String appName  = context.getString(stringId);

        _titleTv.setText("Loaded App: " + appName);
    }

    @Override
    protected void inflate() {
        // Explicitly reference the application object since we don't want to match our own injector.
        ObjectGraph appGraph = Injector.obtain(getApplication());
        appGraph.inject(this);
        activityGraph = appGraph;

        ViewGroup container = appContainer.bind(this);
        getLayoutInflater().inflate(R.layout.activity_intro, container);
        ButterKnife.bind(this, container);
    }

    // for injecting the debug drawer

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return activityGraph;
        }
        return super.getSystemService(name);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }
}
