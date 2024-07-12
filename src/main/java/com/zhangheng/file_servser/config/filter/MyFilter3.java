package com.zhangheng.file_servser.config.filter;

import cn.hutool.json.JSONUtil;
import com.zhangheng.file_servser.controller.CaptchaController;
import com.zhangheng.file_servser.controller.WebController;
import com.zhangheng.bean.Message;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 16:07
 * @version: 1.0
 * @description: 验证码过滤
 */
@WebFilter
@Order(3)
public class MyFilter3 extends MyFilter {
    private String[] paths={
            "/upload/*",
            "/web/upload",
            "/deleteFile",
            "/renameFile",
            "/getFileList",
            "/download/findFileList",
    };
    @Value("#{'${server.servlet.context-path}'}")
    private String contextPath;
    private final Logger log= LoggerFactory.getLogger(getClass());
    @Resource
    private CaptchaController captchaController;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = CusAccessObjectUtil.getUri(req);
        if (!Objects.equals(contextPath, "/")) {
            uri = uri.replace(contextPath, "");
        }
        boolean isFilter=false;
        isFilter = isFilter(new HashSet<>(Arrays.asList(paths)),uri,isFilter);
        if (isFilter){
            String code = req.getParameter("code");
            Message msg = captchaController.verifyMathCheck(code, false, req);
            if (msg.getCode()!=200){
                wirterJson(response, JSONUtil.parse(msg).toString(), msg.getCode());
                log.warn("\n验证码核验失败：{}-路径[{}]\n",msg.getTitle(),uri);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
