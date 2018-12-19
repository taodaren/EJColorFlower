package cn.eejing.colorflower.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lzy.okgo.model.HttpParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.model.device.Device;
import cn.eejing.colorflower.model.device.DeviceMaterialStatus;
import cn.eejing.colorflower.model.event.DevConnEvent;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.MaterialInfoBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.OnReceivePackage;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.BleDevProtocol;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.util.ViewFindUtils;
import cn.eejing.colorflower.view.adapter.ViewPagerAdapter;
import cn.eejing.colorflower.view.base.BaseActivityEvent;
import cn.eejing.colorflower.view.customize.SelfDialog;
import cn.eejing.colorflower.view.customize.SelfDialogBase;
import cn.eejing.colorflower.view.fragment.ConfigTempFragment;
import cn.eejing.colorflower.view.fragment.ConfigTimeFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.colorflower.app.AppConstant.APP_QR_GET_MID;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.colorflower.app.AppConstant.HANDLE_BLE_CONN;
import static cn.eejing.colorflower.app.AppConstant.HANDLE_BLE_DISCONN;
import static cn.eejing.colorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.colorflower.app.AppConstant.QR_DEV_MAC;
import static cn.eejing.colorflower.app.AppConstant.QR_MATERIAL_ID;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;
import static cn.eejing.colorflower.app.AppConstant.TYPE_END_USED;
import static cn.eejing.colorflower.app.AppConstant.TYPE_NO_USED;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_USED;

/**
 * 设备配置
 */

