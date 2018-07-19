package cn.eejing.ejcolorflower.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.MyLifecycleHandler;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.util.Util;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 管理多个 BLE 设备的发现、连接、通讯
 */

public class BLEActivity extends BaseActivity {
    private static final String TAG = "BLE";
    private static final int MAX_BLUETOOTH_SEND_PKG_LEN = 18;
    private static final int REQUEST_ENABLE_BT = 38192;
    private static final int REFRESHING_PERIOD = 60 * 1000;
    private static final int SCANNING_TIME = 8 * 1000;

    private long mNextRefreshingTime;
    private boolean mScanning = false;
    private boolean mShutdown = false;
    private boolean mUserDenied = false;
    private boolean mDoNextRefresh = true;

    private List<ScanFilter> mScanFilters = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanSettings mScanSettings = null;
    private Handler mHandler;

    // 已经连接到的设备
    private Map<String, DeviceManager> mDeviceManagerSet = new ArrayMap<>();
    // 允许连接的设备
    private List<String> mAllowedConnectDevicesMAC = new ArrayList<>();

    private final Map<String, Pair<Runnable, Integer>> mPeriodRunnable = new ArrayMap<>();
    private String mAllowedConnectDevicesName = "";

    private final LinkedList<GattOperation> mGattOperations = new LinkedList<>();
    private final Object mGattOperationLock = new Object();
    private GattOperation mCurrentGattOperation = null;

    static private BLEActivity BleInstance = null;

    public static BLEActivity getBleCtrl() {
        return BleInstance;
    }

    /**
     * 设置允许连接设备管理(通过 MAC)
     */
    public void setAllowedConnectDevicesMAC(List<String> newMacs) {
        mAllowedConnectDevicesMAC = newMacs;
        removeConnectedMoreDevice();
    }

    public void clearAllowedConnectDevicesMAC() {
        mAllowedConnectDevicesMAC.clear();
    }

    public void addAllowedConnectDevicesMAC(String newMac) {
        mAllowedConnectDevicesMAC.add(newMac);
    }

    /**
     * 更新允许连接的设备 MAC 地址列表后，删除已经连接的不在列表中多余的设备
     */
    public void removeConnectedMoreDevice() {
        // 判断已经连接的数据
        for (DeviceManager mgr : mDeviceManagerSet.values()) {
            // 如果已连接的设备不在 AllowedConnectDevicesMAC 中，断开设备连接
            if (!mAllowedConnectDevicesMAC.contains(mgr.mac)) {
                if (mgr.gatt != null && mgr.connected) {
                    mgr.gatt.disconnect(); //断开连接
                }
                mDeviceManagerSet.remove(mgr.mac);
            }
        }

    }

    /**
     * 配置当前 APP 处理的蓝牙设备名称
     *
     * @param name 蓝牙设备名称
     */
    public void setAllowedConnDevName(String name) {
        mAllowedConnectDevicesName = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyLifecycleHandler.addListener(mOnForegroundStateChangeListener);

        mHandler = new Handler();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = (bluetoothManager == null) ? null : bluetoothManager.getAdapter();
        BleInstance = this;
    }

    @Override
    protected int layoutViewId() {
        return 0;
    }

    private final MyLifecycleHandler.OnForegroundStateChangeListener mOnForegroundStateChangeListener =
            new MyLifecycleHandler.OnForegroundStateChangeListener() {
                @Override
                public void onStateChanged(boolean foreground) {
                    Log.i(TAG, "OnForegroundStateChangeListener " + foreground);
                    if (foreground) {
                        for (Pair<Runnable, Integer> s : mPeriodRunnable.values()) {
                            mHandler.postDelayed(s.first, s.second);
                        }
                        mHandler.post(mPoll);
                    } else {
                        mHandler.removeCallbacks(mPoll);
                        for (Pair<Runnable, Integer> s : mPeriodRunnable.values()) {
                            mHandler.removeCallbacks(s.first);
                        }
                    }
                }
            };

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        MyLifecycleHandler.removeListener(mOnForegroundStateChangeListener);

        for (Pair<Runnable, Integer> s : mPeriodRunnable.values()) {
            mHandler.removeCallbacks(s.first);
        }
        mPeriodRunnable.clear();

