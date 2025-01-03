package com.zhangheng.file_servser.controller;

import com.zhangheng.bean.Message;
import com.zhangheng.captcha.AbstractCaptcha;
import com.zhangheng.captcha.CircleCaptcha;
import com.zhangheng.captcha.generator.MathGenerator;
import com.zhangheng.file_servser.entity.CaptchaConfig;
import com.zhangheng.file_servser.service.CaptchaService;
import com.zhangheng.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-07-30 23:39
 * @version: 1.0
 * @description: 数学验证码验证
 */
@RestController
@RequestMapping("getVerify")
public class CaptchaController {
    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);
    @Resource
    private CaptchaService captchaService;
    @Resource
    private CaptchaConfig captchaConfig;

    /**
     * 校验验证码
     *
     * @param code
     * @param isClear
     * @param request
     * @return
     */
    @RequestMapping("checking")
    public Message verifyMathCheck(@RequestParam(value = "code", defaultValue = "") String code,
                                   @RequestParam(value = "isClear", defaultValue = "true") Boolean isClear,
                                   HttpServletRequest request) {
        return captchaService.verifyCheck(code, isClear, request);
    }

    /**
     * 生成验证码
     *
     * @param request
     * @return
     */
    @RequestMapping("base64")
    private Message mathBase(HttpServletRequest request) {
        Message msg = new Message();
        try {
            AbstractCaptcha captcha = captchaService.getCaptcha();
            captcha.createCode();
            String code = captcha.getCode();
            HttpSession session = request.getSession();
            msg.setCode(200);
            msg.setTitle("获取成功");
            if (captchaConfig.getMode().equals(1)) {
                msg.setMessage("请仔细观察图中验证码字符，并输入观察到的完整字符");
                session.setAttribute("verify-code", code);
            } else {
                msg.setMessage("请仔细观察图中数学算式，并输入观察到的完整算式计算后的结果");
                session.setAttribute("verify-code", MathUtil.operation(code));
            }
            String imageBase64Data;
            if (captchaConfig.getType().equals(3)) {
                imageBase64Data = captcha.getImageBase64Data("image/gif");
            } else {
                imageBase64Data = captcha.getImageBase64Data();
            }
            msg.setObj(imageBase64Data);
        } catch (Exception e) {
            msg.setCode(500);
            msg.setTitle("获取错误");
            msg.setTitle(e.getMessage());
        }
        return msg;
    }


    /**
     * 获取数学验证码
     */
    @RequestMapping("/getVerify/math")
    public void getMathVerify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = null;
        response.setCharacterEncoding("UTF-8");
        try {
            AbstractCaptcha captcha = new CircleCaptcha(200, 100, 6, 50);
            captcha.setGenerator(new MathGenerator());
            captcha.createCode();
            String code = captcha.getCode();
            HttpSession session = request.getSession();
            session.setAttribute("verify-code", MathUtil.operation(code));
            outputStream = response.getOutputStream();
            response.setHeader("Content-Disposition", "filename=[ZH]Captcha-" + new Date().getTime() + ".gif");
            captcha.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            response.sendError(500, "验证码生成错误:" + e.getMessage());
            log.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

}
