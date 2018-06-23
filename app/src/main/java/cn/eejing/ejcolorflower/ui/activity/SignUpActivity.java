package cn.eejing.ejcolorflower.ui.activity;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.model.request.RegisterBean;
import cn.eejing.ejcolorflower.model.request.SendMsgBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Encryption;

/**
 * 注册
 */

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_register_phone)
    EditText mPhone;
    @BindView(R.id.et_register_set_pwd)
    EditText mSetPwd;
    @BindView(R.id.et_register_confirm_pwd)
    EditText mConfirmPwd;
    @BindView(R.id.et_register_verify_code)
    EditText mVerifyCode;
    @BindView(R.id.btn_register_get_code)
    Button btnGetCode;
    @BindView(R.id.btn_register_register)
    SuperButton btnRegister;

    private Gson mGson;
    private String mIv;


    @Override
    protected int layoutViewId() {
        return R.layout.activity_sign_up;
    }

    @Override
    public void initView() {
        setToolbar("注册", View.VISIBLE);

        mIv = Encryption.newIv();
    }

    @Override
    public void initListener() {
        btnGetCode.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_get_code:
                getDateWithSendMsg();
                break;
            case R.id.btn_register_register:
                signup();
                break;
            default:
                break;
        }
    }

    private void getDateWithRegister(final ProgressDialog dialog) {
        OkGo.<String>post(Urls.REGISTER)
                .tag(this)
                .params("mobile", mPhone.getText().toString())
                .params("password", mSetPwd.getText().toString())
                .params("re_password", mSetPwd.getText().toString())
                .params("code", mVerifyCode.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "register request succeeded--->" + body);

                        mGson = new Gson();
                        RegisterBean bean = mGson.fromJson(body, RegisterBean.class);

                        switch (bean.getCode()) {
                            case 1:
                                onSignupSuccess();
                                dialog.dismiss();
                                break;
                            case 9:
                                Toast.makeText(getBaseContext(), "该手机号已注册", Toast.LENGTH_LONG).show();
                                btnRegister.setEnabled(true);
                                dialog.dismiss();
                                break;
                            case 5:
                                Toast.makeText(getBaseContext(), "手机号格式不正确", Toast.LENGTH_LONG).show();
                                btnRegister.setEnabled(true);
                                dialog.dismiss();
                                break;
                            case 8:
                                Toast.makeText(getBaseContext(), "两次输入密码不一致", Toast.LENGTH_LONG).show();
                                btnRegister.setEnabled(true);
                                dialog.dismiss();
                            case 6:
                                Toast.makeText(getBaseContext(), "验证码不正确", Toast.LENGTH_LONG).show();
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
                            Log.e(AppConstant.TAG, "send msg request succeeded--->" + body);

                            mGson = new Gson();
                            SendMsgBean bean = mGson.fromJson(body, SendMsgBean.class);
                            switch (bean.getCode()) {
                                case 1:
                                    Toast.makeText(getBaseContext(), "验证码发送成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(getBaseContext(), "手机号码不能为空", Toast.LENGTH_LONG).show();
                                    break;
                                case 3:
                                    Toast.makeText(getBaseContext(), "当天只能发送 5 条信息", Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    Toast.makeText(getBaseContext(), "请 5 分钟后再发送", Toast.LENGTH_SHORT).show();
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
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "注册失败", Toast.LENGTH_LONG).show();

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
