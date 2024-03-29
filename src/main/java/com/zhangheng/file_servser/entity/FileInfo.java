package com.zhangheng.file_servser.entity;

import lombok.Data;

/**
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-04-02 16:47
 */
@Data
public class FileInfo {
    private String name;//文件名
    private String type;//文件类型
    private String path;//文件路径
    private String update_time;//更新时间
    private double size;//文件大小
    private String auth;
}
