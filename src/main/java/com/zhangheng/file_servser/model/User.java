package com.zhangheng.file_servser.model;

import lombok.Data;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-02-20 14:28
 * @version: 1.0
 * @description:
 */
@Data
public class User {
    private String ip;
    private String key;
    private Type type;

    public enum Type{
        Test,
        Common,
        Admin,
        Unknown
    }
}
