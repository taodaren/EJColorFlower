package cn.eejing.ejcolorflower.view.activity;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.allen.library.SuperButton;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.DelDeviceEvent;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.util.ViewFindUtils;
import cn.eejing.ejcolorflower.view.adapter.ViewPagerAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.view.fragment.PageDeviceInfoFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;

/**
 * 设备详情
 */

public class DeDeviceDetailsActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.btn_add_material)         SuperButton btnAddMaterial;
    @BindView(R.id.btn_remove_device)        SuperButton btnRemoveDevice;

    private String[] mTitles = {"温度", "DMX地址", "剩余时间"};
    private SegmentTabLayout mTabLayout;
    private View mDecorView;

    private List<Fragment> mFragments;
    private ViewPager mVPager;
    private int mPageType;
    private String mDeviceId, mMemberId, mToken;
    private SelfDialogBase mDialog;

    public DeDeviceDetailsActivity() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_device_details;
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    public void initView() {
        mDeviceId = getIntent().getStringExtra("device_id");
        mMemberId = getIntent().getStringExtra("member_id");
        mToken = getIntent().getStringExtra("token");
        setToolbar(mDeviceId, View.VISIBLE);

        int temp, dmx, time, tempThresholdHigh;
        temp = getIntent().getIntExtra("device_temp", 0);
        dmx = getIntent().getIntExtra("device_dmx", 0);
        time = getIntent().getIntExtra("device_time", 0);
        tempThresholdHigh = getIntent().getIntExtra("device_threshold", 0);

        mFragments = new ArrayList<>();
        mFragments.add(PageDeviceInfoFragment.newInstance(temp, tempThresholdHigh, AppConstant.TYPE_TEMP, Long.parseLong(mDeviceId)));
        mFragments.add(PageDeviceInfoFragment.newInstance(dmx, tempThresholdHigh, AppConstant.TYPE_DMX, Long.parseLong(mDeviceId)));
        mFragments.add(PageDeviceInfoFragment.newInstance(time, tempThresholdHigh, AppConstant.TYPE_TIME, Long.parseLong(mDeviceId)));

        mDecorView = getWindow().getDecorView();
        mTabLayout = ViewFindUtils.find(mDecorView, R.id.tl_device_del);
        mPageType = getIntent().getIntExtra("page", 0);

        initTLVP();
    }

    @Override
    public void initListener() {
        btnAddMaterial.setOnClickListener(this);
        btnRemoveDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_material:
                Intent intent = new Intent(this, DeQrAddMaterialActivity.class);
                intent.putExtra("device_id", mDeviceId);
                jumpToActivity(intent);
                finish();
                break;
            case R.id.btn_remove_device:
                showDialog();
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

    private void showDialog() {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle("确认将 " + mDeviceId + " 删除？");
        mDialog.setYesOnclickListener("确认", new SelfDialogBase.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                // 删除设备
                getDataWithDelDevice();
                mDialog.dismiss();
            }
        });
        mDialog.setNoOnclickListener("取消", new SelfDialogBase.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void getDataWithDelDevice() {
        OkGo.<String>post(Urls.RM_DEVICE)
                .tag(this)
                .params("member_id", mMemberId)
                .params("device_id", mDeviceId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "rm device request succeeded--->" + body);
                        EventBus.getDefault().post(new DelDeviceEvent(mDeviceId));
                        finish();
                    }
                });
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
