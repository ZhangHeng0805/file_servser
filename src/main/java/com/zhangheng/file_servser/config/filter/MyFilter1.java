package com.zhangheng.file_servser.config.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file.FileUtil;
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
import java.util.Date;
import java.util.Objects;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 14:43
 * @version: 1.0
 * @description: 频率限制
 */
@WebFilter
@Order(1)
@Slf4j
public class MyFilter1 extends MyFilter {

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
        isFilter = isFilter(filterConfig.getRateFilterExcludePath(), uri, isFilter);
        if (isFilter) {
            long nowTime = new Date().getTime();
            HttpSession session = req.getSession();
            Long sessionTime = Convert.toLong(session.getAttribute(uri + "_t"), null);
            session.setAttribute(uri + "_t", nowTime);
            if (sessionTime != null) {
                //判断请求间隔时间
                long abs = Math.abs(nowTime - sessionTime);
                if (abs < filterConfig.getRequestMinIntervalMs()) {
                    Message msg = new Message();
                    msg.setTitle("请求频繁!");
                    msg.setCode(StatusCode.HTTP_503.getCode());
                    msg.setMessage(StatusCode.HTTP_503.getMessage());
                    wirterJson(response, JSONUtil.parse(msg).toString(), msg.getCode());
                    log.warn("\n请求频率过快,路径[{}]-间隔[{}ms]\n", uri, abs);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }


}
