package com.larryhowell.xunta.net;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadTask;

/**
 * 简化版上传监听器
 */
public abstract class BaseUploadListener implements UploadListener {
    @Override
    public void onUploading(UploadTask uploadTask) {

    }

    @Override
    public void onUploadCancelled(UploadTask uploadTask) {

    }
}