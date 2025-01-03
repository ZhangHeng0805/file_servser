package com.zhangheng.file_servser.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file.FileUtil;
import com.zhangheng.file_servser.entity.AccessKey;
import com.zhangheng.file_servser.entity.ServerConfig;
import com.zhangheng.file_servser.model.FileInfo;
import com.zhangheng.file_servser.model.User;
import com.zhangheng.file_servser.service.FileService;
import com.zhangheng.file_servser.service.KeyService;
import com.zhangheng.file_servser.service.UpLoadService;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.file_servser.utils.FiletypeUtil;
import com.zhangheng.log.printLog.Log;
import com.zhangheng.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-20 17:07
 */
@Controller
@Slf4j
public class WebController {

    @Resource
    private UpLoadService upLoadService;
    @Resource
    private ServerConfig serverConfig;
    @Resource
    private KeyService keyService;
    @Resource
    private AccessKey accessKey;
    @Resource
    private FileService fileService;
//    @Value(value = "#{'${spring.servlet.multipart.max-file-size}'}")
//    private String maxFileSize;

    @RequestMapping("/favicon.ico")
    public String favicon() {
        return "forward:/static/favicon.ico";
    }

    @RequestMapping("/access-key")
    public Object getTestKey(String key) {
        if (StringUtils.hasLength(key)){
            return keyService.getInclude(key);
        }
        return accessKey.getTestKeys();
    }

