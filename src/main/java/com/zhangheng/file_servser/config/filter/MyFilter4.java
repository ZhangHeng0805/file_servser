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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 16:07
 * @version: 1.0
 * @description: cookie过滤
 */
@WebFilter
@Order(4)
@Slf4j
public class MyFilter4 extends MyFilter {
    private final String[] paths = {
            "/deleteFile",
            "/renameFile",
            "/getFileList",
            "/upload/",
            "/download/getAllFileType",
            "/download/findFileList",
            "/getVerify/",
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = CusAccessObjectUtil.getUri(req);
        if (!Objects.equals(contextPath, "/")) {
            uri = uri.replace(contextPath, "");
        }
        boolean isFilter = false;

        isFilter = isFilter(new HashSet<>(Arrays.asList(paths)), uri, isFilter);
        if (isFilter) {
            HttpSession session = req.getSession();
            String cid = Convert.toStr(session.getAttribute("cid"), "");
            String sid = Convert.toStr(session.getAttribute("sid"), "");
            Boolean isCid = CusAccessObjectUtil.isExitCookie(req, "zhangheng0805_cid", cid);
            Boolean isSid = CusAccessObjectUtil.isExitCookie(req, "zhangheng0805_sid", sid);
            if (!isCid || !isSid) {
                Message msg = new Message();
                msg.setTitle("身份验证过期,请刷新页面");
                msg.setCode(StatusCode.HTTP_401.getCode());
                msg.setMessage(StatusCode.HTTP_401.getMessage());
                wirterJson(response, JSONUtil.parse(msg).toString(), msg.getCode());
                log.warn("\n身份核验失败-路径[{}]\n", uri);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
