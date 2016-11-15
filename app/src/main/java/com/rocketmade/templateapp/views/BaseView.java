package com.rocketmade.templateapp.views;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import icepick.Icepick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by eliasbagley on 7/15/15.
 */
public abstract class BaseView extends RelativeLayout {
    protected CompositeSubscription compositeSubscription = new CompositeSubscription(); // For auto unsubscribing all subscriptions added to it on destroy

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        compositeSubscription.unsubscribe();
    }

    public BaseView(Context context) {
        super(context);
        initialize(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    //region helper methods

    protected void recreateCompositeSubscription() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = new CompositeSubscription();
        }
    }

    //endregion

    protected void initialize(Context context) {
        inflate(context);
        ButterKnife.bind(this);
    }

    protected abstract void inflate(Context context);
}

