package cn.eejing.ejcolorflower.ui.activity;

import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.MaterialInfoBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

public class QRAddMaterialActivity extends BaseActivity implements View.OnClickListener, QRCodeView.Delegate {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.zxingview_add_material)
    ZXingView mQRCodeView;
    @BindView(R.id.tv_light_switch_add_material)
    TextView tvLightSwitch;

    private int mFlag;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_qradd_material;
    }

    @Override
    public void initView() {
        setToolbar("二维码扫描", View.VISIBLE);

        // 设置扫描二维码的代理
        mQRCodeView.setDelegate(this);
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
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
            case R.id.img_title_back:
                finish();
                break;
            case R.id.tv_light_switch_add_material:
                switchFlashlight();
                break;
            default:
                break;
        }
    }

    private void scanResults(String result) {
        // 处理扫描结果
        Log.i(AppConstant.TAG, "扫描结果:" + result);
        String materialId = result.substring(result.length() - 14);
        Log.i("TAG", "scanResults: " + materialId);

        vibrate();
        // 延迟1.5秒后开始识别
        mQRCodeView.startSpot();

        OkGo.<String>post(Urls.MATERIAL_INFO)
                .params("material_id", materialId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "material_info request succeeded--->" + body);

                        Gson gson = new Gson();
                        MaterialInfoBean bean = gson.fromJson(body, MaterialInfoBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                Toast.makeText(QRAddMaterialActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            case 1:
                                Toast.makeText(QRAddMaterialActivity.this, "料包添加成功", Toast.LENGTH_SHORT).show();
                                finish();
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
