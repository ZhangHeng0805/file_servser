package com.zhangheng.file_servser.utils;

import com.zhangheng.util.NetworkUtil;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

import java.util.Objects;

public class ApplicationUtil {

    public static int getWebServerPort(ServletWebServerApplicationContext applicationContext) {
        WebServer webServer = applicationContext.getWebServer();
        if (webServer == null) {
            return -1;
        }
        return webServer.getPort();
    }

    public static String getBaseUrl(ServletWebServerApplicationContext applicationContext, String host) {
        WebServer webServer = applicationContext.getWebServer();
        if (webServer == null) {
            return null;
        }
        String scheme = "http";
        int port = webServer.getPort();
        if (webServer instanceof TomcatWebServer) {
            scheme = ((TomcatWebServer) webServer).getTomcat().getConnector().getScheme();
        }
        String path = Objects.requireNonNull(applicationContext.getServletContext()).getContextPath();
        if (port == 443) {
            scheme = "https";
        }
        if (NetworkUtil.isIPv6Address(host)) {
            host = "[" + host + "]";
        }
        if (!(port == 443 || port == 80)) {
            host = host + ":" + port;
        }
        if (path == null || "/".equals(path)) {
            path = "";
        }
        return scheme + "://" + host + path + "/";
    }
}
