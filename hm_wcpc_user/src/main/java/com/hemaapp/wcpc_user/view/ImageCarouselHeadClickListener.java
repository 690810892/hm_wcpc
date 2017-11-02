package com.hemaapp.wcpc_user.view;

import android.content.Context;
import android.content.Intent;

import com.hemaapp.hm_FrameWork.showlargepic.ShowLargePicActivity;
import com.hemaapp.wcpc_user.activity.ShowInternetPageActivity;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.AddListInfor;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;

/**首页和个模块的头部图片轮播监听事件
 * Created by UU on 2016/9/24.
 */

public class ImageCarouselHeadClickListener implements ImageCarouselBanner.ImageCarouselListener {

    private Context mContext;
    private ArrayList<AddListInfor> mDatas = new ArrayList<>();
    private String AdType = "0";//广告类型	1首页广告;2药品详情大图
    private ArrayList<String> imagelist = new ArrayList<>();
    public ImageCarouselHeadClickListener(Context context, ArrayList<AddListInfor> datas, String type) {
        mContext = context;
        mDatas= datas;
        AdType = type;
        if(datas!=null && datas.size()>0){
            for(int i = 0; i<datas.size(); i++){
                imagelist.add(i, datas.get(i).getImgurlbig());
            }
        }
    }
    @Override
    public void onItemClickListener(int index) {
        AddListInfor add = mDatas.get(index);
        User user = hm_WcpcUserApplication.getInstance().getUser();
        String token = user == null?"":user.getToken();
        SysInitInfo sysInitInfo= hm_WcpcUserApplication.getInstance().getSysInitInfo();
        String path;
        //跳转处理
        Intent it;
        if(AdType.equals("2")){
            it = new Intent(mContext, ShowLargePicActivity.class);
            it.putExtra("imagelist", imagelist);
            it.putExtra("position", index);
            it.putExtra("title_str", add.getContent());
            it.putExtra("titleAndContentVisible", false);
            it.putExtra("bottomHeight", 1);
            it.putExtra("content_str", "");
            mContext.startActivity(it);
        } else
            switch (Integer.parseInt(add.getKeytype())) {
                case 1://web链接
                    path=sysInitInfo.getSys_plugins()+ "share/advertise.php?id="+add.getId();
                    it = new Intent(mContext, ShowInternetPageActivity.class);
                    it.putExtra("name", "广告详情");
                    it.putExtra("path", path);
                    mContext.startActivity(it);
                    break;
                case 2://
                    break;
                default:
                    break;
            }
    }
}
