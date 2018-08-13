package cn.eejing.ejcolorflower.util;

import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.ConnDevInfo;
import cn.eejing.ejcolorflower.view.activity.AppActivity;

/**
 * 喷射配置工具类
 */

public class JetStyleUtils {
    private static AppActivity.FireworkDevCtrl mDevCtrl = AppActivity.getFireworksDevCtrl();

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
    public static void jetInterval(long devID, int devLocation, int gap, int duration, int frequency) {
        for (int loopId = 0; loopId < frequency; loopId++) {
            if (loopId == 0) {
                // 如果 loopId 为 0 ，证明是第一次，间隔时间无效，直接喷射
                switch (devLocation % 2) {
                    case 0:
                        // 如果设备是第偶数个，高度 100
//                        jetStart(devID, BleDeviceProtocol.pkgJetStart(devID, gap, duration, 100));
                        intervalLog(devID, loopId, devLocation, 0, duration, 100);
                        break;
                    case 1:
                        // 如果设备是第奇数个，高度 60
//                        jetStart(devID, BleDeviceProtocol.pkgJetStart(devID, gap, duration, 60));
                        intervalLog(devID, loopId, devLocation, 0, duration, 60);
                        break;
                }
            }
            if (loopId > 0) {
                // 否则大于 0 ，证明不是第一次，间隔时间生效，带延迟喷射
                if (loopId % 2 == 0) {
                    // 如果是第偶数次喷射
                    switch (devLocation % 2) {
                        case 0:
                            // 如果设备是第偶数个，高度 100
//                        intervalJetEven(devID, gap, duration, 100);
                            intervalLog(devID, loopId, devLocation, gap, duration, 100);
                            break;
                        case 1:
                            // 如果设备是第奇数个，高度 60
//                        intervalJetOdd(devID, gap, duration, 60);
                            intervalLog(devID, loopId, devLocation, gap, duration, 60);
                            break;
                    }
                } else if (loopId % 2 == 1) {
                    // 如果是第奇数次喷射
                    switch (devLocation % 2) {
                        case 0:
                            // 如果设备是第偶数个，高度 60
//                        intervalJetEven(devID, gap, duration, 60);
                            intervalLog(devID, loopId, devLocation, gap, duration, 60);
                            break;
                        case 1:
                            // 如果设备是第奇数个，高度 100
//                        intervalJetOdd(devID, gap, duration, 100);
                            intervalLog(devID, loopId, devLocation, gap, duration, 100);
                            break;
                    }
                }
            }
        }
    }

    public static void jetInt(List<ConnDevInfo> devList, int gap, int duration, int frequency) {
        for (int devLoc = 0; devLoc < devList.size(); devLoc++) {
            switch (devLoc % 2) {
                case 0:
                    // 第偶数个设备
                    for (int loopId = 0; loopId < frequency; loopId++) {
                        if (loopId == 0) {
                            // 第 1 次喷射
                            intervalLog(devList.get(devLoc).getDevID(), loopId, devLoc, 0, duration, 100);
                        }
                        if (loopId > 0) {
                            // 第 n 次喷射
                            switch (loopId % 2) {
                                case 0:
                                    // 第偶数次喷射
                                    intervalLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 100);
                                    break;
                                case 1:
                                    // 第奇数次喷射
                                    intervalLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 60);
                                    break;
                            }
                        }
                    }
                    break;
                case 1:
                    // 第奇数个设备
                    for (int loopId = 0; loopId < frequency; loopId++) {
                        if (loopId == 0) {
                            // 第 1 次喷射
                            intervalLog(devList.get(devLoc).getDevID(), loopId, devLoc, 0, duration, 60);
                        }
                        if (loopId > 0) {
                            // 第 n 次喷射
                            switch (loopId % 2) {
                                case 0:
                                    // 第偶数次喷射
                                    intervalLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 60);
                                    break;
                                case 1:
                                    // 第奇数次喷射
                                    intervalLog(devList.get(devLoc).getDevID(), loopId, devLoc, gap, duration, 100);
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    public static void jetI() {
        try {
            jetStart(810361, BleDeviceProtocol.pkgJetStart(810361, 0, 20, 60));
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jetStart(810360, BleDeviceProtocol.pkgJetStart(810360, 2000, 20, 100));

    }

    /**
     * 间隔高低偶数次喷射
     */
    private static void intervalJetEven(long devID, int gap, int duration, int high) {
        try {
            jetStart(devID, BleDeviceProtocol.pkgJetStart(devID, gap, duration, high));
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 间隔高低奇数次喷射
     */
    private static void intervalJetOdd(long devID, int gap, int duration, int high) {
        jetStart(devID, BleDeviceProtocol.pkgJetStart(devID, gap, duration, high));
    }

    private static void intervalLog(long devID, int loopId, int devLocation, int gap, int duration, int high) {
        try {
            Log.i("TTJET", devID + "第 " + (loopId + 1) + " 次喷射" +
                    "\n第 " + (devLocation + 1) + " 台设备喷射 " + duration / 10 + " 秒、间隔 " + gap / 1000 + " 秒、高度 " + high
            );
            jetStart(devID, BleDeviceProtocol.pkgJetStart(devID, gap, duration, high));

            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
}
