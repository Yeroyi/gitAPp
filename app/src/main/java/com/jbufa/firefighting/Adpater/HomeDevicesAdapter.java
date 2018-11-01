package com.jbufa.firefighting.Adpater;

import android.content.Context;
import android.view.ViewGroup;

import com.jbufa.firefighting.Holder.RecordsHolder;
import com.jbufa.firefighting.activity.HomeDevices;
import com.jbufa.firefighting.model.Records;

import cn.lemon.view.adapter.BaseViewHolder;
import cn.lemon.view.adapter.RecyclerAdapter;

public class HomeDevicesAdapter extends RecyclerAdapter<Records> {
    private HomeDevices homeDevices;
    public HomeDevicesAdapter(Context context, HomeDevices homeDevices) {
        super(context);
        this.homeDevices=homeDevices;
    }
    @Override
    public BaseViewHolder<Records> onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        return new RecordsHolder(parent,homeDevices);
    }
}
