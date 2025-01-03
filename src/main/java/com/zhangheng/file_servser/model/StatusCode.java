package com.zhangheng.file_servser.model;

import lombok.Getter;

/**
 * Http状态码
 * @author 张恒
 * @program: zh_tools
 * @email zhangheng.0805@qq.com
 * @date 2022-11-26 23:34
 */
@Getter
public enum StatusCode {

    HTTP_OK(200, "OK"),
    HTTP_400(400, "【ლ(ó﹏òლ)对不起】，请求的语法错误【(๑⁼̴̀д⁼̴́๑)ﾄﾞﾔｯ‼ What are you 弄啥嘞！】，服务器无法理解哦【( ´▽` )ﾉ再见】"),
    HTTP_401(401, "对不起【o(╥﹏╥)o】，您的请求无法验明正身，我不能放你过去哦【(๑‾᷅^‾᷅๑) 嫌弃你】"),
    HTTP_403(403, "对不起【o(╥﹏╥)o】，服务拒绝您的请求，我拒绝你【（︶︿︶）＝╭∩╮ 比中指】"),
    HTTP_404(404, "对不起【o(╥﹏╥)o】，没有找到你需要的东西哦【∑(っ°Д°;)っ卧槽，不见了】"),
    HTTP_500(500, "抱歉【ε=(´ο｀*)))唉】，服务器遇到错误【Σσ(・Д・；)我我我什么都没做!!!】，无法完成请求【๑乛◡乛๑卡在了奇怪的地方】"),
    HTTP_503(503, "请求频繁，我都忙不过来了，太快了服务器受不了啊【(ó﹏ò｡)难受】，慢一点哦【_(:3」∠❀)_菊花碎了一地！】");

    private final Integer code;
    private final String message;
    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
