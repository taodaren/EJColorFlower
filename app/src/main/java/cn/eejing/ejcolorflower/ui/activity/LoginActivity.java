package cn.eejing.ejcolorflower.ui.activity;

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
import cn.eejing.ejcolorflower.LoginSession;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.MainActivity;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.LoginBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Encryption;
import cn.eejing.ejcolorflower.util.Settings;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_login_phone)
    EditText etLoginPhone;
    @BindView(R.id.et_login_pwd)
    EditText etLoginPwd;
    @BindView(R.id.btn_login)
    SuperButton btnLogin;
    @BindView(R.id.tv_login_register)
    TextView tvLoginRegister;
    @BindView(R.id.tv_login_forget)
    TextView tvLoginForget;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_login;
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

        // TODO: 2018/5/21 测试使用 长按跳转到 MainActivity 完成注册登录功能后删除
        tvLoginForget.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                jumpToActivity(MainActivity.class);
                finish();
                return true;
            }
        });
    }

    private void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String phone = etLoginPwd.getText().toString();
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
                        if (gson.fromJson(body, LoginBean.class).getCode() == 4) {
                            // 如果帐号或密码输入错误（返回码为4）重新输入
                            onInputError();
                            dialog.dismiss();
                        } else {
                            LoginBean.LoginData bean = gson.fromJson(body, LoginBean.LoginData.class);
                            Settings.storeSessionInfo(LoginActivity.this, new LoginSession(
                                    etLoginPhone.getText().toString(),
                                    etLoginPwd.getText().toString(),
                                    bean.getMember_id(),
                                    bean.getToken()
                            ));
                            jumpToActivity(MainActivity.class);
                            onLoginSuccess();
                            dialog.dismiss();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_login_register:
                jumpToActivity(RegisterActivity.class);
                break;
            case R.id.tv_login_forget:
                Intent intent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
                intent.putExtra("mobile", etLoginPhone.getText().toString());
                jumpToActivity(intent);
                break;
            default:
                break;
        }
    }

}
