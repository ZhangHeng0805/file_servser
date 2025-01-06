package com.zhangheng.file_servser.service;

import com.zhangheng.file_servser.entity.AccessKey;
import com.zhangheng.file_servser.utils.SpringContextUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 秘钥验证
 *
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-04-06 17:14
 */
@Service
public class KeyService {

    @Resource
    private AccessKey accessKey;

    /**
     * 判断是否为普通秘钥
     *
     * @param key
     * @return
     */
    public boolean isCommonKeys(String key) {
        if (StringUtils.hasLength(key) && !ObjectUtils.isEmpty(accessKey.getCommonKeys())) {
            return accessKey.getCommonKeys().contains(key);
        }
        return false;
    }

    /**
     * 判断是否为管理秘钥
     *
     * @param key
     * @return
     */
    public boolean isAdminKeys(String key) {
        if (StringUtils.hasLength(key) && !ObjectUtils.isEmpty(accessKey.getAdminKeys())) {
            return accessKey.getAdminKeys().contains(key);
        }
        return false;
    }

    /**
     * 判断是否为临时秘钥
     *
     * @param key
     * @return
     */
    public boolean isTestKeys(String key) {
        if (StringUtils.hasLength(key) && !ObjectUtils.isEmpty(accessKey.getTestKeys())) {
            return accessKey.getTestKeys().contains(key);
        }
        return false;
    }

    public List<String> getInclude(String key) {
        if (StringUtils.hasLength(key)) {
            String property = SpringContextUtils.getEnvironment().getProperty("zhfs.key.file-path.include." + key, String.class);
            if (StringUtils.hasLength(property)) {
                return Arrays.stream(property.split(","))
                        .filter(StringUtils::hasLength)
                        .map(s -> {
                            if (s.startsWith("/")) {
                                return s.substring(1);
                            } else {
                                return s;
                            }
                        }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
