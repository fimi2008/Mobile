package com.ran.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.utils.ToastUtils;
import com.ran.mobilesafe.view.SettingItemView;

/**
 * 第二个设置向导界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:39
 * 邮箱: vonshine15@163.com
 */
public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView siv_sim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_2);

        siv_sim = (SettingItemView) findViewById(R.id.siv_sim);

        String sim = mPref.getString("sim", null);
        // 初始化是否绑定sim卡
        if (TextUtils.isEmpty(sim)) {
            siv_sim.changeChecked(false);
        } else {
            siv_sim.changeChecked(true);
        }

        siv_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_sim.isChecked()) {
                    siv_sim.changeChecked(false);
                    mPref.edit().remove("sim").commit(); // 删除已绑定的SIM卡序列号
                } else {
                    siv_sim.changeChecked(true);
                    // 保存sim卡信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber(); // 获取sim卡序列号
                    mPref.edit().putString("sim", simSerialNumber).commit();
                }
            }
        });
    }

    /**
     * 展示上一步
     */
    @Override
    public void showBefore() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();

        // 两个界面切换的动画,进入动画和退出动画
        overridePendingTransition(R.animator.tran_previous_in, R.animator.tran_previous_out);
    }

    /**
     * 展示下一步
     */
    @Override
    public void showNext() {
        if (!siv_sim.isChecked()) {
            ToastUtils.show(this, "必须绑定SIM卡!");
            return;
        }

        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        // 两个界面切换的动画,进入动画和退出动画
        overridePendingTransition(R.animator.tran_in, R.animator.tran_out);
    }
}