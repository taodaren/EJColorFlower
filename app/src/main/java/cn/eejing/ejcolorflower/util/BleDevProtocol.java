package cn.eejing.ejcolorflower.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import cn.eejing.ejcolorflower.model.device.DeviceConfig;
import cn.eejing.ejcolorflower.model.device.DeviceMaterialStatus;
import cn.eejing.ejcolorflower.model.device.DeviceStatus;

/**
 * 蓝牙设备协议处理
 */

public class BleDevProtocol {
    private final static String TAG = "BleDevProtocol";
    private final static int MAX_PKG_LEN = 0x80;
    private final static int CMD_GET_STATUS = 1;                                // 获取内部状态
    private final static int CMD_GET_CONFIG = 2;                                // 获取配置
    private final static int CMD_SET_TEMP_THRESHOLD = 3;                        // 设置温度阈值
    private final static int CMD_SET_MOTOR_SPEED = 4;                           // 设置电机默认速度
    private final static int CMD_SET_DEV_ID = 5;                                // 设置设备ID
    private final static int CMD_SET_DMX_ADDRESS = 6;                           // 设置DMX地址
    private final static int CMD_JET_START = 7;                                 // 开始喷射
    private final static int CMD_ADD_MATERIAL = 8;                              // 加料
    private final static int CMD_GET_TIMESTAMP = 9;                             // 获取时间戳
    private final static int CMD_JET_STOP = 10;                                 // 停止喷射
    private final static int CMD_SET_SCRAPE_MATERIAL_TIME = 11;                 // 设置刮料时间
    private final static int CMD_GET_ADD_MATERIAL_STATUS = 12;                  // 获取加料状态
    private final static int CMD_CLEAR_ADD_MATERIAL_INFO = 13;                  // 清除加料信息
    private final static int CMD_ENTER_LCD_OPERATING_MODE = 14;                 // 进入液晶屏操作模式
    private final static int CMD_QR_DATA_SEPARATE_PKG = 15;                     // 二维码数据（分包）
    private final static int CMD_ENTER_REAL_TIME_CTRL_MODE = 16;                // 进入在线实时控制模式
    private final static int CMD_CLEAR_MATERIAL = 17;                           // 清料

    private final static int HEADER_LEN = 7;
    private final byte[] pkg = new byte[MAX_PKG_LEN];
    private int mPkgLen;
    private boolean mTranslate;

    /** 接收 */
    public void bleReceive(byte[] data) {
        for (byte b : data) {
            onByte(b);
        }
    }


    /** 转义判断 */
    private void onByte(byte b) {
        if (mTranslate) {
            mTranslate = false;
            onByteLevel2((byte) (((int) b & 0xff) ^ 0xFF), true);
        } else if (b == (byte) 0xFE) {
            mTranslate = true;
        } else {
            onByteLevel2(b, false);
        }
    }

    private int type;                             // 报文加密位置类型
    private int dataLen;                          // 数据长度
    private final byte[] S = new byte[6];         // 随机数长度
    private int niS;                              // 随机数个数
    private final static int[] K = new int[]{     // 密码本
            30, 19, 3, 37, 17, 16, 35, 36, 16, 37, 22, 4, 42, 15, 21, 14, 29, 45, 20, 41, 38, 13,
            12, 5, 5, 40, 8, 1, 34, 7, 8, 2, 19, 13, 31, 27, 0, 20, 28, 24, 38, 36, 23, 10, 1, 14,
            43, 33, 13, 16, 15, 3, 37, 4, 25, 6, 40, 12, 5, 42, 25, 3, 31, 29, 18, 25, 39, 30, 24,
            47, 11, 47, 34, 22, 46, 8, 44, 39, 44, 21, 27, 9, 15, 2, 11, 28, 6, 19, 41, 21, 46, 45,
            39, 44, 31, 23, 18, 34, 33, 9, 24, 36, 23, 30, 17, 41, 22, 26, 9, 32, 6, 4, 26, 14, 18,
            40, 38, 11, 17, 12, 7, 35, 32, 27, 32, 2, 10, 0, 20, 0, 35, 45, 26, 1, 47, 33, 28, 46,
            42, 10, 43, 43, 29, 7
    };

    private static byte map(byte[] S, int i, byte d) {
        int m = 0;
        for (int j = 0; j < 8; j++) {
            int k = K[(i * 8 + j) % K.length];
            m |= ((((int) S[k >> 3] & (1 << (k & 7))) != 0) ? 1 : 0) << j;
        }
        return (byte) (((int) d & 0xff) ^ m);
    }

