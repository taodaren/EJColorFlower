package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.ArrayMap;
import android.view.KeyEvent;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.device.Device;
import cn.eejing.colorflower.model.device.DeviceConfig;
import cn.eejing.colorflower.model.device.DeviceStatus;
import cn.eejing.colorflower.model.event.DevConnEvent;
import cn.eejing.colorflower.model.request.QueryDevMacBean;
import cn.eejing.colorflower.model.session.LoginSession;
import cn.eejing.colorflower.presenter.ISendCommand;
import cn.eejing.colorflower.presenter.OnReceivePackage;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.BleDevProtocol;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.util.Util;
import cn.eejing.colorflower.view.fragment.TabCtrlFragment;
import cn.eejing.colorflower.view.fragment.TabMallFragment;
import cn.eejing.colorflower.view.fragment.TabMineFragment;
import cn.eejing.colorflower.view.fragment.TabVideoFragment;

import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.colorflower.app.AppConstant.EXIT_LOGIN;
import static cn.eejing.colorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_FORCED_UPDATE;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_SCANNING_CONN_DEV;
import static cn.eejing.colorflower.app.AppConstant.UUID_GATT_CHARACTERISTIC_WRITE;
import static cn.eejing.colorflower.app.AppConstant.UUID_GATT_SERVICE;

public class MainActivity extends BLEManagerActivity implements ISendCommand, BottomNavigationBar.OnTabSelectedListener {
    private static final String TAG = "MainActivity";

    private List<Fragment> mFragments;
    private Fragment mCurrentFragment;

    private LoginSession mLoginSession;
    private Gson mGson;

    private static MainActivity AppInstance;

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
        mGson = new Gson();

        initBtnNavBar();
        mFragments = getFragments();
        setDefFragment();

