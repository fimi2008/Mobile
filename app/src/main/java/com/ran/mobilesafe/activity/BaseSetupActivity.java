package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.ran.mobilesafe.properties.ParameterUtils;

/**
 * 设置向导基类,不需要注册到清单文件中
 *
 * 作者: wangxiang on 15/10/19 16:02
 * 邮箱: vonshine15@163.com
 */
public abstract  class BaseSetupActivity extends Activity{
    private GestureDetector mGesture;
    public SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);
        mGesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            /**
             * 监听手势滑动事件
             * @param e1 表示滑动的起点
             * @param e2 表示滑动的终点
             * @param velocityX 表示水平速度
             * @param velocityY 表示垂直速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 向右滑动,展示上一步
                if (e2.getRawX() - e1.getRawX() > 200) {
                    showBefore();
                    return true;
                }
                // 向左滑动,展示下一步
                if (e1.getRawX() - e2.getRawX() > 200) {
                    showNext();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 上一步逻辑由子类实现
     */
    public abstract void showBefore();

    /**
     * 下一步逻辑由子类实现
     */
    public abstract void showNext();

    /**
     * 上一步
     *
     * @param view
     */
    public void before(View view){
        showBefore();
    }

    /**
     * 下一步
     *
     * @param view
     */
    public void next(View view){
        showNext();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 委托手势识别器处理触摸事件
        mGesture.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
