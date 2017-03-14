package com.larryhowell.xunta.presenter;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by howell on 2015/11/29.
 * UploadPresenter接口
 */
public interface IUploadImagePresenter {
    void doUploadImage(Bitmap bitmap, String dir, String objectName, String type);

    void doUploadImages(List<String> path, String dir);

    interface IUploadImageView {
        void onUploadImageResult(Boolean result, String info);

        void onUploadImagesResult(Boolean result, String info, List<String> pictureList);
    }
}
