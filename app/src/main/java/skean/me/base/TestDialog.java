package skean.me.base;

import android.content.Context;

import skean.me.base.component.FullDialog;

/**
 * Created by Skean on 19/7/3.
 */
public class TestDialog extends FullDialog {

    public TestDialog(Context context) {
        super(context);
    }

    public TestDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TestDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

}
