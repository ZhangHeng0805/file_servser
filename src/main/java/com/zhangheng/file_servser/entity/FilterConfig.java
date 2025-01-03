package com.zhangheng.file_servser.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2025/01/03 星期五 14:32
 * @version: 1.0
 * @description: 请求过滤配置
 */
@Component
@ConfigurationProperties(prefix = "zhfs.config.filter")
@Data
public class FilterConfig {
    /**
     * 请求频率限制,请求的间隔最小时间[ms]
     */
    private long requestMinIntervalMs;

    public long getRequestMinIntervalMs() {
        return requestMinIntervalMs > 0 ? requestMinIntervalMs : 0;
    }

    /**
     * 频率限制排除API路径,逗号分割
     */
    private HashSet<String> rateFilterExcludePath;
    /**
     * 单次会话session最大请求次数
     */
    private int requestMaxCount;
    /**
     * session会话次数限制排除API路径,逗号分割
     */
    private HashSet<String> countFilterExcludePath;
}
