package me.skean.skeanframework.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import me.skean.skeanframework.R;

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


    public static String getCheckedRadioButtonText(RadioGroup group) {
        if (group.getCheckedRadioButtonId() == -1) return "";
        else return ((RadioButton) group.findViewById(group.getCheckedRadioButtonId())).getText().toString();
    }


    public static String getCheckedCheckBoxText(ViewGroup parent) {
        List<String> checkedTexts = new ArrayList<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof CheckBox && ((CheckBox) v).isChecked()) {
                checkedTexts.add(((CheckBox) v).getText().toString());
            }
        }
        if (checkedTexts.size() > 1) {
            return TextUtils.join("，", checkedTexts);
        } else return "";
    }

    public static void setTabLayoutDivider(TabLayout tabLayout, @LinearLayoutCompat.DividerMode int dividerMode, @DrawableRes int resId) {
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(dividerMode);
            Context c = tabLayout.getContext();
            ((LinearLayout) root).setDividerDrawable(ContextCompat.getDrawable(c, resId));
        }
    }

}
