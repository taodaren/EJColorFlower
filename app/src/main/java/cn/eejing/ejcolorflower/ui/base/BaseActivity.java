package cn.eejing.ejcolorflower.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;

/**
 * @创建者 Taodaren
 * @描述 Activity 基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        // 子类不再需要设置布局 ID，也不再需要使用 ButterKnife.BindView()
        setContentView(layoutViewId());
        ButterKnife.bind(this);

        setStatusBar();
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
     * 沉浸式状态栏
     */
    protected void setStatusBar() {
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));
    }

    /**
     * 设置 Toolbar
     *
     * @param title           标题
     * @param titleVisibility 标题控件是否显示
     */
    public void setToolbar(String title, int titleVisibility) {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 显示 Toolbar 左侧导航按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            // 设置导航按钮图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left);
            // 隐藏 Toolbar 自带标题栏
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // 设置标题
        TextView textTitle = findViewById(R.id.tv_title_toolbar);
        textTitle.setVisibility(titleVisibility);
        textTitle.setText(title);
        // 设置返回按钮
        ImageView imgTitleBack = findViewById(R.id.img_title_back);
        imgTitleBack.setVisibility(View.VISIBLE);
    }

    /**
     * 由子类实现
     *
     * @return 当前界面的布局文件 id
     */
    protected abstract int layoutViewId();

    public void jumpToActivity(Intent intent) {
        startActivity(intent);
    }

    public void jumpToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

}
