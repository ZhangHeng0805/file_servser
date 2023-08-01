package com.zhangheng.file_servser.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-08-01 11:39
 * @version: 1.0
 * @description:
 */
@Configuration
@ConfigurationProperties(prefix = "config.captcha")
@Validated
@Data
public class CaptchaConfig {
    /**
     * 验证码类型：1-线圈，2-扭曲，3-GIF
     */
    private Integer type;
    /**
     * 验证码模式：1-随机，2-数学
     */
    private Integer mode;
    /**
     * 验证码长度
     */
    private Integer length;
    /**
     * 验证码难度
     */
    private Integer difficulty;
    /**
     * 验证码宽度
     */
    private Integer width;
    /**
     * 验证码高度
     */
    private Integer height;
}
