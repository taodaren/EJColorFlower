package cn.eejing.colorflower.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.model.HttpParams;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.NO_TOKEN;
import static cn.eejing.colorflower.app.AppConstant.SEND_MSG_FLAG_FORGET;

/**
 * 忘记密码
 */

public class SiPwdForgetActivity extends BaseActivity {
    private static final String TAG = "SiPwdForgetActivity";

    @BindView(R.id.et_forget_phone)              EditText    etPhone;
    @BindView(R.id.et_forget_set_pwd)            EditText    etSetPwd;
    @BindView(R.id.et_forget_confirm_pwd)        EditText    etConfirmPwd;
    @BindView(R.id.et_forget_verify_code)        EditText    etVerifyCode;
    @BindView(R.id.btn_forget_get_code)          Button      btnForgetGetCode;
    @BindView(R.id.btn_reset_pwd)                SuperButton btnResetPwd;

    private Gson mGson;
    private String mIv;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_si_pwd_forget;
    }

    @Override
    public void initView() {
        String mobile = getIntent().getStringExtra("mobile");
        etPhone.setText(mobile);

        mIv = Encryption.newIv();
    }

    @OnClick({R.id.img_back_pwd_forget, R.id.btn_forget_get_code, R.id.btn_reset_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_pwd_forget:
                finish();
                break;
            case R.id.btn_forget_get_code:
                getDateWithSendMsg();
                break;
            case R.id.btn_reset_pwd:
                resetPwd();
                break;
        }
    }

    public void resetPwd() {
        String info = validate();
        if (!info.equals("验证通过")) {
            ToastUtil.showLong(info);
            btnResetPwd.setEnabled(true);
            return;
        }

        btnResetPwd.setEnabled(false);

        final ProgressDialog dialog = new ProgressDialog(this,
                ProgressDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setMessage("密码重置中...");
        dialog.show();

        getDataWithPwdFind(dialog);
    }

    public String validate() {
        String mobile = etPhone.getText().toString();
        String pwd = etSetPwd.getText().toString();
        String confirmPwd = etConfirmPwd.getText().toString();
        String code = etVerifyCode.getText().toString();

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

        if (Integer.parseInt(etSetPwd.getText().toString()) != Integer.parseInt(etConfirmPwd.getText().toString())) {
            return "设置密码与确认密码不一致";
        }

        return "验证通过";
    }

    @SuppressWarnings("unchecked")
    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(etPhone.getText().toString(), mIv);

            OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
            HttpParams params = new HttpParams();
            params.put("mobile", encryptPhone);
            params.put("iv", mIv);
            params.put("flag", SEND_MSG_FLAG_FORGET);

            OkGoBuilder.getInstance().Builder(this)
                    .url(Urls.GET_DEVICE_MAC)
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
    private void getDataWithPwdFind(ProgressDialog dialog) {
        try {
            String encryptPwd = Encryption.encrypt(etSetPwd.getText().toString(), mIv);
            String code = etVerifyCode.getText().toString();

            OkGoBuilder.getInstance().setToken(NO_TOKEN);
            HttpParams params = new HttpParams();
            params.put("mobile", etPhone.getText().toString());
            params.put("code", code);
            params.put("password", encryptPwd);
            params.put("iv", mIv);

            OkGoBuilder.getInstance().Builder(this)
                    .url(Urls.CHANGE_PWD)
                    .method(OkGoBuilder.POST)
                    .params(params)
                    .cls(CodeMsgBean.class)
                    .callback(new Callback<CodeMsgBean>() {
                        @Override
                        public void onSuccess(CodeMsgBean bean, int id) {
                            LogUtil.d(TAG, "修改密码 请求成功");

                            switch (bean.getCode()) {
                                case 1:
                                    ToastUtil.showShort("重置密码成功");
                                    setResult(RESULT_OK, new Intent()
                                            .putExtra("forget_phone", etPhone.getText().toString())
                                    );
                                    finish();
                                    break;
                                default:
                                    ToastUtil.showShort(bean.getMessage());
                                    break;
                            }
                            btnResetPwd.setEnabled(true);
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
