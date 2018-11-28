package cn.eejing.colorflower.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间戳等工具类
 */

public class StampUtil {
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 获取当前时间 */
    public static String getCurDate(String format){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param format  日期格式
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = FORMAT;
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format  如：yyyy-MM-dd HH:mm:ss
     */
    public static String date2TimeStamp(String dateStr, String format) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /** 获取系统当前时间戳 */
    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    /** 取得当前时间戳（精确到秒） */
    public static String timeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /** 判断是否闰年 */
    public static boolean isLeapYear(int year) {
        return (year % 100 == 0 && year % 400 == 0) || (year % 100 != 0 && year % 4 == 0);
    }

    /** 获取特定年月对应的天数 */
    public static int getLastDay(int year, int month) {
        if (month == 2) {
            // 二月闰年返回29，防止28
            return isLeapYear(year) ? 29 : 28;
        }
        // 一三五七八十腊，三十一天永不差
        return month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12 ? 31 : 30;
    }

    private void forExample() {
        // 运行输出:timeStamp=1470278082
        String timeStamp = timeStamp();
        LogUtil.d("TYC", "timeStamp=" + timeStamp);

        // 运行输出:1470278082980 该方法的作用是返回当前的计算机时间，时间的表达格式为当前计算机时间和GMT时间(格林威治时间)1970年1月1号0时0分0秒所差的毫秒数
        LogUtil.d("TYC", "" + getTimeStamp());

        // 运行输出:date=2016-08-04 10:34:42
        String date = timeStamp2Date(timeStamp, FORMAT);
        LogUtil.d("TYC", "date=" + date);

        // 运行输出:1470278082
        String timeStamp2 = date2TimeStamp(date, FORMAT);
        LogUtil.d("TYC", "timeStamp2=" + timeStamp2);
    }
}
