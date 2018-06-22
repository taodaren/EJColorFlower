package cn.eejing.ejcolorflower.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.adapter.ViewPagerAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.ui.fragment.MiOrderPageFragment;

/**
 * 我的订单
 */

public class MiOrderActivity extends BaseActivity implements View.OnClickListener {
    private static final int DEFAULT_SELECTION = 0;

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.tl_mi_order)
    TabLayout mTabLayout;
    @BindView(R.id.vp_mi_order)
    ViewPager mViewPager;

    private String[] mTitles = {"待发货", "待收货", "已完成"};
    private List<Fragment> mFragments;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_order;
    }

    @Override
    public void initView() {
        setToolbar("我的订单", View.VISIBLE);
        setTabPagerFragment();
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void setTabPagerFragment() {
        // 添加标签
        for (String mTitle : mTitles) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTitle));
        }

        // 添加 Fragment
        mFragments = new ArrayList<>();
        for (String mTitle : mTitles) {
            mFragments.add(MiOrderPageFragment.newInstance(mTitle));
        }

        // 设置预加载页面数量
        mViewPager.setOffscreenPageLimit(4);
        // 设置 adapter
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
        // 设置滚动条
        mTabLayout.setHorizontalScrollBarEnabled(true);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        // 将 TabLayout 和 ViewPager 关联起来
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setFocusable(true);
        // 设置默认选中页面
        mViewPager.setCurrentItem(DEFAULT_SELECTION);
    }

}
