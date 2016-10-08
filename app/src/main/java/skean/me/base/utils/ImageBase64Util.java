package skean.me.base.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageBase64Util {

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

    //base64字符串转化成图片
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

    //base64字符串转化成图片
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

