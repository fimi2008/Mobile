package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ran.mobilesafe.R;

/**
 * 高级工具
 *
 * 作者: wangxiang on 15/10/26 15:32
 * 邮箱: vonshine15@163.com
 */
public class AToolsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    public void numberAdderssQuery(View view){
        startActivity(new Intent(this, AddressActivity.class));
    }
}