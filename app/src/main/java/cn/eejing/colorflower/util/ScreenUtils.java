package cn.eejing.colorflower.util;

import android.app.Activity;
import android.content.Context;
import android.view.Display;

/**
 * 获取屏幕尺寸
 */

public class ScreenUtils {

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();

        return display.getWidth();
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
