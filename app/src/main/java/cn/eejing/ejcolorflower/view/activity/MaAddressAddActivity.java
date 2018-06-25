package cn.eejing.ejcolorflower.view.activity;

import android.view.View;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

public class MaAddressAddActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_address_add;
    }

    @Override
    public void initView() {
        setToolbar("添加收货地址", View.VISIBLE);

    }

}
