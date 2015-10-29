package com.ran.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ran.mobilesafe.R;

/**
 * 设置中心-选项内容
 * <p/>
 * 作者: wangxiang on 15/10/16 14:52
 * 邮箱: vonshine15@163.com
 */
public class SettingClickView extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvDesc;

    public SettingClickView(Context context) {
        super(context);
        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        // 将自定义的布局文件设置给当前的SettingClickView
        View.inflate(getContext(), R.layout.view_setting_click, this);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 设置描述
     *
     * @param desc
     */
    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }
}
