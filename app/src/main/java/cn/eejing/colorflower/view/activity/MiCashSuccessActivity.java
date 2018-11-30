package cn.eejing.colorflower.view.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    @Override
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        super.setToolbar(title, titleVisibility, menu, menuVisibility);
        ImageView imgBill = findViewById(R.id.img_vip_toolbar);
        ViewGroup.LayoutParams params = imgBill.getLayoutParams();
        params.height = 68;
        params.width = 68;
        imgBill.setLayoutParams(params);
        imgBill.setVisibility(View.VISIBLE);
        imgBill.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_bill));
    }

    @OnClick({R.id.img_vip_toolbar, R.id.btn_account_cash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_vip_toolbar:
                jumpToActivity(MiBillActivity.class);
                break;
            case R.id.btn_account_cash:
                finish();
                break;
        }
    }

}
