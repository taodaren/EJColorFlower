package cn.eejing.colorflower.model.device;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.bluetooth.BleBluetooth;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.exception.OtherException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.BleLog;
import com.clj.fastble.utils.HexUtil;

import java.util.List;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.presenter.ble.connectByDev.IConnectFailByDev;
import cn.eejing.colorflower.presenter.ble.connectByDev.IConnectSuccessByDev;
import cn.eejing.colorflower.presenter.ble.connectByDev.IDisConnectedByDev;
import cn.eejing.colorflower.presenter.ble.connectByDev.IStartConnectByDev;
import cn.eejing.colorflower.presenter.ble.connectByMac.IConnectFailByMac;
import cn.eejing.colorflower.presenter.ble.connectByMac.IConnectSuccessByMac;
import cn.eejing.colorflower.presenter.ble.connectByMac.IDisConnectedByMac;
import cn.eejing.colorflower.presenter.ble.connectByMac.IStartConnectByMac;
import cn.eejing.colorflower.presenter.ble.notify.ICharacteristicChanged;
import cn.eejing.colorflower.presenter.ble.notify.INotifyFailure;
import cn.eejing.colorflower.presenter.ble.notify.INotifySuccess;
import cn.eejing.colorflower.presenter.ble.startScan.ILeScan;
import cn.eejing.colorflower.presenter.ble.startScan.IScanFinished;
import cn.eejing.colorflower.presenter.ble.startScan.IScanStarted;
import cn.eejing.colorflower.presenter.ble.startScan.IScanning;
import cn.eejing.colorflower.presenter.ble.write.IWriteFailure;
import cn.eejing.colorflower.presenter.ble.write.IWriteSuccess;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;

/**
 * 用于每次只连接一个蓝牙设备处理
 */

public class BleSingleDevCtrl {
    protected static final String    TAG                              = "BleSingleDevCtrl";
    protected static final String    BLE_DEV_NAME                     = "EEJING-CHJ-01";
    protected static final String    UUID_GATT_SERVICE                = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    protected static final String    UUID_GATT_CHARACTERISTIC_NOTIFY  = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    protected static final String    UUID_GATT_CHARACTERISTIC_WRITE   = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    protected static final int       MSG_CONNECT_SUCCESS              = 1;

    protected IStartConnectByMac     startConnectByMac;
    protected IConnectFailByMac      connectFailByMac;
    protected IConnectSuccessByMac   connectSuccessByMac;
    protected IDisConnectedByMac     disConnectedByMac;

    protected IStartConnectByDev     startConnectByDev;
    protected IConnectFailByDev      connectFailByDev;
    protected IConnectSuccessByDev   connectSuccessByDev;
    protected IDisConnectedByDev     disConnectedByDev;

    protected ILeScan                leScan;
    protected IScanFinished          scanFinished;
    protected IScanning              scanning;
    protected IScanStarted           scanStarted;

    protected INotifySuccess         notifySuccess;
    protected INotifyFailure         notifyFailure;
    protected ICharacteristicChanged characteristicChanged;

    protected IWriteSuccess          writeSuccess;
    protected IWriteFailure          writeFailure;

    protected BleDevice mBleDevice;
    protected long mDevId;
    protected static BleSingleDevCtrl bleCtrl = null;

    public static BleSingleDevCtrl getInstance() {
        if (bleCtrl == null){
            new BleSingleDevCtrl();
        }
        return bleCtrl;
    }

    // 是否已经连接
    protected boolean isConnect = false;

