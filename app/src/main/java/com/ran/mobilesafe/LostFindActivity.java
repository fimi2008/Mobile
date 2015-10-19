package com.ran.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ran.properties.ParameterUtils;

/**
 * 手机防盗界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:29
 * 邮箱: vonshine15@163.com
 */
public class LostFindActivity extends Activity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);
        boolean configed = sharedPreferences.getBoolean(ParameterUtils.CONFIGED, false);
        // 判断是否进入过设置向导
        if (configed) {
            setContentView(R.layout.activity_lost_find);
        } else {
            // 跳转设置向导界面
            startActivity(new Intent(this, Setup1Activity.class));
            finish();
        }
    }

    /**
     * 重新进入设置向导
     *
     * @param view
     */
    public void reset(View view) {
        startActivity(new Intent(this, Setup1Activity.class));
    }
}
