package skean.me.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具合集
 */
public class ImageUtil {
    /**
     * 图片压缩操作类
     */
    public static class Compressor {

        public static final int PREFER_LONG_SIDE = 1000;
        public static final int PREFER_QUALITY = 50;

        /**
         * 压缩图片异步回调
         */
        public interface BitmapCallBack {
            void onSuccess(Bitmap bitmap);

            void onFail();
        }

        /**
         * 保存压缩图片异步回调
         */
        public interface FileCallBack {
            void onSuccess(File file);

            void onFail();
        }

        /**
         * 以同步方式, 将原图片压缩成适合尺寸的Bitmap
         *
         * @param context 上下文
         * @param rawFile 原图片
         * @return 压缩后图片
         */
        @WorkerThread
        public static Bitmap toPreferSizeBitmap(Context context, File rawFile) {
            return toActualSizeBitmap(context, rawFile, PREFER_LONG_SIDE);
        }

        /**
         * 以异步方式, 将原图片压缩成适合尺寸的Bitmap
         *
         * @param context  上下文
         * @param rawFile  原图片
         * @param callBack 回调
         */
        public static void toPreferSizeBitmap(Context context, File rawFile, BitmapCallBack callBack) {
            toActualSizeBitmap(context, rawFile, PREFER_LONG_SIDE, callBack);
        }

        /**
         * 以同步方式, 将原图片压缩成指定长边尺寸(短边将按比例自动设置)的Bitmap
         *
         * @param context  上下文
         * @param rawFile  原图片
         * @param longSide 长边尺寸
         * @return 压缩后图片
         */
        @WorkerThread
        public static Bitmap toActualSizeBitmap(Context context, File rawFile, int longSide) {
            return toActualSizeBitmap(context, rawFile, longSide, 1);
        }

        /**
         * 以异步方式, 将原图片压缩成指定长边尺寸(短边将按比例自动设置)的Bitmap
         *
         * @param context  上下文
         * @param rawFile  原图片
         * @param longSide 长边尺寸
         * @param callBack 回调
         */
        public static void toActualSizeBitmap(Context context, File rawFile, int longSide, BitmapCallBack callBack) {
            toActualSizeBitmap(context, rawFile, longSide, 1, callBack);
        }

