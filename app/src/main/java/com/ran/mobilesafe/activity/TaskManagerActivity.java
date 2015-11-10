package com.ran.mobilesafe.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ran.mobilesafe.R;
import com.ran.mobilesafe.bean.AppInfo;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.utils.AppUtils;
import com.ran.mobilesafe.utils.SharedPreferencesUtils;
import com.ran.mobilesafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 进程管理
 */
public class TaskManagerActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_task_size)
    private TextView tv_task_size;
    @ViewInject(R.id.tv_task_memory)
    private TextView tv_task_memory;
    @ViewInject(R.id.list_view)
    private ListView list_view;
    @ViewInject(R.id.tv_apps)
    private TextView tvApps;
    @ViewInject(R.id.ll_pb)
    private LinearLayout llpb;
    @ViewInject(R.id.ll_btn)
    private LinearLayout ll_btn;

    private List<AppInfo> userApps;
    private List<AppInfo> systemApps;

    private long availMemory;
    private String totalMemory;
    private AppAdapter appAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != appAdapter) {
            appAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {
        if (null != userApps && userApps.size() > 0) {
            for (AppInfo info : userApps) {
                // 判断当前的用户程序是不是自己的程序.如果是自己的程序,则跳过不处理
                if (info.getPackageName().equals(getPackageName())) {
                    continue;
                }
                info.setIsChecked(true);
            }
        }
        if (null != systemApps && systemApps.size() > 0) {
            for (AppInfo info : systemApps) {
                info.setIsChecked(true);
            }
        }
        appAdapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void invertSelect(View view) {
        if (null != userApps && userApps.size() > 0) {
            for (AppInfo info : userApps) {
                // 判断当前的用户程序是不是自己的程序.如果是自己的程序,则跳过不处理
                if (info.getPackageName().equals(getPackageName())) {
                    continue;
                }
                info.setIsChecked(!info.isChecked());
            }
        }
        if (null != systemApps && systemApps.size() > 0) {
            for (AppInfo info : systemApps) {
                info.setIsChecked(!info.isChecked());
            }
        }
        appAdapter.notifyDataSetChanged();
    }

    /**
     * 清理
     *
     * @param view
     */
    public void clear(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        int availMem = 0;
        if (null != userApps && userApps.size() > 0) {
            Iterator<AppInfo> iterator = userApps.iterator();
            while (iterator.hasNext()) {
                AppInfo next = iterator.next();
                if (next.isChecked()) {
                    am.killBackgroundProcesses(next.getPackageName());
                    iterator.remove();
                    count++;
                    availMem += next.getAppSize();
                }
            }
        }
        if (null != systemApps && systemApps.size() > 0) {
            Iterator<AppInfo> iterator = systemApps.iterator();
            while (iterator.hasNext()) {
                AppInfo next = iterator.next();
                if (next.isChecked()) {
                    am.killBackgroundProcesses(next.getPackageName());
                    iterator.remove();
                    count++;
                    availMem += next.getAppSize();
                }
            }
        }

        ToastUtils.show(this, "成功清理了" + count + "个进程,释放了" + Formatter.formatFileSize(this, availMem) + "内存");
        appAdapter.notifyDataSetChanged();

        availMemory -= availMem;
        tv_task_size.setText("运行中进程:" + (userApps.size() + systemApps.size()) + "个");
        tv_task_memory.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, availMemory) + "/" + totalMemory);
    }

    /**
     * 设置
     *
     * @param view
     */
    public void setting(View view) {
        startActivity(new Intent(this, TaskManagerSettingActivity.class));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                availMemory = AppUtils.getAvailMemoryL(TaskManagerActivity.this);
                totalMemory = AppUtils.getTotalMemory(TaskManagerActivity.this);

                List<AppInfo> appInfos = AppUtils.queryRunningProcess(TaskManagerActivity.this, false);
                userApps = new ArrayList<AppInfo>();
                systemApps = new ArrayList<AppInfo>();
                for (AppInfo info : appInfos) {
                    if (info.isUserApp()) {
                        userApps.add(info);
                    } else {
                        systemApps.add(info);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            llpb.setVisibility(View.INVISIBLE);
            tvApps.setVisibility(View.VISIBLE);
            ll_btn.setVisibility(View.VISIBLE);
            tv_task_size.setText("运行中进程:" + (userApps.size() + systemApps.size()) + "个");
            tv_task_memory.setText("剩余/总内存:" + Formatter.formatFileSize(TaskManagerActivity.this, availMemory) + "/" + totalMemory);
            appAdapter = new AppAdapter();
            list_view.setAdapter(appAdapter);
        }
    };

    /**
     * 初始化UI
     */
    private void initUI() {
        setContentView(R.layout.activity_task_manager);
        ViewUtils.inject(this);
        llpb.setVisibility(View.VISIBLE);
        ll_btn.setVisibility(View.INVISIBLE);

        // 设置listviewd的滚动监听
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             * 屏幕滚动时触发
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (null != userApps && null != systemApps) {
                    if (firstVisibleItem > (userApps.size() + 1)) {
                        //系统应用程序
                        tvApps.setText("系统程序(" + systemApps.size() + ")个");
                    } else {
                        //用户应用程序
                        tvApps.setText("用户程序(" + userApps.size() + ")个");
                    }
                }
            }
        });

        // 明细绑定点击事件
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 得到当前点击listview的对象
                Object object = list_view.getItemAtPosition(position);

                if (null != object && object instanceof AppInfo) {
                    AppInfo info = (AppInfo) object;

                    if (info.getPackageName().equals(getPackageName())) {
                        return;
                    }
                    AppAdapter.ViewHolder holder = (AppAdapter.ViewHolder) view.getTag();

                    /**
                     * 判断当前的item是否被勾选上
                     * 如果被勾选上了。那么就改成没有勾选。 如果没有勾选。就改成已经勾选
                     */
                    if (info.isChecked()) {
                        holder.cb_checked.setChecked(false);
                        info.setIsChecked(false);
                    } else {
                        holder.cb_checked.setChecked(true);
                        info.setIsChecked(true);
                    }
                }
            }
        });
    }

    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            boolean isShowed = SharedPreferencesUtils.getBoolean(TaskManagerActivity.this, ParameterUtils.IS_SHOW_SYSTEM, false);
            if (isShowed) {
                return userApps.size() + systemApps.size() + 2;
            } else {
                return userApps.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == (userApps.size() + 1)) {
                return null;
            }
            AppInfo info;
            if (position < (userApps.size() + 1)) {
                info = userApps.get(position - 1);
            } else {
                info = systemApps.get(position - userApps.size() - 2);
            }
            return info;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView view = new TextView(TaskManagerActivity.this);
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                view.setText("用户程序(" + userApps.size() + ")");
                return view;
            } else if (position == userApps.size() + 1) {
                TextView view = new TextView(TaskManagerActivity.this);
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                view.setText("系统程序(" + systemApps.size() + ")");
                return view;
            }

            ViewHolder holder;
            if (null != convertView && convertView instanceof LinearLayout) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(TaskManagerActivity.this, R.layout.item_task_list, null);
                holder = new ViewHolder();
                holder.ivicon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvappname = (TextView) convertView.findViewById(R.id.tv_appname);
                holder.tvrom = (TextView) convertView.findViewById(R.id.tv_rom);
                holder.cb_checked = (CheckBox) convertView.findViewById(R.id.cb_checked);
                convertView.setTag(holder);
            }

            AppInfo appInfo;
            if (position <= userApps.size()) {
                appInfo = userApps.get(position - 1);
            } else {
                appInfo = systemApps.get(position - userApps.size() - 2);
            }
            holder.ivicon.setImageDrawable(appInfo.getIcon());
            holder.tvappname.setText(appInfo.getAppName());
            StringBuffer sb = new StringBuffer("占用内存:");
            sb.append(Formatter.formatFileSize(TaskManagerActivity.this, appInfo.getAppSize()));
            holder.tvrom.setText(sb.toString());

            if (appInfo.isChecked()) {
                holder.cb_checked.setChecked(true);
            } else {
                holder.cb_checked.setChecked(false);
            }
            // 判断是否是自己应用,是则隐藏checkbox
            if (appInfo.getPackageName().equals(getPackageName())) {
                holder.cb_checked.setVisibility(View.INVISIBLE);
            } else {
                holder.cb_checked.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            private ImageView ivicon;
            private TextView tvappname;
            private TextView tvrom;
            private CheckBox cb_checked;
        }
    }
}