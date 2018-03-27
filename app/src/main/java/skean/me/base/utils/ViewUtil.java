package skean.me.base.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import skean.yzsm.com.framework.R;

/**
 * 一个方便设置LayoutParams参数的工具类
 */
public class ViewUtil {

    private static final int MAX_INTERVAL_FOR_CLICK = 250;
    private static final int MAX_DISTANCE_FOR_CLICK = 100;


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


    public static boolean detectClickEvent(View v, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            v.setTag(R.id.keyDownX, ev.getX());
            v.setTag(R.id.keyDownY, ev.getY());
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            int dx = (int) Math.abs((float) v.getTag(R.id.keyDownX) - ev.getX());
            int dy = (int) Math.abs((float) v.getTag(R.id.keyDownY) - ev.getY());
            long dm = ev.getEventTime() - ev.getDownTime();
            return dx < MAX_DISTANCE_FOR_CLICK && dy < MAX_DISTANCE_FOR_CLICK && dm < MAX_INTERVAL_FOR_CLICK;
        }
        return false;
    }

    public static void setEditTextErrorAndFocus(TextView et, String errMessage) {
        et.setError(errMessage);
        et.requestFocus();
    }

}
