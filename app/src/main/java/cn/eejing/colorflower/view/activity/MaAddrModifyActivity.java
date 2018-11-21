package cn.eejing.colorflower.view.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import cn.eejing.colorflower.model.request.AddrListBean;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 修改收货地址
 */

public class MaAddrModifyActivity extends BaseActivity {
    private static final String TAG = "MaAddrModifyActivity";

    @BindView(R.id.et_addr_modify_consignee)        EditText etConsignee;
    @BindView(R.id.et_addr_modify_phone)            EditText etPhone;
    @BindView(R.id.et_addr_modify_address)          EditText etAddress;
    @BindView(R.id.tv_addr_modify_address)          TextView tvAddress;

    private Gson mGson;
    private String mAddress;
    private int mAddressId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_modify;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setToolbar("修改收货地址", View.VISIBLE, null, View.GONE);
        mGson = new Gson();

        Intent intent = getIntent();
        if (intent != null) {
            // 如果信息是从编辑页传过来的
            if (intent.getStringExtra("type").equals("edit")) {
                AddrListBean.DataBean data = (AddrListBean.DataBean) intent.getSerializableExtra("address_info");
                mAddressId = data.getId();
                etConsignee.setText(data.getConsignee());
                etPhone.setText(data.getMobile());

                StringBuilder sb = new StringBuilder();
                String addressAll = data.getAddress();
                String[] split = addressAll.split(" ");
                for (int i = 0; i < split.length - 1; i++) {
                    sb.append(split[i]).append(" ");
                }
                etAddress.setText(split[split.length - 1]);
                tvAddress.setText(sb.toString().trim());
            }
        }
    }

    @OnClick(R.id.btn_addr_modify_save)
    public void clickSave() {
        // 非空判断
        if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            ToastUtil.showShort("请输入手机号");
        } else if (TextUtils.isEmpty(etConsignee.getText().toString().trim())) {
            ToastUtil.showShort("请输入收货人姓名");
        } else if (tvAddress.getText().toString().equals("请选择")
                || TextUtils.isEmpty(tvAddress.getText().toString().trim())) {
            ToastUtil.showShort("请您选择所在地区");
        } else if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
            ToastUtil.showShort("请您填写详细地址");
        } else {
            mAddress = tvAddress.getText().toString().trim() + " " + etAddress.getText().toString().trim();
            getDataWithAddrUpdate();
        }
    }

    @OnClick(R.id.layout_addr_modify_select)
    public void clickModifySelect() {
        jumpToActivity(MaAddrProvinceActivity.class);
    }

    private void getDataWithAddrUpdate() {
        OkGo.<String>post(Urls.EDIT_ADDRESS)
                .tag(this)
                .params("consignee", etConsignee.getText().toString())
                .params("province", "")
                .params("city", "")
                .params("district", "")
                .params("address", mAddress.trim())
                .params("mobile", etPhone.getText().toString())
                .params("token", MainActivity.getAppCtrl().getToken())
                .params("address_id", mAddressId)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "修改收货地址 请求成功: " + body);

                                 CodeMsgBean bean = mGson.fromJson(body, CodeMsgBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         ToastUtil.showShort("地址更改成功");
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAddressInfo(String address) {
        if (!TextUtils.isEmpty(address)) {
            tvAddress.setText(address);
            etAddress.setText("");
        }
    }
}
