package com.zhangheng.file_servser.config.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file_servser.config.MvcConfig;
import com.zhangheng.file_servser.entity.StatusCode;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 14:43
 * @version: 1.0
 * @description: 每次会话次数限制
 */
@WebFilter
@Order(2)
public class MyFilter2 extends MyFilter {
    private String[] excludePath={
            "/static/**",//静态资源
            "/favicon.ico",//网址图标
            "/error/**",//错误
            "/getVerify/",
            "/download/getAllFileType",
            "/download/show/",
            "/download/split/",
    };
    private Logger log= LoggerFactory.getLogger(getClass());
    @Value(value = "#{'${config.max-request-counts}'}")
    private Integer maxCount;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = CusAccessObjectUtil.getUri(req);
        boolean isFilter=true;
        isFilter = isFilter(excludePath,uri,isFilter);
        if (isFilter){
            HttpSession session = req.getSession();
            Integer sessionCount = Convert.toInt(session.getAttribute("_c"), 0);
            sessionCount = sessionCount + 1;
            session.setAttribute("_c", sessionCount);
            if (sessionCount > maxCount) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter writer = response.getWriter();
                Message msg = new Message();
                msg.setCode(403);
                msg.setTime(TimeUtil.getNowTime());
                msg.setTitle("请求达上限!");
                msg.setMessage(StatusCode.Http403);
                writer.print(JSONUtil.parse(msg).toString());
                writer.close();
                log.warn("\n请求次数过多,路径[{}]-次数[{}]\n",uri,sessionCount);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}