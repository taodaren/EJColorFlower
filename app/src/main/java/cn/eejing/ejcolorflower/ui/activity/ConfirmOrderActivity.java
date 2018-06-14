package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 确认订单
 */

public class ConfirmOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.btn_submit_order)
    Button btnSubmitOrder;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_confirm_order;
    }

    @Override
    public void initView() {
        setToolbar("确认订单", View.VISIBLE);
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
        btnSubmitOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            case R.id.btn_submit_order:
                jumpToActivity(PaymentOrderActivity.class);
                break;
            default:
                break;
        }
    }

}
