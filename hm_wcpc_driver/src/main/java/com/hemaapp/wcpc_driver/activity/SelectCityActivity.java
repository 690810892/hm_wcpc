package com.hemaapp.wcpc_driver.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.LocationAdapter;
import com.hemaapp.wcpc_driver.module.DistrictInfor;
import com.hemaapp.wcpc_driver.view.LetterListView;
import com.hemaapp.wcpc_driver.view.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WangYuxia on 2016/6/24.
 */
public class SelectCityActivity extends BaseActivity {

    private ImageView left;
    private TextView title;
    private TextView right;
    private TextView text_loccity;
    private ListView listView;
    private LetterListView letterListView;
    private String city;

    private ArrayList<DistrictInfor> allDistricts = new ArrayList<>();
    private TextView overlay; //点击字母索引列表后，出现的额内容

    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private Handler handler;
    private OverlayThread overlayThread;
    private WindowManager windowManager;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private LocationAdapter mLocationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_selectcity);
        super.onCreate(savedInstanceState);
        text_loccity.setText("当前定位城市："+city);
        handler = new Handler();
        overlayThread = new OverlayThread();
        initOverlay();
        pinyinComparator = new PinyinComparator();
        getNetWorker().districtList("-1");
    }

    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    @Override
    protected void onDestroy() {
        windowManager.removeView(overlay);
        super.onDestroy();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DISTRICT_LIST:
                showProgressDialog("正在获取城市列表");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DISTRICT_LIST:
                cancelProgressDialog();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DISTRICT_LIST:
                HemaPageArrayResult<DistrictInfor> dResult = (HemaPageArrayResult<DistrictInfor>) baseResult;
                allDistricts = dResult.getObjects();

                Collections.sort(allDistricts, pinyinComparator);
                mLocationAdapter.setList(allDistricts);
                setAdapter(allDistricts);
                mLocationAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    /**
     * 为ListView设置适配器
     *
     * @param list
     */
    private void setAdapter(List<DistrictInfor> list) {
        if (list != null) {
            listView.setAdapter(mLocationAdapter);
            alphaIndexer = mLocationAdapter.getAlphaIndexer();
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DISTRICT_LIST:
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case DISTRICT_LIST:
                showTextDialog("获取地区列表失败");
                break;
            default:
                break;
        }
    }
    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        title = (TextView) findViewById(R.id.title_text);
        right = (TextView) findViewById(R.id.title_btn_right);
        text_loccity = (TextView) findViewById(R.id.textview);
        listView = (ListView) findViewById(R.id.listview);
        letterListView = (LetterListView) findViewById(R.id.listview0);
        mLocationAdapter = new LocationAdapter(this);
    }

    @Override
    protected void getExras() {
        city = mIntent.getStringExtra("city");
    }

    @Override
    protected void setListener() {
        title.setText("选择城市");
        right.setVisibility(View.INVISIBLE);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        letterListView
                .setOnTouchingLetterChangedListener(new LetterListViewListener());
    }

    public void onItemClick(DistrictInfor infor){
        mIntent.putExtra("name", infor.getName());
        setResult(RESULT_OK, mIntent);
        finish();
    }

    private class LetterListViewListener implements
            LetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer != null && alphaIndexer.get(s) != null) {
                final int position = alphaIndexer.get(s);
                listView.setSelection(position);
            } else if ("↑".equals(s)) {
                listView.setSelection(0);
            }
            overlay.setText(s);
            overlay.setVisibility(View.VISIBLE);
            handler.removeCallbacks(overlayThread);
            // 延迟一秒后执行，让overlay为不可见
            handler.postDelayed(overlayThread, 1500);
        }
    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {

        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }
}
