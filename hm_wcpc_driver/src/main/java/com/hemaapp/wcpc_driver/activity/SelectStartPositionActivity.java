package com.hemaapp.wcpc_driver.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.adapter.SelectPositionAdapter;

import java.util.List;

import xtom.frame.view.XtomListView;

/**
 * Created by WangYuxia on 2016/6/23.
 * 起始点/目的地 搜索
 */
public class SelectStartPositionActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {

    private EditText editText;
    private TextView button;
    private XtomListView listView;

    private String content;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;//搜索
    private String citycode = "";//搜索城市
    private List<PoiItem> poiItems ;

    private SelectPositionAdapter adapter;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_searchstartposition);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {

    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {

    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {

    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {

    }

    @Override
    protected void findView() {
        editText = (EditText) findViewById(R.id.edittext);
        button = (TextView) findViewById(R.id.button);
        listView = (XtomListView) findViewById(R.id.listview);
    }

    @Override
    protected void getExras() {
        citycode = mIntent.getStringExtra("citycode");
        type = mIntent.getStringExtra("type");
    }

    @Override
    protected void setListener() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 0)
                    button.setText("搜索");
                else
                    button.setText("取消");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = ((TextView) v).getText().toString();
                if(value.equals("取消"))
                    finish();
                else {
                    content = editText.getText().toString();
                    if(isNull(content)){
                        showTextDialog("抱歉，请输入搜索内容");
                        return;
                    }

                    toStartSearch();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter != null && !adapter.isEmpty()){
                    PoiItem item = poiItems.get(position);
                    mIntent.putExtra("lng", item.getLatLonPoint().getLongitude());
                    mIntent.putExtra("lat", item.getLatLonPoint().getLatitude());
                    mIntent.putExtra("name", item.getTitle());
                    mIntent.putExtra("city", item.getCityName());
                    setResult(RESULT_OK, mIntent);
                    finish();
                }
            }
        });
    }

    private void toStartSearch(){
        String style = "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|" +
                "医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|" +
                "金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";
        query = new PoiSearch.Query(content, style, citycode);
        /**
         * keyWord表示搜索字符串，
         * 第二个参数表示POI搜索类型，二者选填其一，
         * POI搜索类型共分为以下20种：汽车服务|汽车销售|
         * 汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
         * 住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
         * 金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
         * cityCode表示POI搜索区域的编码，是必须设置参数
         * */
        query.setPageSize(100);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查询页码
        poiSearch = new PoiSearch(this,query);//初始化poiSearch对象
        poiSearch.setOnPoiSearchListener(this);//设置回调数据的监听器
        poiSearch.searchPOIAsyn();//开始搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    // 取得搜索到的poiitems有多少页
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    if(adapter == null){
                        adapter = new SelectPositionAdapter(mContext, poiItems);
                        adapter.setEmptyString("抱歉，没有搜索到相关信息");
                        listView.setAdapter(adapter);
                    }else{
                        adapter.setItems(poiItems);
                        adapter.setEmptyString("抱歉，没有搜索到相关信息");
                        adapter.notifyDataSetChanged();
                    }
                }
            } else {
                Toast.makeText(mContext, "该距离内没有找到结果", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "异常代码---"+i, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }
}
