package com.zhangheng.file_servser.utils;

import com.zhangheng.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class ApplicationStartup {

    @Value("${project.version:}")
    private String version;

    private static ConfigurableApplicationContext applicationContext = null;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        applicationContext = event.getApplicationContext();
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        String id = applicationContext.getId();
        StringBuilder sb = new StringBuilder("\n\n\n---------------------------"+id+": "+version+"-------------------------------\n");

        if (applicationContext instanceof ServletWebServerApplicationContext) {
            ServletWebServerApplicationContext webServerApplicationContext = (ServletWebServerApplicationContext) applicationContext;
            WebServer webServer = webServerApplicationContext.getWebServer();
            if (webServer != null) {
                String host = NetworkUtil.getIp();
                String contextPath = webServerApplicationContext.getServletContext() == null ? null : webServerApplicationContext.getServletContext().getContextPath();
                if (contextPath == null || "/".equals(contextPath)) {
                    contextPath = "";
                }

                sb.append("\t").append(id).append(" is running! Access URLs:\n");
                String localUrl = ApplicationUtil.getBaseUrl(webServerApplicationContext, "localhost");
                sb.append("\tLocal:   ").append(localUrl).append("\n");
                List<String> urls;
                if (webServer instanceof TomcatWebServer) {
                    urls = getWebServerUrls((TomcatWebServer) webServer, host, contextPath);
                } else {
                    urls = Collections.singletonList(ApplicationUtil.getBaseUrl(webServerApplicationContext, host));
                }
                for (String url : urls) {
                    sb.append("\tAccess URL:   ").append(url).append("\n");
                }
            }
        }
        sb.append("\tDefault Profiles: ").append(Arrays.toString(env.getDefaultProfiles())).append("\n");
        sb.append("\tActive Profiles:  ").append(Arrays.toString(env.getActiveProfiles())).append("\n");
        sb.append("---------------------------Loaded Configuration Files-------------------------------");
        env.getPropertySources().forEach(source -> {
            if (source instanceof OriginTrackedMapPropertySource) {
                sb.append("\n\t").append(source.getName());
            }
        });
        sb.append("\n---------------------------------------------------------------------\n\n");

        log.info(sb.toString());
    }

    private List<String> getWebServerUrls(TomcatWebServer tomcatWebServer, String host, String contextPath) {
        List<String> urls = new ArrayList<>();
        if (contextPath.startsWith("/")){
            contextPath = contextPath.substring(1);
        }
        for (org.apache.catalina.connector.Connector connector : tomcatWebServer.getTomcat().getService().findConnectors()) {
            urls.add(getUrl(connector.getScheme(), host, connector.getPort(), contextPath));
        }
        return urls;
    }

    private static String getUrl(String scheme, String host, int port, String contextPath) {
        if ((port == 80 && scheme.equalsIgnoreCase("http")) || (port == 443 && scheme.equalsIgnoreCase("https"))) {
            return scheme + "://" + host + "/" + contextPath;
        }
        return scheme + "://" + host + ":" + port + "/" + contextPath;
    }

    public static void restart() {
        if (applicationContext == null) {
            return;
        }
        Thread thread = new Thread(() -> {
            ApplicationArguments args = applicationContext.getBean(ApplicationArguments.class);
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
            for (Object object : beans.values()) {
                applicationContext.close();
                applicationContext = SpringApplication.run(object.getClass(), args.getSourceArgs());
                break;
            }
        });
        thread.start();
    }
}
