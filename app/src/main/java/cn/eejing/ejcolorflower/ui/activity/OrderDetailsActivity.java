package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

public class OrderDetailsActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_order_details;
    }

    @Override
    public void initView() {
        setToolbar("订单详情", View.VISIBLE);
    }

}
