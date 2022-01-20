package com.zhangheng.file_servser.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * 账号
 */

@Configuration
@ConfigurationProperties(prefix = "accs")
@Data
public class Account {
    private String account;//账号
    private String password;//密码
    private int state;//状态
}


