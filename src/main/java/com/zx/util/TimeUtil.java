package com.zx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间工具
 */
public class TimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtil.class);

    //进行比较的日期格式
    private static final String TIME_FORMAT = "HH:mm:ss";

    //白天的开始 --0
    public static final String AM_START = "7:00:00";
    //白天的结束时间
    public static final String AM_END = "23:00:00";

//    //下午的开始时间 --1
//    public static final String PM_START = "12:00:00";
//    //下午的结束时间
//    public static final String PM_END = "18:00:00";
//
//    //晚上的开始时间 --2
//    public static final String NIGHT_START = "18:00:00";
//    //晚上的结束时间
//    public static final String NIGHT_END = "00:00:00";

    //深夜的开始时间 --2
    public static final String NIGHT_START = "01:00:00";
    //深夜的结束时间
    public static final String NIGHT_END = "07:00:00";




    /**
     * 判断当前时间到底在那个时间段,并返回对应的线程数
     */
    public static Integer getThreadNumber(){
        //每个时间段对应的线程数
        Integer[] MAPPER = new Integer[]{ConfigUtil.THREAD_NUMBER + 20, ConfigUtil.THREAD_NUMBER / 10 + 10};
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
        final String currentTime = format.format(new Date());

        if(isBetween(currentTime,AM_START,AM_END)){
            return MAPPER[0];
        }
        else{
            return MAPPER[1];
        }

    }

    /**
     * 判断当前时间是否在指定string的时间的范围内
     */
    public static boolean isBetween(String currentTime, String startTime,String endTime){
        try {
            SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
            //三个时间都转成date
            Date startDate = format.parse(startTime);
            Date endDate = format.parse(endTime);
            Date currentDate = format.parse(currentTime);
            //如果在范围之间，返回true
            if(currentDate.after(startDate) && currentDate.before(endDate))
                return true;
        } catch (ParseException e) {
            LOGGER.warn("日期格式转换错误！error:{}",e.getMessage());
        }
        return false;
    }

}
