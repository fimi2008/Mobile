package com.ran.rocket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * 背景,用于显示火箭的烟雾
 *
 * 作者: wangxiang on 15/10/29 11:50
 * 邮箱: vonshine15@163.com
 */
public class BackgroundActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg);

        ImageView ivTop = (ImageView) findViewById(R.id.iv_top);
        ImageView ivBottom = (ImageView) findViewById(R.id.iv_bottom);

        // 渐变动画
        AlphaAnimation anim = new AlphaAnimation(0,1);
        anim.setDuration(1000);
        anim.setFillAfter(true); // 动画结束后保持状态

        // 运行动画
        ivTop.setAnimation(anim);
        ivBottom.setAnimation(anim);

        // 延迟1秒后结束activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);
    }
}
