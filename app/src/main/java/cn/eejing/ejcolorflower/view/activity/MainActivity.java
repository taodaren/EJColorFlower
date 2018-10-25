package cn.eejing.ejcolorflower.view.activity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceStatus;
import cn.eejing.ejcolorflower.model.event.DevConnEvent;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.model.request.QueryDevMacBean;
import cn.eejing.ejcolorflower.model.session.LoginSession;
import cn.eejing.ejcolorflower.presenter.ISendCommand;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.util.Util;
import cn.eejing.ejcolorflower.view.fragment.TabCtrlFragment;
import cn.eejing.ejcolorflower.view.fragment.TabMallFragment;
import cn.eejing.ejcolorflower.view.fragment.TabMineFragment;

import static cn.eejing.ejcolorflower.app.AppConstant.EXIT_LOGIN;
import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.ejcolorflower.app.AppConstant.UUID_GATT_CHARACTERISTIC_WRITE;
import static cn.eejing.ejcolorflower.app.AppConstant.UUID_GATT_SERVICE;

public class MainActivity extends BLEManagerActivity implements ISendCommand, BottomNavigationBar.OnTabSelectedListener {
    private static final String TAG = "MainActivity";

    private List<Fragment> mFragments;
    private Fragment mCurrentFragment;

    private String mMemberId, mToken;
    private Gson mGson;

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
        super.initView();
        AppInstance = this;
        addActivity(EXIT_LOGIN, this);
        LoginSession session = Settings.getLoginSessionInfo(this);
        mMemberId = String.valueOf(session.getMember_id());
        mToken = session.getToken();
        mGson = new Gson();

        initBtnNavBar();
        mFragments = getFragments();
        setDefFragment();

        mServerDevList = new ArrayList<>();
        mServerMacList = new ArrayList<>();

