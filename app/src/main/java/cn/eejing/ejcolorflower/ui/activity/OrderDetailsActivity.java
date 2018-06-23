package cn.eejing.ejcolorflower.ui.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.OrderDetailsBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 订单详情
 */

public class OrderDetailsActivity extends BaseActivity {

    @BindView(R.id.tv_order_dtl_status)
    TextView tvOrderStatus;
    @BindView(R.id.tv_order_dtl_consignee)
    TextView tvConsignee;
    @BindView(R.id.tv_order_dtl_phone)
    TextView tvPhone;
    @BindView(R.id.tv_order_dtl_address)
    TextView tvAddress;
    @BindView(R.id.tv_order_dtl_name)
    TextView tvName;
    @BindView(R.id.tv_order_dtl_money)
    TextView tvMoney;
    @BindView(R.id.tv_order_dtl_num)
    TextView tvNum;
    @BindView(R.id.tv_order_dtl_odd)
    TextView tvOdd;
    @BindView(R.id.tv_order_dtl_time)
    TextView tvTime;
    @BindView(R.id.img_order_dtl_show)
    ImageView imgShow;

    private int mOrderId;
    private String mType;
    private Gson mGson;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_order_details;
    }

    @Override
    public void initView() {
        setToolbar("订单详情", View.VISIBLE);
        mType = getIntent().getStringExtra("type");
        mOrderId = getIntent().getIntExtra("order_id", 0);
        mGson = new Gson();
    }

    @Override
    public void setToolbar(String title, int titleVisibility) {
        // 沉浸式状态栏
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorWhite));

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackground(getDrawable(R.color.colorWhite));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 隐藏 Toolbar 左侧导航按钮
            actionBar.setDisplayHomeAsUpEnabled(false);
            // 隐藏 Toolbar 自带标题栏
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // 设置标题
        TextView textTitle = findViewById(R.id.tv_title_toolbar);
        textTitle.setVisibility(titleVisibility);
        textTitle.setTextColor(getResources().getColor(R.color.colorGroupName));
        textTitle.setText(title);

        // 设置返回按钮
        ImageView imgTitleBack = findViewById(R.id.img_title_back);
        imgTitleBack.setVisibility(View.VISIBLE);
        imgTitleBack.setImageDrawable(getDrawable(R.drawable.ic_arrow_black));
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        getDataWithOrderDtl();
    }

    private void getDataWithOrderDtl() {
        OkGo.<String>post(Urls.ORDER_DETAILS)
                .tag(this)
                .params("order_id", mOrderId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "order_details request succeeded --->" + body);

                        OrderDetailsBean bean = mGson.fromJson(body, OrderDetailsBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                setData(bean.getData());
                                break;
                            case 0:
                                Toast.makeText(OrderDetailsActivity.this, "亲，请检查您的网络设置", Toast.LENGTH_SHORT).show();
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
