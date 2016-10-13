package skean.me.base.utils;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.URLUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.api.sharedpreferences.EditorHelper;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import skean.me.base.net.DownloadHelper;

/**
 * App的通用工具合集
 */
public class AppCommonUtils {

    public static final int TIME_OUT = 10 * 1000;

    public static Gson gsonSerializer() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd HH:mm").setPrettyPrinting().create();
    }

    public static Gson gsonDeserializer() {
        return new GsonBuilder().enableComplexMapKeySerialization().create();
    }

    public static OkHttpClient.Builder newAppHttpBuilder() {
        return new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .writeTimeout(TIME_OUT, TimeUnit.SECONDS);
    }

    public static Retrofit baseRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpBuilder().build())
                                     .addConverterFactory(GsonConverterFactory.create())
                                     .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                     .build();
    }

    public static Retrofit progressRetrofit(String baseUrl, @NonNull DownloadHelper helper) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(helper.getOkHttpClient(newAppHttpBuilder()))
                                     .addConverterFactory(GsonConverterFactory.create())
                                     .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                     .build();
    }

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
