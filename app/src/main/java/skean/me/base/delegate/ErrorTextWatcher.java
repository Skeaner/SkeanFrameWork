package skean.me.base.delegate;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import skean.me.base.utils.ContentUtil;

/**
 * TextWatcher错误监听
 */

public class ErrorTextWatcher implements TextWatcher {

    WeakReference<TextView> ref;

    public ErrorTextWatcher(TextView view) {
        this.ref = new WeakReference<TextView>(view);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (ref.get().getError() != null) {
            if (!ContentUtil.isEmpty(s)) {
                ref.get().setError(null);
                ref.get().removeTextChangedListener(this);
            }
        }
    }
}
