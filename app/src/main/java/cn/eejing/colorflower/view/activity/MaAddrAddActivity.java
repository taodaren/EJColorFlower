package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.lzy.okgo.model.HttpParams;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.event.AddrAddEvent;
import cn.eejing.colorflower.model.event.AddrSelectEvent;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivityEvent;

/**
 * 添加收货地址
 */

public class MaAddrAddActivity extends BaseActivityEvent {
    private static final String TAG = "MaAddrAddActivity";

    @BindView(R.id.et_address_add_consignee)      ClearableEditText etConsignee;
    @BindView(R.id.et_address_add_phone)          ClearableEditText etPhone;
    @BindView(R.id.et_address_add_address)        ClearableEditText etAddress;
    @BindView(R.id.tv_address_add_address)        TextView          tvAddress;
    @BindView(R.id.stv_address_add_def)           SuperTextView     stvSwitch;

    private int mFlag;
    private String mAddress, mProvinceId, mCityId, mDistrictId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_add;
    }

    @Override
    public void initView() {
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

    @SuppressWarnings("unchecked")
    private void getDataWithAddressAdd() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("consignee", etConsignee.getText().toString());
        params.put("province", mProvinceId);
        params.put("city", mCityId);
        params.put("district", mDistrictId);
        params.put("address", mAddress);
        params.put("mobile", etPhone.getText().toString());
        params.put("is_default", mFlag);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.CREATE_ADDRESS)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "添加收货地址 请求成功");

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

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onEventAddrSelect(AddrSelectEvent event) {
        super.onEventAddrSelect(event);
        mProvinceId = event.getProvinceId();
        mCityId = event.getCityId();
        mDistrictId = event.getDistrictId();
        tvAddress.setText(event.getProvince() + " " + event.getCity() + " " + event.getDistrict());
        etAddress.setText("");
    }
}
