package com.ran.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.ran.mobilesafe.R;
import com.ran.mobilesafe.db.dao.AddressDao;

/**
 * 来电提醒,手机号码归属地查询service
 *
 * 作者: wangxiang on 15/10/27 10:13
 * 邮箱: vonshine15@163.com
 */
public class AddressService extends Service {

    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager windowManager;
    private View view;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); // 监听打电话的状态

        // 开启广播
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
    }

    class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    System.out.println("响铃状态...");
                    String address = AddressDao.getAddress(incomingNumber); // 根据来电号码查询归属地
                    // Toast.makeText(AddressService.this, address, Toast.LENGTH_LONG).show();
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE: // 电话闲置状态
                    if (null != windowManager && null != view){
                        windowManager.removeView(view); // 从window中移除tv(View)
                        view = null;
                    }
                default:break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 监听去电的广播
     * 需要权限:<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>
     * <p/>
     * 作者: wangxiang on 15/10/27 11:30
     * 邮箱: vonshine15@163.com
     */
    class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData(); // 去电号码

            String address = AddressDao.getAddress(number);
//            Toast.makeText(context, address, Toast.LENGTH_LONG).show();
            showToast(address);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE); // 停止来电监听

        unregisterReceiver(receiver); // 注销广播
    }

    /**
     * 自定义归属地浮窗
     */
    private void showToast(String text){
        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

//        view = new TextView(this);
        view = View.inflate(this, R.layout.toast_address, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_number);
        tv.setText(text);
        windowManager.addView(view, params); // 将View添加到屏幕中
    }
}
