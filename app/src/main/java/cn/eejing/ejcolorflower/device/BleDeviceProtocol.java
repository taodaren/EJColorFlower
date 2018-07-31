package cn.eejing.ejcolorflower.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import cn.eejing.ejcolorflower.util.BinaryReader;
import cn.eejing.ejcolorflower.util.CRC16;
import cn.eejing.ejcolorflower.util.Util;

/**
 * 蓝牙设备协议处理
 */

public class BleDeviceProtocol {
    private final static String TAG = "BleDeviceProtocol";
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
    private final static int CMD_EXIT_REAL_TIME_CTRL_MODE = 17;                 // 退出实时控制模式

    private final static int HEADER_LEN = 7;
    private final byte[] pkg = new byte[MAX_PKG_LEN];
    private int mPkgLen = 0;
    private boolean mTranslate;

    /** 接收 */
    public void bleReceive(byte[] data) {
        for (byte b : data) {
            onByte(b);
        }
    }

    /** 反转义 */
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

    private int s = 0;
    private int data_len = 0;
    private final byte[] S = new byte[6];
    private int Sn = 0;
    private final static int[] K = new int[]{
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

    /** 解密 */
    private void onByteLevel2(byte b, boolean is_translated) {
        if (b == (byte) 0xDC && !is_translated) {
            s = 1;
        } else if (s == 1) {
            data_len = (int) b & 0xff;
            if (data_len > MAX_PKG_LEN) {
                s = 0;
            } else {
                Sn = 0;
                s = 2;
            }
        } else if (s == 2) {
            S[Sn] = b;
            Sn++;
            if (Sn >= S.length) {
                s = 3;
                mPkgLen = 0;
            }
        } else if (s == 3) {
            pkg[mPkgLen] = map(mPkgLen, b);
            mPkgLen++;
            if (mPkgLen >= data_len) {
                s = 0;
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
        Log.e(TAG, "格式错误的 PKG--->" + Util.hex(pkg, mPkgLen));
    }

    public byte[] getPkg() {
        return Arrays.copyOfRange(pkg, 0, mPkgLen);
    }

    /** PKG 长度已验证 */
    private void pkgLengthValidated() {
        if (CRC16.validate(pkg, mPkgLen - 2)) {
            if ((pkg[2] & 0x80) != 0) {
                Log.i(TAG, "ack package   " + Util.hex(pkg, mPkgLen));
                ackPkg();
            } else {
                Log.e(TAG, "drop command package " + Util.hex(pkg, mPkgLen));
            }
        } else {
            Log.e(TAG, "CRC wrong package " + Util.hex(pkg, mPkgLen));
        }
    }

    private void ackPkg() {
        int cmd = pkg[2] & 0x7F;
//        long id = parseID(pkg, mPkgLen);
        switch (cmd) {
            case CMD_GET_STATUS:
                DeviceStatus ds = parseStatus(pkg, mPkgLen);
                if (ds != null) {
                    receivePkg(ds);
                }
                break;
            case CMD_GET_CONFIG:
                DeviceConfig config = parseConfig(pkg, mPkgLen);
                if (config != null) {
                    receivePkg(config);
                }
                break;
            default:
                receivePkg(pkg, mPkgLen);
                break;
        }
    }

    /** 接收包-状态 */
    protected void receivePkg(@NonNull DeviceStatus status) {
    }

    /** 接收包-配置 */
    protected void receivePkg(@NonNull DeviceConfig config) {
    }

    /** 接收包-其它 */
    protected void receivePkg(@NonNull byte[] pkg, int pkg_len) {
        Log.e(TAG, "接收 PKG--->" + Util.hex(pkg, pkg_len));
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

        Log.i(TAG, "CMD PKG--->" + Util.hex(pkg, pkg.length));
        return pkg;
    }

    /** 加密包 */
    @NonNull
    private static byte[] encryptPkg(@NonNull byte[] d) {
        final SecureRandom seedGen = new SecureRandom();
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

    // TODO: /**<-------------------- 以下发送数据（打包） -------------------->**/
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
    public static byte[] pkgJetStart(long devId, int delay, int duration, int high) {
        byte[] data = new byte[5];

        data[0] = (byte) (delay & 0xff);
        data[1] = (byte) ((delay >> 8) & 0xff);

        data[2] = (byte) (duration & 0xff);
        data[3] = (byte) ((duration >> 8) & 0xff);

        data[4] = (byte) (high & 0xff);

        return cmdPkg(CMD_JET_START, devId, data);
    }

    /** 加料 */
    @NonNull
    public static byte[] pkgAddMaterial(long devId, int time, long timestamp, long userId, long materialId) {
        byte[] data = new byte[14];

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

        return cmdPkg(CMD_ADD_MATERIAL, devId, data);
    }

    /** 获取时间戳 */
    @NonNull
    public static byte[] pkgGetTimestamp(long devId,long timestamp) {
        byte[] data = new byte[4];

        data[0] = (byte) (timestamp & 0xff);
        data[1] = (byte) ((timestamp >> 8) & 0xff);
        data[2] = (byte) ((timestamp >> 16) & 0xff);
        data[3] = (byte) ((timestamp >> 24) & 0xff);

        return cmdPkg(CMD_GET_TIMESTAMP, devId, data);
    }

    /** 停止喷射 */
    @NonNull
    public static byte[] pkgJetStop(long id) {
        return cmdPkg(CMD_JET_STOP, id, null);
    }

    /** 设置刮料时间 */
    @NonNull
    public static byte[] pkgSetScrapeMaterialTime(long id, int time) {
        byte[] data = new byte[1];

        data[0] = (byte) (time & 0xff);

        return cmdPkg(CMD_SET_SCRAPE_MATERIAL_TIME, id, data);
    }

    /** 获取加料状态 */
    @NonNull
    public static byte[] pkgGetAddMaterialStatus(long id) {
        return cmdPkg(CMD_GET_ADD_MATERIAL_STATUS, id, null);
    }

    /** 清除加料信息 */
    @NonNull
    public static byte[] pkgClearAddMaterialInfo(long id, long user_id, long material_id) {
        byte[] data = new byte[10];

        data[0] = (byte) (user_id & 0xff);
        data[1] = (byte) ((user_id >> 8) & 0xff);
        data[2] = (byte) ((user_id >> 16) & 0xff);
        data[3] = (byte) ((user_id >> 24) & 0xff);

        data[4] = (byte) (material_id & 0xff);
        data[5] = (byte) ((material_id >> 8) & 0xff);
        data[6] = (byte) ((material_id >> 16) & 0xff);
        data[7] = (byte) ((material_id >> 24) & 0xff);
        data[8] = (byte) ((material_id >> 32) & 0xff);
        data[9] = (byte) ((material_id >> 40) & 0xff);

        return cmdPkg(CMD_CLEAR_ADD_MATERIAL_INFO, id, data);
    }

    /** 进入在线实时控制模式 */
    @NonNull
    public static byte[] pkgEnterRealTimeCtrlMode(long id, int devNum, int startAddress, byte[] high) {
        byte[] data = new byte[2 + devNum];
        data[0] = (byte) (devNum & 0xff);
        data[1] = (byte) (startAddress & 0xff);
        for (int i = 0; i < devNum; i++) {
            data[1 + i] = high[i];
        }
        return cmdPkg(CMD_ENTER_REAL_TIME_CTRL_MODE, id, data);
    }


    // TODO: /**<-------------------- 以下蓝牙接收数据（解析） -------------------->**/
    @Nullable
    private static DeviceStatus parseStatus(@NonNull byte[] pkg, int pkg_len) {
        DeviceStatus ds = new DeviceStatus();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            ds.mTemperature = reader.readSignedShortLSB();
            ds.mSupplyVoltage = (float) reader.readUnsignedChar() / 10;
            ds.mMotorSpeed[0] = reader.readUnsignedChar();
            ds.mMotorSpeed[1] = reader.readUnsignedChar();
            ds.mMotorSpeed[2] = reader.readUnsignedChar();
            ds.mMotorSpeed[3] = reader.readUnsignedChar();
            ds.mPitch = reader.readUnsignedChar();
            ds.mUltrasonicDistance = reader.readUnsignedChar();
            ds.mInfraredDistance = reader.readUnsignedChar();
            ds.mRestTime = reader.readUnsignedShortLSB();
            return ds;
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static DeviceConfig parseConfig(@NonNull byte[] pkg, int pkg_len) {
        DeviceConfig config = new DeviceConfig();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            config.mID = reader.readUnsignedIntLSB();
            config.mMotorDefaultSpeed[0] = reader.readUnsignedChar();
            config.mMotorDefaultSpeed[1] = reader.readUnsignedChar();
            config.mMotorDefaultSpeed[2] = reader.readUnsignedChar();
            config.mMotorDefaultSpeed[3] = reader.readUnsignedChar();
            config.mTemperatureThresholdLow = reader.readSignedShortLSB();
            config.mTemperatureThresholdHigh = reader.readSignedShortLSB();
            config.mDMXAddress = reader.readUnsignedShortLSB();
            config.mGualiaoTime = reader.readUnsignedChar();
            return config;
        } catch (IOException e) {
            return null;
        }
    }

    private static long parseID(byte[] pkg, int pkg_len) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(3);
            return reader.readUnsignedIntLSB();
        } catch (IOException e) {
            return 0;
        }
    }

    public static int parseDmx(@NonNull byte[] pkg, int pkg_len) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long parseTimestamp(@NonNull byte[] pkg, int pkg_len) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedIntLSB();
        } catch (IOException e) {
            return -1;
        }
    }

    @Nullable
    public static DeviceMaterialStatus parseMaterialStatus(@NonNull byte[] pkg, int pkg_len) {
        DeviceMaterialStatus status = new DeviceMaterialStatus();
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            status.exist = reader.readUnsignedChar();
            status.userId = reader.readUnsignedIntLSB();
            status.materialId = reader.readUnsignedIntLSB();
            return status;
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public static int parseAddMaterial(@NonNull byte[] pkg, int pkg_len) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            return -1;
        }
    }

    public static int parseStartJet(@NonNull byte[] pkg, int pkg_len) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * @return 错误代码，-1表示非法数据
     */
    public static int parseClearMaterialInfo(@NonNull byte[] pkg, int pkg_len) {
        BinaryReader reader = new BinaryReader(new ByteArrayInputStream(pkg, 0, pkg_len));
        try {
            reader.skip(HEADER_LEN);
            return reader.readUnsignedChar();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
