package com.yangshanlin.yangsl.zhihufab;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author yangshanlin
 * @date 2018/6/11
 */
@RunWith(AndroidJUnit4.class)
public class LogTest {

    @Test
    public void e() {
        Log.e("test", "log test");
    }

}
