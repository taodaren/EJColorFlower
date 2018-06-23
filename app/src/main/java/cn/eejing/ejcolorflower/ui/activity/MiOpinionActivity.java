package cn.eejing.ejcolorflower.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * @创建者 Taodaren
 * @描述 我的 → 意见反馈
 */
public class MiOpinionActivity extends BaseActivity {
    int MAX_LENGTH = 500;
    int Rest_Length = MAX_LENGTH;

    @BindView(R.id.edit_opinion_content)
    EditText editOpinionContent;
    @BindView(R.id.tv_num_length)
    TextView tvNumLength;
    @BindView(R.id.btn_opinion_submit)
    SuperButton btnOpinionSubmit;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_opinion;
    }

    @Override
    public void initView() {
        setToolbar("意见反馈", View.VISIBLE);
        setEditEnter();
    }

    private void setEditEnter() {
        // 设置最大可输入字符数
        editOpinionContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        // 设置 EditText 输入监听
        editOpinionContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Rest_Length = MAX_LENGTH - editOpinionContent.getText().length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                Rest_Length = MAX_LENGTH - editOpinionContent.getText().length();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                Rest_Length = MAX_LENGTH - editOpinionContent.getText().length();
                tvNumLength.setText(editOpinionContent.getText().length() + getString(R.string.num_500));
                if (Rest_Length <= 0) {
                    Toast.makeText(MiOpinionActivity.this, R.string.upper_limit, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
