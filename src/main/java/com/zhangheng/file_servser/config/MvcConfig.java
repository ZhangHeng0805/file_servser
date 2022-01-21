package com.zhangheng.file_servser.config;

import com.zhangheng.file_servser.config.interceptor.VerifyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-19 10:51
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private VerifyInterceptor verifyInterceptor;
    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //上传验证
        registry.addInterceptor(verifyInterceptor).addPathPatterns("/upload/**");
    }
}
