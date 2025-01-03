package com.zhangheng.file_servser.controller;

import cn.hutool.core.util.StrUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file.FileUtil;
import com.zhangheng.file_servser.model.User;
import com.zhangheng.file_servser.service.UpLoadService;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.file_servser.utils.FiletypeUtil;
import com.zhangheng.log.printLog.Log;
import com.zhangheng.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 文件上传接口
 *
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-18 16:52
 */
@RequestMapping("upload")
@Controller
@Slf4j
public class UpLoadController {

    @Autowired
    private UpLoadService upLoadService;
    //    @Autowired
//    private WebController webController;
//    @Autowired
//    private KeyService keyService;
//    @Value("#{'${is-add-appName}'}")
//    private Boolean is_add_appName;
//    @Value("#{'${admin_keys}'.split(',')}")
//    private List<String> admin_keys;
//    @Value("${baseDir}")
//    private String baseDir;
//    @Value("${appName}")
//    private String appName;
//    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 保存图片接口（MultipartFile格式）
     *
     * @param image   大小2Mb以内的MultipartFile格式的图片
     * @param imgName 图片名称 (可不填)
     * @param path    保存文件夹 （可不填）
     * @return
     */
//    @ResponseBody
//    @RequestMapping("/saveMulImg")
//    public Message saveMulImagInterface(MultipartFile image
//            , @Nullable String imgName
//            , @Nullable String path, HttpServletRequest request) {
//        Message msg = new Message();
//        msg.setTime(TimeUtil.getNowTime());
//        User user = (User) request.getAttribute("user");
//        if (user.getType().equals(User.Type.Common) || user.getType().equals(User.Type.Admin)) {
//            if (!image.isEmpty()) {
//                //判断图片大小
//                if (image.getSize() < 2080000) {
//                    if (FiletypeUtil.getFileType(image.getOriginalFilename()).equals("image")) {
//                        String name = imgName != null ? imgName : image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf("."));
//                        String Path = path != null ? path.split("/")[0] : FiletypeUtil.getFileType(image.getOriginalFilename());
//                        String s = upLoadService.saveFile(image, name, Path);
//                        if (s != null) {
//                            msg.setCode(200);
//                            msg.setTitle("图片保存成功");
//                            msg.setMessage(s);
//                            log.info(s);
//                        } else {
//                            msg.setCode(500);
//                            msg.setTitle("图片保存失败");
//                            msg.setMessage("上传图片保存失败");
//                        }
//                    } else {
//                        msg.setCode(500);
//                        msg.setTitle("格式错误");
//                        msg.setMessage("上传图片格式错误，建议选择主流的图片格式（png、jpg）");
//                    }
//                } else {
//                    msg.setCode(500);
//                    msg.setTitle("图片错误");
//                    msg.setMessage("上传图片大小超过2Mb限制");
//                }
//            } else {
//                msg.setCode(500);
//                msg.setTitle("图片错误");
//                msg.setMessage("上传图片为空");
//            }
//        } else {
//            msg.setCode(500);
//            msg.setTitle("秘钥key错误");
//            msg.setMessage("该秘钥没有上传文件的权限！");
//        }
//        log.info("\n" + msg.toString() + "\n");
//        return msg;
//    }

