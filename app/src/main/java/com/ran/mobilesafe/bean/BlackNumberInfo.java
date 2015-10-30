package com.ran.mobilesafe.bean;

/**
 * 黑名单对象
 *
 * 作者: wangxiang on 15/10/29 16:20
 * 邮箱: vonshine15@163.com
 */
public class  BlackNumberInfo {
    private String number; // 电话号码
    private int Mode;      // 拦截模式,1-短信拦截,2-电话拦截,3-全部拦截

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMode() {
        return Mode;
    }

    public void setMode(int mode) {
        Mode = mode;
    }
}
