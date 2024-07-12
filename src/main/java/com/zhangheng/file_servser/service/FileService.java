package com.zhangheng.file_servser.service;

import com.zhangheng.file_servser.entity.FileInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class FileService {

    public List<FileInfo> getFileList(String relativePath, String base, String auth) {
        List<FileInfo> fileList = new LinkedList<>();
        File file = new File(base + relativePath);
        File[] files = file.listFiles();
        if (files!=null) {
            for (File f : files) {
                fileList.add(new FileInfo(f, base, auth));
            }
        }
        return fileList;
    }
}
