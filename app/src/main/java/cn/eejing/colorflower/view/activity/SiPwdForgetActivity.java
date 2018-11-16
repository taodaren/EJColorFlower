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
import cn.eejing.colorflower.model.request.PwdFindBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.SEND_MSG_FLAG_FORGET;

/**
 * 忘记密码
 */

public class SiPwdForgetActivity extends BaseActivity {
    private static final String TAG = "SiPwdForgetActivity";

    @BindView(R.id.et_forget_phone)              EditText etPhone;
    @BindView(R.id.et_forget_set_pwd)            EditText etSetPwd;
    @BindView(R.id.et_forget_confirm_pwd)        EditText etForgetConfirmPwd;
    @BindView(R.id.et_forget_verify_code)        EditText etForgetVerifyCode;
    @BindView(R.id.btn_forget_get_code)          Button btnForgetGetCode;
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
        String confirmPwd = etForgetConfirmPwd.getText().toString();
        String code = etForgetVerifyCode.getText().toString();

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

        if (pwd.length() < 4 || pwd.length() > 18) {
            return "4至18个字母数字字符";
        }

        if (confirmPwd.length() < 4 || confirmPwd.length() > 18) {
            return "4至18个字母数字字符";
        }

        if (Integer.parseInt(etSetPwd.getText().toString()) != Integer.parseInt(etForgetConfirmPwd.getText().toString())) {
            return "设置密码与确认密码不一致";
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
                    .params("flag", SEND_MSG_FLAG_FORGET)
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

    private void getDataWithPwdFind(ProgressDialog dialog) {
        try {
            String encryptPwd = Encryption.encrypt(etSetPwd.getText().toString(), mIv);
            String code = etForgetVerifyCode.getText().toString();

            OkGo.<String>post(Urls.CHANGE_PWD)
                    .tag(this)
                    .params("mobile", etPhone.getText().toString())
                    .params("code", code)
                    .params("password", encryptPwd)
                    .params("iv", mIv)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            LogUtil.d(TAG, "修改密码 请求成功: " + body);

                            mGson = new Gson();
                            PwdFindBean bean = mGson.fromJson(body, PwdFindBean.class);

                            switch (bean.getCode()) {
                                case 0:
                                    ToastUtil.showShort("重置密码失败");
                                    break;
                                case 1:
                                    ToastUtil.showShort("重置密码成功");
                                    setResult(RESULT_OK, new Intent()
                                            .putExtra("forget_phone", etPhone.getText().toString())
                                    );
                                    finish();
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
