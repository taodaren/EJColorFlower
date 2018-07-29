package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.zhouwei.library.CustomPopWindow;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.lite.MasterModeEntity;
import cn.eejing.ejcolorflower.model.request.AddMasterModeBean;
import cn.eejing.ejcolorflower.view.adapter.DeMasterModeAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

/**
 * 设置主控模式
 */

public class DeMasterModeActivity extends BaseActivity {

    @BindView(R.id.rv_master_mode)             RecyclerView rvMasterMode;
    @BindView(R.id.img_add_master_mode)        ImageView imgAddMode;

    private CustomPopWindow mCustomPopWindow;
    private DeMasterModeAdapter mAdapter;
    private List<AddMasterModeBean> mList;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("设置主控模式", View.VISIBLE);
        mList = new ArrayList<>();
        // 数据库中查询是否保存信息
        isSaveDBInfo();

        initRecyclerView();
    }

    private void isSaveDBInfo() {
        List<MasterModeEntity> entities = LitePal.findAll(MasterModeEntity.class);
        if (entities != null && entities.size() > 0) {
            for (int i = 0; i < entities.size(); i++) {
                mList.add(new AddMasterModeBean(entities.get(i).getMode()));
            }
        }
    }

    @OnClick(R.id.img_add_master_mode)
    public void onClickedAdd() {
        // 配置喷射样式
        configJetStyle();
    }

    private void configJetStyle() {
        // 显示 PopupWindow 同时背景变暗
        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_selec_jet_mode, null);
        handleLogic(contentView);
        // 创建并显示 popWindow
        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                // 弹出 popWindow 时，背景是否变暗
                .enableBackgroundDark(true)
                // 控制亮度
                .setBgDarkAlpha(0.7f)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Log.e("TAG", "onDismiss");
                    }
                })
                .create()
                // 设置 pop 位置
                .showAsDropDown(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                switch (v.getId()) {
                    case R.id.pop_mode_stream:
                        addSQLiteData(CONFIG_STREAM);
                        mList.add(new AddMasterModeBean(CONFIG_STREAM));
                        mAdapter.refreshList(mList);
                        break;
                    case R.id.pop_mode_ride:
                        addSQLiteData(CONFIG_RIDE);
                        mList.add(new AddMasterModeBean(CONFIG_RIDE));
                        mAdapter.refreshList(mList);
                        break;
                    case R.id.pop_mode_interval:
                        addSQLiteData(CONFIG_INTERVAL);
                        mList.add(new AddMasterModeBean(CONFIG_INTERVAL));
                        mAdapter.refreshList(mList);
                        break;
                    case R.id.pop_mode_together:
                        addSQLiteData(CONFIG_TOGETHER);
                        mList.add(new AddMasterModeBean(CONFIG_TOGETHER));
                        mAdapter.refreshList(mList);
                        break;
                    default:
                        break;
                }
            }
        };
        contentView.findViewById(R.id.pop_mode_stream).setOnClickListener(listener);
        contentView.findViewById(R.id.pop_mode_ride).setOnClickListener(listener);
        contentView.findViewById(R.id.pop_mode_interval).setOnClickListener(listener);
        contentView.findViewById(R.id.pop_mode_together).setOnClickListener(listener);
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvMasterMode.setLayoutManager(manager);
        // 绑定适配器
        mAdapter = new DeMasterModeAdapter(this, mList);
        rvMasterMode.setAdapter(mAdapter);
    }

    private void addSQLiteData(String mode) {
        MasterModeEntity entity = new MasterModeEntity(mode);
        entity.setMode(mode);
        entity.save();
    }

}
