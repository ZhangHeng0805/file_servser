package com.zhangheng.file_servser.entity;

import com.zhangheng.file_servser.utils.PathUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2025/01/03 星期五 15:42
 * @version: 1.0
 * @description: 服务配置
 */
@Component
@ConfigurationProperties(prefix = "zhfs.config.server")
@Data
public class ServerConfig {
    /**
     * 文件存储根目录
     */
    private String homeDir;

    public void setHomeDir(String homeDir) {
        this.homeDir = PathUtil.cleanPath(homeDir);
        if (!this.homeDir.endsWith("/")) {
            this.homeDir = this.homeDir + "/";
        }
    }

    /**
     * 服务版本
     */
    private String version;
    /**
     * 上传文件件名前缀名
     */
    private String filePrefix;
    /**
     * 最大文件名长度
     */
    private int maxFileNameLength = 35;
    /**
     * 文件保存路径最大长度
     */
    private int maxFilePathLength = 20;
    /**
     * 上传文件最大大小
     */
    @Value(value = "#{'${spring.servlet.multipart.max-file-size}'}")
    private String maxFileSize;

    /**
     * 普通下载是否以附件方式下载
     */
    private boolean attachmentShowDownload = false;


}
