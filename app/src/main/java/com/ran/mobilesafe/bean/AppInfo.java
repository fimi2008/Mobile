package com.ran.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 应用描述
 * <p/>
 * 作者: wangxiang on 15/11/3 13:10
 * 邮箱: vonshine15@163.com
 */
public class AppInfo {
    private Drawable icon;      // app图标
    private String appName;     // app名称
    private long appSize;       // app大小
    private boolean userApp;    // 是否是用户app
    private boolean isRom;      // 是否安装在内存
    private String packageName; // 包名
    private boolean isChecked;  // 是否被选中

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}