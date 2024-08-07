package com.zhangheng.file_servser.config.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file.FileUtil;
import com.zhangheng.file_servser.entity.StatusCode;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private HashSet<String> excludePath;
    @Value("#{'${server.servlet.context-path}'}")
    private String contextPath;


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
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value(value = "#{'${config.request-interval}'}")
    private Long minInterval;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        CusAccessObjectUtil.Request info = getRequestInfo(req);
        String requestInfo = CusAccessObjectUtil.getCompleteRequest(info);
        long contentLengthLong = request.getContentLengthLong();
        if (!info.getUri().startsWith(contextPath + "/static")) {
            log.info("\n访问日志:{}，大小:{}\n", requestInfo, FileUtil.fileSizeStr(contentLengthLong));
        }

        String uri = info.getUri();
        if (!Objects.equals(contextPath, "/"))
            uri = uri.replace(contextPath, "");
        boolean isFilter = true;
        isFilter = isFilter(excludePath, uri, isFilter);
        if (isFilter) {
            long nowTime = new Date().getTime();
            HttpSession session = req.getSession();
            Long sessionTime = Convert.toLong(session.getAttribute(uri + "_t"), null);
            session.setAttribute(uri + "_t", nowTime);
            if (sessionTime != null) {
                //判断请求间隔时间
                long abs = Math.abs(nowTime - sessionTime);
                if (abs < minInterval) {
                    Message msg = new Message();
                    msg.setCode(503);
                    msg.setTitle("请求频繁!");
                    msg.setMessage(StatusCode.Http503);
                    wirterJson(response, JSONUtil.parse(msg).toString(), msg.getCode());
                    log.warn("\n请求频率过快,路径[{}]-间隔[{}ms]\n", uri, abs);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }


}
