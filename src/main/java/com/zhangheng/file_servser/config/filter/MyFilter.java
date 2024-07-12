package com.zhangheng.file_servser.config.filter;

import com.zhangheng.file_servser.utils.CusAccessObjectUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 15:54
 * @version: 1.0
 * @description:
 */
public abstract class MyFilter implements Filter {


    public CusAccessObjectUtil.Request getRequestInfo(HttpServletRequest request) {
        return new CusAccessObjectUtil.Request(request);
    }


    /**
     * 地址过滤判断
     *
     * @param paths   url地址数组
     * @param url     判断的url
     * @param defualt 默认值
     * @return 若存在，返回非默认值；不存在，则返回默认值
     */
    protected boolean isFilter(Set<String> paths, String url, boolean defualt) {
        for (String s : paths) {
            if (url.startsWith(s.replace("*", ""))) {
                defualt = !defualt;
                break;
            }
        }
        return defualt;
    }

    protected ServletResponse wirterJson(ServletResponse response, String json) throws IOException {
        return wirterJson(response, json, 200);
    }

    protected ServletResponse wirterJson(ServletResponse response, String json, Integer state) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setStatus(state);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(json);
        writer.close();
        return response;
    }

}
