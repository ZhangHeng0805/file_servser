package com.zhangheng.file_servser.entity;

import com.zhangheng.file.FileUtil;
import com.zhangheng.file.FiletypeUtil;
import com.zhangheng.file_servser.service.FileService;
import com.zhangheng.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-04-02 16:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {
    private String name;//文件名
    private String type;//文件类型
    private String path;//文件路径
    private String update_time;//更新时间
    private Long size;//文件大小
    private String auth;
    private Boolean isFile;
    private Boolean isDirectory;


    public FileInfo(File file, String basePath, String auth) {
        this.name = file.getName();
        this.isFile = file.isFile();
        if (this.isFile) {
            this.type = FiletypeUtil.getFileType(file);
        }
        if (basePath != null) {
            Path base = Paths.get(basePath);
            this.path = FileUtil.normalize(base.relativize(file.toPath()).toString());
        } else {
            this.path = FileUtil.normalize(file.getPath());
        }
        this.update_time = TimeUtil.toTime(new Date(file.lastModified()));
        this.size = file.length();
        this.auth = auth;
        this.isDirectory = file.isDirectory();
    }
}
