package cn.eejing.ejcolorflower.view.activity;

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
import cn.eejing.ejcolorflower.model.session.LoginSession;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.model.request.AddDeviceBean;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * 添加设备
 */

public class DeQrAddDeviceActivity extends BaseActivity implements View.OnClickListener, QRCodeView.Delegate {

    @BindView(R.id.zxingview)
    ZXingView mQRCodeView;
    @BindView(R.id.tv_light_switch)
    TextView tvLightSwitch;

    private int mFlag;
    private String mMemberId, mToken;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_qr_add_device;
    }

    @Override
    public void initView() {
        setToolbar("二维码扫描", View.VISIBLE);

        LoginSession session = Settings.getLoginSessionInfo(this);
        mToken = session.getToken();
        mMemberId = getIntent().getStringExtra("member_id");

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
    public void onScanQRCodeSuccess(String result) {
        scanResults(result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        // 处理打开相机出错
        Log.e(AppConstant.TAG, "打开相机出错");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_light_switch:
                switchFlashlight();
                break;
            default:
                break;
        }
    }

    private void scanResults(String result) {
        // 处理扫描结果
        Log.i(AppConstant.TAG, "扫描结果:" + result);
        String deviceId = result.substring(result.length() - 6);

        vibrate();
        // 延迟1.5秒后开始识别
        mQRCodeView.startSpot();
        OkGo.<String>post(Urls.ADD_DEVICE)
                .params("member_id", mMemberId)
                .params("device_id", deviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "add device request succeeded--->" + body);

                        Gson gson = new Gson();
                        AddDeviceBean bean = gson.fromJson(body, AddDeviceBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                Toast.makeText(DeQrAddDeviceActivity.this, "暂无此设备", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(DeQrAddDeviceActivity.this, "设备添加成功", Toast.LENGTH_SHORT).show();
                                jumpToActivity(AppActivity.class);
                                finish();
                                break;
                            case 2:
                                Log.e(AppConstant.TAG, "QR code please pass in member id.");
                                break;
                            case 3:
                                Log.e(AppConstant.TAG, "QR code please pass in device id.");
                                break;
                            case 4:
                                Toast.makeText(DeQrAddDeviceActivity.this, "设备重复添加了", Toast.LENGTH_SHORT).show();
                                break;
                            case 5:
                                Toast.makeText(DeQrAddDeviceActivity.this, "该设备不存在", Toast.LENGTH_SHORT).show();
                                break;
                            case 6:
                                Toast.makeText(DeQrAddDeviceActivity.this, "此设备已绑定", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    /**
     * 切换闪光灯
     */
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

}
