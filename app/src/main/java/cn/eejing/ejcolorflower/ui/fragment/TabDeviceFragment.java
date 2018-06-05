package cn.eejing.ejcolorflower.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.DeviceEvent;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.LoginSession;
import cn.eejing.ejcolorflower.app.MainActivity;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceMaterialStatus;
import cn.eejing.ejcolorflower.device.DeviceState;
import cn.eejing.ejcolorflower.device.OnReceivePackage;
import cn.eejing.ejcolorflower.device.Protocol;
import cn.eejing.ejcolorflower.model.request.AddMaterialBean;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.model.request.MaterialInfoBean;
import cn.eejing.ejcolorflower.ui.activity.LoginActivity;
import cn.eejing.ejcolorflower.ui.adapter.TabDeviceAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;
import cn.eejing.ejcolorflower.util.Settings;

import static android.support.constraint.Constraints.TAG;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_ALREADY_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_TO_BE_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_UN_USED;

/**
 * @创建者 Taodaren
 * @描述 设备模块
 */

public class TabDeviceFragment extends BaseFragment {

    @BindView(R.id.rv_tab_device)
    PullLoadMoreRecyclerView rvTabDevice;

    private Gson mGson;
    private List<DeviceListBean.DataBean.ListBean> mList;
    private TabDeviceAdapter mAdapter;
    private String mMemberId, mToken, mDeviceId;

    private DeviceState mState;
    private DeviceConfig mConfig;
    private DeviceMaterialStatus mMaterialStatus;

    private OnFragmentInteractionListener mListener;
    private MainActivity.FireworksDeviceControl mDeviceControl;

    public interface OnRecvHandler {
        void onState(Device device, DeviceState state);

        void onConfig(DeviceConfig config);

        void onMaterialStatus(Device device, DeviceMaterialStatus materialStatus);
    }

    public interface OnFragmentInteractionListener {
        void scanDevice();

        void setRegisterDevice(List<DeviceListBean.DataBean.ListBean> list);

        void setRecvHandler(OnRecvHandler handler);
    }

    public static TabDeviceFragment newInstance() {
        return new TabDeviceFragment();
    }

