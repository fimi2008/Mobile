package com.ran.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;

/**
 * 第四个设置向导界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:39
 * 邮箱: vonshine15@163.com
 */
public class Setup4Activity extends BaseSetupActivity {


    private CheckBox cb_checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_4);

        cb_checked = (CheckBox) findViewById(R.id.cb_checked);

        boolean isChecked = mPref.getBoolean(ParameterUtils.PROTECT, false);
        if (isChecked) {
            cb_checked.setText("防盗保护已经开启");
        } else {
            cb_checked.setText("防盗保护没有开启");
        }
        cb_checked.setChecked(isChecked);

        cb_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_checked.setText("防盗保护已经开启");
                } else {
                    cb_checked.setText("防盗保护没有开启");
                }
                // 保存防盗保护是否开启
                mPref.edit().putBoolean(ParameterUtils.PROTECT, isChecked).commit();
            }
        });
    }

    /**
     * 上一步
     */
    @Override
    public void showBefore() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        // 两个界面切换的动画,进入动画和退出动画
        overridePendingTransition(R.animator.tran_previous_in, R.animator.tran_previous_out);
    }

    /**
     * 设置完成
     */
    @Override
    public void showNext() {
        startActivity(new Intent(this, LostFindActivity.class));
        finish();
        // 两个界面切换的动画,进入动画和退出动画
        overridePendingTransition(R.animator.tran_in, R.animator.tran_out);

        // 更新sp,表示已经展示过设置向导,下次进来就不展示啦
        mPref.edit().putBoolean(ParameterUtils.CONFIGED, true).commit();
    }
}
