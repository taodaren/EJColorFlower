package cn.eejing.colorflower.view.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.model.HttpParams;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.VersionUpdateBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.AppUtils;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.SelfDialogBase;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.activity.SignInActivity;

import static cn.eejing.colorflower.app.AppConstant.EXIT_LOGIN;
import static cn.eejing.colorflower.app.AppConstant.NO_TOKEN;
import static cn.eejing.colorflower.app.AppConstant.REQUEST_CODE_FORCED_UPDATE;
import static cn.eejing.colorflower.app.BaseApplication.getVersionName;
import static cn.eejing.colorflower.presenter.Urls.DOWN_LOAD_APK;

/**
 * Activity 基类
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private static Map<String, Activity> activityMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.setLevel(LogUtil.getmLevel());
        // 子类不再需要设置布局 ID，也不再需要使用 ButterKnife.BindView()
        setContentView(layoutViewId());
        ButterKnife.bind(this);
        setStatusBar();
        initView();
        initData();
        initListener();
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

    public void finishAllActivity() {
        for (Map.Entry<String, Activity> entry : activityMap.entrySet()) {
            finish();
        }
        activityMap.clear();
    }

    public void logout(Activity activity) {
        // 清空缓存
        MySettings.clearLoginInfo(getBaseContext());
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
                    if (!granted) {
                        // 只要有一个权限被拒绝，就会执行
                        ToastUtil.showShort("未授权权限，部分功能不能使用");
                    }
                });
    }

    /** 设备断开 Dialog */
    public void showDialogByDisconnect(Activity activity) {
        SelfDialogBase dialog;
        dialog = new SelfDialogBase(activity);
        dialog.setTitle("设备已断开，请检查设备连接");
        dialog.setYesOnclickListener("确定", () -> {
            dialog.dismiss();
            startActivity(new Intent(activity, MainActivity.class));
        });
        dialog.show();
    }

    /** 版本更新 */
    @SuppressWarnings("unchecked")
    public void forcedVersionUpdate() {
        OkGoBuilder.getInstance().setToken(NO_TOKEN);
        HttpParams params = new HttpParams();
        params.put("app_id", 2);// 2代表安卓客户端
        params.put("version_code", getVersionName(this));

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.NEW_VERSION_UPDATE)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(VersionUpdateBean.class)
                .callback(new Callback<VersionUpdateBean>() {
                    @Override
                    public void onSuccess(VersionUpdateBean bean, int id) {
                        LogUtil.d(TAG, "版本更新 请求成功");

                        if (bean.getCode() == 1) {
                            // 版本升级信息获取成功强制更新（is_upload 1-强制更新 0-可选更新）
                            if (bean.getData().getVersionData().getIs_upload() == 1) {
                                showDialogByVersionUpdate();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    /** 强制版本更新 Dialog */
    private void showDialogByVersionUpdate() {
        SelfDialogBase dialog;
        dialog = new SelfDialogBase(this);
        dialog.setTitle("更新到最新版本方可继续使用！");
        dialog.setYesOnclickListener("更新", () -> {
            dialog.dismiss();
            installProcess();
        });
        dialog.setNoOnclickListener("退出APP", () -> {
            finishAllActivity();
            dialog.dismiss();
        });
        dialog.show();
    }

    /** 安装应用的流程 */
    public void installProcess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 先获取是否有安装未知来源应用的权限
            if (!getPackageManager().canRequestPackageInstalls()) {
                // 没有权限，设置未知来源权限
                showDialogByUnknownPermission();
                return;
            }
            // 有权限，开始安装应用程序
            AppUtils.downLoadApk(this, DOWN_LOAD_APK, "异景炫彩");
        }
    }

    /** 打开未知来源权限 Dialog */
    private void showDialogByUnknownPermission() {
        SelfDialogBase dialog;
        dialog = new SelfDialogBase(this);
        dialog.setTitle("安装应用需要打开未知来源权限，请去设置中开启权限！");
        dialog.setYesOnclickListener("设置", () -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startInstallPermissionSettingActivity();
            }
            dialog.dismiss();
        });
        dialog.setNoOnclickListener("退出APP", () -> {
            finishAllActivity();
            dialog.dismiss();
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        // 注意这个是 8.0 新 API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        // 在 MainActivity 进行回调
        startActivityForResult(intent, REQUEST_CODE_FORCED_UPDATE);
    }

}
