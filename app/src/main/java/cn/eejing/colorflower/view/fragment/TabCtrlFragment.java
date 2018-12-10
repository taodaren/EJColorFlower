package cn.eejing.colorflower.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Objects;

import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.model.event.DevConnEvent;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.activity.CtDevConfigActivity;
import cn.eejing.colorflower.view.activity.CtQrScanActivity;
import cn.eejing.colorflower.view.base.BaseFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.colorflower.app.AppConstant.APP_QR_GET_DID;
import static cn.eejing.colorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.colorflower.app.AppConstant.QR_DEV_MAC;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_SCANNING_CONN_DEV;

/**
 * 控制模块
 */

public class TabCtrlFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "TabCtrlFragment";

    private BaseApplication mApp;

    public static TabCtrlFragment newInstance() {
        return new TabCtrlFragment();
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_ctrl;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.control_name, View.VISIBLE);
    }

    @Override
    public void initView(View rootView) {
        EventBus.getDefault().register(this);
        mApp = (BaseApplication) getContext().getApplicationContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFlagStopContext = 0;
        requestCodeQRCodePermissions();
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    @OnClick(R.id.btn_ctrl_ble_conn)
    public void onClickedConnDev() {
        mApp.setFlagQrCode(APP_QR_GET_DID);

        Objects.requireNonNull(getActivity()).startActivityForResult(new Intent(getContext(), CtQrScanActivity.class), REQUEST_CODE_SCANNING_CONN_DEV);
    }

    private int mFlagConnStatus, mFlagStopContext;
    private String mStatus;

    /** 蓝牙连接状态 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DevConnEvent event) {
        mStatus = event.getStatus();
        LogUtil.i(TAG, "ctrl model: " + event.getMac() + " | " + event.getId() + " | " + mStatus);
        if (mStatus.equals("已连接")) {
            if (mFlagConnStatus == 0 && mFlagStopContext != 2) {
                // 如果连接状态为已连接，并且回到 TabCtrlFragment，跳转到设备配置界面
                startActivity(new Intent(getContext(), CtDevConfigActivity.class)
                        .putExtra(QR_DEV_ID, event.getId())
                        .putExtra(QR_DEV_MAC, event.getMac())
                );
            }
            mFlagConnStatus = 1;
        }
        if (mStatus.equals("不可连接")) {
            mFlagConnStatus = 0;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.w(TAG, "onStop: ");

        if (mStatus != null && mStatus.equals("不可连接")) {
            // 正常断开
            mFlagStopContext = 1;
        } else {
            // 异常断开
            mFlagStopContext = 2;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
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
}
