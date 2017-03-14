package com.larryhowell.xunta.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.larryhowell.xunta.R;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.Constants;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.IPlanPresenter;
import com.larryhowell.xunta.presenter.IUploadImagePresenter;
import com.larryhowell.xunta.presenter.IUserInfoPresenter;
import com.larryhowell.xunta.presenter.PlanPresenterImpl;
import com.larryhowell.xunta.presenter.UploadImagePresenterImpl;
import com.larryhowell.xunta.presenter.UserInfoPresenterImpl;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

public class UserInfoActivity extends BaseActivity
        implements IUploadImagePresenter.IUploadImageView, View.OnClickListener,
        IUserInfoPresenter.IUserInfoView, IPlanPresenter.IPlanView {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_nickname)
    TextView mNicknameTextView;

    @Bind(R.id.iv_portrait)
    ImageView mPortraitImageView;

    @Bind(R.id.rv_portrait)
    MaterialRippleLayout mPortraitRipple;

    @Bind(R.id.rv_nickname)
    MaterialRippleLayout mNicknameRipple;

    @Bind(R.id.rv_bindlist)
    MaterialRippleLayout mBindListRipple;

    @Bind(R.id.rv_location)
    MaterialRippleLayout mLocationRipple;

    @Bind(R.id.rv_plan)
    MaterialRippleLayout mPlanRipple;

    @Bind(R.id.rv_logout)
    MaterialRippleLayout mLogoutRipple;

    @Bind(R.id.appBar)
    AppBarLayout mAppBarLayout;

    private TextView mInputTextView;
    private MaterialDialog mDialog;
    private ProgressDialog mProgressDialog;
    private boolean changed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNicknameTextView.setText(Config.nickname);

        ImageLoader.getInstance().displayImage(
                Config.portrait + "&w=" + UtilBox.dip2px(this, 50) + "&h=" + UtilBox.dip2px(this, 50),
                mPortraitImageView);

        mPortraitRipple.setOnClickListener(view -> {
            String[] items = {"更换头像"};

            AlertDialog.Builder builder;
            Dialog dialog;

            builder = new AlertDialog.Builder(UserInfoActivity.this);
            builder.setItems(items, (dialog1, which) -> {
                Crop.pickImage(UserInfoActivity.this, Constants.CODE_PICK_PORTRAIT);
            })
                    .setCancelable(true);

            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        });

        mNicknameRipple.setOnClickListener(view -> showNicknameDialog(mNicknameTextView.getText().toString()));

        mBindListRipple.setOnClickListener(view ->
                startActivity(new Intent(this, BindListActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(
                                this, Pair.create(mAppBarLayout, "appBar")).toBundle()
                ));

        mLocationRipple.setOnClickListener(view ->
                startActivity(new Intent(this, LocationListActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(
                                this, Pair.create(mAppBarLayout, "appBar")).toBundle()
                ));

        mPlanRipple.setOnClickListener(view -> {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setCancelable(false);
            }

            mProgressDialog.setMessage("获取中...");
            mProgressDialog.show();

            new PlanPresenterImpl(this).getPlan(Config.telephone);
        });

        mLogoutRipple.setOnClickListener(view -> {
            AlertDialog.Builder builder;
            Dialog dialog;

            builder = new AlertDialog.Builder(UserInfoActivity.this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);
            builder.setMessage("你确定不是手滑了么?")
                    .setCancelable(true)
                    .setPositiveButton("注销", (dialogInterface, i) -> {
                        UtilBox.clearAllData(UserInfoActivity.this);

                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("我手滑了", null);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        });
    }

    @Override
    public void onGetPlanResult(Boolean result, String info) {
        if (mProgressDialog.isShowing()) {
            new Handler().postDelayed(() -> mProgressDialog.dismiss(), 500);
        }

        if (result) {
            startActivity(new Intent(this, PlanDetailActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(
                            this, Pair.create(mAppBarLayout, "appBar")).toBundle()
            );
        } else {
            UtilBox.showSnackbar(this, info);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.iv_portrait})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_portrait:
                Intent intent = new Intent(this, CheckPictureActivity.class);

                ArrayList<String> portrait = new ArrayList<>();
                portrait.add(Config.portrait);
                intent.putStringArrayListExtra("pictureList", portrait);
                intent.putExtra("index", 0);
                intent.putExtra("fromWhere", "net");

                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in_quick, R.anim.scale_stay);
                break;
        }
    }

    @SuppressLint("InflateParams")
    private void showNicknameDialog(final String nickname) {
        final View contentView = getLayoutInflater()
                .inflate(R.layout.dialog_input, null);

        TextInputLayout til = (TextInputLayout) contentView.findViewById(R.id.til_input);
        til.setHint("昵称");

        final EditText et_nickname = ((EditText) contentView
                .findViewById(R.id.edt_input));
        et_nickname.setText(nickname);
        et_nickname.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        et_nickname.requestFocus();

        mDialog = new MaterialDialog(this).setPositiveButton("修改", v -> {
            new Handler().post(() -> {
                if (!Config.isConnected) {
                    UtilBox.showSnackbar(this, R.string.cant_access_network);
                    return;
                }

                final String newNickname = et_nickname.getText().toString();
                mInputTextView = (TextView) contentView.findViewById(R.id.tv_input);

                if (newNickname.isEmpty() || newNickname.length() == 0) {
                    mInputTextView.setVisibility(View.VISIBLE);
                    mInputTextView.setText("昵称不能为空");
                } else if (newNickname.getBytes().length > 24) {
                    mInputTextView.setVisibility(View.VISIBLE);
                    mInputTextView.setText("昵称过长");
                } else if (newNickname.equals(nickname)) {
                    mDialog.dismiss();
                } else {
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(this);
                        mProgressDialog.setCancelable(false);
                    }

                    mProgressDialog.setMessage("修改中...");
                    mProgressDialog.show();

                    new UserInfoPresenterImpl(this).updateNickname(newNickname);
                }
            });
        });
        mDialog.setNegativeButton("取消", v -> {
            mDialog.dismiss();
        });

        mDialog.setContentView(contentView)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    @Override
    public void onUpdateNickname(Boolean result, String info) {
        if (mProgressDialog.isShowing()) {
            new Handler().postDelayed(() -> mProgressDialog.dismiss(), 500);
        }

        if (result) {
            mDialog.dismiss();
            mNicknameTextView.setText(info);
            changed = true;
        } else {
            mInputTextView.setVisibility(View.VISIBLE);
            mInputTextView.setText(info);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CODE_PICK_PORTRAIT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri destination = Uri.fromFile(new File(this.getCacheDir(), "cropped"));
                    Crop.of(data.getData(), destination).asSquare()
                            .start(this, Constants.CODE_CROP_PORTRAIT);
                }
                break;

            case Constants.CODE_CROP_PORTRAIT:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = Crop.getOutput(data);

                    if (!Config.isConnected) {
                        UtilBox.showSnackbar(this, R.string.cant_access_network);
                        return;
                    }

                    final Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeStream(
                                this.getContentResolver().openInputStream(imageUri));

                        final String objectName = Config.telephone + ".jpg";

                        new UploadImagePresenterImpl(this, this).doUploadImage(
                                UtilBox.compressImage(bitmap, Constants.SIZE_IMAGE),
                                Constants.DIR_PORTRAIT, objectName, Constants.SP_KEY_PORTRAIT);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onUploadImageResult(Boolean result, String info) {
        if (result) {
            changed = true;
            ImageLoader.getInstance().displayImage(
                    Config.portrait + "&w=" + UtilBox.dip2px(this, 50) + "&h=" + UtilBox.dip2px(this, 50),
                    mPortraitImageView);
        } else {
            UtilBox.showSnackbar(this, info);
        }
    }

    @Override
    public void onUploadImagesResult(Boolean result, String info, List<String> pictureList) {

    }

    @Override
    public void onBackPressed() {
        if (changed) {
            setResult(RESULT_OK);
        }

        super.onBackPressed();

    }

    @Override
    public void onGetUserInfoResult(Boolean result, String info) {

    }

    @Override
    public void onMakePlanResult(Boolean result, String info) {

    }

    @Override
    public void onCancelPlanResult(Boolean result, String info) {

    }
}
