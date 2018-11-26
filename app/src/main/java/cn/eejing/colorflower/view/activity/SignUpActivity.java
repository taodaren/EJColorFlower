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
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.SEND_MSG_FLAG_REGISTER;

/**
 * 注册
 */

public class SignUpActivity extends BaseActivity {
    private static final String TAG = "SignUpActivity";

    @BindView(R.id.et_register_phone)              EditText    mPhone;
    @BindView(R.id.et_register_set_pwd)            EditText    mSetPwd;
    @BindView(R.id.et_register_confirm_pwd)        EditText    mConfirmPwd;
    @BindView(R.id.et_register_verify_code)        EditText    mVerifyCode;
    @BindView(R.id.btn_register_get_code)          Button      btnGetCode;
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
                signUp();
                break;
        }
    }

    public void signUp() {
        String info = validate();
        if (!info.equals("验证通过")) {
            ToastUtil.showLong(info);
            btnRegister.setEnabled(true);
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

    public String validate() {
        String mobile = mPhone.getText().toString();
        String pwd = mSetPwd.getText().toString();
        String confirmPwd = mConfirmPwd.getText().toString();
        String code = mVerifyCode.getText().toString();

        if (mobile.isEmpty()) {
            return "手机号码不能为空";
        }

        if (pwd.isEmpty()) {
            return "设置密码不能为空";
        }

        if (confirmPwd.isEmpty()) {
            return "确认密码不能为空";
        }

        if (code.isEmpty()) {
            return "验证码不能为空";
        }

        if (pwd.length() < 6 || pwd.length() > 18) {
            return "请设置6至18个字母数字字符";
        }

        if (confirmPwd.length() < 6 || confirmPwd.length() > 18) {
            return "请设置6至18个字母数字字符";
        }

        if (Integer.parseInt(mSetPwd.getText().toString()) != Integer.parseInt(mConfirmPwd.getText().toString())) {
            return "设置密码与确认密码不一致";
        }

        return "验证通过";
    }

    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(mPhone.getText().toString(), mIv);

            OkGo.<String>post(Urls.SEND_MSG)
                    .tag(this)
                    .params("mobile", encryptPhone)
                    .params("iv", mIv)
                    .params("flag", SEND_MSG_FLAG_REGISTER)
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

    private void getDateWithRegister(final ProgressDialog dialog) {
        try {
            String encryptSetPwd = Encryption.encrypt(mSetPwd.getText().toString(), mIv);

            OkGo.<String>post(Urls.REGISTER)
                    .tag(this)
                    .params("mobile", mPhone.getText().toString())
                    .params("code", mVerifyCode.getText().toString())
                    .params("password", encryptSetPwd)
                    .params("iv", mIv)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            LogUtil.d(TAG, "普通用户注册 请求成功: " + body);

                            mGson = new Gson();
                            CodeMsgBean bean = mGson.fromJson(body, CodeMsgBean.class);

                            switch (bean.getCode()) {
                                case 1:
                                    ToastUtil.showShort("账号注册成功");
                                    setResult(RESULT_OK, new Intent()
                                            .putExtra("register_phone", mPhone.getText().toString())
                                            .putExtra("register_pwd", mSetPwd.getText().toString())
                                    );
                                    finish();
                                    break;
                                default:
                                    ToastUtil.showShort(bean.getMessage());
                                    break;
                            }
                            btnRegister.setEnabled(true);
                            dialog.dismiss();

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
