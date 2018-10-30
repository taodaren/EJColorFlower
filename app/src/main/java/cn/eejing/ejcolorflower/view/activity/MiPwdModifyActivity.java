package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.PwdUpdateBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Encryption;
import cn.eejing.ejcolorflower.util.MySettings;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 修改密码
 */

public class MiPwdModifyActivity extends BaseActivity {

    @BindView(R.id.et_current_psd)        EditText etCurrentPsd;
    @BindView(R.id.et_reset_psd)          EditText etResetPsd;
    @BindView(R.id.et_confirm_psd)        EditText etConfirmPsd;
    @BindView(R.id.btn_modify_pwd)        SuperButton btnModifyPwd;

    private Gson mGson;
    private String mIv, mMemberId, mToken;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_pwd_modify;
    }

    @Override
    public void initView() {
        mGson = new Gson();

        mIv = Encryption.newIv();
        mMemberId = String.valueOf(MySettings.getLoginSessionInfo(this).getMember_id());
        mToken = MySettings.getLoginSessionInfo(this).getToken();
    }

    @OnClick({R.id.img_back_pwd_modify, R.id.btn_modify_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_pwd_modify:
                finish();
                break;
            case R.id.btn_modify_pwd:
                getDataWithPwdUpdate();
                break;
        }
    }

    private void getDataWithPwdUpdate() {
        String pwdCurrent, pwdReset, pwdConfirm;

        try {
            pwdCurrent = Encryption.encrypt(etCurrentPsd.getText().toString(), mIv);
            pwdReset = Encryption.encrypt(etResetPsd.getText().toString(), mIv);
            pwdConfirm = Encryption.encrypt(etConfirmPsd.getText().toString(), mIv);

            OkGo.<String>post(Urls.PWD_UPDATE)
                    .tag(this)
                    .params("member_id", mMemberId)
                    .params("token", mToken)
                    .params("password", pwdCurrent)
                    .params("new_pwd", pwdReset)
                    .params("res_pwd", pwdConfirm)
                    .params("iv", mIv)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            Log.e(AppConstant.TAG, "pwd_update request succeeded--->" + body);

                            PwdUpdateBean bean = mGson.fromJson(body, PwdUpdateBean.class);
                            switch (bean.getCode()) {
                                case 0:
                                    Toast.makeText(getBaseContext(), "修改密码失败", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    logout(MiPwdModifyActivity.this);
                                    Toast.makeText(getBaseContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(getBaseContext(), "旧密码不能为空", Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    Toast.makeText(getBaseContext(), "旧密码错误,请重新输入", Toast.LENGTH_SHORT).show();
                                    break;
                                case 5:
                                    Toast.makeText(getBaseContext(), "新密码不能为空", Toast.LENGTH_SHORT).show();
                                    break;
                                case 6:
                                    Toast.makeText(getBaseContext(), "确认密码不能为空", Toast.LENGTH_SHORT).show();
                                    break;
                                case 7:
                                    Toast.makeText(getBaseContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
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
