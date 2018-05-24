package cn.eejing.ejcolorflower.app;

import android.os.ParcelUuid;

import java.util.UUID;

/**
 * @创建者 Taodaren
 * @描述 全局常量
 */

public class AppConstant {
    public static final String TAG = "taodaren";
    public static final ParcelUuid UUID_GATT_SERVICE
            = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_GATT_CHARACTERISTIC_WRITE
            = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final int ACK_TIMEOUT = 1000;

}
