package com.zhangheng.file_servser;

import com.zhangheng.file_servser.entity.CaptchaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class FileServserApplicationTests {

    @Autowired
    private CaptchaConfig captcha;
    @Test
    void contextLoads() throws Exception{
        System.out.println(captcha);
    }

}
