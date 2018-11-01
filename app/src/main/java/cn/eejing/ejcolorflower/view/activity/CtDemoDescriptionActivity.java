package cn.eejing.ejcolorflower.view.activity;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.BaseApplication;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

/**
 * 演示说明
 */

public class CtDemoDescriptionActivity extends BaseActivity {

    @BindView(R.id.img_gif_demo)        ImageView imgGifDemo;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_demo_description;
    }

    @Override
    public void initView() {
        super.initView();
        BaseApplication baseApplication = (BaseApplication) getApplication();
        switch (baseApplication.getFlagGifDemo()) {
            case CONFIG_STREAM:
                showDemoGif("流水灯演示", R.drawable.ic_gif_stream);
                break;
            case CONFIG_RIDE:
                showDemoGif("跑马灯演示", R.drawable.ic_gif_ride);
                break;
            case CONFIG_INTERVAL:
                showDemoGif("间隔高低演示", R.drawable.ic_gif_interval);
                break;
            case CONFIG_TOGETHER:
                showDemoGif("齐喷演示", R.drawable.ic_gif_together);
                break;
        }
    }

    private void showDemoGif(String type, int ic) {
        setToolbar(type, View.VISIBLE, null, View.GONE);
        Glide.with(this).load(ic).into(imgGifDemo);
    }
}
