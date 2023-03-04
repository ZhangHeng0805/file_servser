package com.zhangheng.file_servser.config.interceptor;

import com.zhangheng.file_servser.controller.WebController;
import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.entity.User;
import com.zhangheng.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UploadInterceptor implements HandlerInterceptor {
    private Logger log= LoggerFactory.getLogger(getClass());
    @Autowired
    private WebController webController;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getAttribute("user");
        Message msg = new Message();
        msg.setTime(TimeUtil.getNowTime());
        if (user!=null){
            if (user.getType().equals(User.Type.Common)||user.getType().equals(User.Type.Admin)){
                msg=webController.verifyMathCheck((String)request.getParameter("code"),true,request);
                if (msg.getCode()==200){
                    log.info("文件上传拦截：验证成功，放行！");
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
        log.info("文件上传拦截：验证失败，拦截！"+msg.toString());
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/error/error_key").forward(request,response);
        return false;
    }
}
