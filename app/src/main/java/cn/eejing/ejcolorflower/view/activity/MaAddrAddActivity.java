package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.AddrAddEvent;
import cn.eejing.ejcolorflower.model.request.AddrAddBean;
import cn.eejing.ejcolorflower.model.session.AddrSession;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_AREAS;
import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_CITYS;
import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_PROVINCESS;

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
    // 省、市、县、地址
    private String mProvincess, mCity, mAreas, mAddress;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_add;
    }

    @Override
    public void initView() {
        mGson = new Gson();
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(this).getMember_id());
        mToken = Settings.getLoginSessionInfo(this).getToken();

        mProvincess = getIntent().getStringExtra(ADDRESS_PROVINCESS);
        mCity = getIntent().getStringExtra(ADDRESS_CITYS);
        mAreas = getIntent().getStringExtra(ADDRESS_AREAS);

        AddrSession session = Settings.getAddrSessionInfo(this);
        String consignee = session.getConsignee();
        String phone = session.getPhone();
        String address = session.getAddress();
        if (consignee != null) {
            etConsignee.setText(consignee);
        }
        if (phone != null) {
            etPhone.setText(phone);
        }
        if (address != null) {
            etAddress.setText(address);
        }

        setToolbar("添加收货地址", View.VISIBLE);

        // 显示收货人、手机号码、详细地址
        if (mProvincess != null && mCity != null && mAreas != null) {
            // 如果省市县不为空（已选择）
            if (consignee != null) {
                // 如果收货人已输入过，显示收货人
                etConsignee.setText(consignee);
            }
            if (phone != null) {
                // 如果手机号已输入过，显示手机号
                etPhone.setText(phone);
            }
            if (address != null) {
                // 如果详细地址已输入过，显示详细地址
                etAddress.setText(address);
            }
        }
        setAddress();
    }

    @Override
    public void initListener() {
        stvSwitch.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                superTextView.setSwitchIsChecked(!superTextView.getSwitchIsChecked());
            }
        }).setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mFlag = 1;
                } else {
                    mFlag = 0;
                }
            }
        });
    }

    @OnClick(R.id.btn_address_add_save)
    public void clickSave() {
        if (mProvincess == null || mCity == null || mAreas == null) {
            Toast.makeText(this, "请您选择所在地区", Toast.LENGTH_SHORT).show();
        } else if ((etConsignee.getText() != null || etPhone.getText() != null || etAddress.getText() != null)
                && etAddress.getText() == null
                ) {
            Toast.makeText(this, "请您填写详细地址", Toast.LENGTH_SHORT).show();
        } else {
            setSession();
            getDataWithAddressAdd();
        }
    }

    @OnClick(R.id.layout_address_add_select)
    public void clickAddSelect() {
        if (etConsignee.getText() != null || etPhone.getText() != null || etAddress.getText() != null) {
            // 如果输入过信息，保存起来
            Settings.saveAddressInfo(this, new AddrSession(
                    etConsignee.getText().toString(),
                    etPhone.getText().toString(),
                    etAddress.getText().toString()
            ));
        }

        jumpToActivity(MaAddrProvincesActivity.class);
    }

    @SuppressLint("SetTextI18n")
    private void setAddress() {
        if (mProvincess != null && mCity != null && mAreas != null) {
            setSession();
            // 如果省市县已选择，则展示地址
            tvAddress.setText(mProvincess + " " + mCity + " " + mAreas);
        }
        if (etAddress.getText() != null) {
            // 如果输入详细地址栏为不为空，设置展示地址
            mAddress = mProvincess + " " + mCity + " " + mAreas + etAddress.getText().toString();
        }
    }

    private void setSession() {
        AddrSession session = Settings.getAddrSessionInfo(this);
        String consignee = session.getConsignee();
        String phone = session.getPhone();
        String address = session.getAddress();
        if (consignee != null) {
            etConsignee.setText(consignee);
        }
        if (phone != null) {
            etPhone.setText(phone);
        }
        if (address != null) {
            etAddress.setText(address);
        }
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

}
