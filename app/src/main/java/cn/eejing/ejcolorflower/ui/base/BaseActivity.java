package cn.eejing.ejcolorflower.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author taodaren
 * @date 2018/5/11
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(layoutViewId());
        initView();
        initData();
        initListener();
    }

    /**
     * 在 setContentView() 调用之前调用，可以设置 WindowFeature (如：this.requestWindowFeature(Window.FEATURE_NO_TITLE);)
     */
    public void init() {
    }

    public void initView() {
    }

    public void initData() {
    }

    public void initListener() {
    }

    /**
     * 由子类实现
     *
     * @return 当前界面的布局文件 id
     */
    protected abstract int layoutViewId();

}
