package com.jbufa.firefighting.Adpater;

import android.content.Context;
import android.view.ViewGroup;

import com.jbufa.firefighting.Holder.MessageHolder;
import com.jbufa.firefighting.model.MessageBean;

import cn.lemon.view.adapter.BaseViewHolder;
import cn.lemon.view.adapter.RecyclerAdapter;

public class MessageAdpater extends RecyclerAdapter<MessageBean> {
    public MessageAdpater(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder<MessageBean> onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        return new MessageHolder(parent);
    }
}
