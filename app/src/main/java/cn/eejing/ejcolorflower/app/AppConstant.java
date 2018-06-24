package cn.eejing.ejcolorflower.app;

import android.os.ParcelUuid;

import java.util.UUID;

/**
 * 全局常量
 */

public class AppConstant {
    public static final String TAG = "taodaren";

    public static final String PAY_CODE = "alipay";
    public static final String EXIT_LOGIN = "exit_login";
    public static final String ARG_TYPE = "arg_type";

    public static final ParcelUuid UUID_GATT_SERVICE
            = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_GATT_CHARACTERISTIC_WRITE
            = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    public static final int ACK_TIMEOUT = 1000;

    // 请求代码 QRCode 权限
    public static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    public static final int TYPE_TEMP = 1;
    public static final int TYPE_DMX = 2;
    public static final int TYPE_TIME = 3;

    public static final int TYPE_NO_USED = 0;
    public static final int TYPE_WAIT_USED = 1;
    public static final int TYPE_END_USED = 2;

    public static final String TYPE_WAIT_SHIP = "待发货";
    public static final String TYPE_WAIT_RECEIPT = "待收货";
    public static final String TYPE_COMPLETE_GOODS = "已完成";

    // 效果功能（方式）
    public static final String CONFIG_STREAM = "流水灯";
    public static final String CONFIG_RIDE = "跑马灯";
    public static final String CONFIG_INTERVAL= "间隔高低";
    public static final String CONFIG_TOGETHER = "齐喷";

    // 配置设备喷射方向
    public static final String LEFT_TO_RIGHT = "1";
    public static final String BORDER_TO_CENTER = "2";
    public static final String RIGHT_TO_LEFT = "3";
    public static final String CENTER_TO_BORDER = "4";

    // 配置设备喷射默认值
    public static final String DEFAULT_STREAM_RIDE_GAP = "2";
    public static final String DEFAULT_STREAM_RIDE_DURATION = "3";
    public static final String DEFAULT_STREAM_RIDE_GAP_BIG = "5";
    public static final String DEFAULT_STREAM_RIDE_LOOP = "3";
    public static final String DEFAULT_INTERVAL_GAP = "2";
    public static final String DEFAULT_INTERVAL_DURATION = "5";
    public static final String DEFAULT_INTERVAL_FREQUENCY = "3";
    public static final String DEFAULT_TOGETHER_DURATION = "10";
    public static final String DEFAULT_TOGETHER_HIGH = "100";

}
