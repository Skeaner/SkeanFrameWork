package skean.me.base.utils;

import android.view.ViewGroup;

/**
 * 一个方便设置LayoutParams参数的工具类
 */
public class LayoutParamsUtil {

    public static <T extends ViewGroup.LayoutParams> T setHeight(T layoutParams, int height) {
        layoutParams.height = height;
        return layoutParams;
    }

    public static <T extends ViewGroup.LayoutParams> T setWidth(T layoutParams, int width) {
        layoutParams.width = width;
        return layoutParams;
    }

    public static <T extends ViewGroup.LayoutParams> T setWidthAndHeight(T layoutParams, int width, int height) {
        layoutParams.width = width;
        layoutParams.height = height;
        return layoutParams;
    }

}
