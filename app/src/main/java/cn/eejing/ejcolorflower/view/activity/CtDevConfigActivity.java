package cn.eejing.ejcolorflower.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.GApp;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceMaterialStatus;
import cn.eejing.ejcolorflower.model.event.DevConnEvent;
import cn.eejing.ejcolorflower.model.request.AddMaterialBean;
import cn.eejing.ejcolorflower.model.request.CancelMaterialStatusBean;
import cn.eejing.ejcolorflower.model.request.ChangeMaterialStatusBean;
import cn.eejing.ejcolorflower.model.request.MaterialInfoBean;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.SelfDialog;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.util.ViewFindUtils;
import cn.eejing.ejcolorflower.view.adapter.ViewPagerAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.view.fragment.ConfigTempFragment;
import cn.eejing.ejcolorflower.view.fragment.ConfigTimeFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.APP_QR_GET_MID;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_MAC;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_MATERIAL_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_END_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_NO_USED;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_WAIT_USED;

public class CtDevConfigActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "CtDevConfigActivity";
    private static final String JL = "about_add_material";

    @BindView(R.id.img_ble_toolbar)         ImageView imgBleToolbar;
    @BindView(R.id.btn_add_material)        Button btnAddMaterial;
    @BindView(R.id.btn_enter_master)        Button btnEnterMaster;
    @BindView(R.id.layout_dmx_set)          LinearLayout dmxSet;
    @BindView(R.id.tv_dmx_show)             TextView tvDmxShow;

    private String[] mTitles = {"温度", "时间"};
    private SegmentTabLayout mTabLayout;
    private View mDecorView;

    private GApp mApp;
    private List<Fragment> mFragments;
    private ViewPager mVPager;
    private int mPageType;
    private SelfDialog mDialog;
    private Gson mGson;
    private long mMemberId;
    private String mToken;

    private int mDMXAddress;
    private int mTemperature;
    private int mRestTime;

    private ConfigTempFragment mTempFragment = ConfigTempFragment.newInstance(mTemperature);
    private ConfigTimeFragment mTimeFragment = ConfigTimeFragment.newInstance(mRestTime);

    // 是否可以进入主控模式
    private boolean isEnterMasterCtrl;
    private long mDevId;
    private String mDevMac;
    private Set<Integer> mDmxSet;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_dev_config;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        mApp = (GApp) getApplication();
        setToolbar("设备配置", View.VISIBLE, null, View.GONE);

        mMemberId = Settings.getLoginSessionInfo(this).getMember_id();
        mToken = Settings.getLoginSessionInfo(this).getToken();
        mGson = new Gson();

        mDmxSet = new HashSet<>();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    public void initListener() {
        btnAddMaterial.setOnClickListener(this);
        btnEnterMaster.setOnClickListener(this);
        dmxSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_dmx_set:
                showDialog();
                break;
            case R.id.btn_add_material:
                mApp.setFlagQrCode(APP_QR_GET_MID);
                startActivityForResult(new Intent(this, CtQrScanActivity.class), 1);
                break;
            case R.id.btn_enter_master:
                if (isEnterMasterCtrl) {
                    jumpToActivity(new Intent(this, CtMasterModeActivity.class).putExtra("device_id", mDevId).putExtra("member_id", mMemberId));
                } else {
                    Toast.makeText(this, "DMX 为 0 方可进入主控模式", Toast.LENGTH_SHORT).show();
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
                BleDeviceProtocol.pkgGetAddMaterialStatus(mDevId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        DeviceMaterialStatus addMaterialStatus = BleDeviceProtocol.parseAddMaterialStatus(pkg, pkg.length);
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
                            Toast.makeText(CtDevConfigActivity.this, "加料失败，请重新加料", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void byServerMaterialInfo_D(final long materialId) {
        OkGo.<String>post(Urls.MATERIAL_INFO)
                .params("material_id", materialId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(TAG, "获取料包信息请求成功: " + body);

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
                                        Log.w(JL, "未使用状态_D");
                                        // 标记为待使用
                                        byServerWaitUseStatus(materialId, addTime);
                                        break;
                                    case TYPE_WAIT_USED:
                                        Log.w(JL, "待使用状态_D");
                                        // 提示被哪个设备绑定
                                        final long useDevice = Long.parseLong(bean.getData().getUse_device());
                                        Log.i(JL, "绑定设备ID: " + useDevice);
                                        Log.i(JL, "本机设备ID: " + mDevId);
                                        // 提示被哪个设备绑定
                                        if (useDevice == mDevId) {
                                            // 如果被本设备绑定
                                            Log.i(JL, "如果被本设备绑定，走到此处");

                                            AlertDialog.Builder builder = new AlertDialog.Builder(CtDevConfigActivity.this);
                                            builder.setTitle("料包已被本设备绑定")
                                                    .setMessage("继续添加或取消绑定返回")
                                                    .setPositiveButton("继续添加", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // 获取时间戳
                                                            Log.i(JL, "开始获取时间戳...");
                                                            cmdGetTimestamps(materialId, addTime, 3);
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
                                        Log.w(JL, "已使用状态_D");
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

    private void byServerNoUseStatus(long materialId) {
        OkGo.<String>post(Urls.NO_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDevId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(TAG, "标记为未使用状态请求成功: " + body);

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
        Log.i(JL, "byServerMaterialInfo_E: " + mDevId + " " + deviceMID + " " + mMemberId);
        OkGo.<String>post(Urls.MATERIAL_INFO)
                .params("material_id", deviceMID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(TAG, "获取料包信息请求成功: " + body);

                        MaterialInfoBean bean = mGson.fromJson(body, MaterialInfoBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                Log.i(JL, "获取信息失败_E ！");
                                information(getString(R.string.toast_get_info_failed));
                                break;
                            case 1:
                                Log.i(JL, "获取信息成功_E ！");
                                Log.d(JL, "【服务器】物料使用状态_E：" + bean.getData().getUse_status());

                                long serverMId = Long.parseLong(bean.getData().getMaterial_num());
                                Log.i(JL, "料包ID（from device）: " + deviceMID);
                                Log.i(JL, "料包ID（from server）: " + serverMId);
                                switch (bean.getData().getUse_status()) {
                                    // 判断当前服务端状态
                                    case TYPE_NO_USED:
                                        Log.e(JL, "未使用状态_E");
                                        Log.e(JL, "不应该存在该状态，若出现，检查代码逻辑或设备");
                                        break;
                                    case TYPE_WAIT_USED:
                                        Log.w(JL, "待使用状态_E");
                                        // 料包标记为已使用状态（服务器）
                                        byServerEndUseStatus_E(deviceMID, serverMId);
                                        break;
                                    case TYPE_END_USED:
                                        Log.w(JL, "已使用状态_E");
                                        // 清除加料信息（设备端）
                                        cmdClearAddMaterialInfo_E(deviceMID, serverMId, 3);
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

    private void byServerEndUseStatus_E(final long deviceMId, final long serverMId) {
        OkGo.<String>post(Urls.END_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", deviceMId)
                .params("device_id", mDevId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(TAG, "标记为已使用状态请求成功: " + body);

                        ChangeMaterialStatusBean bean = mGson.fromJson(body, ChangeMaterialStatusBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                // 状态修改成功
                                Log.i(JL, "状态已修改为已使用！");
                                // 清除加料信息
                                cmdClearAddMaterialInfo_E(deviceMId, serverMId, 3);
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

    private void cmdClearAddMaterialInfo_E(final long deviceMId, final long serverMId, final int resendNum) {
        Log.i(JL, "清除加料信息参数: " + "mDevId--" + mDevId + " mMemberId--" + mMemberId + " deviceMId--" + deviceMId + " serverMId--" + serverMId);

        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDeviceProtocol.pkgClearAddMaterialInfo(mDevId, mMemberId, deviceMId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        int info = BleDeviceProtocol.parseClearAddMaterialInfo(pkg, pkg.length);
                        Log.e(JL, "清除加料信息返回值: " + info + "  " + pkg.length);
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
                        Log.e(TAG, "timeout");
                        if (resendNum != 0) {
                            cmdClearAddMaterialInfo_E(deviceMId,serverMId, resendNum - 1);
                        } else {
                            Toast.makeText(CtDevConfigActivity.this, "清除加料信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void byServerWaitUseStatus(final long materialId, final int addTime) {
        OkGo.<String>post(Urls.WAIT_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDevId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(TAG, "标记为待使用状态请求成功: " + body);

                        AddMaterialBean bean = mGson.fromJson(body, AddMaterialBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                information(getString(R.string.toast_add_material_failed));
                                break;
                            case 1:
                                // 料包已绑定,并已进入即将使用(锁定)状态
                                Log.i(JL, "开始获取时间戳...");
                                cmdGetTimestamps(materialId, addTime, 3);
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

    private void cmdGetTimestamps(final long materialId, final int addTime, final int resendNum) {
        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDeviceProtocol.pkgGetTimestamp(mDevId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        long timestamp = BleDeviceProtocol.parseGetTimestamp(pkg, pkg.length);

                        Log.i(JL, "开始加料...");
                        cmdAddMaterial(timestamp, mDevId, addTime, materialId,3);
                    }

                    @Override
                    public void timeout() {
                        Log.e(TAG, "timeout");
                        if (resendNum != 0) {
                            cmdGetTimestamps(materialId, addTime, resendNum - 1);
                        } else {
                            Toast.makeText(CtDevConfigActivity.this, "获取时间戳失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void cmdAddMaterial(final long timestamp, final long deviceId, final int addTime, final long materialId, final int resendNum) {
        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDeviceProtocol.pkgAddMaterial(deviceId, addTime, timestamp, mMemberId, materialId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        final int material = BleDeviceProtocol.parseAddMaterial(pkg, pkg.length);
                        Log.i(JL, "加料结果: " + material);
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
                        Log.e(TAG, "timeout");
                        if (resendNum != 0) {
                            cmdAddMaterial(timestamp, deviceId, addTime, materialId, resendNum - 1);
                        } else {
                            Toast.makeText(CtDevConfigActivity.this, "加料失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void byServerEndUseStatus_D(final long materialId) {
        OkGo.<String>post(Urls.END_USE_STATUS)
                .params("member_id", mMemberId)
                .params("material_id", materialId)
                .params("device_id", mDevId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(TAG, "标记为已使用状态请求成功: " + body);

                        ChangeMaterialStatusBean bean = mGson.fromJson(body, ChangeMaterialStatusBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                // 状态修改成功
                                Log.i(JL, "状态已修改为已使用！");
                                information(getString(R.string.toast_add_material_success));
                                // 清除加料信息
                                cmdClearAddMaterialInfo_D(materialId, 3);
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

    private void cmdClearAddMaterialInfo_D(final long materialId, final int resendNum) {
        Log.i(JL, "清除加料信息参数: " + "mDevId--" + mDevId + " mMemberId--" + mMemberId + " materialId--" + materialId);

        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDeviceProtocol.pkgClearAddMaterialInfo(mDevId, mMemberId, materialId),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        int info = BleDeviceProtocol.parseClearAddMaterialInfo(pkg, pkg.length);
                        Log.w(JL, "清除加料信息返回值: " + info);
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
                        Log.e(JL, "清除加料信息超时！");
                        if (resendNum != 0) {
                            cmdClearAddMaterialInfo_D(materialId, resendNum - 1);
                        } else {
                            Toast.makeText(CtDevConfigActivity.this, "清除加料信息失败", Toast.LENGTH_SHORT).show();
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
    private Handler mHandler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_conn));
                    tvDmxShow.setText("DMX " + String.valueOf(mDMXAddress));
                    isEnterMasterCtrl = mDMXAddress == 0;
                    break;
                case 2:
                    imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_desconn));
                    tvDmxShow.setText("DMX地址");
                    isEnterMasterCtrl = mDMXAddress == 0;
                    break;
            }
        }
    };

    /** 蓝牙连接状态 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DevConnEvent event) {
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        if (event.getDeviceConfig() != null) {
            Log.i(TAG, "dev cfg event: " + event.getMac() + " | " + event.getId() + " | " + event.getStatus());

            mDmxSet.add(event.getDeviceConfig().mDMXAddress);

            switch (event.getStatus()) {
                case "已连接":
                    mDMXAddress = event.getDeviceConfig().mDMXAddress;
                    mTemperature = event.getDeviceStatus().mTemperature;
                    mRestTime = event.getDeviceStatus().mRestTime;
                    mHandler.sendEmptyMessage(1);
                    break;
                case "不可连接":
                    mDMXAddress = -1;
                    mTemperature = -1;
                    mRestTime = -1;
                    mHandler.sendEmptyMessage(2);
                    break;
            }
        }
    }

    private void showDialog() {
        mDialog = new SelfDialog(this);
        mDialog.setTitle("修改设备 DMX 地址");
        mDialog.setMessage("设置 DMX 地址和取值范围0~510");
        mDialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                if (!(mDialog.getEditTextStr().equals(""))) {
                    try {
                        final int niDmx = Integer.parseInt(mDialog.getEditTextStr());
                        if (!(niDmx >= 0 && niDmx <= 510)) {
                            // 如果输入的 DMX 不在 1~511 之间，提示用户
                            Toast.makeText(CtDevConfigActivity.this, "您设置的 DMX 地址超出范围\n请重新设置", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else {
                            // 更新 DMX 地址
                            updateDmx(niDmx);
                            mDialog.dismiss();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        // 还有不按规矩出牌的？有！
                        Toast.makeText(CtDevConfigActivity.this, "请设置正确的 DMX 地址", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                } else {
                    Toast.makeText(CtDevConfigActivity.this, "未更新 DMX 地址", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
        mDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void updateDmx(int niDmx) {
        Device device = MainActivity.getAppCtrl().getDevice(mDevMac);
        MainActivity.getAppCtrl().sendCommand(device, BleDeviceProtocol.pkgSetDmxAddress(mDevId, niDmx), new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                if (pkg.length > 8 && pkg[7] == 0) {
                    Log.i(TAG, "配置 DMX 回复成功");
                    MainActivity.getAppCtrl().getDeviceConfig(mDevMac);
                    mDialog.dismiss();
                } else {
                    Log.i(TAG, "配置 DMX 回复失败");
                }
            }

            @Override
            public void timeout() {
            }
        });
    }

    private void information(String info) {
        Toast.makeText(this, info, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MainActivity.getAppCtrl().disconnectDevice(mDevMac);
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
