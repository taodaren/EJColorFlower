package cn.eejing.ejcolorflower.view.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.GApp;
import cn.eejing.ejcolorflower.model.request.VersionUpdateBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.AppUtils;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.util.MySettings;
import cn.eejing.ejcolorflower.view.activity.MainActivity;
import cn.eejing.ejcolorflower.view.activity.SignInActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.EXIT_LOGIN;
import static cn.eejing.ejcolorflower.app.AppConstant.FORCED_UPDATE;
import static cn.eejing.ejcolorflower.presenter.Urls.DOWN_LOAD_APK;

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
        MySettings.clearInfo(getBaseContext());
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
//                        Toast.makeText(BaseActivity.this, "已允许权限", Toast.LENGTH_SHORT).show();
                    } else {
                        // 只要有一个权限被拒绝，就会执行
                        Toast.makeText(BaseActivity.this, "未授权权限，部分功能不能使用", Toast.LENGTH_SHORT).show();
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
            dialog.dismiss();
        });
        dialog.show();
    }

    /** 安装应用的流程 */
    public void installProcess() {
        Log.w("TYC", "installProcess: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 先获取是否有安装未知来源应用的权限
            if (!getPackageManager().canRequestPackageInstalls()) {
                // 没有权限
                showDialogByUnknownPermission();
                return;
            }
            Log.w("TYC", "有权限: ");
            // 有权限，开始安装应用程序
//            Toast.makeText(this, "有权限，开始安装应用程序", Toast.LENGTH_SHORT).show();
            AppUtils.downLoadApk(getApplication(), DOWN_LOAD_APK, "异景炫彩");
//            installApk();
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
            dialog.dismiss();
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        // 注意这个是 8.0 新 API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, FORCED_UPDATE);
    }

    private String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String versionName = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /** 强制版本更新 */
    public void forcedVersionUpdate() {
        OkGo.<String>post(Urls.VERSION_UPDATE)
                .tag(this)
                .params("app_id", 2)// 2代表安卓客户端
                .params("version_code", getVersionName(this))
//                .params("version_code", 3.0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.d(AppConstant.TAG, "新版本更新请求成功！" + body);

                        Gson gson = new Gson();
                        VersionUpdateBean bean = gson.fromJson(body, VersionUpdateBean.class);
                        Log.i(AppConstant.TAG, "Is_upload: " + bean.getData().getVersionData().getIs_upload());
                        Log.i(AppConstant.TAG, "getCode: " + bean.getCode());
                        if (bean.getCode() == 1) {
                            // 版本升级信息获取成功
                            switch (bean.getData().getVersionData().getIs_upload()) {
                                case 1:
                                    // 强制更新
                                    showDialogByVersionUpdate();
                                    break;
                                case 0:
                                    // 可选更新
                                    break;
                            }
                        }
                    }
                });
    }

    /** 强制版本更新 */
    public void urlDownload() {
//        OkGo.get(DOWN_LOAD_APK)//
//                .tag(this)//
//                .execute(new FileCallback(destFileDir, destFileName) {
//                    @Override
//                    public void onSuccess(Response<File> response) {
//
//                    }
//                });
    }

    // 安装应用
    private void installApk(File apk) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        } else {// Android7.0之后获取 uri 要用 contentProvider
            Uri uri = getImageContentUri(GApp.getContext(), apk);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(intent);
    }


    public static Uri getImageContentUri(Context context,File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
