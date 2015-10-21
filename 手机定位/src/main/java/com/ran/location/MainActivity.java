package com.ran.location;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_location;
    private LocationManager locationManager;
    private MyLocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_location = (TextView) findViewById(R.id.tv_location);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

//        List<String> allProviders = locationManager.getAllProviders();
//        System.out.println(allProviders);

        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = locationManager.getBestProvider(criteria, true);

        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String j = "经度:" + location.getLongitude();
            String w = "纬度:" + location.getLatitude();
            String accuracy = "精确度:" + location.getAccuracy();
            String altitude = "海拔:" + location.getAltitude();

            tv_location.setText(j + "\n" + w + "\n" + accuracy + "\n" + altitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("onProviderDisabled");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);// 当activity销毁时,停止更新位置, 节省电量
    }
}
