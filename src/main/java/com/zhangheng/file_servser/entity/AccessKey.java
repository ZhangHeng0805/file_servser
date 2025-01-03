package com.zhangheng.file_servser.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2025/01/03 星期五 11:03
 * @version: 1.0
 * @description:
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "zhfs.key")
public class AccessKey {
    /**
     * 临时访问秘钥[浏览]
     */
    private HashSet<String> testKeys;
    /**
     * 普通访问秘钥[上传，浏览]
     */
    private HashSet<String> commonKeys;
    /**
     * 管理员访问秘钥[上传，浏览，删除,重命名]全部权限
     */
    private HashSet<String> adminKeys;

}
