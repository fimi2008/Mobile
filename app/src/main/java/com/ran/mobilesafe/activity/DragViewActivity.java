package com.ran.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ran.mobilesafe.R;
import com.ran.mobilesafe.properties.ParameterUtils;

/**
 * 修改归属地显示位置
 * <p/>
 * 作者: wangxiang on 15/10/27 21:54
 * 邮箱: vonshine15@163.com
 */
public class DragViewActivity extends Activity {
    private TextView tvTop;
    private TextView tvBottom;
    private ImageView ivDrag;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private SharedPreferences mPrep;
    private long[] mHits = new long[2]; // 数组长度表示要点击的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        mPrep = getSharedPreferences(ParameterUtils.SP_NAME, MODE_PRIVATE);
        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);
        ivDrag = (ImageView) findViewById(R.id.iv_drag);

        int lastX = mPrep.getInt(ParameterUtils.LASTX, 0);
        int lastY = mPrep.getInt(ParameterUtils.LASTY, 0);

        // 获取布局对象,重新设置位置
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        layoutParams.leftMargin = lastX;    // 设置左边距
        layoutParams.topMargin = lastY;     // 设置top边距
        //ivDrag.setLayoutParams(layoutParams);

        final int winWidth = getWindowManager().getDefaultDisplay().getWidth();  // 屏幕宽度
        final int winHeight = getWindowManager().getDefaultDisplay().getHeight();    // 屏幕高度

        initTextView(lastY, winHeight);

        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0 , mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis()-500)){
//                    ToastUtils.show(DragViewActivity.this, "双击啦");
                    ivDrag.layout((winWidth-ivDrag.getWidth())/2, ivDrag.getTop(), (winWidth+ivDrag.getWidth())/2, ivDrag.getBottom());
                }
            }
        });

        // 设置触摸监听
        ivDrag.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        endX = (int) event.getRawX();
                        endY = (int) event.getRawY();

                        // 计算移动偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        // 更新左上右下距离
                        int l = ivDrag.getLeft() + dx;
                        int t = ivDrag.getTop() + dy;

                        int r = ivDrag.getRight() + dx;
                        int b = ivDrag.getBottom() + dy;

                        // 判断是否超出屏幕边界,注意状态栏的高度
                        if (l < 0 || r > winWidth || t < 0 || b > winHeight - 40) {
                            break;
                        }

                        initTextView(t, winHeight);

                        // 更新界面
                        ivDrag.layout(l, t, r, b);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 记录坐标点
                        SharedPreferences.Editor edit = mPrep.edit();
                        edit.putInt(ParameterUtils.LASTX, ivDrag.getLeft());
                        edit.putInt(ParameterUtils.LASTY, ivDrag.getTop());
                        edit.commit();
                        break;
                    default:
                        break;
                }

                return false; // 事件要往下传递
            }
        });
    }

    /**
     * 根据图片位置,决定提示框显示/隐藏
     *
     * @param lastY
     * @param winHeight
     */
    private void initTextView(int lastY, int winHeight) {
        if (lastY > winHeight / 2) {// 提示信息上边显示,下边隐藏
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }
    }
}