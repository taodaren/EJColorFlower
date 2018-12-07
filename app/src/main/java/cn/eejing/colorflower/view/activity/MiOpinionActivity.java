package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 意见反馈
 */

public class MiOpinionActivity extends BaseActivity {
    private static final String TAG = "MiOpinionActivity";
    private static final int MAX_LENGTH = 500;

    @BindView(R.id.edit_opinion_content)    EditText edContent;
    @BindView(R.id.tv_num_length)           TextView tvLength;

    private int mRestLength = MAX_LENGTH;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_opinion;
    }

    @Override
    public void initView() {
        setToolbar("意见反馈", View.VISIBLE, null, View.GONE);
        setEditEnter();
    }

    private void setEditEnter() {
        // 设置最大可输入字符数
        edContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        // 设置 EditText 输入监听
        edContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRestLength = MAX_LENGTH - edContent.getText().length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mRestLength = MAX_LENGTH - edContent.getText().length();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                mRestLength = MAX_LENGTH - edContent.getText().length();
                tvLength.setText(edContent.getText().length() + getString(R.string.num_500));
                if (mRestLength <= 0) {
                    Toast.makeText(MiOpinionActivity.this, R.string.upper_limit, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.btn_opinion_submit)
    public void onViewClicked() {
        getDataWithFeedBack();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithFeedBack() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("content", edContent.getText().toString());

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.FEED_BACK)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "意见反馈 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                finish();
                                ToastUtil.showShort(bean.getMessage());
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }
}
