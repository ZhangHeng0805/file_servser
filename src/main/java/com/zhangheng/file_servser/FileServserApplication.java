package com.zhangheng.file_servser;

import com.zhangheng.file_servser.utils.ApplicationUtil;
import com.zhangheng.system.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.Async;

import java.util.Arrays;

@SpringBootApplication
@EntityScan
@ConfigurationPropertiesScan
@ServletComponentScan("com.zhangheng.file_servser.config.filter")
@Slf4j
public class FileServserApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServserApplication.class, args);
    }


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
                String localUrl = ApplicationUtil.getBaseUrl(webServerApplicationContext, "localhost");
                String externalUrl = ApplicationUtil.getBaseUrl(webServerApplicationContext, NetUtil.getLocalIpAddress());
                sb.append("\t").append(id).append(" is running! Access URLs:\n");
                sb.append("\tLocal:   ").append(localUrl).append("\n");
                sb.append("\tExternal:   ").append(externalUrl).append("\n");
//        sb.append("\tSwagger: http://localhost").append(port).append(path).append("/doc.html\n");
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
}
