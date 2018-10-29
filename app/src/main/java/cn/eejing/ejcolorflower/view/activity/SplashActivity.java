package cn.eejing.ejcolorflower.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 闪屏启动页
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        super.initView();
        // Activity 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // get user info 以后可以添加验证用户信息等
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }
}
