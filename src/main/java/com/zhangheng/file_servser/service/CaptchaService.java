package com.zhangheng.file_servser.service;

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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2025/01/03 星期五 15:18
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class CaptchaService implements InitializingBean {
    @Resource
    private CaptchaConfig captchaConfig;
    @Getter
    private AbstractCaptcha captcha;

    /**
     * 检查校验验证码
     *
     * @param code
     * @param isClear
     * @param request
     * @return
     */
    public Message verifyCheck(String code,
                               Boolean isClear,
                               HttpServletRequest request) {
        Message msg = new Message();
        HttpSession session = request.getSession();
        String vCode = Convert.toStr(session.getAttribute("verify-code"), "");
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

    @Override
    public void afterPropertiesSet() throws Exception {
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
        if (captchaConfig.getMode().equals(1)) {
            captcha.setGenerator(new RandomGenerator(captchaConfig.getLength()));
        } else {
            captcha.setGenerator(new MathGenerator());
        }
        log.info("验证码初始化完成！");
    }
}
