package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.OrderDetailsBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.TYPE_COMPLETE_GOODS;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_PAYMENT;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_RECEIPT;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_SHIP;

/**
 * 订单详情
 */

public class MiOrderDetailsActivity extends BaseActivity {
    @BindView(R.id.tv_order_dtl_status)       TextView  tvOrderStatus;
    @BindView(R.id.tv_order_dtl_consignee)    TextView  tvConsignee;
    @BindView(R.id.tv_order_dtl_phone)        TextView  tvPhone;
    @BindView(R.id.tv_order_dtl_address)      TextView  tvAddress;
    @BindView(R.id.tv_order_dtl_name)         TextView  tvName;
    @BindView(R.id.tv_order_dtl_money)        TextView  tvMoney;
    @BindView(R.id.tv_order_dtl_num)          TextView  tvNum;
    @BindView(R.id.tv_order_dtl_odd)          TextView  tvOdd;
    @BindView(R.id.tv_order_dtl_time)         TextView  tvTime;
    @BindView(R.id.img_order_dtl_show)        ImageView imgShow;

    private static final String TAG = "MiOrderDetailsActivity";
    private String mOrderId, mType;
    private Gson mGson;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_order_details;
    }

    @Override
    public void initView() {
        setToolbar("订单详情", View.VISIBLE, null, View.GONE);
        mType = getIntent().getStringExtra("type");
        mOrderId = getIntent().getStringExtra("order_id");
        mGson = new Gson();
    }

    @Override
    public void initData() {
        getDataWithOrderDtl();
    }

    private void getDataWithOrderDtl() {
        OkGo.<String>post(Urls.ORDER_DETAIL)
                .tag(this)
                .params("order_sn", mOrderId)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(TAG, "订单详情 请求成功: " + body);

                        OrderDetailsBean bean = mGson.fromJson(body, OrderDetailsBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                setData(bean.getData());
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setData(OrderDetailsBean.DataBean bean) {
        Glide.with(this).load(bean.getOriginal_img()).into(imgShow);
        tvConsignee.setText(getString(R.string.text_consignee) + bean.getGoods_name());
        tvAddress.setText(getString(R.string.text_shipping_address) + bean.getAddress());
        tvPhone.setText(bean.getMobile());
        tvName.setText(bean.getGoods_name());
        tvMoney.setText(getString(R.string.rmb) + bean.getTotal_price());
        tvNum.setText(getString(R.string.text_multiply) + bean.getQuantity());
        tvOdd.setText(getString(R.string.text_order_num) + bean.getOrder_sn());
        tvTime.setText(getString(R.string.text_order_time) + bean.getCreate_time());

        switch (mType) {
            case TYPE_WAIT_PAYMENT:
                tvOrderStatus.setText(getString(R.string.type_wait_payment));
                break;
            case TYPE_WAIT_SHIP:
                tvOrderStatus.setText(getString(R.string.type_wait_ship));
                break;
            case TYPE_WAIT_RECEIPT:
                tvOrderStatus.setText(getString(R.string.type_wait_receipt));
                break;
            case TYPE_COMPLETE_GOODS:
                tvOrderStatus.setText(getString(R.string.type_complete_goods));
                break;
            default:
                break;
        }
    }

}
