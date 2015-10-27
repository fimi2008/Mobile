package com.ran.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 服务状态工具类
 *
 * 作者: wangxiang on 15/10/27 11:05
 * 邮箱: vonshine15@163.com
 */
public class ServiceStatusUtils {

    /**
     * 检查服务是否运行
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName){

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100); // 获取系统所有正在运行的服务,最多返回100个

        for (ActivityManager.RunningServiceInfo info : runningServices){
            String className = info.service.getClassName();
            System.out.println(serviceName);
            if (className.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
