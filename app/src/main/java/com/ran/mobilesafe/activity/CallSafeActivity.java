package com.ran.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.adapter.MyBaseAdapter;
import com.ran.mobilesafe.bean.BlackNumberInfo;
import com.ran.mobilesafe.bean.PageResult;
import com.ran.mobilesafe.db.dao.BlackNumberDao;
import com.ran.mobilesafe.utils.ToastUtils;

import java.util.List;

public class CallSafeActivity extends AppCompatActivity {

    private ListView listView;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout llPd;
    private final int pageSize = 20;
    private int page = 1; // 默认第一页开始
    private int totalPage = 1;
    private EditText et_page;
    private TextView tv_showPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        et_page = (EditText) findViewById(R.id.et_page);
        tv_showPage = (TextView) findViewById(R.id.tv_showPage);
        listView = (ListView) findViewById(R.id.list_view);

        initUI();
        initData();
    }

    /**
     * 上一页
     * @param view
     */
    public void before(View view){
        if (page <= 1){
            ToastUtils.show(this, "已经是第一页了");
        }else{
            page--;
            initUI();
            initData();
        }
    }

    /**
     * 下一页
     * @param view
     */
    public void next(View view){
        if (page >= totalPage){
            ToastUtils.show(this, "已经是最后一页了");
        }else{
            page++;
            initUI();
            initData();
        }
    }

    /**
     * 跳转
     * @param view
     */
    public void jump(View view){
        String text = et_page.getText().toString();
        if (TextUtils.isEmpty(text)){
            ToastUtils.show(this, "请正确输入页数");
            return;
        }else{
            try {
                int num = Integer.parseInt(text);
                if (num >= 1 && num <= totalPage){
                    page = num;
                    initUI();
                    initData();
                }else{
                    ToastUtils.show(this, "请正确输入页数");
                }
            }catch (Exception e){
                ToastUtils.show(this, "请正确输入页数");
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            llPd.setVisibility(View.INVISIBLE); // 加载完成隐藏加载进度
            CallSafeAdapter callSafeAdapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
            listView.setAdapter(callSafeAdapter);
            et_page.setText(page+"");
            tv_showPage.setText(page + "/" + totalPage);
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                BlackNumberDao dao = new BlackNumberDao(CallSafeActivity.this);
//                blackNumberInfos = dao.queryAll();
                PageResult<BlackNumberInfo> result = dao.queryPar(page, pageSize);
                blackNumberInfos = result.getDatas();
                totalPage = result.getTotalPage();
                SystemClock.sleep(500); // 模拟加载慢的情况.休眠3秒时间
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        // 展示加载的圆圈
        llPd = (LinearLayout) findViewById(R.id.ll_pb);
        llPd.setVisibility(View.VISIBLE);
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
