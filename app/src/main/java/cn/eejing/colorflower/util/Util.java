package cn.eejing.colorflower.util;

import java.util.Locale;

/**
 * 工具类
 */

public class Util {
    public static String hex(byte[] data, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len && i < data.length; i++) {
            sb.append(String.format(Locale.US, "%02X ", (int) data[i] & 0xff));
        }
        return sb.toString();
    }

    public static byte[] int2byte(int[] d) {
        byte[] r = new byte[d.length];
        for (int i = 0; i < d.length; i++) {
            r[i] = (byte) d[i];
        }
        return r;
    }

}
