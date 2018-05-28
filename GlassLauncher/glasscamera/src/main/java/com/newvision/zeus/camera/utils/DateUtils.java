package com.newvision.zeus.camera.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


/**
 * Created by Ct on 2015/12/7.
 * 日期时间工具类
 */
public class DateUtils {


    /**
     * 计算两个日期大小
     *
     * @param time1 2015-01-01 12:00
     * @param time2 2015-01-01 12:00
     * @return true:time1>time2
     * @throws ParseException
     */
    public static boolean computeDateBigOrSmall(String time1, String time2) {
        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date1 = formatter.parse(time1);
            date2 = formatter.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ((date1 != null ? date1.getTime() : 0) - (date2 != null ? date2.getTime() : 0)) >= 0;
    }

    public static boolean computeDateNoTimeBigOrSmall(String time1, String time2) {
        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date1 = formatter.parse(time1);
            date2 = formatter.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ((date1 != null ? date1.getTime() : 0) - (date2 != null ? date2.getTime() : 0)) > 0;
    }

    public static boolean computeDateNoTimeBigOrSmall(String time1, String time2, String dateFormat) {
        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            date1 = formatter.parse(time1);
            date2 = formatter.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1 == null || date2 == null)
            return false;
        return (date1.getTime() - date2.getTime()) > 0;
    }

    // 时间计算方法。
    public static String timeLogic(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();
        Date date = StrToDate(dateStr);
        calendar.setTime(date);
        long past = calendar.getTimeInMillis();
        // 相差的秒数
        long time = (now - past) / 1000;
        StringBuffer sb = new StringBuffer();
        if (time >= 0 && time < 60) { // 1小时内
            return sb.append(time).append("秒前").toString();
        } else if (time > 60 && time < 3600) {
            return sb.append(time / 60).append("分钟前").toString();
        } else if (time >= 3600 && time < 3600 * 24) {
            return sb.append(time / 3600).append("小时前").toString();
        } else if (time >= 3600 * 24 && time < 3600 * 48) {
            return sb.append("昨天").toString();
        } else if (time >= 3600 * 48) {
            return sb.append(time / (3600 * 24)).append("天前").toString();
        }
        return dateStr;
    }

    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType) {
        formatType = "HH:mm";
        String strTime = null;
        Date date = null; // long类型转成Date类型
        try {
            date = longToDate(currentTime, formatType);
            strTime = dateToString(date, formatType); // date类型转成String

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strTime;
    }

    //utc time to string
    public static String getFileDate(String currentTime) {

        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        long lcc = Long.valueOf(currentTime);
        int i = Integer.parseInt(currentTime);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    //utc time to string
    public static String getFileTime(String currentTime) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lcc = Long.valueOf(currentTime);
        int i = Integer.parseInt(currentTime);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getcurrentTime() {

        SimpleDateFormat fomate = new SimpleDateFormat("MMddhhmm");
        Date date = new Date(System.currentTimeMillis());
        return fomate.format(date);
    }


    public static String getYear() {

        SimpleDateFormat fomate = new SimpleDateFormat("yyyy");
        Date date = new Date(System.currentTimeMillis());
        return fomate.format(date);
    }

    public static String getMounth() {

        SimpleDateFormat fomate = new SimpleDateFormat("MM");
        Date date = new Date(System.currentTimeMillis());
        return fomate.format(date);
    }

    public static String getDay() {

        SimpleDateFormat fomate = new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis());
        return fomate.format(date);
    }

    public static String getHour() {

        SimpleDateFormat fomate = new SimpleDateFormat("HH");
        Date date = new Date(System.currentTimeMillis());
        return fomate.format(date);
    }

    public static String getMinute() {

        SimpleDateFormat fomate = new SimpleDateFormat("mm");
        Date date = new Date(System.currentTimeMillis());
        return fomate.format(date);
    }

    public static String getSecond() {

        SimpleDateFormat fomate = new SimpleDateFormat("ss");
        Date date = new Date(System.currentTimeMillis());
        return fomate.format(date);
    }

    /**
     * 获取当前时间按小时和分钟
     *
     * @return
     */
    public static String getTime() {

        return new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
    }

    /**
     * 获取当前时间按小时和分钟
     *
     * @return
     */
    public static String getVideoTime() {

        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }
}
