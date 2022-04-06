package com.zhangheng.file_servser.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 秘钥验证
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-04-06 17:14
 */
@Service
public class KeyService {

    @Value("#{'${keys}'.split(',')}")
    private List<String> keys;
    @Value("#{'${admin_keys}'.split(',')}")
    private List<String> admin_keys;

    /**
     * 判断是否为普通秘钥
     * @param key
     * @return
     */
    public boolean isKeys(String key){
        if (key!=null) {
            //遍历普通秘钥
            for (String s : keys) {
                if (s.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为管理秘钥
     * @param key
     * @return
     */
    public boolean isAdminKeys(String key){
        if (key!=null) {
            //遍历管理秘钥
            for (String s : admin_keys) {
                if (s.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }
}
