package cn.eejing.colorflower.view.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.OpenWalletBean;
import cn.eejing.colorflower.presenter.OnPasswordFinishedListener;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.Encryption;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.SelfDialogBase;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;
import cn.eejing.colorflower.view.customize.PayPopupWindow;

/**
 * 我的账户
 */

public class MiAccountActivity extends BaseActivity implements OnPasswordFinishedListener, TextWatcher {

    @BindView(R.id.tv_money_have)       TextView          tvMoney;
    @BindView(R.id.et_cash_num)         ClearableEditText etCashNum;
    @BindView(R.id.et_account_name)     ClearableEditText etName;
    @BindView(R.id.et_account_phone)    ClearableEditText etPhone;
    @BindView(R.id.et_card_num)         ClearableEditText etCard;
    @BindView(R.id.et_open_num)         ClearableEditText etOpen;
    @BindView(R.id.btn_account_cash)    Button            btnCash;

    private static final String TAG = "MiAccountActivity";
    private static final int PWD_STATUS_NO = 0;    // 未设置支付密码
    private static final int PWD_STATUS_OK = 1;    // 已设置支付密码
    private Gson mGson;
    private String mIv;
    private String mMoney;
    private int mPwdStatus;
    private SelfDialogBase mDialog;
    private PayPopupWindow payPopupWindow;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_account;
    }

    @Override
    public void initView() {
        setToolbar("我的账户", View.VISIBLE, null, View.GONE);
        mIv = Encryption.newIv();
    }

    @Override
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        super.setToolbar(title, titleVisibility, menu, menuVisibility);
        ImageView imgBill = findViewById(R.id.img_vip_toolbar);
        ViewGroup.LayoutParams params = imgBill.getLayoutParams();
        params.height = 68;
        params.width = 68;
        imgBill.setLayoutParams(params);
        imgBill.setVisibility(View.VISIBLE);
        imgBill.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_bill));
    }

    @Override
    public void initData() {
        getDataWithOpenWallet();
    }

    @Override
    public void initListener() {
        etCashNum.addTextChangedListener(this);
        etName.addTextChangedListener(this);
        etPhone.addTextChangedListener(this);
        etCard.addTextChangedListener(this);
        etOpen.addTextChangedListener(this);
    }

    @OnClick({R.id.img_vip_toolbar, R.id.btn_set_pay_pwd, R.id.tv_all_cash, R.id.btn_account_cash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_vip_toolbar:
                jumpToActivity(MiBillActivity.class);
                break;
            case R.id.btn_set_pay_pwd:
                switch (mPwdStatus) {
                    case PWD_STATUS_OK:
                        showDialogByPayPwd("您已设置过支付密码\n\t\t\t\t\t是否重置？", "确定", "取消");
                        break;
                    case PWD_STATUS_NO:
                        showDialogByPayPwd("您未设置过支付密码\n\t\t\t\t\t是否设置？", "去设置", "稍后");
                        break;
                }
                break;
            case R.id.tv_all_cash:
                etCashNum.setText(mMoney);
                break;
            case R.id.btn_account_cash:
                String s = etCashNum.getText().toString();
                if (Double.parseDouble(s) > Double.parseDouble(mMoney)) {
                    ToastUtil.showShort("对不起，您没有那么多钱");
                } else if (mPwdStatus == PWD_STATUS_NO) {
                    showDialogByPayPwd("您未设置过支付密码\n\t\t\t\t\t是否设置？", "去设置", "取消");
                } else {
                    popPayPwd();
                }
                break;
        }
    }

    @Override
    public void onFinish(String password) {
        // 输入完6位密码后回调
        getDataWithCashWithdrawal(password);
        payPopupWindow.dismiss();
    }

    private void showDialogByPayPwd(String title, String yes, String no) {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle(title);
        mDialog.setYesOnclickListener(yes, () -> {
            jumpToActivity(MiSetPayPwdActivity.class);
            mDialog.dismiss();
        });
        mDialog.setNoOnclickListener(no, () -> {
            if (no.equals("重试")) {
                popPayPwd();
            }
            mDialog.dismiss();
        });
        mDialog.show();
    }

    /** 弹出支付密码框 */
    private void popPayPwd() {
        payPopupWindow = new PayPopupWindow(this);
        payPopupWindow.setPrice(etCashNum.getText().toString());
        payPopupWindow.setOnPasswordFinishedListener(this);
        payPopupWindow.show(btnCash);
    }

    private void getDataWithOpenWallet() {
        OkGo.<String>post(Urls.OPEN_WALLET)
                .tag(this)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "打开钱包 请求成功: " + body);

                                 mGson = new Gson();
                                 OpenWalletBean bean = mGson.fromJson(body, OpenWalletBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         mMoney = bean.getData().getMoney();
                                         mPwdStatus = bean.getData().getPwd_status();
                                         tvMoney.setText(mMoney);
                                         break;
                                     default:
                                         ToastUtil.showShort(bean.getMessage());
                                         break;
                                 }
                             }
                         }
                );

    }

    private void getDataWithCashWithdrawal(String password) {
        try {
            String payPwd = Encryption.encrypt(password, mIv);

            OkGo.<String>post(Urls.APPLY_FOR_WITH_DRAW)
                    .tag(this)
                    .params("token", MainActivity.getAppCtrl().getToken())
                    .params("pay_password", payPwd)
                    .params("money", etCashNum.getText().toString())
                    .params("iv", mIv)
                    .params("bank", etOpen.getText().toString())
                    .params("card_number", etCard.getText().toString())
                    .params("name", etName.getText().toString())
                    .execute(new StringCallback() {
                                 @Override
                                 public void onSuccess(Response<String> response) {
                                     String body = response.body();
                                     LogUtil.d(TAG, "提现申请 请求成功: " + body);

                                     mGson = new Gson();
                                     CodeMsgBean bean = mGson.fromJson(body, CodeMsgBean.class);
                                     switch (bean.getCode()) {
                                         case 1:
                                             // 跳转到提现成功
                                             jumpToActivity(MiCashSuccessActivity.class);
                                             finish();
                                             break;
                                         case 5:
                                             showDialogByPayPwd("支付密码有误，请重试", "忘记密码", "重试");
                                             break;
                                         default:
                                             ToastUtil.showShort(bean.getMessage());
                                             break;
                                     }
                                 }
                             }
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String strCashNum,strName,strPhone,strCard, strOpen;
        strCashNum = etCashNum.getText().toString().trim();
        strName = etName.getText().toString().trim();
        strPhone = etPhone.getText().toString().trim();
        strCard = etCard.getText().toString().trim();
        strOpen = etOpen.getText().toString().trim();

        if (TextUtils.isEmpty(strCashNum) || TextUtils.isEmpty(strName) || TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strCard)|| TextUtils.isEmpty(strOpen)) {
            // ET 有空情况
            btnCash.setEnabled(Boolean.FALSE);
            btnCash.setBackground(getResources().getDrawable(R.drawable.shape_btn_jbs_no));
        } else {
            // ET 同时不为空的情况
            btnCash.setEnabled(Boolean.TRUE);
            btnCash.setBackground(getResources().getDrawable(R.drawable.shape_btn_jbs));
        }
    }
}
