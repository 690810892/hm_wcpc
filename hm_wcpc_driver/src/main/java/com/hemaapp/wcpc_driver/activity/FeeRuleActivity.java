package com.hemaapp.wcpc_driver.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.MyRecyclerAdapter;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.FeeRuleListInfor;
import com.hemaapp.wcpc_driver.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/25.
 */
public class FeeRuleActivity extends BaseActivity {
    private ImageView left;
    private TextView title;
    private TextView right;

    private LinearLayout layout_cityin, layout_cityout;
    private ImageView image_cityin, image_cityout;
    private TextView text_cityin, text_cityout;

    private String keytype = "1";
    private View view_in, view_out;
    private TextView in_text_city, out_text_city;
    private TextView in_text_count, out_text_count;
    private TextView in_text_distance, in_text_distance_fee, in_text_distance_over, in_text_distance_over_fee;
    private TextView out_text_distance, out_text_distance_fee, out_text_distance_over, out_text_distance_over_fee;
    private TextView in_fail_distance, in_fail_fee, in_fail_distance_over, in_fail_fee_over;
    private TextView out_fail_distance, out_fail_fee, out_fail_distance_over, out_fail_fee_over;

    private ArrayList<FeeRuleListInfor> cityins = new ArrayList<>();
    private ArrayList<FeeRuleListInfor> cityouts = new ArrayList<>();

    private String city;
    private int flag = 1;

    private User user;

    private ImageView background;
    private LinearLayout in_fatherview;
    private TextView in_ok;
    private TextView in_cancel;
    private RecyclerView in_recyclerView;
    private MyRecyclerAdapter in_recyclerAdapter;

    private LinearLayout out_fatherview;
    private TextView out_ok;
    private TextView out_cancel;
    private RecyclerView out_recyclerView;
    private MyRecyclerAdapter out_recyclerAdapter;

