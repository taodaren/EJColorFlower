package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

public class MaGoodsDetailsActivity extends BaseActivity {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_goods_details;
    }

    @Override
    public void initView() {
        int goods_id = getIntent().getIntExtra("goods_id", 0);
        setToolbar(goods_id + "", View.VISIBLE);
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
