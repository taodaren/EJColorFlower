package cn.eejing.colorflower.view.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MyCountDownTimer;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.SEND_MSG_FLAG_PAY;
import static cn.eejing.colorflower.app.AppConstant.SMS_RESEND_TIME;

/**
 * 设置支付密码
 */

public class MiSetPayPwdActivity extends BaseActivity {
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
    private Gson mGson;

    // 监听设置相关 EditText 文本
    private TextWatcher twListenET = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
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
    };

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_set_pay_pwd;
    }

    @Override
    public void initView() {
        setToolbar("设置支付密码", View.VISIBLE, null, View.GONE);
        layoutSet.setVisibility(View.GONE);
        layoutVerify.setVisibility(View.VISIBLE);
        mPwdOriginal = MySettings.getLoginSessionInfo(this).getPassword();
        mIv = Encryption.newIv();
    }

    @Override
    public void initListener() {
        etPwdLogin.addTextChangedListener(twListenET);
        etPhone.addTextChangedListener(twListenET);
        etCode.addTextChangedListener(twListenET);
        etPwdNew.addTextChangedListener(twListenET);
        etPwdConfirm.addTextChangedListener(twListenET);
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
                    etPhone.setText(MySettings.getLoginSessionInfo(MiSetPayPwdActivity.this).getUsername());
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

    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(etPhone.getText().toString(), mIv);

            OkGo.<String>post(Urls.SEND_MSG)
                    .tag(this)
                    .params("mobile", encryptPhone)
                    .params("iv", mIv)
                    .params("flag", SEND_MSG_FLAG_PAY)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            LogUtil.d(TAG, "发送短信 请求成功: " + body);

                            mGson = new Gson();
                            CodeMsgBean bean = mGson.fromJson(body, CodeMsgBean.class);
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
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDateWithSetPayPwd() {
        OkGo.<String>post(Urls.SET_PAY_PWD)
                .tag(this)
                .params("code", mCode)
                .params("mobile", mPhone)
                .params("pay_password", mSetPwd)
                .params("verify_pay_password", mPwdConfirm)
                .params("iv", mIv)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(TAG, "设置支付密码 请求成功: " + body);

                        mGson = new Gson();
                        CodeMsgBean bean = mGson.fromJson(body, CodeMsgBean.class);
                        ToastUtil.showLong(bean.getMessage());
                        finish();
                    }
                });
    }

}
