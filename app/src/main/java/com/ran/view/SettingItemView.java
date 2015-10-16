package com.ran.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ran.mobilesafe.R;

/**
 * 设置中心-选项内容
 * <p/>
 * 作者: wangxiang on 15/10/16 14:52
 * 邮箱: vonshine15@163.com
 */
public class SettingItemView extends RelativeLayout {

    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbStatus;
    private String title;
    private String desc_on;
    private String desc_off;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        title = attrs.getAttributeValue(NAMESPACE, "mtitle");
        desc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
        desc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");

        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        // 将自定义的布局文件设置给当前的SettingItemView
        View.inflate(getContext(), R.layout.view_setting_item, this);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);

        setTitle(title);
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

    /**
     * 返回checkbox勾选状态
     *
     * @return boolean
     */
    public boolean isChecked() {
        return cbStatus.isChecked();
    }

    /**
     * 更改checkbox状态
     *
     * @param checked
     */
    public void changeChecked(boolean checked) {
        cbStatus.setChecked(checked);
        // 根据选择的状态,更新描述
        if (checked) {
            setDesc(desc_on);
        } else {
            setDesc(desc_off);
        }
    }
}
