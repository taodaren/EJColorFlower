package cn.eejing.ejcolorflower.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import cn.eejing.ejcolorflower.model.session.LoginSession;
import cn.eejing.ejcolorflower.model.session.AddrSession;

/**
 * 配置
 */

public class Settings {
    private static SharedPreferences mSp;

    public static LoginSession getLoginSessionInfo(Context context) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return new LoginSession(
                mSp.getString("username", null),
                mSp.getString("password", null),
                mSp.getLong("member_id", 0),
                mSp.getString("token", null));
    }

    public static AddrSession getAddrSessionInfo(Context context) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return new AddrSession(
                mSp.getString("consignee", null),
                mSp.getString("phone", null),
                mSp.getString("address", null));
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

    public static void saveAddressInfo(Context context, AddrSession session) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString("consignee", session.getConsignee());
        editor.putString("phone", session.getPhone());
        editor.putString("address", session.getAddress());
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void clearInfo(Context context) {
        mSp = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        mSp.edit().clear().commit();
    }

}
