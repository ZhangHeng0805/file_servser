package com.zhangheng.file_servser.service;

import com.zhangheng.file_servser.entity.ServerConfig;
import com.zhangheng.file_servser.model.FileInfo;
import com.zhangheng.file_servser.model.User;
import com.zhangheng.file_servser.utils.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService {

    @Resource
    private ServerConfig serverConfig;
    @Resource
    private KeyService keyService;

    public List<FileInfo> getFileList(String relativePath, String base, User user) {
        File folder = new File(base + relativePath);
        List<String> include = keyService.getInclude(user.getKey());
        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .map(file -> new FileInfo(file, base, user.getType().name()))
                .filter(fileInfo -> {
                    if (include.isEmpty()) {
                        return true;
                    } else {
                        for (String s : include) {
                            if (fileInfo.getPath().startsWith(s)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    public boolean isDirectoryEmpty(Path directoryPath) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
            return !directoryStream.iterator().hasNext();
        } catch (IOException e) {
            log.error("检查目录是否为空时出错: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除服务目录下的文件，并附带删除空文件夹
     *
     * @param path
     * @return
     * @throws IOException
     */
    public boolean deleteFile(String path) throws IOException {
        Path filePath = Paths.get(PathUtil.join(serverConfig.getHomeDir(), path));
        boolean deleted = Files.deleteIfExists(filePath);
        if (deleted) {
            log.info("\n文件删除成功：{}\n", path);
            Path parent = filePath.getParent();
            while (true) {
                if (isDirectoryEmpty(parent)) {
                    Files.deleteIfExists(parent);
                    log.info("\n空文件夹删除成功：{}\n", parent);
                    parent = parent.getParent();
                } else {
                    break;
                }
            }
        }
        return deleted;
    }

    /**
     * 删除存在的文件/文件夹
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public boolean delIfExists(Path filePath) throws IOException {
        if (Files.exists(filePath)) {
            if (Files.isDirectory(filePath)) {
                return FileSystemUtils.deleteRecursively(filePath);
            } else {
                return Files.deleteIfExists(filePath);
            }
        } else {
            return false;
        }
    }
}
