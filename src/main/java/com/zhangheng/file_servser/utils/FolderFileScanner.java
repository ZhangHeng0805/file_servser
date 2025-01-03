package com.zhangheng.file_servser.utils;

import com.zhangheng.file_servser.model.MyException;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*文件扫描*/
public class FolderFileScanner {

    private static List<String> scanFiles = new ArrayList<>();

    /**linkedList实现**/
    private static LinkedList<File> queueFiles = new LinkedList<File>();

    /**
     * 递归扫描指定文件夹下面的指定文件
     * @param folderPath 需要进行文件扫描的文件夹路径
     * @return ArrayList<Object> 文件夹中所有文件的绝对路径
     * @throws Exception
     */
    public static List<String> scanFilesWithRecursion(String folderPath) throws Exception {
        List<String> dirctorys = new ArrayList<>();
        File directory = new File(folderPath);
        if (!directory.exists()){
            throw new MyException("路径错误",'"' + folderPath + '"' + "(文件夹路径不存在) input path is not a Directory , please input the right path of the Directory. ^_^...^_^");
        }
        if(!directory.isDirectory()){
            throw new MyException("文件类型错误",'"' + folderPath + '"' + "(不是文件夹路径) input path is not a Directory , please input the right path of the Directory. ^_^...^_^");
        } else {
            File[] filelist = directory.listFiles();
            for(int i = 0; i < filelist.length; i ++){
                //如果当前是文件夹，进入递归扫描文件夹
                if(filelist[i].isDirectory()){
                    dirctorys.add(filelist[i].getAbsolutePath());
                    //递归扫描下面的文件夹
                    scanFilesWithRecursion(filelist[i].getAbsolutePath());
                }
                //非文件夹
                else{
                    scanFiles.add(filelist[i].getCanonicalPath());
                }
            }
        }
        return scanFiles;
    }

    /**
     *
     * 非递归方式扫描指定文件夹下面的所有文件
     * 速度更快
     * @param folderPath 需要进行文件扫描的文件夹路径
     * @return ArrayList<Object> 文件夹中所有文件的绝对路径
     * @throws Exception
     */
    public static List<String> scanFilesWithNoRecursion(String folderPath) throws Exception {
        File directory = new File(folderPath);
        if (!directory.exists()){
            throw new MyException("路径错误",'"' + folderPath + '"' + "(文件夹路径不存在) input path is not a Directory , please input the right path of the Directory. ^_^...^_^");
        }
        if(!directory.isDirectory()){
            throw new MyException("文件类型错误",'"' + folderPath + '"' + "(不是文件夹路径) input path is not a Directory , please input the right path of the Directory. ^_^...^_^");
        }
        else{
            //首先将第一层目录扫描一遍
            File[] files = directory.listFiles();
            //遍历扫出的文件数组，如果是文件夹，将其放入到linkedList中稍后处理
            for(int i = 0; i < files.length; i ++){
                if(files[i].isDirectory()){
                    queueFiles.add(files[i]);
                }else{
                    //暂时将文件名放入scanFiles中
                    scanFiles.add(files[i].getAbsolutePath());
                }
            }
            //如果linkedList非空遍历linkedList
            while(!queueFiles.isEmpty()){
                //移出linkedList中的第一个
                File headDirectory = queueFiles.removeFirst();
                File[] currentFiles = headDirectory.listFiles();
                for(int j = 0; j < currentFiles.length; j ++){
                    if(currentFiles[j].isDirectory()){
                        //如果仍然是文件夹，将其放入linkedList中
                        queueFiles.add(currentFiles[j]);
                    }else{
                        scanFiles.add(currentFiles[j].getAbsolutePath());
                    }
                }
            }
        }
        return scanFiles;
    }

}
