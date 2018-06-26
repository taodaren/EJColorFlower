package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperButton;
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
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_AREAS;
import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_CITYS;
import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_PROVINCESS;

public class MaAddrAddActivity extends BaseActivity {

    @BindView(R.id.btn_address_add_save)
    SuperButton btnSave;
    @BindView(R.id.et_address_add_consignee)
    EditText etConsignee;
    @BindView(R.id.et_address_add_phone)
    EditText etPhone;
    @BindView(R.id.et_address_add_address)
    EditText etAddress;
    @BindView(R.id.tv_address_add_address)
    TextView tvAddress;

    private Gson mGson;
    private String mMemberId, mToken, mAddress;
    private String mProvincess, mCity, mAreas;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_add;
    }

    @Override
    public void initView() {
        setToolbar("添加收货地址", View.VISIBLE);
        setAddress();

        mGson = new Gson();
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(this).getMember_id());
        mToken = Settings.getLoginSessionInfo(this).getToken();
    }

    @SuppressLint("SetTextI18n")
    private void setAddress() {
        mProvincess = getIntent().getStringExtra(ADDRESS_PROVINCESS);
        mCity = getIntent().getStringExtra(ADDRESS_CITYS);
        mAreas = getIntent().getStringExtra(ADDRESS_AREAS);
        if (mProvincess != null && mCity != null && mAreas != null) {
            mAddress = mProvincess + " " + mCity + " " + mAreas;
            tvAddress.setText(mAddress);
        }
        if (mAddress != null) {
            mAddress = etAddress.getText().toString() + mAddress;
        }
    }

    @OnClick(R.id.btn_address_add_save)
    public void clickSave() {
        if (mProvincess == null || mCity == null || mAreas == null) {
            Toast.makeText(this, "请您选择所在地区", Toast.LENGTH_SHORT).show();
        } else if (etAddress.getText() == null) {
            Toast.makeText(this, "请您填写详细地址", Toast.LENGTH_SHORT).show();
        } else {
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
                .params("status", 0)
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
