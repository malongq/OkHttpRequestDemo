package com.izk.okhttp;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Malong
 * on 19/2/18.
 * 使用okhttp拦截器模式下载获取进度
 *
 * 可以在拦截时更新上传、下载进度的监听，比较方便
 */
public class ProgressResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;
    private ProgressListener mProgressListener;

    public ProgressResponseBody(ResponseBody responseBody,ProgressListener progressListener) {
        this.mResponseBody = responseBody;
        this.mProgressListener = progressListener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if (mBufferedSource == null){
            mBufferedSource = Okio.buffer(getSourse(mResponseBody.source()));
        }

        return mBufferedSource;
    }

    private Source getSourse(Source source) {
        return new ForwardingSource(source) {

            long totalSize = 0l;
            long sum = 0l;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {

                if (totalSize == 0) {
                    totalSize = contentLength();
                }

                long len = super.read(sink, byteCount);

                sum += (len == -1 ? 0 : len);

                int progress = (int) ((sum * 1.0f / totalSize) * 100);

                if (len == -1) {
                    mProgressListener.onComplete(totalSize);
                } else {
                    mProgressListener.onPrpgress(progress);
                }

                return len;
            }
        };
    }
}
