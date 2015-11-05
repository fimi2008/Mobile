package com.ran.mobilesafe.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ran.mobilesafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用工具类
 *
 * 作者: wangxiang on 15/11/3 13:17
 * 邮箱: vonshine15@163.com
 */
public class AppUtils {

    /**
     * 获取当前手机所安装的应用
     *
     * @param context
     * @return List<AppInfo>
     */
    public static List<AppInfo> queryAppInfos(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        if (null != installedPackages && installedPackages.size() > 0){
            List<AppInfo> list = new ArrayList<AppInfo>(installedPackages.size());
            AppInfo info;
            for (PackageInfo pinfo : installedPackages){
                info = new AppInfo();
                info.setIcon(pinfo.applicationInfo.loadIcon(pm)); // 应用图标
                info.setAppName(pinfo.applicationInfo.loadLabel(pm).toString()); // 应用名称
                info.setPackageName(pinfo.packageName); // 应用包名
                info.setAppSize(new File(pinfo.applicationInfo.sourceDir).length()); // 应用大小
                int flags = pinfo.applicationInfo.flags;    // 获取到安装应用程序的标记
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                    info.setUserApp(false); // 系统应用
                }else{
                    info.setUserApp(true);  // 用户应用
                }
                if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                    info.setIsRom(false); // 应用安装在sd卡上
                }else{
                    info.setIsRom(true);  // 应用安装在内存
                }

                list.add(info);
            }
            return list;
        }

        return null;
    }
}
