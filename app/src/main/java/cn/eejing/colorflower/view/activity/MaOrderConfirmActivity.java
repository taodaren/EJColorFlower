package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.model.event.AddrAddEvent;
import cn.eejing.colorflower.model.request.AddrListBean;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.ConfirmOrderBean;
import cn.eejing.colorflower.model.request.OrderSetBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.FROM_ORDER_TO_ADDR;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_ADDR_SELECT;

/**
 * 确认订单
 */

public class MaOrderConfirmActivity extends BaseActivity {
    private static final String TAG = "MaOrderConfirmActivity";

    @BindView(R.id.tv_confirm_order_consignee)             TextView     tvConsignee;
    @BindView(R.id.tv_confirm_order_phone)                 TextView     tvPhone;
    @BindView(R.id.tv_confirm_order_address)               TextView     tvAddress;
    @BindView(R.id.layout_addr_confirm_order)              LinearLayout layoutAddr;
    @BindView(R.id.tv_addr_confirm_order)                  TextView     tvAddrNull;
    @BindView(R.id.img_confirm_order_goods)                ImageView    imgGoods;
    @BindView(R.id.tv_confirm_order_name)                  TextView     tvName;
    @BindView(R.id.tv_confirm_order_money)                 TextView     tvMoney;
    @BindView(R.id.tv_confirm_order_num)                   TextView     tvNum;
    @BindView(R.id.tv_confirm_order_num_buy)               TextView     tvNumBuy;
    @BindView(R.id.tv_confirm_order_total_money)           TextView     tvTotalMoney;

    private ConfirmOrderBean.DataBean mBean;
    private Gson mGson;
    private int mGoodsId, mNumber;
    private int mAddressId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_order_confirm;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setToolbar("确认订单", View.VISIBLE, null, View.GONE);

        mGson = new Gson();
        mGoodsId = getIntent().getIntExtra("goods_id", 0);
    }

    @Override
    public void initData() {
        getDataWithConfirmOrder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ll_confirm_order_address, R.id.btn_confirm_order_sub, R.id.btn_confirm_order_add, R.id.btn_submit_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit_order:
                submitOrder();
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
                if (mBean.getAddress().getConsignee() == null) {
                    BaseApplication baseApplication = (BaseApplication) getApplication();
                    baseApplication.setFlagAddrMgr(FROM_ORDER_TO_ADDR);
                    jumpToActivity(MaAddrMgrActivity.class);
                } else {
                    startActivityForResult(new Intent(this, MaAddrSelectActivity.class), REQUEST_CODE_ADDR_SELECT);
                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADDR_SELECT) {
                AddrListBean.DataBean bean = (AddrListBean.DataBean) data.getSerializableExtra("address");

                tvConsignee.setText(getString(R.string.text_consignee) + bean.getConsignee());
                tvPhone.setText(bean.getMobile());
                tvAddress.setText(getString(R.string.text_shipping_address) + bean.getAddress());
                mAddressId = bean.getId();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AddrAddEvent event) {
        // 管理收货地址返回刷新数据
        getDataWithConfirmOrder();
    }

    @SuppressLint("SetTextI18n")
    private void display(int number) {
        tvNum.setText("×" + number);
        tvNumBuy.setText("" + number);

        // 设置合计金额
        double totalMoney = number * Double.parseDouble(mBean.getGoods().getSale_price());
        tvTotalMoney.setText(getString(R.string.rmb) + totalMoney);
    }

    private void submitOrder() {
        if (tvConsignee.getText().length() > 0 && tvPhone.getText().length() > 0 && tvAddress.getText().length() > 0) {
            OkGo.<String>post(Urls.SET_ORDER)
                    .tag(this)
                    .params("goods_id", mGoodsId)
                    .params("address_id", mAddressId)
                    .params("quantity", mNumber)
                    .params("token", MainActivity.getAppCtrl().getToken())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            LogUtil.d(TAG, "用户提交订单并生成订单 请求成功: " + body);

                            OrderSetBean bean = mGson.fromJson(body, OrderSetBean.class);
                            if (bean.getCode() == 1) {
                                jumpToActivity(new Intent(MaOrderConfirmActivity.this, MaOrderPayActivity.class)
                                        .putExtra("order_no", bean.getData().getOrder_sn())
                                        .putExtra("total_price", bean.getData().getTotal_amount())
                                );
                            } else {
                                ToastUtil.showShort(bean.getMessage());
                            }
                        }
                    });
        } else {
            ToastUtil.showShort(getString(R.string.text_add_addr));
        }
    }

    private void getDataWithConfirmOrder() {
        OkGo.<String>post(Urls.CONFIRM_ORDER)
                .tag(this)
                .params("goods_id", mGoodsId)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "确认订单页面展示 请求成功: " + body);

                                 ConfirmOrderBean bean = mGson.fromJson(body, ConfirmOrderBean.class);
                                 mBean = bean.getData();
                                 switch (bean.getCode()) {
                                     case 1:
                                         setData();
                                         break;
                                     default:
                                         ToastUtil.showShort(bean.getMessage());
                                         break;
                                 }
                             }
                         }
                );
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        // 如果地址内容不为空，设置显示信息
        if (mBean.getAddress().getConsignee() != null || mBean.getAddress().getMobile() != null || mBean.getAddress().getAddress() != null) {
            tvAddrNull.setVisibility(View.GONE);
            layoutAddr.setVisibility(View.VISIBLE);
            tvConsignee.setText(getString(R.string.text_consignee) + mBean.getAddress().getConsignee());
            tvPhone.setText(mBean.getAddress().getMobile());
            tvAddress.setText(getString(R.string.text_shipping_address) + mBean.getAddress().getAddress());
        } else {
            tvAddrNull.setVisibility(View.VISIBLE);
            layoutAddr.setVisibility(View.GONE);
        }

        Glide.with(this).load(mBean.getGoods().getOriginal_img()).into(imgGoods);
        tvName.setText(mBean.getGoods().getGoods_name());
        tvMoney.setText(getString(R.string.rmb) + mBean.getGoods().getSale_price());

        mNumber = 1;
        mAddressId = mBean.getAddress().getId();
        display(mNumber);
    }

}
