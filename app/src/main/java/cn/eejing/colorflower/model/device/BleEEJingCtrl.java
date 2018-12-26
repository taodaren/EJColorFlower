package cn.eejing.colorflower.model.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;

import cn.eejing.colorflower.model.event.DevConnEvent;
import cn.eejing.colorflower.presenter.OnReceivePackage;
import cn.eejing.colorflower.util.BleDevProtocol;
import cn.eejing.colorflower.util.LogUtil;

import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;

/**
 * 彩花机相关逻辑控制
 */

public class BleEEJingCtrl extends BleSingleDevCtrl {

    private static BleEEJingCtrl eeCtrl;
    private ProtocolWithDev mCurDevPro;
    private int curSendFailureCnt; // 连续失败次数

    /** 此构造方法用于各接口回调的默认实现 */
    private BleEEJingCtrl() {
        super();
        eeCtrl = this;
    }

    public static BleEEJingCtrl getInstance() {
        if (eeCtrl == null) {
            new BleEEJingCtrl();
        }
        return eeCtrl;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CONNECT_SUCCESS:
                    // 每 2s 获取状态
                    getDevStatus();
                    mHandler.sendEmptyMessageDelayed(MSG_CONNECT_SUCCESS, 2000);
                    break;
            }
        }
    };

    /** 提供发送命令的接口调用 */
    public void sendCommand(byte[] pkg, OnReceivePackage callback) {
        if (mCurDevPro == null) {
            return;
        }
        mCurDevPro.sendCommand(pkg, callback);
    }

    /** 蓝牙发送命令，接收数据超时处理 */
    private void DealWhenCmdSendAndRecvCbTimeOut(){
        mCurDevPro.flagAddTimeOut++;
        if (mCurDevPro.flagAddTimeOut > 3) {
            // 超时断开连接
            EventBus.getDefault().post(new DevConnEvent(mBleDevice.getMac(), DEVICE_CONNECT_NO));
            mCurDevPro.flagAddTimeOut = 0;
            mCurDevPro.bSendEn = false;
        }
    }

    /** 添加一个获取配置命令 到 发送列表 */
    private void getDevCfg() {
        if( mCurDevPro == null ){ return; }
        mCurDevPro.sendCommand(BleDevProtocol.pkgGetConfig(mDevId), new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                mCurDevPro.nCurDealSend = null;
            }

            @Override
            public void timeout() {
                DealWhenCmdSendAndRecvCbTimeOut();
            }
        });
    }

    /** 添加一个获取状态命令 到 发送列表 */
    private void getDevStatus() {
        mCurDevPro.sendCommand(BleDevProtocol.pkgGetStatus(mDevId), new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                mCurDevPro.nCurDealSend = null;
            }

            @Override
            public void timeout() {
                DealWhenCmdSendAndRecvCbTimeOut();
            }
        });
    }

    /** 真正的设备断开连接的处理过程 */
    @Override
    protected void DealWhenDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
        super.DealWhenDisConnected(isActiveDisConnected, bleDevice, gatt, status);
        if (mCurDevPro != null) {
            mCurDevPro.stopSendThread();
            mCurDevPro = null;
        }
    }

    /** 真正设备连接成功的处理 */
    @Override
    protected void DealWhenOpenNotifySuccess() {
        // notify 通知成功 当前是真正连接成功；开启一个收发数据线程
        Device dev = new Device(mBleDevice.getMac());
        dev.setId(mDevId);
        // 创建收发数据线程及协议处理对象
        mCurDevPro = new ProtocolWithDev(dev);
        // 连续失败次数
        curSendFailureCnt = 0;
        super.DealWhenOpenNotifySuccess();
    }

    /** 真正 设备接收数据的处理过程 */
    @Override
    protected void DealWhenReceiveData(byte[] data) {
        if( mCurDevPro != null ){
            // 接收数据 协议分包解析处理过程
            mCurDevPro.bleReceive(data);
        }
        super.DealWhenReceiveData(data);
    }

    /** 发送数据成功的回调函数处理过程 */
    @Override
    protected void DealWhenSendDataSuccess(int current, int total, byte[] justWrite){
        //LogUtil.d(TAG, "发送数据成功: " + HexUtil.formatHexString(justWrite, true));
        super.DealWhenSendDataSuccess(current,total,justWrite);
        curSendFailureCnt = 0;
    }

    /** 发送数据失败的回调处理过程 */
    @Override
    protected void DealWhenSendDataFailure(BleException exception){
        curSendFailureCnt++;
        if( curSendFailureCnt > 5 ){
            //断开连接 并且重连
            sleepTime(100);
            BleManager.getInstance().disconnect(mBleDevice);
            sleepTime(100);
            connectByMac(mBleDevice.getMac());
        }
        LogUtil.i(TAG, "发送数据失败: " + curSendFailureCnt + " 异常描述: " + exception.getDescription());
    }

    /** 彩花机数据发送过程；需要进行加密 */
    @Override
    public BleSingleDevCtrl sendData(byte[] bytes) {
        return super.sendData(BleDevProtocol.wrappedPackage(bytes));
    }

    /** 继承通过蓝牙与彩花机进行通信的协议数据处理 */
    private class ProtocolWithDev extends BleDevProtocol{
        final Device device;
        boolean bSendEn = true;              // 用于判断线程是否需要结束
        PackageNeedAck nCurDealSend = null;  // 用于引用当前正在发送和等待回复的命令
        final Object lock = new Object();
        int flagAddTimeOut = 0;
        // 有一个列队用于缓冲需要发送的数据
        private final LinkedList<PackageNeedAck> mCmdAckList = new LinkedList<>();

        /** 添加一个命令 */
        void addSendCmd(PackageNeedAck pkg_Ack) {
            synchronized (mCmdAckList) {
                mCmdAckList.addLast(pkg_Ack);
            }
            synchronized (lock) {
                lock.notify();
            }
        }

        /** 添加一个命令；传入需要发送的加密前的数据 */
        void sendCommand(byte[] pkg, OnReceivePackage callback) {
            if (!bConnect || (mBleDevice == null)) {
                return;
            }
            addSendCmd(new PackageNeedAck(mBleDevice.getMac(), pkg, callback));
        }

        ProtocolWithDev(Device device) {
            this.device = device;
            // 创建一个用于管理数据发送和应答的线程
            sendThread.start();
        }

        void stopSendThread() {
            bSendEn = false;
            synchronized (lock) {
                lock.notify();
            }
        }

        /* 添加一个管理每个蓝牙设备数据发送的队列
           每个设备连接到手机后，手机开启一个线程，用于管理当前设备的数据发送，接收，超时，重发 */
        Thread sendThread = new Thread() { // 发送和等待回复命令的处理线程（通道）
            @Override
            public void run() {
                super.run();
                sleepTime(100);  //打开通知成功，蓝牙设备真正连接成功，等待一段时间；开始进行收发处理
                getDevCfg(); //启动时候添加一个 获取配置 和 获取状态的命令
                getDevStatus();
                while (bSendEn && bConnect) { //当前发送过程中；而且设备处于连接成功状态
                    if (nCurDealSend != null) { // 当前有发送需要进行处理
                        PackageNeedAck curDeal = nCurDealSend; //创建一个临时引用，保存当前正在处理的命令
                        if (curDeal.redoCntWhenTimeOut > 0) {
                            curDeal.redoCntWhenTimeOut--;
                            sendData(curDeal.cmd_pkg);
                            // 根据当前发送命令是否需要回复的类型，设置等待时间
                            if (curDeal.callback == null) {
                                sleepTime(70);
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
                                    getDevCfg(); //启动时候添加一个 获取配置 和 获取状态的命令
                                } else {
                                    getDevStatus();  //添加一个获取状态 的命令
                                }
                                //上面获取状态或者获取配置肯定会添加一个命令；下面将命令提取出来
                                synchronized (mCmdAckList) {
                                    nCurDealSend = mCmdAckList.getFirst();
                                    mCmdAckList.removeFirst();
                                }
                            }
                        }
                    }
                }
            }
        };

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
                    LogUtil.i(TAG, "回复数据和命令不匹配: " +
                            HexUtil.formatHexString(nCurDealSend.cmd_pkg, true) +
                            " 接收 " + HexUtil.formatHexString(nCurDealSend.cmd_pkg, true));
                }
                nCurDealSend = null;
            });
        }
    }

    /** 数据发送命令管理类，需要将发送命令和接收回调放在一起进行管理 */
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

//        PackageNeedAck(String mac, byte[] cmd_pkg, OnReceivePackage callback, int redoCnt) {
//            this.mac = mac;
//            this.cmd_pkg = cmd_pkg;
//            this.callback = callback;
//            this.redoCntWhenTimeOut = redoCnt;
//        }
    }
}
