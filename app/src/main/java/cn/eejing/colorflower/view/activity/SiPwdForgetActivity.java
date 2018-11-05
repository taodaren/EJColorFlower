package cn.eejing.colorflower.view.activity;

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
import cn.eejing.colorflower.model.request.PwdFindBean;
import cn.eejing.colorflower.model.request.SendMsgBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 忘记密码
 */

public class SiPwdForgetActivity extends BaseActivity {

    @BindView(R.id.et_forget_phone)              EditText etForgetPhone;
    @BindView(R.id.et_forget_set_pwd)            EditText etForgetSetPwd;
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
        etForgetPhone.setText(mobile);

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
                getDataWithPwdFind();
                break;
        }
    }

    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(etForgetPhone.getText().toString(), mIv);

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

    private void getDataWithPwdFind() {
        try {
            String encryptPwd = Encryption.encrypt(etForgetSetPwd.getText().toString(), mIv);
            String code = etForgetVerifyCode.getText().toString();

            OkGo.<String>post(Urls.PWD_FIND)
                    .tag(this)
                    .params("mobile", etForgetPhone.getText().toString())
                    .params("password", encryptPwd)
                    .params("re_password", encryptPwd)
                    .params("code", code)
                    .params("iv", mIv)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            LogUtil.e(AppConstant.TAG, "password find request succeeded--->" + body);

                            mGson = new Gson();
                            PwdFindBean bean = mGson.fromJson(body, PwdFindBean.class);

                            switch (bean.getCode()) {
                                case 0:
                                    ToastUtil.showShort("重置密码失败");
                                    break;
                                case 1:
                                    ToastUtil.showShort("重置密码成功");
                                    setResult(RESULT_OK, new Intent()
                                            .putExtra("forget_phone", etForgetPhone.getText().toString())
                                    );
                                    finish();
                                    break;
                                case 2:
                                    ToastUtil.showShort("手机号码不能为空");
                                    break;
                                case 3:
                                    ToastUtil.showShort("密码不能为空");
                                    break;
                                case 4:
                                    ToastUtil.showShort("验证码不能为空");
                                    break;
                                case 5:
                                    ToastUtil.showShort("手机号码格式不正确");
                                    break;
                                case 6:
                                    ToastUtil.showShort("验证码不正确");
                                    break;
                                case 7:
                                    ToastUtil.showShort("确认密码不能为空");
                                    break;
                                case 8:
                                    ToastUtil.showShort("两次输入密码不一致");
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
