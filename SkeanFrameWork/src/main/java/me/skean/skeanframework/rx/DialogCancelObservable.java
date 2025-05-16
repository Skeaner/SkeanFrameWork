package me.skean.skeanframework.rx;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.View;

import io.reactivex.rxjava3.android.MainThreadDisposable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;

public final class DialogCancelObservable extends Observable<Object> {
    private final Dialog dialog;

    public DialogCancelObservable(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    protected void subscribeActual(Observer<? super Object> observer) {
        CancelListener cancelListener = new CancelListener(dialog, observer);
        observer.onSubscribe(cancelListener);
        dialog.setOnCancelListener(cancelListener);
    }


    static final class CancelListener extends MainThreadDisposable implements DialogInterface.OnCancelListener {
        private final Dialog dialog;
        private final Observer<? super Object> observer;

        CancelListener(Dialog dialog, Observer<? super Object> observer) {
            this.dialog = dialog;
            this.observer = observer;
        }

        @Override
        protected void onDispose() {
            dialog.setOnCancelListener(null);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (!isDisposed()) {
                observer.onNext(new Object());
            }
        }
    }
}
