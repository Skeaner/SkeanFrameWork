package me.skean.skeanframework.net;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallback mListener;
    private String content_type;

    private static final int DEFAULT_BUFFER_SIZE = 2048;
    public static final int MSG_PROGRESS = 1;

    public interface UploadCallback {
        void onProgress(int percentage);
    }

    public ProgressRequestBody(final File file, String contentType, final UploadCallback listener) {
        this.content_type = contentType;
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(content_type + "/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper(), msg -> {
                if (msg.what == MSG_PROGRESS) {
                    if (mListener != null) mListener.onProgress(msg.arg1);
                    return true;
                }
                return false;
            });
            while ((read = in.read(buffer)) != -1) {
                int percentage = (int) (100 * uploaded / fileLength);
                // update progress on UI thread
                handler.obtainMessage(MSG_PROGRESS, percentage, 0).sendToTarget();
                uploaded += read;
                sink.write(buffer, 0, read);
            }
        }
        finally {
            in.close();
        }
    }

}
