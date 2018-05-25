package cn.eejing.ejcolorflower.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.adapter.DePagerAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.ui.fragment.DevicePageFragment;
import cn.eejing.ejcolorflower.util.ViewFindUtils;

public class DeviceDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;

    private String[] mTitles = {"温度", "DMX地址", "剩余时间"};
    private SegmentTabLayout mTabLayout;
    private View mDecorView;

    private List<Fragment> mFragments;
    private DePagerAdapter mAdapter;
    private ViewPager mVPager;
    private int mPageType;
    private int mTempThreshold, mDmxAddress, mTimeLeft;

    public DeviceDetailsActivity() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.activity_device_details;
    }

    @Override
    public void initView() {
        setToolbar(getIntent().getStringExtra("device_id"), View.VISIBLE);
        // TODO: 18/5/26 假数据
        mTempThreshold = 500;
        mDmxAddress = 18;
        mTimeLeft = 300;

        mFragments = new ArrayList<>();
        mFragments.add(DevicePageFragment.newInstance(mTempThreshold));
        mFragments.add(DevicePageFragment.newInstance(mDmxAddress));
        mFragments.add(DevicePageFragment.newInstance(mTimeLeft));

        mDecorView = getWindow().getDecorView();
        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tl_device_del);
        mPageType = getIntent().getIntExtra("page", 0);

        initTLVP();
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
        }
    }

    private void initTLVP() {
        mVPager = ViewFindUtils.find(mDecorView, R.id.vp_device);
        mAdapter = new DePagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mVPager.setAdapter(mAdapter);

        // 设置 TabLayout 数据
        mTabLayout.setTabData(mTitles);
        // 设置 TabLayout 选择侦听器
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mVPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        // 添加页面更改侦听器
        mVPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // 在滚动页
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // 在选择的页面上
            @Override
            public void onPageSelected(int position) {
                // 设置当前标签
                mTabLayout.setCurrentTab(position);
            }

            // 在页面滚动状态改变
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 设置默认选中页面
        mVPager.setCurrentItem(mPageType);
    }

}
