package com.zhangheng.file_servser.utils;

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
        String path = Objects.requireNonNull(applicationContext.getServletContext()).getContextPath();
        int port = getWebServerPort(applicationContext);
        String protocol = "http";
        if (port == 443) {
            protocol = "https";
        }
        if (!(port == 443 || port == 80)) {
            host = host + ":" + port;
        }
        if (path == null || "/".equals(path)) {
            path = "";
        }
        return protocol + "://" + host + path + "/";
    }
}
