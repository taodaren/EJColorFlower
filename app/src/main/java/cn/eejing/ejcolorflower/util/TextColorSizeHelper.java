package cn.eejing.ejcolorflower.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

public class TextColorSizeHelper {

    /**
     * 更改 TextView 某一段字体的颜色值
     */
    public static SpannableStringBuilder getTextSpan(Context context,
                                                     int subTextBgColor,
                                                     String text, String... subTextArray) {
        if (context == null || text == null || subTextArray == null)
            return null;
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int begin = 0;
        int end = 0;
        for (String subText : subTextArray) {
            begin = text.indexOf(subText, end);
            end = begin + subText.length();
            //
            style.setSpan(new ForegroundColorSpan(subTextBgColor), begin, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return style;
    }


    /**
     * 更改 TextView 某一段字体的颜色(字体带圆角背景)
     */
    public static SpannableStringBuilder getTextSpan(Context context,
                                                     int subTextColor, int subTextBgColor, int radius,
                                                     String text, String... subTextArray) {
        if (context == null || text == null || subTextArray == null) {
            return null;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int begin = 0;
        int end = 0;
        for (String subText : subTextArray) {
            begin = text.indexOf(subText, end);
            end = begin + subText.length();
            //
            style.setSpan(new RadiusBackgroundSpan(subTextColor, subTextBgColor, radius), begin, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return style;
    }

    public static SpannableStringBuilder getTextSpan(Context context,
                                                     int subTextColor, int subTextSize,
                                                     String text, String... subTextArray) {
        if (context == null || text == null || subTextArray == null) {
            return null;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int begin = 0;
        int end = 0;
        for (String subText : subTextArray) {
            begin = text.indexOf(subText, end);
            end = begin + subText.length();
            //
            style.setSpan(new AbsoluteSizeSpan(subTextSize), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            style.setSpan(new ForegroundColorSpan(subTextColor), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return style;
    }

}
