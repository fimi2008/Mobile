package com.ran.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.ran.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

/**
 *
 */
public class CallSafeService extends Service {
    private InnerReceiver receiver;
    private BlackNumberDao dao;
    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        // 初始化短信广播
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);

        // 监听来电
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyListener listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); // 监听打电话的状态
    }

    class MyListener extends PhoneStateListener {
        // 电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                /**
                 * @see TelephonyManager#CALL_STATE_IDLE  电话闲置
                 * @see TelephonyManager#CALL_STATE_RINGING 电话铃响的状态
                 * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通
                 */
                case TelephonyManager.CALL_STATE_RINGING:
                    System.out.println("响铃状态...");
                    // 过滤+86开头的号码
                    if (incomingNumber.contains("+86")) {
                        incomingNumber = incomingNumber.replace("+86", "");
                    }
                    int mode = dao.getModeByNumber(incomingNumber); // 根据来电号码查询归属地

                    /**
                     * 黑名单拦截模式
                     * 1 短信拦截
                     * 2 电话拦截
                     * 3 全部拦截 电话拦截 + 短信拦截
                     *
                     */
                    if (mode == 2 || mode == 3){
                        System.out.println("挂断黑名单电话号码");

                        endCall();

                        Uri uri = Uri.parse("content://call_log/calls");

                        getContentResolver().registerContentObserver(uri, true, new MyContentObserver(new Handler(), incomingNumber));
                    }
                    break;
                default:
                    break;
            }
        }

        private class MyContentObserver extends ContentObserver {
            String incomingNumber;
            /**
             * Creates a content observer.
             *
             * @param handler The handler to run {@link #onChange} on, or null if none.
             * @param incomingNumber
             */
            public MyContentObserver(Handler handler, String incomingNumber) {
                super(handler);
                this.incomingNumber = incomingNumber;
            }

            //当数据改变的时候调用的方法
            @Override
            public void onChange(boolean selfChange) {

                getContentResolver().unregisterContentObserver(this);

                deleteCallLog(incomingNumber);

                super.onChange(selfChange);
            }
        }
        //删掉电话号码
        private void deleteCallLog(String incomingNumber) {

            Uri uri = Uri.parse("content://call_log/calls");

            getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});

        }

        /**
         * 利用反射原理.挂断电话
         */
        private void endCall() {
            try {
                //通过类加载器加载ServiceManager
                Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
                //通过反射得到当前的方法
                Method method = clazz.getDeclaredMethod("getService", String.class);
                IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

                ITelephony iTelephony = ITelephony.Stub.asInterface(ibinder);
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("短信来了");
            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {// 短信最多140字节,
                // 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();// 短信来源号码
                String messageBody = message.getMessageBody();// 短信内容
                // 过滤+86开头的号码
                if (originatingAddress.contains("+86")){
                    originatingAddress = originatingAddress.replace("+86", "");
                }
                //通过短信的电话号码查询拦截的模式
                int mode = dao.getModeByNumber(originatingAddress);

                /**
                 * 黑名单拦截模式
                 * 1 短信拦截
                 * 2 电话拦截
                 * 3 全部拦截 电话拦截 + 短信拦截
                 *
                 */
                if (mode == 1 || mode == 3){
                    abortBroadcast();
                }

                //智能拦截模式 发票  你的头发漂亮 分词(拦截垃圾短信)
                if(messageBody.contains("发票")){
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