    /**
     * 跳转至上传首页
     *
     * @return
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("version", serverConfig.getVersion());
        model.addAttribute("maxFileSize", serverConfig.getMaxFileSize());
        model.addAttribute("maxFileName", serverConfig.getMaxFileNameLength());
        model.addAttribute("maxFilePath", serverConfig.getMaxFilePathLength());
        return "index";
    }

    /**
     * 文件上传页面表单提交
     *
     * @param file     上传文件
     * @param key      访问秘钥
     * @param fileName 文件名称
     * @param path     文件路径
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/web/upload")
    public String upload(MultipartFile file, String key, @Nullable String fileName, @Nullable String path, Model model, HttpServletRequest request) {
        Message msg = new Message();
        User user = (User) request.getAttribute("user");
        if (StringUtils.hasLength(key)) {
            if (user.getType().equals(User.Type.Common) || user.getType().equals(User.Type.Admin)) {
                log.info("页面上传IP:{}；密钥[{}]正确", CusAccessObjectUtil.getIpAddress(request), key);
                if (!file.isEmpty()) {
                    String name = StringUtils.hasLength(fileName) ? fileName : Objects.requireNonNull(file.getOriginalFilename()).substring(0, file.getOriginalFilename().lastIndexOf("."));
                    String Path = StringUtils.hasLength(path) ? path.split("/")[0] : FiletypeUtil.getFileType(file.getOriginalFilename());
                    String s = upLoadService.saveFile(file, name, Path);
                    if (s != null) {
                        msg.setCode(200);
                        msg.setMessage("文件保存成功！<br><a target='_blank' href='/download/show/" + s + "'>" + s + "</a>");
                    } else {
                        msg.setCode(500);
                        msg.setMessage("文件保存失败！");
                    }
                } else {
                    msg.setCode(500);
                    msg.setMessage("上传文件为空！");
                }
            } else {
                msg.setCode(500);
                msg.setMessage("秘钥错误！该秘钥没有上传文件的权限");
            }
        } else {
            msg.setCode(500);
            msg.setMessage("秘钥不能为空！");
        }
        model.addAttribute("msg", msg);
        return "index";
    }

    @ResponseBody
    @RequestMapping("/getFileList")
    public Message getFileList(String path, HttpServletRequest request) {
        Message msg = new Message();
        User user = (User) request.getAttribute("user");
        if (user != null && !User.Type.Unknown.equals(user.getType())) {
            List<FileInfo> fileList = fileService.getFileList(path, serverConfig.getHomeDir(), user);
            msg.setCode(200);
            msg.setTime("查询成功");
            msg.setMessage("查询" + fileList.size() + "条记录");
            msg.setObj(fileList);
        } else {
            msg.setCode(500);
            msg.setTitle("秘钥错误");
            msg.setMessage("对不起，访问秘钥错误");
        }
        return msg;
    }

    @ResponseBody
    @RequestMapping("/deleteFile")
    public Message deleteFile(String path, HttpServletRequest request) throws IOException {
        Message msg = new Message();
        User user = (User) request.getAttribute("user");
        if (StringUtils.hasLength(user.getKey())) {
            if (user.getType().equals(User.Type.Admin)) {
                if (path != null && !path.isEmpty()) {
                    File file = new File(serverConfig.getHomeDir() + path);
                    if (file.exists()) {
                        if (!file.isDirectory()) {
                            boolean b = fileService.deleteFile(path);
                            if (b) {
                                Log.Warn("文件删除：" + path + "\n" + com.zhangheng.util.CusAccessObjectUtil.getCompleteRequest(request) + "\n");
                                msg.setCode(200);
                                msg.setTitle("删除成功");
                                msg.setMessage("成功！文件删除成功：" + path);
                            } else {
                                msg.setCode(500);
                                msg.setTitle("删除失败");
                                msg.setMessage("错误！文件删除失败：" + path);
                            }
                        } else {
                            msg.setCode(404);
                            msg.setTitle("路径错误");
                            msg.setMessage("警告！路径：" + path + "不是文件类型");
                        }
                    } else {
                        msg.setCode(404);
                        msg.setTitle("文件不存在");
                        msg.setMessage("警告！删除文件的不存在：" + path);
                    }
                } else {
                    msg.setCode(500);
                    msg.setTitle("路径为空");
                    msg.setMessage("错误！删除路径为空");
                }
            } else {
                msg.setCode(500);
                msg.setTitle("秘钥错误");
                msg.setMessage("秘钥错误！请使用管理秘钥操作");
            }
        } else {
            msg.setCode(500);
            msg.setTitle("秘钥为空");
            msg.setMessage("错误！管理秘钥不能为空");
        }
        log.info("\n{}\n", msg);
        return msg;
    }

    @ResponseBody
    @RequestMapping("renameFile")
    public Message reNameFile(HttpServletRequest request,
                              @RequestParam(defaultValue = "") String path,
                              @RequestParam(defaultValue = "") String newName) {
        User user = (User) request.getAttribute("user");
        Message msg = new Message();
        if (user.getType().equals(User.Type.Admin)) {
            File file = new File(serverConfig.getHomeDir() + path);
            if (file.exists() && file.isFile()) {
                if (!StrUtil.isBlank(newName)) {
                    newName = FileUtil.getPrefix(newName);
                    FileUtil.rename(file, newName.concat(".").concat(FileUtil.getSuffix(file)), true);
                    msg.setCode(200);
                    msg.setTitle("重命名成功!");
                    msg.setMessage(newName);
                    Log.Debug("文件重命名：" + path + " | " + newName + "\n" + com.zhangheng.util.CusAccessObjectUtil.getCompleteRequest(request) + "\n");
                } else {
                    msg.setCode(500);
                    msg.setTitle("重命名失败");
                    msg.setMessage("新文件名不能为空！");
                }
            } else {
                msg.setCode(500);
                msg.setTitle("重命名失败");
                msg.setMessage("文件路径错误！");
            }
        } else {
            msg.setCode(500);
            msg.setTitle("秘钥错误");
            msg.setMessage("秘钥错误！请使用管理秘钥操作");
        }
        log.info("\n{}\n", msg);
        return msg;
    }



    @ResponseBody
    @RequestMapping("/static/client")
    public void client(@RequestBody String map, HttpServletRequest request) {
        String ip = com.zhangheng.util.CusAccessObjectUtil.getClientIp(request);
        StringBuilder sb = new StringBuilder();
//        System.out.println(map);
        if (!StrUtil.isBlank(map) && !"null".equals(map)) {
            JSONObject jb = JSONUtil.parseObj(map);
            String cid = jb.getStr("cid");
            String sid = jb.getStr("sid");
            String bsv = jb.getStr("bsv", "");
            String t = jb.getStr("t", "");
            Long r = jb.getLong("r");
            sb.append("时间:" + TimeUtil.toTime(new Date(r)))
                    .append("\tip:" + ip)
                    .append("\t系统:" + jb.getStr("os") + jb.getStr("osv"))
                    .append("\t浏览器:" + jb.getStr("bs") + "-V" + bsv + "(" + jb.getStr("ul") + ")[" + jb.getStr("br") + "]")
                    .append("\tsid:" + sid)
                    .append("\tcid:" + cid);
            String app = jb.getStr("app");
            if (!StrUtil.isEmptyIfStr(app)) {
                sb.append("\t应用:" + app);
            }
            String token = Base64Encoder.encode(sid + cid);
            token = Base64Encoder.encode(token + r);
            token = Base64Encoder.encode(token + bsv);
            if (token.equals(t)) {
                sid = Base64.isBase64(sid) ? Base64Decoder.decodeStr(sid) : sid;
                HttpSession session = request.getSession();
                session.setAttribute("cid", cid);
                session.setAttribute("sid", sid);
            } else {
                sb.append("\n客户端验证失败！");
            }
        } else {
            sb.append("暂无信息");
        }
        log.info("\nWeb端信息:{{}}\n", sb);
    }

}
