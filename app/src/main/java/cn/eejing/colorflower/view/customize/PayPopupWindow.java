package cn.eejing.colorflower.view.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import java.util.List;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.presenter.OnPasswordFinishedListener;
import cn.eejing.colorflower.view.adapter.KeyboardAdapter;

/**
 * 支付布局
 */

public class PayPopupWindow extends PopupWindow implements KeyboardAdapter.OnKeyboardClickListener {

    private int currentIndex;      // 当前即将要输入密码的格子的索引
    private ImageView ivClose;     // 关闭按钮
    private ImageView ivIcon;      // 头像
    private TextView tvTitle;      // 标题
    private TextView tvMessage;    // 消费详情
    private TextView tvPrice;      // 价格
    private SixPwdView sixPwdView;
    private KeyboardView keyboardView;
    private List<String> mList;
    private String[] numbers;
    private ImageView[] points;
    public OnPasswordFinishedListener listener;

    public PayPopupWindow(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_pay_popup, null);
        setContentView(contentView);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        // 让 PopupWindow 同样覆盖状态栏
        setClippingEnabled(false);
        // 加上一层黑色透明背景
        setBackgroundDrawable(new ColorDrawable(0xAA000000));
        initView(contentView);
    }

    private void initView(View contentView) {
        ivClose = contentView.findViewById(R.id.iv_close);
        tvTitle = contentView.findViewById(R.id.tv_title);
        tvMessage = contentView.findViewById(R.id.tv_message);
        tvPrice = contentView.findViewById(R.id.tv_price);
        sixPwdView = contentView.findViewById(R.id.password_view);
        keyboardView = contentView.findViewById(R.id.keyboard_view);
        ivClose.setOnClickListener(view -> dismiss());
        keyboardView.setOnKeyBoardClickListener(this);
        mList = keyboardView.getListData();
        numbers = sixPwdView.getNumbers();
        points = sixPwdView.getPoints();

        // 这里给每个 FrameLayout 添加点击事件，当键盘被收起时点击空白输入框，再次弹出键盘
        // 微信也是这样的，但我觉得并没有什么意义
        for (int i = 0; i < sixPwdView.getLayouts().length; i++) {
            final int finalI = i;
            sixPwdView.getLayouts()[i].setOnClickListener(view -> {
                if (points[finalI].getVisibility() != View.VISIBLE && !keyboardView.isVisible()) {
                    keyboardView.show();
                }
            });
        }
    }

    // 可以自定义一些方法
    public PayPopupWindow setIcon(String url) {
        // 设置头像
        return this;
    }

    public PayPopupWindow setTitle(CharSequence title) {
        tvTitle.setText(title);
        return this;
    }

    public PayPopupWindow setMessage(CharSequence message) {
        tvMessage.setText(message);
        return this;
    }

    public PayPopupWindow setPrice(CharSequence price) {
        tvPrice.setText(price);
        return this;
    }

    /** 弹出 PopupWindow */
    public void show(View rootView) {
        showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onKeyClick(View view, RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case 9: // 点击小数点没有作用，最好是把小数点隐藏掉，我这里偷懒了
                break;
            default:
                if (currentIndex >= 0 && currentIndex < numbers.length) {
                    numbers[currentIndex] = mList.get(position);
                    points[currentIndex].setVisibility(View.VISIBLE);
                    currentIndex++; // 当前位置的密码输入后，位置加一

                    if (currentIndex == numbers.length && listener != null) {
                        // 已经输入了六位数的密码了，回调方法
                        listener.onFinish(sixPwdView.getPassword());
                    }
                }
        }
    }

    @Override
    public void onDeleteClick(View view, RecyclerView.ViewHolder holder, int position) {
        // 点击删除按钮
        if (currentIndex > 0 && currentIndex < numbers.length) {
            currentIndex--;
            numbers[currentIndex] = "";
            points[currentIndex].setVisibility(View.GONE);
        }
    }

    public void setOnPasswordFinishedListener(OnPasswordFinishedListener listener) {
        this.listener = listener;
    }
}
