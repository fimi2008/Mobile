package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.service.AddressService;
import com.ran.mobilesafe.utils.ServiceStatusUtils;
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
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        config = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);

        initAutoUpdate();

        initAddress();
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
    private void initAddress(){
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
}
