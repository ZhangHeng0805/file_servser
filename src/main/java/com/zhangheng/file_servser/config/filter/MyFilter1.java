package com.zhangheng.file_servser.config.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file.FileUtil;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 14:43
 * @version: 1.0
 * @description: 频率限制
 */
@WebFilter
@Order(1)
public class MyFilter1 extends MyFilter {
    @Value("#{'${config.filter1.excludePath}'.split(',')}")
    private String[] excludePath;


//    private String[] excludePath={
//            "/static/**",//静态资源
//            "/favicon.ico",//网址图标
//            "/error/**",//错误
//            "/download/getAllFileType",
//            "/getVerify/",
//            "/download/show/",
//            "/download/split/",
//
//    };
    private Logger log= LoggerFactory.getLogger(getClass());

    @Value(value = "#{'${config.request-interval}'}")
    private Long minInterval;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String requestInfo = CusAccessObjectUtil.getCompleteRequest(req);
        long contentLengthLong = request.getContentLengthLong();
        log.info("\n访问日志:{}，大小:{}\n",requestInfo, FileUtil.fileSizeStr(contentLengthLong));

        String uri = CusAccessObjectUtil.getUri(req);
        boolean isFilter=true;
        isFilter = isFilter(excludePath,uri, isFilter);
        if (isFilter){
            long nowTime = new Date().getTime();
            HttpSession session = req.getSession();
            Long sessionTime = Convert.toLong(session.getAttribute(uri+"_t"), null);
            session.setAttribute(uri+"_t", nowTime);
            if (sessionTime != null) {
                //判断请求间隔时间
                long abs = Math.abs(nowTime - sessionTime);
                if (abs < minInterval) {
                    Message msg = new Message();
                    msg.setCode(503);
                    msg.setTime(TimeUtil.getNowTime());
                    msg.setTitle("请求频繁!");
                    msg.setMessage(StatusCode.Http503);
                    wirterJson(response, JSONUtil.parse(msg).toString(),msg.getCode());
                    log.warn("\n请求频率过快,路径[{}]-间隔[{}ms]\n",uri,abs);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }




}
