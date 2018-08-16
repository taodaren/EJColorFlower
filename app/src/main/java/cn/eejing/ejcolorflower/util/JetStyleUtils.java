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
    private static final int WHAT_TOGETHER = 4;
    private static AppActivity.FireworkDevCtrl mDevCtrl = AppActivity.getFireworksDevCtrl();

    private static List<ConnDevInfo> mDevList;
    private static int mLoopId, mGap, mDuration, mFrequency, mHigh;
    private static boolean mIsStarStream, mIsStarRide, mIsStarInterval, mIsStarTogether;

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_INTERVAL:
                    if (mFrequency == 0) {
                        onlyOneInterval(mDevList, mDuration);
                    } else {
                        moreInterval();
                    }
                    break;
                case WHAT_TOGETHER:
                    for (ConnDevInfo bean : mDevList) {
                        if (mIsStarTogether) {
                            jetStop(bean.getDevID(), BleDeviceProtocol.pkgJetStop(bean.getDevID()));
                            sleepFive();
                        } else {
                            jetStart(bean.getDevID(), BleDeviceProtocol.pkgJetStart(bean.getDevID(), mGap, mDuration, mHigh));
                            sleepFive();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private static void onlyOneInterval(List<ConnDevInfo> devList, int duration) {
        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
            switch (devLoc % 2) {
                case 0:
                    // 第偶数个设备
                    printLog(devList.get(devLoc).getDevID(), 0, devLoc, 0, duration, 100);
                    jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 100));
                    sleepFive();
                    break;
                case 1:
                    printLog(devList.get(devLoc).getDevID(), 0, devLoc, 0, duration, 40);
                    jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 40));
                    sleepFive();
                    // 第奇数个设备
                    break;
            }
        }
    }

    private static void moreInterval() {
        mLoopId = 0;
        if (mLoopId < mFrequency) {
            long delay;
            if (mLoopId == 0) {
                delay = 0;
                mHandler.postDelayed(mRunIntervalFirst, delay);
            }
            if (mLoopId > 0) {
                if (mLoopId == 1) {
                    delay = mGap;
                } else {
                    delay = (mDuration / 10 + mGap / 1000) * 1000;
                }
                mHandler.postDelayed(mRunIntervalOther, delay);
            }
        } else {
            mHandler.removeMessages(WHAT_INTERVAL);
        }
    }

    private static Runnable mRunIntervalFirst = new Runnable() {
        @Override
        public void run() {
            Log.e("TTJET", "第 " + mLoopId + " 轮喷射开始");
            firstInterval();
            Log.i("TTJET", "第 " + mLoopId + " 轮喷射结束，延时 " + 0 + " 秒");
            mLoopId++;
            mHandler.sendEmptyMessage(WHAT_INTERVAL);
        }
    };

    private static Runnable mRunIntervalOther = new Runnable() {
        @Override
        public void run() {
            Log.e("TTJET", "第 " + mLoopId + " 轮喷射开始");
            otherInterval();
            Log.i("TTJET", "第 " + mLoopId + " 轮喷射结束，延时 " + mGap / 1000 + " 秒");
            mLoopId++;
            mHandler.sendEmptyMessage(WHAT_INTERVAL);
        }
    };

    private static void firstInterval() {
        for (int devLoc = 0; devLoc < mDevList.size(); devLoc++) {
            switch (devLoc % 2) {
                case 0:
                    // 第偶数个设备
                    printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, 0, mDuration, 100);
                    jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), 0, mDuration, 100));
                    sleepFive();
                    break;
                case 1:
                    printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, 0, mDuration, 40);
                    jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), 0, mDuration, 40));
                    sleepFive();
                    // 第奇数个设备
                    break;
            }
        }
    }

    private static void otherInterval() {
        switch (mLoopId % 2) {
            case 0:
                // 第偶数次喷射
                for (int devLoc = 0; devLoc < mDevList.size(); devLoc++) {
                    switch (devLoc % 2) {
                        case 0:
                            // 第偶数个设备
                            printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 100);
                            jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 100));
                            sleepFive();
                            break;
                        case 1:
                            // 第奇数个设备
                            printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 40);
                            jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 40));
                            sleepFive();
                            break;
                    }
                }
                break;
            case 1:
                // 第奇数次喷射
                for (int devLoc = 0; devLoc < mDevList.size(); devLoc++) {
                    switch (devLoc % 2) {
                        case 0:
                            // 第偶数个设备
                            printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 40);
                            jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 40));
                            sleepFive();
                            break;
                        case 1:
                            printLog(mDevList.get(devLoc).getDevID(), mLoopId, devLoc, mGap, mDuration, 100);
                            jetStart(mDevList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(mDevList.get(devLoc).getDevID(), mGap, mDuration, 100));
                            sleepFive();
                            // 第奇数个设备
                            break;
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
                // 如果是首次喷射，直接发送喷射命令
                jetStart(devID, pkgStream);
                // 为了多台设备一起喷射，5毫秒人实际感受不到
                sleepFive();
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
                            sleepFive();
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
        mHandler.sendEmptyMessage(WHAT_INTERVAL);
    }

    /**
     * 齐喷
     */
    public static void jetTogether(List<ConnDevInfo> devList, int gap, int duration, int high, boolean isStarTogether) {
        mIsStarTogether = isStarTogether;
        mDevList = devList;
        mGap = gap;
        mDuration = duration;
        mHigh = high;
        mHandler.sendEmptyMessage(WHAT_TOGETHER);
    }

    /** 开始喷射 */
    private static void jetStart(long deviceId, byte[] pkg) {
        mDevCtrl.sendCommand(deviceId, pkg);
    }

    /** 停止喷射 */
    private static void jetStop(long deviceId, byte[] pkg) {
        mDevCtrl.sendCommand(deviceId, pkg);
    }

    /** 睡 5 秒，为了多台设备同时执行效果 */
    private static void sleepFive() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printLog(long devID, int loopId, int devLocation, int gap, int duration, int high) {
        Log.i("TTJET", devID + "第 " + loopId + " 次喷射" +
                "\n第 " + (devLocation + 1) + " 台设备喷射 " + duration / 10 + " 秒、间隔 " + gap / 1000 + " 秒、高度 " + high
        );
    }
}
