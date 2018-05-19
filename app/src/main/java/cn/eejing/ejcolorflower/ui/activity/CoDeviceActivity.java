package cn.eejing.ejcolorflower.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.EditDeviceToGroupBean;
import cn.eejing.ejcolorflower.ui.adapter.CoDeviceAdapter;
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

    private List<String> mRightList;
    private List<String> mLeftList;
    private int mGroupId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_device;
    }

    @Override
    public void initView() {
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
                        Log.e(AppConstant.TAG, "goEditDeviceToGroup request succeeded--->" + body);

                        Gson gson = new Gson();
                        EditDeviceToGroupBean bean = gson.fromJson(body, EditDeviceToGroupBean.class);
                        Log.e(AppConstant.TAG, "goEditDeviceToGroup onSuccess: json" + bean);

                        mLeftList = bean.getData().getList();
                        mRightList = bean.getData().getPossess();

                        initRv();
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

    private void initRv() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvAddedCan.setLayoutManager(manager);
        rvAddedAlready.setLayoutManager(manager);
        CoDeviceAdapter adapter = new CoDeviceAdapter(this, mLeftList, mRightList);
        rvAddedCan.setAdapter(adapter);
        rvAddedAlready.setAdapter(adapter);
    }

}
