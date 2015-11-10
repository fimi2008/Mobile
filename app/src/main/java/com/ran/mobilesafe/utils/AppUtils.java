package com.ran.mobilesafe.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Debug;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import com.ran.mobilesafe.bean.AppInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用工具类
 * <p/>
 * 作者: wangxiang on 15/11/3 13:17
 * 邮箱: vonshine15@163.com
 * <!--         获取mac地址权限 -->
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <!--     获取手机信息权限 -->
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 */
public class AppUtils {

    /**
     * 获取当前手机所安装的应用
     *
     * @param context
     * @return List<AppInfo>
     */
    public static List<AppInfo> queryAppInfos(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        if (null != installedPackages && installedPackages.size() > 0) {
            List<AppInfo> list = new ArrayList<AppInfo>(installedPackages.size());
            AppInfo info;
            for (PackageInfo pinfo : installedPackages) {
                info = new AppInfo();
                info.setIcon(pinfo.applicationInfo.loadIcon(pm)); // 应用图标
                info.setAppName(pinfo.applicationInfo.loadLabel(pm).toString()); // 应用名称
                info.setPackageName(pinfo.packageName); // 应用包名
                info.setAppSize(new File(pinfo.applicationInfo.sourceDir).length()); // 应用大小
                int flags = pinfo.applicationInfo.flags;    // 获取到安装应用程序的标记
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    info.setUserApp(false); // 系统应用
                } else {
                    info.setUserApp(true);  // 用户应用
                }
                if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                    info.setIsRom(false); // 应用安装在sd卡上
                } else {
                    info.setIsRom(true);  // 应用安装在内存
                }

                list.add(info);
            }
            return list;
        }

        return null;
    }

    /**
     * 查询正在正在运行的应用
     *
     * @param context
     * @param flag    是否过滤系统的应用和电话应用
     * @return List<AppInfo>
     */
    public static List<AppInfo> queryRunningProcess(Context context, boolean flag) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的应用
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        //获取包管理器，在这里主要通过包名获取程序的图标和程序名
        PackageManager pm = context.getPackageManager();
        List<AppInfo> list = new ArrayList<AppInfo>(run.size());
        PackageInfo packageInfo;
        AppInfo pr;
        for (ActivityManager.RunningAppProcessInfo ra : run) {
            if (flag) {
                //这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
                if (ra.processName.equals("system") || ra.processName.equals("com.android.phone")) {
                    continue;
                }
            }

            try {
                packageInfo = pm.getPackageInfo(ra.processName, 0);
                pr = new AppInfo();
                pr.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                pr.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString()); // 应用名称
                pr.setPackageName(packageInfo.packageName); // 应用包名
                /**
                 * 获取内存基本信息
                 */
                Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{ra.pid}); // 一共只有一个数据
                int totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty();// 获取到总共占用了多少内存
                pr.setAppSize(totalPrivateDirty * 1024); // 占用多少内存
                int flags = packageInfo.applicationInfo.flags;    // 获取到安装应用程序的标记
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    pr.setUserApp(false); // 系统应用
                } else {
                    pr.setUserApp(true);  // 用户应用
                }
                if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                    pr.setIsRom(false); // 应用安装在sd卡上
                } else {
                    pr.setIsRom(true);  // 应用安装在内存
                }
                list.add(pr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获取android当前可用内存大小
     *
     * @param context
     * @return String 格式化后数据
     */
    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存

        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 获取android当前可用内存大小
     *
     * @param context
     * @return long
     */
    public static long getAvailMemoryL(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return mi.availMem;
    }

    /**
     * 获得系统总内存
     *
     * @param context
     * @return String 格式化后数据
     */
    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获得手机屏幕宽高
     *
     * @param context
     * @return int[]{width, height}
     */
    public static int[] getHeightAndWidth(Activity context) {
        int width = context.getWindowManager().getDefaultDisplay().getWidth();
        int heigth = context.getWindowManager().getDefaultDisplay().getHeight();
        int[] result = new int[]{width, heigth};
        return result;
    }

    /**
     * 获取IMEI号，IESI号，手机型号
     *
     * @param context
     * @return String[]{手机IMEI号,手机IESI号,手机型号,手机品牌,手机号码}
     */
    public static String[] getInfo(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号
        String mtyb = android.os.Build.BRAND;// 手机品牌
        String number = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        String[] result = new String[]{imei, imsi, mtype, mtyb, number};
        return result;
    }

    /**
     * 获取手机MAC地址,只有手机开启wifi才能获取到mac地址
     *
     * @param context
     * @return string
     */
    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String result = wifiInfo.getMacAddress();
        return result;
    }

    /**
     * 手机CPU信息
     *
     * @return string[]{cpu型号, cpu频率}
     */
    public static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};  //1-cpu型号  //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }
}