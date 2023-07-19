package com.zhangheng.file_servser.controller;

import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-07-13 15:58
 * @version: 1.0
 * @description:
 */
@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler({Exception.class})
    public String handleException(Exception e, HttpServletRequest request, Map<String,Object> map){
//        request.setAttribute("javax.servlet.error.status_code",code);
//        System.out.println("error："+JSONUtil.parse(map).toStringPretty());
        map.put("message",e.getMessage());
        return "forward:/error";
    }
}
