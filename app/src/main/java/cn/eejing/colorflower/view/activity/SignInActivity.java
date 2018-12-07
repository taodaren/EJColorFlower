package cn.eejing.colorflower.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allen.library.SuperButton;
import com.lzy.okgo.model.HttpParams;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.LoginBean;
import cn.eejing.colorflower.model.session.LoginSession;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.NO_TOKEN;

/**
 * 登录
 */

public class SignInActivity extends BaseActivity {
    private static final String TAG = "SignInActivity";
    private static final int REQUEST_SIGN_UP    = 1;
    private static final int REQUEST_FORGET_PWD = 2;

    @BindView(R.id.et_login_phone)           EditText       etPhone;
    @BindView(R.id.et_login_pwd)             EditText       etPwd;
    @BindView(R.id.tv_login_register)        TextView       tvRegister;
    @BindView(R.id.tv_login_forget)          TextView       tvForgetPwd;
    @BindView(R.id.btn_login)                SuperButton    btnLogin;
    @BindView(R.id.layout_hide)              RelativeLayout layoutHide;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_sign_in;
    }

    @Override
    public void initView() {
        LoginSession session = MySettings.getLoginInfo(this);
        String phone = session.getUsername();
        String password = session.getPassword();
        if (phone != null) {
            etPhone.setText(phone);
        }
        if (password != null) {
            etPwd.setText(password);
        }
        if (phone != null && password != null) {
            // 如果存在手机号和密码，隐藏操作相关布局，延迟 1s 自动登陆
            layoutHide.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(this::login, 1000);
        } else {
            layoutHide.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SIGN_UP:
                    // 默认情况下，我们只需完成活动并自动登录它们
                    etPhone.setText(data.getStringExtra("register_phone"));
                    etPwd.setText(data.getStringExtra("register_pwd"));
                    login();
                    break;
                case REQUEST_FORGET_PWD:
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
                startActivityForResult(new Intent(SignInActivity.this, SignUpActivity.class), REQUEST_SIGN_UP);
                break;
            case R.id.tv_login_forget:
                startActivityForResult(new Intent(SignInActivity.this, SiPwdForgetActivity.class)
                        .putExtra("mobile", etPhone.getText().toString()), REQUEST_FORGET_PWD
                );
                break;
        }
    }

    private void login() {
        String info = validate();
        if (!info.equals("验证通过")) {
            onLoginFailed(info);
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog =
                new ProgressDialog(SignInActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);// 设置点击进度对话框外的区域对话框不消失
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

    @SuppressWarnings("unchecked")
    private void getDataWithLogin(final ProgressDialog dialog, String phone, String password, String iv) {
        OkGoBuilder.getInstance().setToken(NO_TOKEN);
        HttpParams params = new HttpParams();
        params.put("mobile", phone);
        params.put("password", password);
        params.put("iv", iv);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.LOGIN)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(LoginBean.class)
                .callback(new Callback<LoginBean>() {
                    @Override
                    public void onSuccess(LoginBean bean, int id) {
                        LogUtil.d(TAG, "用户登录 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                MySettings.saveLoginInfo(SignInActivity.this, new LoginSession(
                                        bean.getData().getToken(),
                                        bean.getData().getLevel(),
                                        bean.getData().getUser_id(),
                                        etPhone.getText().toString(),
                                        etPwd.getText().toString()
                                ));
                                jumpToActivity(MainActivity.class);
                                onLoginSuccess();
                                break;
                            default:
                                onLoginFailed(bean.getMessage());
                                break;
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                        onLoginFailed("出现异常，登陆失败");
                        dialog.dismiss();
                    }
                }).build();
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

    public String validate() {
        String phone = etPhone.getText().toString();
        String password = etPwd.getText().toString();

        if (phone.isEmpty()) {
            return "手机号码不能为空";
        }

        if (password.isEmpty()) {
            return "登陆密码不能为空";
        }

        if (phone.length() != 11) {
            return "请输入一个有效的手机号码";
        }

        if (password.length() < 6 || password.length() > 20) {
            return "请设置6至20个字母数字字符";
        }

        return "验证通过";
    }

}
