package com.zhangheng.file_servser.service;

import com.zhangheng.file_servser.entity.ServerConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2024-04-14 14:01
 * @version: 1.0
 * @description:
 */
@Service
public class DownloadService {

    @Resource
    private ServerConfig serverConfig;

    public File paresPath(HttpServletRequest request, HttpServletResponse response, String moduleBaseName) throws UnsupportedEncodingException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        log.info("moduleBaseName:{}",moduleBaseName);
        //请求的完整路径（地址）
        final String pathq =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
//        log.info("pathq:{}",pathq);
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
//        log.info("bestMatchingPattern:{}",bestMatchingPattern);
        String arguments = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, pathq);
//        log.info("arguments:{}",arguments);
        String moduleName;
        if (null != arguments && !arguments.isEmpty()) {
            moduleName = moduleBaseName + '/' + arguments;
        } else {
            moduleName = moduleBaseName;
        }
//        log.info(moduleName);
        String type = "";
        String name = "";
        if (moduleName.lastIndexOf("/") > 0) {
            type = moduleName.substring(0, moduleName.lastIndexOf("/"));
            name = moduleName.substring(moduleName.lastIndexOf("/") + 1);
        }
        return new File(serverConfig.getHomeDir() + type + "/" + name);
    }
}