public class CtDevConfigActivity extends BaseActivityEvent implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.img_ble_toolbar)         ImageView    imgBleToolbar;
    @BindView(R.id.layout_dmx_set)          LinearLayout dmxSet;
    @BindView(R.id.tv_dmx_show)             TextView     tvDmxShow;

    private static final String TAG = "CtDevConfigActivity";
    private static final String JL = "加料";

    private String[] mTitles = {"温度", "时间"};
    private SegmentTabLayout mTabLayout;
    private View mDecorView;

    private BaseApplication mApp;
    private List<Fragment> mFragments;
    private ViewPager mVPager;
    private int mPageType;
    private SelfDialog mDialogDmx;
    private SelfDialogBase mDialogBack;
    private long mMemberId;

    private int mDMXAddress;
    private int mTemperature;
    private int mRestTime;

    private ConfigTempFragment mTempFragment = ConfigTempFragment.newInstance(mTemperature);
    private ConfigTimeFragment mTimeFragment = ConfigTimeFragment.newInstance(mRestTime);

    // 是否可以进入主控模式
    private boolean isEnterMasterCtrl;
    private long mDevId;
    private String mDevMac;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_dev_config;
    }

    @Override
    public void initView() {
        mApp = (BaseApplication) getApplication();
        setToolbar("设备配置", View.VISIBLE, null, View.GONE);

        mMemberId = MySettings.getLoginInfo(this).getUserId();
        mDevId = getIntent().getLongExtra(QR_DEV_ID, 0);
        mDevMac = getIntent().getStringExtra(QR_DEV_MAC);
        Log.i(TAG, "设备信息: " + mDevId + " " + mDevMac);

        // 如果 DMX 为 0，true；反之 false
        isEnterMasterCtrl = mDMXAddress == 0;

        mFragments = new ArrayList<>();
        mFragments.add(mTempFragment);
        mFragments.add(mTimeFragment);

        mDecorView = getWindow().getDecorView();
        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tl_device_del);
        mPageType = getIntent().getIntExtra("page", 0);

        initTLVP();
    }

    @Override
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        super.setToolbar(title, titleVisibility, menu, menuVisibility);
        imgBleToolbar.setVisibility(View.VISIBLE);
        imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_desconn));
        // 设置返回按钮
        ImageView imgBack = findViewById(R.id.img_back_toolbar);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(v -> showDialogByBack());
    }

    @Override
    public void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @OnClick({R.id.img_back_toolbar, R.id.layout_dmx_set, R.id.btn_add_material, R.id.btn_enter_single, R.id.btn_enter_master})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_dmx_set:
                showDialogByDmx();
                break;
            case R.id.btn_add_material:
                mApp.setFlagQrCode(APP_QR_GET_MID);
                startActivityForResult(new Intent(this, CtQrScanActivity.class), 1);
                break;
            case R.id.btn_enter_single:
                if (!isEnterMasterCtrl) {
                    jumpToActivity(new Intent(this, CtSingleModeActivity.class));
                } else {
                    ToastUtil.showShort("DMX不为0方可进入单台控制模式");
                }
                break;
            case R.id.btn_enter_master:
                if (isEnterMasterCtrl) {
                    jumpToActivity(new Intent(this, CtMasterModeActivity.class));
                } else {
                    ToastUtil.showShort("DMX为0方可进入多台控制模式");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    long qrMid = data.getLongExtra(QR_MATERIAL_ID, 0);
                    Log.d(TAG, "onActivityResult dev id: " + qrMid);
                    // 处理加料逻辑
                    cmdGetAddMaterialStatus(qrMid, 3);
                }
                break;
            default:
                break;
        }
    }

    /** 判断设备加料状态 */
    private void cmdGetAddMaterialStatus(final long qrMid, final int resendNum) {
        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDevProtocol.pkgGetAddMaterialStatus(mDevId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        DeviceMaterialStatus addMaterialStatus = BleDevProtocol.parseAddMaterialStatus(pkg, pkg.length);
                        assert addMaterialStatus != null;
                        long devMid = addMaterialStatus.getMaterialId();
                        Log.i(JL, "加料状态 " + addMaterialStatus.getExist());
                        Log.i(JL, "devMid " + devMid);
                        Log.i(JL, "qrMid " + qrMid);
                        switch (addMaterialStatus.getExist()) {
                            // 如果设备端获取加料状态为无记录
                            case 0:
                                // 向服务端获取料包信息
                                byServerMaterialInfo_D(qrMid);
                                break;
                            // 如果设备端获取加料状态为已添加
                            case 1:
                                // 向服务端获取料包信息(料包已添加)
                                byServerMaterialInfo_E(devMid);
                                break;
                        }
                    }

                    @Override
                    public void timeout() {
                        Log.e(TAG, "timeout");
                        if (resendNum != 0) {
                            cmdGetAddMaterialStatus(qrMid, resendNum - 1);
                        } else {
                            ToastUtil.showShort("加料失败，请重新加料");
                        }
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private void byServerMaterialInfo_D(final long materialId) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("material_id", materialId);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.GET_MATERIAL_INFO)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(MaterialInfoBean.class)
                .callback(new Callback<MaterialInfoBean>() {
                    @Override
                    public void onSuccess(MaterialInfoBean bean, int id) {
                        Log.d(TAG, "获取料包信息 请求成功");

                        final int addTime = Integer.parseInt(bean.getData().getDuration());

                        switch (bean.getCode()) {
                            case 1:
                                Log.i(JL, "获取信息成功_D ！");
                                Log.e(JL, "【服务器】物料使用状态_D：" + bean.getData().getStatus());
                                switch (bean.getData().getStatus()) {
                                    // 判断当前服务端状态
                                    case TYPE_NO_USED:
                                        Log.w(JL, "未使用状态_D");
                                        // 标记为待使用
                                        byServerWaitUseStatus(materialId, addTime);
                                        break;
                                    case TYPE_WAIT_USED:
                                        Log.w(JL, "待使用状态_D");
                                        // 获取时间戳
                                        Log.i(JL, "开始获取时间戳...");
                                        cmdGetTimestamps(materialId, addTime, 3);
                                        break;
                                    case TYPE_END_USED:
                                        LogUtil.w(JL, "已使用状态_D");
                                        ToastUtil.showShort(getString(R.string.toast_already_used));
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            default:
                                Log.i(JL, bean.getMessage() + "_D ！");
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    @SuppressWarnings("unchecked")
    private void byServerMaterialInfo_E(final long deviceMID) {
        LogUtil.i(JL, "byServerMaterialInfo_E: " + mDevId + " " + deviceMID + " " + mMemberId);
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("material_id", deviceMID);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.GET_MATERIAL_INFO)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(MaterialInfoBean.class)
                .callback(new Callback<MaterialInfoBean>() {
                    @Override
                    public void onSuccess(MaterialInfoBean bean, int id) {
                        Log.d(TAG, "获取料包信息 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                LogUtil.i(JL, "获取信息成功_E ！");
                                LogUtil.d(JL, "【服务器】物料使用状态_E：" + bean.getData().getStatus());

                                long serverMId = bean.getData().getId();
                                LogUtil.i(JL, "料包ID（from device）: " + deviceMID);
                                LogUtil.i(JL, "料包ID（from server）: " + serverMId);
                                switch (bean.getData().getStatus()) {
                                    // 判断当前服务端状态
                                    case TYPE_WAIT_USED:
                                        LogUtil.w(JL, "待使用状态_E");
                                        // 料包标记为已使用状态（服务器）
                                        byServerEndUseStatus_E(deviceMID, serverMId);
                                        break;
                                    case TYPE_END_USED:
                                        LogUtil.w(JL, "已使用状态_E");
                                        // 清除加料信息（设备端）
                                        cmdClearAddMaterialInfo_E(deviceMID, serverMId, 3);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            default:
                                Log.i(JL, bean.getMessage() + "_E ！");
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    @SuppressWarnings("unchecked")
    private void byServerEndUseStatus_E(final long deviceMId, final long serverMId) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("material_id", deviceMId);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.END_USE_STATUS)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "修改为已使用状态 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                // 状态修改成功
                                LogUtil.i(JL, "状态已修改为已使用！");
                                // 清除加料信息
                                cmdClearAddMaterialInfo_E(deviceMId, serverMId, 3);
                                break;
                            default:
                                LogUtil.e(JL, bean.getMessage());
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    private void cmdClearAddMaterialInfo_E(final long deviceMId, final long serverMId, final int resendNum) {
        LogUtil.i(JL, "清除加料信息参数: " + "mDevId--" + mDevId + " mMemberId--" + mMemberId + " deviceMId--" + deviceMId + " serverMId--" + serverMId);

        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDevProtocol.pkgClearAddMaterialInfo(mDevId, mMemberId, deviceMId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        int info = BleDevProtocol.parseClearAddMaterialInfo(pkg, pkg.length);
                        LogUtil.e(JL, "清除加料信息返回值: " + info + "  " + pkg.length);
                        switch (info) {
                            case 0:
                                LogUtil.i(JL, "已清理加料信息");
                                // 获取此次料包信息
                                byServerMaterialInfo_D(serverMId);
                                break;
                            case 1:
                                LogUtil.e(JL, "清除加料出错！！！");
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void timeout() {
                        LogUtil.e(TAG, "timeout");
                        if (resendNum != 0) {
                            cmdClearAddMaterialInfo_E(deviceMId, serverMId, resendNum - 1);
                        } else {
                            ToastUtil.showShort("清除加料信息失败");
                        }
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private void byServerWaitUseStatus(final long materialId, final int addTime) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("material_id", materialId);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.WAIT_USE_STATUS)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "修改为待使用状态 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                // 料包已绑定,并已进入即将使用(锁定)状态
                                LogUtil.i(JL, "开始获取时间戳...");
                                cmdGetTimestamps(materialId, addTime, 3);
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    private void cmdGetTimestamps(final long materialId, final int addTime, final int resendNum) {
        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDevProtocol.pkgGetTimestamp(mDevId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        long timestamp = BleDevProtocol.parseGetTimestamp(pkg, pkg.length);

                        LogUtil.i(JL, "开始加料...");
                        cmdAddMaterial(timestamp, mDevId, addTime, materialId, 3);
                    }

                    @Override
                    public void timeout() {
                        LogUtil.e(TAG, "timeout");
                        if (resendNum != 0) {
                            cmdGetTimestamps(materialId, addTime, resendNum - 1);
                        } else {
                            ToastUtil.showShort("获取时间戳失败");
                        }
                    }
                }
        );
    }

    private void cmdAddMaterial(final long timestamp, final long deviceId, final int addTime, final long materialId, final int resendNum) {
        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDevProtocol.pkgAddMaterial(deviceId, addTime, timestamp, mMemberId, materialId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        final int material = BleDevProtocol.parseAddMaterial(pkg, pkg.length);
                        LogUtil.i(JL, "加料结果: " + material);
                        switch (material) {
                            case 0:
                                // 加料成功，服务端标记为已使用
                                byServerEndUseStatus_D(materialId);
                                break;
                            case 1:
                                // 加料失败
                                ToastUtil.showShort(getString(R.string.toast_add_material_failed));
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void timeout() {
                        LogUtil.w(TAG, "cmdAddMaterial timeout");
                        if (resendNum != 0) {
                            cmdAddMaterial(timestamp, deviceId, addTime, materialId, resendNum - 1);
                        } else {
                            ToastUtil.showShort("加料失败");
                        }
                    }
                }
        );
    }

    @SuppressWarnings("unchecked")
    private void byServerEndUseStatus_D(final long materialId) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("material_id", materialId);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.END_USE_STATUS)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "修改为已使用状态 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                // 状态修改成功
                                LogUtil.i(JL, "状态已修改为已使用！");
                                ToastUtil.showShort(getString(R.string.toast_add_material_success));
                                // 清除加料信息
                                cmdClearAddMaterialInfo_D(materialId, 3);
                                break;
                            default:
                                LogUtil.e(JL, bean.getMessage());
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    private void cmdClearAddMaterialInfo_D(final long materialId, final int resendNum) {
        LogUtil.i(JL, "清除加料信息参数: " + "mDevId--" + mDevId + " mMemberId--" + mMemberId + " materialId--" + materialId);

        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDevProtocol.pkgClearAddMaterialInfo(mDevId, mMemberId, materialId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        int info = BleDevProtocol.parseClearAddMaterialInfo(pkg, pkg.length);
                        LogUtil.w(JL, "清除加料信息返回值: " + info);
                        switch (info) {
                            case 0:
                                LogUtil.i(JL, "已清理加料信息");
                                break;
                            case 1:
                                LogUtil.e(JL, "清除加料出错！！！");
                                break;
                        }
                    }

                    @Override
                    public void timeout() {
                        LogUtil.e(JL, "清除加料信息超时！");
                        if (resendNum != 0) {
                            cmdClearAddMaterialInfo_D(materialId, resendNum - 1);
                        } else {
                            ToastUtil.showShort("清除加料信息失败");
                        }
                    }
                }
        );
    }

    private void initTLVP() {
        mVPager = ViewFindUtils.find(mDecorView, R.id.vp_device);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mVPager.setAdapter(adapter);

        // 设置 TabLayout 数据
        mTabLayout.setTabData(mTitles);
        // 设置 TabLayout 选择侦听器
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mVPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        // 添加页面更改侦听器
        mVPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // 在滚动页
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            // 在选择的页面上
            @Override
            public void onPageSelected(int position) {
                // 设置当前标签
                mTabLayout.setCurrentTab(position);
            }

            // 在页面滚动状态改变
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // 设置预加载页面个数
        mVPager.setOffscreenPageLimit(0);
        // 设置默认选中页面
        mVPager.setCurrentItem(mPageType);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_BLE_CONN:
                    imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_conn));
                    tvDmxShow.setText("DMX " + String.valueOf(mDMXAddress));
                    isEnterMasterCtrl = mDMXAddress == 0;
                    break;
                case HANDLE_BLE_DISCONN:
                    imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_desconn));
                    tvDmxShow.setText("DMX地址");
                    isEnterMasterCtrl = mDMXAddress == 0;
                    showDialogByDisconnect(CtDevConfigActivity.this);
                    break;
            }
        }
    };

    @Override
    public void onEventBleConn(DevConnEvent event) {
        super.onEventBleConn(event);
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        if (event.getStatus() != null) {
            LogUtil.i(TAG, "dev cfg event: " + event.getMac() + " | " + event.getId() + " | " + event.getStatus());

            switch (event.getStatus()) {
                case DEVICE_CONNECT_YES:
                    mDMXAddress = event.getDeviceConfig().getDMXAddress();
                    mTemperature = event.getDeviceStatus().getTemperature();
                    mRestTime = event.getDeviceStatus().getRestTime();
                    mHandler.sendEmptyMessage(HANDLE_BLE_CONN);
                    break;
                case DEVICE_CONNECT_NO:
                    mDMXAddress = -1;
                    mTemperature = -1;
                    mRestTime = -1;
                    mHandler.sendEmptyMessage(HANDLE_BLE_DISCONN);
                    break;
            }
        }
    }

    private void updateDmx(int niDmx) {
        Device device = MainActivity.getAppCtrl().getDevice(mDevMac);
        MainActivity.getAppCtrl().sendCommand(device, BleDevProtocol.pkgSetDmxAddress(mDevId, niDmx), new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                if (pkg.length > 8 && pkg[7] == 0) {
                    LogUtil.i(TAG, "配置 DMX 回复成功");
                    MainActivity.getAppCtrl().getDeviceConfig(mDevMac);
                    mDialogDmx.dismiss();
                } else {
                    LogUtil.i(TAG, "配置 DMX 回复失败");
                }
            }

            @Override
            public void timeout() {
            }
        });
    }

    /** 修改 DMX Dialog */
    private void showDialogByDmx() {
        mDialogDmx = new SelfDialog(this);
        mDialogDmx.setEtInputType(InputType.TYPE_CLASS_NUMBER);
        mDialogDmx.setTitle("修改设备 DMX 地址");
        mDialogDmx.setMessage("设置 DMX 地址和取值范围0~510");
        mDialogDmx.setYesOnclickListener("确定", () -> {
            if (!(mDialogDmx.getEditTextStr().equals(""))) {
                try {
                    final int niDmx = Integer.parseInt(mDialogDmx.getEditTextStr());
                    if (!(niDmx >= 0 && niDmx <= 510)) {
                        // 如果输入的 DMX 不在 1~511 之间，提示用户
                        ToastUtil.showShort("DMX超出范围，请重新设置");
                        mDialogDmx.dismiss();
                    } else if (niDmx % 2 != 0) {
                        // 若输入奇数，自动转换为偶数，并提示用户
                        updateDmx(niDmx + 1);
                        ToastUtil.showShort("DMX只能为偶数，已为您转为偶数");
                        mDialogDmx.dismiss();
                    } else {
                        // 更新 DMX 地址
                        updateDmx(niDmx);
                        mDialogDmx.dismiss();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // 还有不按规矩出牌的？有！
                    ToastUtil.showShort("请设置正确的 DMX 地址");
                    mDialogDmx.dismiss();
                }
            } else {
                ToastUtil.showShort("未更新 DMX 地址");
                mDialogDmx.dismiss();
            }
        });
        mDialogDmx.setNoOnclickListener("取消", () -> mDialogDmx.dismiss());
        mDialogDmx.show();
    }

    /** 返回 Dialog */
    public void showDialogByBack() {
        mDialogBack = new SelfDialogBase(this);
        mDialogBack.setTitle("返回将断开设备，确定返回吗？");
        mDialogBack.setYesOnclickListener("确定", () -> {
            MainActivity.getAppCtrl().disconnectDevice(mDevMac);
            finish();
            mDialogBack.dismiss();
        });
        mDialogBack.setNoOnclickListener("取消", () -> mDialogBack.dismiss());
        mDialogBack.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showDialogByBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
}