    /** 此构造方法用于各接口回调的默认实现 */
    public BleSingleDevCtrl() {
        scanStarted = success -> {
        };
        scanning = bleDevice -> {
        };
        scanFinished = scanResultList -> {
        };
        leScan = (BleDevice bleDevice) -> {
        };

        startConnectByDev = () -> {
        };
        connectFailByDev = (bleDevice, exception) -> {
        };
        connectSuccessByDev = (bleDevice, gatt, status) -> {
        };
        disConnectedByDev = (isActiveDisConnected, bleDevice, gatt, status) -> {
        };

        startConnectByMac = () -> {
        };
        connectFailByMac = (bleDevice, exception) -> {
        };
        connectSuccessByMac = (bleDevice, gatt, status) -> {
        };
        disConnectedByMac = (isActiveDisConnected, bleDevice, gatt, status) -> {
        };

        notifySuccess = () -> {
        };
        notifyFailure = exception -> {
        };
        characteristicChanged = data -> {
        };

        writeSuccess = (current, total, justWrite) -> {
        };
        writeFailure = exception -> {
        };
        bleCtrl = this;
    }

    public void sleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 配置扫描规则 startScan()之前调用
     * <p>
     * - 在扫描设备之前，可以配置扫描规则，筛选出与程序匹配的设备
     * - 不配置的话均为默认参数
     * - 在 2.1.2 版本及之前，必须先配置过滤规则再扫描；
     * - 在 2.1.3 版本之后可以无需配置，开启默认过滤规则的扫描。
     */
    public BleSingleDevCtrl setScanRule(String mac) {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids(serviceUuids)              // 只扫描指定的服务的设备，可选
                .setDeviceName(true, BLE_DEV_NAME)    // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                          // 只扫描指定 mac 的设备，可选
//                .setAutoConnect(isAutoConnect)              // 连接时的 autoConnect 参数，可选，默认false
                .setScanTimeOut(10000)                      // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        return this;
    }

    protected String mConnectMac = null;
    /**
     * 扫描
     * - 扫描及过滤过程是在工作线程中进行，所以不会影响主线程的UI操作，最终每一个回调结果都会回到主线程。
     */
    public BleSingleDevCtrl startScan(String mac) {
        // 扫描规则配置
        mConnectMac = mac;
        setScanRule(mac);

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override/* 开始扫描（主线程）*/
            public void onScanStarted(boolean success) {
                LogUtil.i(TAG, "开始扫描: " + success);
                scanStarted.onScanStarted(success);
            }

            @Override/* 扫描过程中所有被扫描到的结果回调 */
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                leScan.onLeScan(bleDevice);
            }

            @Override/* 扫描到一个符合扫描规则的 BLE 设备（主线程）*/
            public void onScanning(BleDevice bleDevice) {
                LogUtil.i(TAG, "扫描中: " + bleDevice.getMac());
                // 扫描到一个符合扫描规则的设备停止扫描并开始连接
                cancelScan();
                connectByMac(mac);
                scanning.onScanning(bleDevice);
            }

