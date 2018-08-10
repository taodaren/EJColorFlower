package cn.eejing.ejcolorflower.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
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
    public static void jetInterval(long devID, int devLocation, int duration, int frequency, int high) {
        byte[] pkgInterval;
        try {
            if (devLocation % 2 == 0) {
                // 如果设备是第偶数个，高度100
                pkgInterval = BleDeviceProtocol.pkgJetStart(devID, 0, duration, high);
                frequencyInterval(devID, pkgInterval, frequency);
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (devLocation % 2 == 1) {
            // 如果设备是第奇数个，高度60
            pkgInterval = BleDeviceProtocol.pkgJetStart(devID, 0, duration, 60);
            frequencyInterval(devID, pkgInterval, frequency);
        }
    }

    /**
     * 齐喷
     */
    public static void jetTogether(long devID, int gap, int duration, int high) {
        Log.i("SWITCH_CTRL", "TOGETHER_喷射时间: " + duration);
        Log.i("SWITCH_CTRL", "TOGETHER_高度: " + high);

        byte[] pkgTogether = BleDeviceProtocol.pkgJetStart(devID, gap, duration, high);
        try {
            jetStart(devID, pkgTogether);
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始喷射
     */
    private static void jetStart(long deviceId, byte[] pkg) {
        mDevCtrl.sendCommand(deviceId, pkg, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
                Log.i("JET", "喷射ACK--->" + pkg.length + "===" + pkg);
                int jet = BleDeviceProtocol.parseStartJet(pkg, pkg.length);
                Log.i("JET", "喷射解析--->" + jet);
            }

            @Override
            public void timeout() {
                Log.i("JET", "解析超时");
            }
        });
    }

    /**
     * 间隔高低次数判断
     */
    private static void frequencyInterval(long devID, byte[] pkgInterval, int frequency) {
        if (frequency == 0) {
            try {
                jetStart(devID, pkgInterval);
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (frequency > 0) {

        }
    }
}
