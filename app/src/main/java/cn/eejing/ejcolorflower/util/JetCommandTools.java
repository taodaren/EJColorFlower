package cn.eejing.ejcolorflower.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.ConnDevInfo;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.view.activity.MainActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.BLE_RETURN_FAILURE;
import static cn.eejing.ejcolorflower.app.AppConstant.BLE_RETURN_SUCCESS;
import static cn.eejing.ejcolorflower.app.AppConstant.CLEAR_MATERIAL_GROUP;
import static cn.eejing.ejcolorflower.app.AppConstant.CLEAR_MATERIAL_MASTER;
import static cn.eejing.ejcolorflower.app.AppConstant.CTRL_DEV_NUM;
import static cn.eejing.ejcolorflower.app.AppConstant.TAG_BLE_COMMAND;

/**
 * 喷射管理
 */

public class JetCommandTools {
    private static MainActivity.FireworkDevCtrl mDevCtrl = MainActivity.getFireworksDevCtrl();

    /** 开始喷射 */
    public static void jetStart(List<ConnDevInfo> devList, byte[] highs) {
        for (ConnDevInfo bean : devList) {
            int i = devList.indexOf(bean);
            Log.i(TAG_BLE_COMMAND, "高度: " + highs[i]);

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

    /** 停止整组喷射 5 次 */
    public static void jetStopFive(List<ConnDevInfo> devList) {
        for (int i = 0; i < 5; i++) {
            Log.i(TAG_BLE_COMMAND, "stop");
            trySleep(100);
            jetStop(devList);
        }
    }

    /** 清料 */
    public static void clearMaterial(final Context context, List<ConnDevInfo> devList, long devId, int ctrlMode, int startAddress, int devNum, int high) {
        byte[] byHighs = new byte[CTRL_DEV_NUM];
        switch (ctrlMode) {
            case CLEAR_MATERIAL_GROUP:// 分组模式
                for (int i = 0; i < devList.size(); i++) {
                    byHighs[i] = (byte) high;
                }
                for (ConnDevInfo bean : devList) {
                    clearMaterialCommand(context, bean.getDevID(), ctrlMode, startAddress, devNum, byHighs, 5);
                    trySleep(5);
                }
                break;
            case CLEAR_MATERIAL_MASTER:// 主控模式
                for (int i = 0; i < devNum; i++) {
                    byHighs[i] = (byte) high;
                }
                clearMaterialCommand(context, devId, ctrlMode, startAddress, devNum, byHighs, 5);
                break;
            default:
                break;
        }
    }

    @SuppressLint("UseSparseArrays")
    private static Map<Long, String> mClearFailureDevMap = new HashMap<>();

    /** 清料命令 */
    private static void clearMaterialCommand(final Context context, final long devId,
                                             final int mode, final int startAddress, final int devNum, final byte[] highs, final int resendNum) {
        mDevCtrl.sendCommand(devId,
                BleDeviceProtocol.pkgClearMaterial(devId, mode, startAddress, devNum, highs),
                new OnReceivePackage() {
                    @Override
                    public void ack(@NonNull byte[] pkg) {
                        int returnCode = BleDeviceProtocol.parseReturnCode(pkg, pkg.length);
                        switch (returnCode) {
                            case BLE_RETURN_SUCCESS:
                                if (mClearFailureDevMap.size() == devNum) {
                                    // 如果所有设备都清料成功，提示用户
                                    Toast.makeText(context, "清料成功！", Toast.LENGTH_LONG).show();
                                }
                                break;
                            case BLE_RETURN_FAILURE:
                                if (resendNum != 0) {
                                    clearMaterialCommand(context, devId, mode, startAddress, devNum, highs, resendNum - 1);
                                } else {
                                    Toast.makeText(context, devId + "清料失败，请重新清料", Toast.LENGTH_LONG).show();
                                    mClearFailureDevMap.put(devId, "清料失败");
                                }
                                break;
                        }
                    }

                    @Override
                    public void timeout() {
                        Log.e(TAG_BLE_COMMAND, "清料超时！");
                        if (resendNum != 0) {
                            clearMaterialCommand(context, devId, mode, startAddress, devNum, highs, resendNum - 1);
                        } else {
                            Toast.makeText(context, devId + "清料超时，请重新清料", Toast.LENGTH_LONG).show();
                            mClearFailureDevMap.put(devId, "清料失败");
                        }
                    }
                });
    }

    /** 睡多少毫秒，为了多台（组）设备同时执行效果 */
    private static void trySleep(long milliSecond) {
        try {
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
