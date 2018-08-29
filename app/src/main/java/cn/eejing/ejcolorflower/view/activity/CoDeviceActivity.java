package cn.eejing.ejcolorflower.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.model.request.EditDeviceToGroupBean;
import cn.eejing.ejcolorflower.view.adapter.CoDeviceLeftAdapter;
import cn.eejing.ejcolorflower.view.adapter.CoDeviceRightAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 添加、移除设备
 */

public class CoDeviceActivity extends BaseActivity implements
        View.OnClickListener, CoDeviceLeftAdapter.LeftClickListener, CoDeviceRightAdapter.RightClickListener {

    @BindView(R.id.btn_device_save)         SuperButton btnDeviceSave;
    @BindView(R.id.tv_added_can)            TextView tvAddedCan;
    @BindView(R.id.rv_added_can)            RecyclerView rvAddedCan;
    @BindView(R.id.tv_added_already)        TextView tvAddedAlready;
    @BindView(R.id.rv_added_already)        RecyclerView rvAddedAlready;

    private List<String> mList, mPossess;
    private LinearLayoutManager mManager;
    private CoDeviceLeftAdapter leftAdapter;
    private CoDeviceRightAdapter rightAdapter;
    private int mGroupId;
    private String mMemberId, mToken;
    private Gson mGson;
    private String mNewPossess;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_device;
    }

    @Override
    public void initView() {
        setToolbar(getIntent().getStringExtra("group_name"), View.VISIBLE, null, View.GONE);

        mGson = new Gson();
        mList = new ArrayList<>();
        mPossess = new ArrayList<>();
        mMemberId = getIntent().getStringExtra("member_id");
        mToken = getIntent().getStringExtra("token");
        mGroupId = getIntent().getIntExtra("group_id", 0);
    }

    @Override
    public void initData() {
        getDataWithEditDeviceGroup();
    }

    @Override
    public void initListener() {
        btnDeviceSave.setOnClickListener(this);
    }

    @Override
    public void onClickLeft(View view, int position) {
        if (mList.size() > 0 && mPossess.size() == 0) {
            tvAddedCan.setVisibility(View.GONE);
            tvAddedAlready.setVisibility(View.GONE);
        } else if (mList.size() == 1 && mPossess.size() > 0) {
            tvAddedCan.setVisibility(View.VISIBLE);
            tvAddedAlready.setVisibility(View.GONE);
        }

        try {
            String data = mList.get(position);

            leftAdapter.removeData(position);
            rightAdapter.addData(data);
            mPossess.add(data);
            mList.remove(position);

            mNewPossess = mGson.toJson(mPossess);
            Log.i(AppConstant.TAG, "onClickLeft mNewPossess: " + mNewPossess);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickRight(View view, int position) {
        if (mPossess.size() > 0 && mList.size() == 0) {
            tvAddedCan.setVisibility(View.GONE);
            tvAddedAlready.setVisibility(View.GONE);
        } else if (mPossess.size() == 1 && mList.size() > 0) {
            tvAddedCan.setVisibility(View.GONE);
            tvAddedAlready.setVisibility(View.VISIBLE);
        }

        try {
            String data = mPossess.get(position);

            rightAdapter.removeData(position);
            leftAdapter.addData(data);
            mPossess.remove(position);
            mList.add(data);

            mNewPossess = mGson.toJson(mPossess);
            Log.i(AppConstant.TAG, "onClickRight mNewPossess: " + mNewPossess);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_device_save:
                Log.i(AppConstant.TAG, "onClickSave mNewPossess: " + mNewPossess);
                getDataWithAddDeviceToGroup();
                break;
            default:
                break;
        }
    }

    private void initRvAddedCan() {
        if (mList != null) {
            if (mList.size() > 0) {
                tvAddedCan.setVisibility(View.GONE);
            } else {
                tvAddedCan.setVisibility(View.VISIBLE);
            }
            mManager = new LinearLayoutManager(this);
            rvAddedCan.setLayoutManager(mManager);
            leftAdapter = new CoDeviceLeftAdapter(this, mList, this);
            rvAddedCan.setAdapter(leftAdapter);
        }
    }

    private void initRvAddedReady() {
        if (mList != null) {
            if (mPossess.size() > 0) {
                tvAddedAlready.setVisibility(View.GONE);
            } else {
                tvAddedAlready.setVisibility(View.VISIBLE);
            }
            mManager = new LinearLayoutManager(this);
            rvAddedAlready.setLayoutManager(mManager);
            rightAdapter = new CoDeviceRightAdapter(this, mPossess, this);
            rvAddedAlready.setAdapter(rightAdapter);
        }
    }

    private void getDataWithEditDeviceGroup() {
        OkGo.<String>post(Urls.GO_EDIT_DEVICE_TO_GROUP)
                .tag(this)
                .params("member_id", mMemberId)
                .params("group_id", mGroupId)
                .params("token", mToken)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "go_edit_device_to_group request succeeded！！！" + body);

                        mGson = new Gson();
                        EditDeviceToGroupBean bean = mGson.fromJson(body, EditDeviceToGroupBean.class);
                        mList = bean.getData().getList();
                        mPossess = bean.getData().getPossess();

                        initRvAddedCan();
                        initRvAddedReady();
                    }
                });

    }

    private void getDataWithAddDeviceToGroup() {
        OkGo.<String>post(Urls.ADD_DEVICE_TO_GROUP)
                .tag(this)
                .params("member_id", mMemberId)
                .params("group_id", mGroupId)
                .params("device_id", mNewPossess)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "add_device_to_group request succeeded！！！" + body);
                        finish();
                    }
                });
    }

}
