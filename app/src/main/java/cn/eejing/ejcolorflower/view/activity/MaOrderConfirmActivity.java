package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.GApp;
import cn.eejing.ejcolorflower.model.event.AddrAddEvent;
import cn.eejing.ejcolorflower.model.request.AddrListBean;
import cn.eejing.ejcolorflower.model.request.ConfirmOrderBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.MySettings;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.FROM_ORDER_TO_ADDR;
import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_ADDR_SELECT;

/**
 * 确认订单
 */

public class MaOrderConfirmActivity extends BaseActivity {

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
    @BindView(R.id.tv_confirm_order_postage_full)          TextView     tvPostageFull;
    @BindView(R.id.tv_confirm_order_postage_basics)        TextView     tvPostageBasics;
    @BindView(R.id.tv_confirm_order_total_money)           TextView     tvTotalMoney;

    private ConfirmOrderBean.DataBean mBean;
    private Gson mGson;
    private String mMemberId, mToken;
    private int mGoodsId, mNumber;
    private double mTotalMoney;
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
        mMemberId = String.valueOf(MySettings.getLoginSessionInfo(this).getMember_id());
        mToken = MySettings.getLoginSessionInfo(this).getToken();
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
                if (tvConsignee.getText().length() > 0 && tvPhone.getText().length() > 0 && tvAddress.getText().length() > 0) {
                    Intent intent = new Intent(this, MaOrderPayActivity.class);
                    intent.putExtra("goods_id", mGoodsId);
                    intent.putExtra("quantity", mNumber);
                    intent.putExtra("address_id", mAddressId);
                    intent.putExtra("member_id", mMemberId);
                    intent.putExtra("token", mToken);
                    intent.putExtra("money", mTotalMoney);
                    jumpToActivity(intent);
                } else {
                    Toast.makeText(this, getString(R.string.text_add_addr), Toast.LENGTH_SHORT).show();
                }
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
                if (mBean.getAddress().getName() == null) {
                    GApp gApp = (GApp) getApplication();
                    gApp.setFlagAddrMgr(FROM_ORDER_TO_ADDR);
                    jumpToActivity(MaAddrManageActivity.class);
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

                tvConsignee.setText(getString(R.string.text_consignee) + bean.getName());
                tvPhone.setText(bean.getMobile());
                tvAddress.setText(getString(R.string.text_shipping_address) + bean.getAddress_all());
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
        // 如果地址内容不为空，设置显示信息
        if (mBean.getAddress().getName() != null || mBean.getAddress().getMobile() != null || mBean.getAddress().getAddress_all() != null) {
            tvAddrNull.setVisibility(View.GONE);
            layoutAddr.setVisibility(View.VISIBLE);
            tvConsignee.setText(getString(R.string.text_consignee) + mBean.getAddress().getName());
            tvPhone.setText(mBean.getAddress().getMobile());
            tvAddress.setText(getString(R.string.text_shipping_address) + mBean.getAddress().getAddress_all());
        } else {
            tvAddrNull.setVisibility(View.VISIBLE);
            layoutAddr.setVisibility(View.GONE);
        }

        Glide.with(this).load(mBean.getGoods().getImage()).into(imgGoods);
        tvName.setText(mBean.getGoods().getName());
        tvMoney.setText(getString(R.string.rmb) + mBean.getGoods().getMoney());
        tvPostageBasics.setText(getString(R.string.basic_postage) + mBean.getGoods().getBasics_postage());
        tvPostageFull.setText(getString(R.string.postage_before) + mBean.getGoods().getPostage() + getString(R.string.postage_after));

        mNumber = 1;
        mAddressId = mBean.getAddress().getId();
        display(mNumber);
    }

}
