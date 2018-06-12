package cn.eejing.ejcolorflower.ui.activity;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.BORDER_TO_CENTER;
import static cn.eejing.ejcolorflower.app.AppConstant.CENTER_TO_BORDER;
import static cn.eejing.ejcolorflower.app.AppConstant.LEFT_TO_RIGHT;
import static cn.eejing.ejcolorflower.app.AppConstant.RIGHT_TO_LEFT;

/**
 * 流水灯
 */

public class ConfigStreamActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.rbtn_left_to_right)
    RadioButton rbtnLeftToRight;
    @BindView(R.id.rbtn_border_to_center)
    RadioButton rbtnBorderToCenter;
    @BindView(R.id.radio_left)
    RadioGroup radioLeft;
    @BindView(R.id.rbtn_right_to_left)
    RadioButton rbtnRightToLeft;
    @BindView(R.id.rbtn_center_to_border)
    RadioButton rbtnCenterToBorder;
    @BindView(R.id.radio_right)
    RadioGroup radioRight;
    @BindView(R.id.btn_config_verify)
    Button btnVerify;

    // 用于保存当前被选中的按钮
    String strBtnSelected = "unInit";
    BtnSelected btnListener1, btnListener2, btnListener3, btnListener4;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_config_strem_ride;
    }

    @Override
    public void initView() {
        setToolbar(getString(R.string.config_stream), View.VISIBLE);
        btnListener1 = new BtnSelected(LEFT_TO_RIGHT);
        btnListener2 = new BtnSelected(BORDER_TO_CENTER);
        btnListener3 = new BtnSelected(RIGHT_TO_LEFT);
        btnListener4 = new BtnSelected(CENTER_TO_BORDER);
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        rbtnLeftToRight.setOnClickListener(btnListener1);
        rbtnBorderToCenter.setOnClickListener(btnListener2);
        rbtnRightToLeft.setOnClickListener(btnListener3);
        rbtnCenterToBorder.setOnClickListener(btnListener4);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            case R.id.btn_config_verify:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 监听类，每个 RadioButton 均对 Click 动作进行监听：
     * 若用户点击的是 GroupLeft 的按钮，则清除 GroupRight 中按钮被选中的状态
     * 若用户点击的是 GroupRight 的按钮，则清除 GroupLeft 中按钮被选中的状态
     */
    public class BtnSelected implements View.OnClickListener {
        BtnSelected(String str) {
            bntID = str;
        }

        final String bntID;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View arg0) {
            strBtnSelected = bntID;

            if (bntID.equals(LEFT_TO_RIGHT) || bntID.equals(BORDER_TO_CENTER)) {
                radioRight.clearCheck();
            } else if (bntID.equals(RIGHT_TO_LEFT) || bntID.equals(CENTER_TO_BORDER)) {
                radioLeft.clearCheck();
            }

            switch (arg0.getId()) {
                case R.id.rbtn_left_to_right:
                    btnChangeState(R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                            R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick);
                    break;
                case R.id.rbtn_border_to_center:
                    btnChangeState(R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                            R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick);
                    break;
                case R.id.rbtn_right_to_left:
                    btnChangeState(R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click,
                            R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick);
                    break;
                case R.id.rbtn_center_to_border:
                    btnChangeState(R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click,
                            R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite);
                    break;
                default:
                    break;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void btnChangeState(@DrawableRes int lr, @DrawableRes int bc, @DrawableRes int rl, @DrawableRes int cb,
                                    @ColorRes int lrColor, @ColorRes int bcColor, @ColorRes int rlColor, @ColorRes int cbColor) {
            rbtnLeftToRight.setBackground(getDrawable(lr));
            rbtnBorderToCenter.setBackground(getDrawable(bc));
            rbtnRightToLeft.setBackground(getDrawable(rl));
            rbtnCenterToBorder.setBackground(getDrawable(cb));

            rbtnLeftToRight.setTextColor(getColor(lrColor));
            rbtnBorderToCenter.setTextColor(getColor(bcColor));
            rbtnRightToLeft.setTextColor(getColor(rlColor));
            rbtnCenterToBorder.setTextColor(getColor(cbColor));
        }
    }

}
