package com.ran.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

/**
 * 自动清理进程
 */
public class KillProcessService extends Service {

    private LockScreenReceiver receiver;

    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 锁屏广播
     */
    private class LockScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取到进程管理器
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            //获取到手机上面所以正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                // 不要删除自己
                if (runningAppProcessInfo.processName.equals(getPackageName())) {
                    continue;
                }
                activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new LockScreenReceiver();
        //锁屏的过滤器
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //注册一个锁屏的广播
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //当应用程序推出的时候。需要把广播反注册掉
        unregisterReceiver(receiver);
        //手动回收
        receiver = null;
    }
}
