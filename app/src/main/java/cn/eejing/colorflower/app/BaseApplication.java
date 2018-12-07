package cn.eejing.colorflower.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Application 基类
 */

public class BaseApplication extends LitePalApplication {
    // 以下属性应用于整个应用程序，合理利用资源，减少资源浪费
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;          // 上下文
    private static Thread mMainThread;        // 主线程
    private static long mMainThreadId;        // 主线程 id
    private static Looper mMainLooper;        // 循环队列
    private static Handler mHandler;          // 主线程 Handler

    private String flagQrCode;
    private String flagGifDemo;
    private String flagAddrMgr;

    @Override
    public void onCreate() {
        super.onCreate();
        // 对全局属性赋值
        mContext = getApplicationContext();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();

        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        initOkGo();
        // 初始化 LitePal 数据库
        LitePal.initialize(this);
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        BaseApplication.mContext = mContext;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static void setMainThread(Thread mMainThread) {
        BaseApplication.mMainThread = mMainThread;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static void setMainThreadId(long mMainThreadId) {
        BaseApplication.mMainThreadId = mMainThreadId;
    }

    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    public static void setMainThreadLooper(Looper mMainLooper) {
        BaseApplication.mMainLooper = mMainLooper;
    }

    public static Handler getMainHandler() {
        return mHandler;
    }

    public static void setMainHandler(Handler mHandler) {
        BaseApplication.mHandler = mHandler;
    }

    public static String getVersionName(Context context) {
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

    /** 重启当前应用 */
    public static void restart() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    private void initOkGo() {
        // 1.构建OkHttpClient.Builder
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 2.配置log
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        // log 打印级别，决定了 log 显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               // log 颜色级别，决定了 log 在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 // 添加 OkGo 默认 debug 日志

        // 3.超时时间设置，默认 60 秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        // 4.配置Cookie
        // 如果你用到了 Cookie 的持久化或者叫 Session 的保持，那么建议配置一个 Cookie，
        // 这个也是可以自定义的，不一定非要用 OkGo 自己的，以下三个是 OkGo 默认提供的三种方式，
        // 可以选择添加，也可以自己实现 CookieJar 的接口，自己管理 cookie。

        // 自动管理 cookie（或者叫 session 的保持），以下几种任选其一就行
//        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));       // 使用 sp 保持 cookie，如果 cookie 不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));       // 使用数据库保持 cookie，如果 cookie 不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));       // 使用内存保持 cookie，app 退出后，cookie 消失

        // 5.Https 相关设置，以下几种方案根据需要自己设置
        // 方法一：信任所有证书,不安全有风险
        // HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        // 方法二：自定义信任规则，校验服务端证书
        // HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        // 方法三：使用预埋证书，校验服务端证书（自签名证书）
        // HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        // 方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        // HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));

        // builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        // 配置 https 的域名匹配规则，详细看 demo 的初始化介绍，不需要就不要加入，使用不当会导致 https 握手失败
        // builder.hostnameVerifier(new SafeHostnameVerifier());

        // 6.配置 OkGo
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/

        /* ---------- 这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传 ---------- */
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");                      // header 不支持中文，不允许有特殊字符
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");             // param 支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");
        /* ---------- 这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传 ---------- */

        OkGo.getInstance().init(this)                           // 必须调用初始化
                .setOkHttpClient(builder.build())               // 建议设置 OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               // 全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   // 全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               // 全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers)                      // 全局公共头
                .addCommonParams(params);                       // 全局公共参数
    }

    /** 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者 leader 确定，以下代码不要直接使用 */
    private class SafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                for (X509Certificate certificate : chain) {
                    // 检查证书是否过期，签名是否通过等
                    certificate.checkValidity();
                }
            } catch (Exception e) {
                throw new CertificateException(e);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /** 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者 leader 确定，以下代码不要直接使用 */
    private class SafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            // 验证主机名是否匹配
            // return hostname.equals("server.jeasonlzy.com");
            return true;
        }
    }

    public String getFlagQrCode() {
        return flagQrCode;
    }

    public void setFlagQrCode(String flagQrCode) {
        this.flagQrCode = flagQrCode;
    }

    public String getFlagGifDemo() {
        return flagGifDemo;
    }

    public void setFlagGifDemo(String flagGifDemo) {
        this.flagGifDemo = flagGifDemo;
    }

    public String getFlagAddrMgr() {
        return flagAddrMgr;
    }

    public void setFlagAddrMgr(String flagAddrMgr) {
        this.flagAddrMgr = flagAddrMgr;
    }

}
