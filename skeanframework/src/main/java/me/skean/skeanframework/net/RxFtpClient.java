package me.skean.skeanframework.net;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * 异步FTP客户端
 */
public class RxFtpClient {

    public static final int DEFAULT_TIME_OUT = 5 * 1000;

    public static final String TAG = "RxFtpClient";

    /**
     * 开始ftp服务
     */
    private static void start(FTPClient ftpClient,
                              String host,
                              int port,
                              String user,
                              String password,
                              String dir,
                              boolean autoCreateDir) throws IOException {
        // 打开FTP服务
        try {
            connect(ftpClient, host, port, user, password);
        }
        catch (IOException e) {
            throw new IOException("连接FTP服务器失败");
        }
        try {
            // 设置模式
            boolean result = ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
            if (!result) throw new IOException("FTP服务器不支持传输文件");
            // FTP下创建文件夹
            String[] dirs = dir.split("/");
            if (dirs.length > 1) {
                dirs = Arrays.copyOfRange(dirs, 1, dirs.length);
                for (String d : dirs) {
                    if (autoCreateDir) ftpClient.makeDirectory(d);
                    // 改变FTP目录
                    result = ftpClient.changeWorkingDirectory(d);
                    if (!result) throw new IOException("无权限读取FTP目录/该目录不存在");
                }
            }
        }
        catch (IOException e) {
            disconnect(ftpClient);
            throw e;
        }
    }

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    private static void connect(FTPClient ftpClient, String host, int port, String user, String password) throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(host, port);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(user, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        }
    }

    /**
     * 关闭FTP服务.
     */
    private static void disconnect(FTPClient ftpClient) throws IOException {
        // 退出FTP
        ftpClient.logout();
        // 断开连接
        ftpClient.disconnect();
    }

    public static Observable<Integer> upload(String hostName,
                                             int port,
                                             String user,
                                             String password,
                                             String remotePath,
                                             File localFile) {
        return upload(hostName, port, user, password, remotePath, DEFAULT_TIME_OUT, localFile);
    }

    public static Observable<Integer> upload(String host,
                                             int port,
                                             String user,
                                             String password,
                                             String dir,
                                             int timeout,
                                             File localFile) {
        return Observable.create(emitter -> {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setDefaultTimeout(timeout);
            ftpClient.setConnectTimeout(timeout);
            if (dir == null || !dir.startsWith("/")) {
                throw new RuntimeException("文件夹请以/开头");
            }
            start(ftpClient, host, port, user, password, dir, true);
            long fileSize = localFile.length();
            ftpClient.setCopyStreamListener(new CopyStreamListener() {
                int progress = 0;

                @Override
                public void bytesTransferred(CopyStreamEvent event) {
                }

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                    int tmp = (int) ((100 * totalBytesTransferred) / fileSize);
                    if (tmp != progress) {
                        progress = tmp;
                        emitter.onNext(progress);
                    }
                }
            });
            uploadFileInternal(ftpClient, localFile);
            disconnect(ftpClient);
            emitter.onComplete();
        });
    }

    /**
     * 内部上传文件的操作
     */
    private static void uploadFileInternal(FTPClient ftpClient, File localFile) throws IOException {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFile));
            // 上传单个文件
            boolean success = ftpClient.storeFile(localFile.getName(), bis);
            // 关闭文件流
            bis.close();
            if (!success) throw new IOException("上传文件失败!");
        }
        catch (IOException e) {
            disconnect(ftpClient);
            String message = "上传文件失败!";
            if (e instanceof FileNotFoundException) message = "上传文件不存在!";
            throw new IOException(message);
        }
    }

    public static Observable<Integer> download(String hostName, int port, String user, String password, String path, File localFile) {
        return download(hostName, port, user, password, path, DEFAULT_TIME_OUT, localFile);
    }

    public static Observable<Integer> download(String host,
                                               int port,
                                               String user,
                                               String password,
                                               String path,
                                               int timeout,
                                               File localFile) {
        return Observable.create(emitter -> {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setDefaultTimeout(timeout);
            ftpClient.setConnectTimeout(timeout);
            String dir = null;
            String fileName = null;
            if (path == null) {
                throw new RuntimeException("文件路径不能为为空");
            }
            else {
                if (path.startsWith("/")) {
                    int lastSep = path.lastIndexOf("/");
                    if (lastSep == 0) {
                        dir = "/";
                    }
                    else {
                        dir = path.substring(0, lastSep);
                    }
                    fileName = path.substring(lastSep + 1);
                }
                else {
                    dir = "/";
                    fileName = path;
                }
            }
            start(ftpClient, host, port, user, password, dir, false);
            FTPFile file = findFtpFile(ftpClient, dir, fileName);
            long fileSize = file.getSize();
            ftpClient.setCopyStreamListener(new AsyncFtpClient.DefaultCopyStreamListener() {
                int progress = 0;

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                    int tmp = (int) ((100 * totalBytesTransferred) / fileSize);
                    if (tmp != progress) {
                        progress = tmp;
                        emitter.onNext(progress);
                    }
                }
            });
            downloadFileInternal(ftpClient, dir, file, localFile);
            disconnect(ftpClient);
            emitter.onComplete();
        });
    }

    /**
     * 找出FTP服务的文件
     */
    private static FTPFile findFtpFile(FTPClient ftpClient, String dir, final String fileName) throws IOException {
        try {
            FTPFile[] files = ftpClient.listFiles(dir, file -> file.getName().equals(fileName));
            if (files == null || files.length == 0) {
                throw new IOException("找不到文件");
            }
            return files[0];
        }
        catch (IOException e) {
            disconnect(ftpClient);
            throw new IOException(e.getLocalizedMessage());
        }
    }

    /**
     * 下载一个文件内部操作
     */
    private static void downloadFileInternal(FTPClient ftpClient, String dir, FTPFile ftpFile, File localFile) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(localFile);
            boolean success = ftpClient.retrieveFile(dir + "/" + ftpFile.getName(), fos);
            fos.close();
            if (!success) throw new IOException("传输文件失败!");
        }
        catch (IOException e) {
            disconnect(ftpClient);
            throw new IOException("传输文件失败!");
        }
    }

    public static Single<Boolean> delete(String hostName, int port, String user, String password, String path) {
        return delete(hostName, port, user, password, path, DEFAULT_TIME_OUT);
    }

    public static Single<Boolean> delete(String host, int port, String user, String password, String path, int timeout) {
        return Single.create(emitter -> {
            FTPClient ftpClient = new FTPClient();
            ftpClient.setDefaultTimeout(timeout);
            ftpClient.setConnectTimeout(timeout);
            String dir = null;
            String fileName = null;
            if (path == null) {
                throw new RuntimeException("文件路径不能为为空");
            }
            else {
                if (path.startsWith("/")) {
                    int lastSep = path.lastIndexOf("/");
                    dir = path.substring(0, lastSep + 1);
                    fileName = path.substring(lastSep + 1);
                }
                else {
                    dir = "/";
                    fileName = path;
                }
            }
            start(ftpClient, host, port, user, password, dir, false);
            findFtpFile(ftpClient, dir, fileName);
            ftpClient.changeWorkingDirectory(dir);
            boolean result = ftpClient.deleteFile(dir + fileName);
            emitter.onSuccess(result);
            disconnect(ftpClient);
        });
    }

}
