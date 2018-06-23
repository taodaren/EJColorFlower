package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * @创建者 Taodaren
 * @描述 我的 → 设置 → 个人信息
 */
public class MiUserInfoActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_user_info;
    }

    @Override
    public void initView() {
        setToolbar("个人信息", View.VISIBLE);
    }

}
