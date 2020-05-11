package base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import skean.yzsm.com.framework.R;

public class AdvanceImageView extends AppCompatImageView {

    public static final int MATRIX = 0;
    public static final int FIT_XY = 1;
    public static final int FIT_START = 2;
    public static final int FIT_CENTER = 3;
    public static final int FIT_END = 4;
    public static final int CENTER = 5;
    public static final int CENTER_CROP = 6;
    public static final int CENTER_INSIDE = 7;
    public static final int LEFT_CROP = 8;
    public static final int TOP_CROP = 9;
    public static final int RIGHT_CROP = 10;
    public static final int BOTTOM_CROP = 11;
    private int scaleTypeIndex = -1;

    public AdvanceImageView(Context context) {
        super(context);
    }

    public AdvanceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AdvanceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context c, AttributeSet attr) {
        final TypedArray a = c.obtainStyledAttributes(attr, R.styleable.AdvanceImageView, 0, 0);
        int index = a.getInt(R.styleable.AdvanceImageView_scale_type, -1);
        setScaleType(index);
        a.recycle();
    }

    public void setScaleType(int scaleType) {
        scaleTypeIndex = scaleType;
        if (scaleType >= 0 && scaleType < 8) {
            setScaleType(ScaleType.values()[scaleType]);
        } else {
            setScaleType(ScaleType.MATRIX);
        }
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean result = super.setFrame(l, t, r, b);
        if (scaleTypeIndex > 7 && getDrawable() != null) {
            Matrix matrix = getImageMatrix();
            float scale;
            int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            int drawableWidth = 0;
            int drawableHeight = 0;
            float deltaX = 0;
            float deltaY = 0;
            int moveX = 0;
            int moveY = 0;
            boolean fitX = false;
            boolean fitY = false;
            if (getDrawable() != null) {
                drawableWidth = getDrawable().getIntrinsicWidth();
                drawableHeight = getDrawable().getIntrinsicHeight();
            }
            if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
                scale = (float) viewHeight / (float) drawableHeight;
                fitY = true;
            } else {
                scale = (float) viewWidth / (float) drawableWidth;
                fitX = true;
            }
            deltaX = drawableWidth * scale - viewWidth;
            deltaY = drawableHeight * scale - viewHeight;
            switch (scaleTypeIndex) {
                case LEFT_CROP:
                    if (fitX) moveY = -Math.round(deltaY / 2);
                    break;
                case TOP_CROP:
                    if (fitY) moveX = -Math.round(deltaX / 2);
                    break;
                case RIGHT_CROP:
                    if (fitX) moveY = -Math.round(deltaY / 2);
                    if (fitY) moveX = -Math.round(deltaX);
                    break;
                case BOTTOM_CROP:
                    if (fitX) moveY = -Math.round(deltaY);
                    if (fitY) moveX = -Math.round(deltaX / 2);
                    break;
            }
            matrix.setScale(scale, scale);
            matrix.postTranslate(moveX, moveY);
            setImageMatrix(matrix);
        }
        return result;
    }
}
