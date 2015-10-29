package com.ran.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.db.dao.AddressDao;
import com.ran.mobilesafe.properties.ParameterUtils;

/**
 * 来电提醒,手机号码归属地查询service
 * <p/>
 * 作者: wangxiang on 15/10/27 10:13
 * 邮箱: vonshine15@163.com
 */
public class AddressService extends Service {

    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager windowManager;
    private View view;
    private SharedPreferences mPrep;

    private int startX;
    private int startY;
    private int winWidth;
    private int winHeight;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPrep = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);

        // 监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); // 监听打电话的状态

        // 开启广播
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    System.out.println("响铃状态...");
                    String address = AddressDao.getAddress(incomingNumber); // 根据来电号码查询归属地
                    // Toast.makeText(AddressService.this, address, Toast.LENGTH_LONG).show();
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE: // 电话闲置状态
                    if (null != windowManager && null != view) {
                        windowManager.removeView(view); // 从window中移除tv(View)
                        view = null;
                    }
                default:
                    break;
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
    private void showToast(String text) {
        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        winWidth = windowManager.getDefaultDisplay().getWidth();
        winHeight = windowManager.getDefaultDisplay().getHeight();

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        // 需要权限:<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.gravity = Gravity.LEFT + Gravity.TOP; // 将重心位置设置为左上方,也就是(0,0)从左上方开始,而不是默认的中心位置
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        int lastX = mPrep.getInt(ParameterUtils.LASTX, 0);
        int lastY = mPrep.getInt(ParameterUtils.LASTY, 0);

        // 设置浮窗的位置,基于左上方的偏移量
        params.x = lastX;
        params.y = lastY;

//        view = new TextView(this);
        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green};
        int style = mPrep.getInt(ParameterUtils.ADDRESS_STYLE, 0);

        view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

        TextView tv = (TextView) view.findViewById(R.id.tv_number);
        tv.setText(text);
        windowManager.addView(view, params); // 将View添加到屏幕中

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        // 计算移动偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        // 更新浮窗位置
                        params.x += dx;
                        params.y += dy;

                        // 防止坐标偏移屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > winWidth - view.getWidth()) {
                            params.x = winWidth - view.getWidth();
                        }
                        if (params.y > winHeight - view.getHeight()){
                            params.y = winHeight - view.getHeight();
                        }

                        windowManager.updateViewLayout(view, params);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 记录坐标点
                        SharedPreferences.Editor edit = mPrep.edit();
                        edit.putInt(ParameterUtils.LASTX, params.x);
                        edit.putInt(ParameterUtils.LASTY, params.y);
                        edit.commit();
                        break;
                }
                return false;
            }
        });
    }
}