    private byte map(int i, byte d) {
        return map(S, i, d);
    }

    /** 二级转义判断 */
    private void onByteLevel2(byte b, boolean isTranslated) {
        if (b == (byte) 0xDC && !isTranslated) {
            type = 1;
        } else if (type == 1) {
            dataLen = (int) b & 0xff;
            if (dataLen > MAX_PKG_LEN) {
                type = 0;
            } else {
                niS = 0;
                type = 2;
            }
        } else if (type == 2) {
            S[niS] = b;
            niS++;
            if (niS >= S.length) {
                type = 3;
                mPkgLen = 0;
            }
        } else if (type == 3) {
            pkg[mPkgLen] = map(mPkgLen, b);
            mPkgLen++;
            if (mPkgLen >= dataLen) {
                type = 0;
                validatePkg();
            }
        }
    }

    /** 验证 PKG */
    private void validatePkg() {
        if (mPkgLen >= 9 && pkg[0] == (byte) 0xCD) {
            int dataLen = (int) pkg[1] & 0xff;
            if (dataLen + 9 == mPkgLen) {
                pkgLengthValidated();
                return;
            }
        }
        LogUtil.e(TAG, "格式错误的 PKG--->" + Util.hex(pkg, mPkgLen));
    }

    public byte[] getPkg() {
        return Arrays.copyOfRange(pkg, 0, mPkgLen);
    }

    /** PKG 长度已验证 */
    private void pkgLengthValidated() {
        if (CRC16.validate(pkg, mPkgLen - 2)) {
            if ((pkg[2] & 0x80) != 0) {
                LogUtil.i(TAG, "ack package " + Util.hex(pkg, mPkgLen));
                ackPkg();
            } else {
                LogUtil.e(TAG, "drop command package " + Util.hex(pkg, mPkgLen));
            }
        } else {
            LogUtil.e(TAG, "CRC wrong package " + Util.hex(pkg, mPkgLen));
        }
    }

    private void ackPkg() {
        int cmd = pkg[2] & 0x7F;
//        long id = parseID(pkg, mPkgLen);
        switch (cmd) {
            case CMD_GET_STATUS:
                DeviceStatus ds = parseStatus(pkg, mPkgLen);
                if (ds != null) {
                    onReceivePkg(ds);
                }
                break;
            case CMD_GET_CONFIG:
                DeviceConfig config = parseConfig(pkg, mPkgLen);
                if (config != null) {
                    onReceivePkg(config);
                }
                break;
            default:
                onReceivePkg(pkg, mPkgLen);
                break;
        }
    }

    protected void onReceivePkg(@NonNull DeviceStatus status) {
    }

    protected void onReceivePkg(@NonNull DeviceConfig config) {
    }

    protected void onReceivePkg(@NonNull byte[] pkg, int pkgLen) {
        LogUtil.e(TAG, "receive package " + Util.hex(pkg, pkgLen));
    }

    /** 是否匹配 */
    public static boolean isMatch(@NonNull byte[] cmd_pkg, @NonNull byte[] ack_pkg) {
        return (cmd_pkg[2] | (byte) 0x80) == ack_pkg[2] &&
                ((cmd_pkg[3] == 0 && cmd_pkg[4] == 0 && cmd_pkg[5] == 0 && cmd_pkg[6] == 0) ||
                        (cmd_pkg[3] == ack_pkg[3] && cmd_pkg[4] == ack_pkg[4] &&
                                cmd_pkg[5] == ack_pkg[5] && cmd_pkg[6] == ack_pkg[6]));
    }

    /** 命令包 */
    @NonNull
    private static byte[] cmdPkg(int cmd, long id, @Nullable byte[] data) {
        final int dataLen = (data == null) ? 0 : data.length;
        final byte[] pkg = new byte[9 + dataLen];
        pkg[0] = (byte) 0xCD;
        pkg[1] = (byte) dataLen;
        pkg[2] = (byte) cmd;
        pkg[3] = (byte) (id & 0xff);
        pkg[4] = (byte) ((id >> 8) & 0xff);
        pkg[5] = (byte) ((id >> 16) & 0xff);
        pkg[6] = (byte) ((id >> 24) & 0xff);
        if (data != null) {
            System.arraycopy(data, 0, pkg, 7, dataLen);
        }
        int crc = CRC16.calculate(pkg, 7 + dataLen);
        pkg[7 + dataLen] = (byte) (crc & 0xff);
        pkg[8 + dataLen] = (byte) ((crc >> 8) & 0xff);

        LogUtil.i(TAG, "CMD PKG--->" + Util.hex(pkg, pkg.length));
        return pkg;
    }

