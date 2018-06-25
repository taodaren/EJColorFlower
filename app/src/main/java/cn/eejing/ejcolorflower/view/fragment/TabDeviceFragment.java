package cn.eejing.ejcolorflower.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import cn.eejing.ejcolorflower.model.event.DeviceEvent;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.LoginSession;
import cn.eejing.ejcolorflower.view.activity.AppActivity;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceMaterialStatus;
import cn.eejing.ejcolorflower.device.DeviceState;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.device.Protocol;
import cn.eejing.ejcolorflower.model.request.AddMaterialBean;
import cn.eejing.ejcolorflower.model.request.CancelMaterialStatusBean;
import cn.eejing.ejcolorflower.model.request.ChangeMaterialStatusBean;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.model.request.MaterialInfoBean;
import cn.eejing.ejcolorflower.view.activity.SignInActivity;
import cn.eejing.ejcolorflower.view.adapter.TabDeviceAdapter;
import cn.eejing.ejcolorflower.view.base.BaseFragment;
import cn.eejing.ejcolorflower.util.Settings;

import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_END_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_NO_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_WAIT_USED;

/**
 * 设备模块
 */

public class TabDeviceFragment extends BaseFragment {
    private static final String JL = "测试加料功能>>>>>>";

    @BindView(R.id.rv_tab_device)
    PullLoadMoreRecyclerView rvTabDevice;

    private Gson mGson;
    private List<DeviceListBean.DataBean.ListBean> mList;
    private TabDeviceAdapter mAdapter;
    private String mMemberId, mToken, mDeviceId;

    private DeviceState mState;
    private DeviceConfig mConfig;

    private OnFragmentInteractionListener mListener;
    private AppActivity.FireworksDeviceControl mDeviceControl;

    public interface OnRecvHandler {
        void onState(Device device, DeviceState state);

        void onConfig(DeviceConfig config);
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
    };

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
        mDeviceControl = AppActivity.getFireworksDeviceControl();

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceId(DeviceEvent deviceId) {
        mDeviceId = deviceId.getId();
        Log.i("TabDeviceFragment", "mDeviceId: " + mDeviceId);
    }

