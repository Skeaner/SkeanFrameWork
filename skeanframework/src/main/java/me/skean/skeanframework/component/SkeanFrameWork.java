package me.skean.skeanframework.component;

import android.content.Context;

/**
 * Created by Skean on 20/7/9.
 */
public class SkeanFrameWork {
    private static Context context;

    public static void init(Context c) {
        context = c;
    }

    public static Context getContext() {
        return context;
    }
}
