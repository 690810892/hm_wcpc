package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 发布行程
 */
public class SendActivity extends BaseActivity {


    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.tv_start_city)
    TextView tvStartCity;
    @BindView(R.id.iv_change)
    ImageView ivChange;
    @BindView(R.id.tv_end_city)
    TextView tvEndCity;
    @BindView(R.id.lv_city)
    LinearLayout lvCity;
    @BindView(R.id.leftlin)
    LinearLayout leftlin;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_pin)
    TextView tvPin;
    @BindView(R.id.lv_pin)
    LinearLayout lvPin;
    @BindView(R.id.tv_charter)
    TextView tvCharter;
    @BindView(R.id.lv_charter)
    LinearLayout lvCharter;
    @BindView(R.id.ev_content)
    EditText evContent;
    @BindView(R.id.tv_coupon)
    TextView tvCoupon;
    @BindView(R.id.lv_coupon)
    LinearLayout lvCoupon;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_feeinfor)
    TextView tvFeeinfor;
    @BindView(R.id.tv_button)
    TextView tvButton;
    private User user;
    private SysInitInfo infor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_send);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                cancelProgressDialog();
                break;
            case CLIENT_LOGINOUT:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                showTextDialog("检查版本信息失败，请稍后重试");
                break;
            case CLIENT_LOGINOUT:
                showTextDialog("退出登录失败，请稍后重试");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                showTextDialog("检查版本信息失败，请稍后重试");
                break;
            case CLIENT_LOGINOUT:
                showTextDialog("退出登录失败，请稍后重试");
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                break;
            default:
                break;
        }
    }


    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask.getHttpInformation();
        switch (information) {
            case INIT:
                showProgressDialog("请稍后...");
                break;
            case CLIENT_LOGINOUT:
                showProgressDialog("请稍后...");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        titleBtnRight.setText("计费规则");
        titleBtnRight.setTextColor(mContext.getResources().getColor(R.color.title_color));
        titleText.setText("发布跨城行程");
    }
    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.tv_start_city, R.id.iv_change, R.id.tv_end_city, R.id.lv_city, R.id.tv_start, R.id.tv_end, R.id.tv_time, R.id.tv_count, R.id.lv_pin, R.id.lv_charter, R.id.lv_coupon, R.id.tv_feeinfor, R.id.tv_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                break;
            case R.id.tv_start_city:
                break;
            case R.id.iv_change:
                break;
            case R.id.tv_end_city:
                break;
            case R.id.lv_city:
                break;
            case R.id.tv_start:
                break;
            case R.id.tv_end:
                break;
            case R.id.tv_time:
                break;
            case R.id.tv_count:
                break;
            case R.id.lv_pin:
                break;
            case R.id.lv_charter:
                break;
            case R.id.lv_coupon:
                break;
            case R.id.tv_feeinfor:
                break;
            case R.id.tv_button:
                break;
        }
    }
}
