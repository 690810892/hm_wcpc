package com.hemaapp.wcpc_user.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hemaapp.wcpc_user.BaseRecycleAdapter;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.module.PersonCountInfor;

import java.util.List;

/**
 */
public class PersonCountAdapter extends BaseRecycleAdapter<PersonCountInfor> {
    private Context mContext;
    public PersonCountInfor blog;
    public PersonCountAdapter(Context mContext, List<PersonCountInfor> datas) {
        super(datas);
        this.mContext=mContext;
    }

    @Override
    protected void bindData(BaseViewHolder holder, final int position) {
        final PersonCountInfor infor=datas.get(position);
        ((TextView)holder.getView(R.id.textview)).setText(infor.getCount());
        if (infor.isChecked()){
            ((TextView)holder.getView(R.id.textview)).setTextColor(0xffffffff);
            ((TextView)holder.getView(R.id.textview)).setBackgroundResource(R.drawable.bg_selectcount);
        }else {
            ((TextView)holder.getView(R.id.textview)).setTextColor(0xff737373);
            ((TextView)holder.getView(R.id.textview)).setBackgroundColor(0x00000000);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (PersonCountInfor da:datas){
                    da.setChecked(false);
                }
                infor.setChecked(true);
               notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_personcount;
    }
}
