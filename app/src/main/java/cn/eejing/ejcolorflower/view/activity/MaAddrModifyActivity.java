package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
 * 修改收货地址
 */

public class MaAddrModifyActivity extends BaseActivity {

    @BindView(R.id.et_addr_modify_consignee)        EditText etConsignee;
    @BindView(R.id.et_addr_modify_phone)            EditText etPhone;
    @BindView(R.id.et_addr_modify_address)          EditText etAddress;
    @BindView(R.id.tv_addr_modify_address)          TextView tvAddress;

    private Gson mGson;
    private String mMemberId, mToken;
    // 省、市、县、地址
    private String mProvincess, mCity, mAreas, mAddress;
    private int mAddressId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_modify;
    }

    @Override
    public void initView() {
        setToolbar("修改收货地址", View.VISIBLE);

        mGson = new Gson();
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(this).getMember_id());
        mToken = Settings.getLoginSessionInfo(this).getToken();

        mProvincess = getIntent().getStringExtra(ADDRESS_PROVINCESS);
        mCity = getIntent().getStringExtra(ADDRESS_CITYS);
        mAreas = getIntent().getStringExtra(ADDRESS_AREAS);
        mAddressId = getIntent().getIntExtra("address_id", 0);

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

    @OnClick(R.id.btn_addr_modify_save)
    public void clickSave() {
        if (mProvincess == null || mCity == null || mAreas == null) {
            Toast.makeText(this, "请您选择所在地区", Toast.LENGTH_SHORT).show();
        } else if ((etConsignee.getText() != null || etPhone.getText() != null || etAddress.getText() != null)
                && etAddress.getText() == null
                ) {
            Toast.makeText(this, "请您填写详细地址", Toast.LENGTH_SHORT).show();
        } else {
            setSession();
            getDataWithAddrUpdate();
        }
    }

    @OnClick(R.id.layout_addr_modify_select)
    public void clickModifySelect() {
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

    private void getDataWithAddrUpdate() {
        OkGo.<String>post(Urls.ADDRESS_UPDATE)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .params("address_id", mAddressId)
                .params("name", etConsignee.getText().toString())
                .params("mobile", etPhone.getText().toString())
                .params("address", mAddress)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "address_update request succeeded--->" + body);

                                 AddrAddBean bean = mGson.fromJson(body, AddrAddBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         Toast.makeText(MaAddrModifyActivity.this, "地址更改成功", Toast.LENGTH_SHORT).show();
                                         EventBus.getDefault().post(new AddrAddEvent("add_ok"));
                                         finish();
                                         break;
                                     case 4:
                                         Toast.makeText(MaAddrModifyActivity.this, "收货人不能为空", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 5:
                                         Toast.makeText(MaAddrModifyActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 6:
                                         Toast.makeText(MaAddrModifyActivity.this, "详细地址不能为空", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 7:
                                         Toast.makeText(MaAddrModifyActivity.this, "地址不存在", Toast.LENGTH_SHORT).show();
                                         break;
                                     case 0:
                                         Toast.makeText(MaAddrModifyActivity.this, "地址更改失败", Toast.LENGTH_SHORT).show();
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
