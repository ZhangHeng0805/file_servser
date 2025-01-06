# [服务器](https://gitee.com/ZhangHeng0805/file_servser/releases/download/V23.03.04/file_server.zip)下载
# 本项目不需要数据库，可直接运行
# 配置文件
* 在服务器jar包同级目录下新建一个application.yml配置文件，将下面配置粘贴进去，修改配置文件后需要重启服务器
```yaml
spring:
  servlet:
    multipart:
      #单个上传文件的最大值
      max-file-size: 100MB
      #最大请求文件大小（总文件大小）
      max-request-size: 100MB
server:
  tomcat:
    #设置Tomcat服务器中 HTTP POST 请求的最大表单提交大小
    max-http-form-post-size: 200MB
  servlet:
    session:
      #会话超时时间
      timeout: 10m
zhfs:
  key:
    #临时访问秘钥[浏览]
    test-keys: 123456
    #普通访问秘钥[上传，浏览]
    common-keys: 666666,888888
    #管理员访问秘钥[上传，浏览，删除,重命名]全部权限
    admin-keys: admin123456
    file-path:
      include:
        #秘钥允许访问文件路径，缺省代表不限制
        123456: /image/*.png,html
  config:
    captcha:
      #验证密难度
      difficulty: 50
      #验证码长度
      length: 4
      #验证码高度
      height: 100
      #验证码宽度
      width: 200
      #验证码模式：random-随机字符，math-数学运算
      mode: random
      #验证码干扰类型：coil-线圈干扰，line-横线干扰，gif_coil-动图线圈干扰
      type: coil
    filter:
      #请求频率限制,请求的间隔最小时间[ms]
      request-min-interval-ms: 1000
      #单次会话session最大请求次数
      request-max-count: 20
    server:
      #上传文件件名前缀名
      file-prefix: 星曦向荣网
      #服务版本,更新版本可以刷新前端缓存
      version: 25.01.06
      #最大文件名长度
      max-file-name-length: 35
      #文件保存路径最大长度
      max-file-path-length: 15
      #文件存储根目录
      home-dir: files/
```
> 默认访问地址：
> http://localhost:8088/zh-file/
# 一、文件上传（单个文件最大100Mb）
上传默认最大文件100MB，可以通过配置文件修改
# 二、文件下载
### 普通下载
* 访问路径：```http://localhost:8088/zh-file/download/show/+文件保存路径```
* 请求方式：get/post

> 例： ```http://localhost:8088/zh-file/download/show/text/星曦向荣网48122_张恒.pdf ```
### 断点分片下载
* 访问路径：```http://localhost:8088/zh-file/download/split/+文件保存路径```
* 请求方式：get/post
> * 例： ```http://localhost:8088/zh-file/download/split/text/星曦向荣网48122_张恒.pdf ```
> * 注意：此下载需要下载客户端支持断点分片下载功能

> 文件下载不需要请求数据，只需下载路径正确


