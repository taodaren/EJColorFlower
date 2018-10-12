package cn.eejing.ejcolorflower.app;

import android.os.ParcelUuid;

import java.util.UUID;

/**
 * 全局常量
 */

public class AppConstant {
    public static final String TAG = "taodaren";
    public static final String TAG_HARDWARE_DEV = "硬件设备";
    public static final String TAG_BLE_COMMAND = "蓝牙命令";
    public static final String EMPTY = "";

    public static final String APP_ID = "wx6a32217e1e3ae4f4";

    public static final String APP_QR_GET_MID = "二维码扫描获取料包 ID";
    public static final String APP_QR_GET_DID = "二维码扫描获取设备 ID";

    public static final String QR_DEV_ID = "qr_dev_id";
    public static final String QR_DEV_MAC = "qr_dev_mac";
    public static final String QR_MATERIAL_ID = "qr_material_id";

    public static final String PAY_CODE_ALI = "alipay";
    public static final String PAY_CODE_WEI = "weipay";
    public static final String EXIT_LOGIN = "exit_login";
    public static final String ARG_TYPE = "arg_type";

    public static final ParcelUuid UUID_GATT_SERVICE
            = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_GATT_CHARACTERISTIC_WRITE
            = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    // 连接设备喷射缓存区数量
    public static final int CTRL_DEV_NUM = 300;

    // 蓝牙返回（解析）状态
    public static final int BLE_RETURN_SUCCESS = 0;
    public static final int BLE_RETURN_FAILURE = 1;

    // 请求代码 QRCode 权限
    public static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    public static final int TYPE_TEMP = 1;
    public static final int TYPE_DMX = 2;
    public static final int TYPE_TIME = 3;

    // 加料使用状态
    public static final int TYPE_NO_USED = 0;
    public static final int TYPE_WAIT_USED = 1;
    public static final int TYPE_END_USED = 2;

    public static final String DEVICE_CONNECT_YES = "设备已连接";
    public static final String DEVICE_CONNECT_NO = "设备已断开";

    public static final String TYPE_WAIT_SHIP = "待发货";
    public static final String TYPE_WAIT_RECEIPT = "待收货";
    public static final String TYPE_COMPLETE_GOODS = "已完成";

    // 地址选择
    public static final String ADDRESS_PROVINCESS = "address_provincess";
    public static final String ADDRESS_AREAS = "address_areas";
    public static final String ADDRESS_CITYS = "address_citys";
    public static final String ADDRESS_ID_PROVINCESS = "address_id_provincess";
    public static final String ADDRESS_ID_AREAS = "address_id_areas";
    public static final String ADDRESS_ID_CITYS = "address_id_citys";

    // 清料操作模式（1-非主控模式；2-主控模式）
    public static final int CLEAR_MATERIAL_GROUP = 1;
    public static final int CLEAR_MATERIAL_MASTER = 2;

    // 配置设备效果
    public static final String CONFIG_STREAM = "流水灯";
    public static final String CONFIG_RIDE = "跑马灯";
    public static final String CONFIG_INTERVAL = "间隔高低";
    public static final String CONFIG_TOGETHER = "齐喷";
    public static final String CONFIG_DEF = "默认";

    // 配置设备喷射方向
    public static final String LEFT_TO_RIGHT = "1";
    public static final String BORDER_TO_CENTER = "2";
    public static final String RIGHT_TO_LEFT = "3";
    public static final String CENTER_TO_BORDER = "4";

    // 当前时间和循环 ID
    public static final int CURRENT_TIME = 0;
    public static final int LOOP_ID = 0;

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
