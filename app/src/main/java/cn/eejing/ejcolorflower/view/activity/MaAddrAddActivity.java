package cn.eejing.ejcolorflower.view.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.AddrAddEvent;
import cn.eejing.ejcolorflower.model.request.AddrAddBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.MySettings;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 添加收货地址
 */

public class MaAddrAddActivity extends BaseActivity {

    @BindView(R.id.et_address_add_consignee)        EditText etConsignee;
    @BindView(R.id.et_address_add_phone)            EditText etPhone;
    @BindView(R.id.et_address_add_address)          EditText etAddress;
    @BindView(R.id.tv_address_add_address)          TextView tvAddress;
    @BindView(R.id.stv_address_add_def)             SuperTextView stvSwitch;

    private int mFlag;
    private Gson mGson;
    private String mMemberId, mToken;
    private String mAddress;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_add;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        mGson = new Gson();
        mMemberId = String.valueOf(MySettings.getLoginSessionInfo(this).getMember_id());
        mToken = MySettings.getLoginSessionInfo(this).getToken();

        setToolbar("添加收货地址", View.VISIBLE, null, View.GONE);
    }

    @Override
    public void initListener() {
        stvSwitch.setOnSuperTextViewClickListener(superTextView -> superTextView.setSwitchIsChecked(!superTextView.getSwitchIsChecked()))
                .setSwitchCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked) {
                        mFlag = 1;
                    } else {
                        mFlag = 0;
                    }
                });
    }

    @OnClick(R.id.btn_address_add_save)
    public void clickSave() {
        if (TextUtils.isEmpty(etConsignee.getText().toString().trim())) {
            Toast.makeText(this, "请输入收货人姓名", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
        } else if (tvAddress.getText().toString().trim().equals("请选择")
                || TextUtils.isEmpty(tvAddress.getText().toString().trim())) {
            Toast.makeText(this, "请您选择所在地区", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
            Toast.makeText(this, "请您填写详细地址", Toast.LENGTH_SHORT).show();
        } else {
            mAddress = tvAddress.getText().toString().trim() + " " + etAddress.getText().toString().trim();
            getDataWithAddressAdd();
        }
    }

    @OnClick(R.id.layout_address_add_select)
    public void clickAddSelect() {
        jumpToActivity(MaAddrProvincesActivity.class);
    }

    private void getDataWithAddressAdd() {
        OkGo.<String>post(Urls.ADDRESS_ADD)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .params("name", etConsignee.getText().toString())
                .params("mobile", etPhone.getText().toString())
                .params("address", mAddress)
                .params("status", mFlag)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "address_add request succeeded--->" + body);

                                 AddrAddBean bean = mGson.fromJson(body, AddrAddBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         Toast.makeText(MaAddrAddActivity.this, "地址添加成功", Toast.LENGTH_SHORT).show();
                                         EventBus.getDefault().post(new AddrAddEvent("add_ok"));
                                         finish();
                                         break;
                                     case 4:
                                         Toast.makeText(MaAddrAddActivity.this, "收货人不能为空", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 5:
                                         Toast.makeText(MaAddrAddActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 6:
                                         Toast.makeText(MaAddrAddActivity.this, "详细地址不能为空", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 0:
                                         Toast.makeText(MaAddrAddActivity.this, "地址添加失败", Toast.LENGTH_SHORT).show();
                                         break;
                                     default:
                                         break;
                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverAddressInfo(String address) {
        tvAddress.setText(address);
        etAddress.setText("");
    }
}
