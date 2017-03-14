package com.larryhowell.xunta.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.larryhowell.xunta.R;
import com.larryhowell.xunta.adapter.BindListAdapter;
import com.larryhowell.xunta.common.Config;
import com.larryhowell.xunta.common.UtilBox;
import com.larryhowell.xunta.presenter.BindPresenterImpl;
import com.larryhowell.xunta.presenter.IBindPresenter;
import com.larryhowell.xunta.widget.DividerItemDecoration;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

public class BindListActivity extends AppCompatActivity
        implements IBindPresenter.IBindView {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recyclerView)
    SuperRecyclerView mRecyclerView;

    @Bind(R.id.appBar)
    AppBarLayout mAppBarLayout;

    private BindListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private MaterialDialog mDialog;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bind_list);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("绑定中...");
        mProgressDialog.setCancelable(false);

        mRecyclerView.setRefreshListener(this::refresh);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null));

        // 延迟执行才能使旋转进度条显示出来
        new Handler().postDelayed(() -> {
            mRecyclerView.setRefreshing(true);
            refresh();
        }, 200);
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

    private void refresh() {
        if (!Config.isConnected) {
            UtilBox.showSnackbar(this, R.string.cant_access_network);

            mRecyclerView.setRefreshing(false);
            return;
        }

        mRecyclerView.setRefreshing(true);

        new BindPresenterImpl(this).getBindList();
    }

    @Override
    public void OnGetBindListResult(Boolean result, String info) {
        mRecyclerView.setRefreshing(false);

        if (result) {
            if (mAdapter == null) {
                mAdapter = new BindListAdapter(this);

                mAdapter.setOnItemClickListener((view, person) -> {
                    Intent intent = new Intent(BindListActivity.this, MemberMainActivity.class);
                    intent.putExtra("person", person);
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    BindListActivity.this,
                                    mAppBarLayout, "appBar").toBundle());
                });

                if (Config.bindList != null && Config.bindList.size() != 0) {
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mRecyclerView.getLayoutParams();
                    layoutParams.height = Config.bindList.size() * (UtilBox.dip2px(this, 72) + 1) + 10;
                    mRecyclerView.setLayoutParams(layoutParams);
                }

                mRecyclerView.setAdapter(mAdapter);
            } else {
                if (Config.bindList != null && Config.bindList.size() != 0) {
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mRecyclerView.getLayoutParams();
                    layoutParams.height = Config.bindList.size() * (UtilBox.dip2px(this, 72) + 1) + 10;
                    mRecyclerView.setLayoutParams(layoutParams);
                }

                mAdapter.notifyDataSetChanged();
            }
        } else {
            UtilBox.showSnackbar(this, info);
        }
    }

    @OnClick({R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showAddMemberDialog();
                break;
        }
    }

    @SuppressLint("InflateParams")
    private void showAddMemberDialog() {
        final View contentView = getLayoutInflater().inflate(R.layout.dialog_input, null);

        TextInputLayout til = (TextInputLayout) contentView.findViewById(R.id.til_input);
        til.setHint("手机号");

        mTextView = (TextView) contentView.findViewById(R.id.tv_input);

        final EditText editText = ((EditText) contentView
                .findViewById(R.id.edt_input));
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.requestFocus();

        mDialog = new MaterialDialog(this);
        mDialog.setPositiveButton("绑定", v -> {
            if (TextUtils.isEmpty(editText.getText())) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("请输入对方手机号");
            } else if (!Config.isConnected) {
                Toast.makeText(this, R.string.cant_access_network,
                        Toast.LENGTH_SHORT).show();
            } else if (!UtilBox.isTelephoneNumber(editText.getText().toString())) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("请输入11位手机号");
            } else {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText("正在请求绑定...");

                new BindPresenterImpl(BindListActivity.this).bind(editText.getText().toString());
            }
        }).setNegativeButton("取消", v -> {
            mDialog.dismiss();
        }).setContentView(contentView)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    @Override
    public void onBindResult(Boolean result, String info) {
        if (result) {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
                UtilBox.showSnackbar(this, "已向对方发出申请");
            }

            refresh();
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(info);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Config.bindMessage != null && !"".equals(Config.bindMessage)) {
            try {
                JSONObject jsonObject = new JSONObject(Config.bindMessage);

                String nickname = jsonObject.getString("name");
                String telephone = jsonObject.getString("telephone");

                AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth);

                builder.setMessage("用户 " + nickname + " (手机号" + telephone + ")想与你绑定")
                        .setCancelable(false)
                        .setPositiveButton("绑定", (dialog, which) -> {
                            mProgressDialog.setMessage("绑定中...");
                            mProgressDialog.show();

                            new BindPresenterImpl(BindListActivity.this).bindConfirm(true, telephone);
                        })
                        .setNegativeButton("拒绝", (dialog, which) -> {
                            mProgressDialog.setMessage("请稍候...");
                            mProgressDialog.show();

                            new BindPresenterImpl(BindListActivity.this).bindConfirm(true, telephone);
                        })
                        .show();

                Config.bindMessage = "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBindConfirmResult(Boolean result, String info) {
        if (mProgressDialog.isShowing()) {
            new Handler().postDelayed(() -> mProgressDialog.dismiss(), 500);
        }

        if (result) {
            UtilBox.showSnackbar(this, "已完成");

            refresh();
        } else {
            UtilBox.showSnackbar(this, info);
        }
    }
}
