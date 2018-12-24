package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.lzy.okgo.model.HttpParams;

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
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.QueryDevMacBean;
import cn.eejing.colorflower.model.session.LoginSession;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.OnReceivePackage;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.presenter.comm.ObserverManager;
import cn.eejing.colorflower.util.BleDevProtocol;
import cn.eejing.colorflower.util.BtnBarUtil;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.util.Util;
import cn.eejing.colorflower.view.fragment.TabCtrlFragment;
import cn.eejing.colorflower.view.fragment.TabMallFragment;
import cn.eejing.colorflower.view.fragment.TabMineFragment;
import cn.eejing.colorflower.view.fragment.TabVideoFragment;
import cn.jzvd.Jzvd;

import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.colorflower.app.AppConstant.EXIT_LOGIN;
import static cn.eejing.colorflower.app.AppConstant.QR_DEV_ID;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_FORCED_UPDATE;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_SCANNING_CONN_DEV;

public class MainActivity extends BleActivity implements /*ISendCommand, */BottomNavigationBar.OnTabSelectedListener {
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
                    Log.i(TAG, "onActivityResult");
                    long devId = data.getLongExtra(QR_DEV_ID, 0);
                    mDevIdByQR = String.valueOf(devId);
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

    private String mDevIdByQR;
    private String mMacByDevId;

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
                            mMacByDevId = bean.getData().getMac();
                            // 设置扫描规则并开始扫描
                            setScanRule(mMacByDevId);
                            startScan();
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    /** 扫描 */
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override/* 开始扫描（主线程）*/
            public void onScanStarted(boolean success) {
                Log.i(TAG, "onScanStarted: " + success);
//                mDeviceAdapter.clearScanDevice();
//                mDeviceAdapter.notifyDataSetChanged();
//                img_loading.startAnimation(operatingAnim);
//                img_loading.setVisibility(View.VISIBLE);
//                btn_scan.setText(getString(R.string.stop_scan));
            }

            @Override/* 扫描过程中所有被扫描到的结果回调 */
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override/* 扫描到一个符合扫描规则的 BLE 设备（主线程）*/
            public void onScanning(BleDevice bleDevice) {
                Log.i(TAG, "onScanning: " + bleDevice.getMac());
                // 扫描到一个符合扫描规则的设备停止扫描并开始连接
                cancelScan();
                mBleDevice = bleDevice;
                connect();
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override/* 扫描结束，列出所有扫描到的符合扫描规则的BLE设备（主线程）*/
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.i(TAG, "onScanFinished: " + scanResultList.size());
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));
            }
        });
    }

    /**
     * 连接【通过设备对象】
     * <p>
     * - 在某些型号手机上，connectGatt 必须在主线程才能有效。非常建议把连接过程放在主线程。
     * - 连接失败后重连：框架中包含连接失败后的重连机制，可以配置重连次数和时间间隔。当然也可以自行在`onConnectFail`回调方法中延时调用`connect`方法。
     * - 连接断开后重连：可以在`onDisConnected`回调方法中再次调用`connect`方法。
     * - 为保证重连成功率，建议断开后间隔一段时间之后进行重连。
     * - 某些机型上连接失败后会短暂地无法扫描到设备，可以通过设备对象或设备mac直连，而不经过扫描。
     */
    public void connect() {
        BleManager.getInstance().connect(mBleDevice, new BleGattCallback() {
            @Override/* 开始连接 */
            public void onStartConnect() {
            }

            @Override/* 连接失败 */
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                ToastUtil.showLong(getString(R.string.connect_fail));
            }

            @Override/* 连接成功，BleDevice 即为所连接的 BLE 设备 */
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                sleepTime(100);
                openNotify();
            }

            @Override/* 连接中断，isActiveDisConnected 表示是否是主动调用了断开连接方法 */
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if (isActiveDisConnected) {
                    ToastUtil.showLong(getString(R.string.active_disconnected));
                } else {
                    ToastUtil.showLong(getString(R.string.disconnected));
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }

            }
        });
    }

    private void openNotify() {
        BleManager.getInstance().notify(mBleDevice,
                UUID_GATT_SERVICE, UUID_GATT_CHARACTERISTIC_NOTIFY,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.i(TAG, "onNotifySuccess: ");
                        sleepTime(100);
                        sendDeviConfig();
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Log.i(TAG, "onNotifyFailure: " + exception.getCode());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.i(TAG, "onCharacteristicChanged: " + HexUtil.formatHexString(data, true));
                    }
                });
    }

    public void sendDeviConfig() {
        BleManager.getInstance().write(mBleDevice,
                UUID_GATT_SERVICE, UUID_GATT_CHARACTERISTIC_WRITE,
                BleDevProtocol.pkgGetConfig(Long.parseLong(mDevIdByQR)),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                        Log.i(TAG, "发送获取设备配置命令成功: " + HexUtil.formatHexString(justWrite, true));
                        mHandler.sendEmptyMessageDelayed(MSG_CONNECT_SUCCESS, 100);
                        mHandler.sendEmptyMessageDelayed(MSG_RECEIVE_DATA, 300);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.i(TAG, "发送获取设备配置命令失败: " + exception.getDescription());
                        // 重新获取配置
                        sleepTime(100);
                        sendDeviConfig();
                    }
                });
    }

    public void sendDevStatus() {
        BleManager.getInstance().write(mBleDevice,
                UUID_GATT_SERVICE, UUID_GATT_CHARACTERISTIC_WRITE,
                BleDevProtocol.pkgGetStatus(Long.parseLong(mDevIdByQR)),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                        Log.i(TAG, "发送获取设备状态命令成功: " + HexUtil.formatHexString(justWrite, true));
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.i(TAG, "发送获取设备状态命令失败: " + exception.getDescription());
                    }
                });
    }

    public void receiveDevData() {
        BleManager.getInstance().read(mBleDevice,
                "00001801-0000-1000-8000-00805f9b34fb", UUID_GATT_CHARACTERISTIC_READ,
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        Log.i(TAG, "接收数据成功: " + HexUtil.formatHexString(data, true));
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        Log.i(TAG, "接收数据失败: " + exception.getDescription());
                    }
                });
    }


    public static final String UUID_GATT_SERVICE                = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_GATT_CHARACTERISTIC_NOTIFY  = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_GATT_CHARACTERISTIC_WRITE   = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_GATT_CHARACTERISTIC_READ    = "00002a05-0000-1000-8000-00805f9b34fb";

    private static final int MSG_CONNECT_SUCCESS = 1;
    private static final int MSG_RECEIVE_DATA    = 2;
    private BleDevice mBleDevice;

    @SuppressLint("HandlerLeak")
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CONNECT_SUCCESS:
                    // 每 2s 获取状态
                    sendDevStatus();
                    mHandler.sendEmptyMessageDelayed(MSG_CONNECT_SUCCESS, 2000);
                    break;
                case MSG_RECEIVE_DATA:
                    // 每 2s 接收蓝牙数据
                    receiveDevData();
                    mHandler.sendEmptyMessageDelayed(MSG_RECEIVE_DATA, 2000);
                    break;
            }
        }
    };

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
                    mMacByDevId = device.getAddress();
                    if (nCurDealSend != null) { // 当前有发送需要进行处理
                        PackageNeedAck curDeal = nCurDealSend;
                        if (curDeal.redoCntWhenTimeOut > 0) {
                            curDeal.redoCntWhenTimeOut--;
//                            send(device.getAddress(), curDeal.cmd_pkg, true);
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
                                    //sendDeviConfig(device.getAddress());
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
                                                        // 超时断开连接
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
                                                        // 超时断开连接
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
                    if( nCurDealSend.callback != null ) {
                        nCurDealSend.callback.ack(pkg);
                    }
                } else {
                    LogUtil.i(TAG, "回复数据和命令不匹配 " + Util.hex(nCurDealSend.cmd_pkg, 4) + " 接收 " + Util.hex(pkg, 4));
                }
                nCurDealSend = null;
            });
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
//            addDeviceByMac(mac);
            mProtocolMap.put(mac, new ProtocolWithDev(dev));
        }
    }

    /** 断开连接设备 */
    public void disconnectDevice(final String mac) {
        if (mProtocolMap.containsKey(mac)) {
            Device dev = new Device(mac);
            dev.getId();
            mDevList.remove(dev);
//            removeDeviceByMac(mac);
            ProtocolWithDev p = mProtocolMap.get(mac);
            p.stopSendThread();
            mProtocolMap.remove(mac);
//            disconnectAll();
        }
    }

