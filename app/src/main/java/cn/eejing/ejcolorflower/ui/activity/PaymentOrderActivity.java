package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.ImageView;

import com.allen.library.SuperButton;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 订单支付
 */

public class PaymentOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.btn_pay)
    SuperButton btnPay;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_payment_order;
    }

    @Override
    public void initView() {
        setToolbar("订单支付", View.VISIBLE);
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
                // pay
                getOrderInfo();
                break;
            default:
                break;
        }
    }

    private void getOrderInfo() {
        // TODO: 2018/6/14  生成订单信息接口
    }

}
