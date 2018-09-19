package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceStatus;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.presenter.ISendCommand;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.util.Util;
import cn.eejing.ejcolorflower.view.fragment.TabControlFragment;
import cn.eejing.ejcolorflower.view.fragment.TabDeviceFragment;
import cn.eejing.ejcolorflower.view.fragment.TabMallFragment;
import cn.eejing.ejcolorflower.view.fragment.TabMineFragment;

import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.ejcolorflower.app.AppConstant.EXIT_LOGIN;
import static cn.eejing.ejcolorflower.app.AppConstant.UUID_GATT_CHARACTERISTIC_WRITE;
import static cn.eejing.ejcolorflower.app.AppConstant.UUID_GATT_SERVICE;

public class MainActivity extends BLEManagerActivity implements ISendCommand, BottomNavigationBar.OnTabSelectedListener {
    private static final String TAG = "MainActivity";
    private final static int ACK_TIMEOUT = 1000;

    private List<Fragment> mFragments;
    private Fragment mCurrentFragment;

    private boolean mRequestConfig;

    static private MainActivity AppInstance;

    public static MainActivity getAppCtrl() {
        return AppInstance;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        AppInstance = this;
        addActivity(EXIT_LOGIN, this);

        initBtnNavBar();
        mFragments = getFragments();
        setDefFragment();

        addScanFilter(UUID_GATT_SERVICE);
        refresh();
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
        BottomNavigationBar mNavBar = findViewById(R.id.bottom_navigation_bar);

        // 设置模块名背景色
        mNavBar.setBarBackgroundColor(R.color.colorPrimary);
        // 设置背景模式
        mNavBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        // 设置Tab点击的模式
        mNavBar.setMode(BottomNavigationBar.MODE_FIXED);

        // 添加 Tab
        mNavBar
                // 设置导航图标及名称
                .addItem(new BottomNavigationItem(R.drawable.tab_device, R.string.device_name)
                        // 导航背景颜色
                        .setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_control, R.string.control_name)
                        .setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_mall, R.string.mall_name)
                        .setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_mine, R.string.mine_name)
                        .setActiveColorResource(R.color.colorWhite))
                // 默认显示面板
                .setFirstSelectedPosition(0)
                // 初始化
                .initialise();

        // 设置事件监听器
        mNavBar.setTabSelectedListener(this);
    }

    /** 将 Fragment 加入 fragments 里面 */
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(TabDeviceFragment.newInstance());
        list.add(TabControlFragment.newInstance());
        list.add(TabMallFragment.newInstance());
        list.add(TabMineFragment.newInstance());
        return list;
    }

    /** 设置默认 fragment */
    private void setDefFragment() {
        Fragment defFragment = mFragments.get(0);
        if (!defFragment.isAdded()) {
            addFragment(R.id.main_content, defFragment);
            mCurrentFragment = defFragment;
        }
    }

    /** 添加 Fragment 到 Activity 的布局 */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
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
    protected void setStatusBar() {
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private final List<Device> mDevList = new LinkedList<>();
    private final Map<String, ProtocolWithDevice> mProtocolMap = new ArrayMap<>();

    private class ProtocolWithDevice extends BleDeviceProtocol {
        final Device device;

        ProtocolWithDevice(@NonNull Device device) {
            this.device = device;
        }

        @Override
        protected void onReceivePkg(@NonNull DeviceStatus state) {
            device.setState(state);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mDevListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void onReceivePkg(@NonNull DeviceConfig config) {
            device.setConfig(config);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mDevListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void onReceivePkg(@NonNull final byte[] pkg, int pkg_len) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doMatch(device.getAddress(), pkg);
                }
            });
        }
    }

    private Device getDevice(String mac) {
        for (Device device : mDevList) {
            if (device.getAddress().equals(mac)) {
                return device;
            }
        }
        return null;
    }

    private void addDevice(final String mac, long id) {
        if (!mProtocolMap.containsKey(mac)) {
            final Device dev = new Device(mac);
            dev.setId(id);
            mDevList.add(dev);
//            mDevListAdapter.notifyDataSetChanged();
            addDeviceByMac(mac);
            mProtocolMap.put(mac, new ProtocolWithDevice(dev));
        }
    }

    /** 设备就绪 */
    @Override
    void onDeviceReady(final String mac) {
        Log.i(TAG, "onDeviceReady " + mac);
        if (setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE)) {
            Device device = getDevice(mac);

            if (device != null) {
                device.setConnected(true);
            }

            registerRefreshStatus(mac);
        }
    }

    private void registerRefreshStatus(final String mac) {
        registerPeriod(mac + "-status", new Runnable() {
            @Override
            public void run() {
                ProtocolWithDevice pd = mProtocolMap.get(mac);
                if (pd == null) {
                    return;
                }
                Device device = getDevice(mac);
                if (device != null) {
                    DeviceConfig config = device.getConfig();
                    long id = (config == null) ? 0 : config.mID;
                    if (config == null || mRequestConfig) {
                        mRequestConfig = !send(mac, BleDeviceProtocol.pkgGetConfig(id), true);
                    }
                    send(mac, BleDeviceProtocol.pkgGetStatus(id), true);
                }
            }
        }, 2000);
    }

    @Override
    void onDeviceConnect(String mac) {
        Log.i(TAG, "onDeviceConnect " + mac);
        registerRefreshStatus(mac);

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(true);
//            mDevListAdapter.notifyDataSetChanged();
        } else {
            Log.i(TAG, "onDeviceConnect no device");
        }
    }

    /** 设备断开 */
    @Override
    void onDeviceDisconnect(String mac) {
        unregisterPeriod(mac + "-status");

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(false);
//            mDevListAdapter.notifyDataSetChanged();
        }

