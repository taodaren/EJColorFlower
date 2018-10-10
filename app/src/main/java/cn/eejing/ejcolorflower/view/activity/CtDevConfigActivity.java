package cn.eejing.ejcolorflower.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.model.event.DevConnEvent;
import cn.eejing.ejcolorflower.util.SelfDialog;
import cn.eejing.ejcolorflower.util.ViewFindUtils;
import cn.eejing.ejcolorflower.view.adapter.ViewPagerAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.view.fragment.ConfigTempFragment;
import cn.eejing.ejcolorflower.view.fragment.ConfigTimeFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_MAC;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_MATERIAL_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;

public class CtDevConfigActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks{
    private static final String TAG = "CtDevConfigActivity";

    @BindView(R.id.img_ble_toolbar)         ImageView    imgBleToolbar;
    @BindView(R.id.btn_add_material)        Button       btnAddMaterial;
    @BindView(R.id.btn_enter_master)        Button       btnEnterMaster;
    @BindView(R.id.layout_dmx_set)          LinearLayout dmxSet;
    @BindView(R.id.tv_dmx_show)             TextView     tvDmxShow;

    private String[] mTitles = {"温度", "时间"};
    private SegmentTabLayout mTabLayout;
    private View mDecorView;

    private List<Fragment> mFragments;
    private ViewPager mVPager;
    private int mPageType;
    private SelfDialog mDialog;

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
        setToolbar("设备配置", View.VISIBLE, null, View.GONE);

        mDmxSet = new HashSet<>();
        mDevId = getIntent().getLongExtra(QR_DEV_ID, 0);
        mDevMac = getIntent().getStringExtra(QR_DEV_MAC);
        Log.i(TAG, "设备信息: " + mDevId + " " + mDevMac);


        // 如果 DMX 为 0，true；反之 false
        isEnterMasterCtrl = mDMXAddress == 0;

        mFragments = new ArrayList<>();
        mFragments.add(ConfigTempFragment.newInstance(mTemperature));
        mFragments.add(ConfigTimeFragment.newInstance(mRestTime));

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
                startActivityForResult(new Intent(this, CtQrScanActivity.class), 1);
                break;
            case R.id.btn_enter_master:
                if (isEnterMasterCtrl) {
                    jumpToActivity(new Intent(this, CtMasterModeActivity.class).putExtra("device_id", mDevId));
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
                    long devId = data.getLongExtra(QR_MATERIAL_ID, 0);
                    Log.d(TAG, "onActivityResult dev id: " + devId);
                    // 处理加料逻辑
                }
                break;
            default:
                break;
        }
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

    int mDMXAddress;
    int mTemperature;
    int mRestTime;
    /** 蓝牙连接状态 */
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DevConnEvent event) {
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        mDmxSet.add(event.getDeviceConfig().mDMXAddress);

        switch (event.getStatus()) {
            case "已连接":
                imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_conn));
                mDMXAddress = event.getDeviceConfig().mDMXAddress;
                mTemperature = event.getDeviceStatus().mTemperature;
                mRestTime = event.getDeviceStatus().mRestTime;
                tvDmxShow.setText("DMX " + String.valueOf(mDMXAddress));

                isEnterMasterCtrl = mDMXAddress == 0;

                ConfigTempFragment.updateBleData(mTemperature);
                ConfigTimeFragment.updateBleData(mRestTime);
                break;
            case "不可连接":
                imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_desconn));
                mDMXAddress = -1;
                mTemperature = -1;
                mRestTime = -1;
                tvDmxShow.setText("DMX地址");

                isEnterMasterCtrl = mDMXAddress == 0;

                ConfigTempFragment.updateBleData(0);
                ConfigTimeFragment.updateBleData(0);
                break;
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
        MainActivity.getAppCtrl().sendCommand(device, BleDeviceProtocol.pkgSetDmxAddress(mDevId, niDmx));
        try {
            Thread.sleep(500);
            MainActivity.getAppCtrl().setFlagRequestConfig(true);
            tvDmxShow.setText(String.valueOf(mDMXAddress));
            mDialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
