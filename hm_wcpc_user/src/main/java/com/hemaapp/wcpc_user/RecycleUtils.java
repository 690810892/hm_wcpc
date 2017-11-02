package com.hemaapp.wcpc_user;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;



/**
 *
 */
public class RecycleUtils {

    /**
     * 初始化水平的Recycle
     * @param recyclerView
     */
    public static void initHorizontalRecyle(RecyclerView recyclerView){

        LinearLayoutManager layoutManager = new LinearLayoutManager(hm_WcpcUserApplication.getInstance());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
    }
    public static void initHorizontalRecyleNoScrll(RecyclerView recyclerView){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(hm_WcpcUserApplication.getInstance()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * 初始化水平的Recycle
     * @param recyclerView
     */
    public static void initVerticalRecyle(RecyclerView recyclerView){

        LinearLayoutManager layoutManager = new LinearLayoutManager(hm_WcpcUserApplication.getInstance());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }
    public static void initVerticalRecyleNoScrll(RecyclerView recyclerView){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(hm_WcpcUserApplication.getInstance()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}
