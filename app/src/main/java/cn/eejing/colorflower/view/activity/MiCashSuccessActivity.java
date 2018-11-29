package cn.eejing.colorflower.view.activity;

import android.view.View;

import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 提现成功
 */

public class MiCashSuccessActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_cash_success;
    }

    @Override
    public void initView() {
        setToolbar("提现成功", View.VISIBLE, null, View.GONE);
    }

    @OnClick(R.id.btn_account_cash)
    public void onViewClicked() {
        finish();
    }

}
