package com.ran.mobilesafe.properties;

/**
 * 该app中使用的统一规定的参数名
 *
 * 作者: wangxiang on 15/10/16 16:05
 * 邮箱: vonshine15@163.com
 */
public interface ParameterUtils {
    final long SPLASH_TIME = 2000; // 闪屏展示最短时间
    final String AUTO_UPDATE = "auto_update";   // 自动更新设置
    final String ADDRESS = "address";           // 归属地显示设置
    final String CONFIGED = "configed";     // 手机防盗是否设置过
    final String SAFE_PHONE = "safePhone";  // 安全号码
    final String PROTECT = "protect";       // 是否设置防盗保护
    final String LOCATION = "location";     // 经纬度

    final String SP_NAME = "config";    //SharedPreferences名

}
