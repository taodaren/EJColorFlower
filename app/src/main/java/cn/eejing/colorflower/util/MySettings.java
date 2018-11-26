package cn.eejing.colorflower.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import cn.eejing.colorflower.model.session.LoginSession;

/**
 * 配置
 */

public class MySettings {
    private static SharedPreferences mSp;

    public static LoginSession getLoginInfo(Context context) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return new LoginSession(
                mSp.getString("token", null),
                mSp.getString("level", null),
                mSp.getLong("user_id", 0),
                mSp.getString("username", null),
                mSp.getString("password", null));
    }

    public static void saveLoginInfo(Context context, LoginSession session) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString("token", session.getToken());
        editor.putString("level", session.getLevel());
        editor.putLong("user_id", session.getUserId());
        editor.putString("username", session.getUsername());
        editor.putString("password", session.getPassword());
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void clearLoginInfo(Context context) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        mSp.edit().clear().commit();
    }

}
