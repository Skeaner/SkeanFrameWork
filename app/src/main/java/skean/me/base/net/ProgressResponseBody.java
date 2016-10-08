package skean.me.base.net;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 带进度检测功能的ResponseBody
 */
public class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final Listener listener;
    private BufferedSource bufferedSource;

    /**
     * 进度监听
     */
    public interface Listener {
        /**
         * @param byteWritten 已经下载或上传字节数
         * @param total       总字节数
         * @param done        是否完成
         */
        void onResponseProgress(long byteWritten, long total, boolean done);
    }

    public ProgressResponseBody(ResponseBody responseBody, Listener listener) {
        this.responseBody = responseBody;
        this.listener = listener;
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
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += (bytesRead != -1 ? bytesRead : 0);
                listener.onResponseProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }

}
