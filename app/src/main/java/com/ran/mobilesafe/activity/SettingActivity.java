package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.service.AddressService;
import com.ran.mobilesafe.service.CallSafeService;
import com.ran.mobilesafe.utils.ServiceStatusUtils;
import com.ran.mobilesafe.view.SettingClickView;
import com.ran.mobilesafe.view.SettingItemView;

/**
 * 设置中心
 * <p/>
 * 作者: wangxiang on 15/10/16 14:30
 * 邮箱: vonshine15@163.com
 */
public class SettingActivity extends Activity {
    private SettingItemView sivUpdate;  // 自动更新设置
    private SettingItemView sivAddress; // 归属地显示设置
    private SettingClickView scvAddressStyle;   // 修改风格
    private SettingClickView scvAddressLocation; // 修改归属地显示位置
    private SettingItemView siv_black_number;  // 黑名单拦截设置
    private SharedPreferences config;
    private String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    private int style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        config = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);

        initAutoUpdate();

        initAddress();

        initAddressStyle();

        initAddressLocation();
        
        initBlackNumber();
    }

    /**
     * 初始化黑名单拦截设置
     */
    private void initBlackNumber() {
        siv_black_number = (SettingItemView) findViewById(R.id.siv_black_number);

        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, CallSafeService.class.getName());

        siv_black_number.changeChecked(serviceRunning);

        siv_black_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断checkbox的当前勾选状态
                if (siv_black_number.isChecked()) {
                    siv_black_number.changeChecked(false);
                    // 停止黑名单拦截
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                } else {
                    siv_black_number.changeChecked(true);
                    // 开启黑名单拦截
                    startService(new Intent(SettingActivity.this, CallSafeService.class));
                }
            }
        });
    }

    /**
     * 初始化自动更新设置
     */
    private void initAutoUpdate() {
        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);

//        sivUpdate.setTitle("自动更新");
        if (config.getBoolean(ParameterUtils.AUTO_UPDATE, true)) {
//            sivUpdate.setDesc("自动更新已开启");
            sivUpdate.changeChecked(true);
        } else {
//            sivUpdate.setDesc("自动更新已关闭");
            sivUpdate.changeChecked(false);
        }


        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断checkbox的当前勾选状态
                if (sivUpdate.isChecked()) {
                    sivUpdate.changeChecked(false);
//                    sivUpdate.setDesc("自动更新已关闭");
                    //更新sp
                    config.edit().putBoolean(ParameterUtils.AUTO_UPDATE, false).commit();
                } else {
                    sivUpdate.changeChecked(true);
//                    sivUpdate.setDesc("自动更新已开启");
                    //更新sp
                    config.edit().putBoolean(ParameterUtils.AUTO_UPDATE, true).commit();
                }
            }
        });
    }

    /**
     * 初始化归属地开关
     */
    private void initAddress() {
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);

        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, AddressService.class.getName());

        sivAddress.changeChecked(serviceRunning);

        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断checkbox的当前勾选状态
                if (sivAddress.isChecked()) {
                    sivAddress.changeChecked(false);
                    // 停止归属地服务
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                } else {
                    sivAddress.changeChecked(true);
                    // 开启归属地服务
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }

    /**
     * 修改归属地提示框风格
     */
    private void initAddressStyle() {
        scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
        style = config.getInt(ParameterUtils.ADDRESS_STYLE, 0);

        scvAddressStyle.setTitle("归属地提示框风格");
        scvAddressStyle.setDesc(items[style]);

        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDailog();
            }
        });
    }

    /**
     * 弹出选择风格的单选框
     */
    private void showSingleChooseDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("归属地提示框风格");

        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                config.edit().putInt(ParameterUtils.ADDRESS_STYLE, which).commit(); // 保存选择的风格
                dialog.dismiss();   // 让dialog消失
                scvAddressStyle.setDesc(items[which]); // 更新组合控件的描述信息
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 修改归属地显示位置
     */
    private void initAddressLocation(){
        scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");

        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
            }
        });
    }
}