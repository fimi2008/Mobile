package com.ran.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ran.mobilesafe.R;

/**
 * 第一个设置向导界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:39
 * 邮箱: vonshine15@163.com
 */
public class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_1);
    }

    @Override
    public void showBefore() {
    }

    /**
     * 下一步
     */
    @Override
    public void showNext() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();

        // 两个界面切换的动画,进入动画和退出动画
        overridePendingTransition(R.animator.tran_in, R.animator.tran_out);
    }
}
