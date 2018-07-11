package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.List;
import java.util.Map;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceState;
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

public class AppActivity extends BLEActivity implements ISendCommand,
        BottomNavigationBar.OnTabSelectedListener,
        TabDeviceFragment.OnFragmentInteractionListener,
        TabControlFragment.OnFragmentInteractionListener {
    private static final String TAG = "AppActivity";

    private List<Fragment> mFragments;
    private Fragment mCurrentFragment;

    private boolean mRequestConfig = false;
    //    private TabDeviceFragment.OnRecvHandler mTabDeviceOnRecvHandler;
    private TabControlFragment.OnRecvHandler mTabControlOnRecvHandler;

    // MAC 地址与设备 ID 对应关系
    private final Map<Long, String> mDeviceMacToId = new ArrayMap<>();
    // 设备控制
    private static FireworksDeviceControl mFireworksDeviceControl;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        addActivity(EXIT_LOGIN, this);

        initBtnNavBar();
        mFragments = getFragments();
        setDefFragment();

        mFireworksDeviceControl = mFireworksDeviceControlImpl;
        addScanFilter(UUID_GATT_SERVICE);

        // 配置当前 APP 处理的蓝牙设备名称
        setAllowedConnectDevicesName("EEJING-CHJ");
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 如果用以下这种做法则不保存状态，再次进来的话会显示默认tab
        // 总是执行这句代码来调用父类去保存视图层的状态
//        super.onSaveInstanceState(outState);
    }

    /**
     * 设置底部导航
     */
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

    /**
     * 将 Fragment 加入 fragments 里面
     */
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(TabDeviceFragment.newInstance());
        list.add(TabControlFragment.newInstance());
        list.add(TabMallFragment.newInstance());
        list.add(TabMineFragment.newInstance());
        return list;
    }

    /**
     * 设置默认 fragment
     */
    private void setDefFragment() {
        Fragment defFragment = mFragments.get(0);
        if (!defFragment.isAdded()) {
            addFragment(R.id.main_content, defFragment);
            mCurrentFragment = defFragment;
        }
    }

    /**
     * 添加 Fragment 到 Activity 的布局
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    /**
     * 切换 fragment
     */
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

    /**
     * Tab 被选中
     */
    @Override
    public void onTabSelected(int position) {
        replaceFragment(mFragments.get(position));
    }

    /**
     * Tab 被取消选中
     */
    @Override
    public void onTabUnselected(int position) {
    }

    /**
     * Tab 被重新选中
     */
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

    // TODO: /**<-------------------- 以下硬件交互相关 -------------------->**/

    /**
     * 通过服务器信息设置设备
     *
     * @param list 设备列表
     */
    @Override
    public void setRegisterDevice(List<DeviceListBean.DataBean.ListBean> list) {
//        BLEActivity.getBleCtrl().clearAllowedConnectDevicesMAC();
        // 清除允许连接的设备 MAC
        clearAllowedConnectDevicesMAC();
        // 清除 MAC 地址与设备 ID 对应关系
        mDeviceMacToId.clear();
        for (int i = 0; i < list.size(); i++) {
            // 添加允许连接的设备 MAC
            addAllowedConnectDevicesMAC(list.get(i).getMac());
            mDeviceMacToId.put(Long.parseLong(list.get(i).getId()), list.get(i).getMac());
        }
        // 更新允许连接的设备 MAC 地址列表后，删除已经连接的不在列表中多余的设备
        removeConnectedMoreDevice();
    }

    public String getMacById(Long id) {
        return mDeviceMacToId.get(id);
    }

    /**
     * 通过服务器给的设备id 查找设备（已经连接上的设备）
     *
     * @param deviceId 服务器设备 id
     * @return 设备（已经连接）
     */
    public Device findDeviceById(long deviceId) {
        String mac = getMacById(deviceId);
        if (mac == null) {
            return null;
        }
        BLEActivity.DeviceManager dev = getConnectedDeviceByMac(mac);
        if (dev == null) {
            return null;
        }
        ProtocolWithDevice pt = (ProtocolWithDevice) (dev.bleDeviceProtocol);
        return pt.device;
    }

    private Device getDevice(String mac) {
        BLEActivity.DeviceManager dev = getConnectedDeviceByMac(mac);
        if (dev == null) {
            return null;
        }
        ProtocolWithDevice pt = (ProtocolWithDevice) (dev.bleDeviceProtocol);
        return pt.device;
    }

