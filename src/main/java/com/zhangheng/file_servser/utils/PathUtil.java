package com.zhangheng.file_servser.utils;

import org.springframework.util.StringUtils;

import java.nio.file.Paths;

public abstract class PathUtil {

    /**
     * 获取目录下的子目录，确保relativePath必须是在root之下
     */
//    public static Path resolveSubPath(final Path root, final String relativePath) {
//        String path = relativePath.replace('\\', '/').replaceAll("\\.{2,}", "");
//        if (Pattern.compile("^[a-zA-Z]:([/\\\\].*)?").matcher(relativePath).matches()) {
//            path = path.substring(3);
//        }
//        path = path.replaceFirst("^[./\\\\]+", "");
//        return root.resolve(path).toAbsolutePath();
//    }

    /**
     * 去除首尾的空字符，\转/ 连续多个/转单个/ 连续多个.转单个.
     * @param path
     * @return
     */
    public static String cleanPath(String path) {
        if (StringUtils.hasLength(path)) {
            return path.trim()
                .replace('\\', '/')
                .replaceAll("/{2,}", "/")
                .replaceAll("\\.+", ".");
        }
        return path;
    }

    public static String normalize(String path) {
        if (!StringUtils.hasLength(path)) {
            return path;
        }
        StringBuilder sb = new StringBuilder(path.trim().replace('\\', '/'));
        // 替换多个斜杠为单个斜杠
        int index = sb.indexOf("//");
        while (index != -1) {
            sb.deleteCharAt(index);
            index = sb.indexOf("//", index);
        }
//        String p=path.trim()
//            .replace('\\', '/')
//            .replaceAll("/+", "/");
        // 使用Paths来normalize路径
        return Paths.get(sb.toString()).normalize().toString().replace('\\', '/');
    }

    /**
     * 得到一个绝对路径相对于一个跟目录的相对路径
     */
    public static String relativize(String root, String absolutePath) {
        if (root == null) {
            return absolutePath;
        }
        root = normalize(root);
        absolutePath = normalize(absolutePath);
        if (!absolutePath.startsWith(root)) {
            throw new IllegalArgumentException(absolutePath + " is not a Path that can be relativized against " + root);
        }
        return absolutePath.substring(root.length());
    }

    /**
     * 路径拼接
     */
    public static String join(String root, String... paths) {
        StringBuilder rootBuilder = new StringBuilder(root);
        for (String path : paths) {
            if (rootBuilder.toString().endsWith("/")) {
                rootBuilder.append(path.startsWith("/") ? path.substring(1) : path);
            } else {
                rootBuilder.append(path.startsWith("/") ? path : "/" + path);
            }
        }
        return rootBuilder.toString();
    }
}
