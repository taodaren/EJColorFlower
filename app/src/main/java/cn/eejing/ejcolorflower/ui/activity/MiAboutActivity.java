package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * @创建者 Taodaren
 * @描述 我的 → 关于我们
 */

public class MiAboutActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_about;
    }

    @Override
    public void initView() {
        setToolbar("关于我们", View.VISIBLE);
    }

}
