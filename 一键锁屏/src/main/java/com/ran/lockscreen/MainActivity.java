package com.ran.lockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager mDPM;
    private ComponentName mComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComp = new ComponentName(this, AdminReceiver.class);
    }

    // 激活设备管理器, 也可以在设置->安全->设备管理器中手动激活
    public void activeAdmin(View view) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComp);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "有个设备管理器,就可以进行设备管理了,真是好方便啊!");
        startActivity(intent);
    }

    // 一键锁屏
    public void lockScreen(View view) {
        // 判断设备管理器是否已经激活
        if (mDPM.isAdminActive(mComp)) {
            mDPM.lockNow();// 立即锁屏
            mDPM.resetPassword("579060", 0);
        } else {
            Toast.makeText(this, "必须先激活设备管理器!", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearData(View view) {
        if (mDPM.isAdminActive(mComp)) {// 判断设备管理器是否已经激活
            mDPM.wipeData(0);// 清除数据,恢复出厂设置
        } else {
            Toast.makeText(this, "必须先激活设备管理器!", Toast.LENGTH_SHORT).show();
        }
    }

    public void unInstall(View view) {
        mDPM.removeActiveAdmin(mComp);// 取消激活

        // 卸载程序
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
