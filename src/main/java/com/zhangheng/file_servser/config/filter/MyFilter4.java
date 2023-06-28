package com.zhangheng.file_servser.config.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.entity.StatusCode;
import com.zhangheng.file_servser.utils.CusAccessObjectUtil;
import com.zhangheng.util.TimeUtil;
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

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 16:07
 * @version: 1.0
 * @description: cookie过滤
 */
@WebFilter
@Order(4)
public class MyFilter4 extends MyFilter {
    private String[] paths={
            "/deleteFile",
            "/upload/",
            "/download/getAllFileType",
            "/download/findFileList",
            "/getVerify/math",
    };
    @Value("#{'${server.servlet.context-path}'}")
    private String contextPath;
    private Logger log= LoggerFactory.getLogger(getClass());
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = CusAccessObjectUtil.getUri(req);
        if (contextPath!="/")
            uri=uri.replace(contextPath,"");
        boolean isFilter=false;
        isFilter = isFilter(paths,uri,isFilter);
        if (isFilter){
            HttpSession session = req.getSession();
            String cid = Convert.toStr(session.getAttribute("cid"),"");
            String sid = Convert.toStr(session.getAttribute("sid"),"");
            Boolean isCid = CusAccessObjectUtil.isExitCookie(req, "zhangheng0805_cid", cid);
            Boolean isSid = CusAccessObjectUtil.isExitCookie(req, "zhangheng0805_sid", sid);
            if (!isCid||!isSid){
                Message msg = new Message();
                msg.setTime(TimeUtil.getNowTime());
                msg.setCode(401);
                msg.setTitle("身份验证失败,请刷新重试");
                msg.setMessage(StatusCode.Http401);
                wirterJson(response, JSONUtil.parse(msg).toString(), msg.getCode());
                log.warn("\n身份核验失败-路径[{}]\n",uri);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