        getDataWithDeviceList();
        scanRefresh();
    }

    public void scanRefresh() {
        addScanFilter(UUID_GATT_SERVICE);
        refresh();
    }

    private void getDataWithDeviceList() {
        OkGo.<String>post(Urls.DEVICE_LIST)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "设备列表请求成功！" + body);

                        DeviceListBean bean = mGson.fromJson(body, DeviceListBean.class);
                        DeviceListBean.DataBean.ListBean deviceBean = mGson.fromJson(body, DeviceListBean.DataBean.ListBean.class);
                        switch (bean.getCode()) {
                            case 101:
                            case 102:
                                Toast.makeText(MainActivity.this, R.string.toast_login_fail, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                finish();
                                break;
                            case 1:
                                mServerDevList = bean.getData().getList();
                                for (int i = 0; i< mServerDevList.size(); i++) {
                                    mServerMacList.add(mServerDevList.get(i).getMac());
                                }
                                setAllowConnDevListMAC(mServerMacList);
                                Log.i(TAG, "设备列表 size：" + mServerDevList.size());
                                Log.i(TAG, "服务器 MAC size：" + mServerMacList.size());
                                break;
                            default:
                        }
                    }
                });
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

    private List<DeviceListBean.DataBean.ListBean> mServerDevList;// 服务器绑定设备列表
    private List<String> mServerMacList;
    private final List<Device> mDevList = new LinkedList<>();
    private final Map<String, ProtocolWithDevice> mProtocolMap = new ArrayMap<>();

    private class ProtocolWithDevice extends BleDeviceProtocol {
        final Device device;
        boolean bSendEn = true;              // 用于判断线程是否需要结束
        PackageNeedAck nCurDealSend = null;  // 用于引用当前正在发送和等待回复的命令
        final Object lock = new Object();

        /* 添加一个管理每个蓝牙设备数据发送的队列
           每个设备连接到手机后，手机开启一个线程，用于管理当前设备的数据发送，接收，超时，重发 */
        Thread sendThread = new Thread() { //发送和等待回复命令的处理线程（通道）
            @Override
            public void run() {
                super.run();
                while (bSendEn) {
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
                                while (bSendEn && (curDeal != null) && (System.currentTimeMillis() - send_time < 300)) {
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
                                    // 等待 2 秒
                                    lock.wait(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            synchronized (mCmdAckList) {
                                cmdCnt = mCmdAckList.size();
                            }
                            if (bSendEn && cmdCnt == 0) {
                                // 2 秒的时间内没有命令；可以发送一次获取状态的命令
                                DeviceConfig mConfig = device.getConfig();
                                long id = (mConfig == null) ? 0 : mConfig.mID;
                                // 等待获取状态完成
                                nCurDealSend = new PackageNeedAck(device.getAddress(), BleDeviceProtocol.pkgGetStatus(id),
                                        new OnReceivePackage() {
                                            @Override
                                            public void ack(@NonNull byte[] pkg) {
                                                nCurDealSend = null;
                                            }

                                            @Override
                                            public void timeout() {

                                            }
                                        });
                            }
                        }
                    }
                }
            }
        };

        // 有一个列队用于缓冲需要发送的数据
        private final LinkedList<PackageNeedAck> mCmdAckList = new LinkedList<>();

        ProtocolWithDevice(@NonNull Device device) {
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
            Log.i(TAG, "获取状态成功");
            // 接收到 状态返回 需要发送一个通知
            EventBus.getDefault().post(new DevConnEvent(device.getId(), device.getAddress(), "已连接", device.getState(), device.getConfig()));
        }

        @Override
        protected void onReceivePkg(@NonNull DeviceConfig config) {
            device.setConfig(config);
            nCurDealSend = null;
            Log.i(TAG, "获取配置成功DMX=" + config.mDMXAddress);
            EventBus.getDefault().post(new DevConnEvent(device.getId(), device.getAddress(), "已连接", device.getState(), device.getConfig()));
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    mDevListAdapter.notifyDataSetChanged();
//                }
//            });
        }

        @Override
        protected void onReceivePkg(@NonNull final byte[] pkg, int pkg_len) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //doMatch(device.getAddress(), pkg);
                    if (nCurDealSend == null) {
                        Log.i(TAG, "没有发送数据包回复处理，但是接收到回复数据");
                    } else if (BleDeviceProtocol.isMatch(nCurDealSend.cmd_pkg, pkg)) {
                        nCurDealSend.callback.ack(pkg);
                    } else {
                        Log.i(TAG, "回复数据和命令不匹配 " + Util.hex(nCurDealSend.cmd_pkg, 4) + " 接收 " + Util.hex(pkg, 4));
                    }
                    nCurDealSend = null;
                }
            });
        }
    }

    public void getDeviceConfig(String mac) {
        Device device = getDevice(mac);
        long id = 0;
        if (device != null) {
            DeviceConfig mConfig = device.getConfig();
            id = (mConfig == null) ? 0 : mConfig.mID;
        }
        ProtocolWithDevice p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(mac, BleDeviceProtocol.pkgGetConfig(id), null));
            Log.i(TAG, "获取一次配置");
        }
    }

    public void getDeviceState(String mac) {
        Device device = getDevice(mac);
        long id = 0;
        if (device != null) {
            DeviceConfig mConfig = device.getConfig();
            id = (mConfig == null) ? 0 : mConfig.mID;
        }
        ProtocolWithDevice p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(mac, BleDeviceProtocol.pkgGetStatus(id), null));
            Log.i(TAG, "获取一次状态");
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
        Log.i(TAG, "connDevice " + mac + " " + id);
        if (!mProtocolMap.containsKey(mac)) {
            Log.i(TAG, "connDevice: 123");
            Device dev = new Device(mac);
            dev.setId(id);
            mDevList.add(dev);
            addDeviceByMac(mac);
            mProtocolMap.put(mac, new ProtocolWithDevice(dev));
        }
    }

    /** 断开连接设备 */
    public void disconnectDevice(final String mac) {
        if (mProtocolMap.containsKey(mac)) {
            Device dev = new Device(mac);
            dev.getId();
            mDevList.remove(dev);
            removeDeviceByMac(mac);
            ProtocolWithDevice p = mProtocolMap.get(mac);
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

        Log.d(TAG, "dev mac: " + mac);
        for (int i = 0; i < mServerDevList.size(); i++) {
            Log.d(TAG, "allow mac: " + mServerMacList);
        }
        Log.d(TAG, "allow: " + mServerMacList.contains(mac));
        // 是否与服务器 MAC 地址匹配
        if (mServerMacList.contains(mac)) {
            // 如果服务器设备列表的 Mac 与扫描到的蓝牙 Mac 一致，此设备可连接
            EventBus.getDefault().post(new DevConnEvent(mac,"可连接"));
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
        getDeviceConfig(mac);
        getDeviceState(mac);
    }

    @Override
    void onDeviceConnect(String mac) {
        Log.i(TAG, "onDeviceConnect " + mac);
        registerRefreshStatus(mac);

        Device device = getDevice(mac);
        if (device != null) {
            device.setConnected(true);
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
            EventBus.getDefault().post(new DevConnEvent(mac,"不可连接"));
        }
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
        ProtocolWithDevice p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(device.getAddress(), pkg, null));
        }
    }

    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg, OnReceivePackage callback) {
        String mac = device.getAddress();
        ProtocolWithDevice p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(device.getAddress(), pkg, callback));
        }
    }

    private static class PackageNeedAck {
        final byte[] cmd_pkg;
        final String mac;
//        public long send_time;
        final OnReceivePackage callback;
        int redoCntWhenTimeOut = 1;
//        ProtocolWithDevice deviceProtocol=null;

        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback) {
            this.mac = mac;
            this.cmd_pkg = cmd_pkg;
            this.callback = callback;
//            this.send_time = System.currentTimeMillis();
        }

        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback, int redoCnt) {
            this.mac = mac;
            this.cmd_pkg = cmd_pkg;
            this.callback = callback;
            this.redoCntWhenTimeOut = redoCnt;
//            this.send_time = System.currentTimeMillis();
        }

//        boolean isTimeout() {
//            //return (System.currentTimeMillis() - send_time) > ACK_TIMEOUT;
//            return false;
//        }
    }

    private String mStrDevId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    long devId = data.getLongExtra(QR_DEV_ID, 0);
                    mStrDevId = String.valueOf(devId);
                    Log.d(TAG, "onActivityResult dev id: " + devId);

                    // 获取 ID 对应 MAC
                    getDataWithQueryDevMac();
                }
                break;
            default:
                break;
        }
    }

    private String mMacById;

    private void getDataWithQueryDevMac() {
        OkGo.<String>post(Urls.QUERY_DEV_MAC)
                .tag(this)
                .params("id", mStrDevId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(TAG, "查询设备 Mac 地址请求成功！" + body);

                        QueryDevMacBean bean = mGson.fromJson(body, QueryDevMacBean.class);
                        if (bean.getCode() == 1) {
                            mMacById = bean.getData().getMac();
                            Log.d(TAG, "QueryDevMac mMacById: " + mMacById);

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

    public String getMemberId() {
        return mMemberId;
    }

}