        /**
         * 以同步方式, 将原图片按照比例压缩成设定尺寸的Bitmap
         *
         * @param context   上下文
         * @param rawFile   原图片
         * @param longSide  长边最少尺寸
         * @param shortSide 短边最少尺寸
         * @return 压缩后图片
         */
        @WorkerThread
        public static Bitmap toActualSizeBitmap(Context context, File rawFile, int longSide, int shortSide) {
            try {
                int[] sizes = convertSize(rawFile, longSide, shortSide);
                return Glide.with(context)
                            .asBitmap()
                            .load(rawFile)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .apply(RequestOptions.overrideOf(sizes[0], sizes[1]))
                            .submit()
                            .get();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 以异步方式, 将原图片按照比例压缩成设定尺寸的Bitmap
         *
         * @param context   上下文
         * @param rawFile   原图片
         * @param longSide  长边最短尺寸
         * @param shortSide 短边最短尺寸
         * @param callBack  回调
         */
        public static void toActualSizeBitmap(Context context, File rawFile, int longSide, int shortSide, BitmapCallBack callBack) {
            int[] sizes = convertSize(rawFile, longSide, shortSide);
            Glide.with(context)
                 .asBitmap()
                 .load(rawFile)
                 .skipMemoryCache(true)
                 .diskCacheStrategy(DiskCacheStrategy.NONE)
                 .apply(RequestOptions.overrideOf(sizes[0], sizes[1]))
                 .into(new CustomTarget2(callBack));
        }

        /**
         * 以同步方式, 将原图片压缩成适合尺寸的Bitmap, 保存在目标文件中
         *
         * @param context    上下文
         * @param rawFile    原图片
         * @param targetFile 目标文件
         * @return 压缩后图片
         */
        @WorkerThread
        public static boolean toPreferSizeFile(Context context, File rawFile, File targetFile) {
            return toActualSizeFile(context, rawFile, targetFile, PREFER_QUALITY, PREFER_LONG_SIDE);
        }

        /**
         * 以异步方式, 将原图片压缩成适合尺寸的Bitmap, 保存在目标文件中
         *
         * @param context    上下文
         * @param rawFile    原图片
         * @param targetFile 目标文件
         * @param callBack   回调
         */
        public static void toPreferSizeFile(Context context, File rawFile, File targetFile, FileCallBack callBack) {
            toActualSizeFile(context, rawFile, targetFile, PREFER_QUALITY, PREFER_LONG_SIDE, callBack);
        }

        /**
         * 以同步方式, 将原图片压缩成指定长边尺寸(短边将按比例自动设置)的Bitmap, 保存在目标文件中
         *
         * @param context    上下文
         * @param rawFile    原图片
         * @param targetFile 目标文件
         * @param quality    图片质量,为0-100
         * @param longSide   长边尺寸
         * @return 是否保存成功
         */
        @WorkerThread
        public static boolean toActualSizeFile(Context context, File rawFile, File targetFile, int quality, int longSide) {
            return toActualSizeFile(context, rawFile, targetFile, quality, longSide, 1);
        }

        /**
         * 以异步方式, 将原图片压缩成指定长边尺寸(短边将按比例自动设置)的Bitmap, 保存在目标文件中
         *
         * @param context    上下文
         * @param rawFile    原图片
         * @param targetFile 目标文件
         * @param quality    图片质量,为0-100
         * @param longSide   长边尺寸
         * @param callBack   回调
         */
        public static void toActualSizeFile(Context context,
                                            File rawFile,
                                            File targetFile,
                                            int quality,
                                            int longSide,
                                            FileCallBack callBack) {
            toActualSizeFile(context, rawFile, targetFile, quality, longSide, 1, callBack);
        }

        /**
         * 以同步方式, 将原图片按照比例压缩成设定尺寸的Bitmap, 保存在目标文件中
         *
         * @param context    上下文
         * @param rawFile    原图片
         * @param targetFile 目标文件
         * @param quality    图片质量,为0-100
         * @param longSide   长边最短尺寸
         * @param shortSide  短边最短尺寸
         * @return 是否保存成功
         */
        @WorkerThread
        public static boolean toActualSizeFile(Context context, File rawFile, File targetFile, int quality, int longSide, int shortSide) {
            if (targetFile == null) return false;
            Bitmap bitmap = toActualSizeBitmap(context, rawFile, longSide, shortSide);
            if (bitmap == null) return false;
            try {
                if (targetFile.exists()) targetFile.delete();
                targetFile.createNewFile();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, new FileOutputStream(targetFile));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * 以异步方式, 将原图片按照比例压缩成设定尺寸的Bitmap, 保存在目标文件中
         *
         * @param context    上下文
         * @param rawFile    原图片
         * @param targetFile 目标文件
         * @param quality    图片质量,为0-100
         * @param longSide   长边最短尺寸
         * @param shortSide  短边最短尺寸
         * @param callBack   回调
         */
        public static void toActualSizeFile(Context context,
                                            File rawFile,
                                            File targetFile,
                                            int quality,
                                            int longSide,
                                            int shortSide,
                                            FileCallBack callBack) {
            int[] sizes = convertSize(rawFile, longSide, shortSide);
            Glide.with(context)
                 .asBitmap()
                 .load(rawFile)
                 .skipMemoryCache(true)
                 .diskCacheStrategy(DiskCacheStrategy.NONE)
                 .apply(RequestOptions.overrideOf(sizes[0], sizes[1]))
                 .into(new CustomTarget2(targetFile, quality, callBack));

        }

        /**
         * 根据原图片, 和目标长边, 短边尺寸, 智能转换为目标长宽尺寸
         *
         * @param file      原图片
         * @param longSide  目标长边尺寸
         * @param shortSide 目标短边尺寸
         * @return 长宽尺寸数组
         */
        public static int[] convertSize(File file, int longSide, int shortSide) {
            BitmapFactory.Options options = decodeBitmap(file);
            if (options == null) return new int[2];
            else if (options.outWidth > options.outHeight) {
                return new int[]{longSide, shortSide};
            } else {
                return new int[]{shortSide, longSide};
            }
        }

        /**
         * 根据原图片获取其测量用Bitmap
         *
         * @param file 原图片
         * @return 测量用Bitmap
         */
        private static BitmapFactory.Options decodeBitmap(File file) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            return options;
        }

        private static class CustomTarget2 extends CustomTarget<Bitmap> {
            File targetFile;
            int quality;
            FileCallBack fCallback;
            BitmapCallBack bCallback;

            CustomTarget2(File targetFile, int quality, FileCallBack callback) {
                this.quality = quality;
                this.targetFile = targetFile;
                this.fCallback = callback;
            }

            /**
             * 自定义Picasso的回调操作
             *
             * @param callback 回调
             */
            CustomTarget2(BitmapCallBack callback) {
                this.bCallback = callback;
            }

            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                if (bCallback != null) bCallback.onSuccess(bitmap);
                else if (fCallback != null) {
                    if (targetFile == null) fCallback.onFail();
                    new BitmapCompressTask(targetFile, quality, fCallback, bCallback).execute(bitmap);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (fCallback != null) fCallback.onFail();
                else if (bCallback != null) bCallback.onFail();
            }

            private static class BitmapCompressTask extends AsyncTask<Bitmap, Void, Boolean> {
                File targetFile;
                int quality;
                FileCallBack fCallback;
                BitmapCallBack bCallback;

                BitmapCompressTask(File targetFile, int quality, FileCallBack fCallback, BitmapCallBack bCallback) {
                    this.targetFile = targetFile;
                    this.quality = quality;
                    this.fCallback = fCallback;
                    this.bCallback = bCallback;
                }

                @Override
                protected Boolean doInBackground(Bitmap... bitmaps) {
                    Bitmap b = bitmaps[0];
                    try {
                        if (targetFile.exists()) targetFile.delete();
                        boolean success = targetFile.createNewFile();
                        if (!success) return false;
                        b.compress(Bitmap.CompressFormat.JPEG, quality, new FileOutputStream(targetFile));
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    if (result) fCallback.onSuccess(targetFile);
                    else fCallback.onFail();
                }
            }

        }
    }

    /**
     * Base64操作类
     */
    public static class Base64Util {

        /**
         * 将图片转换为Base64编码
         *
         * @param image 图片的file
         * @return 转换后的字符串
         */
        public static String encodeToString(File image) {
            InputStream is;
            byte[] data;
            //读取图片字节数组
            try {
                is = new FileInputStream(image);
                data = new byte[is.available()];
                is.read(data);
                is.close();
                return Base64.encodeToString(data, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 将图片转换为Base64编码
         *
         * @param bitmap 图片的file
         * @return 转换后的字符串
         */
        public static String encodeToString(Bitmap bitmap) {
            //读取图片字节数组
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] byteArray = bos.toByteArray();
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        /**
         * 图片转换为Base64编码字节数组
         *
         * @param image 图片的file
         * @return 转换后的字节数组
         */
        public static byte[] encodeToBytes(File image) {
            InputStream is;
            byte[] data;
            byte[] result = null;
            //读取图片字节数组
            try {
                is = new FileInputStream(image);
                data = new byte[is.available()];
                is.read(data);
                is.close();
                result = Base64.encode(data, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        /**
         * 将base64字符串转化成图片Bitmap
         *
         * @param base64Str base64编码字符串
         * @return Bitmap数据
         */
        public static Bitmap decodeAsBitmap(String base64Str) {   //对字节数组字符串进行Base64解码并生成图片
            if (base64Str == null) //图像数据为空
                return null;
            try {
                //Base64解码
                byte[] data = Base64.decode(base64Str.getBytes(), Base64.DEFAULT);
                for (int i = 0; i < data.length; ++i) {
                    if (data[i] < 0) {//调整异常数据
                        data[i] += 256;
                    }
                }
                return BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 将base64字符串转化成图片数据字节数组
         *
         * @param base64Str base64编码字符串
         * @return Bitmap数据
         */
        public static byte[] decodeAsBytes(String base64Str) {   //对字节数组字符串进行Base64解码并生成图片
            if (base64Str == null) //图像数据为空
                return null;
            try {
                //Base64解码
                byte[] data = Base64.decode(base64Str.getBytes(), Base64.DEFAULT);
                for (int i = 0; i < data.length; ++i) {
                    if (data[i] < 0) {//调整异常数据
                        data[i] += 256;
                    }
                }
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //base64字符串转化成图片
        public static Bitmap decodeAsBitmap(byte[] rawData) {   //对字节数组字符串进行Base64解码并生成图片
            if (rawData == null) //图像数据为空
                return null;
            try {
                //Base64解码
                byte[] data = Base64.decode(rawData, Base64.DEFAULT);
                for (int i = 0; i < data.length; ++i) {
                    if (data[i] < 0) {//调整异常数据
                        data[i] += 256;
                    }
                }
                return BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}