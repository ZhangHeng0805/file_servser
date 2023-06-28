package com.zhangheng.file_servser.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.entity.User;
import com.zhangheng.file_servser.service.UpLoadService;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.file_servser.utils.FiletypeUtil;
import com.zhangheng.util.FormatUtil;
import com.zhangheng.util.MathUtil;
import com.zhangheng.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-20 17:07
 */
@Controller
public class WebController {

    @Autowired
    private UpLoadService upLoadService;
    private Logger log = LoggerFactory.getLogger(getClass());
    @Value(value = "#{'${version}'}")
    private String version;
    @Value(value = "#{'${spring.servlet.multipart.max-file-size}'}")
    private String maxFileSize;
    @Value(value = "#{'${zh.file.upload.max-name}'}")
    private Integer maxFileName;
    @Value(value = "#{'${zh.file.upload.max-path}'}")
    private Integer maxFilePath;
    @Value("${baseDir}")
    private String baseDir;

    @RequestMapping("/favicon.ico")
    public String favicon() {
        return "forward:/static/favicon.ico";
    }

    /**
     * 跳转至上传首页
     *
     * @return
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("version", version);
        model.addAttribute("maxFileSize", maxFileSize);
        model.addAttribute("maxFileName", maxFileName);
        model.addAttribute("maxFilePath", maxFilePath);
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
        if (key != null && key.length() > 0) {
            if (user.getType().equals(User.Type.Common) || user.getType().equals(User.Type.Admin)) {
                log.info("页面上传IP:{}；密钥[{}]正确", CusAccessObjectUtil.getIpAddress(request), key);
                if (!file.isEmpty()) {
                    String name = fileName != null && fileName.length() > 0 ? fileName : file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
                    String Path = path != null && path.length() > 0 ? path.split("/")[0] : FiletypeUtil.getFileType(file.getOriginalFilename());
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
    @RequestMapping("deleteFile")
    public Message deleteFile(String path, HttpServletRequest request) {
        Message msg = new Message();
        msg.setTime(com.zhangheng.file_servser.utils.TimeUtil.time(new Date()));
        User user = (User) request.getAttribute("user");
        if (user.getKey() != null && user.getKey().trim().length() > 0) {
            if (user.getType().equals(User.Type.Admin)) {
                if (path != null && path.length() > 0) {
                    File file = new File(baseDir + path);
                    if (file.exists()) {
                        if (!file.isDirectory()) {
                            boolean b = upLoadService.deleteFile(path);
                            if (b) {
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
        log.info("\n" + msg.toString() + "\n");
        return msg;
    }

    CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 6, 150);

    /**
     * 获取数学验证码
     */
    @RequestMapping("/getVerify/math")
    public void getMathVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = null;
        response.setCharacterEncoding("UTF-8");
        try {
            captcha.setGenerator(new MathGenerator());
            captcha.createCode();
            String code = captcha.getCode();
            HttpSession session = request.getSession();
            session.setAttribute("verify-code", MathUtil.operation(code));
            outputStream = response.getOutputStream();
            response.setHeader("Content-Disposition", "filename=[星曦向荣]验证码" + new Date().getTime() + ".png");
            captcha.write(outputStream);
        } catch (Exception e) {
            response.sendError(500, "验证码生成错误:" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    @RequestMapping("/getVerify/math/checking")
    @ResponseBody
    public Message verifyMathCheck(@RequestParam("code") String code, @RequestParam("isClear") Boolean isClear, HttpServletRequest request) {
        Message msg = new Message();
        msg.setTime(TimeUtil.getNowTime());
        HttpSession session = request.getSession();
        Integer vCode = Convert.toInt(session.getAttribute("verify-code"));
//        System.out.println(vCode);
//        System.out.println(code);
        if (vCode != null && !StrUtil.isBlank(code)) {
            if (FormatUtil.isNumber(code)) {
                if (vCode.equals(Convert.toInt(code))) {
                    msg.setCode(200);
                    msg.setTitle("验证码正确");
                    msg.setMessage("恭喜，验证成功！");
                    if (isClear)
                        session.setAttribute("verify-code", null);
                } else {
                    msg.setCode(500);
                    msg.setTitle("验证码错误");
                    msg.setMessage("对不起，验证码输入错误！");
                }
            } else {
                msg.setCode(500);
                msg.setTitle("验证码格式错误");
                msg.setMessage("请输入验证码中的计算结果！");
            }
        } else {
            msg.setCode(404);
            msg.setTitle("验证码为空");
            msg.setMessage("请重新获取验证码，然后再来验证！");
        }
        return msg;
    }

    @ResponseBody
    @RequestMapping("/static/client")
    public void client(@RequestBody String map, HttpServletRequest request) {
        String ip = com.zhangheng.util.CusAccessObjectUtil.getIpAddress(request);
        StringBuilder sb = new StringBuilder();
//        System.out.println(map);
        if (!StrUtil.isBlank(map) && !"null".equals(map)) {
            JSONObject jb = JSONUtil.parseObj(map);
            String cid = jb.getStr("cid");
            String sid = jb.getStr("sid");
            sb.append("时间:" + TimeUtil.toTime(new Date(jb.getLong("r"))))
                    .append("\tip:" + ip)
                    .append("\t系统:" + jb.getStr("os") + jb.getStr("osv"))
                    .append("\t浏览器:" + jb.getStr("bs") + "-V" + jb.getStr("bsv") + "(" + jb.getStr("ul") + ")[" + jb.getStr("br") + "]")
                    .append("\tsid:" + sid )
                    .append("\tcid:" + cid );
            String app = jb.getStr("app");
            if (!StrUtil.isEmptyIfStr(app)) {
                sb.append("\t应用:" + app);
            }
            HttpSession session = request.getSession();
            session.setAttribute("cid", cid);
            sid = Base64.isBase64(sid) ? Base64Decoder.decodeStr(sid) : sid;
            session.setAttribute("sid", sid);
        } else {
            sb.append("暂无信息");
        }
        log.info("\nWeb端信息:{{}}\n", sb.toString());
    }

}
