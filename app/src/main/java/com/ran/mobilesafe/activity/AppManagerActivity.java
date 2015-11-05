package com.ran.mobilesafe.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ran.mobilesafe.R;
import com.ran.mobilesafe.bean.AppInfo;
import com.ran.mobilesafe.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {
    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.tv_sd)
    private TextView tvSd;
    @ViewInject(R.id.tv_apps)
    private TextView tvApps;

    private LinearLayout llpb;
    private List<AppInfo> userApps;
    private List<AppInfo> systemApps;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            llpb.setVisibility(View.INVISIBLE);
            tvApps.setVisibility(View.VISIBLE);
            listView.setAdapter(new AppAdapter());
        }
    };


    /**
     * 初始化数据
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                // 查询所有安装到手机上面的应用
                List<AppInfo> appInfos = AppUtils.queryAppInfos(AppManagerActivity.this);
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

    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userApps.size() + systemApps.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 || position == userApps.size() + 1) {
                return null;
            }
            AppInfo info;
            if (position <= userApps.size() + 1) {
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
                TextView view = new TextView(AppManagerActivity.this);
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                view.setText("用户程序(" + userApps.size() + ")");
                return view;
            } else if (position == userApps.size() + 1) {
                TextView view = new TextView(AppManagerActivity.this);
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.GRAY);
                view.setText("系统程序(" + systemApps.size() + ")");
                return view;
            }

            ViewHolder holder;
            if (null != convertView && convertView instanceof LinearLayout) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app_list, null);
                holder = new ViewHolder();
                holder.ivicon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvappname = (TextView) convertView.findViewById(R.id.tv_appname);
                holder.tvrom = (TextView) convertView.findViewById(R.id.tv_rom);
                holder.tvappsize = (TextView) convertView.findViewById(R.id.tv_appsize);
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
            holder.tvrom.setText(appInfo.isRom() ? "手机内存" : "SD卡");
            holder.tvappsize.setText("应用大小:" + Formatter.formatFileSize(AppManagerActivity.this, appInfo.getAppSize()));
            return convertView;
        }

        private class ViewHolder {
            private ImageView ivicon;
            private TextView tvappname;
            private TextView tvrom;
            private TextView tvappsize;
        }
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        setContentView(R.layout.activity_app_manager);
        llpb = (LinearLayout) findViewById(R.id.ll_pb);
        llpb.setVisibility(View.VISIBLE);
        ViewUtils.inject(this);
        // 获取到rom内存的运行的剩余空间
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();

        // 获取到sd卡的剩余空间
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        // 格式化大小
        tvRom.setText("内存可用:" + Formatter.formatFileSize(this, rom_freeSpace));
        tvSd.setText("sd卡可用:" + Formatter.formatFileSize(this, sd_freeSpace));

        // 设置listviewd的滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                popupWindowDismiss();

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取当前点击的item对象
                Object obj = listView.getItemAtPosition(position);
                if (null != obj && obj instanceof AppInfo) {
                    clickAppInfo = (AppInfo) obj;
                    View view_popup = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);
                    LinearLayout ll_uninstall = (LinearLayout) view_popup.findViewById(R.id.ll_uninstall);
                    LinearLayout ll_run = (LinearLayout) view_popup.findViewById(R.id.ll_run);
                    LinearLayout ll_share = (LinearLayout) view_popup.findViewById(R.id.ll_share);
                    LinearLayout ll_detail = (LinearLayout) view_popup.findViewById(R.id.ll_detail);

                    ll_detail.setOnClickListener(AppManagerActivity.this);
                    ll_uninstall.setOnClickListener(AppManagerActivity.this);
                    ll_run.setOnClickListener(AppManagerActivity.this);
                    ll_share.setOnClickListener(AppManagerActivity.this);

                    popupWindowDismiss();

                    // -2表示包裹内容
                    popupWindow = new PopupWindow(view_popup, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);

                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, location[0] + 70, location[1]);
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(500);

                    AlphaAnimation alp = new AlphaAnimation(0, 1);
                    alp.setDuration(500);

                    AnimationSet set = new AnimationSet(true);
                    set.addAnimation(sa);
                    set.addAnimation(alp);

                    view_popup.startAnimation(set);
                }
            }
        });
    }

    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 卸载
            case R.id.ll_uninstall:
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getPackageName()));
                startActivity(uninstall_localIntent);
                popupWindowDismiss();
                break;
            // 运行
            case R.id.ll_run:
                Intent start_localIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getPackageName());
                startActivity(start_localIntent);
                popupWindowDismiss();
                break;
            // 分享
            case R.id.ll_share:
                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppInfo.getAppName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickAppInfo.getPackageName());
                startActivity(Intent.createChooser(share_localIntent, "分享"));
                popupWindowDismiss();
                break;
            // 详情
            case R.id.ll_detail:
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
                startActivity(detail_intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        popupWindowDismiss();

        super.onDestroy();
    }
}
