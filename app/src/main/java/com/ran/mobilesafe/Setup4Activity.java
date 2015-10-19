package com.ran.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ran.properties.ParameterUtils;

/**
 * 第四个设置向导界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:39
 * 邮箱: vonshine15@163.com
 */
public class Setup4Activity extends Activity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_4);

        sharedPreferences = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);
    }

    /**
     * 设置完成
     *
     * @param view
     */
    public void next(View view) {
        startActivity(new Intent(this, LostFindActivity.class));
        finish();

        // 更新sp,表示已经展示过设置向导,下次进来就不展示啦
        sharedPreferences.edit().putBoolean(ParameterUtils.CONFIGED, true).commit();
    }

    /**
     * 上一步
     *
     * @param view
     */
    public void before(View view) {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
    }
}
