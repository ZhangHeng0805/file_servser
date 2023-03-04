package com.zhangheng.file_servser.config.interceptor;

import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.entity.User;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.file_servser.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

/**
 * 验证码拦截器
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-19 10:52
 */

@Configuration
public class VerifyInterceptor implements HandlerInterceptor {

    @Value("#{'${keys}'.split(',')}")
    private List<String> keys;
    @Value("#{'${admin_keys}'.split(',')}")
    private List<String> admin_keys;
    @Value("#{'${test_keys}'.split(',')}")
    private List<String> test_keys;
    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = request.getParameter("key");
        Message msg = new Message();
        if (key!=null&&key.length()>0) {
            User user = new User();
            user.setIp(CusAccessObjectUtil.getIpAddress(request));
            user.setKey(key);
            if (test_keys.indexOf(key)>-1){
                log.info("IP[{}],临时密钥["+key+"]访问成功",user.getIp());
                user.setType(User.Type.Test);
            }else if (keys.indexOf(key)>-1){
                log.info("IP[{}],普通密钥["+key+"]访问成功",user.getIp());
                user.setType(User.Type.Common);
            }else if (admin_keys.indexOf(key)>-1){
                log.info("IP[{}],管理密钥["+key+"]访问成功",user.getIp());
                user.setType(User.Type.Admin);
            }else {
                log.info("IP[{}],未知密钥["+key+"]访问拦截",user.getIp());
                user.setType(User.Type.Unknown);
//                msg.setTime(TimeUtil.time(new Date()));
//                msg.setCode(500);
//                msg.setTitle("验证密钥错误");
//                msg.setMessage("访问秘钥key错误!");
//                request.setAttribute("msg",msg);
//                request.getRequestDispatcher("/error/error_key").forward(request,response);
//                return false;
            }
            request.setAttribute("user",user);
            return true;
        }else {
            msg.setTime(TimeUtil.time(new Date()));
            msg.setCode(500);
            msg.setTitle("验证密钥为空");
            msg.setMessage("错误！请输入访问密钥key");
        }
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/error/error_key").forward(request,response);
        return false;
    }


}
