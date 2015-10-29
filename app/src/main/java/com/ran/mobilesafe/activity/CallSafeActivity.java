package com.ran.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.adapter.MyBaseAdapter;
import com.ran.mobilesafe.bean.BlackNumberInfo;
import com.ran.mobilesafe.db.dao.BlackNumberDao;

import java.util.List;

public class CallSafeActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);

        initUI();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        BlackNumberDao dao = new BlackNumberDao(this);
        List<BlackNumberInfo> blackNumberInfos = dao.queryAll();
        CallSafeAdapter callSafeAdapter = new CallSafeAdapter(blackNumberInfos, this);
        listView.setAdapter(callSafeAdapter);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        listView = (ListView) findViewById(R.id.list_view);
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo>  {

        public CallSafeAdapter(List list, Context mContext) {
            super(list, mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BlackNumberInfo info = list.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tvNumber =  (TextView) convertView.findViewById(R.id.tv_number);
                holder.tvMode  = (TextView) convertView.findViewById(R.id.tv_mode);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvNumber.setText(info.getNumber());
            // 1-短信拦截,2-电话拦截,3-全部拦截
            String text = "";
            if (info.getMode() == 1){
                text = "短信拦截";
            }else if (info.getMode() == 2){
                text = "电话拦截";
            }else if (info.getMode() == 3){
                text = "短信拦截+电话拦截";
            }
            holder.tvMode.setText(text);

            return convertView;
        }

        class ViewHolder {
            private TextView tvNumber;
            private TextView tvMode;
        }
    }
}
