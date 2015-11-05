package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.utils.MD5Utils;


/**
 * 主页
 * <p/>
 * 作者: wangxiang on 15/10/15 21:37
 * 邮箱: vonshine15@163.com
 */
public class HomeActivity extends Activity {

    private GridView gv_home;
    private SharedPreferences sharedPreferences;
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

        sharedPreferences = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);

        // 设置监听
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 8:// 设置中心
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                    case 7:// 高级工具
                        startActivity(new Intent(HomeActivity.this, AToolsActivity.class));
                        break;
                    case 6: // 缓存清理
                        break;
                    case 5: // 手机杀毒
                        break;
                    case 4: // 流量统计
                        break;
                    case 3:// 进程管理
                        break;
                    case 2:// 软件管理
                        startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
                        break;
                    case 1: // 通讯卫士
                        startActivity(new Intent(HomeActivity.this, CallSafeActivity.class));
                        break;
                    case 0:// 手机防盗
                        showPwdDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显示密码弹窗
     */
    private void showPwdDialog() {
        // 判断是否设置密码
        String pwd = sharedPreferences.getString("pwd", null);
        if (TextUtils.isEmpty(pwd)) {
            // 如果没有设置过,弹出设置密码的弹框
            showPwdSetDialog();
        } else {
            // 如果设置过,弹出输入密码弹窗
            showPwdInputDialog(pwd);
        }

    }

    /**
     * 输入密码弹窗
     */
    private void showPwdInputDialog(final String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_input_pwd, null);
        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        final EditText et_pwd = (EditText) view.findViewById(R.id.et_pwd);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_pwd.getText().toString();
                if (!TextUtils.isEmpty(pwd)) {
                    if (password.equals(MD5Utils.encode(pwd))) {
//                        Toast.makeText(HomeActivity.this, "密码正确!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "对不起,密码错误!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        alertDialog.setView(view, 0, 0, 0, 0);


        alertDialog.show();
    }

    /**
     * 设置密码的弹框
     */
    private void showPwdSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_set_pwd, null);
        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        final EditText et_pwd = (EditText) view.findViewById(R.id.et_pwd);
        final EditText et_pwd_confirm = (EditText) view.findViewById(R.id.et_pwd_confirm);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_pwd.getText().toString();
                String pwd_confirm = et_pwd_confirm.getText().toString();
                if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(pwd_confirm)) {
                    if (pwd.equals(pwd_confirm)) {
                        sharedPreferences.edit().putString("pwd", MD5Utils.encode(pwd)).commit(); // 保存密码
//                        Toast.makeText(HomeActivity.this, "设置成功!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "二次密码不一致!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
//        alertDialog.setView(view); // 将自定义的布局文件设置给dialog
        alertDialog.setView(view, 0, 0, 0, 0); // 设置边距为0,保证在2.x版本上运行样式没问题

        alertDialog.show();
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