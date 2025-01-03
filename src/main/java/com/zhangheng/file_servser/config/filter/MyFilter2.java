package com.zhangheng.file_servser.config.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file_servser.model.StatusCode;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 14:43
 * @version: 1.0
 * @description: 每次会话次数限制
 */
@WebFilter
@Order(2)
@Slf4j
public class MyFilter2 extends MyFilter {
//    @Value("#{'${config.filter2.excludePath}'.split(',')}")
//    private HashSet<String> excludePath;
//    @Value("#{'${server.servlet.context-path}'}")
//    private String contextPath;

    //    private String[] excludePath={
//            "/static/**",//静态资源
//            "/favicon.ico",//网址图标
//            "/error/**",//错误
//            "/getVerify/",
//            "/download/getAllFileType",
//            "/download/show/",
//            "/download/split/",
//    };
//    private final Logger log = LoggerFactory.getLogger(getClass());
//    @Value(value = "#{'${config.max-request-counts}'}")
//    private Integer maxCount;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = CusAccessObjectUtil.getUri(req);
        if (!Objects.equals(contextPath, "/")) {
            uri = uri.replace(contextPath, "");
        }
        boolean isFilter = true;
        isFilter = isFilter(filterConfig.getCountFilterExcludePath(), uri, isFilter);
        if (isFilter) {
            HttpSession session = req.getSession();
            Integer sessionCount = Convert.toInt(session.getAttribute(uri + "_c"), 0);
            sessionCount = sessionCount + 1;
            session.setAttribute(uri + "_c", sessionCount);
            if (sessionCount > filterConfig.getRequestMaxCount()) {
                Message msg = new Message();
                msg.setTitle("请求达上限!");
                msg.setCode(StatusCode.HTTP_403.getCode());
                msg.setMessage(StatusCode.HTTP_403.getMessage());
                wirterJson(response, JSONUtil.parse(msg).toString(), msg.getCode());
                log.warn("\n请求次数过多,路径[{}]-次数[{}]\n", uri, sessionCount);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
