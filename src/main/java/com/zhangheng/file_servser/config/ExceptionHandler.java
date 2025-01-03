package com.zhangheng.file_servser.config;

import cn.hutool.core.util.StrUtil;
import com.zhangheng.bean.Message;
import com.zhangheng.file_servser.model.StatusCode;
import com.zhangheng.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * @author: ZhangHeng
 * @email: zhangheng_0805@163.com
 * @date: 2023-04-26 14:57
 * @version: 1.0
 * @description:
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandler extends DefaultErrorAttributes {
    @Value("#{'${server.servlet.context-path}'}")
    private String contextPath;

    //    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Throwable.class)
    public Message e(Exception e) {
        Message msg = new Message();
        msg.setCode(500);
        msg.setTitle("错误异常");
        msg.setMessage(e.getMessage());
        log.error("\n全局异常捕获:{}\n", e.toString());
        return msg;
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        String message = errorAttributes.get("message").toString();
        errorAttributes.put("timestamp", TimeUtil.getNowTime());
        errorAttributes.put("index",contextPath);
        if (StrUtil.isBlank(message) || message.equals("No message available")) {
            Object status = errorAttributes.get("status");
            String msg = "";
            for (StatusCode value : StatusCode.values()) {
                if (status.equals(value.getCode())){
                    msg = value.getMessage();
                    break;
                }
            }
            errorAttributes.put("message", msg);
        } else {
            errorAttributes.put("message", message);
        }
        log.error("\n错误异常请求:{}\n", errorAttributes);
        return errorAttributes;
    }

    /**
     * 获取异常详细信息，知道出了什么错，错在哪个类的第几行 .
     *
     * @param ex
     * @return
     */
    public static String getExceptionDetail(Exception ex) {
        String ret = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream pout = new PrintStream(out);
            ex.printStackTrace(pout);
            ret = new String(out.toByteArray());
            pout.close();
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }

}
