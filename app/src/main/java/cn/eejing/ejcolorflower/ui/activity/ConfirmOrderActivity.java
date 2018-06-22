package cn.eejing.ejcolorflower.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.ConfirmOrderBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * 确认订单
 */

public class ConfirmOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.btn_submit_order)
    Button btnSubmitOrder;
    @BindView(R.id.tv_confirm_order_consignee)
    TextView tvConsignee;
    @BindView(R.id.tv_confirm_order_phone)
    TextView tvPhone;
    @BindView(R.id.tv_confirm_order_address)
    TextView tvAddress;
    @BindView(R.id.ll_confirm_order_address)
    LinearLayout llAddress;
    @BindView(R.id.img_confirm_order_goods)
    ImageView imgGoods;
    @BindView(R.id.tv_confirm_order_name)
    TextView tvName;
    @BindView(R.id.tv_confirm_order_money)
    TextView tvMoney;
    @BindView(R.id.tv_confirm_order_num)
    TextView tvNum;
    @BindView(R.id.btn_confirm_order_sub)
    Button btnSub;
    @BindView(R.id.tv_confirm_order_num_buy)
    TextView tvNumBuy;
    @BindView(R.id.btn_confirm_order_add)
    Button btnAdd;
    @BindView(R.id.tv_confirm_order_postage_full)
    TextView tvPostageFull;
    @BindView(R.id.tv_confirm_order_postage_basics)
    TextView tvPostageBasics;
    @BindView(R.id.tv_confirm_order_total_money)
    TextView tvTotalMoney;

    private ConfirmOrderBean.DataBean mBean;
    private Gson mGson;
    private String mMemberId, mToken;
    private int mGoodsId, mNumber;
    private double mTotalMoney;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_confirm_order;
    }

    @Override
    public void initView() {
        setToolbar("确认订单", View.VISIBLE);

        mGson = new Gson();
        mGoodsId = getIntent().getIntExtra("goods_id", 0);
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(this).getMember_id());
        mToken = Settings.getLoginSessionInfo(this).getToken();
    }

    @Override
    public void initData() {
        getDataWithConfirmOrder();
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnSubmitOrder.setOnClickListener(this);
        llAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            case R.id.btn_submit_order:
                Intent intent = new Intent(this, PaymentOrderActivity.class);
                intent.putExtra("goods_id", mGoodsId);
                intent.putExtra("quantity", mNumber);
                intent.putExtra("address_id", mBean.getAddress().getId());
                intent.putExtra("member_id", mMemberId);
                intent.putExtra("token", mToken);
                intent.putExtra("money", mTotalMoney);
                jumpToActivity(intent);
                break;
            case R.id.btn_confirm_order_add:
                mNumber = mNumber + 1;
                display(mNumber);
                break;
            case R.id.btn_confirm_order_sub:
                if (mNumber > 1) {
                    mNumber = mNumber - 1;
                    display(mNumber);
                }
                break;
            case R.id.ll_confirm_order_address:
                jumpToActivity(ShippingAddressActivity.class);
                break;
            default:
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void display(int number) {
        tvNum.setText("×" + number);
        tvNumBuy.setText("" + number);

        // 设置合计金额
        mTotalMoney = number * mBean.getGoods().getMoney();
        if (mTotalMoney < mBean.getGoods().getPostage()) {
            mTotalMoney = mTotalMoney + mBean.getGoods().getBasics_postage();
        }
        tvTotalMoney.setText(getString(R.string.rmb) + mTotalMoney);
    }

    private void getDataWithConfirmOrder() {
        OkGo.<String>post(Urls.CONFIRM_ORDER)
                .tag(this)
                .params("goods_id", mGoodsId)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "confirm_order request succeeded--->" + body);

                                 ConfirmOrderBean bean = mGson.fromJson(body, ConfirmOrderBean.class);
                                 mBean = bean.getData();
                                 switch (bean.getCode()) {
                                     case 1:
                                         setData();
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

    @SuppressLint("SetTextI18n")
    private void setData() {
        tvConsignee.setText(getString(R.string.text_consignee) + mBean.getAddress().getName());
        tvPhone.setText(mBean.getAddress().getMobile());
        tvAddress.setText(getString(R.string.text_shipping_address) + mBean.getAddress().getAddress_all());

        Glide.with(this).load(mBean.getGoods().getImage()).into(imgGoods);
        tvName.setText(mBean.getGoods().getName());
        tvMoney.setText(getString(R.string.rmb) + mBean.getGoods().getMoney());
        tvPostageBasics.setText(getString(R.string.basic_postage) + mBean.getGoods().getBasics_postage());
        tvPostageFull.setText(getString(R.string.postage_before) + mBean.getGoods().getPostage() + getString(R.string.postage_after));

        mNumber = 1;
        display(mNumber);
    }

}
