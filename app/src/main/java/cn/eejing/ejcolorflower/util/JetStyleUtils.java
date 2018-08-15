package cn.eejing.ejcolorflower.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.ConnDevInfo;
import cn.eejing.ejcolorflower.view.activity.AppActivity;

/**
 * 喷射配置工具类
 */

public class JetStyleUtils {
    private static final int WHAT_INTERVAL = 3;
    private static AppActivity.FireworkDevCtrl mDevCtrl = AppActivity.getFireworksDevCtrl();

    private static List<ConnDevInfo> mDevList;
    private static int mGap, mDuration, mFrequency, mLoopId;

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_INTERVAL:
                    if (mLoopId < mFrequency) {
                        long delay;
                        if (mLoopId == 0) {
                            delay = 0;
                            final long aDelay = delay;
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("TTJET", "第 " + mLoopId + " 轮喷射开始");
                                    firstInterval();
                                    Log.i("TTJET", "第 " + mLoopId + " 轮喷射结束，延时 " + aDelay + " 秒");
                                    mLoopId++;
                                    mHandler.sendEmptyMessage(WHAT_INTERVAL);
                                }
                            }, delay);
                        }
                        if (mLoopId > 0) {
                            if (mLoopId == 1) {
                                delay = mGap;
                            } else {
                                delay = (mDuration / 10 + mGap / 1000) * 1000;
                            }
                            final long bDelay = delay;
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("TTJET", "第 " + mLoopId + " 轮喷射开始");
                                    otherInterval();
                                    Log.i("TTJET", "第 " + mLoopId + " 轮喷射结束，延时 " + mGap / 1000 + " 秒");
                                    mLoopId++;
                                    mHandler.sendEmptyMessage(WHAT_INTERVAL);
                                }
                            }, mGap);
                        }
                    } else {
                        mHandler.removeMessages(WHAT_INTERVAL);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private static void firstInterval() {
        for (int devLoc = 0; devLoc < mDevList.size(); devLoc++) {
            try {
                switch (devLoc % 2) {
                    case 0:
                        // 第偶数个设备
                        printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, 0, mDuration, 100);
                        jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), 0, mDuration, 100));
                        Thread.sleep(10);
                        break;
                    case 1:
                        printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, 0, mDuration, 40);
                        jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), 0, mDuration, 40));
                        Thread.sleep(10);
                        // 第奇数个设备
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void otherInterval() {
        switch (mLoopId % 2) {
            case 0:
                // 第偶数次喷射
                for (int devLoc = 0; devLoc < mDevList.size(); devLoc++) {
                    try {
                        switch (devLoc % 2) {
                            case 0:
                                // 第偶数个设备
                                printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 100);
                                jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 100));
                                Thread.sleep(10);
                                break;
                            case 1:
                                // 第奇数个设备
                                printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 40);
                                jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 40));
                                Thread.sleep(10);
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                // 第奇数次喷射
                for (int devLoc = 0; devLoc < mDevList.size(); devLoc++) {
                    try {
                        switch (devLoc % 2) {
                            case 0:
                                // 第偶数个设备
                                printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 40);
                                jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 40));
                                Thread.sleep(10);
                                break;
                            case 1:
                                printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 100);
                                jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 100));
                                Thread.sleep(10);
                                // 第奇数个设备
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 流水灯
     */
    public static void jetStream(final long devID, final int devLocation, int direction, int gap, int duration, final int gapBig, int loop, int high, int listSize) {
        final byte[] pkgStream = BleDeviceProtocol.pkgJetStart(devID, gap * devLocation, (listSize - devLocation) * duration, high);

        // 循环 loop 次
        for (int loopNum = 0; loopNum < loop; loopNum++) {
            if (loopNum == 0) {
                Log.e("SWITCH_CTRL", devID + "第" + loopNum + "次喷射");

                try {
                    // 如果是首次喷射，直接发送喷射命令
                    jetStart(devID, pkgStream);
                    // 为了多台设备一起喷射，5毫秒人实际感受不到
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (loopNum > 0) {
                Log.e("SWITCH_CTRL", devID + "第" + loopNum + "次喷射");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 两次循环间隔时间
                            TimeUnit.SECONDS.sleep((long) gapBig);
                            jetStart(devID, pkgStream);
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }
    }

    /**
     * 跑马灯
     */
    public static void jetRide(long devID, int devLocation, int direction, int gap, int duration, int gapBig, int loop, int high) {

    }

    /**
     * 间隔高低
     */
    public static void jetInterval(final List<ConnDevInfo> devList, final int gap, final int duration, int frequency) {
        mDevList = devList;
        mGap = gap;
        mDuration = duration;
        mFrequency = frequency;

        // 如果次数为 1 次，不换方向，喷一轮
        if (mFrequency == 0) {
            onlyOneInterval(mDevList, mDuration);
        } else {
            mLoopId = 0;
            mHandler.sendEmptyMessage(WHAT_INTERVAL);
        }
//        Log.e("ljmsx", "第 " + (finalLoopId + 1) + " 轮喷射，延时 " + delayTime + " 秒");
//        switch (finalLoopId) {
//            case 0:
//                // 第 1 轮喷射
//                for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
//                    try {
//                        switch (devLoc % 2) {
//                            case 0:
//                                // 第偶数个设备
//                                printLog(devList.get(devLoc).getDevID(), finalLoopId, devLoc, 0, duration, 100);
//                                jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 100));
//                                Thread.sleep(5);
//                                break;
//                            case 1:
//                                printLog(devList.get(devLoc).getDevID(), finalLoopId, devLoc, 0, duration, 40);
//                                jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 40));
//                                Thread.sleep(5);
//                                // 第奇数个设备
//                                break;
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            default:
//                // 第 n 轮喷射
//                switch (finalLoopId % 2) {
//                    case 0:
//                        // 第偶数次喷射
//                        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
//                            try {
//                                switch (devLoc % 2) {
//                                    case 0:
//                                        // 第偶数个设备
//                                        printLog(devList.get(devLoc).getDevID(), finalLoopId, devLoc, gap, duration, 100);
//                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 100));
//                                        Thread.sleep(5);
//                                        break;
//                                    case 1:
//                                        // 第奇数个设备
//                                        printLog(devList.get(devLoc).getDevID(), finalLoopId, devLoc, gap, duration, 40);
//                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 40));
//                                        Thread.sleep(5);
//                                        break;
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        break;
//                    case 1:
//                        // 第奇数次喷射
//                        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
//                            try {
//                                switch (devLoc % 2) {
//                                    case 0:
//                                        // 第偶数个设备
//                                        printLog(devList.get(devLoc).getDevID(), finalLoopId, devLoc, gap, duration, 40);
//                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 40));
//                                        Thread.sleep(5);
//                                        break;
//                                    case 1:
//                                        printLog(devList.get(devLoc).getDevID(), finalLoopId, devLoc, gap, duration, 100);
//                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 100));
//                                        Thread.sleep(5);
//                                        // 第奇数个设备
//                                        break;
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        break;
//                }
//                break;
//        }
    }

    private static void onlyOneInterval(List<ConnDevInfo> devList, int duration) {
        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
            try {
                switch (devLoc % 2) {
                    case 0:
                        // 第偶数个设备
                        printLog(devList.get(devLoc).getDevID(), 0, devLoc, 0, duration, 100);
                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 100));
                        Thread.sleep(10);
                        break;
                    case 1:
                        printLog(devList.get(devLoc).getDevID(), 0, devLoc, 0, duration, 40);
                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 40));
                        Thread.sleep(10);
                        // 第奇数个设备
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 齐喷
     */
    public static void jetTogether(List<ConnDevInfo> devList, int gap, int duration, int high) {
        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
            try {
                jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, high));
//                Log.i("TTJET", "第 " + (devLoc + 1) + " 台设备喷射 " + duration / 10 + " 秒、高度 " + high);
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始喷射
     */
    private static void jetStart(long deviceId, byte[] pkg) {
        mDevCtrl.sendCommand(deviceId, pkg);
    }

    private static void printLog(long devID, int loopId, int devLocation, int gap, int duration, int high) {
        Log.i("TTJET", devID + "第 " + loopId + " 次喷射" +
                "\n第 " + (devLocation + 1) + " 台设备喷射 " + duration / 10 + " 秒、间隔 " + gap / 1000 + " 秒、高度 " + high
        );
    }
}
