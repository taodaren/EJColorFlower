package cn.eejing.ejcolorflower.util;

import android.util.Log;

import java.util.List;

import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.ConnDevInfo;
import cn.eejing.ejcolorflower.view.activity.MainActivity;

/**
 * 喷射管理
 */

public class JetCommandTools {
    private static MainActivity.FireworkDevCtrl mDevCtrl = MainActivity.getFireworksDevCtrl();

    /** 开始喷射 */
    public static void jetStart(List<ConnDevInfo> devList, byte[] highs) {
        for (ConnDevInfo bean : devList) {
            int i = devList.indexOf(bean);
            Log.i("MGR_JET", "高度: " + highs[i]);

            if (highs[i] == 0) {
                mDevCtrl.sendCommand(bean.getDevID(), BleDeviceProtocol.pkgJetStop(bean.getDevID()));
                trySleep(5);
            } else {
                mDevCtrl.sendCommand(bean.getDevID(), BleDeviceProtocol.pkgJetStart(bean.getDevID(), 0, 5, highs[i]));
                trySleep(5);
            }
        }
    }

    /** 停止喷射 */
    public static void jetStop(List<ConnDevInfo> devList) {
        for (ConnDevInfo bean : devList) {
            mDevCtrl.sendCommand(bean.getDevID(), BleDeviceProtocol.pkgJetStop(bean.getDevID()));
            trySleep(5);
        }
    }

    /** 停止整租喷射 5 次 */
    public static void jetStopFive(List<ConnDevInfo> devList) {
        for (int i = 0; i < 5; i++) {
            Log.i("CMCML", "stop");
            trySleep(100);
            jetStop(devList);
        }
    }

    /** 睡多少毫秒，为了多台（组）设备同时执行效果 */
    public static void trySleep(long milliSecond) {
        try {
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
