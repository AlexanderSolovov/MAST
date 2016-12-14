package com.rmsi.android.mast.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.util.GuiUtility;

public class AttributeAdapter extends BaseAdapter {
    private List<Attribute> attrList;
    private Context context;
    private LayoutInflater inflater;

    public AttributeAdapter(Context contextAct, List<Attribute> attrList) {
        this.context = contextAct;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.attrList = attrList;
    }

    @Override
    public int getCount() {
        return attrList.size();
    }

    @Override
    public Object getItem(int position) {
        return attrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return GuiUtility.createViewFromAttribute((Attribute) getItem(position), inflater);
    }
}
