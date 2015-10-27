package com.ran.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ran.mobilesafe.db.dao.AddressDao;

/**
 * 监听去电的广播(已废弃,改用动态注册广播,在AddressService类中)
 * 需要权限:<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
 * <p/>
 * 作者: wangxiang on 15/10/27 11:30
 * 邮箱: vonshine15@163.com
 */
public class OutCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData(); // 去电号码

        String address = AddressDao.getAddress(number);
        Toast.makeText(context, address, Toast.LENGTH_LONG).show();
    }
}
