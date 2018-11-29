package cn.eejing.colorflower.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间相关工具类
 */

public class DateUtil {
    public static final String FORMAT_ALL  = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_MDHM = "MM-dd HH:mm";
    public static final String FORMAT_YMD  = "yyyy-MM-dd";
    public static final String FORMAT_MD   = "MM-dd";
    public static final String FORMAT_HMS  = "HH:mm:ss";
    public static final String FORMAT_HM   = "HH:mm";

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
            format = FORMAT_ALL;
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

    /** 获取特定年月对应的天数 */
    public static int getLastDay(int year, int month) {
        if (month == 2) {
            // 二月闰年返回29，防止28
            return isLeapYear(year) ? 29 : 28;
        }
        // 一三五七八十腊，三十一天永不差
        return month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12 ? 31 : 30;
    }

    /** 判断是否闰年 */
    public static boolean isLeapYear(int year) {
        return (year % 100 == 0 && year % 400 == 0) || (year % 100 != 0 && year % 4 == 0);
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException 解析异常
     */
    public static boolean isToday(String day) throws ParseException {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return diffDay == 0;
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException 解析异常
     */
    public static boolean isYesterday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            return diffDay == -1;
        }
        return false;
    }

    /** 得到本周周一 */
    public static long getMondayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + 1);
        return c.getTimeInMillis();
    }

    /** 得到本周周日 */
    public static long getSundayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + 7);
        return c.getTimeInMillis();
    }

    /** 是否是这个星期 */
    public static boolean isWeek(long time) {
        long startWeek = getMondayOfThisWeek(); // 本周一
        long endWeek = getSundayOfThisWeek();
        return time >= startWeek && time <= endWeek;
    }

    /** 显示星期格式 */
    public static String showWeekString(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if (Calendar.MONDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return "星期一";
        }
        if (Calendar.TUESDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return "星期二";
        }
        if (Calendar.WEDNESDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return "星期三";
        }
        if (Calendar.THURSDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return "星期四";
        }
        if (Calendar.FRIDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return "星期五";
        }
        if (Calendar.SATURDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return "星期六";
        }
        if (Calendar.SUNDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            return "星期日";
        }
        return "星期一";
    }

    /** 是否是今年 */
    public static boolean isYear(long time) {
        Calendar cal = Calendar.getInstance();
        int dy = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(time);
        int iy = cal.get(Calendar.YEAR);
        return dy == iy;
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

}