//    @Override
//    void onFoundDevice(BluetoothDevice bleDevice, @Nullable List<ParcelUuid> serviceUuids) {
//        super.onFoundDevice(bleDevice, serviceUuids);
//        String name = bleDevice.getName();
//        String mac = bleDevice.getAddress();
//        Log.d(TAG, "onFoundDevice in mac: " + mac + " name: " + name);
//        if( mProtocolMap.containsKey(mac) ) {
//            Log.d(TAG, "onFoundDevice MAC: " + mac);
//            addDeviceByObject(bleDevice);
//        }
//        // 通过设备广播名称，判断是否为配置的设备
//        if (name.indexOf(getAllowedConnDevName()) != 0) {
//            return;
//        }
////        LogUtil.d(TAG, "dev mac: " + mac);
//    }

//    /** 设备就绪 */
//    @Override
//    void onDeviceReady(final String mac) {
//        LogUtil.i(TAG, "onDeviceReady " + mac);
//        if (setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE)) {
//            Device device = getDevice(mac);
//
//            if (device != null) {
//                device.setConnected(true);
//            }
//
//            registerRefreshStatus(mac);
//        }
//    }


//    @Override
//    void onDeviceConnect(String mac) {
//        LogUtil.i(TAG, "onDeviceConnect " + mac);
//        registerRefreshStatus(mac);
//
//        Device device = getDevice(mac);
//        if (device != null) {
//            device.setConnected(true);
//        }
//    }

//    /** 设备断开 */
//    @Override
//    void onDeviceDisconnect(String mac) {
//        unregisterPeriod(mac + "-status");
//
//        Device device = getDevice(mac);
//        if (device != null) {
//            device.setConnected(false);
//            EventBus.getDefault().post(new DevConnEvent(mac, DEVICE_CONNECT_NO));
//        }
//    }
//
//    @Override
//    void onReceive(String mac, byte[] data) {
//        super.onReceive(mac, data);
//        BleDevProtocol p = mProtocolMap.get(mac);
//        if (p != null) {
//            p.bleReceive(data);
//        }
//    }

//    @Override
    public void sendCommand(@NonNull Device device, @NonNull byte[] pkg) {
        String mac = device.getAddress();
        ProtocolWithDev p = mProtocolMap.get(mac);
        if (p != null) {
            p.addSendCmd(new PackageNeedAck(device.getAddress(), pkg, null));
        }
    }

//    @Override
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

    public long getDevId() {
        return Long.parseLong(mDevIdByQR);
    }

    public String getDevMac() {
        return mMacByDevId;
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

    private void sleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