    /**
     * 保存图片接口（base64格式）
     *
     * @param image   大小2Mb以内的base64格式的图片
     * @param imgName 图片名称 (可不填)
     * @param path    保存文件夹 （可不填）
     * @return
     */
//    @ResponseBody
//    @RequestMapping("/saveBase64Img")
//    public Message saveBase64ImagInterface(String image
//            , @Nullable String imgName
//            , @Nullable String path, HttpServletRequest request) {
//        Message msg = new Message();
//        User user = (User) request.getAttribute("user");
//        if (user.getType().equals(User.Type.Common) || user.getType().equals(User.Type.Admin)) {
//            if (!image.isEmpty()) {
//                //判断图片大小
//                if (image.length() > 1) {
//                    String name = imgName != null ? imgName : UUID.randomUUID().toString().substring(0, 8);
//                    String Path = path != null ? path : "image";
//                    String s = upLoadService.base64ToImg(image, name, Path);
//                    if (s != null) {
//                        msg.setCode(200);
//                        msg.setTitle("图片保存成功");
//                        msg.setMessage(s);
//                        log.info(s);
//                    } else {
//                        msg.setCode(500);
//                        msg.setTitle("图片保存失败");
//                        msg.setMessage("上传图片保存失败");
//                    }
//                } else {
//                    msg.setCode(500);
//                    msg.setTitle("图片错误");
//                    msg.setMessage("上传图片大小超过2Mb限制");
//                }
//            } else {
//                msg.setCode(500);
//                msg.setTitle("图片错误");
//                msg.setMessage("上传图片数据为空");
//            }
//        } else {
//            msg.setCode(500);
//            msg.setTitle("秘钥key错误");
//            msg.setMessage("该秘钥没有上传文件的权限！");
//        }
//        log.info("\n" + msg.toString() + "\n");
//        return msg;
//    }

    private boolean uplaodCheck_signature(String fileName,long size,HttpServletRequest request){
        String req_key = request.getParameter("key");
        String req_code = request.getParameter("code");
        String req_time = request.getHeader("x-t");
        String req_size = request.getHeader("x-size");
        String req_signature = request.getHeader("x-signature");
        if (!StrUtil.isBlank(fileName)&&!StrUtil.isBlank(req_size)&&!StrUtil.isBlank(req_signature)){
            try {
//                String encoding = "UTF-8";
                String str = req_time + req_key + size + req_code + "zh0805" + fileName.length();
//                System.out.println(str);
                String md5 = EncryptUtil.getMyMd5(str);
                if(md5.equalsIgnoreCase(req_signature))
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 保存文件接口（MultipartFile格式）
     *
     * @param file     MultipartFile格式的文件
     * @param fileName 文件名（可不填）
     * @param path     保存文件夹名 （可不填）
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveMulFile")
    public Message saveFileInterface(MultipartFile file
            , @RequestParam String fileName
            , @RequestParam String path, HttpServletRequest request) {
        Message msg = new Message();
        boolean b = uplaodCheck_signature(fileName, file.getSize(), request);
        if (b) {
            User user = (User) request.getAttribute("user");
            if (user.getType().equals(User.Type.Common) || user.getType().equals(User.Type.Admin)) {
                log.info("\n文件上传：" + CusAccessObjectUtil.getRequst(request) + "\n");
                if (!file.isEmpty()) {
                    String name = StringUtils.hasLength(fileName) ? fileName : Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
                    String Path = StringUtils.hasLength(path) ? path.split("/")[0] : FiletypeUtil.getFileType(file.getOriginalFilename());
                    String s = upLoadService.saveFile(file, name, Path);
                    if (s != null) {
                        Log.Info("文件上传["+ FileUtil.fileSizeStr(file.getSize())+"]："+s+"\n"+ com.zhangheng.util.CusAccessObjectUtil.getCompleteRequest(request)+"\n");
                        msg.setCode(200);
                        msg.setTitle("上传文件保存成功");
                        msg.setMessage(s);
                    } else {
                        msg.setCode(500);
                        msg.setTitle("文件保存失败");
                        msg.setMessage("上传文件保存失败");
                    }
                } else {
                    msg.setCode(500);
                    msg.setTitle("文件错误");
                    msg.setMessage("上传文件为空");
                }
            } else {
                msg.setCode(500);
                msg.setTitle("上传失败，秘钥错误");
                msg.setMessage("该秘钥没有上传文件的权限！");
            }
        }else {
            msg.setCode(500);
            msg.setTitle("上传失败，上传异常");
            msg.setMessage("对不起！上传信息异常！");
        }
        log.info("\n{}\n", msg);
        return msg;
    }


}
