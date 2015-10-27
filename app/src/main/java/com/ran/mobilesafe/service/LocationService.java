package com.ran.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.ran.mobilesafe.properties.ParameterUtils;

/**
 * 获取经纬度坐标的service
 * <p/>
 * 作者: wangxiang on 15/10/21 10:09
 * 邮箱: vonshine15@163.com
 */
public class LocationService extends Service {

    private LocationManager lm;
    private SharedPreferences mPref;
    private MyLocationListenter mlistenter;

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        mPref = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);// 是否允许付费,比如使用3g网络定位
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);// 获取最佳位置提供者

        mlistenter = new MyLocationListenter();
        lm.requestLocationUpdates(bestProvider, 0, 0, mlistenter);
    }

    class MyLocationListenter implements LocationListener {

        // 位置发生变化
        @Override
        public void onLocationChanged(Location location) {
            System.out.println("get location...");

            // 将获取的经纬度保存在sp中
            mPref.edit().putString(ParameterUtils.LOCATION, "x:" + location.getLongitude() + ";y:" + location.getLatitude()).commit();

            stopSelf();// 停掉service
        }

        // 位置提供者状态发生变化
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        // 用户打开gps
        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        // 用户关闭gps
        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(mlistenter);// 当activity销毁时,停止更新位置, 节省电量
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
