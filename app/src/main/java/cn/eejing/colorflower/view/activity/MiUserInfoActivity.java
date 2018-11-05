package cn.eejing.colorflower.view.activity;

import android.view.View;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.view.base.BaseActivity;
import cn.eejing.colorflower.util.MySettings;

/**
 * 个人信息
 */

public class MiUserInfoActivity extends BaseActivity {

    @BindView(R.id.stv_phone_num)       SuperTextView stvPhoneNum;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_user_info;
    }

    @Override
    public void initView() {
        setToolbar("个人信息", View.VISIBLE, null, View.GONE);
        // set cellphone number
        stvPhoneNum.setRightString(MySettings.getLoginSessionInfo(this).getUsername());
    }

}
