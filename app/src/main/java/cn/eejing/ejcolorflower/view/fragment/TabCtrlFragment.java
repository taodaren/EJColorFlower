package cn.eejing.ejcolorflower.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;
import java.util.Objects;

import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.activity.CtDevConfigActivity;
import cn.eejing.ejcolorflower.view.activity.CtQrScanActivity;
import cn.eejing.ejcolorflower.view.base.BaseFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;

/**
 * 控制模块
 */

public class TabCtrlFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks{

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
    public void onStart() {
        super.onStart();
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
//        Objects.requireNonNull(getActivity()).startActivityForResult(new Intent(getContext(), CtQrScanActivity.class), 1);
        // TODO: 方便测试，直接跳转，正式开发使用上面扫描连接
        startActivity(new Intent(getContext(), CtDevConfigActivity.class).putExtra(QR_DEV_ID, 810360));
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
