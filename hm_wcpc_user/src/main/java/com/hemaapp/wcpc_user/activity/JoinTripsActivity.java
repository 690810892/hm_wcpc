package com.hemaapp.wcpc_user.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.JoinTripRecyclerAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.FeeCalculationInfor;
import com.hemaapp.wcpc_user.module.PersonCountInfor;
import com.hemaapp.wcpc_user.module.TripListInfor;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/13.
 */
public class JoinTripsActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;

    private TextView text_selectcount; //选择乘车数量
    private ArrayList<PersonCountInfor> counts = new ArrayList<>();
    private ImageView background;
    private LinearLayout fatherview;
    private RecyclerView recyclerView;
    private TextView count_cancle;
    private TextView count_ok;
    private PersonCountInfor infor; //选中的人数
    private JoinTripRecyclerAdapter recyclerAdapter;

    private TextView text_mymoney;
    private TextView text_savemoeny;
    private TextView text_agree;
    private String isAgreed = "0";
    private EditText editText;
    private TextView text_submit;

    private String id; //trips的id
    private String remainnum, maxnumbers;
    private String personcount, success, fail;
    private String content, lng, lat, address, franchisee_id, keytype, distance, district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_jointrips);
        super.onCreate(savedInstanceState);
        district = XtomSharedPreferencesUtil.get(mContext, "district");
        init();
    }

    private void init() {
        int c = Integer.parseInt(remainnum);
        for (int i = 0; i < c; i++) {
            counts.add(i, new PersonCountInfor(String.valueOf(i + 1), false));
        }

        int max = Integer.parseInt(maxnumbers);
        if (c < max) {
            text_agree.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.mipmap.img_agree_s, 0);
            isAgreed = "1";
        }
        text_savemoeny.setVisibility(View.INVISIBLE);
        text_mymoney.setText(BaseUtil.get2double(Double.parseDouble(success)));
    }

    //计算费用
    private void toCalcuteMoney() {
        getNetWorker().feeCalculation(keytype, franchisee_id, distance, personcount, district);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case JOIN_TRIPS:
                showProgressDialog("请稍后...");
                break;
            case FEE_CALCULATION:
                showProgressDialog("正在计算...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case JOIN_TRIPS:
                cancelProgressDialog();
                break;
            case FEE_CALCULATION:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case JOIN_TRIPS:
                HemaArrayResult<TripListInfor> tResult = (HemaArrayResult<TripListInfor>) hemaBaseResult;
                showTextDialog(tResult.getMsg());
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                }, 1000);
                break;
            case FEE_CALCULATION:
                HemaArrayResult<FeeCalculationInfor> fResult = (HemaArrayResult<FeeCalculationInfor>) hemaBaseResult;
                FeeCalculationInfor calinfor = fResult.getObjects().get(0);
                success = calinfor.getSuccessfee();
                fail = calinfor.getFailfee();

                if ("1".equals(isAgreed)) {
                    text_mymoney.setText(BaseUtil.get2double(Double.parseDouble(success)));
                    text_savemoeny.setText("已优惠" + BaseUtil.get2double(Double.parseDouble(fail) - Double.parseDouble(success)) + "元");
                    text_savemoeny.setVisibility(View.VISIBLE);
                } else {
                    text_mymoney.setText(BaseUtil.get2double(Double.parseDouble(success)));
                    text_savemoeny.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case JOIN_TRIPS:
                showTextDialog("抱歉，加入行程失败");
                break;
            case FEE_CALCULATION:
                showTextDialog("费用计算错误");
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask hemaNetTask, HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case JOIN_TRIPS:
                showTextDialog(baseResult.getMsg());
                break;
            case FEE_CALCULATION:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        right = (TextView) findViewById(R.id.title_btn_right);
        title = (TextView) findViewById(R.id.title_text);

        text_selectcount = (TextView) findViewById(R.id.textview_0);
        text_agree = (TextView) findViewById(R.id.textview_4);
        text_mymoney = (TextView) findViewById(R.id.textview_2);
        text_savemoeny = (TextView) findViewById(R.id.textview_3);
        editText = (EditText) findViewById(R.id.edittext);
        text_submit = (TextView) findViewById(R.id.button);
        background = (ImageView) findViewById(R.id.imageview);
        fatherview = (LinearLayout) findViewById(R.id.father);
        count_ok = (TextView) fatherview.findViewById(R.id.textview_1);
        count_cancle = (TextView) fatherview.findViewById(R.id.textview_0);
        recyclerView = (RecyclerView) fatherview.findViewById(R.id.recyclerView);
    }

    @Override
    protected void getExras() {
        id = mIntent.getStringExtra("id");
        success = mIntent.getStringExtra("success");
        maxnumbers = mIntent.getStringExtra("maxnumbers");
        remainnum = mIntent.getStringExtra("remaincount");
        franchisee_id = mIntent.getStringExtra("franchisee_id");
        keytype = mIntent.getStringExtra("keytype");
        distance = mIntent.getStringExtra("distance");
    }

    @Override
    protected void setListener() {
        title.setText("选择乘车人数");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_selectcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPersonCountPopWindow();
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                fatherview.setVisibility(View.GONE);
            }
        });

        text_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.parseInt(remainnum); //剩余座位数
                int max = Integer.parseInt(maxnumbers); //最多乘车人数
                if (c < max) {
                    showTextDialog("此行程已经有人加入");
                    return;
                } else {
                    if ("0".equals(isAgreed)) {
                        isAgreed = "1";
                        text_agree.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.mipmap.img_agree_s, 0);

                        if (!isNull(personcount)) {
                            personcount = infor.getCount();
                            toCalcuteMoney();
                        }
                    } else {
                        isAgreed = "0";
                        text_agree.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.mipmap.img_agree_n, 0);
                        if (!isNull(personcount)) {
                            personcount = remainnum;
                            toCalcuteMoney();
                        }
                    }
                }
            }
        });
        text_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNull(personcount)) {
                    showTextDialog("请选择乘车人数");
                    return;
                }

                content = editText.getText().toString();
                User user = hm_WcpcUserApplication.getInstance().getUser();

                lng = XtomSharedPreferencesUtil.get(mContext, "lng");
                lat = XtomSharedPreferencesUtil.get(mContext, "lat");
                address = XtomSharedPreferencesUtil.get(mContext, "address");
                getNetWorker().joinTrips(user.getToken(), id, personcount, isAgreed, content, text_mymoney.getText().toString(), fail, lng, lat, address);
            }
        });
    }

    private void showPersonCountPopWindow() {
        background.setVisibility(View.VISIBLE);
        fatherview.setVisibility(View.VISIBLE);
        count_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                fatherview.setVisibility(View.GONE);
            }
        });

        count_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                background.setVisibility(View.GONE);
                fatherview.setVisibility(View.GONE);
                if (infor == null) {
                    showTextDialog("抱歉，请选择乘车人数");
                    return;
                }
                personcount = infor.getCount();
                text_selectcount.setText(personcount + "人");
                text_selectcount.setTextColor(mContext.getResources().getColor(R.color.word_black));
                toCalcuteMoney();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        recyclerAdapter = new JoinTripRecyclerAdapter(JoinTripsActivity.this, counts);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void operate(int position) {
        for (int i = 0; i < counts.size(); i++) {
            if (i == position) {
                counts.get(i).setChecked(true);
                infor = counts.get(i);
            } else
                counts.get(i).setChecked(false);
        }
        recyclerAdapter.notifyDataSetChanged();
    }
}
