package cn.eejing.colorflower.model.http;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import cn.eejing.colorflower.model.event.JumpLoginEvent;
import cn.eejing.colorflower.model.request.BaseBean;
import cn.eejing.colorflower.model.request.TokenBean;
import cn.eejing.colorflower.model.session.LoginSession;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.view.activity.MainActivity;

/**
 * OkGgo 帮助类-建造者模式
 */

public class OkGoBuilder<T> {
    private static final String TAG = "OkGoBuilder";
    public static final int GET  = 1;    // get 请求
    public static final int POST = 2;    // post 请求

    private Activity mActivity;
    private String mToken;
    private String mUrl;                 // 请求网址
    private HttpParams mParams;          // 参数
    private Class<T> mEntity;            // 实体类
    private Callback<T> mCallback;       // 回调
    private int mRequestType;            // 请求类型

    // 单列模式
    private static OkGoBuilder mOkGoBuilder = null;

    /** 构造函数私有化 */
    private OkGoBuilder() {
    }

    /** 公有的静态函数，对外暴露获取单例对象的接口 */
    public static OkGoBuilder getInstance() {
        if (mOkGoBuilder == null) {
            synchronized (OkGoBuilder.class) {
                if (mOkGoBuilder == null) {
                    mOkGoBuilder = new OkGoBuilder();
                }
            }
        }
        return mOkGoBuilder;
    }

    public OkGoBuilder Builder(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    public OkGoBuilder url(String url) {
        this.mUrl = url;
        return this;
    }

    public OkGoBuilder method(int requestType) {
        this.mRequestType = requestType;
        return this;
    }

    public OkGoBuilder params(HttpParams params) {
        this.mParams = params;
        return this;
    }

    public OkGoBuilder cls(Class<T> clazz) {
        this.mEntity = clazz;
        return this;
    }

    public OkGoBuilder callback(Callback<T> callback) {
        this.mCallback = callback;
        return this;
    }

    public OkGoBuilder build() {
        if (mRequestType == GET) {
            getRequest();
        } else {
            postRequest();
        }
        return this;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    /** post 请求 */
    private void postRequest() {
        mParams.put("token", mToken);

        OkGo.<T>post(mUrl)                       // 请求方式和请求 url
                .params(mParams)                 // 请求参数
                .tag(this)                       // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")            // 设置当前请求的缓存 key, 建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new DialogCallback<T>(mActivity, mEntity) {
                    @Override
                    public void onSuccess(Response<T> response) {
                        String json = new Gson().toJson(response.body());
                        BaseBean bean = new Gson().fromJson(json, BaseBean.class);
                        switch (bean.getCode()) {
                            case 21:// Token 已更新，重新请求接口
                                mParams.remove("token");
                                TokenBean tokenBean = new Gson().fromJson(json, TokenBean.class);
                                mToken = tokenBean.getData().getToken();
                                MySettings.updateToken(mActivity, new LoginSession(mToken));
                                postRequest();
                                break;
                            case 20:// Token 不存在
                            case 22:// Token 已过期
                                EventBus.getDefault().post(new JumpLoginEvent("跳转到登陆界面"));
                                break;
                            default:
                                mCallback.onSuccess(response.body(), 1);
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<T> response) {
                        super.onError(response);
                        Throwable throwable = response.getException();
                        if (throwable != null) {
                            mCallback.onError(throwable, 2);
                        }
                    }
                });
    }

    /** get 请求 */
    private void getRequest() {
        mParams.put("token", mToken);

        OkGo.<T>get(mUrl)
                .params(mParams)
                .tag(this)
                .cacheKey("cacheKey")
                .cacheMode(CacheMode.DEFAULT)
                .execute(new DialogCallback<T>(mActivity, mEntity) {
                    @Override
                    public void onSuccess(Response<T> response) {
                        String json = new Gson().toJson(response.body());
                        BaseBean bean = new Gson().fromJson(json, BaseBean.class);
                        switch (bean.getCode()) {
                            case 21:// Token 已更新，重新请求接口
                                mParams.remove("token");
                                TokenBean tokenBean = new Gson().fromJson(json, TokenBean.class);
                                mToken = tokenBean.getData().getToken();
                                MySettings.updateToken(mActivity, new LoginSession(mToken));
                                getRequest();
                                break;
                            case 20:// Token 不存在
                            case 22:// Token 已过期
                                EventBus.getDefault().post(new JumpLoginEvent("跳转到登陆界面"));
                                break;
                            default:
                                mCallback.onSuccess(response.body(), 1);
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<T> response) {
                        super.onError(response);
                        Throwable throwable = response.getException();
                        if (throwable != null) {
                            throwable.printStackTrace();
                            mCallback.onError(throwable, 2);
                        }
                    }
                });
    }

}