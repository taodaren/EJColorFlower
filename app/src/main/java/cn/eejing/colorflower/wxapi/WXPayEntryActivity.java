package cn.eejing.colorflower.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.activity.MaOrderPayActivity;

import static cn.eejing.colorflower.app.AppConstant.APP_ID;
import static cn.eejing.colorflower.app.AppConstant.PAY_WX;

/**
 * 微信支付录入
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    /** 支付结果回调 */
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            LogUtil.d(TAG, "onPayFinish, errCode = " + resp.errCode);
            finish();

            switch (resp.errCode) {
                case 0:
                    // 支付成功
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.app_tip);
                    builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
                    builder.show();
                    MaOrderPayActivity.getInstance().getDataWithCallBackConfirm(PAY_WX, null);
                    break;
                case -2:
                    ToastUtil.showShort("支付已取消");
                    break;
                case -1:
                    LogUtil.e(TAG, "支付异常");
                    break;
            }
        }
    }

}