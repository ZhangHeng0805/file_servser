package com.zhangheng.file_servser.config.interceptor;

import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    private Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = request.getParameter("key");
        Message msg = new Message();
        if (key!=null) {
            //遍历普通秘钥
            for (String s : keys) {
                if (s.equals(key)) {
                    log.info("普通密钥["+s+"]访问成功");
                    return true;
                }
            }
            //遍历管理秘钥
            for (String s:admin_keys){
                if (s.equals(key)){
                    log.info("管理密钥["+s+"]访问成功");
                    return true;
                }
            }
        }else {
            msg.setTime(TimeUtil.time(new Date()));
            msg.setCode(500);
            msg.setTitle("验证密钥为空");
            msg.setMessage("错误！请输入访问密钥");
        }
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/error_key").forward(request,response);
        return false;
    }
}
