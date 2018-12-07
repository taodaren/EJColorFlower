package cn.eejing.colorflower.model.http;

import com.google.gson.Gson;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.Type;

import cn.eejing.colorflower.model.request.TokenBean;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 将返回过来的 json 字符串转换为实体类
 */

public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Type mType;
    private Class<T> clazz;

    public JsonCallback() {
    }

    public JsonCallback(Type type) {
        mType = type;
    }

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        T data = null;
        Gson gson = new Gson();
        assert response.body() != null;
        String str = response.body().string();

        if (mType != null) {
            data = gson.fromJson(str, mType);
        }
        if (clazz != null) {
            // 由于 json 数据类型不确定，这里做 try-catch 处理
            try {
                data = gson.fromJson(str, clazz);
            } catch (Exception e) {
                data = gson.fromJson(str, (Type) TokenBean.class);
                e.printStackTrace();
            }
            // 可以将错误信息在 onError 中获取到
            // https://github.com/jeasonlzy/okhttp-OkGo/wiki/Callback#callback%E4%BB%8B%E7%BB%8D
        }
        return data;
    }
}
