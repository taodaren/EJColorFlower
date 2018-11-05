package cn.eejing.colorflower.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.AppConstant;
import cn.eejing.colorflower.model.request.RegisterBean;
import cn.eejing.colorflower.model.request.SendMsgBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 注册
 */

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.et_register_phone)              EditText mPhone;
    @BindView(R.id.et_register_set_pwd)            EditText mSetPwd;
    @BindView(R.id.et_register_confirm_pwd)        EditText mConfirmPwd;
    @BindView(R.id.et_register_verify_code)        EditText mVerifyCode;
    @BindView(R.id.btn_register_get_code)          Button btnGetCode;
    @BindView(R.id.btn_register_register)          SuperButton btnRegister;

    private Gson mGson;
    private String mIv;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_sign_up;
    }

    @Override
    public void initView() {
        mIv = Encryption.newIv();
    }

    @OnClick({R.id.img_back_sign_up, R.id.btn_register_get_code, R.id.btn_register_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_sign_up:
                finish();
                break;
            case R.id.btn_register_get_code:
                getDateWithSendMsg();
                break;
            case R.id.btn_register_register:
                signup();
                break;
        }
    }

    private void getDateWithRegister(final ProgressDialog dialog) {
        try {
            String encryptSetPwd = Encryption.encrypt(mSetPwd.getText().toString(), mIv);
            String encryptConfirmPwd = Encryption.encrypt(mConfirmPwd.getText().toString(), mIv);

            OkGo.<String>post(Urls.REGISTER)
                    .tag(this)
                    .params("mobile", mPhone.getText().toString())
                    .params("password", encryptSetPwd)
                    .params("re_password", encryptConfirmPwd)
                    .params("code", mVerifyCode.getText().toString())
                    .params("iv", mIv)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            LogUtil.e(AppConstant.TAG, "register request succeeded--->" + body);

                            mGson = new Gson();
                            RegisterBean bean = mGson.fromJson(body, RegisterBean.class);

                            switch (bean.getCode()) {
                                case 1:
                                    ToastUtil.showShort("账号注册成功");
                                    onSignupSuccess();
                                    dialog.dismiss();
                                    break;
                                case 9:
                                    ToastUtil.showShort("该手机号已注册");
                                    btnRegister.setEnabled(true);
                                    dialog.dismiss();
                                    break;
                                case 5:
                                    ToastUtil.showShort("手机号格式不正确");
                                    btnRegister.setEnabled(true);
                                    dialog.dismiss();
                                    break;
                                case 8:
                                    ToastUtil.showShort("两次输入密码不一致");
                                    btnRegister.setEnabled(true);
                                    dialog.dismiss();
                                case 6:
                                    ToastUtil.showShort("验证码不正确");
                                    btnRegister.setEnabled(true);
                                    dialog.dismiss();
                                    break;
                                default:
                                    onSignupFailed();
                                    dialog.dismiss();
                                    break;
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(mPhone.getText().toString(), mIv);

            OkGo.<String>post(Urls.SEND_MSG)
                    .tag(this)
                    .params("mobile", encryptPhone)
                    .params("iv", mIv)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            LogUtil.e(AppConstant.TAG, "send msg request succeeded--->" + body);

                            mGson = new Gson();
                            SendMsgBean bean = mGson.fromJson(body, SendMsgBean.class);
                            switch (bean.getCode()) {
                                case 1:
                                    ToastUtil.showShort("验证码发送成功");
                                    break;
                                case 2:
                                    ToastUtil.showShort("手机号码不能为空");
                                    break;
                                case 3:
                                    ToastUtil.showShort("当天只能发送 5 条信息");
                                    break;
                                case 4:
                                    ToastUtil.showShort("请 5 分钟后再发送");
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        btnRegister.setEnabled(false);

        final ProgressDialog dialog = new ProgressDialog(this,
                ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setMessage("创建账号中...");
        dialog.show();

        getDateWithRegister(dialog);
    }

    public void onSignupSuccess() {
        btnRegister.setEnabled(true);
        setResult(RESULT_OK, new Intent()
                .putExtra("register_phone", mPhone.getText().toString())
                .putExtra("register_pwd", mSetPwd.getText().toString())
        );
        finish();
    }

    public void onSignupFailed() {
        ToastUtil.showLong("注册失败");
        btnRegister.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String mobile = mPhone.getText().toString();
        String pwd = mSetPwd.getText().toString();

        if (mobile.isEmpty() || mobile.length() < 3) {
            mPhone.setError("至少3个字符");
            valid = false;
        } else {
            mPhone.setError(null);
        }

        if (pwd.isEmpty() || pwd.length() < 4 || pwd.length() > 18) {
            mPhone.setError("4至18个字母数字字符");
            valid = false;
        } else {
            mSetPwd.setError(null);
        }

        return valid;
    }

}
