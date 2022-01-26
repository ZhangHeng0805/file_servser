package com.zhangheng.file_servser.controller;

import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.service.UpLoadService;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.file_servser.utils.FiletypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-20 17:07
 */
@Controller
public class WebController {

    @Value("#{'${keys}'.split(',')}")
    private List<String> keys;
    @Autowired
    private UpLoadService upLoadService;
    private Logger log= LoggerFactory.getLogger(getClass());

    /**
     * 跳转至上传首页
     * @return
     */
    @GetMapping("/")
    public String index(){
        return "index";
    }

    /**
     * 文件上传页面表单提交
     * @param file 上传文件
     * @param key 访问秘钥
     * @param fileName 文件名称
     * @param path 文件路径
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/")
    public String upload(MultipartFile file, String key, @Nullable String fileName, @Nullable String path, Model model, HttpServletRequest request){
        Message msg = new Message();
        if (key!=null&&key.length()>0){
            if (keys.indexOf(key)>-1){
                log.info("页面上传IP:{}；密钥[{}]正确", CusAccessObjectUtil.getIpAddress(request),key);
                if (!file.isEmpty()){
                    String name=fileName!=null&&fileName.length()>0?fileName:file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf("."));
                    String Path=path!=null&&path.length()>0?path.split("/")[0]:FiletypeUtil.getFileType(file.getOriginalFilename());
                    String s = upLoadService.saveFile(file, name, Path);
                    if (s!=null){
                        msg.setCode(200);
                        msg.setMessage("文件保存成功！<br><a target='_blank' href='/download/show/"+s+"'>"+s+"</a>");
                    }else {
                        msg.setCode(500);
                        msg.setMessage("文件保存失败！");
                    }
                }else {
                    msg.setCode(500);
                    msg.setMessage("上传文件为空！");
                }
            }else {
                msg.setCode(500);
                msg.setMessage("秘钥错误！");
            }
        }else {
            msg.setCode(500);
            msg.setMessage("秘钥不能为空！");
        }
        model.addAttribute("msg",msg);
        return "index";
    }


}
