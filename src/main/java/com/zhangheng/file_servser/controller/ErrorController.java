package com.zhangheng.file_servser.controller;

import com.zhangheng.bean.Message;
import com.zhangheng.file_servser.utils.TimeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 错误控制器
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-19 11:03
 */
@Controller
@RequestMapping("error")
public class ErrorController {

    /**
     * 秘钥错误接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("error_key")
    public Message error_key(HttpServletRequest request){
        Message msg = (Message) request.getAttribute("msg");
        if(msg==null||msg.getTime()==null) {
            msg=new Message();
            msg.setTime(TimeUtil.time(new Date()));
            msg.setCode(500);
            msg.setTitle("验证秘钥错误");
            msg.setMessage("对不起，你的验证秘钥错误！");
        }
        return msg;
    }

    /**
     * 404错误接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("err_404")
    public Message err_404(HttpServletRequest request){
        Message msg = (Message) request.getAttribute("msg");
        if(msg==null||msg.getTime()==null) {
            msg=new Message();
            msg.setTime(TimeUtil.time(new Date()));
            msg.setCode(404);
            msg.setTitle("没有找到");
            msg.setMessage("对不起！没有找到你需要的内容 o(╥﹏╥)o");
        }
        return msg;
    }

    /**
     * 500错误接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("err_500")
    public Message err_500(HttpServletRequest request){
        Message msg = (Message) request.getAttribute("msg");
        if(msg==null||msg.getTime()==null) {
            msg=new Message();
            msg.setTime(TimeUtil.time(new Date()));
            msg.setCode(500);
            msg.setTitle("出错了");
            msg.setMessage("哎呀！服务器出错了 (。・＿・。)ﾉI’m sorry~");
        }
        return msg;
    }
    @RequestMapping("test")
    public void test(){
        int i=1/0;
    }
}
