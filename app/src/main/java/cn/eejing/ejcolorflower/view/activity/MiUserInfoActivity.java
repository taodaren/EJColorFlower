package cn.eejing.ejcolorflower.view.activity;

import android.view.View;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Settings;

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
        setToolbar("个人信息", View.VISIBLE);
        // set cellphone number
        stvPhoneNum.setRightString(Settings.getLoginSessionInfo(this).getUsername());
    }

}
