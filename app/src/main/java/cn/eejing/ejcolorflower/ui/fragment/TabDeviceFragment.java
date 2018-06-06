package cn.eejing.ejcolorflower.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import cn.eejing.ejcolorflower.model.request.ChangeMaterialStatusBean;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.model.request.MaterialInfoBean;
import cn.eejing.ejcolorflower.ui.activity.LoginActivity;
import cn.eejing.ejcolorflower.ui.adapter.TabDeviceAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;
import cn.eejing.ejcolorflower.util.Settings;

import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_ALREADY_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_TO_BE_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_UN_USED;

/**
 * @创建者 Taodaren
 * @描述 设备模块
 */

public class TabDeviceFragment extends BaseFragment {
    private static final String JL = "测试加料功能 >>>>>>";

    @BindView(R.id.rv_tab_device)
    PullLoadMoreRecyclerView rvTabDevice;

    private Gson mGson;
    private List<DeviceListBean.DataBean.ListBean> mList;
    private TabDeviceAdapter mAdapter;
    private String mMemberId, mToken, mDeviceId;

    private DeviceState mState;
    private DeviceConfig mConfig;

    private OnFragmentInteractionListener mListener;
    private MainActivity.FireworksDeviceControl mDeviceControl;

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceId(DeviceEvent deviceId) {
        mDeviceId = deviceId.getId();
        Log.i("TabDeviceFragment", "mDeviceId: " + mDeviceId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMaterialId(final String materialId) {
        // 在此处理加料逻辑

        // 判断设备加料状态
        final byte[] pkg = Protocol.get_material_status(Long.parseLong(mDeviceId));
        mDeviceControl.sendCommand(Long.parseLong(mDeviceId), pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                DeviceMaterialStatus materialStatus = Protocol.parseMaterialStatus(pkg, pkg.length);
                Log.e(JL, "【设备端】加料状态: " + materialStatus.exist);
                switch (materialStatus.exist) {
                    // 如果设备端获取加料状态为无记录
                    case 0:
                        // 向服务端获取料包信息
                        getDataWithMaterialInfo(materialId);
                        break;
                    // 如果设备端获取加料状态为已添加
                    case 1:
                        // 向服务端获取料包信息(料包已添加)
                        getDataWithMaterialInfoAdded(materialId);
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
                                Log.i(JL, "获取信息成功（设备无记录情况）！！！" + bean.getData().getUse_status());
                                Log.e(JL, "【服务器】物料使用状态（设备无记录情况）：" + bean.getData().getUse_status());
                                switch (bean.getData().getUse_status()) {
                                    // 判断当前服务端状态
                                    case TYPE_UN_USED:
                                        Log.e(JL, "未使用状态");
                                        // 标记为待使用
                                        getDataWithAddMaterial(materialId, addTime);
                                        break;
                                    case TYPE_TO_BE_USED:
                                        Log.e(JL, "待使用状态");
                                        // 提示被哪个设备绑定
                                        Log.i(JL, "被哪个设备绑定: " + bean.getData().getUse_device());
                                        break;
                                    case TYPE_ALREADY_USED:
                                        Log.e(JL, "已使用状态");
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


    private void getDataWithMaterialInfoAdded(final String materialId) {
        OkGo.<String>post(Urls.MATERIAL_INFO)
                .params("material_id", materialId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "added material_info request succeeded--->" + body);

                        MaterialInfoBean bean = mGson.fromJson(body, MaterialInfoBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                Toast.makeText(getContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Log.i(JL, "获取信息成功（设备有记录情况）！！！" + bean.getData().getUse_status());
                                Log.e(JL, "【服务器】物料使用状态（设备有记录情况）：" + bean.getData().getUse_status());
                                switch (bean.getData().getUse_status()) {
                                    // 判断当前服务端状态
                                    case TYPE_UN_USED:
                                        Log.e(JL, "未使用状态");
                                        Log.e(JL, "不应该存在该状态，若出现，检查代码逻辑或设备");
                                        // TODO: 18/6/7 标记为待使用 检查逻辑 不应该存在该状态
                                        getDataWithAddMaterial(materialId, Integer.parseInt(bean.getData().getDuration()));
                                        break;
                                    case TYPE_TO_BE_USED:
                                        Log.e(JL, "待使用状态");
                                        // 料包标记为已使用状态（服务器）
                                        getDataWithChangMaterialStatus(materialId);
                                        // 获取此次料包信息
                                        getDataWithMaterialInfo(materialId);
                                        break;
                                    case TYPE_ALREADY_USED:// 已使用
                                        Log.e(JL, "已使用状态");
                                        // 清除加料信息（设备端）
                                        setDeviceWithClearInfo(materialId);
                                        // 获取此次料包信息
                                        getDataWithMaterialInfo(materialId);
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
                                information(getString(R.string.toast_add_material_failed));
                                break;
                            case 1:
                                // 料包已绑定,并已进入锁定状态
                                // 获取时间戳
                                Log.i(JL, "开始获取时间戳...");
                                setDeviceWithGetTimestamps(materialId, addTime);
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

    private void getDataWithChangMaterialStatus(final String materialId) {
        OkGo.<String>post(Urls.CHANGE_MATERIAL_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDeviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "change_material_status request succeeded--->" + body);

                        ChangeMaterialStatusBean bean = mGson.fromJson(body, ChangeMaterialStatusBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                // 状态修改成功
                                Log.i(JL, "状态已修改为已使用！");
                                // 清除加料信息
                                setDeviceWithClearInfo(materialId);
                                break;
                            case 0:
                                Snackbar.make(getView(), "状态修改失败", Snackbar.LENGTH_SHORT)
                                        .setAction("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        })
                                        .show();
//                                Toast.makeText(getContext(), "状态修改失败", Toast.LENGTH_SHORT).show();
                                break;
                            case 5:
                                Snackbar.make(getView(), "您无权对该设备进行操作", Snackbar.LENGTH_SHORT)
                                        .setAction("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        })
                                        .show();
//                                Toast.makeText(getContext(), "您无权对该设备进行操作", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    private void setDeviceWithClearInfo(String materialId) {
        byte[] pkg = Protocol.clear_material_info(Long.parseLong(mDeviceId), Long.parseLong(mMemberId), Long.parseLong(materialId));
        mDeviceControl.sendCommand(Long.parseLong(mDeviceId), pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                int info = Protocol.parseClearMaterialInfo(pkg, pkg.length);
                Log.e(JL, "清除加料信息返回值-->" + info);
                if (info == -1) {
                    Log.e(JL, "清除加料出错！！！");
                } else {
                    Log.i(JL, "已清理加料信息");
                }
            }

            @Override
            public void timeout() {
            }
        });
    }

    private void setDeviceWithGetTimestamps(final String materialId, final int addTime) {
        final long deviceId = Long.parseLong(mDeviceId);
        byte[] pkg = Protocol.get_timestamp_package(deviceId);

        mDeviceControl.sendCommand(deviceId, pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                long timestamp = Protocol.parseTimestamp(pkg, pkg.length);

                Log.i(JL, "开始加料...");
                // 加料（设备端）
                setDeviceWithAddMaterial(timestamp, deviceId, addTime, materialId);
            }

            @Override
            public void timeout() {
            }
        });
    }

    private void setDeviceWithAddMaterial(long timestamp, final long deviceId, int addTime, final String materialId) {
        Log.i(JL, "加料设备所需参数" + "\n时间戳--->" + timestamp + "\n设备 ID--->" + deviceId + "\n加料时间--->" + addTime + "\n料包 ID--->" + materialId);
        final byte[] pkgAdd = Protocol.add_material(deviceId, addTime, timestamp, Long.parseLong(mMemberId), Long.parseLong(materialId));

        mDeviceControl.sendCommand(deviceId, pkgAdd, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                final int material = Protocol.parseAddMaterial(pkg, pkg.length);
                Log.i(JL, "加料结果--->" + material);
                switch (material) {
                    case 0:
                        // 加料成功，服务端标记为已使用
                        getDataWithChangMaterialStatus(materialId);
                        information(getString(R.string.toast_add_material_success));
                        break;
                    case 1:
                        // 加料失败
                        for (int i = 0; i < 3; i++) {
                            // 重新发送三次
                            resend(material);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void timeout() {

            }

            private void resend(final int material) {
                mDeviceControl.sendCommand(deviceId, pkgAdd, new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        switch (material) {
                            case 0:
                                // 加料成功，服务端标记为已使用
                                getDataWithChangMaterialStatus(materialId);
                                information(getString(R.string.toast_add_material_success));
                                break;
                            case 1:
                                // 再失败？我不信了！
                                break;
                        }
                    }

                    @Override
                    public void timeout() {

                    }
                });
            }

        });
    }

}
