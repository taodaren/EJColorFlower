package cn.eejing.colorflower.view.activity;

import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.AppConstant;
import cn.eejing.colorflower.model.request.AboutLinkBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 关于我们
 */

public class MiAboutActivity extends BaseActivity {

    @BindView(R.id.web_about)
    WebView webAbout;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_about;
    }

    @Override
    public void initView() {
        setToolbar("关于我们", View.VISIBLE, null, View.GONE);
    }

    @Override
    public void initData() {
        getDataWithAboutLink();
    }

    private void getDataWithAboutLink() {
        OkGo.<String>get(Urls.ABOUT_LINK)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.e(AppConstant.TAG, "about_link request succeeded --->" + body);

                        Gson gson = new Gson();
                        AboutLinkBean bean = gson.fromJson(body, AboutLinkBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                setWebView(bean.getData());
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void setWebView(String data) {
        webAbout.loadUrl(data);

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
