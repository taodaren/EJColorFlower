package cn.eejing.ejcolorflower.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.AddDeviceToGroupBean;
import cn.eejing.ejcolorflower.model.request.EditDeviceToGroupBean;
import cn.eejing.ejcolorflower.ui.adapter.CoDeviceLeftAdapter;
import cn.eejing.ejcolorflower.ui.adapter.CoDeviceRightAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * @创建者 Taodaren
 * @描述 添加、移除设备
 */
public class CoDeviceActivity extends BaseActivity implements
        View.OnClickListener, CoDeviceLeftAdapter.LeftClickListener, CoDeviceRightAdapter.RightClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
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
    private LinearLayoutManager mManager;
    private CoDeviceLeftAdapter leftAdapter;
    private CoDeviceRightAdapter rightAdapter;
    private int mGroupId;
    private Gson mGson;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_device;
    }

    @Override
    public void initView() {
        setToolbar(getIntent().getStringExtra("group_name"), View.VISIBLE);

        mList = new ArrayList<>();
        mPossess = new ArrayList<>();
        mGroupId = getIntent().getIntExtra("group_id", 0);
    }

    @Override
    public void initData() {
        getDataWithEditDeviceGroup();
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
        btnDeviceSave.setOnClickListener(this);
    }

    private void initRvAddedCan() {
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

    private void initRvAddedReady() {
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

    private void getDataWithEditDeviceGroup() {
        OkGo.<String>post(Urls.GO_EDIT_DEVICE_TO_GROUP)
                .tag(this)
                .params("member_id", 12)
                .params("group_id", mGroupId)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();

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
                .params("member_id", 12)
                .params("group_id", mGroupId)
                .params("device_id", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();

                        mGson = new Gson();
                        mGson.fromJson(body, AddDeviceToGroupBean.class);
                    }
                });
    }

    @Override
    public void onClickLeft(View view) {
        int position = (Integer) view.getTag();
        leftAdapter.removeData(position);
        rightAdapter.addData(mPossess.size(), mList.get(position));
    }

    @Override
    public void onClickRight(View view) {
        int position = (Integer) view.getTag();
        rightAdapter.removeData(position);
        leftAdapter.addData(mList.size(), mPossess.get(position));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            case R.id.btn_device_save:
                // TODO: 18/5/20 未完成
                getDataWithAddDeviceToGroup();
                break;
            default:
                break;
        }
    }

}
