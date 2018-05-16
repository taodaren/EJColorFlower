package cn.eejing.ejcolorflower.ui.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

public class CoDeviceActivity extends BaseActivity {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_device;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        int groupId = intent.getIntExtra("group_id", 0);
        String groupName = intent.getStringExtra("group_name");
        Log.e(AppConstant.TAG, "initView: groupId--->" + groupId);
        Log.e(AppConstant.TAG, "initView: groupName--->" + groupName);

        setToolbar(groupName, View.VISIBLE);
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
