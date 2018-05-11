package cn.eejing.ejcolorflower.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.eejing.ejcolorflower.R;

/**
 * @author taodaren
 * @date 2018/5/11
 */

public abstract class BaseFragment extends Fragment {
    protected View fragmentLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(layoutViewId(), container, false);
        fragmentLayout = initView(rootView);
        return fragmentLayout;
    }

    public View initView(View rootView) {
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initListener();
    }

    public void initData() {
    }

    public void initListener() {
    }

    /**
     * 由子类实现
     *
     * @return 得到当前界面的布局文件 id（）
     */
    protected abstract int layoutViewId();

    /**
     * 设置 Toolbar
     *
     * @param toolbarId       menu_toolbar ID
     * @param title           标题
     * @param titleVisibility 标题控件是否显示
     */
    public void setToolbar(int toolbarId, int title, int titleVisibility ) {
        // Fragment 中使用 Toolbar
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        Toolbar toolbar = fragmentLayout.findViewById(toolbarId);
        assert appCompatActivity != null;
        appCompatActivity.setSupportActionBar(toolbar);

        // 设置标题
        TextView textTitle = fragmentLayout.findViewById(R.id.title_toolbar);
        textTitle.setVisibility(titleVisibility);
        textTitle.setText(title);

        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            // 隐藏 Toolbar 自带标题栏
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

}
