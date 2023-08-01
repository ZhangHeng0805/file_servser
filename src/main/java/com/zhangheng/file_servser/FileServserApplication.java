package com.zhangheng.file_servser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@EntityScan
@ConfigurationPropertiesScan
@ServletComponentScan("com.zhangheng.file_servser.config.filter")
public class FileServserApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServserApplication.class, args);
    }

}
