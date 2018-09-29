package cn.eejing.ejcolorflower.view.activity;

import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.AddDeviceBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_MATERIAL_ID;

public class CtQrScanActivity extends BaseActivity implements View.OnClickListener, QRCodeView.Delegate {
    private static final String TAG = "CtQrScanActivity";

    @BindView(R.id.zxingview)              ZXingView mQRCodeView;
    @BindView(R.id.tv_light_switch)        TextView  tvLightSwitch;

    private int mFlag;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_qr_add_device;
    }

    @Override
    public void initView() {
        setToolbar("二维码扫描", View.VISIBLE, null, View.GONE);

        // 设置扫描二维码的代理
        mQRCodeView.setDelegate(this);
    }

    @Override
    public void initListener() {
        tvLightSwitch.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 打开后置摄像头开始预览，但是并未开始识别
        mQRCodeView.startCamera();
        // 显示扫描框，并且延迟1.5秒后开始识别
        mQRCodeView.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        // 关闭摄像头预览，并且隐藏扫描框
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 在 onDestroy 方法中调用 mQRCodeView.onDestroy()
        // 在 onStop 方法中调用 mQRCodeView.stopCamera()，否则会出现黑屏
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_light_switch:
                switchFlashlight();
                break;
            default:
                break;
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        scanResults(result);
    }

    private void scanResults(String result) {
        // 处理扫描结果
        Log.i(TAG, "scanResults: " + result);
        Log.i(TAG, "length: " + result.length());

        switch (result.length()) {
            case 6:
                // 扫描到设备 ID
                setResults(result, QR_DEV_ID);
                break;
            case 15:
                // 扫描到料包 ID
                setResults(result, QR_MATERIAL_ID);
                break;
            default:
                break;
        }
    }

    private void setResults(String result, String flag) {
        vibrate();
        // 延迟 1.5s 后开始识别
        mQRCodeView.startSpot();
        Log.i(TAG, "id: " + Long.parseLong(result));
        setResult(RESULT_OK, new Intent().putExtra(flag, Long.parseLong(result)));
        finish();
    }

    /** 震动 */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        assert vibrator != null;
        vibrator.vibrate(200);
    }

    /** 切换闪光灯 */
    private void switchFlashlight() {
        switch (mFlag) {
            case 0:
                turnOn();
                mFlag++;
                break;
            case 1:
                turnOff();
                mFlag--;
                break;
            default:
                break;
        }
        mFlag = mFlag % 2;
    }

    private void turnOn() {
        tvLightSwitch.setText("关闭照明");
        mQRCodeView.openFlashlight();
    }

    private void turnOff() {
        tvLightSwitch.setText("开启照明");
        mQRCodeView.closeFlashlight();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }
}
