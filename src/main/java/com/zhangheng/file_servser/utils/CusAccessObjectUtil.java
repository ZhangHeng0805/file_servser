package com.zhangheng.file_servser.utils;

import lombok.Data;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义访问对象工具类
 * 获取request对象的IP地址等信息
 */
public class CusAccessObjectUtil {

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"
    };

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @param request 请求对象
     * @return 请求用户的IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        for (String header : HEADERS_TO_TRY) {
            ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        //如果没有代理，则获取真实ip
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }



    /**
     * 获取请求头中的User-Agent
     *
     * @param request 请求对象
     * @return 请求头中的User-Agent
     */
    public static String getUser_Agent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua;
    }

    /**
     * 获取请求行中的资源名称
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getUri(HttpServletRequest request) throws UnsupportedEncodingException {
        String uri = request.getRequestURI();//返回请求行中的资源名称
        return URLDecoder.decode(uri, "UTF-8");
    }

    /**
     * 判断Cookie中是否存在key-value
     *
     * @param request
     * @param cookieName
     * @param cookieValue
     * @return
     */
    public static Boolean isExitCookie(HttpServletRequest request, String cookieName, String cookieValue) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    if (cookie.getValue().equals(cookieValue)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取指定格式请求信息
     * [ip] / User-Agent
     *
     * @param request 请求对象
     * @return
     */
    public static String getRequst(HttpServletRequest request) {
        String s = "[" + getIpAddress(request) + "] / " + getUser_Agent(request);
        return s;
    }

    /**
     * 获取指定完整格式请求信息
     * <请求方式> [请求路径地址] (ip地址) {User-Agent}
     *
     * @param request 请求对象
     * @return 完整格式请求信息
     */
    public static String getCompleteRequest(HttpServletRequest request) {
        Request requestInfo = new Request(request);
        return getCompleteRequest(requestInfo);
    }

    public static String getCompleteRequest(Request requestInfo) {
        return "<" + requestInfo.getMethod() + ">\t" +
                "[" + requestInfo.getUri() + "]\t" +
                "(" + requestInfo.getIp() + ")\t" +
                "{" + requestInfo.getUserAgent() + "}\t";
    }

    @Data
    public static class Request {
        //请求IP
        private String ip;
        //资源路径
        private String uri;
        //请求完整地址
        private String url;
        private String method;
        private String params;
        private String userAgent;

        public Request(HttpServletRequest request) {
            this.ip = getIpAddress(request);
            try {
                this.uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                this.uri = request.getRequestURI();
            }
            try {
                this.url = URLDecoder.decode(request.getRequestURL().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                this.url = request.getRequestURL().toString();
            }
            this.method=request.getMethod();
            this.params=request.getQueryString();
            this.userAgent=request.getHeader("User-Agent");
        }

        public Request() {
        }

    }
}
