package cn.eejing.colorflower.model.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

/**
 * 网络请求前显示一个 dialog,请求结束后取消 loading
 */

public abstract class DialogCallback<T> extends JsonCallback<T> {

    private ProgressDialog mProgressDialog;

    public DialogCallback(Activity activity, Class<T> tClass) {
        super(tClass);
        initDialog(activity);
    }

    private void initDialog(Activity activity) {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("网络请求中");
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            try {
                mProgressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFinish() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSuccess(Response<T> response) {
    }
}