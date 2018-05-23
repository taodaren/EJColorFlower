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
import cn.eejing.ejcolorflower.ui.fragment.SimpleCardFragment;
import cn.eejing.ejcolorflower.util.ViewFindUtils;

public class DeviceDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;

    private String[] mTitles = {"温度", "DMX地址", "剩余时间"};
    private SegmentTabLayout mTabLayout;
    private View mDecorView;

    private List<Fragment> mFragments;
    private DePagerAdapter mAdapter;


    @Override
    protected int layoutViewId() {
        return R.layout.activity_device_details;
    }

    @Override
    public void initView() {
        mFragments = new ArrayList<>();
        // TODO: 2018/5/23 暂时写死
        setToolbar("888888", View.VISIBLE);
//        setToolbar(getIntent().getStringExtra("device_id"), View.VISIBLE);

        for (String title : mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title));
        }

        mDecorView = getWindow().getDecorView();
        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tl_device_del);

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
        final ViewPager vp = ViewFindUtils.find(mDecorView, R.id.vp_device);
        mAdapter = new DePagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        vp.setAdapter(mAdapter);

        // 设置 TabLayout 数据
        mTabLayout.setTabData(mTitles);
        // 设置 TabLayout 选择侦听器
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        // 添加页面更改侦听器
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        vp.setCurrentItem(0);
    }

}
