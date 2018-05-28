package com.newvision.zeus.glassmanager.utils;

/**
 * Created by yanjiatian on 2017/9/8.
 * 对正则表达式的验证
 */

public class RegularUtils {
    //验证6-13数字字母
    public static final String PWD_REG = "[a-z0-9A-Z]{6,13}";
    public static final String PHONE_CODE = "[0-9]{6}";
    public static final String MAIL_CODE = "[a-z0-9A-Z]{5}";
    //验证１开头的11位手机号码
    public static final String PHONE_REG = "1\\d{10}";
    //验证邮箱
    public static final String MAIL_REG = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
}
