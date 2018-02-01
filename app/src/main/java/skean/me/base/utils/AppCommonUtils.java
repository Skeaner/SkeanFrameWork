package skean.me.base.utils;

import android.content.SharedPreferences;
import android.os.Build;
import android.widget.TextView;

import org.androidannotations.api.sharedpreferences.EditorHelper;

import java.lang.reflect.Field;

/**
 * App的通用工具合集
 */
public class AppCommonUtils {

    public static boolean commitEditorHelper(EditorHelper helper) {
        try {
            Field editorField = EditorHelper.class.getDeclaredField("editor");
            editorField.setAccessible(true);
            SharedPreferences.Editor editor = (SharedPreferences.Editor) editorField.get(helper);
            editor.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 给textView设置文字Drawable
     *
     * @param textView View
     * @param left     左文字
     * @param top      上文字
     * @param right    右文字
     * @param bottom   下文字
     */
    public static void setTextDrawable(TextView textView,
                                       TextDrawableBuilder left,
                                       TextDrawableBuilder top,
                                       TextDrawableBuilder right,
                                       TextDrawableBuilder bottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(left == null ? null : left.build(),
                                                                     top == null ? null : top.build(),
                                                                     right == null ? null : right.build(),
                                                                     bottom == null ? null : bottom.build());
        } else textView.setCompoundDrawablesWithIntrinsicBounds(left == null ? null : left.build(),
                                                                top == null ? null : top.build(),
                                                                right == null ? null : right.build(),
                                                                bottom == null ? null : bottom.build());
    }

}
