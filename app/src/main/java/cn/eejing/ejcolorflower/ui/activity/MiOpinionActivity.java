package cn.eejing.ejcolorflower.ui.activity;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.model.request.FeedBackBean;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * 意见反馈
 */

public class MiOpinionActivity extends BaseActivity {
    int MAX_LENGTH = 500;
    int Rest_Length = MAX_LENGTH;

    @BindView(R.id.edit_opinion_content)
    EditText edContent;
    @BindView(R.id.tv_num_length)
    TextView tvLength;
    @BindView(R.id.btn_opinion_submit)
    SuperButton btnSubmit;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_opinion;
    }

    @Override
    public void initView() {
        setToolbar("意见反馈", View.VISIBLE);
        setEditEnter();
    }

    @Override
    public void initListener() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataWithFeedBack();
            }
        });
    }

    private void setEditEnter() {
        // 设置最大可输入字符数
        edContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        // 设置 EditText 输入监听
        edContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Rest_Length = MAX_LENGTH - edContent.getText().length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Rest_Length = MAX_LENGTH - edContent.getText().length();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                Rest_Length = MAX_LENGTH - edContent.getText().length();
                tvLength.setText(edContent.getText().length() + getString(R.string.num_500));
                if (Rest_Length <= 0) {
                    Toast.makeText(MiOpinionActivity.this, R.string.upper_limit, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDataWithFeedBack() {
        OkGo.<String>post(Urls.FEED_BACK)
                .tag(this)
                .params("content", edContent.getText().toString())
                .params("mobile", Settings.getLoginSessionInfo(this).getUsername())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "feed_back request succeeded --->" + body);

                        Gson gson = new Gson();
                        FeedBackBean bean = gson.fromJson(body, FeedBackBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                Toast.makeText(MiOpinionActivity.this, "意见反馈成功", Toast.LENGTH_SHORT).show();
                                finish();
                                break;
                            case 2:
                                Toast.makeText(MiOpinionActivity.this, "请输入反馈意见", Toast.LENGTH_SHORT).show();
                                break;
                            case 0:
                                Toast.makeText(MiOpinionActivity.this, "意见反馈失败", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

}
