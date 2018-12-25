package com.loslink.myview.utils.rx;


import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author: zhoubinjia
 * date: 2017/4/27
 */
public class RxManager {
    static final String TAG = "RxManager";

    private RxManager() {
    }

    public static void execute(Runnable runnable) {
//        GbLog.d("RxManager", "execute", runnable);
        Completable.fromRunnable(runnable)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public static void execute(Runnable runnable, Scheduler scheduler) {
//        GbLog.d("RxManager", "execute", runnable, scheduler);
        Completable.fromRunnable(runnable)
                .subscribeOn(scheduler)
                .subscribe();
    }

    public static <T> Single<T> create(Callable<T> callable) {
//        GbLog.d("RxManager", "create", callable);
        return Single.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Single<T> create(Callable<T> callable, Scheduler scheduler) {
//        GbLog.d("RxManager", "create", callable, scheduler);
        return Single.fromCallable(callable)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Completable create(Runnable runnable) {
//        GbLog.d("RxManager", "create", runnable);
        return Completable.fromRunnable(runnable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
