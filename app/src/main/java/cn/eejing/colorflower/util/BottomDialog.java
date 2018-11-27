package cn.eejing.colorflower.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 自定义底部滑出 Dialog
 */

public class BottomDialog extends Dialog {

    public BottomDialog(Context context) {
        super(context);
        init();
    }

    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected BottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        // 获取当前Activity所在的窗体
        Window dialogWindow = this.getWindow();
        // 设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 消除边距
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        // 设置宽度充满屏幕
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = 0;//设置Dialog距离底部的距离
        // 将属性设置给窗体
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void show() {
        super.show();
    }
}
