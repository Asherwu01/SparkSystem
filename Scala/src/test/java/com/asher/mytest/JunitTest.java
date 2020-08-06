package com.asher.mytest;

import org.junit.Test;

import java.lang.reflect.Field;

public class JunitTest {
    @Test
    public void test(){
        System.out.println("dadf");
    }

    @Test
    public void test2() throws NoSuchFieldException, IllegalAccessException {
        String s = "abcde";
        Field value = s.getClass().getDeclaredField("value");
        value.setAccessible(true);
        char[] o = (char[])value.get(s);

        o[1]='Z';
        System.out.println(s);
    }
}
