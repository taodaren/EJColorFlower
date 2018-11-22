package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 关于我们
 */

public class MiAboutActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_about;
    }

    @Override
    public void initView() {
        setToolbar("关于我们", View.VISIBLE, null, View.GONE);
        setWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        WebView webAbout = findViewById(R.id.web_about);
        webAbout.loadUrl(Urls.ABOUT_US);
        WebSettings webSettings = webAbout.getSettings();

        // 5.0 以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        // 允许 js 代码
        webSettings.setJavaScriptEnabled(true);
        // 允许 SessionStorage/LocalStorage 存储
        webSettings.setDomStorageEnabled(true);
        // 禁用放缩
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        // 禁用文字缩放
        webSettings.setTextZoom(100);
        // 10M 缓存，api 18 后，系统自动管理。
        webSettings.setAppCacheMaxSize(10 * 1024 * 1024);
        // 允许缓存，设置缓存位置
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getDir("appcache", 0).getPath());
        // 允许 WebView 使用 File 协议
        webSettings.setAllowFileAccess(true);
        // 不保存密码
        webSettings.setSavePassword(false);
        // 自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
    }

}
