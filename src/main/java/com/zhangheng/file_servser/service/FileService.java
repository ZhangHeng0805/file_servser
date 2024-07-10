package com.zhangheng.file_servser.service;

import com.zhangheng.file_servser.entity.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileService {

    public List<FileInfo> getFileList(String path, String base, String auth) {
        List<FileInfo> fileList = new LinkedList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            fileList.add(new FileInfo(f, base, auth));
        }
        return fileList;
    }
}
