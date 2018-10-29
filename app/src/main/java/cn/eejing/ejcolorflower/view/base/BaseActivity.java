package cn.eejing.ejcolorflower.view.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.activity.SignInActivity;
import io.reactivex.functions.Consumer;

import static cn.eejing.ejcolorflower.app.AppConstant.EXIT_LOGIN;

/**
 * Activity 基类
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static Map<String, Activity> activityMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        // 子类不再需要设置布局 ID，也不再需要使用 ButterKnife.BindView()
        setContentView(layoutViewId());
        ButterKnife.bind(this);

        setStatusBar();
        initView();
        initData();
        initListener();
    }

    /** 在 setContentView() 调用之前调用，可以设置 WindowFeature (如：this.requestWindowFeature(Window.FEATURE_NO_TITLE);) */
    public void init() {
    }

    public void initView() {
    }

    public void initData() {
    }

    public void initListener() {
    }

    /** 沉浸式状态栏 */
    protected void setStatusBar() {
        // Android 6.0 + 实现状态栏字色和图标浅黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // 使状态栏全透明
        StatusBarUtil.setTranslucent(this, 0);
    }

    /**
     * 设置 Toolbar
     *
     * @param title           标题
     * @param titleVisibility 标题控件是否显示
     * @param menu            右侧菜单文字
     * @param menuVisibility  右侧控件是否显示
     */
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 隐藏 Toolbar 左侧导航按钮
            actionBar.setDisplayHomeAsUpEnabled(false);
            // 隐藏 Toolbar 自带标题栏
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // 设置标题
        TextView textTitle = findViewById(R.id.tv_title_toolbar);
        textTitle.setVisibility(titleVisibility);
        textTitle.setTextSize(20);
        textTitle.setText(title);
        // 设置返回按钮
        ImageView imgTitleBack = findViewById(R.id.img_back_toolbar);
        imgTitleBack.setVisibility(View.VISIBLE);
        imgTitleBack.setOnClickListener(v -> finish());
        // 设置右侧菜单按钮
        TextView textMenu = findViewById(R.id.tv_menu_toolbar);
        textMenu.setVisibility(menuVisibility);
        textMenu.setText(menu);
    }

    /** 由子类实现 @return 当前界面的布局文件 id */
    protected abstract int layoutViewId();

    public void jumpToActivity(Intent intent) {
        startActivity(intent);
    }

    public void jumpToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public static void addActivity(String key, Activity activity) {
        if (activityMap.get(key) == null) {
            activityMap.put(key, activity);
        }
    }

    public static void delActivity(String key) {
        Activity activity = activityMap.get(key);
        if (activity != null) {
            if (activity.isDestroyed() || activity.isFinishing()) {
                activityMap.remove(key);
                return;
            }
            activity.finish();
            activityMap.remove(key);
        }
    }

    public void logout(Activity activity) {
        // 清空缓存
        Settings.clearInfo(getBaseContext());
        // 退出登陆回到登陆界面
        startActivity(new Intent(activity, SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        activity.finish();
        // 结束 MainActivity
        delActivity(EXIT_LOGIN);
    }

    @SuppressLint("CheckResult")
    public void setRxPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        // 申请的权限全部允许
                        Toast.makeText(BaseActivity.this, "已允许权限", Toast.LENGTH_SHORT).show();
                    } else {
                        // 只要有一个权限被拒绝，就会执行
                        Toast.makeText(BaseActivity.this, "未授权权限，部分功能不能使用", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
