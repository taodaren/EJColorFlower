package cn.eejing.colorflower.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.allen.library.SuperButton;
import com.lzy.okgo.model.HttpParams;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.QueryDevMacBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.NO_TOKEN;
import static cn.eejing.colorflower.app.AppConstant.SEND_MSG_FLAG_REGISTER;

/**
 * 注册
 */

public class SignUpActivity extends BaseActivity {
    private static final String TAG = "SignUpActivity";

    @BindView(R.id.et_register_phone)              ClearableEditText mPhone;
    @BindView(R.id.et_register_set_pwd)            ClearableEditText mSetPwd;
    @BindView(R.id.et_register_confirm_pwd)        ClearableEditText mConfirmPwd;
    @BindView(R.id.et_register_verify_code)        ClearableEditText mVerifyCode;
    @BindView(R.id.btn_register_get_code)          Button            btnGetCode;
    @BindView(R.id.btn_register_register)          SuperButton       btnRegister;

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

    @SuppressWarnings("unchecked")
    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(mPhone.getText().toString(), mIv);

            OkGoBuilder.getInstance().setToken(NO_TOKEN);
            HttpParams params = new HttpParams();
            params.put("mobile", encryptPhone);
            params.put("iv", mIv);
            params.put("flag", SEND_MSG_FLAG_REGISTER);

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
    private void getDateWithRegister(final ProgressDialog dialog) {
        try {
            String encryptSetPwd = Encryption.encrypt(mSetPwd.getText().toString(), mIv);

            OkGoBuilder.getInstance().setToken(NO_TOKEN);
            HttpParams params = new HttpParams();
            params.put("mobile", mPhone.getText().toString());
            params.put("code", mVerifyCode.getText().toString());
            params.put("password", encryptSetPwd);
            params.put("iv", mIv);

            OkGoBuilder.getInstance().Builder(this)
                    .url(Urls.REGISTER)
                    .method(OkGoBuilder.POST)
                    .params(params)
                    .cls(QueryDevMacBean.class)
                    .callback(new Callback<QueryDevMacBean>() {
                        @Override
                        public void onSuccess(QueryDevMacBean bean, int id) {
                            LogUtil.d(TAG, "普通用户注册 请求成功");

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

                        @Override
                        public void onError(Throwable e, int id) {
                        }
                    }).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
