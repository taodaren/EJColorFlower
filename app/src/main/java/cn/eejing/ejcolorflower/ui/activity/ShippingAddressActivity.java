package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 选择收货地址
 */

public class ShippingAddressActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_shipping_address)
    TextView tvTitle;
    @BindView(R.id.img_back_shipping_address)
    ImageView imgBack;
    @BindView(R.id.tv_manage_shipping_address)
    TextView tvManage;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_shipping_address;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择收货地址");
    }

    @Override
    public void initListener() {
        imgBack.setOnClickListener(this);
        tvManage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_shipping_address:
                finish();
                break;
            case R.id.tv_manage_shipping_address:
                Toast.makeText(this, "manage", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

}