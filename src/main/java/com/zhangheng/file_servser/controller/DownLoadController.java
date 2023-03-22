package com.zhangheng.file_servser.controller;

import com.zhangheng.file.FileUtil;
import com.zhangheng.file_servser.entity.FileInfo;
import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.entity.StatusCode;
import com.zhangheng.file_servser.entity.User;
import com.zhangheng.file_servser.service.KeyService;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.file.FiletypeUtil;
import com.zhangheng.file_servser.utils.FolderFileScanner;
import com.zhangheng.file_servser.utils.TimeUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件下载
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-18 16:48
 */
@RequestMapping("download")
@Controller
public class DownLoadController {


    @Autowired
    private KeyService keyService;
    @Value("${appName}")
    private String appName;
    @Value("#{'${test_keys}'.split(',')}")
    private List<String> test_keys;
    @Value("${baseDir}")
    private String baseDir;
    private Logger log = LoggerFactory.getLogger(getClass());
    private List<String> files = new ArrayList<>();

    @ResponseBody
    @RequestMapping("/accs")
    public String main(){
        return test_keys.toString();
    }
    /**
     * 文件下载请求
     * @param name 文件名
     * @param type 文件类型（父级文件夹名称）
     * @param request
     * @param response
     * @throws IOException
     */

    /**
     * 文件下载请求
     * @param moduleBaseName
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("show/{moduleBaseName}/**")
    public void show(@PathVariable("moduleBaseName") String moduleBaseName,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException, ServletException {
        String err = null;
        FileInputStream input = null;
        File file = null;
        ServletOutputStream outputStream = null;
        try {
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        log.info("moduleBaseName:{}",moduleBaseName);
            String ipMessage;
            ipMessage = CusAccessObjectUtil.getCompleteRequest(request);
//        String ipMessage = IPAnalysisAPI.getIPMessage(request);
            log.info("\n文件下载请求：" + ipMessage);
//        log.info("下载IP："+ipAddress);
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
            file = new File(baseDir + type + "/" + name);
//            FileInputStream input = null;
            outputStream = response.getOutputStream();

            //文件类型
            response.setHeader("Content-Type", FiletypeUtil.getFileContentType(file.getName())+";charset=UTF-8");
            //显示文件大小
            response.setHeader("Content-Length", String.valueOf(file.length()));
            //设置文件下载方式为附件方式，以及设置文件名
//            response.setHeader("Content-Disposition", "attchment;filename=" + file.getName());
            response.setHeader("Content-Disposition", "filename=\"" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name())+"\"");
            input = FileUtils.openInputStream(file);
            IOUtils.copy(input, outputStream);
//            log.info("下载请求成功:"+file.getPath());
        } catch (Exception e) {
            if (e.toString().indexOf("not exist") > 1) {
                err = "对不起o(╥﹏╥)o，没有找到你需要的文件";
            } else {
                err = "错误o(╥﹏╥)o，下载出错误了";
            }
            log.error("错误：" + e.getMessage());
        } finally {
            try {
                if (file.exists()) {
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
                        response.sendError(404, StatusCode.Http404);
                    } else {
                        response.sendError(500, StatusCode.Http500);
                    }
                } catch (Exception e) {
//                    log.error(e.toString());
                }
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
    }




    @ResponseBody
    @RequestMapping("findFileList")
    public List<Message> findFileList(HttpServletRequest request, String type)  {
        List<Message> list =new ArrayList<>();
        list.clear();
        User user = (User) request.getAttribute("user");
        if (user.getKey()!=null&&user.getKey().length()>0){
            if (!user.getType().equals(User.Type.Unknown)){
                if (type!=null&&type.length()>0){
                    log.info("IP[{}],秘钥[{}],查询[{}]文件夹",user.getIp(),user.getKey(),type);
                    files.clear();
                    try {
                        switch (type){
                            case "$all$"://检索全部文件
                                files = FolderFileScanner.scanFilesWithRecursion(baseDir);
                                break;
                                default:
//                                    files.add("对不起，"+type+"中没有找到你需要的，请换一个！");
                                    files = FolderFileScanner.scanFilesWithRecursion(baseDir+type);
                                    break;
                        }
                        if (!files.isEmpty()) {
                            File file=null;
                            for (Object o : files) {
                                String s1 = "";
                                String s = String.valueOf(o);
                                String replace = baseDir.replace("/", "");
                                if (s.indexOf(replace) > 1) {
                                    s1 = s.substring(s.indexOf(replace) + baseDir.length());
                                    s1=s1.replace("\\","/");
                                    file = new File(s);
                                    FileInfo info = new FileInfo();
                                    if (file.exists()){
                                        String name = file.getName();
                                        info.setName(FileUtil.getMainName(name));
                                        info.setType(FiletypeUtil.getFileType(name));
                                        info.setSize(file.length());
                                        info.setUpdate_time(TimeUtil.time(new Date(file.lastModified())));
                                        info.setPath(s1);
                                        //判断是否为管理秘钥
                                        if (user.getType().equals(User.Type.Admin)) {
                                            info.setAuth("admin");
                                        }
                                    }else {
                                        info.setName("***");
                                        info.setType("***");
                                        info.setSize(0);
                                        info.setUpdate_time("***");
                                    }
                                    list.add(new Message(null,200,s1.substring(s1.lastIndexOf("/")+1), s1,info));
                                } else {
                                    s1 = s;
                                    list.add(new Message(null,404,"(＞人＜；)对不起，没有找到你需要的",s1,null));
                                }
                            }
                        }
                    }catch (Exception e){
                        //e.printStackTrace();
                        log.error(e.getMessage());
                        list.add(new Message(null,500,"出错了ε=(´ο｀*)))唉",e.getMessage(),null));
                    }
                }else {
                    list.add(new Message(null,500,"文件夹名称为null","对不起，文件夹名称不能为空",null));
                }
            }else {
                list.add(new Message(null,500,"秘钥错误","对不起，访问秘钥错误",null));
            }
        }else {
            list.add(new Message(null,500,"秘钥为null","对不起，访问秘钥不能为空",null));
        }
        return list;
    }

    /**
     * 获取文件夹列表
     * @return
     */
    @ResponseBody
    @PostMapping("getAllFileType")
    public List<Message> getAllFileType(){
        ArrayList<Message> list = new ArrayList<>();
        list.clear();
        files.clear();
        try {
            list.add(new Message(null,200,"全部","$all$",null));
            File fileList = new File(baseDir);
            File[] files = fileList.listFiles();
            for (File f : files) {
                if (f.isDirectory()){
                    String path = f.getPath().substring(baseDir.length());
                    list.add(new Message(null,500,path,path,null));
                }
            }
        }catch (Exception e){
            list.add(new Message(null,500,"出错了！",e.getMessage(),null));
        }

        return list;
    }
}
