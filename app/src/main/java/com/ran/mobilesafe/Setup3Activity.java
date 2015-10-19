package com.ran.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 第三个设置向导界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:39
 * 邮箱: vonshine15@163.com
 */
public class Setup3Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_3);
    }

    /**
     * 下一步
     *
     * @param view
     */
    public void next(View view) {
        startActivity(new Intent(this, Setup4Activity.class));
        finish();
    }

    /**
     * 上一步
     *
     * @param view
     */
    public void before(View view) {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
    }
}
