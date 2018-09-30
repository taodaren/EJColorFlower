package cn.eejing.ejcolorflower.view.activity;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.util.ViewFindUtils;
import cn.eejing.ejcolorflower.view.adapter.ViewPagerAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.view.fragment.PageDeviceInfoFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_MATERIAL_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_TEMP;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_TIME;

public class CtDevConfigActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks{
    private static final String TAG = "CtDevConfigActivity";

    @BindView(R.id.btn_add_material)        Button    btnAddMaterial;
    @BindView(R.id.btn_enter_master)        Button    btnEnterMaster;

    private String[] mTitles = {"温度", "时间"};
    private SegmentTabLayout mTabLayout;
    private View mDecorView;

    private List<Fragment> mFragments;
    private ViewPager mVPager;
    private int mPageType;
    private SelfDialogBase mDialog;
    private int mDmx;
    // 是否可以进入主控模式
    private boolean isEnterMasterCtrl;
    private long mDevId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_dev_config;
    }

    @Override
    public void initView() {
        setToolbar("设备配置", View.VISIBLE, null, View.GONE);

        mDevId = getIntent().getLongExtra(QR_DEV_ID, 0);
        Log.i(TAG, "mDevId: " + mDevId);

        int temp, time;
        // todo 暂时写死，设备获取温度及时间
        temp = 114;
        time = 3000;

        // 如果 DMX 为 0，true；反之 false
        isEnterMasterCtrl = mDmx == 0;

        mFragments = new ArrayList<>();
        mFragments.add(PageDeviceInfoFragment.newInstance(temp,  TYPE_TEMP, mDevId));
        mFragments.add(PageDeviceInfoFragment.newInstance(time,  TYPE_TIME, mDevId));

        mDecorView = getWindow().getDecorView();
        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tl_device_del);
        mPageType = getIntent().getIntExtra("page", 0);

        initTLVP();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    public void initListener() {
        btnAddMaterial.setOnClickListener(this);
        btnEnterMaster.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_material:
                startActivityForResult(new Intent(this, CtQrScanActivity.class), 1);
                break;
            case R.id.btn_enter_master:
                if (isEnterMasterCtrl) {
//                    jumpToActivity(new Intent(this, DeMasterModeActivity.class).putExtra("device_id", mDevId));
                    Toast.makeText(this, "工程师正在紧急开发中...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "DMX 为 0 方可进入主控模式", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    long devId = data.getLongExtra(QR_MATERIAL_ID, 0);
                    Log.d(TAG, "onActivityResult dev id: " + devId);
                    // 处理加料逻辑
                }
                break;
            default:
                break;
        }
    }

    private void initTLVP() {
        mVPager = ViewFindUtils.find(mDecorView, R.id.vp_device);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mVPager.setAdapter(adapter);

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


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
}
