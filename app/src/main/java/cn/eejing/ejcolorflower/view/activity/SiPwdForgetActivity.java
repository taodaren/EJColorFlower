package cn.eejing.ejcolorflower.view.activity;

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
import cn.eejing.ejcolorflower.model.request.PwdFindBean;
import cn.eejing.ejcolorflower.model.request.SendMsgBean;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Encryption;

/**
 * 忘记密码
 */

public class SiPwdForgetActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_forget_phone)              EditText etForgetPhone;
    @BindView(R.id.et_forget_set_pwd)            EditText etForgetSetPwd;
    @BindView(R.id.et_forget_confirm_pwd)        EditText etForgetConfirmPwd;
    @BindView(R.id.et_forget_verify_code)        EditText etForgetVerifyCode;
    @BindView(R.id.btn_forget_get_code)          Button btnForgetGetCode;
    @BindView(R.id.btn_reset_pwd)                SuperButton btnResetPwd;

    private Gson mGson;
    private String mMobile, mIv;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_si_pwd_forget;
    }

    @Override
    public void initView() {
        setToolbar("忘记密码", View.VISIBLE);
        String mobile = getIntent().getStringExtra("mobile");
        etForgetPhone.setText(mobile);
        mMobile = etForgetPhone.getText().toString();

        mIv = Encryption.newIv();
    }

    @Override
    public void initListener() {
        btnForgetGetCode.setOnClickListener(this);
        btnResetPwd.setOnClickListener(this);
    }

    private void getDateWithSendMsg() {
        try {
            String encryptPhone = Encryption.encrypt(mMobile, mIv);

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

    private void getDataWithPwdFind() {
        try {
            String encryptPwd = Encryption.encrypt(etForgetSetPwd.getText().toString(), mIv);
            String code = etForgetVerifyCode.getText().toString();

            OkGo.<String>post(Urls.PWD_FIND)
                    .tag(this)
                    .params("mobile", mMobile)
                    .params("password", encryptPwd)
                    .params("re_password", encryptPwd)
                    .params("code", code)
                    .params("iv", mIv)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            Log.e(AppConstant.TAG, "password find request succeeded--->" + body);

                            mGson = new Gson();
                            PwdFindBean bean = mGson.fromJson(body, PwdFindBean.class);

                            switch (bean.getCode()) {
                                case 0:
                                    Toast.makeText(getBaseContext(), "重置密码失败", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    finish();
                                    Toast.makeText(getBaseContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    Toast.makeText(getBaseContext(), "手机号码不能为空", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(getBaseContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    Toast.makeText(getBaseContext(), "验证码不能为空", Toast.LENGTH_SHORT).show();
                                    break;
                                case 5:
                                    Toast.makeText(getBaseContext(), "手机号码格式不正确", Toast.LENGTH_SHORT).show();
                                    break;
                                case 6:
                                    Toast.makeText(getBaseContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                                    break;
                                case 7:
                                    Toast.makeText(getBaseContext(), "确认密码不能为空", Toast.LENGTH_SHORT).show();
                                    break;
                                case 8:
                                    Toast.makeText(getBaseContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forget_get_code:
                getDateWithSendMsg();
                break;
            case R.id.btn_reset_pwd:
                getDataWithPwdFind();
                break;
            default:
                break;
        }
    }

}
