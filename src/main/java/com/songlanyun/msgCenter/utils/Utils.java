package com.songlanyun.msgCenter.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static <T, K, V> T map2Bean(Map<K, V> mp, Class<T> beanCls)
            throws Exception, IllegalArgumentException, InvocationTargetException {
        T t = null;
        try {

            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            t = beanCls.newInstance();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (mp.containsKey(key)) {
                    Object value = mp.get(key);
                    Method setter = property.getWriteMethod();// Java中提供了用来访问某个属性的
                    // getter/setter方法
                    setter.invoke(t, value);
                }
            }

        } catch (IntrospectionException e) {

            e.printStackTrace();
        }
        return t;
    }

    /**
     * 倒计时
     *
     * @param endTime   倒计时间(毫秒)
     * @param timerTask 实现
     */
    public static Timer countDown(
            int endTime,
            Consumer<Timer> timerTask) {
        // 开始时间
        long start = System.currentTimeMillis();
        // 结束时间
        long end = start + endTime;

        Timer timer = new Timer();
        // 计时结束时候，停止全部timer计时计划任务
        timer.schedule(new TimerTask() {
            public void run() {
                timerTask.accept(timer);
                timer.cancel();
            }
        }, new Date(end));
        return timer;
    }

    public static int countStr(String srcStr, String findStr) {
        int count = 0;
        Pattern pattern = Pattern.compile(findStr);// 通过静态方法compile(String regex)方法来创建,将给定的正则表达式编译并赋予给Pattern类
        Matcher matcher = pattern.matcher(srcStr);//
        while (matcher.find()) {// boolean find() 对字符串进行匹配,匹配到的字符串可以在任何位置
            count++;
        }
        return count;
    }

}
