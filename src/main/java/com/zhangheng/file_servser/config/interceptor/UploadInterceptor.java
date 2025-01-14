package com.zhangheng.file_servser.config.interceptor;

import com.zhangheng.file_servser.controller.CaptchaController;
import com.zhangheng.bean.Message;
import com.zhangheng.file_servser.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-03-04 11:34
 * @version: 1.0
 * @description:
 */
@Service
@Slf4j
public class UploadInterceptor implements HandlerInterceptor {
//    private Logger log= LoggerFactory.getLogger(getClass());
    @Autowired
    private CaptchaController captchaController;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getAttribute("user");
        Message msg = new Message();
        if (user!=null){
            if (user.getType().equals(User.Type.Common)||user.getType().equals(User.Type.Admin)){
                msg=captchaController.verifyMathCheck(request.getParameter("code"),true,request);
                if (msg.getCode()==200){
                    log.info("\n文件上传拦截：验证成功，放行！\n");
                    return true;
                }
            }else {
                msg.setCode(500);
                msg.setTitle("秘钥权限不足");
                msg.setMessage("对不起！该秘钥没有上传文件的权限");
            }
        }else {
            msg.setCode(500);
            msg.setTitle("没有验证信息");
            msg.setMessage("错误！请输入访问密钥key");
        }
        log.info("\n文件上传拦截：验证失败，拦截！"+ msg +"\n");
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/error/error_key").forward(request,response);
        return false;
    }
}
