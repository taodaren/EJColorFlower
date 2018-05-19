package cn.eejing.ejcolorflower.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.EditDeviceToGroupBean;
import cn.eejing.ejcolorflower.ui.adapter.CoDeviceLeftAdapter;
import cn.eejing.ejcolorflower.ui.adapter.CoDeviceRightAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 添加、移除设备
 */
public class CoDeviceActivity extends BaseActivity {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.btn_device_edit)
    SuperButton btnDeviceEdit;
    @BindView(R.id.btn_device_save)
    SuperButton btnDeviceSave;
    @BindView(R.id.tv_added_can)
    TextView tvAddedCan;
    @BindView(R.id.rv_added_can)
    RecyclerView rvAddedCan;
    @BindView(R.id.tv_added_already)
    TextView tvAddedAlready;
    @BindView(R.id.rv_added_already)
    RecyclerView rvAddedAlready;

    private List<String> mList, mPossess;
    private int mGroupId;
    private LinearLayoutManager mManager;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_device;
    }

    @Override
    public void initView() {
        mList = new ArrayList<>();
        mPossess = new ArrayList<>();

        Intent intent = getIntent();
        mGroupId = intent.getIntExtra("group_id", 0);
        String groupName = intent.getStringExtra("group_name");

        setToolbar(groupName, View.VISIBLE);
    }

    @Override
    public void initData() {
        OkGo.<String>post(Urls.GO_EDIT_DEVICE_TO_GROUP)
                .tag(this)
                .params("member_id", 12)
                .params("group_id", mGroupId)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();

                        Gson gson = new Gson();
                        EditDeviceToGroupBean bean = gson.fromJson(body, EditDeviceToGroupBean.class);
                        mList = bean.getData().getList();
                        mPossess = bean.getData().getPossess();

                        initRvAddedCan();
                        initRvAddedReady();
                    }
                });
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRvAddedCan() {
        if (mList.size() > 0) {
            tvAddedCan.setVisibility(View.GONE);
        } else {
            tvAddedCan.setVisibility(View.VISIBLE);
        }
        mManager = new LinearLayoutManager(this);
        rvAddedCan.setLayoutManager(mManager);
        CoDeviceLeftAdapter adapter = new CoDeviceLeftAdapter(this, mList);
        rvAddedCan.setAdapter(adapter);
    }

    private void initRvAddedReady() {
        if (mPossess.size() > 0) {
            tvAddedAlready.setVisibility(View.GONE);
        } else {
            tvAddedAlready.setVisibility(View.VISIBLE);
        }
        mManager = new LinearLayoutManager(this);
        rvAddedAlready.setLayoutManager(mManager);
        CoDeviceRightAdapter adapter = new CoDeviceRightAdapter(this, mPossess);
        rvAddedAlready.setAdapter(adapter);
    }

}
