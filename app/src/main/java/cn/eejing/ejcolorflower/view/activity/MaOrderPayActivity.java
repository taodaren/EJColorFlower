package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.PayWeiBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.model.request.CallBackConfirmBean;
import cn.eejing.ejcolorflower.model.request.PayAliBean;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.util.PayResult;

import static cn.eejing.ejcolorflower.app.AppConstant.APP_ID;

/**
 * 订单支付
 */

public class MaOrderPayActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_pay)                SuperButton btnPay;
    @BindView(R.id.tv_pay_money)           TextView tvPayMoney;
    @BindView(R.id.ll_pay_ali)             LinearLayout mPayAli;
    @BindView(R.id.ll_pay_wei_xin)         LinearLayout mPayWx;
    @BindView(R.id.img_pay_ali)            ImageView imgPayAli;
    @BindView(R.id.img_pay_wei_xin)        ImageView imgPayWx;

    private static final int SDK_PAY_FLAG = 1;
    private Gson mGson;
    private PayAliBean.DataBean mBeanAli;
    private PayWeiBean.DataBean mBeanWei;
    private String mMemberId, mToken;
    private int mGoodsId, mNumber, mAddressId;
    private int mPayFlag = 1;
    private IWXAPI api;

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
        setToolbar("订单支付", View.VISIBLE);
        tvPayMoney.setText(getString(R.string.rmb) + getIntent().getDoubleExtra("money", 0));

        mGson = new Gson();
        mGoodsId = getIntent().getIntExtra("goods_id", 0);
        mNumber = getIntent().getIntExtra("quantity", 0);
        mAddressId = getIntent().getIntExtra("address_id", 0);
        mMemberId = getIntent().getStringExtra("member_id");
        mToken = getIntent().getStringExtra("token");

        // 初始化微信支付 api
        api = WXAPIFactory.createWXAPI(this, APP_ID);
    }

    @Override
    public void initListener() {
        btnPay.setOnClickListener(this);
        mPayAli.setOnClickListener(this);
        mPayWx.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
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
                imgPayAli.setImageDrawable(getDrawable(R.drawable.circular_check));
                imgPayWx.setImageDrawable(getDrawable(R.drawable.circular_not_check));
                break;
            case R.id.ll_pay_wei_xin:
                mPayFlag = 0;
                imgPayAli.setImageDrawable(getDrawable(R.drawable.circular_not_check));
                imgPayWx.setImageDrawable(getDrawable(R.drawable.circular_check));
                break;
            default:
                break;
        }
    }

    private void payResult(Message msg) {
        PayResult payResult = new PayResult((Map<String, String>) msg.obj);
        // 同步返回的结果必须放置到服务端进行验证
        //（验证的规则请看 https://doc.open.alipay.com/doc2/detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&docType=1)
        // 建议商户依赖异步通知
        String resultInfo = payResult.getResult();// 同步返回需要验证的信息

        String resultStatus = payResult.getResultStatus();
        switch (resultStatus) {
            case "9000":
                // 判断 resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                Toast.makeText(MaOrderPayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                break;
            case "8000":
                // 支付确认中（小概率事件）
                // 判断 resultStatus 为非"9000"则代表可能支付失败
                Toast.makeText(MaOrderPayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                break;
            default:
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                Toast.makeText(MaOrderPayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 调用支付宝
     */
    public void callAlipay(final String orderString) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造 PayTask 对象
                PayTask alipay = new PayTask(MaOrderPayActivity.this);
                // 调用支付接口，获取支付结果
                Map<String, String> result = alipay.payV2(orderString, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 调用微信支付
     */
    public void sendPayRequest() {
        PayReq req = new PayReq();
        req.appId = mBeanWei.getAppid();
        req.partnerId = mBeanWei.getPartnerid();
        req.prepayId = mBeanWei.getPrepayid();
        req.packageValue = mBeanWei.getPackageX();
        req.nonceStr = mBeanWei.getNoncestr();
        req.timeStamp = String.valueOf(mBeanWei.getTimestamp());
        req.sign = mBeanWei.getSign();
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        // 调用微信支付 sdk 支付方法
        api.sendReq(req);
    }

    private void getDataWithPayAli() {
        OkGo.<String>post(Urls.PAY)
                .tag(this)
                .params("pay_code", AppConstant.PAY_CODE_ALI)
                .params("goods_id", mGoodsId)
                .params("quantity", mNumber)
                .params("address_id", mAddressId)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "pay ali request succeeded--->" + body);

                                 PayAliBean bean = mGson.fromJson(body, PayAliBean.class);
                                 mBeanAli = bean.getData();
                                 switch (bean.getCode()) {
                                     case 1:
                                         callAlipay(mBeanAli.getOrderString());
                                         break;
                                     default:
                                         break;
                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );
    }

    private void getDataWithPayWei() {
        OkGo.<String>post(Urls.PAY)
                .tag(this)
                .params("pay_code", AppConstant.PAY_CODE_WEI)
                .params("goods_id", mGoodsId)
                .params("quantity", mNumber)
                .params("address_id", mAddressId)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "pay wei request succeeded--->" + body);

                                 PayWeiBean bean = mGson.fromJson(body, PayWeiBean.class);
                                 mBeanWei = bean.getData();
                                 switch (bean.getCode()) {
                                     case 1:
                                         sendPayRequest();
                                         break;
                                     default:
                                         break;
                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );
    }

    private void getDataWithCallBackConfirm() {
        OkGo.<String>post(Urls.CALL_BACK_CONFIRM)
                .tag(this)
                .params("order_num", 15)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "call_back_confirm request succeeded--->" + body);

                                 CallBackConfirmBean bean = mGson.fromJson(body, CallBackConfirmBean.class);
                                 switch (bean.getCode()) {
                                     case 0:
                                         break;
                                     default:
                                         break;
                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );
    }

}
