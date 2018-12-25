package com.loslink.myview.utils.rx;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * author: zhoubinjia
 * date: 2017/4/27
 */
public abstract class RxTask<Params, Progress, Result> {

    private Params[] mParams;
    private Result mResult;
    private Disposable disposable;
    private FlowableEmitter<Progress[]> emitter;
    private BackpressureStrategy strategy = BackpressureStrategy.BUFFER;

    private FlowableOnSubscribe<Progress[]> source = new FlowableOnSubscribe<Progress[]>() {
        @Override
        public void subscribe(@NonNull FlowableEmitter<Progress[]> e) throws Exception {
            emitter = e;
            try {
                mResult = doInBackground(mParams);
                e.onComplete();
            } catch (Exception e1) {
                e.onError(e1);
            }
        }
    };

    private Consumer<Progress[]> onNext = new Consumer<Progress[]>() {
        @Override
        public void accept(@NonNull Progress[] progresses) throws Exception {
            onProgressUpdate(progresses);
        }
    };

    private Consumer<Throwable> onError = new Consumer<Throwable>() {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
//            GbLog.e("RxTask onError", throwable);
            onError(throwable);
        }
    };

    private Action onComplete = new Action() {
        @Override
        public void run() throws Exception {
            onPostExecute(mResult);
        }
    };

    @SuppressWarnings("unchecked")
    protected abstract Result doInBackground(Params... params);

    protected void onPreExecute() {
    }

    protected void onPostExecute(Result result) {
    }

    protected void onError(Throwable throwable) {
    }

    @SuppressWarnings("unchecked")
    protected void onProgressUpdate(Progress... values) {
    }

    @SafeVarargs
    protected final void publishProgress(Progress... values) {
        if (emitter != null) {
            emitter.onNext(values);
        }
    }

    @SafeVarargs
    public final RxTask execute(Params... params) {
        return execute(Schedulers.io(), params);
    }

    @SafeVarargs
    public final RxTask execute(Scheduler scheduler, Params... params) {
        if (disposable != null) disposable.dispose();
        mParams = params;
        onPreExecute();
        disposable = Flowable.create(source, strategy)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onComplete);
        return this;
    }

    public void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public boolean isDisposed() {
        return disposable == null || disposable.isDisposed();
    }

    public void setStrategy(BackpressureStrategy strategy) {
        this.strategy = strategy;
    }
}
