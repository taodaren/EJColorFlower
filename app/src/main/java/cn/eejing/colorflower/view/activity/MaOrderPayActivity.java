package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.PayAliBean;
import cn.eejing.colorflower.model.request.PayWeiBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.PayResult;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.APP_ID;
import static cn.eejing.colorflower.app.AppConstant.PAY_ALI;
import static cn.eejing.colorflower.app.AppConstant.PAY_WX;

/**
 * 订单支付
 */

public class MaOrderPayActivity extends BaseActivity {
    private static final String TAG = "MaOrderPayActivity";

    @BindView(R.id.btn_pay)                SuperButton btnPay;
    @BindView(R.id.tv_pay_money)           TextView tvPayMoney;
    @BindView(R.id.ll_pay_ali)             LinearLayout mPayAli;
    @BindView(R.id.ll_pay_wei_xin)         LinearLayout mPayWx;
    @BindView(R.id.img_pay_ali)            ImageView imgPayAli;
    @BindView(R.id.img_pay_wei_xin)        ImageView imgPayWx;

    private static final int SDK_PAY_FLAG = 1;
    private Gson mGson;
    private String mOrderNo;
    private double mTotalPrice;
    private int mPayFlag = 1;
    private IWXAPI wxApi;

    private static MaOrderPayActivity mInstance;

    public static MaOrderPayActivity getInstance() {
        return mInstance;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    payResult(msg);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_order_pay;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        mInstance = this;
        setToolbar("订单支付", View.VISIBLE, null, View.GONE);

        mGson = new Gson();
        mOrderNo = getIntent().getStringExtra("order_no");
        mTotalPrice = getIntent().getDoubleExtra("total_price", 0);
        tvPayMoney.setText(getString(R.string.rmb) + mTotalPrice);

        // 初始化微信支付 注册APPID
        wxApi = WXAPIFactory.createWXAPI(this, null);
        wxApi.registerApp(APP_ID);
    }

    @OnClick({R.id.ll_pay_ali, R.id.ll_pay_wei_xin, R.id.btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pay:
                if (mPayFlag == 1) {
                    getDataWithPayAli();
                } else {
                    getDataWithPayWei();
                }
                break;
            case R.id.ll_pay_ali:
                mPayFlag = 1;
                imgPayAli.setImageDrawable(getDrawable(R.drawable.ic_single_selected));
                imgPayWx.setImageDrawable(getDrawable(R.drawable.ic_single_unselected));
                break;
            case R.id.ll_pay_wei_xin:
                mPayFlag = 0;
                imgPayAli.setImageDrawable(getDrawable(R.drawable.ic_single_unselected));
                imgPayWx.setImageDrawable(getDrawable(R.drawable.ic_single_selected));
                break;
        }
    }

    private void payResult(Message msg) {
        PayResult payResult = new PayResult((Map<String, String>) msg.obj);
        // 同步返回的结果必须放置到服务端进行验证
        //（验证的规则请看 https://doc.open.alipay.com/doc2/detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&docType=1)
        // 建议商户依赖异步通知
        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
        LogUtil.v(TAG, "" + resultInfo);
        getDataWithCallBackConfirm(PAY_ALI, payResult);
    }

    /** 调用支付宝 */
    public void callAliPay(final String orderString) {
        Runnable payRunnable = () -> {
            // 构造 PayTask 对象
            PayTask alipay = new PayTask(MaOrderPayActivity.this);
            // 调用支付接口，获取支付结果
            Map<String, String> result = alipay.payV2(orderString, true);

            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /** 调用微信支付 */
    public void sendPayRequest(PayWeiBean.DataBean wxData) {
        PayReq request = new PayReq();
        request.appId = wxData.getAppid();
        request.partnerId = wxData.getPartnerid();
        request.prepayId = wxData.getPrepayid();
        request.packageValue = wxData.getPackageX();
        request.nonceStr = wxData.getNoncestr();
        request.timeStamp = String.valueOf(wxData.getTimestamp());
        request.sign = wxData.getSign();
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        // 调用微信支付 sdk 支付方法
        wxApi.sendReq(request);
    }

    private void getDataWithPayAli() {
        OkGo.<String>post(Urls.A_LI_PAY)
                .tag(this)
                .params("trade_no", mOrderNo)
                .params("total_price", mTotalPrice)
                .params("subject", "商品购买")
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "支付宝支付 请求成功: " + body);

                                 if (mGson.fromJson(body, PayAliBean.class).getCode() == 1) {
                                     callAliPay(mGson.fromJson(body, PayAliBean.class).getData().getOrderString());
                                 }
                             }
                         }
                );
    }

    private void getDataWithPayWei() {
        OkGo.<String>post(Urls.WE_CHAT_PAY)
                .tag(this)
                .params("trade_no", mOrderNo)
                .params("total_price", mTotalPrice)
                .params("subject", "商品购买")
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "微信支付 请求成功: " + body);

                                 if (mGson.fromJson(body, PayWeiBean.class).getCode() == 1) {
                                     sendPayRequest(mGson.fromJson(body, PayWeiBean.class).getData());
                                 }
                             }
                         }
                );
    }

    public void getDataWithCallBackConfirm(String type, PayResult result) {
        OkGo.<String>post(Urls.CALL_BACK_CONFIRM)
                .tag(this)
                .params("order_sn", mOrderNo)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "商品订单支付结果确认 请求成功: " + body);
                                 switch (type) {
                                     case PAY_ALI:
                                         int resultStatus = Integer.parseInt(result.getResultStatus());
                                         switch (resultStatus) {
                                             case 9000:
                                                 // 订单支付成功
                                                 ToastUtil.showShort("支付宝支付成功");
                                                 jumpToActivity(MainActivity.class);
                                                 break;
                                             case 8000:
                                                 // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                                                 ToastUtil.showShort("支付结果确认中");
                                                 break;
                                             default:
                                                 // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                                 ToastUtil.showShort("支付失败");
                                                 break;
                                         }
                                         break;
                                     case PAY_WX:
                                         startActivity(new Intent(MaOrderPayActivity.this, MainActivity.class));
                                         ToastUtil.showShort("微信支付成功");
                                         break;
                                 }
                             }
                         }
                );
    }
}
