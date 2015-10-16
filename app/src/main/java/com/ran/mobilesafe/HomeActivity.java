package com.ran.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 主页
 * <p/>
 * 作者: wangxiang on 15/10/15 21:37
 * 邮箱: vonshine15@163.com
 */
public class HomeActivity extends Activity {
    private GridView gv_home;
    private Item[] items = new Item[]{new Item("手机防盗", R.mipmap.home_safe),
            new Item("通讯卫士", R.mipmap.home_callmsgsafe),
            new Item("软件管理", R.mipmap.home_apps),
            new Item("进程管理", R.mipmap.home_taskmanager),
            new Item("流量统计", R.mipmap.home_netmanager),
            new Item("手机杀毒", R.mipmap.home_trojan),
            new Item("缓存清理", R.mipmap.home_sysoptimize),
            new Item("高级工具", R.mipmap.home_tools),
            new Item("设置中心", R.mipmap.home_settings)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gv_home = (GridView) findViewById(R.id.gv_home);

        gv_home.setAdapter(new HomeAdapter());

        // 设置监听
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 8:// 设置中心
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                    case 7:
                        break;
                    case 6:
                        break;
                    case 5:
                        break;
                    case 4:
                        break;
                    case 3:
                        break;
                    case 2:
                        break;
                    case 1:
                        break;
                    case 0:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = View.inflate(HomeActivity.this, R.layout.home_list_item, null);

                holder = new ViewHolder();
                holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_item);
                holder.tv_item = (TextView) convertView.findViewById(R.id.tv_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_item.setText(items[position].name);
            holder.iv_item.setImageResource(items[position].imageId);

            return convertView;
        }

        /**
         * 减少 findViewById 消耗的内部类
         */
        private class ViewHolder {
            ImageView iv_item;
            TextView tv_item;
        }
    }

    /**
     * 主页选项,所需元素类
     */
    class Item {
        private String name;        // 选项名
        private int imageId;        // 选项图标

        public Item(String name, int imageId) {
            this.name = name;
            this.imageId = imageId;
        }
    }
}