    private FeeRuleListInfor in_infor;
    private FeeRuleListInfor out_infor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feerule);
        super.onCreate(savedInstanceState);
        setTopBackGround(Integer.parseInt(keytype));
        user = hm_WcpcDriverApplication.getInstance().getUser();
        getInfor(keytype);
        city = XtomSharedPreferencesUtil.get(mContext, "city");
        in_text_city.setText(city);
        out_text_city.setText(city);
    }

    private void getInfor(String keytype){
        String district = XtomSharedPreferencesUtil.get(mContext, "district");
        getNetWorker().feeRuleList( user.getFranchisee_id(), district);
    }

    private void setTopBackGround(int type){
        if(type == 1){
            flag = 1;
            image_cityin.setVisibility(View.VISIBLE);
            text_cityin.setTextColor(mContext.getResources().getColor(R.color.yellow));
            image_cityout.setVisibility(View.INVISIBLE);
            text_cityout.setTextColor(mContext.getResources().getColor(R.color.qianhui));
            view_in.setVisibility(View.VISIBLE);
            view_out.setVisibility(View.GONE);
        }else{
            flag = 2;
            image_cityout.setVisibility(View.VISIBLE);
            text_cityout.setTextColor(mContext.getResources().getColor(R.color.yellow));
            image_cityin.setVisibility(View.INVISIBLE);
            text_cityin.setTextColor(mContext.getResources().getColor(R.color.qianhui));
            view_in.setVisibility(View.GONE);
            view_out.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                HemaPageArrayResult<FeeRuleListInfor> fReslut = (HemaPageArrayResult<FeeRuleListInfor>) hemaBaseResult;
                if(flag == 1){
                    cityins = fReslut.getObjects();
                }else{
                    cityouts = fReslut.getObjects();
                }
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case FEE_RULE_LIST:
                showTextDialog("抱歉，获取数据失败");
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        layout_cityin = (LinearLayout) findViewById(R.id.layout_0);
        text_cityin = (TextView) findViewById(R.id.textview_0);
        image_cityin = (ImageView) findViewById(R.id.imageview_0);
        layout_cityout = (LinearLayout) findViewById(R.id.layout_1);
        text_cityout = (TextView) findViewById(R.id.textview_1);
        image_cityout = (ImageView) findViewById(R.id.imageview_1);

        view_in = findViewById(R.id.scrollView_in);
        in_text_city = (TextView) view_in.findViewById(R.id.textview_2);
        in_text_count = (TextView) view_in.findViewById(R.id.textview_3);
        in_text_distance = (TextView) view_in.findViewById(R.id.textview_4);
        in_text_distance_fee = (TextView) view_in.findViewById(R.id.textview_5);
        in_text_distance_over= (TextView) view_in.findViewById(R.id.textview_6);
        in_text_distance_over_fee = (TextView) view_in.findViewById(R.id.textview_7);
        in_fail_distance = (TextView) view_in.findViewById(R.id.fail_0);
        in_fail_fee = (TextView) view_in.findViewById(R.id.fail_1);
        in_fail_distance_over = (TextView) view_in.findViewById(R.id.fail_2);
        in_fail_fee_over = (TextView) view_in.findViewById(R.id.fail_3);

        view_out = findViewById(R.id.scrollView_out);
        out_text_city = (TextView) view_out.findViewById(R.id.textview_2);
        out_text_count = (TextView) view_out.findViewById(R.id.textview_3);
        out_text_distance = (TextView) view_out.findViewById(R.id.textview_4);
        out_text_distance_fee = (TextView) view_out.findViewById(R.id.textview_5);
        out_text_distance_over= (TextView) view_out.findViewById(R.id.textview_6);
        out_text_distance_over_fee = (TextView) view_out.findViewById(R.id.textview_7);
        out_fail_distance = (TextView) view_out.findViewById(R.id.fail_4);
        out_fail_fee = (TextView) view_out.findViewById(R.id.fail_5);
        out_fail_distance_over = (TextView) view_out.findViewById(R.id.fail_6);
        out_fail_fee_over = (TextView) view_out.findViewById(R.id.fail_7);

        background = (ImageView) findViewById(R.id.imageview);
        in_fatherview = (LinearLayout) findViewById(R.id.father);
        in_ok = (TextView) in_fatherview.findViewById(R.id.textview_1);
        in_cancel = (TextView) in_fatherview.findViewById(R.id.textview_0);
        in_recyclerView = (RecyclerView) in_fatherview.findViewById(R.id.recyclerView);

        out_fatherview = (LinearLayout) findViewById(R.id.father1);
        out_ok = (TextView) out_fatherview.findViewById(R.id.textview_1);
        out_cancel = (TextView) out_fatherview.findViewById(R.id.textview_0);
        out_recyclerView = (RecyclerView) out_fatherview.findViewById(R.id.recyclerView1);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        title.setText("计费规则");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        in_text_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCount(1);
            }
        });

        out_text_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCount(2);
            }
        });
        setListener(layout_cityin);
        setListener(layout_cityout);
        setListener(background);
    }

    private void showSelectCount(final int type){
        if(type == 1){
            background.setVisibility(View.VISIBLE);
            in_fatherview.setVisibility(View.VISIBLE);
            out_fatherview.setVisibility(View.GONE);
            in_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    in_fatherview.setVisibility(View.GONE);
                }
            });

            in_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    in_fatherview.setVisibility(View.GONE);
                    if(in_infor == null){
                        showTextDialog("请选择乘车人数");
                        return;
                    }
                    in_text_count.setText(in_infor.getNumbers()+"人");

                    in_text_distance.setText(in_infor.getMileage()+"公里内");
                    in_text_distance_fee.setText(in_infor.getSuccessfee()+"元");
                    in_text_distance_over.setText(in_infor.getMileage()+"公里以上部分");
                    in_text_distance_over_fee.setText(in_infor.getSuccessoverfee()+"元/公里");

                    in_fail_distance.setText(in_infor.getMileage()+"公里内");
                    in_fail_fee.setText(in_infor.getFailfee()+"元");
                    in_fail_distance_over.setText(in_infor.getMileage()+"公里以上部分");
                    in_fail_fee_over.setText(in_infor.getFailoverfee()+"元/公里");
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            in_recyclerView.setLayoutManager(linearLayoutManager);
            //设置适配器
            in_recyclerAdapter = new MyRecyclerAdapter(FeeRuleActivity.this, cityins);
            in_recyclerView.setAdapter(in_recyclerAdapter);
        }else{
            background.setVisibility(View.VISIBLE);
            in_fatherview.setVisibility(View.GONE);
            out_fatherview.setVisibility(View.VISIBLE);
            out_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    out_fatherview.setVisibility(View.GONE);
                }
            });

            out_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    background.setVisibility(View.GONE);
                    out_fatherview.setVisibility(View.GONE);
                    if(out_infor == null){
                        showTextDialog("请选择乘车人数");
                        return;
                    }
                    out_text_count.setText(out_infor.getNumbers()+"人");

                    out_text_distance.setText(out_infor.getMileage()+"公里内");
                    out_text_distance_fee.setText(out_infor.getSuccessfee()+"元");
                    out_text_distance_over.setText(out_infor.getMileage()+"公里以上部分");
                    out_text_distance_over_fee.setText(out_infor.getSuccessoverfee()+"元/公里");

                    out_fail_distance.setText(out_infor.getMileage()+"公里内");
                    out_fail_fee.setText(out_infor.getFailfee()+"元");
                    out_fail_distance_over.setText(out_infor.getMileage()+"公里以上部分");
                    out_fail_fee_over.setText(out_infor.getFailoverfee()+"元/公里");
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            out_recyclerView.setLayoutManager(linearLayoutManager);
            //设置适配器
            out_recyclerAdapter = new MyRecyclerAdapter(FeeRuleActivity.this, cityouts);
            out_recyclerView.setAdapter(out_recyclerAdapter);
        }
    }

    public void operate(int position){
        if(flag == 1){
            for(int i = 0; i < cityins.size(); i++){
                if(i == position) {
                    cityins.get(i).setChecked(true);
                    in_infor = cityins.get(i);
                } else
                    cityins.get(i).setChecked(false);
            }
            in_recyclerAdapter.notifyDataSetChanged();
        }else{
            for(int i = 0; i < cityouts.size(); i++){
                if(i == position) {
                    cityouts.get(i).setChecked(true);
                    out_infor = cityouts.get(i);
                } else
                    cityouts.get(i).setChecked(false);
            }
            out_recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.layout_0:
                        setTopBackGround(1);
//                        in_webview.setFocusable(false);
                        if(cityins == null || cityins.size() == 0)
                            getInfor("1");
                        break;
                    case R.id.layout_1:
                        setTopBackGround(2);
//                        out_webview.setFocusable(false);
                        in_text_city.requestFocus();
                        if(cityouts == null || cityouts.size() == 0)
                            getInfor("2");
                        break;
                    case R.id.imageview:
                        background.setVisibility(View.GONE);
                        in_fatherview.setVisibility(View.GONE);
                        out_fatherview.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }
}
