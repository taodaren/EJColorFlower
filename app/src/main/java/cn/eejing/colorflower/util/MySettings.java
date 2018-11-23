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

    public static LoginSession getLoginSessionInfo(Context context) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return new LoginSession(
                mSp.getString("username", null),
                mSp.getString("password", null),
                mSp.getLong("member_id", 0),
                mSp.getString("token", null));
    }

    public static void storeSessionInfo(Context context, LoginSession session) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString("username", session.getUsername());
        editor.putString("password", session.getPassword());
        editor.putLong("member_id", session.getMember_id());
        editor.putString("token", session.getToken());
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void clearInfo(Context context) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        mSp.edit().clear().commit();
    }

}
