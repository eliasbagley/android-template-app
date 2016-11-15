package com.rocketmade.templateapp.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rocketmade.templateapp.MyApp;
import com.rocketmade.templateapp.Session;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import icepick.Icepick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by alexdoub on 7/02/15.
 */
public abstract class BaseFragment extends Fragment {

    @Inject Session _session;
    @Inject Bus     _eventBus;

    protected CompositeSubscription compositeSubscription = new CompositeSubscription(); // For auto unsubscribing all subscriptions added to it on destroy

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaknceState) {

        //Inflate the view
        View view = inflateView(inflater, container);

        //Do pre-initialization
        MyApp.injectFragment(this, view); //Injects butterknife & dagger object graph
        _eventBus.register(this);

        //Tell fragment to Initialize
        initialize(view.getContext());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (_eventBus != null) {
            _eventBus.unregister(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeSubscription.unsubscribe();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    // These methods must be overridden by the subclass
    protected abstract View inflateView(LayoutInflater inflater, ViewGroup container);

    protected abstract void initialize(Context context);

    //region helper methods

    protected void recreateCompositeSubscription() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = new CompositeSubscription();
        }
    }

    // Helper to set the title
    protected void setTitle(String title) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof  AppCompatActivity) {
                ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setTitle(title);
                }
            }
        }
    }


    //endregion
}
