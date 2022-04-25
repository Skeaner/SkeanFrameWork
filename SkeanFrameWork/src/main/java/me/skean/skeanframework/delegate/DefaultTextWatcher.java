package me.skean.skeanframework.delegate;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * TextWatcher 空实现
 */

public class DefaultTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
