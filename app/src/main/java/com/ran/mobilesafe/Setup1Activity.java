package com.ran.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 第一个设置向导界面
 *
 * 作者: wangxiang on 15/10/19 09:39
 * 邮箱: vonshine15@163.com
 */
public class Setup1Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_1);
    }

    public void next(View view){
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
    }
}
