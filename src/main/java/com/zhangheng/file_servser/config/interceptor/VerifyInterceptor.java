package com.zhangheng.file_servser.config.interceptor;

import cn.hutool.core.convert.Convert;
import com.zhangheng.bean.Message;
import com.zhangheng.file_servser.entity.User;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.util.EncryptUtil;
import com.zhangheng.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private HashSet<String> keys;
    @Value("#{'${admin_keys}'.split(',')}")
    private HashSet<String> admin_keys;
    @Value("#{'${test_keys}'.split(',')}")
    private HashSet<String> test_keys;
    private final Logger log= LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = request.getParameter("key");

        Message msg = new Message();
        if (key!=null&& !key.isEmpty()) {
            User user = new User();
            user.setIp(CusAccessObjectUtil.getIpAddress(request));
            user.setKey(key);
            if (check_Key(test_keys,request,key)){
                log.info("\nIP[{}],临时密钥访问成功\n",user.getIp());
                user.setType(User.Type.Test);
            }else if (check_Key(keys,request,key)){
                log.info("\nIP[{}],普通密钥访问成功\n",user.getIp());
                user.setType(User.Type.Common);
            }else if (check_Key(admin_keys,request,key)){
                log.info("\nIP[{}],管理密钥访问成功\n",user.getIp());
                user.setType(User.Type.Admin);
            }else {
                log.info("\nIP[{}],未知密钥访问拦截\n",user.getIp());
                user.setType(User.Type.Unknown);
            }
            request.setAttribute("user",user);
            return true;
        }else {
            msg.setCode(500);
            msg.setTitle("验证密钥为空");
            msg.setMessage("错误！请输入访问密钥key");
        }
        request.setAttribute("msg",msg);
        request.getRequestDispatcher("/error/error_key").forward(request,response);
        return false;
    }

    public boolean check_Key(Set<String> keys, HttpServletRequest request, String key) throws Exception {
        HttpSession session = request.getSession();
        String cid = Convert.toStr(session.getAttribute("cid"),"");
        String sid = Convert.toStr(session.getAttribute("sid"),"");
        if (keys!=null&&!keys.isEmpty()) {
            for (String s : keys) {
                if (EncryptUtil.getMyMd5(cid+s+sid).equalsIgnoreCase(key))
                    return true;
            }
        }
        return false;
    }
}
