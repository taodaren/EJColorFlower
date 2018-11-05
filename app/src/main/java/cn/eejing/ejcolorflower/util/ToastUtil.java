package cn.eejing.ejcolorflower.util;

import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

import cn.eejing.ejcolorflower.app.BaseApplication;

public class ToastUtil {

    private static Toast mToast;

    public static void showShort(CharSequence text) {
        mToast = Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void showShort(@StringRes int resId) {
        mToast = Toast.makeText(BaseApplication.getContext(), resId, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void showLong(CharSequence text) {
        mToast = Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

}
