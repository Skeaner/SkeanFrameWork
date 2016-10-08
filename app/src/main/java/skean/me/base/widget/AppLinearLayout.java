package skean.me.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 增加了一个xFraction和yFraction属性, 用于动画animator的LinearLayout
 */
public class AppLinearLayout extends LinearLayout {
    public AppLinearLayout(Context context) {
        super(context);
    }

    public AppLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float getXFraction() {
        return getX() / getWidth();
    }

    public void setXFraction(float xFraction) {
        final int width = getWidth();
        setX((width > 0) ? (xFraction * width) : -9999);
    }


    public float getYFraction() {
        return getY() / getHeight();
    }

    public void setYFraction(float yFraction) {
        final int height = getHeight();
        setY((height > 0) ? (yFraction * height) : -9999);
    }



}