        scanRefresh();
    }

    public void scanRefresh() {
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
                .addItem(new BottomNavigationItem(R.drawable.tab_mall, R.string.video_name).setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_mine, R.string.mine_name).setActiveColorResource(R.color.colorWhite))
                // 默认显示面板
                .setFirstSelectedPosition(0)
                // 初始化
                .initialise();
        // 设置事件监听器
        navBar.setTabSelectedListener(this);
    }

    /** 将 Fragment 加入 fragments 里面 */
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(TabCtrlFragment.newInstance());
        list.add(TabMallFragment.newInstance());
        list.add(TabVideoFragment.newInstance());
        list.add(TabMineFragment.newInstance());
        return list;
    }

    /** 设置默认 fragment */
    private void setDefFragment() {
        Fragment defFragment = mFragments.get(0);
        if (!defFragment.isAdded()) {
            addFragment(defFragment);
            mCurrentFragment = defFragment;
        }
    }

    /** 添加 Fragment 到 Activity 的布局 */
    protected void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_content, fragment);
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

    private final List<Device> mDevList = new LinkedList<>();
    private final Map<String, ProtocolWithDev> mProtocolMap = new ArrayMap<>();

    private class ProtocolWithDev extends BleDevProtocol {
        final Device device;
        boolean bSendEn = true;              // 用于判断线程是否需要结束
        PackageNeedAck nCurDealSend = null;  // 用于引用当前正在发送和等待回复的命令
        final Object lock = new Object();
        int flagAddTimeOut = 0;

        /* 添加一个管理每个蓝牙设备数据发送的队列
           每个设备连接到手机后，手机开启一个线程，用于管理当前设备的数据发送，接收，超时，重发 */
        Thread sendThread = new Thread() { //发送和等待回复命令的处理线程（通道）
            @Override
            public void run() {
                super.run();
                while (bSendEn) {
                    mMacById = device.getAddress();
                    if (nCurDealSend != null) { //当前有发送需要进行处理
                        PackageNeedAck curDeal = nCurDealSend;
                        if (curDeal.redoCntWhenTimeOut > 0) {
                            curDeal.redoCntWhenTimeOut--;
                            send(device.getAddress(), curDeal.cmd_pkg, true);
                            // 根据当前发送命令是否需要回复的类型，设置等待时间
                            if (curDeal.callback == null) {
                                // 不需要回复的处理
                                try {
                                    Thread.sleep(70);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                nCurDealSend = null;
                            } else {
                                // 等待回复过程 当 curDeal 被置为 null 时，表示回复成功
                                long send_time = System.currentTimeMillis();
                                while (bSendEn && System.currentTimeMillis() - send_time < 300) {
                                    synchronized (lock) {
                                        try {
                                            lock.wait(50); //等待2秒
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        } else {
                            // 重发次数为 0 不需要再次发送该命令了
                            if (curDeal.callback != null) {
                                curDeal.callback.timeout();
                            }
                            nCurDealSend = null;
                        }
                    } else {
                        int cmdCnt;
                        synchronized (mCmdAckList) {
                            cmdCnt = mCmdAckList.size();
                        }
                        if (cmdCnt > 0) {
                            synchronized (mCmdAckList) {
                                nCurDealSend = mCmdAckList.getFirst();
                                mCmdAckList.removeFirst();
                            }
                        } else {
                            synchronized (lock) {
                                //线程等待有新的发送任务
                                try {
                                    // 等待 0.5 秒
                                    lock.wait(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            synchronized (mCmdAckList) {
                                cmdCnt = mCmdAckList.size();
                            }
                            if (bSendEn && cmdCnt == 0) {
                                // 0.5 秒的时间内没有命令；可以发送一次获取状态的命令
                                DeviceConfig mConfig = device.getConfig();
                                if (mConfig == null) {
                                    //getDeviceConfig(device.getAddress());
                                    long id = 0;
                                    LogUtil.i(TAG, "获取一次配置");
                                    nCurDealSend = new PackageNeedAck(device.getAddress(), BleDevProtocol.pkgGetConfig(id),
                                            new OnReceivePackage() {
                                                @Override
                                                public void ack(@NonNull byte[] pkg) {
                                                    nCurDealSend = null;
                                                }

                                                @Override
                                                public void timeout() {
                                                    flagAddTimeOut++;
                                                    if (flagAddTimeOut > 3) {
                                                        // 超出距离断开连接
                                                        LogUtil.e(TAG, "timeout1: ");
                                                        EventBus.getDefault().post(new DevConnEvent(getDevMac(), DEVICE_CONNECT_NO));
                                                        flagAddTimeOut = 0;
                                                        bSendEn = false;
                                                    }
                                                }
                                            });
                                } else {
                                    long id = mConfig.getID();
                                    // 等待获取状态完成
                                    nCurDealSend = new PackageNeedAck(device.getAddress(), BleDevProtocol.pkgGetStatus(id),
                                            new OnReceivePackage() {
                                                @Override
                                                public void ack(@NonNull byte[] pkg) {
                                                    nCurDealSend = null;
                                                }

                                                @Override
                                                public void timeout() {
                                                    flagAddTimeOut++;
                                                    if (flagAddTimeOut > 3) {
                                                        // 超出距离断开连接
                                                        LogUtil.e(TAG, "timeout2: ");
                                                        EventBus.getDefault().post(new DevConnEvent(getDevMac(), DEVICE_CONNECT_NO));
                                                        flagAddTimeOut = 0;
                                                        bSendEn = false;
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                }
            }
        };

        // 有一个列队用于缓冲需要发送的数据
        private final LinkedList<PackageNeedAck> mCmdAckList = new LinkedList<>();

        ProtocolWithDev(@NonNull Device device) {
            this.device = device;
            // 创建一个用于管理数据发送和应答的线程
            sendThread.start();
        }

        void addSendCmd(PackageNeedAck pkg_Ack) {
            synchronized (mCmdAckList) {
                mCmdAckList.addLast(pkg_Ack);
            }
            synchronized (lock) {
                lock.notify();
            }
        }

        void stopSendThread() {
            bSendEn = false;
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        protected void onReceivePkg(@NonNull DeviceStatus state) {
            device.setState(state);
            nCurDealSend = null;
            flagAddTimeOut = 0;
            EventBus.getDefault().post(new DevConnEvent(device.getId(), device.getAddress(), DEVICE_CONNECT_YES, device.getState(), device.getConfig()));
        }

        @Override
        protected void onReceivePkg(@NonNull DeviceConfig config) {
            device.setConfig(config);
            nCurDealSend = null;
            flagAddTimeOut = 0;
            EventBus.getDefault().post(new DevConnEvent(device.getId(), device.getAddress(), DEVICE_CONNECT_YES, device.getState(), device.getConfig()));
        }

        @Override
        protected void onReceivePkg(@NonNull final byte[] pkg, int pkg_len) {
            runOnUiThread(() -> {
                if (nCurDealSend == null) {
                    LogUtil.i(TAG, "没有发送数据包回复处理，但是接收到回复数据");
                } else if (BleDevProtocol.isMatch(nCurDealSend.cmd_pkg, pkg)) {
                    nCurDealSend.callback.ack(pkg);
                } else {
                    LogUtil.i(TAG, "回复数据和命令不匹配 " + Util.hex(nCurDealSend.cmd_pkg, 4) + " 接收 " + Util.hex(pkg, 4));
                }
                nCurDealSend = null;
            });
        }
    }

    public void getDeviceConfig(String mac) {
        Device device = getDevice(mac);
        long id = 0;
        if (device != null) {
            DeviceConfig mConfig = device.getConfig();
            id = (mConfig == null) ? 0 : mConfig.getID();
        }
        ProtocolWithDev p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(mac, BleDevProtocol.pkgGetConfig(id), null));
            LogUtil.i(TAG, "获取一次配置");
        }
    }

    public void getDeviceState(String mac) {
        Device device = getDevice(mac);
        long id = 0;
        if (device != null) {
            DeviceConfig mConfig = device.getConfig();
            id = (mConfig == null) ? 0 : mConfig.getID();
        }
        ProtocolWithDev p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(mac, BleDevProtocol.pkgGetStatus(id), null));
            LogUtil.i(TAG, "获取一次状态");
        }
    }

    public Device getDevice(String mac) {
        for (Device device : mDevList) {
            if (device.getAddress().equals(mac)) {
                return device;
            }
        }
        return null;
    }

    /** 连接设备 */
    public void connDevice(final String mac, long id) {
        if (!mProtocolMap.containsKey(mac)) {
            Device dev = new Device(mac);
            dev.setId(id);
            mDevList.add(dev);
            addDeviceByMac(mac);
            mProtocolMap.put(mac, new ProtocolWithDev(dev));
        }
    }

    /** 断开连接设备 */
    public void disconnectDevice(final String mac) {
        if (mProtocolMap.containsKey(mac)) {
            Device dev = new Device(mac);
            dev.getId();
            mDevList.remove(dev);
            removeDeviceByMac(mac);
            ProtocolWithDev p = mProtocolMap.get(mac);
            p.stopSendThread();
            mProtocolMap.remove(mac);
        }
    }

    @Override
    void onFoundDevice(BluetoothDevice bleDevice, @Nullable List<ParcelUuid> serviceUuids) {
        super.onFoundDevice(bleDevice, serviceUuids);
        String name = bleDevice.getName();
        String mac = bleDevice.getAddress();
        // 通过设备广播名称，判断是否为配置的设备
        if (name.indexOf(getAllowedConnDevName()) != 0) {
            return;
        }
//        LogUtil.d(TAG, "dev mac: " + mac);
    }

    /** 设备就绪 */
    @Override
    void onDeviceReady(final String mac) {
        LogUtil.i(TAG, "onDeviceReady " + mac);
        if (setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE)) {
            Device device = getDevice(mac);

            if (device != null) {
                device.setConnected(true);
            }

            registerRefreshStatus(mac);
        }
    }

    private void registerRefreshStatus(final String mac) {
        getDeviceConfig(mac);
        getDeviceState(mac);
    }

    @Override
    void onDeviceConnect(String mac) {
        LogUtil.i(TAG, "onDeviceConnect " + mac);
        registerRefreshStatus(mac);

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(true);
        }
    }

    /** 设备断开 */
    @Override
    void onDeviceDisconnect(String mac) {
        unregisterPeriod(mac + "-status");

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(false);
            EventBus.getDefault().post(new DevConnEvent(mac, DEVICE_CONNECT_NO));
        }
    }

    @Override
    void onReceive(String mac, byte[] data) {
        super.onReceive(mac, data);
        BleDevProtocol p = mProtocolMap.get(mac);
        if (p != null) {
            p.bleReceive(data);
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg) {
        String mac = device.getAddress();
        ProtocolWithDev p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(device.getAddress(), pkg, null));
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback) {
        String mac = device.getAddress();
        ProtocolWithDev p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(device.getAddress(), pkg, callback));
        }
    }

    private static class PackageNeedAck {
        final byte[] cmd_pkg;
        final String mac;
        final OnReceivePackage callback;
        int redoCntWhenTimeOut = 1;

        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback) {
            this.mac = mac;
            this.cmd_pkg = cmd_pkg;
            this.callback = callback;
        }

        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback, int redoCnt) {
            this.mac = mac;
            this.cmd_pkg = cmd_pkg;
            this.callback = callback;
            this.redoCntWhenTimeOut = redoCnt;
        }
    }

    private String mStrDevId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i(TAG, "requestCode: " + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_SCANNING_CONN_DEV:
                if (resultCode == RESULT_OK) {
                    long devId = data.getLongExtra(QR_DEV_ID, 0);
                    mStrDevId = String.valueOf(devId);

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

    private String mMacById;

    private void getDataWithQueryDevMac() {
        OkGo.<String>post(Urls.GET_DEVICE_MAC)
                .tag(this)
                .params("token", getToken())
                .params("device_id", mStrDevId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(TAG, "设备 ID 获取 MAC 地址 请求成功: " + body);

                        QueryDevMacBean bean = mGson.fromJson(body, QueryDevMacBean.class);
                        if (bean.getCode() == 1) {
                            mMacById = bean.getData().getMac();

                            // 连接设备
                            connDevice(mMacById, Long.parseLong(mStrDevId));
                        }
                    }
                });
    }

    public long getDevId() {
        return Long.parseLong(mStrDevId);
    }

    public String getDevMac() {
        return mMacById;
    }

    public String getToken() {
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
