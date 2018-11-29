package cn.eejing.colorflower.view.customize;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.eejing.colorflower.R;
import cn.eejing.colorflower.view.adapter.KeyboardAdapter;

/**
 * 支付键盘输入布局
 */

public class KeyboardView extends RelativeLayout {

    private LinearLayout mCloseKeyboard;
    private RecyclerView mRecyclerView;
    private List<String> mList;
    private KeyboardAdapter mAdapter;
    private Animation animationIn;
    private Animation animationOut;

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_pay_keyboard, this);
        mCloseKeyboard = findViewById(R.id.layout_close_keyboard);
        mCloseKeyboard.setOnClickListener(view -> {
            // 点击关闭键盘
            dismiss();
        });
        mRecyclerView = findViewById(R.id.recycler_view);

        initData();
        initView();
        initAnimation();
    }

    /** 填充数据 */
    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            if (i < 9) {
                mList.add(String.valueOf(i + 1));
            } else if (i == 10) {
                mList.add("0");
            } else {
                mList.add("");
            }
        }
    }

    /** 设置适配器 */
    private void initView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new KeyboardAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /** 初始化动画效果 */
    private void initAnimation() {
        animationIn = AnimationUtils.loadAnimation(getContext(), R.anim.pop_show);
        animationOut = AnimationUtils.loadAnimation(getContext(), R.anim.pop_hide);
    }

    /** 弹出软键盘 */
    public void show() {
        startAnimation(animationIn);
        setVisibility(VISIBLE);
    }

    /** 关闭软键盘 */
    public void dismiss() {
        if (isVisible()) {
            startAnimation(animationOut);
            setVisibility(GONE);
        }
    }

    /** 判断软键盘的状态 */
    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    public void setOnKeyBoardClickListener(KeyboardAdapter.OnKeyboardClickListener listener) {
        mAdapter.setOnKeyboardClickListener(listener);
    }

    public List<String> getListData() {
        return mList;
    }

    public LinearLayout getCloseKeyboard() {
        return mCloseKeyboard;
    }
}
