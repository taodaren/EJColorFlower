package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.event.AddrAddEvent;
import cn.eejing.colorflower.model.event.AddrSelectEvent;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 添加收货地址
 */

public class MaAddrAddActivity extends BaseActivity {
    private static final String TAG = "MaAddrAddActivity";

    @BindView(R.id.et_address_add_consignee)      EditText etConsignee;
    @BindView(R.id.et_address_add_phone)          EditText etPhone;
    @BindView(R.id.et_address_add_address)        EditText etAddress;
    @BindView(R.id.tv_address_add_address)        TextView tvAddress;
    @BindView(R.id.stv_address_add_def)           SuperTextView stvSwitch;

    private int mFlag;
    private Gson mGson;
    private String mAddress, mProvinceId, mCityId, mDistrictId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_add;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        mGson = new Gson();

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

    @OnClick({R.id.layout_address_add_select, R.id.btn_address_add_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_address_add_select:
                jumpToActivity(MaAddrProvinceActivity.class);
                break;
            case R.id.btn_address_add_save:
                saveAddAddress();
                break;
        }
    }

    public String validate() {
        String consignee = etConsignee.getText().toString();
        String phone = etPhone.getText().toString();
        String txtAddress = tvAddress.getText().toString().trim();
        String eidtAddress = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(consignee)) {
            return "请输入收货人姓名";
        }

        if (TextUtils.isEmpty(phone)) {
            return "请输入手机号";
        }

        if (phone.length() != 11) {
            return "请输入一个有效的手机号码";
        }

        if (txtAddress.equals("请选择") || TextUtils.isEmpty(txtAddress)) {
            return "请您选择所在地区";
        }

        if (TextUtils.isEmpty(eidtAddress)) {
            return "请您填写详细地址";
        }
        return "验证通过";
    }

    private void saveAddAddress() {
        String info = validate();
        if (!info.equals("验证通过")) {
            ToastUtil.showLong(info);
            return;
        }
        mAddress = tvAddress.getText().toString().trim() + " " + etAddress.getText().toString().trim();
        getDataWithAddressAdd();
    }

    private void getDataWithAddressAdd() {
        OkGo.<String>post(Urls.CREATE_ADDRESS)
                .tag(this)
                .params("consignee", etConsignee.getText().toString())
                .params("province", mProvinceId)
                .params("city", mCityId)
                .params("district", mDistrictId)
                .params("address", mAddress)
                .params("mobile", etPhone.getText().toString())
                .params("is_default", mFlag)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "添加收货地址 请求成功: " + body);

                                 CodeMsgBean bean = mGson.fromJson(body, CodeMsgBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         ToastUtil.showShort("地址添加成功");
                                         EventBus.getDefault().post(new AddrAddEvent("add_ok"));
                                         finish();
                                         break;
                                     default:
                                         ToastUtil.showShort(bean.getMessage());
                                         break;
                                 }
                             }
                         }
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBySelectAddr(AddrSelectEvent event) {
        mProvinceId = event.getProvinceId();
        mCityId = event.getCityId();
        mDistrictId = event.getDistrictId();
        tvAddress.setText(event.getProvince() + " " + event.getCity() + " " + event.getDistrict());
        etAddress.setText("");
    }
}
