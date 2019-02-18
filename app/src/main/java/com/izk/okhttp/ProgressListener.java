package com.izk.okhttp;

/**
 * Created by Malong
 * on 19/2/18.
 * 使用okhttp拦截器模式下载获取进度监听类
 */
public interface ProgressListener {

    public void onPrpgress(int progress);

    public void onComplete(long totalSize);

}