        if (mScanning) {
            mHandler.removeCallbacks(mStopScan);
            mStopScan.run();
        }
        disconnectAll();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (mBluetoothAdapter.isEnabled()) {
                onBluetoothEnabled();
            } else {
                mUserDenied = true;
                onUserDenied();
            }
        }
    }

    // 蓝牙固定时间开启扫描功能（底层功能）
    private final Runnable mPoll = new Runnable() {
        @Override
        public void run() {
            poll();
            if (!mShutdown) {
                mHandler.postDelayed(mPoll, 100);

                if (mDoNextRefresh && (System.currentTimeMillis() - mNextRefreshingTime) > 0) {
                    mDoNextRefresh = false;
                    startScan();
                }
            }
        }
    };

    private final Rationale mDefaultRationale = new Rationale() {
        @Override
        public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
            List<String> permissionNames = Permission.transformText(context, permissions);
            String message = context.getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames));

            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle(R.string.tip)
                    .setMessage(message)
                    .setPositiveButton(R.string.resume, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.execute();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.cancel();
                        }
                    })
                    .show();
        }
    };

    private void addDevice(BluetoothDevice device) {
        final DeviceManager deviceManager;
        String mac = device.getAddress();
        if (!mDeviceManagerSet.containsKey(mac)) {
            deviceManager = new DeviceManager(device);
            mDeviceManagerSet.put(mac, deviceManager);
            onFoundAndConnectOneDevice(deviceManager);
        } else {
            deviceManager = mDeviceManagerSet.get(mac);
        }

        if (deviceManager.gatt == null && !mShutdown) {
            deviceManager.gatt = deviceManager.device.connectGatt(this, true, mBluetoothGattCallback);
        }
    }

    // 找到并连接一台设备
    protected void onFoundAndConnectOneDevice(DeviceManager dev) {
    }

    // 接收到设备的广播信息后，被调用
    void onFoundDevice(BluetoothDevice device, @Nullable List<ParcelUuid> serviceUuids) {
        String name = device.getName();
        String mac = device.getAddress();

        Log.i(TAG, "找到设备---> mac = " + mac + "  name = " + name);

        // 通过设备广播名称，判断是否为采花机设备
        if (name.indexOf(mAllowedConnectDevicesName) != 0) {// "EEJING-CHJ"
            return;
        }

        // 当前接收的广播信息是否为 配置的允许连接的 MAC 地址中设备发出的
        if (mAllowedConnectDevicesMAC.contains(mac)) {
            addDevice(device);
        }
    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        /**
         * BLE 扫描功能开启后，接收到设备（外设）的广播信息后的回掉函数
         *
         * @param result 一个数据包的信息参数
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String mac = result.getDevice().getAddress();
            Log.i(TAG, "扫描结果--->" + callbackType + " " + mac + " | " + result.getDevice().getName());

            if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                if (!mDeviceManagerSet.containsKey(mac)) {
                    ScanRecord record = result.getScanRecord();
                    // 找到设备
                    onFoundDevice(result.getDevice(), (record == null) ? null : record.getServiceUuids());
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "批量扫描结果--->" + results.size());
            for (ScanResult result : results) {
                String mac = result.getDevice().getAddress();
                Log.i(TAG, "扫描结果--->" + " " + mac + " | " + result.getDevice().getName());

                if (!mDeviceManagerSet.containsKey(mac)) {//当前设备还没有被连接
                    ScanRecord record = result.getScanRecord();
                    // 找到设备
                    onFoundDevice(result.getDevice(), (record == null) ? null : record.getServiceUuids());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, "扫描失败--->" + errorCode);
        }
    };

    private final Runnable mStopScan = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
            onStopScan();
            if (!mUserDenied) {
                mDoNextRefresh = true;
                mNextRefreshingTime = System.currentTimeMillis() + REFRESHING_PERIOD;
            }
        }
    };

    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        // 连接状态更改
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            final DeviceManager mgr = getMatchedDeviceManager(gatt);
            if (mgr != null) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "状态连接 " + mgr.mac);
                    mgr.connected = true;
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i(TAG, "状态已断开 " + mgr.mac);
                    mgr.connected = false;
                    mgr.discovering = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onDeviceDisconnect(mgr.mac);
                            mDeviceManagerSet.remove(mgr.mac);  //设备断开连接后，从已连接设备列表中移除
                        }
                    });
                }
            }
        }

        // 发现的服务
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            final DeviceManager mgr = getMatchedDeviceManager(gatt);
            if (mgr != null) {
                mgr.discovering = false;
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i(TAG, "服务发现成功--->" + mgr.mac);
                    mgr.characteristic = new LinkedList<>();
                    for (BluetoothGattService service : gatt.getServices()) {
                        mgr.characteristic.addAll(service.getCharacteristics());
                    }
                    for (BluetoothGattCharacteristic ch : mgr.characteristic) {
                        if ((ch.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            gatt.setCharacteristicNotification(ch, true);
                            List<BluetoothGattDescriptor> descriptorList = ch.getDescriptors();
                            if (descriptorList != null) {
                                for (BluetoothGattDescriptor descriptor : descriptorList) {
                                    add(GattOperation.newWriteDescriptor(gatt, descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE));
                                }
                            }
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onDeviceReady(mgr.mac);
                        }
                    });
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            removeCurrentGattOperation(gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            removeCurrentGattOperation(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            onReceive(gatt.getDevice().getAddress(), characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            removeCurrentGattOperation(gatt);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i(TAG, "onDescriptorWrite");
            removeCurrentGattOperation(gatt);
        }
    };

    // 开始扫描
    public void startScan() {
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                onBluetoothEnabled();
            }
        }
    }

    // 蓝牙已启用
    private void onBluetoothEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(this)
                    .permission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .rationale(mDefaultRationale)
                    .onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            if (AndPermission.hasAlwaysDeniedPermission(BLEActivity.this, permissions)) {
                                showSetting(permissions);
                            }
                        }
                    })
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            startLeScanNoBug();
                        }
                    })
                    .start();
        } else {
            startLeScanNoBug();
        }
    }

    // 用户被拒绝
    private void onUserDenied() {
        Toast.makeText(this, "无法使用蓝牙功能", Toast.LENGTH_SHORT).show();
    }

    // 显示设置
    private void showSetting(final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(this, permissions);
        String message = this.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        final SettingService settingService = AndPermission.permissionSetting(this);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.tip)
                .setMessage(message)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingService.execute();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingService.cancel();
                    }
                })
                .show();
    }

    // 如果在 onBluetoothEnabled 中直接调用 startLeScan， 将出现 android.os.DeadObjectException
    private void startLeScanNoBug() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                startLeScan();
            }
        });
    }

    // 启动扫描
    private void startLeScan() {
        Log.v(TAG, "startLeScan");
        mBluetoothLeScanner = (mBluetoothAdapter == null) ? null : mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner != null) {
            mScanning = true;
            if (mScanFilters == null) {
                mBluetoothLeScanner.startScan(mScanCallback);
            } else {
                if (mScanSettings == null) {
                    mScanSettings = new ScanSettings.Builder()
                            .setReportDelay(0)
                            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                            .build();
                }
                mBluetoothLeScanner.startScan(mScanFilters, mScanSettings, mScanCallback);
            }
            mHandler.postDelayed(mStopScan, SCANNING_TIME);
        }
    }

    private void remove() {
        synchronized (mGattOperationLock) {
            mCurrentGattOperation = null;
            while (!mGattOperations.isEmpty()) {
                mCurrentGattOperation = mGattOperations.pop();
                if (mCurrentGattOperation.run()) {
                    break;
                } else {
                    mCurrentGattOperation = null;
                }
            }
        }
    }

    private void peekDeviceToDiscovering() {
        if (mShutdown) {
            return;
        }
        for (DeviceManager mgr : mDeviceManagerSet.values()) {
            if (mgr.connected && mgr.characteristic == null && !mgr.discovering) {
                Log.i(TAG, "发现服务 " + mgr.mac);
                mgr.discovering = mgr.gatt.discoverServices();
                return;
            }
        }
    }

    private void add(GattOperation op) {
        synchronized (mGattOperationLock) {
            if (mCurrentGattOperation == null) {
                if (op.run()) {
                    mCurrentGattOperation = op;
                }
            } else {
                mGattOperations.push(op);
            }
        }
    }

    private void removeCurrentGattOperation(BluetoothGatt gatt) {
        synchronized (mGattOperationLock) {
            if (mCurrentGattOperation != null && gatt.equals(mCurrentGattOperation.gatt)) {
                remove();
            }
        }
    }

    // 断开全部蓝牙
    private void disconnectAll() {
        mShutdown = true;
        synchronized (mGattOperationLock) {
            mGattOperations.clear();
        }
        for (DeviceManager mgr : mDeviceManagerSet.values()) {
            if (mgr.gatt != null && mgr.connected) {
                mgr.gatt.disconnect();
            }
        }
    }

    // 设置发送默认渠道
    boolean setSendDefaultChannel(String mac, UUID uuid) {
        DeviceManager mgr = mDeviceManagerSet.get(mac);
        return (mgr != null) && mgr.setWriteChannel(uuid);
    }

    // 向一个设备发送数据的最基本函数
    boolean send(String mac, byte[] data, OnReceivePackage revCb) {
        Log.i(TAG, "send:" + Util.hex(data, data.length));
        if (mShutdown) {
            return false;
        }
        data = BleDeviceProtocol.wrapped_package(data);
        DeviceManager mgr = mDeviceManagerSet.get(mac);
        return mgr != null && mgr.sendData(data, revCb);
    }

    // 向一个设备发送数据的最基本函数
    boolean send(String mac, byte[] data) {
        return send(mac, data, null);
    }

    private class WrappedRunnable implements Runnable {
        private final Runnable runnable;
        private final Handler handler;
        private final int delay;

        WrappedRunnable(Runnable runnable, Handler handler, int delay) {
            this.runnable = runnable;
            this.handler = handler;
            this.delay = delay;
        }

        @Override
        public void run() {
            runnable.run();
            handler.postDelayed(this, delay);
        }
    }

    /**
     * 根据 MAC 地址，获取已经连接的设备中的设备管理器
     *
     * @param mac MAC 地址
     * @return 设备管理器
     */
    public DeviceManager getConnectedDeviceByMac(String mac) {
        return mDeviceManagerSet.get(mac);
    }

    /**
     * 对单个已经连接的设备进行管理
     * 包括数据 BLE 协议管理、数据发送管理、等待回复、超时
     */
    public static class DeviceManager {
        final String                            mac;
        final BluetoothDevice                   device;
        BluetoothGatt                           gatt = null;
        List<BluetoothGattCharacteristic>       characteristic = null;
        BluetoothGattCharacteristic             write_characteristic = null;
        boolean                                 connected = false;
        boolean                                 discovering = false;

        int                                     waitTime = 1000;                     // 等待回复、超时
        long                                    sendTime;                            // 数据发送时刻 用于判断是否超时
        OnReceivePackage                        callBack;                            // 接收到数据包及超时的处理回调函数
        byte[]                                  needCallbackCmdPkg;                  // 如果某个命令需要等待回复，记录命令包
        BleDeviceProtocol                       bleDeviceProtocol = null;

        public void setBleDeviceProtocol(BleDeviceProtocol bleDevPro) {
            bleDeviceProtocol = bleDevPro;
        }

        public void setWaitTime(int timeOut) {
            waitTime = timeOut;
        }

        // 接收到蓝牙数据的处理函数
        void dealRecieveDataByProtocol(byte[] data) {
            if (bleDeviceProtocol != null) {
                bleDeviceProtocol.onReceive(data);
            }
        }

        // 接收到匹配的蓝牙设备发送的数据包后，需要进行的回调处理
        public void doMatchPackage(byte[] ack_pkg) {
            if (callBack == null) {
                return;
            }
            if (BleDeviceProtocol.isMatch(needCallbackCmdPkg, ack_pkg)) {
                Log.i(TAG, "doMatch match");
                callBack.ack(ack_pkg);
                // 只要匹配到回复数据后，将不再进行数据接收处理，超时检测
                callBack = null;
            }
        }

        boolean isTimeout() {
            if (callBack == null) {
                return false;
            }
            return (System.currentTimeMillis() - sendTime) > waitTime;
        }

        DeviceManager(BluetoothDevice dev) {
            mac = dev.getAddress();
            device = dev;
        }

        boolean setWriteChannel(UUID uuid) {
            if (characteristic != null) {
                for (BluetoothGattCharacteristic c : characteristic) {
                    if (c.getUuid().equals(uuid)) {
                        write_characteristic = c;
                        return true;
                    }
                }
            }
            return false;
        }

        // 向一个设备发送数据的最基本函数
        boolean sendData(byte[] data, OnReceivePackage revCb) {
            // Log.i(TAG, "send:" + Util.hex(data, data.length));
            if (connected && (gatt != null)) {
                if (write_characteristic != null) {
                    if (data.length > MAX_BLUETOOTH_SEND_PKG_LEN) {
                        for (int i = 0; i < data.length; i += MAX_BLUETOOTH_SEND_PKG_LEN) {
                            int len = Math.min(data.length - i, MAX_BLUETOOTH_SEND_PKG_LEN);
                            BLEActivity.getBleCtrl().add(GattOperation.newWriteCharacteristic(gatt, write_characteristic, Arrays.copyOfRange(data, i, i + len)));
                        }
                    } else {
                        BLEActivity.getBleCtrl().add(GattOperation.newWriteCharacteristic(gatt, write_characteristic, data));
                    }

                    // 超时处理
                    sendTime = System.currentTimeMillis();
                    callBack = revCb;
                    needCallbackCmdPkg = data;

                    return true;
                }
            }
            return false;
        }

        // 向一个设备发送数据的最基本函数
        boolean sendData(byte[] data) {
            return sendData(data, null);
        }
    }

    private static class GattOperation {
        final static int OP_CHARACTERISTIC_WRITE = 1;
        final static int OP_DESCRIPTOR_WRITE = 2;

        BluetoothGattCharacteristic characteristic;
        BluetoothGattDescriptor descriptor;
        BluetoothGatt gatt;
        byte[] data = null;
        int operation;

        static GattOperation newWriteDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, byte[] data) {
            GattOperation r = new GattOperation();
            r.characteristic = null;
            r.descriptor = descriptor;
            r.data = data;
            r.operation = OP_DESCRIPTOR_WRITE;
            r.gatt = gatt;
            return r;
        }

        static GattOperation newWriteCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] data) {
            GattOperation r = new GattOperation();
            r.characteristic = characteristic;
            r.descriptor = null;
            r.data = data;
            r.operation = OP_CHARACTERISTIC_WRITE;
            r.gatt = gatt;
            return r;
        }

        boolean run() {
            switch (operation) {
                case OP_CHARACTERISTIC_WRITE:
                    Log.i(TAG, "write characteristic : " + characteristic.getUuid() + " : " + Util.hex(data, data.length));
                    characteristic.setValue(data);
                    return gatt.writeCharacteristic(characteristic);
                case GattOperation.OP_DESCRIPTOR_WRITE:
                    Log.i(TAG, "write descriptor " + descriptor.getUuid());
                    descriptor.setValue(data);
                    return gatt.writeDescriptor(descriptor);
            }
            return false;
        }
    }

    // 获得匹配的设备管理器
    private DeviceManager getMatchedDeviceManager(BluetoothGatt gatt) {
        String mac = gatt.getDevice().getAddress();
        return mDeviceManagerSet.get(mac);
    }

    /**
     * 添加扫描过滤器
     */
    void addScanFilter(ParcelUuid uuid) {
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(uuid)
                .build();
        if (mScanFilters == null) {
            mScanFilters = new LinkedList<>();
        }
        mScanFilters.add(filter);
    }


    public void scanDevice() {
        refresh();
    }

    /**
     * 停止扫描
     */
    public void onStopScan() {
    }

    /**
     * 刷新
     */
    void refresh() {
        if (mScanning) {
            return;
        }
        mDoNextRefresh = true;
        mNextRefreshingTime = System.currentTimeMillis();
    }

    /**
     * 添加设备
     */
    void addDevice(String mac) {
        mac = mac.toUpperCase();
        Log.i(TAG, "Add device by mac--->" + mac);
        if (BluetoothAdapter.checkBluetoothAddress(mac)) {
            addDevice(mBluetoothAdapter.getRemoteDevice(mac));
        } else {
            throw new IllegalArgumentException("无效的蓝牙地址 : " + mac);
        }
    }

    /**
     * 接收 mac and data
     */
    void onReceive(String mac, byte[] data) {
        Log.i(TAG, "recv from " + mac + "  : " + Util.hex(data, data.length));
        DeviceManager dev = mDeviceManagerSet.get(mac);
        if (dev != null) {
            dev.dealRecieveDataByProtocol(data);
        }
    }

    /**
     * 设备就绪
     */
    void onDeviceReady(String mac) {
    }

    /**
     * 设备断开
     */
    void onDeviceDisconnect(String mac) {
    }

    /**
     * 注册期
     */
    void registerPeriod(@NonNull String tag, @NonNull Runnable runnable, int period) {
        if (mPeriodRunnable.containsKey(tag)) {
            Runnable old = mPeriodRunnable.get(tag).first;
            mHandler.removeCallbacks(old);
        }
        Runnable wrapped_runnable = new WrappedRunnable(runnable, mHandler, period);
        mPeriodRunnable.put(tag, new Pair<>(wrapped_runnable, period));
        mHandler.postDelayed(wrapped_runnable, period);
    }

    /**
     * 取消注册期限
     */
    void unregisterPeriod(@NonNull String tag) {
        if (mPeriodRunnable.containsKey(tag)) {
            Runnable old = mPeriodRunnable.get(tag).first;
            mHandler.removeCallbacks(old);
            mPeriodRunnable.remove(tag);
        }
    }

    /**
     * 轮询
     */
    void poll() {
        peekDeviceToDiscovering();
        doTimeoutCheck();
    }

    /**
     * 做超时检查
     */
    private void doTimeoutCheck() {
        for (DeviceManager mgr : mDeviceManagerSet.values()) {
            if (mgr.isTimeout()) {
                mgr.callBack.timeout();
                // 只进行一次超时检测处理
                mgr.callBack = null;
                return;
            }
        }
    }

}
