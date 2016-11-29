package com.anvata.gankio.module;

import android.app.Fragment;

import rx.Subscription;

/**
 * Fragment基类
 */

public abstract class BaseFragment extends Fragment {

    protected Subscription subscription;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unSubscribe();
    }

    protected void unSubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

}
