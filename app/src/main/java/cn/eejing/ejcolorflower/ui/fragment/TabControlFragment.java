package cn.eejing.ejcolorflower.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * 控制模块
 *
 * @author taodaren
 * @date 2018/5/11
 */

public class TabControlFragment extends BaseFragment {

    public static TabControlFragment newInstance() {
        return new TabControlFragment();
    }

    public TabControlFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_control;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 在 onActivityCreated 方法中初始化 Toolbar
        setToolbar(R.id.main_toolbar, R.string.control_name, View.VISIBLE);
    }

}
