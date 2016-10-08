package skean.me.base.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件的便利工具类
 */
public class FileUtil {

    /**
     * 移动文件
     *
     * @param srcFile 源文件
     * @param destDir 目标位置
     * @return 是否移动成功
     */
    public static boolean move(File srcFile, File destDir) {
        // Move file to new directory
        return srcFile.renameTo(new File(destDir, srcFile.getName()));
    }

    /**
     * 移动文件
     *
     * @param srcFile  源文件
     * @param destPath 目标位置
     * @return 是否移动成功
     */
    public static boolean move(File srcFile, String destPath) {
        // Destination directory
        File dir = new File(destPath);
        return move(srcFile, dir);
    }

    /**
     * 移动文件
     *
     * @param srcFile  原文件路径
     * @param destPath 目标路径
     * @return 是否移动成功
     */
    public static boolean move(String srcFile, String destPath) {
        // File (or directory) to be moved
        File file = new File(srcFile);
        // Destination directory
        File dir = new File(destPath);
        // Move file to new directory
        return file.renameTo(new File(dir, file.getName()));
    }

    public static void copy(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream is = new FileInputStream(oldPath);
                FileOutputStream os = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread;
                    os.write(buffer, 0, byteread);
                }
                os.flush();
                os.close();
                is.close();
            }
        } catch (Exception e) {
            System.out.println("error  ");
            e.printStackTrace();
        }
    }

    public static void copy(File oldfile, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldfile.exists()) {
                InputStream is = new FileInputStream(oldfile);
                FileOutputStream os = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread;
                    os.write(buffer, 0, byteread);
                }
                os.flush();
                os.close();
                is.close();
            }
        } catch (Exception e) {
            System.out.println("error  ");
            e.printStackTrace();
        }
    }

    /**
     * 删除文件/文件夹
     *
     * @param file 文件/文件夹
     */
    public static void delete(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    delete(f);
                }
            }
            file.delete();
        }
    }

    /**
     * 删除文件夹里面的全部内容(保留文件夹)
     *
     * @param dir 文件夹
     */
    public static void deleteChilds(File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                delete(f);
            }
        }
    }

    /**
     * 文件初始化操作, 如果是file不存在, 并且是目录的话创建为目录, 如果不是目录侧创建其父目录,并且创建新文件
     *
     * @param file        目标文件
     * @param isDirectory 是否创建为目录
     * @return 初始化后的文件
     */
    public static File initializeFile(File file, boolean isDirectory) {
        if (file == null) return null;
        if (!file.exists()) {
            try {
                if (isDirectory) {
                    file = file.mkdirs() ? file : null;
                } else {
                    file.getParentFile()
                        .mkdirs();
                    file = file.createNewFile() ? file : null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                file = null;
            }
        }
        return file;
    }


    public static String getPathFromUri(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static void storeFile(File file, InputStream is) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            fos.flush();
        }
        fos.close();
        bis.close();
    }

}