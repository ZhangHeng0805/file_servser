package com.zhangheng.file_servser.entity;


import com.zhangheng.file_servser.utils.TimeUtil;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

@Data
public class Message {

    private String time;//时间戳
    private int code;//状态码(200:成功; 100:提示; 404:警告; 500:错误）
    private String title;//标题
    private String message;//内容
    private Object obj;//对象
    private boolean success;

    public Message() {
    }

    public Message(String time, int code, String title, String message, Object obj) {
        this.time = time;
        this.code = code;
        this.title = title;
        this.message = message;
        this.obj = obj;
        this.success = code == 200;
    }



    public void setCode(int code) {
        this.code = code;
        this.success = code == 200;
    }



    //获取指定范围的随机数
    public static int RandomNum(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    //保留两位小数
    public static double twoDecimalPlaces(double num) {
        DecimalFormat df = new DecimalFormat("######0.00");
        String format = df.format(num);
        Double aDouble = Double.valueOf(format);
        return aDouble;
    }

    //控制台打印日志
    public static void printLog(String msg) {
        String time = TimeUtil.time(new Date());
        System.out.println(time + " " + msg);
    }
}
