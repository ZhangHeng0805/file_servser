package com.zhangheng.file_servser.entity;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023/01/31 0031 09:39
 * @description: 自定义异常类
 */
public class MyException extends Exception{
    public MyException(String type, String message) {
        super(type+"Exception: "+message);
    }

    public MyException(String message) {
        super(message);
    }
}
