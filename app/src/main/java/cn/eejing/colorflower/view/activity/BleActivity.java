package cn.eejing.colorflower.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.presenter.comm.ObserverManager;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

public abstract class BleActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private static final String BLE_DEV_NAME = "EEJING-CHJ-01";
    public static final String UUID_GATT_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final UUID UUID_GATT_CHARACTERISTIC_WRITE
            = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        initAndConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showConnectedDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开所有设备
        BleManager.getInstance().disconnectAllDevice();
        // 退出使用，清理资源
        BleManager.getInstance().destroy();
    }

    /**
     * 初始化及全局配置
     *
     * @method enableLog          默认打开库中的运行日志，如果不喜欢可以关闭
     * @method setReConnectCount  设置连接时重连次数和重连间隔（毫秒），默认为0次不重连
     * @method setSplitWriteNum   设置分包发送的时候，每一包的数据长度，默认20个字节
     * @method setConnectOverTime 设置连接超时时间（毫秒），默认10秒
     * @method setOperateTimeout  配置操作(readRssi、setMtu、write、read、notify、indicate)超时时间（毫秒），默认5秒
     */
    private void initAndConfig() {
        // 初始化
        BleManager.getInstance().init(getApplication());
        // 全局配置
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    private void showConnectedDevice() {
//        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
//        mDeviceAdapter.clearConnectedDevice();
//        for (BleDevice bleDevice : deviceList) {
//            mDeviceAdapter.addDevice(bleDevice);
//        }
//        mDeviceAdapter.notifyDataSetChanged();
    }

    /**
     * 配置扫描规则 startScan()之前调用
     * <p>
     * - 在扫描设备之前，可以配置扫描规则，筛选出与程序匹配的设备
     * - 不配置的话均为默认参数
     * - 在 2.1.2 版本及之前，必须先配置过滤规则再扫描；
     * - 在 2.1.3 版本之后可以无需配置，开启默认过滤规则的扫描。
     */
    public void setScanRule(String mac) {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)              // 只扫描指定的服务的设备，可选
                .setDeviceName(true, BLE_DEV_NAME)    // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                          // 只扫描指定 mac 的设备，可选
//                .setAutoConnect(isAutoConnect)              // 连接时的 autoConnect 参数，可选，默认false
                .setScanTimeOut(10000)                      // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /**
     * 扫描
     * - 扫描及过滤过程是在工作线程中进行，所以不会影响主线程的UI操作，最终每一个回调结果都会回到主线程。
     */
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
     * 中止扫描
     * <p>
     * - 调用该方法后，如果当前还处在扫描状态，会立即结束，并回调`onScanFinished`方法。
     */
    public void cancelScan() {
        BleManager.getInstance().cancelScan();
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
    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override/* 开始连接 */
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override/* 连接失败 */
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
//                img_loading.clearAnimation();
//                img_loading.setVisibility(View.INVISIBLE);
//                btn_scan.setText(getString(R.string.start_scan));
                ToastUtil.showLong(getString(R.string.connect_fail));
            }

            @Override/* 连接成功，BleDevice 即为所连接的 BLE 设备 */
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override/* 连接中断，isActiveDisConnected 表示是否是主动调用了断开连接方法 */
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

//                mDeviceAdapter.removeDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    ToastUtil.showLong(getString(R.string.active_disconnected));
                } else {
                    ToastUtil.showLong(getString(R.string.disconnected));
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }

            }
        });
    }

    /**
     * 连接【通过已知设备Mac】
     * <p>
     * - 此方法可以不经过扫描，尝试直接连接周围复合该Mac的BLE设备。
     * - 在很多使用场景，我建议APP保存用户惯用设备的Mac，然后使用该方法进行连接可以大大提高连接效率。
     */
    private void connectByMac(final String mac) {
        BleManager.getInstance().connect(mac, new BleGattCallback() {
            @Override/* 开始连接 */
            public void onStartConnect() {

            }

            @Override/* 连接失败 */
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

            }

            @Override/* 连接成功，BleDevice 即为所连接的 BLE 设备 */
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

            }

            @Override/* 连接中断，isActiveDisConnected 表示是否是主动调用了断开连接方法 */
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

            }
        });
    }

    /**
     * 扫描并连接
     * <p>
     * - 扫描及过滤过程是在工作线程中进行，所以不会影响主线程的UI操作，但每一个回调结果都会回到主线程。
     * - 连接操作会在主线中进行。
     */
    private void scanAndConnect() {
        BleManager.getInstance().scanAndConnect(new BleScanAndConnectCallback() {
            @Override/* 扫描结束，结果即为扫描到的第一个符合扫描规则的BLE设备，如果为空表示未搜索到（主线程）*/
            public void onScanFinished(BleDevice scanResult) {

            }

            @Override/* 开始连接（主线程）*/
            public void onStartConnect() {

            }

            @Override/* 连接失败（主线程）*/
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

            }

            @Override/* 连接成功，BleDevice即为所连接的BLE设备（主线程）*/
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

            }

            @Override/* 连接断开，isActiveDisConnected是主动断开还是被动断开（主线程）*/
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

            }

            @Override/* 开始扫描（主线程） */
            public void onScanStarted(boolean success) {

            }

            @Override/*  */
            public void onScanning(BleDevice bleDevice) {

            }
        });
    }

    /**
     * 获取设备的信号强度 Rssi
     * <p>
     * 获取设备的信号强度，需要在设备连接之后进行。
     * 某些设备可能无法读取 Rssi，不会回调 onRssiSuccess(),而会因为超时而回调 onRssiFailure()。
     */
    private void readRssi(BleDevice bleDevice) {
        BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
            @Override/* 读取设备的信号强度失败 */
            public void onRssiFailure(BleException exception) {
                Log.i(TAG, "onRssiFailure" + exception.toString());
            }

            @Override/* 读取设备的信号强度成功 */
            public void onRssiSuccess(int rssi) {
                Log.i(TAG, "onRssiSuccess: " + rssi);
            }
        });
    }

    /**
     * 设置最大传输单元MTU
     * <p>
     * 设置 MTU，需要在设备连接之后进行操作。
     * 默认每一个 BLE 设备都必须支持的 MTU 为23。
     * MTU 为 23，表示最多可以发送 20 个字节的数据。
     * 在 Android 低版本(API-17 到 API-20)上，没有这个限制。所以只有在 API21 以上的设备，才会有拓展 MTU 这个需求。
     * 该方法的参数 mtu，最小设置为 23，最大设置为 512。
     * 并不是每台设备都支持拓展 MTU，需要通讯双方都支持才行，也就是说，需要设备硬件也支持拓展 MTU 该方法才会起效果。
     * 调用该方法后，可以通过 onMtuChanged(int mtu) 查看最终设置完后，设备的最大传输单元被拓展到多少。
     * 如果设备不支持，可能无论设置多少，最终的 mtu 还是 23。
     */
    private void setMtu(BleDevice bleDevice, int mtu) {
        BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
            @Override/* 设置 MTU 失败 */
            public void onSetMTUFailure(BleException exception) {
                Log.i(TAG, "onsetMTUFailure" + exception.toString());
            }

            @Override/* 设置 MTU 成功，并获得当前设备传输支持的 MTU 值 */
            public void onMtuChanged(int mtu) {
                Log.i(TAG, "onMtuChanged: " + mtu);
            }
        });
    }

    /** 请求权限结果回调方法 */
    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            // 授予许可
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 检查权限
     * <p>
     * -可每次扫描时调用
     */
    public void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 如果用户没打开蓝牙，提示用户打开蓝牙
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        // 访问精细的位置权限
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        // 权限拒绝列表
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            // 检查自我许可
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            // 如果用户许可授予，进行许可后操作；反之，加入权限拒绝列表
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    /** 授予许可 */
    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                // 如果 Build 版本达到 23 且 GPS 未打开，提示用户打开 GPS 权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    (dialog, which) -> finish())
                            .setPositiveButton(R.string.setting,
                                    (dialog, which) -> {
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                    })

                            .setCancelable(false)
                            .show();
                } else {
//                    setScanRule();
                    startScan();
                }
                break;
        }
    }

    /** 检查GPS是否打开 */
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
//                setScanRule();
                startScan();
            }
        }
    }

}