    public TabDeviceFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_device;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.device_name, View.VISIBLE);
    }

    @Override
    public void initView(View rootView) {
        EventBus.getDefault().register(this);
        mGson = new Gson();
        mList = new ArrayList<>();
        mDeviceControl = MainActivity.getFireworksDeviceControl();

        LoginSession session = Settings.getLoginSessionInfo(getActivity());
        mMemberId = String.valueOf(session.getMember_id());
        mToken = session.getToken();

        initRecyclerView();
    }

    @Override
    public void initData() {
        getDataWithDeviceList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mState = new DeviceState();
            mConfig = new DeviceConfig();
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "必须实现 OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private final OnRecvHandler mOnRecvHandler = new OnRecvHandler() {
        @Override
        public void onState(Device device, DeviceState state) {
            mState = state;
            mAdapter.setDeviceState(device, mState);
        }

        @Override
        public void onConfig(DeviceConfig config) {
            mConfig = config;
            mAdapter.setDeviceConfig(mConfig);
        }

        @Override
        public void onMaterialStatus(Device device, DeviceMaterialStatus materialStatus) {
            Log.i(AppConstant.TAG, "onMaterialStatus: " + materialStatus.exist);
            mMaterialStatus = materialStatus;
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceId(DeviceEvent deviceId) {
        mDeviceId = deviceId.getId();
        Log.i("TabDeviceFragment", "mDeviceId: " + mDeviceId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMaterialId(String materialId) {
        // 在此处理加料逻辑
        Log.i("TAG", "加料状态: " + mMaterialStatus.exist);
        if (mMaterialStatus.exist == 0) {
            // 如果设备端获取加料状态为无记录
            getDataWithMaterialInfo(materialId);
        }
        // 如果设备端获取加料状态为已添加

    }

    private void initRecyclerView() {
        Log.i(AppConstant.TAG, "initRecyclerView State : " + mState.mRestTime);
        Log.i(AppConstant.TAG, "initRecyclerView Config : " + mConfig.mDMXAddress);
        // 设置布局
        rvTabDevice.setLinearLayout();
        // 绑定适配器
        mAdapter = new TabDeviceAdapter(getContext(), mList, mMemberId, mToken);
        mListener.setRecvHandler(mOnRecvHandler);
        rvTabDevice.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvTabDevice.setPushRefreshEnable(false);
        rvTabDevice.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mListener.scanDevice();
                getDataWithDeviceList();
            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvTabDevice.setPullLoadMoreCompleted();
    }

    private void getDataWithDeviceList() {
        OkGo.<String>post(Urls.DEVICE_LIST)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "device list request succeeded--->" + body);

                        DeviceListBean bean = mGson.fromJson(body, DeviceListBean.class);
                        switch (bean.getCode()) {
                            case 101:
                            case 102:
                                Toast.makeText(getActivity().getBaseContext(), "登录失效，请重新登录", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                                break;
                            case 0:
                                // 若返回码为 0 ，表示暂无设备
                                rvTabDevice.setPullLoadMoreCompleted();
                                return;
                            case 1:
                                mList = bean.getData().getList();
                                Log.i(AppConstant.TAG, "onSuccess: get device group list--->" + mList.size());
                                // 注册设备
                                mListener.setRegisterDevice(mList);
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvTabDevice.setPullLoadMoreCompleted();
                                break;
                            default:
                        }
                    }
                });
    }

    private void getDataWithMaterialInfo(final String materialId) {
        Log.i(TAG, "getDataWithMaterialInfo: materialId--->" + materialId);
        OkGo.<String>post(Urls.MATERIAL_INFO)
                .params("material_id", materialId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "material_info request succeeded--->" + body);

                        MaterialInfoBean bean = mGson.fromJson(body, MaterialInfoBean.class);
                        int addTime = Integer.parseInt(bean.getData().getDuration());

                        switch (bean.getCode()) {
                            case 0:
                                Toast.makeText(getContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                break;
                            case 1:
                                Toast.makeText(getContext(), "获取信息成功", Toast.LENGTH_SHORT).show();
                                Log.i("TAG", "onSuccess: Use status--->" + bean.getData().getUse_status());
                                switch (bean.getData().getUse_status()) {
                                    // 判断当前服务端状态
                                    case TYPE_UN_USED:// 未使用
                                        // 标记为待使用
                                        getDataWithAddMaterial(materialId, addTime);
                                        break;
                                    case TYPE_TO_BE_USED:// 待使用
                                        Log.e(AppConstant.TAG, "待使用状态");
                                        break;
                                    case TYPE_ALREADY_USED:// 已使用
                                        Log.e(AppConstant.TAG, "已使用状态");
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            default:
                                break;
                        }

                    }
                });
    }

    private void getDataWithAddMaterial(final String materialId, final int addTime) {
        OkGo.<String>post(Urls.ADD_MATERIAL)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDeviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "add_material request succeeded--->" + body);

                        AddMaterialBean bean = mGson.fromJson(body, AddMaterialBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                Toast.makeText(getContext(), "设备添料失败", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                break;
                            case 1:
                                // 料包已绑定,并已进入锁定状态
                                // 获取时间戳
                                Log.e("TAG", "开始获取时间戳...");
                                setDeviceWithgetTimestamps(materialId, addTime);
                                break;
                            case 5:
                                Toast.makeText(getContext(), "您无权对该设备进行操作", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void setDeviceWithgetTimestamps(final String materialId, final int addTime) {
        final long deviceId = Long.parseLong(mDeviceId);
        byte[] pkg = Protocol.get_timestamp_package(deviceId);

        mDeviceControl.sendCommand(deviceId, pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                long timestamp = Protocol.parseTimestamp(pkg, pkg.length);
                Log.e("TAG", "开始加料...");

                // 加料（设备端）
                setDeviceWithAddMaterial(timestamp, deviceId, addTime, materialId);
            }

            @Override
            public void timeout() {
            }
        });
    }

    private void setDeviceWithAddMaterial(long timestamp, long deviceId, int addTime, String materialId) {
        Log.i(TAG, "\n时间戳--->" + timestamp + "\n设备id--->" + deviceId + "\n加料时间--->" + addTime + "\n料包ID--->" + materialId);

        byte[] pkgAddMaterial = Protocol.add_material(deviceId, addTime, timestamp, Long.parseLong(mMemberId), Long.parseLong(materialId));
        mDeviceControl.sendCommand(deviceId, pkgAddMaterial);
        Log.i(TAG, "发送命令结束");
    }

}
