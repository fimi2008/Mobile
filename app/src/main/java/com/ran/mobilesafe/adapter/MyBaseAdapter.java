package com.ran.mobilesafe.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 自定义adapter
 *
 * 作者: wangxiang on 15/10/29 18:11
 * 邮箱: vonshine15@163.com
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    public List<T> list;
    public Context mContext;
    public MyBaseAdapter() {
    }

    public MyBaseAdapter(List<T> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}