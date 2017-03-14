package com.larryhowell.xunta.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.larryhowell.xunta.App;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.Urls;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.net.BaseUploadListener;
import com.larryhowell.xunta.net.MediaServiceUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by howell on 2015/11/29.
 * UploadPresenter实现类
 */
public class UploadImagePresenterImpl implements IUploadImagePresenter {
    private IUploadImageView mIUploadImageView;
    private Context mContext;
    private int uploadedNum = 0;
    private List<String> pictureList = new ArrayList<>();

    public UploadImagePresenterImpl(Context context, IUploadImageView iUploadImageView) {
        this.mContext = context;
        this.mIUploadImageView = iUploadImageView;
    }

    @Override
    public void doUploadImage(Bitmap bitmap, String dir, final String objectName, final String type) {
        UploadListener listener = new BaseUploadListener() {

            @Override
            public void onUploadFailed(UploadTask uploadTask, FailReason failReason) {
                System.out.println(failReason.getMessage());

                mIUploadImageView.onUploadImageResult(false, "图片上传失败，请重试");
            }

            @Override
            public void onUploadComplete(UploadTask uploadTask) {
                Config.time = UtilBox.getCurrentTime();

                SharedPreferences.Editor editor = App.sp.edit();
                editor.putString(Constants.SP_KEY_TIME, Config.time);
                editor.apply();

                Config.portrait = Urls.MEDIA_CENTER_PORTRAIT + Config.telephone + ".jpg" + "?t=" + Config.time;
                mIUploadImageView.onUploadImageResult(true, "");
            }

        };

        MediaServiceUtil.uploadImage(bitmap, dir, objectName, listener);
    }

    public void uploadImages(final List<String> path, final String dir) {
        // 压缩图片
        final Bitmap bitmap = UtilBox.getLocalBitmap(
                path.get(uploadedNum),
                UtilBox.getWidthPixels(mContext), UtilBox.getHeightPixels(mContext));

        UploadListener uploadListener = new BaseUploadListener() {
            @Override
            public void onUploadFailed(UploadTask uploadTask, FailReason failReason) {
                mIUploadImageView.onUploadImagesResult(false,
                        "图片上传失败，请重试", pictureList);
            }

            @Override
            public void onUploadComplete(UploadTask uploadTask) {
                // 已上传的图片数加一
                uploadedNum++;

                // 记录已上传的图片的文件名
                pictureList.add(uploadTask.getResult().url);

                if (uploadedNum < path.size()) {
                    // 接着上传下一张图片
                    try {
                        uploadImages(path, dir);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        mIUploadImageView.onUploadImagesResult(false,
                                "图片上传失败，请重试", pictureList);
                    }
                } else {
                    mIUploadImageView.onUploadImagesResult(true, "", pictureList);
                }
            }
        };

        MediaServiceUtil.uploadImage(
                UtilBox.compressImage(bitmap, Constants.SIZE_IMAGE), dir,
                UtilBox.getMD5Str(Calendar.getInstance().getTimeInMillis() + "") + ".jpg",
                uploadListener);
    }

    @Override
    public void doUploadImages(final List<String> path, final String dir) {
        uploadedNum = 0;
        pictureList.clear();

        uploadImages(path, dir);
    }
}