package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.RecycleUtils;
import com.hemaapp.wcpc_user.ToLogin;
import com.hemaapp.wcpc_user.adapter.MytripAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * 费用明细
 */
public class FeeInforActivity extends BaseActivity {

    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.tv_base)
    TextView tvBase;
    @BindView(R.id.tv_base_price)
    TextView tvBasePrice;
    @BindView(R.id.lv_base)
    LinearLayout lvBase;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.lv_count)
    LinearLayout lvCount;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.lv_end)
    LinearLayout lvEnd;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.lv_start)
    LinearLayout lvStart;
    @BindView(R.id.tv_bao)
    TextView tvBao;
    @BindView(R.id.lv_bao)
    LinearLayout lvBao;
    @BindView(R.id.tv_couple)
    TextView tvCouple;
    @BindView(R.id.lv_couple)
    LinearLayout lvCouple;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.lv_all)
    LinearLayout lvAll;
    @BindView(R.id.imageView)
    ImageView imageView;
    private User user;
    private String token = "",  start = "",  end = "", price="", count = "",  addstart = "",  addend = "",  couple = "",  all = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fee_infor);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        user = hm_WcpcUserApplication.getInstance().getUser();
        if (isNull(addstart)||Float.parseFloat(addstart)==0){
            lvStart.setVisibility(View.GONE);
        }
        if (isNull(addend)||Float.parseFloat(addend)==0){
            lvEnd.setVisibility(View.GONE);
        }
        if (isNull(couple)||Float.parseFloat(couple)==0){
            lvCouple.setVisibility(View.GONE);
        }
        tvBase.setText(start+"--"+end+"(城区基本费用)");
        tvBasePrice.setText("+"+price+"元");
        tvCount.setText(count+"人");
        tvBao.setText("+"+count+"元");
        tvStart.setText("+"+addstart+"元");
        tvEnd.setText("+"+addend+"元");
        tvCouple.setText("-"+couple+"元");
        tvAll.setText(all+"元");
    }
    @Override
    protected void getExras() {
        start=mIntent.getStringExtra("start");
        end=mIntent.getStringExtra("end");
        count=mIntent.getStringExtra("count");
        addstart=mIntent.getStringExtra("addstart");
        addend=mIntent.getStringExtra("addend");
        couple=mIntent.getStringExtra("couple");
        all=mIntent.getStringExtra("all");
        price=mIntent.getStringExtra("price");
    }

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case REFRESH_BLOG_LIST:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
                break;
            case TRIPS_SAVEOPERATE:
                showProgressDialog("请稍后");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_SAVEOPERATE:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
            case TRIPS_SAVEOPERATE:
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_SAVEOPERATE:
                showTextDialog("操作失败");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
    }


    @Override
    protected void setListener() {
        titleText.setText("费用明细");
        titleBtnRight.setText("计费规则");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 11:
                break;
        }
    }



    @OnClick({R.id.title_btn_left, R.id.title_btn_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
//                Intent  it = new Intent(mContext, SelectCity2Activity.class);
//                startActivity(it);
                Intent  it = new Intent(mContext, ShowInternetPageActivity.class);
                it.putExtra("name", "计费规则");
                SysInitInfo sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
                String path = sysInitInfo.getSys_web_service() + "webview/parm/feeinstruction";
                it.putExtra("path", path);
                startActivity(it);
                break;
        }
    }


}
