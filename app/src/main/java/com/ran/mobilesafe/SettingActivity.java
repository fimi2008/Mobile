package com.ran.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ran.properties.ParameterUtils;
import com.ran.view.SettingItemView;

/**
 * 设置中心
 * <p/>
 * 作者: wangxiang on 15/10/16 14:30
 * 邮箱: vonshine15@163.com
 */
public class SettingActivity extends Activity {
    private SettingItemView sivUpdate;  // 自动更新设置
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        config = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);

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
}
