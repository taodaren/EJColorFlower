package cn.eejing.colorflower.view.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MyCountDownTimer;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.NO_TOKEN;
import static cn.eejing.colorflower.app.AppConstant.SEND_MSG_FLAG_PAY;
import static cn.eejing.colorflower.app.AppConstant.SMS_RESEND_TIME;

/**
 * 设置支付密码
 */

public class MiSetPayPwdActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.layout_pay_pwd_verify)        LinearLayout      layoutVerify;
    @BindView(R.id.layout_pay_pwd_set)           LinearLayout      layoutSet;
    @BindView(R.id.et_pay_pwd_login)             ClearableEditText etPwdLogin;
    @BindView(R.id.et_pay_pwd_phone)             ClearableEditText etPhone;
    @BindView(R.id.et_pay_pwd_code)              ClearableEditText etCode;
    @BindView(R.id.et_pay_pwd_new)               ClearableEditText etPwdNew;
    @BindView(R.id.et_pay_pwd_confirm)           ClearableEditText etPwdConfirm;
    @BindView(R.id.btn_pay_pwd_confirm_login)    Button            btnConfirmLogin;
    @BindView(R.id.btn_pay_pwd_confirm_set)      Button            btnConfirmSet;
    @BindView(R.id.tv_pay_get_code)              TextView          tvCode;

    private static final String TAG = "MiSetPayPwdActivity";
    private String mPwdOriginal, mPhone, mCode, mSetPwd, mPwdConfirm;
    private String mIv;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_set_pay_pwd;
    }

    @Override
    public void initView() {
        setToolbar("设置支付密码", View.VISIBLE, null, View.GONE);
        layoutSet.setVisibility(View.GONE);
        layoutVerify.setVisibility(View.VISIBLE);
        mPwdOriginal = MySettings.getLoginInfo(this).getPassword();
        mIv = Encryption.newIv();
    }

    @Override
    public void initListener() {
        etPwdLogin.addTextChangedListener(this);
        etPhone.addTextChangedListener(this);
        etCode.addTextChangedListener(this);
        etPwdNew.addTextChangedListener(this);
        etPwdConfirm.addTextChangedListener(this);
    }

    @OnClick({R.id.btn_pay_pwd_confirm_set, R.id.btn_pay_pwd_confirm_login, R.id.tv_pay_get_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pay_pwd_confirm_set:
                if (!validate().equals("验证通过")) {
                    ToastUtil.showLong(validate());
                    return;
                }
                getDateWithSetPayPwd();
                break;
            case R.id.btn_pay_pwd_confirm_login:
                String pwdLogin = etPwdLogin.getText().toString().trim();

                if (!pwdLogin.equals(mPwdOriginal) || TextUtils.isEmpty(pwdLogin)) {
                    ToastUtil.showShort("登陆密码验证失败");
                    layoutSet.setVisibility(View.GONE);
                    layoutVerify.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.showShort("登陆密码验证成功");
                    etPhone.setText(MySettings.getLoginInfo(MiSetPayPwdActivity.this).getUsername());
                    layoutSet.setVisibility(View.VISIBLE);
                    layoutVerify.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_pay_get_code:
                getDateWithSendMsg();
                break;
        }
    }

    public String validate() {
        if (mPhone.length() != 11) {
            return "请输入一个有效的手机号码";
        }
        if (mSetPwd.length() != 6) {
            return "请设置6位数支付密码";
        }
        if (!mSetPwd.equals(mPwdConfirm)) {
            return "两次输入密码不一致";
        }
        return "验证通过";
    }

    @SuppressWarnings("unchecked")
    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(etPhone.getText().toString(), mIv);

            OkGoBuilder.getInstance().setToken(NO_TOKEN);
            HttpParams params = new HttpParams();
            params.put("mobile", encryptPhone);
            params.put("iv", mIv);
            params.put("flag", SEND_MSG_FLAG_PAY);

            OkGoBuilder.getInstance().Builder(this)
                    .url(Urls.SEND_MSG)
                    .method(OkGoBuilder.POST)
                    .params(params)
                    .cls(CodeMsgBean.class)
                    .callback(new Callback<CodeMsgBean>() {
                        @Override
                        public void onSuccess(CodeMsgBean bean, int id) {
                            LogUtil.d(TAG, "发送短信 请求成功");

                            switch (bean.getCode()) {
                                case 1:
                                    ToastUtil.showShort("验证码发送成功");
                                    new MyCountDownTimer(
                                            MiSetPayPwdActivity.this,
                                            tvCode,
                                            SMS_RESEND_TIME,
                                            1000,
                                            R.drawable.shape_edit_code_select,
                                            R.drawable.shape_edit_code
                                    ).start();
                                    break;
                                default:
                                    ToastUtil.showShort(bean.getMessage());
                                    break;
                            }
                        }

                        @Override
                        public void onError(Throwable e, int id) {
                        }
                    }).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void getDateWithSetPayPwd() {
        try {
            String encryptSetPwd = Encryption.encrypt(mSetPwd, mIv);
            String encryptPwdConfirm = Encryption.encrypt(mPwdConfirm, mIv);

            OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
            HttpParams params = new HttpParams();
            params.put("code", mCode);
            params.put("mobile", mPhone);
            params.put("pay_password", encryptSetPwd);
            params.put("verify_pay_password", encryptPwdConfirm);
            params.put("iv", mIv);

            OkGoBuilder.getInstance().Builder(this)
                    .url(Urls.SET_PAY_PWD)
                    .method(OkGoBuilder.POST)
                    .params(params)
                    .cls(CodeMsgBean.class)
                    .callback(new Callback<CodeMsgBean>() {
                        @Override
                        public void onSuccess(CodeMsgBean bean, int id) {
                            LogUtil.d(TAG, "设置支付密码 请求成功");

                            ToastUtil.showLong(bean.getMessage());
                            finish();
                        }

                        @Override
                        public void onError(Throwable e, int id) {
                        }
                    }).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mPhone = etPhone.getText().toString().trim();
        mCode = etCode.getText().toString().trim();
        mSetPwd = etPwdNew.getText().toString().trim();
        mPwdConfirm = etPwdConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(mPhone) || TextUtils.isEmpty(mCode) || TextUtils.isEmpty(mSetPwd) || TextUtils.isEmpty(mPwdConfirm)) {
            // 设置支付密码 ET 有空情况
            btnConfirmSet.setEnabled(Boolean.FALSE);
            btnConfirmSet.setBackground(getResources().getDrawable(R.drawable.shape_btn_jbs_no));
        } else {
            // ET 同时不为空的情况
            btnConfirmSet.setEnabled(Boolean.TRUE);
            btnConfirmSet.setBackground(getResources().getDrawable(R.drawable.shape_btn_jbs));
        }
    }
}
