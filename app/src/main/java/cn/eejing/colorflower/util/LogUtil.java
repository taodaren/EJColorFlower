package cn.eejing.colorflower.util;

import android.util.Log;

public class LogUtil {
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int NOTHING = 6;

    private static int mLevel = VERBOSE;

    public static void v(String tag, String msg) {
        if (mLevel <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mLevel <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mLevel <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mLevel <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (mLevel <= ERROR) {
            Log.e(tag, msg);
        }
    }

    public static int getVERBOSE() {
        return VERBOSE;
    }

    public static int getDEBUG() {
        return DEBUG;
    }

    public static int getINFO() {
        return INFO;
    }

    public static int getWARN() {
        return WARN;
    }

    public static int getERROR() {
        return ERROR;
    }

    public static int getNOTHING() {
        return NOTHING;
    }

    public static int getmLevel() {
        return mLevel;
    }

    public static void setLevel(int level) {
        LogUtil.mLevel = level;
    }
}
