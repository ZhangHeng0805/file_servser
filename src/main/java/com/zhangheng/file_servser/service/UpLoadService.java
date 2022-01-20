package com.zhangheng.file_servser.service;

import com.zhangheng.file_servser.entity.Message;
import com.zhangheng.file_servser.utils.FiletypeUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

/**
 *
 * @author 张恒
 * @program: file_servser
 * @email zhangheng.0805@qq.com
 * @date 2022-01-19 9:22
 */
@Service
public class UpLoadService {
    @Value("${baseDir}")
    private String baseDir;
    @Value("${appName}")
    private String appName;
    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 删除文件
     * @param path 图片路径
     * @return is 否删除成功
     */
    public boolean deleteFile(String path){
        File file = new File(baseDir+path);
        if (file.exists()){
            boolean delete = file.delete();
            if (delete) {
                log.info("文件删除成功："+path);
                String dirPath=path.split("/")[0];
                File dir = new File(baseDir + dirPath);
                if (dir.listFiles().length<=0){
                    boolean b = deleteDir(dirPath);
                    if (b){
                        log.info("空文件夹清除成功!");
                    }else {
                        log.info("空文件夹清除失败!");
                    }
                }
                return true;
            }else {
                log.error("文件删除失败："+path);
            }
        }else {
            log.error("删除文件的不存在："+path);
        }
        return false;
    }

    /**
     * 保存base64格式的图片
     * @param base64 base64字符
     * @param fileName 文件名
     * @param savePath 保存路径文件夹(父级文件夹的名称)
     * @return 返回保存路径，如果返回为null则保存失败
     */
    public String base64ToImg(String base64, String fileName, String savePath) {
        File file = null;
        String path=null;
        //创建文件目录
        String filePath = baseDir+savePath;
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        try {
            String type=".jpg";
            String[] split = base64.split(",");
            if (split.length>1) {
                type = "." + split[0].split("/")[1].split(";")[0];
            }
            //判断文件名长度
            fileName=fileName.length()<8?fileName:fileName.substring(0,8);
            //构造文件名
            String name="/"+appName
                    + UUID.randomUUID().toString().substring(0, 5)
                    + "_" + fileName;
            base64 = split[1];
            byte[] bytes = Base64.getDecoder().decode(base64);
            file=new File(filePath + name + type);
            fos = new java.io.FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            path = savePath+name+type;
            log.info("图片名：{}", path);
            log.info("图片大小：{}kb", Message.twoDecimalPlaces((double) file.length()/1024));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }

    /**
     * 保存MultipartFile格式的文件（MultipartFile文件），并返回保存路径，(如果返回为null则保存失败)
     * @param file 文件
     * @param fileName 文件名称
     * @param type 文件文件夹（父级文件夹名称）
     * @return 返回保存路径，如果返回为null则保存失败
     */
    public String saveFile(MultipartFile file, String fileName, String type) {
        String path = null;
        //图片不为空
        if (!fileName.isEmpty()) {
            //图片小于2Mb
            String filename = file.getOriginalFilename();
            log.info("文件名：{}", filename);
            log.info("图片大小：{}kb", Message.twoDecimalPlaces((double) file.getSize()/1024));
            //排除文件名中的空格
            filename = filename.replaceAll(" ", "");
            //判断文件名长度
            fileName = fileName.length() < 8 ? fileName : fileName.substring(0, 8);
            //保存文件名
            String name = type + "/" + appName
                    + UUID.randomUUID().toString().substring(0, 5)
                    + "_" + fileName + filename.substring(filename.lastIndexOf("."));
            File outFile = new File(baseDir + name);
            try {
                FileUtils.copyToFile(file.getInputStream(), outFile);
                path = name;
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return path;
    }

    /**
     * 递归删除文件夹
     * @param path 文件夹路径
     * @return
     */
    private boolean deleteDir(String path){
        File file = new File(baseDir + path);
        if (file.isDirectory()) {
            try {
                FileUtils.deleteDirectory(file);
                return true;
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }
}
