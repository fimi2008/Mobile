package com.ran.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;
import com.ran.mobilesafe.service.LocationService;

/**
 * 拦截短信广播
 * <p/>
 * 作者: wangxiang on 15/10/21 10:24
 * 邮箱: vonshine15@163.com
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objs = (Object[]) intent.getExtras().get("pdus");

        // 短信最多140字节,
        // 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
        for (Object obj : objs) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            String address = sms.getOriginatingAddress();// 短信来源号码
            String body = sms.getMessageBody();// 短信内容

            System.out.println(address + ":" + body);

            if ("#*alarm*#".equals(body)){
                // 播放报警音乐, 即使手机调为静音,也能播放音乐, 因为使用的是媒体声音的通道,和铃声无关
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);   // 音量调节,左声道和右声道都调用最大
                player.setLooping(true);    // 单曲循环
                player.start();     // 开始播放

                abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
            }else if ("#*location*#".equals(body)){
                // 获取经纬度坐标
                context.startService(new Intent(context, LocationService.class));
                SharedPreferences sp = context.getSharedPreferences(ParameterUtils.SP_NAME, Context.MODE_PRIVATE);
                String location = sp.getString(ParameterUtils.LOCATION, "get location...");

                System.out.println("location:"+location);

                String phone = sp.getString(ParameterUtils.SAFE_PHONE, "");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, "您的手机位置:"+location, null, null);

                abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
            }else if ("#*wipedata*#".equals(body)){
                System.out.println("远程删除数据");

                abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
            }else if ("#*lockscreen*#".equals(body)){
                System.out.println("远程锁屏");

                abortBroadcast();// 中断短信的传递, 从而系统短信app就收不到内容了
            }
        }
    }
}
