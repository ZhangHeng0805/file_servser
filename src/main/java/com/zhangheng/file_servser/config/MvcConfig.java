package com.zhangheng.file_servser.config;

import com.zhangheng.file_servser.config.interceptor.UploadInterceptor;
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
    @Autowired
    private UploadInterceptor uploadInterceptor;

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //验证
        registry.addInterceptor(verifyInterceptor).excludePathPatterns(
                "/",//首页
                "/download/show/**",//下载
                "/download/split/**",//分片下载
                "/static/**",//静态资源
                "/favicon.ico",//网址图标
                "/error/**",//错误
                "/getVerify/**",//验证码
                "/download/getAllFileType"//获取文件夹列表名
        );
        //上传
        registry.addInterceptor(uploadInterceptor).addPathPatterns("/upload/saveMulFile");
    }
}
