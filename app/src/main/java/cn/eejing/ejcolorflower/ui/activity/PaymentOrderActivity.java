package cn.eejing.ejcolorflower.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.CallBackConfirmBean;
import cn.eejing.ejcolorflower.model.request.PayBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.PayResult;

/**
 * 订单支付
 */

public class PaymentOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.btn_pay)
    SuperButton btnPay;
    @BindView(R.id.tv_pay_money)
    TextView tvPayMoney;

    private static final int SDK_PAY_FLAG = 1;
    private Gson mGson;
    private PayBean.DataBean mBean;
    private String mMemberId, mToken;
    private int mGoodsId, mNumber, mAddressId;

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
        return R.layout.activity_payment_order;
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
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
        btnPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            case R.id.btn_pay:
                getDataWithPay();
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
                Toast.makeText(PaymentOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                break;
            case "8000":
                // 支付确认中（小概率事件）
                // 判断 resultStatus 为非"9000"则代表可能支付失败
                Toast.makeText(PaymentOrderActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                break;
            default:
                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                Toast.makeText(PaymentOrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
                PayTask alipay = new PayTask(PaymentOrderActivity.this);
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

    private void getDataWithPay() {
        OkGo.<String>post(Urls.PAY)
                .tag(this)
                .params("pay_code", AppConstant.PAY_CODE)
                .params("goods_id", mGoodsId)
                .params("quantity", mNumber)
                .params("address_id", mAddressId)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "pay request succeeded--->" + body);

                                 PayBean bean = mGson.fromJson(body, PayBean.class);
                                 mBean = bean.getData();
                                 switch (bean.getCode()) {
                                     case 1:
                                         callAlipay(mBean.getOrderString());
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
