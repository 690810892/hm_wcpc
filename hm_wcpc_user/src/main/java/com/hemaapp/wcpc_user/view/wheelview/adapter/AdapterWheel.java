package com.hemaapp.wcpc_user.view.wheelview.adapter;

import android.content.Context;

import com.hemaapp.wcpc_user.view.wheelview.WheelAdapter;

/**
 * Created by WangYuxia on 2016/5/11.
 * Adapter class for old wheel adapter (deprecated WheelAdapter class).
 *
 * @deprecated Will be removed soon
 */
public class AdapterWheel extends AbstractWheelTextAdapter {

    // Source adapter
    private WheelAdapter adapter;

    /**
     * Constructor
     * @param context the current context
     * @param adapter the source adapter
     */
    public AdapterWheel(Context context, WheelAdapter adapter) {
        super(context);

        this.adapter = adapter;
    }

    /**
     * Gets original adapter
     * @return the original adapter
     */
    public WheelAdapter getAdapter() {
        return adapter;
    }

    @Override
    public int getItemsCount() {
        return adapter.getItemsCount();
    }

    @Override
    protected CharSequence getItemText(int index) {
        return adapter.getItem(index);
    }

}