//        // 发送设备相关数据（设备已断开）
//        EventBus.getDefault().post(new DeviceConnectEvent(DEVICE_CONNECT_NO, mac, device.getState(), device.getConfig()));
    }

    @Override
    void onReceive(String mac, byte[] data) {
        super.onReceive(mac, data);
        BleDeviceProtocol p = mProtocolMap.get(mac);
        if (p != null) {
            p.bleReceive(data);
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg) {
        String mac = device.getAddress();
        BleDeviceProtocol p = mProtocolMap.get(mac);
        if (p != null) {
            send(device.getAddress(), pkg, true);
            mRequestConfig = true;
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback) {
        mPackageNeedAckList.push(new PackageNeedAck(device.getAddress(), pkg, callback));
        sendCommand(device, pkg);
    }

    private static class PackageNeedAck {
        final byte[] cmd_pkg;
        final String mac;
        final long send_time;
        final OnReceivePackage callback;

        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback) {
            this.mac = mac;
            this.cmd_pkg = cmd_pkg;
            this.callback = callback;
            this.send_time = System.currentTimeMillis();
        }

        boolean isTimeout() {
            return (System.currentTimeMillis() - send_time) > ACK_TIMEOUT;
        }
    }

    private final LinkedList<PackageNeedAck> mPackageNeedAckList = new LinkedList<>();

    private void doMatch(String mac, byte[] ack_pkg) {
        Log.i(TAG, "doMatch " + Util.hex(ack_pkg, ack_pkg.length) + " from " + mPackageNeedAckList.size());
        for (PackageNeedAck p : mPackageNeedAckList) {
            Log.i(TAG, "doMatch check " + Util.hex(p.cmd_pkg, p.cmd_pkg.length));
            if (p.mac.equals(mac) && BleDeviceProtocol.isMatch(p.cmd_pkg, ack_pkg)) {
                Log.i(TAG, "doMatch match");
                mPackageNeedAckList.remove(p);
                p.callback.ack(ack_pkg);
                return;
            }
        }
    }

    private void doTimeoutCheck() {
        for (PackageNeedAck p : mPackageNeedAckList) {
            if (p.isTimeout()) {
                mPackageNeedAckList.remove(p);
                p.callback.timeout();
                return;
            }
        }
    }

    @Override
    void poll() {
        super.poll();
        doTimeoutCheck();
    }

}