            @Override/* 扫描结束，列出所有扫描到的符合扫描规则的BLE设备（主线程）*/
            public void onScanFinished(List<BleDevice> scanResultList) {
                LogUtil.i(TAG, "扫描结束");
                scanFinished.onScanFinished(scanResultList);
            }
        });
        return this;
    }


    /**
     * 断开连接，主动调用断开连接函数，否则检测到断开需要重连
     */
    public void disConnect(){
        mConnectMac = null;
        if( mBleDevice != null ) {
            BleManager.getInstance().disconnect(mBleDevice);
            mBleDevice = null;
        }
    }

    /**
     * 中止扫描
     * <p>
     * - 调用该方法后，如果当前还处在扫描状态，会立即结束，并回调`IScanFinished`方法。
     */
    public BleSingleDevCtrl cancelScan() {
        BleManager.getInstance().cancelScan();
        return this;
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
    public BleSingleDevCtrl connectByDev(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override/* 开始连接 */
            public void onStartConnect() {
                startConnectByDev.onStartConnect();
            }

            @Override/* 连接失败 */
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                ToastUtil.showLong(BaseApplication.getContext().getString(R.string.connect_fail));
                connectFailByDev.onConnectFail(bleDevice, exception);
            }

            @Override/* 连接成功，BleDevice 即为所连接的 BLE 设备 */
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                DealWhenConnectSuccess(bleDevice, gatt, status);
            }

            @Override/* 连接中断，isActiveDisConnected 表示是否是主动调用了断开连接方法 */
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                DealWhenDisConnected(isActiveDisConnected, bleDevice, gatt, status);
            }
        });
        return this;
    }

    protected void DealWhenConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        LogUtil.i(TAG, "连接成功: " + bleDevice.getMac());
        setMtu(bleDevice, 18);
        mBleDevice = bleDevice;
        // 连接成功，100 毫秒后 notify
        sleepTime(100);
        isConnect = true;
        openNotify();
        connectSuccessByMac.onConnectSuccess(bleDevice, gatt, status);
    }

    protected void DealWhenDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
        if ( mConnectMac == null ) {
            // 主动调用断开命令 断开设备
            ToastUtil.showLong(BaseApplication.getContext().getString(R.string.active_disconnected));
        } else {
            // 因为信号弱，或者其它原因断开；需要重连
            ToastUtil.showLong(BaseApplication.getContext().getString(R.string.other_disconnected));
            //ObserverManager.getInstance().notifyObserver(bleDevice);
            startScan( mConnectMac );
        }
        isConnect = false;
        disConnectedByMac.onDisConnected(isActiveDisConnected, bleDevice, gatt, status);
    }

    /**
     * 连接【通过已知设备Mac】
     * <p>
     * - 此方法可以不经过扫描，尝试直接连接周围复合该Mac的BLE设备。
     * - 在很多使用场景，我建议APP保存用户惯用设备的Mac，然后使用该方法进行连接可以大大提高连接效率。
     */
    public BleSingleDevCtrl connectByMac(String mac) {
        BleManager.getInstance().connect(mac, new BleGattCallback() {
            @Override/* 开始连接 */
            public void onStartConnect() {
                startConnectByMac.onStartConnect();
            }

            @Override/* 连接失败 */
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                ToastUtil.showLong(BaseApplication.getContext().getString(R.string.connect_fail));
                isConnect = false;
                connectFailByMac.onConnectFail(bleDevice, exception);
            }

            @Override/* 连接成功，BleDevice 即为所连接的 BLE 设备 */
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                DealWhenConnectSuccess(bleDevice, gatt, status);
            }

            @Override/* 连接中断，isActiveDisConnected 表示是否是主动调用了断开连接方法 */
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                DealWhenDisConnected(isActiveDisConnected, bleDevice, gatt, status);
            }
        });
        return this;
    }

    protected void DealWhenOpenNotifySuccess(){
        LogUtil.i(TAG, "通知成功");
        notifySuccess.onNotifySuccess();
    }

    protected void DealWhenReceiveData(byte[] data){
//        LogUtil.i(TAG, "通知接收数据: " + HexUtil.formatHexString(data, true));
        characteristicChanged.onCharacteristicChanged(data);
    }

    /** 发送数据成功的回调函数处理过程 */
    protected void DealWhenSendDataSuccess(int current, int total, byte[] justWrite) {
        //LogUtil.d(TAG, "发送数据成功: " + HexUtil.formatHexString(justWrite, true));
    }

    /** 发送数据失败的回调处理过程 */
    protected void DealWhenSendDataFailure(BleException exception) {
        LogUtil.i(TAG, "发送数据失败: " + exception.getDescription());
    }

    /** 彩花机数据发送过程 */
    public BleSingleDevCtrl sendData(byte[] bytes) {
        write(mBleDevice, UUID_GATT_SERVICE, UUID_GATT_CHARACTERISTIC_WRITE, bytes, true,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        DealWhenSendDataSuccess(current, total, justWrite);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        DealWhenSendDataFailure(exception);
                    }
                }
        );
        return this;
    }

    private void write(BleDevice bleDevice,
                       String uuid_service,
                       String uuid_write,
                       byte[] data,
                       boolean split,
                       BleWriteCallback callback) {

        if (callback == null) {
            throw new IllegalArgumentException("BleWriteCallback不能为空！");
        }

        if (data == null) {
            BleLog.e("写入数据为null");
            callback.onWriteFailure(new OtherException("write data is Null!"));
            return;
        }

        if (data.length > 18 && !split) {
            BleLog.w("注意：数据长度超过18！");
        }

        BleBluetooth bleBluetooth = BleManager.getInstance().getMultipleBluetoothController().getBleBluetooth(bleDevice);
        if (bleBluetooth == null) {
            callback.onWriteFailure(new OtherException("此设备无法连接！"));
        } else {
            if (split && data.length > 18) {
                new MySplitWriter().splitWrite(bleBluetooth, uuid_service, uuid_write, data, callback);
            } else {
                bleBluetooth.newBleConnector()
                        .withUUIDString(uuid_service, uuid_write)
                        .writeCharacteristic(data, callback, uuid_write);
            }
        }
    }

    /**
     * 扫描并连接
     * <p>
     * - 扫描及过滤过程是在工作线程中进行，所以不会影响主线程的UI操作，但每一个回调结果都会回到主线程。
     * - 连接操作会在主线中进行。
     */
    protected BleSingleDevCtrl scanAndConnect() {
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
        return this;
    }

    protected BleSingleDevCtrl openNotify() {
        BleManager.getInstance().notify(mBleDevice,
                UUID_GATT_SERVICE, UUID_GATT_CHARACTERISTIC_NOTIFY,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        DealWhenOpenNotifySuccess();
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        LogUtil.i(TAG, "onNotifyFailure: " + exception.getDescription());
                        notifyFailure.onNotifyFailure(exception);
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        DealWhenReceiveData(data);
                    }
                });
        return this;
    }

    private BleSingleDevCtrl openIndicate(BleDevice bleDevice) {
        BleManager.getInstance().indicate(bleDevice,
                UUID_GATT_SERVICE, UUID_GATT_CHARACTERISTIC_NOTIFY,
                new BleIndicateCallback() {
                    @Override
                    public void onIndicateSuccess() {
                        LogUtil.i(TAG, "onIndicateSuccess: ");
                    }

                    @Override
                    public void onIndicateFailure(BleException exception) {
                        LogUtil.i(TAG, "onIndicateFailure: " + exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        LogUtil.i(TAG, "onCharacteristicChanged: " + HexUtil.formatHexString(data, true));
                    }
                });
        return this;
    }

    public BleSingleDevCtrl writeDate(BleDevice bleDevice, byte[] sendCmdPkg) {
        BleManager.getInstance().write(bleDevice,
                UUID_GATT_SERVICE, UUID_GATT_CHARACTERISTIC_WRITE,
                sendCmdPkg, new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        LogUtil.d(TAG, "onWriteSuccess: " + HexUtil.formatHexString(justWrite, true));
                        writeSuccess.onWriteSuccess(current, total, justWrite);

                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        LogUtil.i(TAG, "onWriteFailure: " + exception.getDescription());
                        writeFailure.onWriteFailure(exception);
                    }
                });
        return this;
    }

    /**
     * 获取设备的信号强度 Rssi
     * <p>
     * 获取设备的信号强度，需要在设备连接之后进行。
     * 某些设备可能无法读取 Rssi，不会回调 onRssiSuccess(),而会因为超时而回调 onRssiFailure()。
     */
    private BleSingleDevCtrl readRssi(BleDevice bleDevice) {
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
        return this;
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
    private BleSingleDevCtrl setMtu(BleDevice bleDevice, int mtu) {
        setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
            @Override/* 设置 MTU 失败 */
            public void onSetMTUFailure(BleException exception) {
                Log.i(TAG, "onsetMTUFailure" + exception.toString());
            }

            @Override/* 设置 MTU 成功，并获得当前设备传输支持的 MTU 值 */
            public void onMtuChanged(int mtu) {
                Log.i(TAG, "onMtuChanged: " + mtu);
            }
        });

        return this;
    }

    private void setMtu(BleDevice bleDevice,
                        int mtu,
                        BleMtuChangedCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleMtuChangedCallback can not be Null!");
        }

        if (mtu > 512) {
            BleLog.e("requiredMtu should lower than 512 !");
            callback.onSetMTUFailure(new OtherException("requiredMtu should lower than 512 !"));
            return;
        }

        if (mtu < 18) {
            BleLog.e("requiredMtu should higher than 18 !");
            callback.onSetMTUFailure(new OtherException("requiredMtu should higher than 18 !"));
            return;
        }

        BleBluetooth bleBluetooth =  BleManager.getInstance().getMultipleBluetoothController().getBleBluetooth(bleDevice);
        if (bleBluetooth == null) {
            callback.onSetMTUFailure(new OtherException("This device is not connected!"));
        } else {
            bleBluetooth.newBleConnector().setMtu(mtu, callback);
        }
    }

    public BleSingleDevCtrl setDevId(long mDevId) {
        this.mDevId = mDevId;
        return this;
    }

    public BleSingleDevCtrl setLeScan(ILeScan leScan) {
        this.leScan = leScan;
        return this;
    }

    public BleSingleDevCtrl setScanFinished(IScanFinished scanFinished) {
        this.scanFinished = scanFinished;
        return this;
    }

    public BleSingleDevCtrl setScanning(IScanning scanning) {
        this.scanning = scanning;
        return this;
    }

    public BleSingleDevCtrl setScanStarted(IScanStarted scanStarted) {
        this.scanStarted = scanStarted;
        return this;
    }

    public BleSingleDevCtrl setNotifySuccess(INotifySuccess notifySuccess) {
        this.notifySuccess = notifySuccess;
        return this;
    }

    public BleSingleDevCtrl setNotifyFailure(INotifyFailure notifyFailure) {
        this.notifyFailure = notifyFailure;
        return this;
    }

    public BleSingleDevCtrl setCharacteristicChanged(ICharacteristicChanged characteristicChanged) {
        this.characteristicChanged = characteristicChanged;
        return this;
    }

    public BleSingleDevCtrl setWriteSuccess(IWriteSuccess writeSuccess) {
        this.writeSuccess = writeSuccess;
        return this;
    }

    public BleSingleDevCtrl setWriteFailure(IWriteFailure writeFailure) {
        this.writeFailure = writeFailure;
        return this;
    }

    public BleSingleDevCtrl setOnStartConnectByDev(IStartConnectByDev startConnect) {
        this.startConnectByDev = startConnect;
        return this;
    }

    public BleSingleDevCtrl setOnConnectFailByDev(IConnectFailByDev connectFail) {
        this.connectFailByDev = connectFail;
        return this;
    }

    public BleSingleDevCtrl setOnConnectSuccessByDev(IConnectSuccessByDev connectSuccess) {
        this.connectSuccessByDev = connectSuccess;
        return this;
    }

    public BleSingleDevCtrl setOnDisConnectedByDev(IDisConnectedByDev disConnected) {
        this.disConnectedByDev = disConnected;
        return this;
    }

    public BleSingleDevCtrl setOnStartConnectByMac(IStartConnectByMac startConnect) {
        this.startConnectByMac = startConnect;
        return this;
    }

    public BleSingleDevCtrl setOnConnectFailByMac(IConnectFailByMac connectFail) {
        this.connectFailByMac = connectFail;
        return this;
    }

    public BleSingleDevCtrl setOnConnectSuccessByMac(IConnectSuccessByMac connectSuccess) {
        this.connectSuccessByMac = connectSuccess;
        return this;
    }

    public BleSingleDevCtrl setOnDisConnectedByMac(IDisConnectedByMac disConnected) {
        this.disConnectedByMac = disConnected;
        return this;
    }

}
