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
import cn.eejing.colorflower.app.AppConstant;
import cn.eejing.colorflower.model.request.OrderDetailsBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 订单详情
 */

public class MiOrderDetailsActivity extends BaseActivity {

    @BindView(R.id.tv_order_dtl_status)       TextView     tvOrderStatus;
    @BindView(R.id.tv_order_dtl_consignee)    TextView     tvConsignee;
    @BindView(R.id.tv_order_dtl_phone)        TextView     tvPhone;
    @BindView(R.id.tv_order_dtl_address)      TextView     tvAddress;
    @BindView(R.id.tv_order_dtl_name)         TextView     tvName;
    @BindView(R.id.tv_order_dtl_money)        TextView     tvMoney;
    @BindView(R.id.tv_order_dtl_num)          TextView     tvNum;
    @BindView(R.id.tv_order_dtl_odd)          TextView     tvOdd;
    @BindView(R.id.tv_order_dtl_time)         TextView     tvTime;
    @BindView(R.id.img_order_dtl_show)        ImageView    imgShow;

    private int mOrderId;
    private String mType;
    private Gson mGson;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_order_details;
    }

    @Override
    public void initView() {
        setToolbar("订单详情", View.VISIBLE, null, View.GONE);
        mType = getIntent().getStringExtra("type");
        mOrderId = getIntent().getIntExtra("order_id", 0);
        mGson = new Gson();
    }

    @Override
    public void initData() {
        getDataWithOrderDtl();
    }

    private void getDataWithOrderDtl() {
        OkGo.<String>post(Urls.ORDER_DETAIL)
                .tag(this)
                .params("order_id", mOrderId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.e(AppConstant.TAG, "order_details request succeeded --->" + body);

                        OrderDetailsBean bean = mGson.fromJson(body, OrderDetailsBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                setData(bean.getData());
                                break;
                            case 0:
                                ToastUtil.showShort("对不起，获取订单详情失败！");
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void setData(OrderDetailsBean.DataBean bean) {
        Glide.with(this).load(bean.getImage()).into(imgShow);
        tvConsignee.setText(getString(R.string.text_consignee) + bean.getName());
        tvAddress.setText(getString(R.string.text_shipping_address) + bean.getAddress());
        tvPhone.setText(bean.getMobile());
        tvName.setText(bean.getGoods_name());
        tvMoney.setText(getString(R.string.rmb) + bean.getMoney());
        tvNum.setText(getString(R.string.text_multiply) + bean.getQuantity());
        tvOdd.setText(getString(R.string.text_order_num) + bean.getOrder_num());
        tvTime.setText(getString(R.string.text_order_time) + bean.getOrder_time());

        switch (mType) {
            case AppConstant.TYPE_WAIT_PAYMENT:
                tvOrderStatus.setText(getString(R.string.type_wait_payment));
                break;
            case AppConstant.TYPE_WAIT_SHIP:
                tvOrderStatus.setText(getString(R.string.type_wait_ship));
                break;
            case AppConstant.TYPE_WAIT_RECEIPT:
                tvOrderStatus.setText(getString(R.string.type_wait_receipt));
                break;
            case AppConstant.TYPE_COMPLETE_GOODS:
                tvOrderStatus.setText(getString(R.string.type_complete_goods));
                break;
            default:
                break;
        }
    }

}
