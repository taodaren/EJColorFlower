package cn.eejing.ejcolorflower.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
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

import java.nio.charset.Charset;

import butterknife.BindView;
import cn.eejing.ejcolorflower.LoginSession;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.MainActivity;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.LoginBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.AESUtils;
import cn.eejing.ejcolorflower.util.Settings;

public class LoginActivity extends BaseActivity {

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
    private String mPhone;
    private String mPassword;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        LoginSession session = Settings.getLoginSessionInfo(this);
        mPhone = session.getUsername();
        mPassword = session.getPassword();
        if (mPhone != null) {
            etLoginPhone.setText(mPhone);
        }
        if (mPassword != null) {
            etLoginPwd.setText(mPassword);
        }
    }

    @Override
    public void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        tvLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToActivity(RegisterActivity.class);
            }
        });
        tvLoginForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToActivity(ForgetPwdActivity.class);
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

        getDataWithLogin(progressDialog, phone, password);
    }

    private void getDataWithLogin(final ProgressDialog dialog, String phone, String password) {
        OkGo.<String>post(Urls.LOGIN)
                .tag(this)
                .params("mobile", phone)
                .params("password", password)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "login request succeeded--->" + body);


                        Gson gson = new Gson();
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
