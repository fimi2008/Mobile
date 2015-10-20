package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.utils.ToastUtils;

/**
 * 第三个设置向导界面
 * <p/>
 * 作者: wangxiang on 15/10/19 09:39
 * 邮箱: vonshine15@163.com
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText safePhone;     // 安全号码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_3);

        safePhone = (EditText) findViewById(R.id.et_safephone);

        // 初始化安全手机号码
        String phone = mPref.getString(ParameterUtils.SAFE_PHONE, "");
        safePhone.setText(phone);
    }

    /**
     * 上一步
     */
    @Override
    public void showBefore() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
        // 两个界面切换的动画,进入动画和退出动画
        overridePendingTransition(R.animator.tran_previous_in, R.animator.tran_previous_out);
    }

    /**
     * 下一步
     */
    @Override
    public void showNext() {
        String phone = safePhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "安全号码不能为空!");
            return;
        }
        // SharedPreferences保存安全号码
        mPref.edit().putString(ParameterUtils.SAFE_PHONE, phone).commit();
        startActivity(new Intent(this, Setup4Activity.class));
        finish();
        // 两个界面切换的动画,进入动画和退出动画
        overridePendingTransition(R.animator.tran_in, R.animator.tran_out);
    }

    /**
     * 选择联系人
     *
     * @param view
     */
    public void searchContact(View view) {
        startActivityForResult(new Intent(this, ContactActivity.class), 1);
    }

    /**
     * 给安全号码赋值
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            String phone = data.getStringExtra("phone");
            phone = phone.trim().replaceAll("-", "").replaceAll(" ", ""); // 过滤号码中的"-"和空格
            safePhone.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
