package cn.eejing.colorflower.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.AppConstant;
import cn.eejing.colorflower.model.request.LoginBean;
import cn.eejing.colorflower.model.session.LoginSession;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 登录
 */

public class SignInActivity extends BaseActivity {
    private static final String TAG = "SignInActivity";
    private static final int REQUEST_SIGNUP = 1;
    private static final int REQUEST_FORGET = 2;

    @BindView(R.id.et_login_phone)           EditText etPhone;
    @BindView(R.id.et_login_pwd)             EditText etPwd;
    @BindView(R.id.tv_login_register)        TextView tvRegister;
    @BindView(R.id.tv_login_forget)          TextView tvForgetPwd;
    @BindView(R.id.btn_login)                SuperButton btnLogin;
    @BindView(R.id.layout_hide)              LinearLayout layoutHide;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_sign_in;
    }

    @Override
    public void initView() {
        LoginSession session = MySettings.getLoginSessionInfo(this);
        String phone = session.getUsername();
        String password = session.getPassword();
        if (phone != null) {
            etPhone.setText(phone);
        }
        if (password != null) {
            etPwd.setText(password);
        }
//        if (phone != null && password != null) {
//            // 如果存在手机号和密码，隐藏操作相关布局，延迟 1s 自动登陆
//            layoutHide.setVisibility(View.INVISIBLE);
//            new Handler().postDelayed(this::login, 1000);
//        } else {
//            layoutHide.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SIGNUP:
                    // 默认情况下，我们只需完成活动并自动登录它们
                    etPhone.setText(data.getStringExtra("register_phone"));
                    etPwd.setText(data.getStringExtra("register_pwd"));
//                    login();
                    break;
                case REQUEST_FORGET:
                    etPhone.setText(data.getStringExtra("forget_phone"));
                    break;
            }
        }
    }

    @OnClick({R.id.btn_login, R.id.tv_login_register, R.id.tv_login_forget})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_login_register:
                startActivityForResult(new Intent(SignInActivity.this, SignUpActivity.class), REQUEST_SIGNUP);
                break;
            case R.id.tv_login_forget:
                startActivityForResult(new Intent(SignInActivity.this, SiPwdForgetActivity.class)
                        .putExtra("mobile", etPhone.getText().toString()), REQUEST_FORGET
                );
                break;
        }
    }

    private void login() {
        if (!validate()) {
            onLoginFailed("验证登陆失败");
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this,
                ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        String phone = etPhone.getText().toString();
        String password = etPwd.getText().toString();

        // 给密码加密
        String iv = Encryption.newIv();
        try {
            String pwd = Encryption.encrypt(password, iv);
            getDataWithLogin(progressDialog, phone, pwd, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getDataWithLogin(final ProgressDialog dialog, String phone, String password, String iv) {
        OkGo.<String>post(Urls.LOGIN)
                .tag(this)
                .params("mobile", phone)
                .params("password", password)
                .params("iv", iv)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.e(AppConstant.TAG, "login request succeeded--->" + body);

                        Gson gson = new Gson();
                        LoginBean bean = gson.fromJson(body, LoginBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                onLoginFailed("登陆失败");
                                dialog.dismiss();
                                break;
                            case 1:
                                MySettings.storeSessionInfo(SignInActivity.this, new LoginSession(
                                        etPhone.getText().toString(),
                                        etPwd.getText().toString(),
                                        bean.getData().getMember_id(),
                                        bean.getData().getToken()
                                ));
                                jumpToActivity(MainActivity.class);
                                onLoginSuccess();
                                dialog.dismiss();
                                break;
                            case 4:
                                // 如果帐号或密码输入错误（返回码为4）重新输入
                                onInputError();
                                dialog.dismiss();
                                break;
                            default:
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        onLoginFailed("出现异常，登陆失败");
                        dialog.dismiss();
                    }
                });
    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        finish();
    }

    public void onLoginFailed(String info) {
        ToastUtil.showLong(info);
        btnLogin.setEnabled(true);
        layoutHide.setVisibility(View.VISIBLE);
    }

    public void onInputError() {
        ToastUtil.showShort("手机号码或密码不正确，请检查信息");
        layoutHide.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String phone = etPhone.getText().toString();
        String password = etPwd.getText().toString();

        if (phone.isEmpty() || phone.length() != 11) {
            etPhone.setError("请输入一个有效的手机号码");
            valid = false;
        } else {
            etPhone.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPwd.setError("4至10个字母数字字符");
            valid = false;
        } else {
            etPwd.setError(null);
        }

        return valid;
    }

}
