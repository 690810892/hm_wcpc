//package com.hemaapp.wcpc_user.adapter;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.hemaapp.wcpc_user.R;
//import com.hemaapp.wcpc_user.activity.PublishInforActivity;
//import com.hemaapp.wcpc_user.module.PersonCountInfor;
//
//import java.util.ArrayList;
//
///**
// * Created by WangYuxia on 2016/5/9.
// * 发布行程 -- 选择乘车人数的数据适配器
// */
//public class PersonCountRecyclerAdapter extends
//        RecyclerView.Adapter<PersonCountRecyclerAdapter.MyViewHolder> {
//
//    private ArrayList<PersonCountInfor> types;
//    private PublishInforActivity mContext;
//
//    public PersonCountRecyclerAdapter(PublishInforActivity mContext,
//                                      ArrayList<PersonCountInfor> types) {
//        this.types = types;
//        this.mContext = mContext;
//    }
//
//    @Override
//    public int getItemCount() {
//        return types == null || types.size() == 0 ? 0 : types.size();
//    }
//
//    // 赋值
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, final int position) {
//
//        holder.textView.setText(types.get(position).getCount());
//        if (types.get(position).isChecked()) {
//            holder.textView.setTextColor(0xffffffff);
//            holder.textView.setBackgroundResource(R.drawable.bg_selectcount);
//        } else {
//            holder.textView.setTextColor(0xff737373);
//            holder.textView.setBackgroundColor(0x00000000);
//        }
//
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                mContext.operate(position, 1);
//            }
//        });
//    }
//
//    // 创建
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
//        View view ;
//        view = LayoutInflater.from(mContext).inflate(
//                R.layout.item_personcount, null);
//        MyViewHolder holder = new MyViewHolder(view);
//        return holder;
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//
//        TextView textView;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            textView = (TextView) itemView.findViewById(R.id.textview);
//        }
//    }
//}
