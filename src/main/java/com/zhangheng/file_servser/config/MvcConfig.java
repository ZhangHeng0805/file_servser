package com.zhangheng.file_servser.config;

import com.zhangheng.file_servser.config.interceptor.UploadInterceptor;
import com.zhangheng.file_servser.config.interceptor.VerifyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-19 10:51
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private VerifyInterceptor verifyInterceptor;
    @Resource
    private UploadInterceptor uploadInterceptor;

    public static String[] excluePath1={
            "/",//首页
            "/download/access-key",//首页
            "/download/show/**",//下载
            "/download/split/**",//分片下载
            "/static/**",//静态资源
            "/favicon.ico",//网址图标
            "/error/**",//错误
            "/getVerify/**",//验证码
            "/download/getAllFileType"//获取文件夹列表名
    };

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //验证
        registry.addInterceptor(verifyInterceptor).excludePathPatterns(excluePath1);
        //上传
        registry.addInterceptor(uploadInterceptor).addPathPatterns("/upload/saveMulFile");
    }
//    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
//        resolver.setTemplateEngine((ISpringTemplateEngine) templateEngine());
//        resolver.setCharacterEncoding("UTF-8");
//        resolver.setOrder(1);
//        resolver.setViewNames(new String[] {"index"}); // 设置允许处理的视图名称
//        registry.viewResolver(resolver);
//    }
//    private TemplateEngine templateEngine() {
//        SpringTemplateEngine engine = new SpringTemplateEngine();
//        engine.setEnableSpringELCompiler(true);
//        // 配置模板解析器等
//        return engine;
//    }
}