    // 提供加密强随机数生成器
    private static final SecureRandom seedGen = new SecureRandom();

    /** 加密包 */
    @NonNull
    private static byte[] encryptPkg(@NonNull byte[] d) {
        final byte[] pkg = new byte[8 + d.length];
        final byte[] S = new byte[6];
        seedGen.nextBytes(S);
        pkg[0] = (byte) 0xDC;
        pkg[1] = (byte) d.length;
        System.arraycopy(S, 0, pkg, 2, S.length);
        for (int i = 0; i < d.length; i++) {
            pkg[8 + i] = map(S, i, d[i]);
        }
        return pkg;
    }

    private final static byte TRANSLATE_LEAD = (byte) 0xFE;

    @NonNull
    private static byte[] translate(@NonNull byte[] d) {
        int c = 0;
        for (int i = 1; i < d.length; i++) {
            if (shouldTranslate(d[i])) {
                c++;
            }
        }

        if (c > 0) {
            final byte[] pkg = new byte[d.length + c];
            pkg[0] = d[0];
            int j = 1;
            for (int i = 1; i < d.length; i++) {
                if (shouldTranslate(d[i])) {
                    pkg[j++] = TRANSLATE_LEAD;
                    pkg[j++] = (byte) ((int) d[i] ^ 0xff);
                } else {
                    pkg[j++] = d[i];
                }
            }
            return pkg;
        } else {
            return d;
        }
    }

    private static boolean shouldTranslate(byte b) {
        return b == (byte) 0xCD ||
                b == (byte) 0xDC ||
                b == TRANSLATE_LEAD;
    }

    @NonNull
    public static byte[] wrappedPackage(@NonNull byte[] data) {
        return translate(encryptPkg(data));
    }

    /** 获取内部状态 */
    @NonNull
    public static byte[] pkgGetStatus(long id) {
        return cmdPkg(CMD_GET_STATUS, id, null);
    }

    /** 获取配置 */
    @NonNull
    public static byte[] pkgGetConfig(long id) {
        return cmdPkg(CMD_GET_CONFIG, id, null);
    }

    /** 设置温度阈值 */
    @NonNull
    public static byte[] pkgSetTempThreshold(long devId, int low, int height) {
        byte[] data = new byte[4];

        data[0] = (byte) (low & 0xff);
        data[1] = (byte) ((low >> 8) & 0xff);
        data[2] = (byte) (height & 0xff);
        data[3] = (byte) ((height >> 8) & 0xff);

        return cmdPkg(CMD_SET_TEMP_THRESHOLD, devId, data);
    }

    /** 设置电机默认速度 */
    @NonNull
    public static byte[] pkgSetMotorDefSpeed(long devId, int[] speeds) {
        byte[] data = new byte[4];

        data[0] = (byte) (speeds[0] & 0xff);
        data[1] = (byte) (speeds[1] & 0xff);
        data[2] = (byte) (speeds[2] & 0xff);
        data[3] = (byte) (speeds[3] & 0xff);

        return cmdPkg(CMD_SET_MOTOR_SPEED, devId, data);
    }

    /** 设置设备 ID */
    @NonNull
    public static byte[] pkgSetDevId(long devId, long newId) {
        byte[] data = new byte[4];

        data[0] = (byte) (newId & 0xff);
        data[1] = (byte) ((newId >> 8) & 0xff);
        data[2] = (byte) ((newId >> 16) & 0xff);
        data[3] = (byte) ((newId >> 24) & 0xff);

        return cmdPkg(CMD_SET_DEV_ID, devId, data);
    }

    /** 设置 DMX 地址 */
    @NonNull
    public static byte[] pkgSetDmxAddress(long devId, int dmxAddress) {
        byte[] data = new byte[2];

        data[0] = (byte) (dmxAddress & 0xff);
        data[1] = (byte) ((dmxAddress >> 8) & 0xff);

        return cmdPkg(CMD_SET_DMX_ADDRESS, devId, data);
    }

    /** 开始喷射 */
    @NonNull
    public static byte[] pkgJetStart(long devId, int gap, int duration, int high) {
        byte[] data = new byte[5];

        data[0] = (byte) (gap & 0xff);
        data[1] = (byte) ((gap >> 8) & 0xff);

        data[2] = (byte) (duration & 0xff);
        data[3] = (byte) ((duration >> 8) & 0xff);

        data[4] = (byte) (high & 0xff);

        return cmdPkg(CMD_JET_START, devId, data);
    }

