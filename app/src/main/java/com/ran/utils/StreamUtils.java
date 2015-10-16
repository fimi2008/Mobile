package com.ran.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 对流进行操作工具类
 *
 * 作者: wangxiang on 15/10/15 20:13
 * 邮箱: vonshine15@163.com
 */
public class StreamUtils {

    /**
     * 将输入流读取成String返回
     * @param in
     * @return string
     */
    public static String readFromStream(InputStream in){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        try {
            while ((len = in.read(buffer)) != -1){
                out.write(buffer, 0 ,len);
            }
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != in){
                    in.close();
                }
                if (null != out){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
