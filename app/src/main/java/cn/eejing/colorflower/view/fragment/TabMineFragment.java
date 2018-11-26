package cn.eejing.colorflower.view.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.allen.library.SuperTextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.activity.MiAboutActivity;
import cn.eejing.colorflower.view.activity.MiBuyRecordActivity;
import cn.eejing.colorflower.view.activity.MiOpinionActivity;
import cn.eejing.colorflower.view.activity.MiOrderActivity;
import cn.eejing.colorflower.view.activity.MiSetActivity;
import cn.eejing.colorflower.view.activity.MiUpgradeVipActivity;
import cn.eejing.colorflower.view.activity.MiVipListActivity;
import cn.eejing.colorflower.view.base.BaseFragment;

import static cn.eejing.colorflower.app.AppConstant.LEVEL_GENERAL_USER;
import static cn.eejing.colorflower.app.AppConstant.LEVEL_VIP_USER;
import static cn.eejing.colorflower.app.AppConstant.LEVEL_VVIP_USER;
import static cn.eejing.colorflower.app.BaseApplication.getVersionName;

/**
 * 我的模块
 */

public class TabMineFragment extends BaseFragment {
    @BindView(R.id.layout_user_info)    RelativeLayout layoutUserInfo;
    @BindView(R.id.layout_vvip_mine)    LinearLayout   layoutVvipShow;
    @BindView(R.id.btn_mine_upgrade)    Button         btnUpgrade;

    private View mRootView;
    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.mine_name, View.VISIBLE);
    }

    @Override
    public void setToolbar(int toolbarId, int title, int titleVisibility) {
        super.setToolbar(toolbarId, title, titleVisibility);
        ImageView imgVip = mRootView.findViewById(R.id.img_vip_toolbar);
        if (MainActivity.getAppCtrl().getLevel().equals(LEVEL_VVIP_USER)) {
            imgVip.setVisibility(View.VISIBLE);
        } else {
            imgVip.setVisibility(View.GONE);
        }
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_mine;
    }

    @Override
    public void initView(View rootView) {
        mRootView = rootView;
        String versionName = getVersionName(BaseApplication.getContext());
        ((SuperTextView) rootView.findViewById(R.id.stv_mine_version)).setRightString("V " + versionName + " 版本");
    }

    @Override
    public void onStart() {
        super.onStart();
        String userLv = MainActivity.getAppCtrl().getLevel();
        switch (userLv) {
            case LEVEL_GENERAL_USER:
                layoutUserInfo.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.ic_user_general));
                layoutVvipShow.setVisibility(View.GONE);
                btnUpgrade.setVisibility(View.VISIBLE);
                break;
            case LEVEL_VIP_USER:
                layoutUserInfo.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.ic_user_vip));
                layoutVvipShow.setVisibility(View.GONE);
                btnUpgrade.setVisibility(View.GONE);
                break;
            case LEVEL_VVIP_USER:
                layoutUserInfo.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.ic_user_vvip));
                layoutVvipShow.setVisibility(View.VISIBLE);
                btnUpgrade.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick({R.id.img_vip_toolbar, R.id.layout_vvip_record, R.id.layout_vvip_account, R.id.btn_mine_upgrade, R.id.stv_mine_order, R.id.stv_mine_opinion, R.id.stv_mine_about, R.id.stv_mine_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_vip_toolbar:
                ((MainActivity) Objects.requireNonNull(getContext())).jumpToActivity(MiVipListActivity.class);
                break;
            case R.id.layout_vvip_record:
                ((MainActivity) Objects.requireNonNull(getContext())).jumpToActivity(MiBuyRecordActivity.class);
                break;
            case R.id.layout_vvip_account:
                ToastUtil.showShort("account");
                break;
            case R.id.btn_mine_upgrade:
                ((MainActivity) Objects.requireNonNull(getContext())).jumpToActivity(MiUpgradeVipActivity.class);
                break;
            case R.id.stv_mine_order:
                ((MainActivity) Objects.requireNonNull(getContext())).jumpToActivity(MiOrderActivity.class);
                break;
            case R.id.stv_mine_opinion:
                ((MainActivity) Objects.requireNonNull(getContext())).jumpToActivity(MiOpinionActivity.class);
                break;
            case R.id.stv_mine_about:
                ((MainActivity) Objects.requireNonNull(getContext())).jumpToActivity(MiAboutActivity.class);
                break;
            case R.id.stv_mine_set:
                ((MainActivity) Objects.requireNonNull(getContext())).jumpToActivity(MiSetActivity.class);
                break;
        }
    }
}