    /** 加料 */
    @NonNull
    public static byte[] pkgAddMaterial(long devId, int time, long timestamp, long userId, long materialId) {
        byte[] data = new byte[16];

        data[0] = (byte) (time & 0xff);
        data[1] = (byte) ((time >> 8) & 0xff);

        data[2] = (byte) (timestamp & 0xff);
        data[3] = (byte) ((timestamp >> 8) & 0xff);
        data[4] = (byte) ((timestamp >> 16) & 0xff);
        data[5] = (byte) ((timestamp >> 24) & 0xff);

        data[6] = (byte) (userId & 0xff);
        data[7] = (byte) ((userId >> 8) & 0xff);
        data[8] = (byte) ((userId >> 16) & 0xff);
        data[9] = (byte) ((userId >> 24) & 0xff);


        data[10] = (byte) (materialId & 0xff);
        data[11] = (byte) ((materialId >> 8) & 0xff);
        data[12] = (byte) ((materialId >> 16) & 0xff);
        data[13] = (byte) ((materialId >> 24) & 0xff);
        data[14] = (byte) ((materialId >> 32) & 0xff);
        data[15] = (byte) ((materialId >> 40) & 0xff);

        return cmdPkg(CMD_ADD_MATERIAL, devId, data);
    }

    /** 获取时间戳 */
    @NonNull
    public static byte[] pkgGetTimestamp(long devId) {
        return cmdPkg(CMD_GET_TIMESTAMP, devId, null);
    }

    /** 停止喷射 */
    @NonNull
    public static byte[] pkgJetStop(long devId) {
        return cmdPkg(CMD_JET_STOP, devId, null);
    }

    /** 设置刮料时间 */
    @NonNull
    public static byte[] pkgSetScrapeMaterialTime(long devId, int time) {
        byte[] data = new byte[1];

        data[0] = (byte) (time & 0xff);

        return cmdPkg(CMD_SET_SCRAPE_MATERIAL_TIME, devId, data);
    }

    /** 获取加料状态 */
    @NonNull
    public static byte[] pkgGetAddMaterialStatus(long devId) {
        return cmdPkg(CMD_GET_ADD_MATERIAL_STATUS, devId, null);
    }

    /** 清除加料信息 */
    @NonNull
    public static byte[] pkgClearAddMaterialInfo(long devId, long userId, long materialId) {
        byte[] data = new byte[10];

        data[0] = (byte) (userId & 0xff);
        data[1] = (byte) ((userId >> 8) & 0xff);
        data[2] = (byte) ((userId >> 16) & 0xff);
        data[3] = (byte) ((userId >> 24) & 0xff);

        data[4] = (byte) (materialId & 0xff);
        data[5] = (byte) ((materialId >> 8) & 0xff);
        data[6] = (byte) ((materialId >> 16) & 0xff);
        data[7] = (byte) ((materialId >> 24) & 0xff);
        data[8] = (byte) ((materialId >> 32) & 0xff);
        data[9] = (byte) ((materialId >> 40) & 0xff);

        return cmdPkg(CMD_CLEAR_ADD_MATERIAL_INFO, devId, data);
    }

    /** 进入液晶屏操作模式 */
    @NonNull
    public static byte[] pkgEnterLcdOperatingMode(long devId, int offset, long db) {
        byte[] data = new byte[34];

        data[0] = (byte) (offset & 0xff);
        data[1] = (byte) ((offset >> 8) & 0xff);

        data[2] = (byte) (db & 0xff);
        for (int i = 0; i < 32; i++) {
            data[i + 3] = (byte) ((db >> (8 * (i + 1))) & 0xff);
        }

        return cmdPkg(CMD_ENTER_LCD_OPERATING_MODE, devId, data);
    }

    /** 二维码数据（分包） */
    @NonNull
    public static byte[] pkgQrDataSeparatePkg(long devId) {
        byte[] data = new byte[0];
        return cmdPkg(CMD_QR_DATA_SEPARATE_PKG, devId, data);
    }

    /** 进入在线实时控制模式 */
    @NonNull
    public static byte[] pkgEnterRealTimeCtrlMode(long devId, int startAddress, int devNum, byte[] high) {
        byte[] data = new byte[2 + devNum];

        data[0] = (byte) (startAddress & 0xff);
        data[1] = (byte) (devNum & 0xff);

        for (int i = 0; i < devNum; i++) {
            data[2 + i] = high[i];
        }
        return cmdPkg(CMD_ENTER_REAL_TIME_CTRL_MODE, devId, data);
    }

