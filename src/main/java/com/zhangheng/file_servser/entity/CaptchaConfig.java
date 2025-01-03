package com.zhangheng.file_servser.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-08-01 11:39
 * @version: 1.0
 * @description: 验证码配置
 */
@Component
@ConfigurationProperties(prefix = "zhfs.config.captcha")
@Data
public class CaptchaConfig {
    /**
     * 验证码干扰类型：1-线圈，2-扭曲，3-GIF
     */
    private Integer type = 1;
    /**
     * 验证码模式：1-随机字符，2-数学运算
     */
    private Integer mode = 1;
    /**
     * 验证码长度
     */
    private int length = 4;
    /**
     * 验证码干扰难度
     */
    private int difficulty = 50;
    /**
     * 验证码宽度px
     */
    private int width = 200;
    /**
     * 验证码高度px
     */
    private int height = 100;
}
