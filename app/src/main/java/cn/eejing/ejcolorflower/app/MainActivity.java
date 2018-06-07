package cn.eejing.ejcolorflower.app;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.ArrayMap;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceMaterialStatus;
import cn.eejing.ejcolorflower.device.DeviceState;
import cn.eejing.ejcolorflower.device.ISendCommand;
import cn.eejing.ejcolorflower.device.OnReceivePackage;
import cn.eejing.ejcolorflower.device.Protocol;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.ui.fragment.TabControlFragment;
import cn.eejing.ejcolorflower.ui.fragment.TabDeviceFragment;
import cn.eejing.ejcolorflower.ui.fragment.TabMallFragment;
import cn.eejing.ejcolorflower.ui.fragment.TabMineFragment;
import cn.eejing.ejcolorflower.util.Util;

import static cn.eejing.ejcolorflower.app.AppConstant.ACK_TIMEOUT;
import static cn.eejing.ejcolorflower.app.AppConstant.UUID_GATT_CHARACTERISTIC_WRITE;
import static cn.eejing.ejcolorflower.app.AppConstant.UUID_GATT_SERVICE;

public class MainActivity extends BLEManagerActivity implements ISendCommand,
        BottomNavigationBar.OnTabSelectedListener,
        TabDeviceFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    private ArrayList<Fragment> mFragments;
    private List<Device> mDeviceList;
    private List<String> mFoundDeviceAddressList;
    private Map<String, ProtocolWithDevice> mProtocolList;
    private LinkedList<PackageNeedAck> mPackageNeedAckList;

    private boolean mRequestConfig = false;
    private TabDeviceFragment.OnRecvHandler mTabDeviceOnRecvHandler;
    // 设备控制
    private static FireworksDeviceControl mFireworksDeviceControl;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initBtnNavBar();
        mFragments = getFragments();
        setDefFragment();

        mDeviceList = new LinkedList<>();
        mFoundDeviceAddressList = new LinkedList<>();
        mProtocolList = new ArrayMap<>();
        mPackageNeedAckList = new LinkedList<>();

        mFireworksDeviceControl = mFireworksDeviceControlImpl;

        addScanFilter(UUID_GATT_SERVICE);
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
     * 设置默认 fragment
     */
    private void setDefFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_content, TabDeviceFragment.newInstance());
        transaction.commit();
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
     * Tab 被选中
     */
    @Override
    public void onTabSelected(int position) {
        // 点击时加载对应的 fragment
        if (mFragments != null) {
            if (position < mFragments.size()) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = mFragments.get(position);
                transaction.replace(R.id.main_content, fragment);
                transaction.commitAllowingStateLoss();
            }
        }
    }

    /**
     * Tab 被取消选中
     */
    @Override
    public void onTabUnselected(int position) {
        if (mFragments != null) {
            if (position < mFragments.size()) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = mFragments.get(position);
                transaction.remove(fragment);
                transaction.commitAllowingStateLoss();
            }
        }
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


    // TODO: /**<-------------------- 以下硬件交互相关 -------------------->**/
    @Override
    public void scanDevice() {
        refresh();
    }

    @Override
    void onStopScan() {
    }

    @Override
    public void setRegisterDevice(List<DeviceListBean.DataBean.ListBean> list) {
        List<String> rmMac = new LinkedList<>();
        List<Device> rmDevice = new LinkedList<>();
        for (String mac : mProtocolList.keySet()) {
            if (!contain(list, mac)) {
                rmMac.add(mac);
                rmDevice.add(getDevice(mac));
            }
        }
        for (String mac : rmMac) {
            mProtocolList.remove(mac);
        }
        for (Device d : rmDevice) {
            mDeviceList.remove(d);
        }
    }

    @Override
    public void setRecvHandler(TabDeviceFragment.OnRecvHandler handler) {
        mTabDeviceOnRecvHandler = handler;
    }

    @Override
    void onFoundDevice(BluetoothDevice device, @Nullable List<ParcelUuid> serviceUuids) {
        String name = device.getName();
        String mac = device.getAddress();

        Log.i(TAG, "找到设备---> mac = " + mac + "  name = " + name);

        if (name.indexOf("EEJING-CHJ") != 0) {
            return;
        }

        if (!mFoundDeviceAddressList.contains(mac)) {
            mFoundDeviceAddressList.add(mac);
        }

        add_device(mac, 0);
    }

    public interface FireworksDeviceControl {
        void sendCommand(long device_id, @NonNull byte[] pkg);

        void sendCommand(long device_id, @NonNull byte[] pkg, OnReceivePackage callback);
    }

    private Device findDeviceById(long deviceId) {
        for (Device device : mDeviceList) {
            if (device.getId() == deviceId) {
                return device;
            }
        }
        return null;
    }

    private final FireworksDeviceControl mFireworksDeviceControlImpl = new FireworksDeviceControl() {
        @Override
        public void sendCommand(long device_id, @NonNull byte[] pkg) {
            Device device = findDeviceById(device_id);
            if (device != null) {
                MainActivity.this.sendCommand(device, pkg);
            }
        }

        @Override
        public void sendCommand(long device_id, @NonNull byte[] pkg, OnReceivePackage callback) {
            Device device = findDeviceById(device_id);
            if (device != null) {
                MainActivity.this.sendCommand(device, pkg, callback);
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
        mPackageNeedAckList.push(new PackageNeedAck(device.getAddress(), pkg, callback));
        send(device.getAddress(), pkg);
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
                            final Device device = getDevice(mac);
                            if (device != null) {
                                DeviceConfig config = device.getConfig();
//                                Log.e(TAG, "run: onDeviceReady = " + config.mDMXAddress);
                                long id = (config == null) ? 0 : config.mID;
                                if (config == null || mRequestConfig) {
                                    mRequestConfig = !send(mac, Protocol.get_config_package(id));
                                }
                                send(mac, Protocol.get_status_package(id));
                            }
                        }
                    },
                    2000);
        }

    }

    @Override
    void onReceive(String mac, byte[] data) {
        super.onReceive(mac, data);
        Protocol p = mProtocolList.get(mac);
        if (p != null) {
            p.onReceive(data);
        }
    }

    @Override
    void onDeviceDisconnect(String mac) {
        unregisterPeriod(mac + "- 设备断开 status");

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(false);
        }
    }

    @Override
    void poll() {
        super.poll();
        doTimeoutCheck();
    }

    private Device getDevice(String mac) {
        for (Device device : mDeviceList) {
            if (device.getAddress().equals(mac)) {
                return device;
            }
        }
        return null;
    }

    private void add_device(final String mac, long id) {
        Log.e(TAG, "add_device: MAC--->" + mac);

        if (!mProtocolList.containsKey(mac)) {
            final Device device = new Device(mac);
            device.setId(id);
            mDeviceList.add(device);

            Log.e(TAG, "add_device: mDeviceList--->" + mDeviceList.size());

            addDevice(mac);
            mProtocolList.put(mac, new ProtocolWithDevice(device));
        }
    }

    // 做超时检查
    private void doTimeoutCheck() {
        for (PackageNeedAck p : mPackageNeedAckList) {
            if (p.isTimeout()) {
                mPackageNeedAckList.remove(p);
                p.callback.timeout();
                return;
            }
        }
    }

    // 做匹配
    private void doMatch(String mac, byte[] ack_pkg) {
        Log.i(TAG, "doMatch " + Util.hex(ack_pkg, ack_pkg.length) + " from " + mPackageNeedAckList.size());
        for (PackageNeedAck p : mPackageNeedAckList) {
            Log.i(TAG, "doMatch check " + Util.hex(p.cmd_pkg, p.cmd_pkg.length));
            if (p.mac.equals(mac) && Protocol.isMatch(p.cmd_pkg, ack_pkg)) {
                Log.i(TAG, "doMatch match");
                mPackageNeedAckList.remove(p);
                p.callback.ack(ack_pkg);
                return;
            }
        }
    }

    // 设备与协议处理
    private class ProtocolWithDevice extends Protocol {
        final Device device;

        ProtocolWithDevice(@NonNull Device device) {
            this.device = device;
        }

        @Override
        protected void onReceivePackage(@NonNull final DeviceState state) {
            device.setState(state);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mTabDeviceOnRecvHandler != null) {
                        mTabDeviceOnRecvHandler.onState(device, state);
                    }
                }
            });
        }

        @Override
        protected void onReceivePackage(@NonNull final DeviceConfig config) {
            device.setConfig(config);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mTabDeviceOnRecvHandler != null) {
                        mTabDeviceOnRecvHandler.onConfig(config);
                    }
                }
            });
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

    private static boolean contain(List<DeviceListBean.DataBean.ListBean> list, String mac) {
        for (DeviceListBean.DataBean.ListBean d : list) {
            if (d.getMac().equals(mac)) {
                return true;
            }
        }
        return false;
    }

}
