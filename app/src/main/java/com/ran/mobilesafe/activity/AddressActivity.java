package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.db.dao.AddressDao;

/**
 * 手机号码归属地查询
 * <p/>
 * 作者: wangxiang on 15/10/26 16:19
 * 邮箱: vonshine15@163.com
 */
public class AddressActivity extends Activity {

    private EditText et_number;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        et_number = (EditText) findViewById(R.id.et_number);
        tv_result = (TextView) findViewById(R.id.tv_result);

        // 监听EditText的变化
        et_number.addTextChangedListener(new TextWatcher() {

            /**
             * 发生变化前的回调
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * 发生变化时的回调
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = s.toString();
                if (!TextUtils.isEmpty(number)) {
                    String result = AddressDao.getAddress(number);
                    tv_result.setText(result);
                }
            }

            /**
             * 发生变化后的回调
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 开始查询
     *
     * @param view
     */
    public void query(View view) {
        String number = et_number.getText().toString().trim();

        if (!TextUtils.isEmpty(number)) {
            String result = AddressDao.getAddress(number);
            tv_result.setText(result);
        } else {
            Animation shake = AnimationUtils.loadAnimation(this, R.animator.shake);

            et_number.startAnimation(shake);
            vibrate();
        }
    }

    /**
     * 手机震动, 需要权限<uses-permission android:name="android.permission.VIBRATE"/>
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // vibrator.vibrate(1000); // 震动1秒
        /**
         * 先等待0秒,再震动0.5秒,再等待0.5秒,再震动0.5秒
         * 参2等于-1表示只执行一次,不循环
         * 参2等于0表示从头循环
         * 参2表示从第几个位置开始循环
         */
        vibrator.vibrate(new long[]{0,500,500,500}, -1);

        // vibrator.cancel();  // 取消震动
    }
}