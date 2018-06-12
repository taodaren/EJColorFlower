package cn.eejing.ejcolorflower.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import cn.eejing.ejcolorflower.app.LoginSession;

/**
 * 配置
 */

public class Settings {

    public static LoginSession getLoginSessionInfo(Context context) {
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return new LoginSession(
                settings.getString("username", null),
                settings.getString("password", null),
                settings.getLong("member_id", 0),
                settings.getString("token", null));
    }

    public static void storeSessionInfo(Context context, LoginSession session) {
        SharedPreferences settings = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", session.getUsername());
        editor.putString("password", session.getPassword());
        editor.putLong("member_id", session.getMember_id());
        editor.putString("token", session.getToken());
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void clearInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

}
