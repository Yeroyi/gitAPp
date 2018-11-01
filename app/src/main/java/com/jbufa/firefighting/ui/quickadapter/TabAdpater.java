package com.jbufa.firefighting.ui.quickadapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jbufa.firefighting.R;
import com.jbufa.firefighting.model.GroupBean;
import com.jbufa.firefighting.model.PlaceBean;

import java.util.List;

public class TabAdpater extends BaseAdapter {
    private Context context;
    private List<PlaceBean> list;
    private int position;
    Holder hold;

    public TabAdpater(Context context, List<PlaceBean> list, int position) {
        this.context = context;
        this.list = list;
        this.position = position;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }

    }

    @Override
    public PlaceBean getItem(int i) {
        if (list != null) {
            return list.get(i);
        } else {
            return null;
        }

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setSelectItem(int position) {
        this.position = position;
        Log.e("ceshi", "postion: " + this.position);
    }

    public void addData(List<PlaceBean> list) {
        if (this.list != null) {
            this.list.clear();
            this.list.addAll(list);
        } else {
            this.list = list;
        }
    }

    public void clear() {
        if (this.list != null) {
            this.list.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context) //
                    .inflate(R.layout.item_tab_list_selecter, viewGroup, false);
            hold = new Holder(view);
            view.setTag(hold);
        } else {
            hold = (Holder) view.getTag();
        }

        if (list != null && list.size() > 0) {
            hold.txt.setText(list.get(i).getPlaceName());
            if(i==position){
                hold.txt.setTextColor(Color.parseColor("#24BBEE"));
                hold.tip_image.setVisibility(View.VISIBLE);
            }else {
                hold.txt.setTextColor(Color.parseColor("#434343"));
                hold.tip_image.setVisibility(View.INVISIBLE);
            }
        }

        return view;
    }

    private static class Holder {
        ImageView tip_image;
        TextView txt;

        public Holder(View view) {
            txt = view.findViewById(R.id.tabName);
            tip_image = view.findViewById(R.id.tip_image);
        }
    }
}
