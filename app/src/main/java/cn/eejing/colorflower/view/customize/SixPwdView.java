package cn.eejing.colorflower.view.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.eejing.colorflower.R;


/**
 * 六位密码输入布局
 */

public class SixPwdView extends RelativeLayout {

    private FrameLayout[] mLayouts;      // 每一位布局
    private ImageView[]   mPoints;       // 保存小黑点
    private String[]      mNumbers;      // 保存输入的密码

    public SixPwdView(Context context) {
        this(context, null);
    }

    public SixPwdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mNumbers = new String[6];
        mPoints  = new ImageView[6];
        mLayouts = new FrameLayout[6];

        LayoutInflater.from(context).inflate(R.layout.layout_pwd_six, this);
        mPoints[0] = findViewById(R.id.iv_0);
        mPoints[1] = findViewById(R.id.iv_1);
        mPoints[2] = findViewById(R.id.iv_2);
        mPoints[3] = findViewById(R.id.iv_3);
        mPoints[4] = findViewById(R.id.iv_4);
        mPoints[5] = findViewById(R.id.iv_5);

        // 这里获取外层的 FrameLayout，是因为之后要给它们添加点击事件
        mLayouts[0] = findViewById(R.id.fl_0);
        mLayouts[1] = findViewById(R.id.fl_1);
        mLayouts[2] = findViewById(R.id.fl_2);
        mLayouts[3] = findViewById(R.id.fl_3);
        mLayouts[4] = findViewById(R.id.fl_4);
        mLayouts[5] = findViewById(R.id.fl_5);
    }

    /** 获取保存6位密码的数组 */
    public String[] getNumbers() {
        return mNumbers;
    }

    /** 获取保存小黑点的数组 */
    public ImageView[] getPoints() {
        return mPoints;
    }

    /** 获取小黑点密码父布局的数组（用来添加点击事件） */
    public FrameLayout[] getLayouts() {
        return mLayouts;
    }

    /** 获取6位支付密码 */
    public String getPassword() {
        StringBuilder builder = new StringBuilder();
        for (String mNumber : mNumbers) {
            if (mNumber != null) {
                builder.append(mNumber);
            }
        }
        return builder.toString();
    }

    /** 清空密码 */
    public void clear() {
        int i = 0;
        while (i < mNumbers.length) {
            mNumbers[i] = null;
            i++;
        }
        for (ImageView mPoint : mPoints) {
            mPoint.setVisibility(GONE);
        }
    }
}
