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
    private static AppActivity.FireworkDevCtrl mDevCtrl = AppActivity.getFireworksDevCtrl();
//    private static Handler mHandler = new Handler();

    private static int mLoopId, mFrequency;
    private static int mFirstDelay, mOtherDelay;
    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mLoopId == 0) {
                        Log.e("TTTTTT", "第 " + (mLoopId + 1) + " 轮喷射，延时 " + mFirstDelay + " 秒");
                    } else {
                        Log.e("TTTTTT", "第 " + (mLoopId + 1) + " 轮喷射，延时 " + mOtherDelay + " 秒");
                    }
                    mLoopId++;
                    mHandler.sendEmptyMessage(2);
                    break;
                case 2:
                    if (mLoopId < mFrequency) {
                        if (mLoopId == 0) {
                            Log.e("TTTTTT", "第 " + (mLoopId + 1) + " 轮喷射，延时 " + 0 + " 秒");
                            mHandler.sendEmptyMessageDelayed(1, mFirstDelay * 1000);
                        } else {
                            mHandler.sendEmptyMessageDelayed(1, mOtherDelay * 1000);
                        }
                    } else {
                        mHandler.removeMessages(1);
                    }
                    break;
                default:
                    break;
            }
        }
    };

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
    public static void jetInterval(List<ConnDevInfo> devList, int gap, int duration, int frequency) {
        // 如果次数为 1 次，不换方向，喷一轮
        if (frequency == 0) {
            onlyOneInterval(devList, duration);
        } else {
            mLoopId = 0;
            // 首轮延迟时间（单位：秒）
            mFirstDelay = duration / 10;
            // 其它轮延迟时间（单位：秒）
            mOtherDelay = mFirstDelay + gap / 1000;
            // 喷射次数
            mFrequency = frequency;
            mHandler.sendEmptyMessage(2);
        }

//        for (int loopId = 0; loopId < frequency; loopId++) {
////            final int delayTime;
////            // 首轮喷射时间
////            int firstJetTime = duration / 10;
////            // 其它轮喷射时间
////            int otherJetTime = (duration / 10 + gap / 1000) + duration / 10;
////            switch (loopId) {
////                case 0:
////                    // 首轮喷射，延时 0 秒
////                    delayTime = 0;
////                    break;
////                default:
////                    // 第一轮喷射开始，延时 [首轮时间 + 其它轮时间 * (次数 - 1)] 秒
////                    delayTime = firstJetTime + otherJetTime * (loopId - 1);
////                    break;
////            }
////
////            final int finalLoopId = loopId;
////            mHandler.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    Log.e("ljmsx", "第 " + (finalLoopId + 1) + " 轮喷射，延时 " + delayTime + " 秒");
////                }
////            }, delayTime * 1000);
//
//            // 第 1 轮喷射
//            if (loopId == 0) {
//                for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
////                    try {
////                        switch (devLoc % 2) {
////                            case 0:
////                                // 第偶数个设备
////                                printLog(devList.get(devLoc).getDevID(), loopId, devLoc, 0, duration, 100);
////                                jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 100));
////                                Thread.sleep(5);
////                                break;
////                            case 1:
////                                printLog(devList.get(devLoc).getDevID(), loopId, devLoc, 0, duration, 40);
////                                jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 40));
////                                Thread.sleep(5);
////                                // 第奇数个设备
////                                break;
////                        }
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
//                }
//            }
//            // 第 n 轮喷射
//            if (loopId > 0) {
////                switch (loopId % 2) {
////                    case 0:
////                        // 第偶数次喷射
////                        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
////                            try {
////                                switch (devLoc % 2) {
////                                    case 0:
////                                        // 第偶数个设备
////                                        printLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 100);
////                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 100));
////                                        Thread.sleep(5);
////                                        break;
////                                    case 1:
////                                        // 第奇数个设备
////                                        printLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 40);
////                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 40));
////                                        Thread.sleep(5);
////                                        break;
////                                }
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                        break;
////                    case 1:
////                        // 第奇数次喷射
////                        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
////                            try {
////                                switch (devLoc % 2) {
////                                    case 0:
////                                        // 第偶数个设备
////                                        printLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 40);
////                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 40));
////                                        Thread.sleep(5);
////                                        break;
////                                    case 1:
////                                        printLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 100);
////                                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), gap, duration, 100));
////                                        Thread.sleep(5);
////                                        // 第奇数个设备
////                                        break;
////                                }
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                        break;
////                }
//            }
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
                        Thread.sleep(5);
                        break;
                    case 1:
                        printLog(devList.get(devLoc).getDevID(), 0, devLoc, 0, duration, 40);
                        jetStart(devList.get(devLoc).getDevID(), BleDeviceProtocol.pkgJetStart(devList.get(devLoc).getDevID(), 0, duration, 40));
                        Thread.sleep(5);
                        // 第奇数个设备
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void manyInterval() {
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
        Log.i("TTJET", devID + "第 " + (loopId + 1) + " 次喷射" +
                "\n第 " + (devLocation + 1) + " 台设备喷射 " + duration / 10 + " 秒、间隔 " + gap / 1000 + " 秒、高度 " + high
        );
    }
}
