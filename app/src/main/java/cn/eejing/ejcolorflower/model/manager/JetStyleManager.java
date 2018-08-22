package cn.eejing.ejcolorflower.model.manager;

import android.util.Log;

import java.util.List;

import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.ConnDevInfo;
import cn.eejing.ejcolorflower.view.activity.AppActivity;

/**
 * 喷射配置工具类
 */

public class JetStyleManager {
    private static AppActivity.FireworkDevCtrl mDevCtrl = AppActivity.getFireworksDevCtrl();

    /** 流水灯 */
    public static void jetStream(List<ConnDevInfo> devList, int direction, int gap, int duration, int gapBig, int loop, boolean isStarStream) {
        for (ConnDevInfo bean : devList) {
            if (isStarStream) {
                jetStart(bean.getDevID(), BleDeviceProtocol.pkgJetStart(bean.getDevID(), gap, duration, 100));
                sleepFive();
            } else {
                jetStop(bean.getDevID(), BleDeviceProtocol.pkgJetStop(bean.getDevID()));
                sleepFive();
            }
        }
    }

    /** 跑马灯 */
    public static void jetRide(List<ConnDevInfo> devList, int direction, int gap, int duration, int gapBig, int loop, boolean isStarRide) {
        for (ConnDevInfo bean : devList) {
            if (isStarRide) {
                jetStart(bean.getDevID(), BleDeviceProtocol.pkgJetStart(bean.getDevID(), gap, duration, 100));
                sleepFive();
            } else {
                jetStop(bean.getDevID(), BleDeviceProtocol.pkgJetStop(bean.getDevID()));
                sleepFive();
            }
        }
    }

    /** 间隔高低 */
    public static void jetInterval(List<ConnDevInfo> devList, final int duration, int high, boolean isStarInterval, int loopId, long delay) {
        for (ConnDevInfo bean : devList) {
            if (isStarInterval) {
                Log.i("TTJET", bean.getDevID() + " 第 " + loopId + " 轮喷射：延时 " + delay/1000 + " 秒，喷射 " + duration/10 + " 秒，高度 " + high);
                jetStart(bean.getDevID(), BleDeviceProtocol.pkgJetStart(bean.getDevID(), 0, duration, high));
                sleepFive();
            } else {
                jetStop(bean.getDevID(), BleDeviceProtocol.pkgJetStop(bean.getDevID()));
                sleepFive();
            }
        }
    }

    /** 齐喷 */
    public static void jetTogether(List<ConnDevInfo> devList, int duration, int high, boolean isStarTogether) {
        for (ConnDevInfo bean : devList) {
            if (isStarTogether) {
                jetStart(bean.getDevID(), BleDeviceProtocol.pkgJetStart(bean.getDevID(), 0, duration, high));
                sleepFive();
            } else {
                jetStop(bean.getDevID(), BleDeviceProtocol.pkgJetStop(bean.getDevID()));
                sleepFive();
            }
        }
    }

    /** 开始喷射 */
    private static void jetStart(long deviceId, byte[] pkg) {
        mDevCtrl.sendCommand(deviceId, pkg);
    }

    /** 停止喷射 */
    private static void jetStop(long deviceId, byte[] pkg) {
        mDevCtrl.sendCommand(deviceId, pkg);
    }

    /** 睡 5 毫秒，为了多台设备同时执行效果 */
    public static void sleepFive() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
