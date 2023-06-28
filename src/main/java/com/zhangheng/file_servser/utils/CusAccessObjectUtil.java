package com.zhangheng.file_servser.utils;

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
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        //如果没有代理，则获取真实ip
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }


    /**
     * 获取请求对象中的信息
     * @param request 请求对象
     * @return {
     * ip:请求ip,
     * uri:请求行中的资源地址,
     * url:请求的完整url,
     * params:参数部分,
     * host:客户端的主机名,
     * port:客户端的端口号,
     * method:请求使用的HTTP方法,
     * }
     */
    public static Map<String,Object> getRequestInfo(HttpServletRequest request) {
        Map<String,Object> map=new HashMap<>();
        String ip = getIpAddress(request);//发出请求的IP地址
        map.put("ip", ip);
        String uri = request.getRequestURI();//返回请求行中的资源名称
        try {
            map.put("uri", URLDecoder.decode(uri,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            map.put("uri", uri);
        }
        String url = request.getRequestURL().toString();//获得客户端发送请求的完整url
        try {
            map.put("url", URLDecoder.decode(url,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            map.put("url", url);
        }
        String params = request.getQueryString();//返回请求行中的参数部分
        map.put("params",params);
        String host = request.getRemoteHost();//返回发出请求的客户端的主机名
        map.put("host",host);
        Integer port = request.getRemotePort();//返回发出请求的客户端的端口号。
        map.put("port",port);
        String method = request.getMethod();//请求使用的HTTP方法（例如：GET、POST、PUT）,
        map.put("method",method);
//        System.out.println("IP地址:" + ip);
//        System.out.println("完整url:" + url);
//        System.out.println("资源名称:" + uri);
//        System.out.println("参数部分:" + params);
//        System.out.println("主机名:" + host);
//        System.out.println("端口号:" + port);
        return map;
    }

    /**
     * 获取请求头中的User-Agent
     * @param request 请求对象
     * @return 请求头中的User-Agent
     */
    public static String getUser_Agent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua;
    }

    /**
     * 获取请求行中的资源名称
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getUri(HttpServletRequest request) throws UnsupportedEncodingException {
        String uri = request.getRequestURI();//返回请求行中的资源名称
        return URLDecoder.decode(uri,"UTF-8");
    }

    /**
     * 判断Cookie中是否存在key-value
     * @param request
     * @param cookieName
     * @param cookieValue
     * @return
     */
    public static Boolean isExitCookie(HttpServletRequest request,String cookieName,String cookieValue){
        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
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
     * @param request 请求对象
     * @return 完整格式请求信息
     */
    public static String getCompleteRequest(HttpServletRequest request){
        Map<String, Object> requestInfo = getRequestInfo(request);
        StringBuilder sb = new StringBuilder();
        sb.append("<"+requestInfo.get("method")+">\t");
        sb.append("["+requestInfo.get("uri")+"]\t");
        sb.append("("+requestInfo.get("ip")+")\t");
        sb.append("{"+getUser_Agent(request)+"}\t");
        return sb.toString();
    }
}
