package com.ran.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.adapter.MyBaseAdapter;
import com.ran.mobilesafe.bean.BlackNumberInfo;
import com.ran.mobilesafe.bean.PageResult;
import com.ran.mobilesafe.db.dao.BlackNumberDao;
import com.ran.mobilesafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class CallSafeActivity extends AppCompatActivity {

    private ListView listView;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout llPd;
    private BlackNumberDao dao;
    private CallSafeAdapter callSafeAdapter;

    private int start = 0; // 开始位置
    private int pageSize = 20; // 每页展示条数
    private int totalNum;  // 总条数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        listView = (ListView) findViewById(R.id.list_view);

        initUI();
        initData();
        loading();
    }

    /**
     * 添加黑名单
     *
     * @param view
     */
    public void addBlackNumber(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialog_view = View.inflate(this, R.layout.dialog_add_black_number, null);

        final EditText et_number = (EditText) dialog_view.findViewById(R.id.et_number);
        final CheckBox cb_sms = (CheckBox) dialog_view.findViewById(R.id.cb_sms);
        final CheckBox cb_phone = (CheckBox) dialog_view.findViewById(R.id.cb_phone);

        // 确定
        dialog_view.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    ToastUtils.show(CallSafeActivity.this, "请输入黑名单号码");
                    return;
                }
                int mode = 0;
                if (cb_sms.isChecked()) {
                    mode += 1;
                }
                if (cb_phone.isChecked()) {
                    mode += 2;
                }
                if (mode == 0) {
                    ToastUtils.show(CallSafeActivity.this, "请勾选拦截模式");
                    return;
                }


                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(number);
                info.setMode(mode);
                // 将电话号码和拦截模式添加到数据库
                dao.add(number, mode);
                if (null != blackNumberInfos && blackNumberInfos.size() > 0) {
                    blackNumberInfos.add(0, info);
                } else {
                    blackNumberInfos = new ArrayList<BlackNumberInfo>();
                    blackNumberInfos.add(0, info);
                }

                if (null != callSafeAdapter) {
                    if (null != callSafeAdapter.list && callSafeAdapter.list.size() > 0){
                        callSafeAdapter.notifyDataSetChanged();
                    }else{
                        callSafeAdapter.list = blackNumberInfos;
                        listView.setAdapter(callSafeAdapter);
                    }
                } else {
                    callSafeAdapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
                    listView.setAdapter(callSafeAdapter);
                }

                dialog.dismiss();
            }
        });
        // 取消
        dialog_view.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(dialog_view);
        dialog.show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            llPd.setVisibility(View.INVISIBLE); // 加载完成隐藏加载进度
            if (callSafeAdapter == null) {
                callSafeAdapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity.this);
                listView.setAdapter(callSafeAdapter);
            } else {
                callSafeAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                // 分批加载数据
                dao = new BlackNumberDao(CallSafeActivity.this);
                PageResult<BlackNumberInfo> result = dao.queryLoading(start, pageSize);
                if (blackNumberInfos == null) {
                    blackNumberInfos = result.getDatas();
                } else {
                    //把后面的数据。追加到blackNumberInfos集合里面。防止黑名单被覆盖
                    blackNumberInfos.addAll(result.getDatas());
                }
                totalNum = result.getTotal();
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

    /**
     * 分批加载数据
     */
    private void loading() {
        // 设置listview的滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * 状态改变时候回调的方法
             * @param view
             * @param scrollState 表示滚动的状态
             *                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE 闲置状态
             *                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指触摸的时候的状态
             *                    AbsListView.OnScrollListener.SCROLL_STATE_FLING 惯性
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 获取到最后一条显示的数据
                        int lastVisiblePosition = listView.getLastVisiblePosition();
                        if (lastVisiblePosition == blackNumberInfos.size() - 1) {
                            // 加载更多的数据,更改加载数据的开始位置
                            start += pageSize;
                            if (start >= totalNum) {
                                ToastUtils.show(CallSafeActivity.this, "没有更多的数据了");
                                return;
                            }
                            initUI();
                            initData();
                        }
                        break;
                }
            }

            /**
             * listview滚动的时候调用的方法时调用。当我们的手指触摸的屏幕的时候就调用
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {

        public CallSafeAdapter(List list, Context mContext) {
            super(list, mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null != list) {
                final BlackNumberInfo info = list.get(position);
                ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
                    holder = new ViewHolder();
                    holder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                    holder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                    holder.ivDel = (ImageView) convertView.findViewById(R.id.iv_del);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                holder.tvNumber.setText(info.getNumber());
                // 1-短信拦截,2-电话拦截,3-全部拦截
                String text = "";
                if (info.getMode() == 1) {
                    text = "短信拦截";
                } else if (info.getMode() == 2) {
                    text = "电话拦截";
                } else if (info.getMode() == 3) {
                    text = "短信拦截+电话拦截";
                }
                holder.tvMode.setText(text);
                holder.ivDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = info.getNumber();
                        boolean del = dao.del(number);
                        if (del) {
                            ToastUtils.show(CallSafeActivity.this, "删除成功");
                            list.remove(info);
                            // 刷新界面
                            callSafeAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.show(CallSafeActivity.this, "删除失败");
                        }
                    }
                });
            }

            return convertView;
        }

        class ViewHolder {
            private TextView tvNumber;
            private TextView tvMode;
            private ImageView ivDel;
        }
    }
}
