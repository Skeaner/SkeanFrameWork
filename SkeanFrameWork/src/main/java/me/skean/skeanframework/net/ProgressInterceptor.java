package me.skean.skeanframework.net;
/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.badoo.mobile.util.WeakHandler;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * 用于Retrofit的进度上传/下载进度监听的拦截器
 */
public class ProgressInterceptor implements Interceptor {

    private static final int MSG_UPLOAD_PROGRESS = 1;
    private static final int MSG_DOWNLOAD_PROGRESS = 2;

    private UploadListener uploadListener;
    private DownloadListener downloadListener;

    private static final String TAG = "ProgressInterceptor";

    public ProgressInterceptor() {
    }

    public ProgressInterceptor(UploadListener uploadListener, DownloadListener downloadListener) {
        this.uploadListener = uploadListener;
        this.downloadListener = downloadListener;
    }

    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    private WeakHandler handler = new WeakHandler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD_PROGRESS:
                    if (uploadListener != null) {
                        int percentage = msg.arg1;
                        boolean done = msg.arg2 == 1;
                        long[] data = (long[]) msg.obj;
                        uploadListener.uploadProgress(data[0], data[1], percentage, done);
                    } else {
                        Log.i(TAG, "noUploadListener");
                    }
                    break;
                case MSG_DOWNLOAD_PROGRESS:
                    if (downloadListener != null) {
                        int percentage = msg.arg1;
                        boolean done = msg.arg2 == 1;
                        long[] data = (long[]) msg.obj;
                        downloadListener.downloadProgress(data[0], data[1], percentage, done);
                    } else {
                        Log.i(TAG, "noDownListener");
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request rawRequest = chain.request();
        Response rawResponse;
        if (rawRequest.body() == null) {
            rawResponse = chain.proceed(rawRequest);
        } else {
            Request progressRequest = rawRequest.newBuilder()
                                                .method(rawRequest.method(), new ProgressRequestBody(rawRequest.body()))
                                                .build();
            rawResponse = chain.proceed(progressRequest);
        }
        return rawResponse.newBuilder().body(new ProgressResponseBody(rawResponse.body())).build();
    }

    /**
     * 带进度检测功能的ResponseBody
     */
    private class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody) {
            this.responseBody = responseBody;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(new ProgressSource(responseBody.source()));
            }
            return bufferedSource;
        }

        private class ProgressSource extends ForwardingSource {

            private long totalBytesRead = 0L;
            private int lastPercentage = 0;
            private int percentageFilter = 3;

            public ProgressSource(Source delegate) {
                super(delegate);
            }

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                boolean done = bytesRead == -1;
                int percentage = (int) (100 * totalBytesRead / contentLength());
                if (done || (percentage - lastPercentage >= percentageFilter)) {
                    lastPercentage = percentage;
                    Log.i(TAG, "download progress -- " + lastPercentage + "%");
                    Message msg = new Message();
                    msg.arg1 = percentage;
                    msg.arg2 = done ? 1 : 0;
                    msg.obj = new long[]{totalBytesRead, contentLength()};
                    msg.what = MSG_DOWNLOAD_PROGRESS;
                    handler.sendMessage(msg);
                }
                return bytesRead;
            }
        }
    }

    /**
     * 带进度检测功能的RequestBody
     */
    private class ProgressRequestBody extends RequestBody {
        private RequestBody requestBody;
        private ProgressSink progressSink;

        private ProgressRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
        }

        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public long contentLength() {
            try {
                return requestBody.contentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            BufferedSink bufferedSink;

            progressSink = new ProgressSink(sink);
            bufferedSink = Okio.buffer(progressSink);

            requestBody.writeTo(bufferedSink);

            bufferedSink.flush();
        }

        private final class ProgressSink extends ForwardingSink {

            private long bytesWritten = 0;
            private int lastPercentage = 0;
            private int percentageFilter = 3;

            private ProgressSink(Sink delegate) {
                super(delegate);
            }

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount != -1 ? byteCount : 0;
                boolean done = bytesWritten == contentLength();
                int percentage = (int) (100 * bytesWritten / contentLength());
                if (done || (percentage - lastPercentage >= percentageFilter)) {
                    lastPercentage = percentage;
                    Log.i(TAG, "upload progress -- " + lastPercentage + "%");
                    Message msg = new Message();
                    msg.arg1 = lastPercentage;
                    msg.arg2 = done ? 1 : 0;
                    msg.obj = new long[]{bytesWritten, contentLength()};
                    msg.what = MSG_UPLOAD_PROGRESS;
                    handler.sendMessage(msg);
                }
            }

        }

    }

    /**
     * 下载进度监控, 在主线程调用downloadProgress();
     */
    public interface DownloadListener {
        void downloadProgress(long bytesRead, long contentLength, int percentage, boolean done);
    }

    /**
     * 上传进度监听,在主线程调用uploadProgress();
     */
    public interface UploadListener {
        void uploadProgress(long bytesWritten, long contentLength, int percentage, boolean done);
    }

}
