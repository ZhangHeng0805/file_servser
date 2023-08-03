package com.zhangheng.file_servser.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.captcha.AbstractCaptcha;
import com.zhangheng.captcha.CircleCaptcha;
import com.zhangheng.captcha.GifCaptcha;
import com.zhangheng.captcha.ShearCaptcha;
import com.zhangheng.captcha.generator.MathGenerator;
import com.zhangheng.captcha.generator.RandomGenerator;
import com.zhangheng.file_servser.entity.CaptchaConfig;
import com.zhangheng.util.FormatUtil;
import com.zhangheng.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
    private AbstractCaptcha captcha;
    @Autowired
    private CaptchaConfig captchaConfig;


    @RequestMapping("checking")
    public Message verifyMathCheck(@RequestParam(value = "code", defaultValue = "") String code, @RequestParam(value = "isClear", defaultValue = "true") Boolean isClear, HttpServletRequest request) {
        Message msg = new Message();
        HttpSession session = request.getSession();
        String vCode = Convert.toStr(session.getAttribute("verify-code"), "");
//        System.out.println(vCode);
//        System.out.println(code);
        if (!StrUtil.isBlank(code)) {
            if (vCode.equalsIgnoreCase(code)) {
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
            msg.setCode(400);
            msg.setTitle("验证码为空");
            msg.setMessage("请重新获取验证码，然后再来验证！");
        }
        return msg;
    }

    @RequestMapping("base64")
    private Message mathBase(HttpServletRequest request) {
        Message msg = new Message();
        try {
            init();
//            captcha = new GifCaptcha(200, 100, 6, 20);
//            captcha.setGenerator(new MathGenerator());
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
            if (captchaConfig.getType().equals(3))
                imageBase64Data = captcha.getImageBase64Data("image/gif");
            else
                imageBase64Data = captcha.getImageBase64Data();
            msg.setObj(imageBase64Data);
        } catch (Exception e) {
            msg.setCode(500);
            msg.setTitle("获取错误");
            msg.setTitle(e.getMessage());
        }
        return msg;
    }

    private void init() {
        switch (captchaConfig.getType()) {
            case 1:
                captcha = new CircleCaptcha(captchaConfig.getWidth(), captchaConfig.getHeight(), captchaConfig.getLength(), captchaConfig.getDifficulty());
                break;
            case 2:
                captcha = new ShearCaptcha(captchaConfig.getWidth(), captchaConfig.getHeight(), captchaConfig.getLength(), captchaConfig.getDifficulty());
                break;
            case 3:
                captcha = new GifCaptcha(captchaConfig.getWidth(), captchaConfig.getHeight(), captchaConfig.getLength(), captchaConfig.getDifficulty());
                break;
            default:
                captcha = new CircleCaptcha(200, 100, 6, 50);
                break;
        }
        if (captchaConfig.getMode().equals(1))
            captcha.setGenerator(new RandomGenerator(captchaConfig.getLength()));
        else
            captcha.setGenerator(new MathGenerator());
    }
}
