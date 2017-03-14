package com.larryhowell.xunta.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.larryhowell.xunta.R;
import com.larryhowell.xunta.common.UtilBox;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;
import uk.co.senab.photoview.PhotoView;

/**
 * 查看图片
 */
public class CheckPictureActivity extends BaseActivity {

    @Bind(R.id.vp_checkPicture)
    ViewPager mViewPager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private int index = 0;
    private ArrayList<String> pictureList;
    private String fromWhere;
    private boolean hasChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pictureList = getIntent().getStringArrayListExtra("pictureList");
        index = getIntent().getIntExtra("index", 0);
        fromWhere = getIntent().getStringExtra("fromWhere");

        if ("net".equals(fromWhere)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_checkpicture);

        ButterKnife.bind(this);

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("local".equals(fromWhere)) {
            getMenuInflater().inflate(R.menu.menu_check_picture, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                final MaterialDialog dialog = new MaterialDialog(this);
                dialog.setTitle("提示")
                        .setMessage("真的要删除这张照片吗?")
                        .setCanceledOnTouchOutside(true)
                        .setPositiveButton("真的", v -> {
                            dialog.dismiss();

                            hasChange = true;

                            int currentItem = mViewPager.getCurrentItem();

                            pictureList.remove(currentItem);

                            if (pictureList.size() != 0) {
                                mViewPager.setAdapter(new SamplePagerAdapter());
                                mViewPager.setCurrentItem(currentItem - 1);
                            } else {
                                if (hasChange) {
                                    Intent intent = new Intent();
                                    intent.putExtra("pictureList", pictureList);
                                    setResult(RESULT_OK, intent);
                                } else {
                                    setResult(RESULT_CANCELED);
                                }

                                CheckPictureActivity.this.finish();
                                overridePendingTransition(R.anim.scale_stay, R.anim.zoom_out_quick);
                            }
                        })
                        .setNegativeButton("我手滑了", v -> {
                            dialog.dismiss();
                        })
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        initToolbar();

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(index, true);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if ("net".equals(fromWhere)) {
            //noinspection ConstantConditions
            getSupportActionBar().hide();
            //requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mToolbar.setNavigationOnClickListener(v -> {
            if (hasChange) {
                Intent intent = new Intent();
                intent.putExtra("pictureList", pictureList);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED);
            }

            finish();
            overridePendingTransition(R.anim.scale_stay, R.anim.zoom_out_quick);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (hasChange) {
                Intent intent = new Intent();
                intent.putExtra("pictureList", pictureList);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED);
            }

            finish();
            overridePendingTransition(R.anim.scale_stay, R.anim.zoom_out_quick);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pictureList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final RelativeLayout layout = new RelativeLayout(CheckPictureActivity.this);

            final RelativeLayout.LayoutParams photoRlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            photoRlp.addRule(RelativeLayout.CENTER_IN_PARENT);

            final ProgressBar progressBar = new ProgressBar(CheckPictureActivity.this);
            final PhotoView photoView = new PhotoView(container.getContext());

            if (!pictureList.get(position).contains("http")) {
                progressBar.setVisibility(View.GONE);
                String imgUrl = ImageDownloader.Scheme.FILE.wrap(pictureList.get(position));
                ImageLoader.getInstance().displayImage(imgUrl, photoView);
            } else {
                ImageLoader.getInstance().displayImage(
                        pictureList.get(position),
                        photoView,
                        new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {
                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                progressBar.setVisibility(View.GONE);
                                UtilBox.showSnackbar(CheckPictureActivity.this, "图片加载出错");
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {
                            }
                        });
            }

            // 单击退出
            photoView.setOnPhotoTapListener((view, v, v1) -> {
                if (hasChange) {
                    Intent intent = new Intent();
                    intent.putExtra("pictureList", pictureList);
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED);
                }

                CheckPictureActivity.this.finish();
                overridePendingTransition(R.anim.scale_stay, R.anim.zoom_out_quick);
            });
            layout.addView(photoView, photoRlp);

            RelativeLayout.LayoutParams progressRlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            progressRlp.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(progressBar, progressRlp);

            container.addView(layout,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}