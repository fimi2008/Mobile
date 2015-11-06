package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.utils.SmsUtils;
import com.ran.mobilesafe.utils.ToastUtils;

/**
 * 高级工具
 * <p/>
 * 作者: wangxiang on 15/10/26 15:32
 * 邮箱: vonshine15@163.com
 */
public class AToolsActivity extends Activity {

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    public void numberAdderssQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    public void backupSms(View view) {
        pd = new ProgressDialog(this);
        pd.setTitle("提示");
        pd.setMessage("正在备份中,请稍等...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();

        new Thread() {
            @Override
            public void run() {
                boolean result = SmsUtils.backupSms(AToolsActivity.this, new SmsUtils.BackupSmsCall() {
                    @Override
                    public void backupBefore(int count) {
                        pd.setMax(count);
                    }

                    @Override
                    public void onBackup(int process) {
                        pd.setProgress(process);
                    }
                });

                if (result){
                    ToastUtils.show(AToolsActivity.this, "备份成功!");
                }else{
                    ToastUtils.show(AToolsActivity.this, "备份失败!");
                }

                pd.dismiss();
            }
        }.start();
    }
}