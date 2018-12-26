package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.clj.fastble.BleManager;
import com.lzy.okgo.model.HttpParams;

import java.util.ArrayList;
import java.util.List;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.device.BleEEJingCtrl;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.QueryDevMacBean;
import cn.eejing.colorflower.model.session.LoginSession;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.BtnBarUtil;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;
import cn.eejing.colorflower.view.fragment.TabCtrlFragment;
import cn.eejing.colorflower.view.fragment.TabMallFragment;
import cn.eejing.colorflower.view.fragment.TabMineFragment;
import cn.eejing.colorflower.view.fragment.TabVideoFragment;
import cn.jzvd.Jzvd;

import static cn.eejing.colorflower.app.AppConstant.EXIT_LOGIN;
import static cn.eejing.colorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_FORCED_UPDATE;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_SCANNING_CONN_DEV;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {
    private static final String TAG = "MainActivity";

    private static MainActivity AppInstance;
    private List<Fragment> mFragments;
    private Fragment mCurrentFragment;
    private LoginSession mLoginSession;

    public static MainActivity getAppCtrl() {
        return AppInstance;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        // 运行时权限
        setRxPermission();
        // 强制版本更新
        forcedVersionUpdate();
        AppInstance = this;
        addActivity(EXIT_LOGIN, this);
        mLoginSession = MySettings.getLoginInfo(this);
        initBtnNavBar();
        getFragments();
        setDefFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开所有设备
        BleManager.getInstance().disconnectAllDevice();
        // 退出使用，清理资源
        BleManager.getInstance().destroy();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 如果用以下这种做法则不保存状态，再次进来的话会显示默认tab
        // 总是执行这句代码来调用父类去保存视图层的状态
//        super.onSaveInstanceState(outState);
    }

    /** 设置底部导航 */
    private void initBtnNavBar() {
        BottomNavigationBar navBar = findViewById(R.id.bottom_navigation_bar);
        navBar
                // 设置模块名选中及未选中背景色
                .setBarBackgroundColor(R.color.colorNavBar).setInActiveColor(R.color.colorTitle)
                // 设置背景模式
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                // 设置Tab点击的模式
                .setMode(BottomNavigationBar.MODE_FIXED)
                // 设置导航图标、名称及背景颜色
                .addItem(new BottomNavigationItem(R.drawable.tab_ctrl, R.string.control_name).setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_mall, R.string.mall_name).setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_video, R.string.video_name).setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_mine, R.string.mine_name).setActiveColorResource(R.color.colorWhite))
                // 默认显示面板
                .setFirstSelectedPosition(0)
                // 初始化
                .initialise();
        BtnBarUtil.setBottomNavigationItem(navBar, 0, 36, 12);
        // 设置事件监听器
        navBar.setTabSelectedListener(this);
    }

    /** 将 Fragment 加入 fragments 里面 */
    private void getFragments() {
        mFragments = new ArrayList<>();
        mFragments.add(TabCtrlFragment.newInstance());
        mFragments.add(TabMallFragment.newInstance());
        mFragments.add(TabVideoFragment.newInstance());
        mFragments.add(TabMineFragment.newInstance());
    }

    /** 设置默认 fragment */
    private void setDefFragment() {
        Fragment defFragment = mFragments.get(0);
        if (!defFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.main_content, defFragment).commit();
            mCurrentFragment = defFragment;
        }
    }

    /** 切换 fragment */
    @SuppressLint("CommitTransaction")
    private void replaceFragment(Fragment fragment) {
        // 添加或者显示 fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment == fragment)
            return;
        if (!fragment.isAdded()) {
            // 如果当前 fragment 未被添加，则添加到 Fragment 管理器中
            transaction.hide(mCurrentFragment).add(R.id.main_content, fragment).commit();
        } else {
            // 如果当前 fragment 已添加，则显示 Fragment 管理器中的 fragment
            transaction.hide(mCurrentFragment).show(fragment).commit();
        }
        mCurrentFragment = fragment;
    }

    /** Tab 被选中 */
    @Override
    public void onTabSelected(int position) {
        replaceFragment(mFragments.get(position));
    }

    /** Tab 被取消选中 */
    @Override
    public void onTabUnselected(int position) {
    }

    /** Tab 被重新选中 */
    @Override
    public void onTabReselected(int position) {
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showShort("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SCANNING_CONN_DEV:
                if (resultCode == RESULT_OK) {
                    mDevIdByQR = data.getLongExtra(QR_DEV_ID, 0);
                    // 获取 ID 对应 MAC
                    getDataWithQueryDevMac();
                }
                break;
            case REQUEST_CODE_FORCED_UPDATE:
                // 再次执行安装流程，包含权限判等
                installProcess();
                break;
            default:
                break;
        }
    }

    private long mDevIdByQR;

    @SuppressWarnings("unchecked")
    private void getDataWithQueryDevMac() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("device_id", mDevIdByQR);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.GET_DEVICE_MAC)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(QueryDevMacBean.class)
                .callback(new Callback<QueryDevMacBean>() {
                    @Override
                    public void onSuccess(QueryDevMacBean bean, int id) {
                        LogUtil.i(TAG, "设备 ID 获取 MAC 地址 请求成功");

                        if (bean.getCode() == 1) {
                            // 获取到服务器 MAC 后连接扫描连接设备
                            BleEEJingCtrl.getInstance().setDevId(mDevIdByQR)
                                    .startScan(bean.getData().getMac());
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    public long getDevId() {
        return mDevIdByQR;
    }

    public String getToken() {
        mLoginSession = MySettings.getLoginInfo(this);
        return mLoginSession.getToken();
    }

    public String getLevel() {
        return mLoginSession.getLevel();
    }

    public void setLevel(String lv) {
        mLoginSession.setLevel(lv);
    }

    public String getUserId() {
        return String.valueOf(mLoginSession.getUserId());
    }

}
