package skean.me.base.utils;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.URLUtil;


import org.androidannotations.api.sharedpreferences.EditorHelper;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

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

}