//    @Override
//    public void setRecvHandler(TabDeviceFragment.OnRecvHandler handler) {
//        mTabDeviceOnRecvHandler = handler;
//    }

    @Override
    public void setRecvHandler(TabControlFragment.OnRecvHandler handler) {
        mTabControlOnRecvHandler = handler;
    }

    // 彩花机设备控制
    public interface FireworksDeviceControl {
        void sendCommand(long device_id, @NonNull byte[] pkg);

        void sendCommand(long device_id, @NonNull byte[] pkg, OnReceivePackage callback);
    }

    private final FireworksDeviceControl mFireworksDeviceControlImpl = new FireworksDeviceControl() {
        @Override
        public void sendCommand(long device_id, @NonNull byte[] pkg) {
            Device device = findDeviceById(device_id);
            if (device != null) {
                AppActivity.this.sendCommand(device, pkg);
            }
        }

        @Override
        public void sendCommand(long device_id, @NonNull byte[] pkg, OnReceivePackage callback) {
            Device device = findDeviceById(device_id);
            if (device != null) {
                AppActivity.this.sendCommand(device, pkg, callback);
            }
        }
    };

    public static FireworksDeviceControl getFireworksDeviceControl() {
        return mFireworksDeviceControl;
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg) {
        send(device.getAddress(), pkg);
        mRequestConfig = true;
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback) {
        send(device.getAddress(), pkg, callback);
    }

    @Override
    void onDeviceReady(final String mac) {
        Log.i(TAG, "设备就绪 onDeviceReady: mac = " + mac);
        if (setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE)) {
            Device device = getDevice(mac);

            if (device != null) {
                device.setConnected(true);
            }

            registerPeriod(mac + "- 注册期 status", new Runnable() {
                @Override
                public void run() {
//                    Device device = findDeviceById(device_id);
//                    if (device != null) {
//                        AppActivity.this.sendCommand(device, pkg);
//                    }
                    final Device device = getDevice(mac);
                    if (device != null) {
                        DeviceConfig config = device.getConfig();
                        long id = (config == null) ? 0 : config.mID;
                        if (config == null || mRequestConfig) {
                            mRequestConfig = !send(mac, BleDeviceProtocol.get_config_package(id));
                        }
                        send(mac, BleDeviceProtocol.get_status_package(id));

                        if (findDeviceById(id) != null) {
                            // 已经连接的设备不为空，发送相关数据
                            EventBus.getDefault().post(new DeviceConnectEvent(DEVICE_CONNECT_YES, mac, device.getState(), device.getConfig()));
                        }
                        if (mTabControlOnRecvHandler != null) {
                            mTabControlOnRecvHandler.onConfig(device, config);
                        }
                    }
                }
            }, 2000);
        }

    }

    @Override
    void onDeviceDisconnect(String mac) {
        unregisterPeriod(mac + "- 设备断开 status");

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(false);
            EventBus.getDefault().post(new DeviceConnectEvent(DEVICE_CONNECT_NO, mac));
        }
    }

    /**
     * 设备与协议处理
     */
    private class ProtocolWithDevice extends BleDeviceProtocol {
        final Device device;

        ProtocolWithDevice(@NonNull Device device) {
            this.device = device;
        }

        @Override
        protected void onReceivePackage(@NonNull final DeviceState state) {
            device.setState(state);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (mTabDeviceOnRecvHandler != null) {
//                        mTabDeviceOnRecvHandler.onState(device, state);
//                    }
//                }
//            });
        }

        @Override
        protected void onReceivePackage(@NonNull final DeviceConfig config) {
            device.setConfig(config);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (mTabDeviceOnRecvHandler != null) {
//                        mTabDeviceOnRecvHandler.onConfig(config);
//                    }
//                }
//            });
        }

        @Override
        protected void onReceivePackage(@NonNull final byte[] pkg, int pkg_len) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doMatch(device.getAddress(), pkg);
                }
            });
        }
    }

    /**
     * 做匹配
     * <p>
     * 在接收到蓝牙数据，经过协议分析，提取数据包后， 通过下面的函数对数据包进行分析处理
     */
    private void doMatch(String mac, byte[] ack_pkg) {
        Log.i(TAG, "doMatch " + Util.hex(ack_pkg, ack_pkg.length));
        BLEActivity.DeviceManager dev = getConnectedDeviceByMac(mac);
        if (dev != null) {
            dev.doMatchPackage(ack_pkg);
        }
    }

    /**
     * 找到并连接一台设备
     */
    @Override
    protected void onFoundAndConnectOneDevice(DeviceManager dev) {
        super.onFoundAndConnectOneDevice(dev);
        final Device device = new Device(dev.mac);
        // 设置接收到蓝牙设备后的处理协议对象
        dev.setBleDeviceProtocol(new ProtocolWithDevice(device));
    }

}
