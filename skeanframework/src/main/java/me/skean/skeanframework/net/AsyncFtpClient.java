package me.skean.skeanframework.net;

import android.os.AsyncTask;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异步FTP客户端
 */
public class AsyncFtpClient {

    private String hostName; // 主机名
    private int serverPort; // 端口号
    private String userName; // 用户名.
    private String password; //用户密码
    private FTPClient ftpClient;
    private ProgressListener progressListener; //进度监控

    public static final int TIME_OUT = 5 * 1000;

    public static final String TAG = "FtpClientMod";

    public AsyncFtpClient() {
        ftpClient = new FTPClient();
        ftpClient.setDefaultTimeout(TIME_OUT);
        ftpClient.setConnectTimeout(TIME_OUT);
    }

    public AsyncFtpClient(String hostName, int serverPort, String userName, String password) {
        this();
        this.hostName = hostName;
        this.serverPort = serverPort;
        this.userName = userName;
        this.password = password;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 控制
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 开始ftp服务
     *
     * @param remotePath FTP目录
     * @throws IOException
     */
    private void start(String remotePath) throws IOException {
        // 打开FTP服务
        try {
            connect();
        } catch (IOException e) {
            throw new IOException("连接FTP服务器失败");
        }
        if (remotePath == null) remotePath = "/";
        try {
            // 设置模式
            boolean result = ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
            if (!result) throw new IOException("FTP服务器不支持传输文件");
            // FTP下创建文件夹
            ftpClient.makeDirectory(remotePath);
            // 改变FTP目录
            result = ftpClient.changeWorkingDirectory(remotePath);
            if (!result) throw new IOException("无权限读取FTP目录");
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, serverPort);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType()
                                                                  .split(" ")[0]);
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
    private void disconnect() {
        if (ftpClient != null) {
            try {
                // 退出FTP
                ftpClient.logout();
                // 断开连接
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 上传文件
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 上传文件
     */
    public void uploadFile(String remotePath, ProgressListener listener, File... files) {
        progressListener = listener;
        new UploadTask().execute(remotePath, Arrays.asList(files));
    }

    /**
     * 内部上传文件的操作
     *
     * @param localFile 本地文件
     * @throws IOException
     */
    private void uploadFileInternal(File localFile) throws IOException {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFile));
            // 上传单个文件
            boolean success = ftpClient.storeFile(localFile.getName(), bis);
            // 关闭文件流
            bis.close();
            if (!success) throw new IOException("上传文件失败!");
        } catch (IOException e) {
            disconnect();
            String message = "上传文件失败!";
            if (e instanceof FileNotFoundException) message = "上传文件不存在!";
            throw new IOException(message);
        }
    }

    /**
     * 上传任务
     */
    private class UploadTask extends AsyncTask<Object, Integer, String> {

        int progress;
        long totalSize;
        long transferredSize;
        String path;
        List<File> fileList;

        @Override
        @SuppressWarnings("unchecked")
        protected String doInBackground(Object... params) {
            if (params.length != 2) return "参数设置错误!";
            path = (String) params[0];
            fileList = (List<File>) params[1];
            for (File f : fileList) {
                totalSize += f.length();
            }
            String errMessage = null;
            try {
                ftpClient.setCopyStreamListener(new DefaultCopyStreamListener() {

                    @Override
                    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                        transferredSize += bytesTransferred;
                        int tmp = (int) ((100 * transferredSize) / totalSize);
                        if (tmp - progress >= 1) {
                            progress = tmp;
                            publishProgress(progress);
                        }
                    }
                });
                start(path);
                for (File file : fileList) {
                    uploadFileInternal(file);
                }
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                errMessage = e.getMessage();
            }
            return errMessage;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (progressListener != null) progressListener.onProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String errMessage) {
            if (progressListener != null) {
                if (errMessage == null) progressListener.onSuccess();
                else progressListener.onFail(errMessage);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 下载
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 下载文件
     */
    public void downloadFile(String remotePath, String localPath, ProgressListener listener, String... fileNames) {
        this.progressListener = listener;
        new DownloadTask().execute(remotePath, localPath, Arrays.asList(fileNames));
    }

    /**
     * 下载一个文件内部操作
     *
     * @param remoteFile 远端File
     * @param remotePath 远端路径
     * @param localPath  本地路径
     * @throws IOException
     */
    private void downloadFileInternal(FTPFile remoteFile, String remotePath, String localPath) throws IOException {
        File localFile;
        try {
            File dir = new File(localPath);
            dir.mkdirs();
            localFile = new File(dir, remoteFile.getName());
            localFile.delete();
            localFile.createNewFile();
        } catch (IOException e) {
            disconnect();
            throw new IOException("创建本地文件失败!");
        }
        try {
            FileOutputStream fos = new FileOutputStream(localFile);
            boolean success = ftpClient.retrieveFile(remotePath + "/" + remoteFile.getName(), fos);
            fos.close();
            if (!success) throw new IOException("传输文件失败!");
        } catch (IOException e) {
            disconnect();
            throw new IOException("传输文件失败!");
        }
    }

    /**
     * 列出FTP服务的文件
     *
     * @param fileNames 指定的文件明, 可以为null, null代表列出全部文件
     * @return FTPFile的列表
     * @throws IOException
     */
    private List<FTPFile> listRemoteFiles(List<String> fileNames) throws IOException {
        ArrayList<FTPFile> ftpFiles = new ArrayList<>();
        try {
            FTPFile[] files = ftpClient.listFiles();
            if (fileNames == null || fileNames.size() == 0) {
                return Arrays.asList(files);
            }
            for (String name : fileNames) {
                boolean exist = false;
                for (FTPFile file : files) {
                    if (file.getName()
                            .equals(name)) {
                        exist = true;
                        ftpFiles.add(file);
                        break;
                    }
                }
                if (!exist) throw new IOException(name + "文件不存在!");
            }
        } catch (IOException e) {
            disconnect();
            throw new IOException("读取FTP文件列表失败");
        }
        return ftpFiles;
    }

    /**
     * 下载任务
     */
    public class DownloadTask extends AsyncTask<Object, Integer, String> {

        int progress;
        long totalSize;
        long transferredSize;
        String remotePath;
        String localPath;
        List<FTPFile> fileList;

        @Override
        @SuppressWarnings("unchecked")
        protected String doInBackground(Object... params) {
            if (params.length != 3) return "参数设置错误!";
            remotePath = (String) params[0];
            localPath = (String) params[1];
            String errMessage = null;
            List<String> fileNames = (List<String>) params[2];
            try {
                start(remotePath);
                // 先判断服务器文件是否存在
                fileList = listRemoteFiles(fileNames);
                for (FTPFile ftpFile : fileList) {
                    totalSize += ftpFile.getSize();
                }
                ftpClient.setCopyStreamListener(new DefaultCopyStreamListener() {
                    @Override
                    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                        transferredSize += bytesTransferred;
                        int tmp = (int) ((100 * transferredSize) / totalSize);
                        if (tmp - progress >= 1) {
                            progress = tmp;
                            publishProgress(progress);
                        }
                    }
                });
                for (FTPFile file : fileList) {
                    downloadFileInternal(file, remotePath, localPath);
                }
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                errMessage = e.getMessage();
            }
            return errMessage;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (progressListener != null) progressListener.onProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String errMessage) {
            if (progressListener != null) {
                if (errMessage == null) progressListener.onSuccess();
                else progressListener.onFail(errMessage);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 删除
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 删除Ftp下的文件.
     *
     * @param remoteFiles Ftp文件(完整目录)
     * @param listener    监听器
     */
    public void deleteFile(ProgressListener listener, String... remoteFiles) {
        if (remoteFiles == null || remoteFiles.length == 0) {
            if (listener != null) listener.onFail("没有指定删除的文件");
            return;
        }
        for (String filePath : remoteFiles) {
            if (filePath.lastIndexOf("/") < 0) {
                if (listener != null) listener.onFail("文件的路径不符合格式");
                return;
            }
        }
        progressListener = listener;
        new DeleteTask().execute(Arrays.asList(remoteFiles));
    }

    /**
     * 分离路径和文件名
     *
     * @param remoteFile 完整的路径
     * @return 分离后的结果
     */
    private String[] separatePath(String remoteFile) {
        String[] result = new String[2];
        int index = remoteFile.lastIndexOf("/");
        result[0] = remoteFile.substring(0, index);
        result[1] = remoteFile.substring(index + 1);
        return result;
    }

    /**
     * 内部删除Ftp下的文件的操作
     *
     * @param remoteFile 文件的路径
     * @throws IOException
     */
    private void deleteFileInternal(String remoteFile) throws IOException {
        try {
            String[] paths = separatePath(remoteFile);
            ftpClient.changeWorkingDirectory(paths[0]);
            if (!Arrays.asList(ftpClient.listNames())
                       .contains(paths[1])) throw new FileNotFoundException(paths[1] + "文件在ftp上不存在!");
            if (!ftpClient.deleteFile(remoteFile)) throw new SecurityException("删除文件失败, 请确认是否有权限删除");
        } catch (Exception e) {
            disconnect();
            String message = e.getClass() == IOException.class ? "删除文件出错!" : e.getMessage();
            throw new IOException(message);
        }
    }

    /**
     * 删除任务
     */
    public class DeleteTask extends AsyncTask<Object, Void, String> {
        List<String> remoteFiles;

        @Override
        @SuppressWarnings("unchecked")
        protected String doInBackground(Object... params) {
            String errMessage = null;
            if (params.length != 1) return "传入的参数格式不符合";
            remoteFiles = (List<String>) params[0];
            try {
                start(null);
                for (String remoteFile : this.remoteFiles) {
                    deleteFileInternal(remoteFile);
                }
                disconnect();
            } catch (IOException e) {
                errMessage = e.getMessage();
            }
            return errMessage;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressListener != null) {
                if (result == null) progressListener.onSuccess();
                else progressListener.onFail(result);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 接口, 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 异步上传的监听接口
     */
    public interface ProgressListener {

        void onProgress(int percentage);

        void onSuccess();

        void onFail(String errorMessage);

    }

    /**
     * 异步上传的监听接口
     */
    public static class DefaultListener implements ProgressListener {

        public void onProgress(int percentage) {
        }

        public void onSuccess() {
        }

        public void onFail(String errorMessage) {
        }

    }

    public static class DefaultCopyStreamListener implements CopyStreamListener {

        @Override
        public void bytesTransferred(CopyStreamEvent copyStreamEvent) {

        }

        @Override
        public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

        }
    }

}
