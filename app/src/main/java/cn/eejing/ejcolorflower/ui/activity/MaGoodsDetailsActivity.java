package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

public class MaGoodsDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_goods_details;
    }

    @Override
    public void initView() {
        setToolbar(getIntent().getStringExtra("name"), View.VISIBLE);

        int goodsId = getIntent().getIntExtra("goods_id", 0);
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            default:
                break;
        }
    }

}
