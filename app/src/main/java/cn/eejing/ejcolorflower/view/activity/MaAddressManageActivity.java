package cn.eejing.ejcolorflower.view.activity;

import android.view.View;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 管理收货地址
 */

public class MaAddressManageActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_address_manage;
    }

    @Override
    public void initView() {
        setToolbar("管理收货地址", View.VISIBLE);
    }

}
