package com.ran.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ran.mobilesafe.properties.ParameterUtils;

/**
 * 监听手机开机启动的广播
 * <p/>
 * 作者: wangxiang on 15/10/19 16:43
 * 邮箱: vonshine15@163.com
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mPref = context.getSharedPreferences(ParameterUtils.SP_NAME, Context.MODE_PRIVATE);
        boolean protect = mPref.getBoolean(ParameterUtils.PROTECT, false);
        // 是否开启手机防盗
        if (protect) {
            String sim = mPref.getString("sim", null); // 获取绑定sim卡序列号
            if (!TextUtils.isEmpty(sim)) {
                // 获取当前手机的sim卡
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String currentSim = tm.getSimSerialNumber();
                if (!sim.equals(currentSim)) {
                    System.out.println("sim卡改变,发送报警短信");
                    String phone = mPref.getString(ParameterUtils.SAFE_PHONE, "");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, "注意:该号码正在使用您的手机!", null, null);
                } else {
                    System.out.println("手机安全");
                }
            }
        }
    }
}