    /** 清料 */
    @NonNull
    public static byte[] pkgClearMaterial(long devId, int mode, int startAddress, int devNum, byte[] high) {
        byte[] data = new byte[3 + devNum];

//        data[0] = (byte) (0x55);                   // 0x55 表示退出
        data[0] = (byte) (mode & 0xff);            // 操作模式：1-非主控模式，2-主控模式
        data[1] = (byte) (startAddress & 0xff);    // DMX 起始地址 +1
        data[2] = (byte) (devNum & 0xff);          // 带主机数量

        // 主机在前，从机低到高顺序低速喷射，统一发20
        for (int i = 0; i < devNum; i++) {
            data[3 + i] = high[i];
        }

        return cmdPkg(CMD_CLEAR_MATERIAL, devId, data);
    }

    /** 解析内部状态 */
    private static DeviceStatus parseStatus(@NonNull byte[] pkg, int pkgLen) {
        DeviceStatus ds = new DeviceStatus();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            ds.setTemperature(reader.readSignedShortLSB());
            ds.setSupplyVoltage((float) reader.readUnsignedChar() / 10);
            ds.setMotorSpeed1(reader.readUnsignedChar());
            ds.setMotorSpeed2(reader.readUnsignedChar());
            ds.setMotorSpeed3(reader.readUnsignedChar());
            ds.setMotorSpeed4(reader.readUnsignedChar());
            ds.setPitch(reader.readUnsignedChar());
            ds.setUltrasonicDistance(reader.readUnsignedChar());
            ds.setInfraredDistance(reader.readUnsignedChar());
            ds.setRestTime(reader.readUnsignedShortLSB());
            return ds;
        } catch (IOException e) {
            return null;
        }
    }

    /** 解析配置 */
    private static DeviceConfig parseConfig(@NonNull byte[] pkg, int pkgLen) {
        DeviceConfig config = new DeviceConfig();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            config.setID(reader.readUnsignedIntLSB());
            config.setFeedDefVelocity(reader.readUnsignedChar());
            config.setScrapeDefVelocity(reader.readUnsignedChar());
            config.setWindDefVelocity(reader.readUnsignedChar());
            config.setBackupDefVelocity(reader.readUnsignedChar());
            config.setTemperatureThresholdLow(reader.readSignedShortLSB());
            config.setTemperatureThresholdHigh(reader.readSignedShortLSB());
            config.setDMXAddress(reader.readUnsignedShortLSB());
            config.setGualiaoTime(reader.readUnsignedChar());
            return config;
        } catch (IOException e) {
            return null;
        }
    }

    /** 解析设备 ID */
    public static long parseSetDevID(@NonNull byte[] pkg, int pkgLen) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(3);
            return reader.readUnsignedIntLSB();
        } catch (IOException e) {
            return 0;
        }
    }

    /** 解析配置 DMX 地址 */
    public static int parseSetDmxAddr(@NonNull byte[] pkg, int pkgLen) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** 解析喷射 */
    public static int parseStartJet(@NonNull byte[] pkg, int pkgLen) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            return -1;
        }
    }

    /** 解析加料 */
    public static int parseAddMaterial(@NonNull byte[] pkg, int pkgLen) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            return -1;
        }
    }

    /** 解析获取时间戳 */
    public static long parseGetTimestamp(@NonNull byte[] pkg, int pkgLen) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedIntLSB();
        } catch (IOException e) {
            return -1;
        }
    }

    /** 解析加料状态 */
    public static DeviceMaterialStatus parseAddMaterialStatus(@NonNull byte[] pkg, int pkgLen) {
        DeviceMaterialStatus status = new DeviceMaterialStatus();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            status.setExist(reader.readUnsignedChar());
            status.setUserId(reader.readUnsignedIntLSB());
            status.setMaterialId(reader.readUnsignedLongLSB());
            return status;
        } catch (IOException e) {
            return null;
        }
    }

    /** 解析清除加料信息 */
    public static int parseClearAddMaterialInfo(@NonNull byte[] pkg, int pkgLen) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            e.printStackTrace();
            // 错误代码，-1表示非法数据
            return -1;
        }
    }

    /** 解析操作结果代码 */
    public static int parseReturnCode(@NonNull byte[] pkg, int pkgLen) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkgLen));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            // 错误代码，-1表示非法数据
            return -1;
        }
    }
}
