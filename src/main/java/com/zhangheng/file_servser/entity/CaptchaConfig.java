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
     * 验证码干扰类型
     */
    private Interference type = Interference.COIL;
    /**
     * 验证码模式
     */
    private Mode mode = Mode.RANDOM;
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

    public enum Mode{
        /**
         * 随机字符
         */
        RANDOM,
        /**
         * 数学运算
         */
        MATH;
    }
    public enum Interference{
        /**
         * 线圈干扰
         */
        COIL,
        /**
         * 横线干扰
         */
        LINE,
        /**
         * 动图线圈干扰
         */
        GIF_COIL,
    }
}
