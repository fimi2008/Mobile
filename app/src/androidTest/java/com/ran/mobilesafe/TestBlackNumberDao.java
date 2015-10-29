package com.ran.mobilesafe;

import android.content.Context;
import android.test.AndroidTestCase;

import com.ran.mobilesafe.bean.BlackNumberInfo;
import com.ran.mobilesafe.db.dao.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * 测试黑名单dao代码
 *
 * 作者: wangxiang on 15/10/29 16:46
 * 邮箱: vonshine15@163.com
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private Context context;

    @Override
    protected void setUp() throws Exception {
        this.context = getContext();
        super.setUp();
    }

    public void testAdd(){
        BlackNumberDao dao = new BlackNumberDao(context);
        Random r = new Random();
        for (int i = 0; i < 200; i++) {
            dao.add("13256752"+i, r.nextInt(3)+1);
        }
    }

    public void testDel(){
        BlackNumberDao dao = new BlackNumberDao(context);
        boolean del = dao.del("132567521");
        assertEquals(true, del);
    }

    public void testQueryAll(){
        BlackNumberDao dao = new BlackNumberDao(context);
        List<BlackNumberInfo> list = dao.queryAll();
        for (BlackNumberInfo i : list){
            System.out.println(i.toString());
        }
    }

    public void testgetModelByNumber(){
        BlackNumberDao dao = new BlackNumberDao(context);
        int mode = dao.getModeByNumber("132567522");
        System.out.println(mode);
    }
}
