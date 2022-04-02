package com.zhangheng.file_servser.controller;

import com.zhangheng.file_servser.entity.FileInfo;
import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.file_servser.utils.FiletypeUtil;
import com.zhangheng.file_servser.utils.FolderFileScanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    @Value("${appName}")
    private String appName;
    @Value("#{'${keys}'.split(',')}")
    private List<String> keys;
    @Value("${baseDir}")
    private String baseDir;
    private Logger log = LoggerFactory.getLogger(getClass());
    private List<Object> files = new ArrayList<>();
    /**
     * 查看账号信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/accs")
    public String main(){
        return keys.toString();
    }
    /**
     * 文件下载请求
     * @param name 文件名
     * @param type 文件类型（父级文件夹名称）
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("show/{type}/{name:.+}")
    public void show(@PathVariable("name") String name,
                     @PathVariable("type") String type, HttpServletRequest request,
                     HttpServletResponse response) throws IOException, ServletException {
        String user_agent = CusAccessObjectUtil.getUser_Agent(request);
        String ipAddress = CusAccessObjectUtil.getIpAddress(request);
        log.info("下载请求头："+user_agent);
        log.info("下载IP："+ipAddress);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        File file = new File(baseDir + type+"/"+name);
        if (file.exists()) {
            FileInputStream input = null;
            try {
                //显示文件大小
                response.setHeader("Content-Length", String.valueOf(file.length()));
                input = FileUtils.openInputStream(file);
                IOUtils.copy(input, response.getOutputStream());
                log.info("下载请求成功:" + file.getPath());
            } catch (Exception e) {
                if (e.getMessage().indexOf("does not exist") > 1) {

                } else {
                    request.getRequestDispatcher("/err_500").forward(request, response);
                }
                log.error("错误：" + e.getMessage());
            } finally {
                try {
                    if (file.exists()) {
                        input.close();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }else {
            request.setAttribute("msg", new Message());
            request.getRequestDispatcher("/err_404").forward(request, response);
        }
    }

    @ResponseBody
    @RequestMapping("findFileList")
    public List<Message> findFileList(String key, String type)  {
        List<Message> list =new ArrayList<>();
        list.clear();
        if (key!=null&&key.length()>0){
            if (keys.indexOf(key)>-1){

                if (type!=null&&type.length()>0){
                    log.info("秘钥[{}],查询[{}]文件夹",key,type);
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
                                        info.setName(file.getName().split("[.]")[0]);
                                        info.setType(FiletypeUtil.getFileType(file.getName()));
                                        info.setSize(file.length());
                                        info.setPath(s1);
                                    }else {
                                        info.setName("***");
                                        info.setType("***");
                                        info.setSize(0);
                                    }
                                    list.add(new Message(null,200,s1.substring(s1.lastIndexOf("/")+1),s1,info));
                                } else {
                                    s1 = s;
                                    list.add(new Message(null,404,"(＞人＜；)对不起，没有找到你需要的",s1,null));
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
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
