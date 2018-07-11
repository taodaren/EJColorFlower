package cn.eejing.ejcolorflower.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.LoginBean;
import cn.eejing.ejcolorflower.model.session.LoginSession;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Encryption;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 登录
 */

public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.et_login_phone)       EditText etLoginPhone;
    @BindView(R.id.et_login_pwd)         EditText etLoginPwd;
    @BindView(R.id.btn_login)            SuperButton btnLogin;
    @BindView(R.id.tv_login_register)    TextView tvLoginRegister;
    @BindView(R.id.tv_login_forget)      TextView tvLoginForget;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_sign_in;
    }

    @Override
    public void initView() {
        LoginSession session = Settings.getLoginSessionInfo(this);
        String mPhone = session.getUsername();
        String mPassword = session.getPassword();
        if (mPhone != null) {
            etLoginPhone.setText(mPhone);
        }
        if (mPassword != null) {
            etLoginPwd.setText(mPassword);
        }
    }

    @Override
    public void initListener() {
        btnLogin.setOnClickListener(this);
        tvLoginRegister.setOnClickListener(this);
        tvLoginForget.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // 默认情况下，我们只需完成活动并自动登录它们
                this.finish();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_login_register:
                jumpToActivity(SignUpActivity.class);
                break;
            case R.id.tv_login_forget:
                Intent intent = new Intent(SignInActivity.this, SiPwdForgetActivity.class);
                intent.putExtra("mobile", etLoginPhone.getText().toString());
                jumpToActivity(intent);
                break;
            default:
                break;
        }
    }

    private void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this,
                ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        String phone = etLoginPhone.getText().toString();
        String password = etLoginPwd.getText().toString();

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
                        Log.e(AppConstant.TAG, "login request succeeded--->" + body);

                        Gson gson = new Gson();
                        LoginBean bean = gson.fromJson(body, LoginBean.class);

                        switch (bean.getCode()) {
                            case 0:
                                onLoginFailed();
                                dialog.dismiss();
                                break;
                            case 1:
                                Settings.storeSessionInfo(SignInActivity.this, new LoginSession(
                                        etLoginPhone.getText().toString(),
                                        etLoginPwd.getText().toString(),
                                        bean.getData().getMember_id(),
                                        bean.getData().getToken()
                                ));
                                jumpToActivity(AppActivity.class);
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
                        onLoginFailed();
                        dialog.dismiss();
                    }
                });
    }

    public void onLoginSuccess() {
        btnLogin.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    public void onInputError() {
        Toast.makeText(getBaseContext(), "手机号码或密码不正确", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String phone = etLoginPhone.getText().toString();
        String password = etLoginPwd.getText().toString();

        if (phone.isEmpty() || phone.length() != 11) {
            etLoginPhone.setError("请输入一个有效的手机号码");
            valid = false;
        } else {
            etLoginPhone.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etLoginPwd.setError("4至10个字母数字字符");
            valid = false;
        } else {
            etLoginPwd.setError(null);
        }

        return valid;
    }

}
