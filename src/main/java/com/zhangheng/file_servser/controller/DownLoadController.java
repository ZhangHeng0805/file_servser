package com.zhangheng.file_servser.controller;

import com.zhangheng.file_servser.entity.Account;
import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
}
