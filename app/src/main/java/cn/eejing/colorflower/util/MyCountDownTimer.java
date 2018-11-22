package cn.eejing.colorflower.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 自定义倒计时器
 */

public class MyCountDownTimer extends CountDownTimer {
    private Context mContext;
    private TextView mTV;
    private int noId, yesId;

    /**
     * @param context           上下文
     * @param view              文本控件
     * @param millisInFuture    倒计时持续时间
     * @param countDownInterval 倒计时间隔时间
     * @param noDrawableId      不可点击状态背景资源
     * @param yesDrawableId     可点击状态背景资源
     */
    public MyCountDownTimer(Context context, TextView view, long millisInFuture, long countDownInterval, int noDrawableId, int yesDrawableId) {
        super(millisInFuture, countDownInterval);
        this.mContext = context;
        this.mTV = view;
        this.noId = noDrawableId;
        this.yesId = yesDrawableId;
    }

    /** 计时过程 */
    @SuppressLint("SetTextI18n")
    @Override
    public void onTick(long millisUntilFinished) {
        // 防止计时过程时重复点击
        mTV.setClickable(false);
        mTV.setBackground(mContext.getResources().getDrawable(noId));
        mTV.setText(millisUntilFinished / 1000 + 1 + "s 后重新获取");
    }

    /** 计时完毕 */
    @Override
    public void onFinish() {
        mTV.setClickable(true);
        mTV.setBackground(mContext.getResources().getDrawable(yesId));
        mTV.setText("重新获取");
    }

}
