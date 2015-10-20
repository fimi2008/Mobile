package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;

/**
 * 手机防盗界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:29
 * 邮箱: vonshine15@163.com
 */
public class LostFindActivity extends Activity {

    private SharedPreferences mPref;
    private TextView tv_safePhone;
    private ImageView iv_protect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);

        boolean configed = mPref.getBoolean(ParameterUtils.CONFIGED, false);
        // 判断是否进入过设置向导
        if (configed) {
            setContentView(R.layout.activity_lost_find);
            tv_safePhone = (TextView) findViewById(R.id.tv_safephone);
            iv_protect = (ImageView) findViewById(R.id.iv_protect);

            String phone = mPref.getString(ParameterUtils.SAFE_PHONE, "");
            tv_safePhone.setText(phone);

            boolean isChecked = mPref.getBoolean(ParameterUtils.PROTECT, false);
            if (isChecked){
                iv_protect.setImageResource(R.mipmap.lock);
            }else{
                iv_protect.setImageResource(R.mipmap.unlock);
            }
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
