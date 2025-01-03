package com.zhangheng.file_servser.controller;

import com.zhangheng.bean.Message;
import com.zhangheng.download.server.service.SplitDownloadService;
import com.zhangheng.file.FileUtil;
import com.zhangheng.file.FiletypeUtil;
import com.zhangheng.file_servser.entity.ServerConfig;
import com.zhangheng.file_servser.model.FileInfo;
import com.zhangheng.file_servser.model.StatusCode;
import com.zhangheng.file_servser.model.User;
import com.zhangheng.file_servser.service.DownloadService;
import com.zhangheng.file_servser.utils.FolderFileScanner;
import com.zhangheng.file_servser.utils.PathUtil;
import com.zhangheng.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文件下载
 *
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-18 16:48
 */
@RequestMapping("download")
@RestController
@Slf4j
public class DownLoadController {
    @Resource
    private ServerConfig serverConfig;
    @Resource
    private DownloadService downloadService;

    //    @Value("${baseDir}")
//    private String baseDir;
//    @Value("${is-download-show-attchment}")
//    private Boolean is_show_attchment;
    @Value("${config.FileType.isAll:true}")
    private Boolean is_show_fileType_all;
    private List<String> files = new ArrayList<>();


    /**
     * 文件普通下载请求
     *
     * @param moduleBaseName
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("show/{moduleBaseName}/**")
    public void show(@PathVariable("moduleBaseName") String moduleBaseName,
                     HttpServletRequest request,
                     HttpServletResponse response) throws Exception {
        String err = null;
        FileInputStream input = null;
        File file = null;
        ServletOutputStream outputStream = null;
        try {
            file = downloadService.paresPath(request, response, moduleBaseName);
            outputStream = response.getOutputStream();
            //最后修改时间
            long lastModified = file.lastModified();
            //判断文件是否被修改
            String fileName = file.getName();
            String encode = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("ETag", encode);
            //文件修改时间
            response.setDateHeader("Last-Modified", lastModified);
            //过期时间
            response.setDateHeader("Expires", System.currentTimeMillis() + 604800000L);
            //文件类型
            response.setHeader("Content-Type", FiletypeUtil.getFileContentType(fileName) + ";charset=UTF-8");
            //显示文件大小
            response.setHeader("Content-Length", String.valueOf(file.length()));
            //设置文件下载方式为附件方式，以及设置文件名
//            response.setHeader("Content-Disposition", "attchment;filename=" + file.getName());
            String disposition = "filename=" + encode;
            disposition = serverConfig.isAttachmentShowDownload() ? "attchment;" + disposition : disposition;
            response.setHeader("Content-Disposition", disposition);
            input = FileUtils.openInputStream(file);
            IOUtils.copy(input, outputStream);
//            log.info("下载请求成功:"+file.getPath());
        } catch (Exception e) {
            if (e.toString().indexOf("not exist") > 1) {
                err = "对不起o(╥﹏╥)o，没有找到你需要的文件";
            } else {
                err = "错误o(╥﹏╥)o，下载出错误了";
            }
            log.error("下载show错误2：{}", e.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                err = "错误o(╥﹏╥)o，下载出错误了";
//                response.sendError(404, e.getMessage());
            }
            if (err != null) {
                try {
                    if (err.startsWith("对不起")) {
                        response.sendError(404, StatusCode.HTTP_404.getMessage());
                    } else {
                        response.sendError(500, StatusCode.HTTP_500.getMessage());
                    }
                } catch (Exception e) {
                    log.error("下载show错误3：{}", e.toString());
                }
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("下载show错误1：{}", e.toString());
            }
        }
    }

    /**
     * 文件分片下载请求
     *
     * @param moduleBaseName
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "split/{moduleBaseName}/**")
    public void downloadFile(@PathVariable("moduleBaseName") String moduleBaseName,
                             HttpServletResponse response,
                             HttpServletRequest request) throws IOException {
        File file = downloadService.paresPath(request, response, moduleBaseName);
        try {
            SplitDownloadService.download(file, request, response);
        } catch (IOException e) {
            log.error("分片下载错误：" + e.getMessage());
            if (!response.isCommitted())
                response.sendError(500, e.getMessage());
        }
    }


    @RequestMapping("findFileList")
    public List<Message> findFileList(HttpServletRequest request, @RequestParam String type) {
        type = PathUtil.cleanPath(type);
        List<Message> list = new ArrayList<>();
        User user = (User) request.getAttribute("user");
        if (StringUtils.hasLength(user.getKey())) {
            if (!user.getType().equals(User.Type.Unknown)) {
                if (type != null && !type.isEmpty()) {
                    log.info("\n文件夹检索：IP[{}],秘钥[{}],查询[{}]文件夹\n", user.getIp(), user.getKey(), type);
                    files.clear();
                    try {
                        if (type.equals("$all$")) {//检索全部文件
                            files = FolderFileScanner.scanFilesWithNoRecursion(serverConfig.getHomeDir());
                        } else {
                            files = FolderFileScanner.scanFilesWithNoRecursion(PathUtil.join(serverConfig.getHomeDir(), type));
                        }
                        if (!files.isEmpty()) {
                            File file = null;
                            for (Object o : files) {
                                String s1 = "";
                                String s = String.valueOf(o);
                                String replace = serverConfig.getHomeDir().replace("/", "");
                                if (s.indexOf(replace) > 1) {
                                    s1 = s.substring(s.indexOf(replace) + serverConfig.getHomeDir().length());
                                    s1 = s1.replace("\\", "/");
                                    file = new File(s);
                                    FileInfo info = new FileInfo();
                                    if (file.exists()) {
                                        String name = file.getName();
                                        info.setName(FileUtil.getMainName(name));
                                        info.setType(FiletypeUtil.getFileType(name));
                                        info.setSize(file.length());
                                        info.setUpdate_time(TimeUtil.toTime(new Date(file.lastModified())));
                                        info.setPath(s1);
                                        info.setAuth(user.getType().name());
                                    } else {
                                        info.setName("***");
                                        info.setType("***");
                                        info.setSize(0L);
                                        info.setUpdate_time("***");
                                    }
                                    list.add(new Message(null, 200, s1.substring(s1.lastIndexOf("/") + 1), s1, true, info));
                                } else {
                                    s1 = s;
                                    list.add(new Message(null, 404, "(＞人＜；)对不起，没有找到你需要的", s1, false, null));
                                }
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        log.error("\n文件检索错误：{}\n", e.getMessage());
                        list.add(new Message(null, 500, "出错了ε=(´ο｀*)))唉", e.getMessage(), false, null));
                    }
                } else {
                    list.add(new Message(null, 500, "文件夹名称为null", "对不起，文件夹名称不能为空", false, null));
                }
            } else {
                list.add(new Message(null, 500, "秘钥错误", "对不起，访问秘钥错误", false, null));
            }
        } else {
            list.add(new Message(null, 500, "秘钥为null", "对不起，访问秘钥不能为空", false, null));
        }
        return list;
    }

    /**
     * 获取文件夹列表
     *
     * @return
     */
    @PostMapping("getAllFileType")
    public List<Message> getAllFileType() {
        ArrayList<Message> list = new ArrayList<>();
        try {
            if (is_show_fileType_all)
                list.add(new Message(null, 200, "全部", "$all$", true, null));
            File fileList = new File(serverConfig.getHomeDir());
            File[] files = fileList.listFiles();
            for (File f : Objects.requireNonNull(files)) {
                if (f.isDirectory()) {
                    String path = f.getPath().substring(serverConfig.getHomeDir().length());
                    list.add(new Message(null, 200, path, path, true, null));
                }
            }
        } catch (Exception e) {
            list.add(new Message(null, 500, "文件类型获取错误！", e.getMessage(), true, null));
        }
        return list;
    }
}