    /**
     * 在此处理加料逻辑
     *
     * @param qrMId 二维码扫描获取的材料 ID
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMaterialId(final String qrMId) {
        // 判断设备加料状态
        final byte[] pkg = Protocol.get_material_status(Long.parseLong(mDeviceId));
        mDeviceControl.sendCommand(Long.parseLong(mDeviceId), pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                DeviceMaterialStatus materialStatus = Protocol.parseMaterialStatus(pkg, pkg.length);
                long deviceMID = materialStatus.materialId;
                Log.e(JL, "【设备端】加料状态: " + materialStatus.exist);
                Log.e(JL, "【设备端】材料ID: " + deviceMID);
                switch (materialStatus.exist) {
                    // 如果设备端获取加料状态为无记录
                    case 0:
                        // 向服务端获取料包信息
                        byServerMaterialInfo_D(qrMId);
                        break;
                    // 如果设备端获取加料状态为已添加
                    case 1:
                        // 向服务端获取料包信息(料包已添加)
                        byServerMaterialInfo_E(deviceMID);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void timeout() {
                Log.i(JL, "获取状态超时");
            }
        });

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

    private void information(String info) {
        Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
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
                                information(getString(R.string.toast_login_fail));
                                startActivity(new Intent(getActivity(), SignInActivity.class));
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

    private void byServerMaterialInfo_D(final String materialId) {
        OkGo.<String>post(Urls.MATERIAL_INFO)
                .params("material_id", materialId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "material_info request succeeded--->" + body);

                        MaterialInfoBean bean = mGson.fromJson(body, MaterialInfoBean.class);
                        final int addTime = Integer.parseInt(bean.getData().getDuration());

                        switch (bean.getCode()) {
                            case 0:
                                Log.i(JL, "获取信息失败_D ！");
                                information(getString(R.string.toast_get_info_failed));
                                break;
                            case 1:
                                Log.i(JL, "获取信息成功_D ！");
                                Log.e(JL, "【服务器】物料使用状态_D：" + bean.getData().getUse_status());
                                switch (bean.getData().getUse_status()) {
                                    // 判断当前服务端状态
                                    case TYPE_NO_USED:
                                        Log.e(JL, "未使用状态_D");
                                        // 标记为待使用
                                        byServerWaitUseStatus(materialId, addTime);
                                        break;
                                    case TYPE_WAIT_USED:
                                        Log.e(JL, "待使用状态_D");
                                        // 提示被哪个设备绑定
                                        final String useDevice = bean.getData().getUse_device();
                                        Log.i(JL, "绑定设备: " + useDevice);
                                        Log.i(JL, "mDeviceId: " + mDeviceId);
                                        // 提示被哪个设备绑定
                                        if (useDevice.equals(mDeviceId)) {
                                            // 如果被本设备绑定
                                            Log.i(JL, "如果被本设备绑定，走到此处");

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("料包已被本设备绑定")
                                                    .setMessage("继续添加或取消绑定返回")
                                                    .setPositiveButton("继续添加", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // 获取时间戳
                                                            Log.i(JL, "开始获取时间戳...");
                                                            byDeviceGetTimestamps(materialId, addTime);
                                                        }
                                                    })
                                                    .setNegativeButton("取消绑定", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // 标记为未使用
                                                            byServerNoUseStatus(materialId);
                                                        }
                                                    }).show();
                                        } else {
                                            // 如果是其它设备，提示已被 ** 设备绑定不可使用
                                            information("料包已被 " + useDevice + " 绑定，不可使用！");
                                        }
                                        break;
                                    case TYPE_END_USED:
                                        Log.e(JL, "已使用状态_D");
                                        information(getString(R.string.toast_already_used));
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

    private void byServerNoUseStatus(String materialId) {
        OkGo.<String>post(Urls.NO_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDeviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "no_use_status request succeeded--->" + body);

                        CancelMaterialStatusBean bean = mGson.fromJson(body, CancelMaterialStatusBean.class);
                        switch (bean.getCode()) {
                            case 0:
                                information(getString(R.string.toast_cancel_bind_failed));
                                break;
                            case 1:
                                information(getString(R.string.toast_cancel_bind_success));
                                break;
                            case 6:
                                information(getString(R.string.toast_no_right_operation_pkg));
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void byServerMaterialInfo_E(final long deviceMID) {
        OkGo.<String>post(Urls.MATERIAL_INFO)
                .params("material_id", deviceMID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "added material_info request succeeded--->" + body);

                        MaterialInfoBean bean = mGson.fromJson(body, MaterialInfoBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                Log.i(JL, "获取信息失败_E ！");
                                information(getString(R.string.toast_get_info_failed));
                                // TODO: 2018/6/8  
                                // 清除加料信息（设备端）
                                byte[] info = Protocol.clear_material_info(Long.parseLong(mDeviceId), Long.parseLong(mMemberId), 274652232);
                                mDeviceControl.sendCommand(Long.parseLong(mDeviceId), info, new OnReceivePackage() {
                                    @Override
                                    public void ack(@NonNull byte[] pkg) {
                                        int info = Protocol.parseClearMaterialInfo(pkg, pkg.length);
                                        if (info == 0) {
                                            Log.e(JL, "ack: ");
                                        }
                                    }

                                    @Override
                                    public void timeout() {

                                    }
                                });
                                // TODO: 2018/6/8
                                break;
                            case 1:
                                Log.i(JL, "获取信息成功_E ！");
                                Log.e(JL, "【服务器】物料使用状态_E：" + bean.getData().getUse_status());

                                String serverMId = bean.getData().getMaterial_num();
                                Log.i(JL, "料包ID（from device）: " + deviceMID);
                                Log.i(JL, "料包ID（from server）: " + serverMId);
                                switch (bean.getData().getUse_status()) {
                                    // 判断当前服务端状态
                                    case TYPE_NO_USED:
                                        Log.e(JL, "未使用状态_E");
                                        Log.e(JL, "不应该存在该状态，若出现，检查代码逻辑或设备");
                                        break;
                                    case TYPE_WAIT_USED:
                                        Log.e(JL, "待使用状态_E");
                                        // 料包标记为已使用状态（服务器）
                                        byServerEndUseStatus_E(deviceMID, serverMId);
                                        break;
                                    case TYPE_END_USED:
                                        Log.e(JL, "已使用状态_E");
                                        // 清除加料信息（设备端）
                                        byDeviceClearInfo_E(deviceMID, serverMId);
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

    private void byServerWaitUseStatus(final String materialId, final int addTime) {
        OkGo.<String>post(Urls.WAIT_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDeviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "wait_use_status request succeeded--->" + body);

                        AddMaterialBean bean = mGson.fromJson(body, AddMaterialBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                information(getString(R.string.toast_add_material_failed));
                                break;
                            case 1:
                                // 料包已绑定,并已进入锁定状态
                                // 获取时间戳
                                Log.i(JL, "开始获取时间戳...");
                                byDeviceGetTimestamps(materialId, addTime);
                                break;
                            case 5:
                                information(getString(R.string.toast_no_right_operation));
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void byServerEndUseStatus_D(final String materialId) {
        OkGo.<String>post(Urls.END_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDeviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "end_use_status request succeeded--->" + body);

                        ChangeMaterialStatusBean bean = mGson.fromJson(body, ChangeMaterialStatusBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                // 状态修改成功
                                Log.i(JL, "状态已修改为已使用！");
                                information(getString(R.string.toast_add_material_success));
                                // 清除加料信息
                                byDeviceClearInfo_D(materialId);
                                break;
                            case 0:
                                Log.e(JL, "状态修改失败");
                                information(getString(R.string.toast_add_material_failed));
                                break;
                            case 5:
                                information(getString(R.string.toast_no_right_operation));
                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    private void byServerEndUseStatus_E(final long deviceMId, final String serverMId) {
        OkGo.<String>post(Urls.END_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", deviceMId)
                .params("device_id", mDeviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "end_use_status request succeeded--->" + body);

                        ChangeMaterialStatusBean bean = mGson.fromJson(body, ChangeMaterialStatusBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                // 状态修改成功
                                Log.i(JL, "状态已修改为已使用！");
                                // 清除加料信息
                                byDeviceClearInfo_E(deviceMId, serverMId);
                                break;
                            case 0:
                                Log.e(JL, "状态修改失败");
                                information(getString(R.string.toast_add_material_failed));
                                break;
                            case 5:
                                information(getString(R.string.toast_no_right_operation));
                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    private void byDeviceClearInfo_D(String materialId) {
        Log.i(JL, "清除加料信息参数: " + "\nmDeviceId" + mDeviceId + "\nmMemberId" + mMemberId + "\nmaterialId" + materialId);
        byte[] pkg = Protocol.clear_material_info(Long.parseLong(mDeviceId), Long.parseLong(mMemberId), Long.parseLong(materialId));
        mDeviceControl.sendCommand(Long.parseLong(mDeviceId), pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                int info = Protocol.parseClearMaterialInfo(pkg, pkg.length);
                Log.e(JL, "清除加料信息返回值-->" + info);
                switch (info) {
                    case 0:
                        Log.i(JL, "已清理加料信息");
                        break;
                    case 1:
                        Log.e(JL, "清除加料出错！！！");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void timeout() {
            }
        });
    }

    private void byDeviceClearInfo_E(final long deviceMId, final String serverMId) {
        Log.i(JL, "清除加料信息参数: " + "\nmDeviceId" + mDeviceId + "\nmMemberId" + mMemberId + "\ndeviceMId" + deviceMId + "\nserverMId" + serverMId);
        byte[] pkg = Protocol.clear_material_info(Long.parseLong(mDeviceId), Long.parseLong(mMemberId), deviceMId);
        mDeviceControl.sendCommand(Long.parseLong(mDeviceId), pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                int info = Protocol.parseClearMaterialInfo(pkg, pkg.length);
                Log.e(JL, "清除加料信息返回值-->" + info + "  " + pkg.length);
                switch (info) {
                    case 0:
                        Log.i(JL, "已清理加料信息");
                        // 获取此次料包信息
                        byServerMaterialInfo_D(serverMId);
                        break;
                    case 1:
                        Log.e(JL, "清除加料出错！！！");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void timeout() {
            }
        });
    }

    private void byDeviceGetTimestamps(final String materialId, final int addTime) {
        final long deviceId = Long.parseLong(mDeviceId);
        byte[] pkg = Protocol.get_timestamp_package(deviceId);

        mDeviceControl.sendCommand(deviceId, pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                long timestamp = Protocol.parseTimestamp(pkg, pkg.length);

                // 加料（设备端）
                Log.i(JL, "开始加料...");
                byDeviceAddMaterial(timestamp, deviceId, addTime, materialId);
            }

            @Override
            public void timeout() {
            }
        });
    }

    private void byDeviceAddMaterial(long timestamp, final long deviceId, int addTime, final String materialId) {
        final byte[] pkgAdd = Protocol.add_material(deviceId, addTime, timestamp, Long.parseLong(mMemberId), Long.parseLong(materialId));

        mDeviceControl.sendCommand(deviceId, pkgAdd, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                final int material = Protocol.parseAddMaterial(pkg, pkg.length);
                Log.i(JL, "加料结果--->" + material);
                switch (material) {
                    case 0:
                        // 加料成功，服务端标记为已使用
                        byServerEndUseStatus_D(materialId);
                        break;
                    case 1:
                        // 加料失败
                        information(getString(R.string.toast_add_material_failed));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void timeout() {
            }

        });
    }

}
