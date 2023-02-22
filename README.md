# [服务器](https://github.com/ZhangHeng0805/file_servser/releases/download/2.0/default.zip)下载
# 配置文件
* 在服务器jar包同级目录下新建一个application.properties配置文件，将下面配置粘贴进去，修改配置文件后需要重启服务器
```properties
# 应用服务 WEB 访问端口
server.port=8080
        
# 文件名前缀名(中文可能会乱码)
appName=XXXR



# 文件存放位置(注意末尾加/，中文可能会乱码)
baseDir=files/

# 临时访问秘钥[浏览](多个秘钥用逗号隔开，注意不要换行)
test_keys=123456

# 普通访问秘钥[上传，浏览](多个秘钥用逗号隔开，注意不要换行)
keys=666666,888888

# 管理员访问秘钥[上传，浏览，删除](多个秘钥用逗号隔开，注意不要换行)
admin_keys=admin123456
```

# 一、文件上传（单个文件最大100Mb）

## 1.保存MultipartFile类型图片
* 访问路径：```localhost:8080/upload/saveMulImg```
* 请求方式：get/post
* 请求参数：


|  参数   |     类型      |                           说明                           |
| :-----: | :-----------: | :------------------------------------------------------: |
|   key   |    String     |                     访问秘钥（必填）                     |
|  image  | MultipartFile |     图片文件（必填）: 大小2Mb以内的主流图片格式文件      |
| imgName |    String     | 图片名称（选填，默认为上传文件名的前8个字符）：最长8字符 |
|  path   |    String     |       保存文件夹名（选填，默认为image）：例：image       |

* 返回参数：

|  参数   |  类型  |                      说明                       |
| :-----: | :----: | :---------------------------------------------: |
|  time   | String |                     时间戳                      |
|  code   |  int   | 状态码(200:成功; 100:提示; 404:警告; 500:错误） |
|  title  | String |                    消息标题                     |
| message | String |     消息内容（如果成功则返回文件保存路径）      |
|   obj   | Object |           附加消息内容（默认为null）            |

## 2.保存Base64类型图片

* 访问路径：```localhost:8080/upload/saveBase64Img```
* 请求方式：get/post
* 请求参数：


|  参数   |  类型  |                        说明                         |
| :-----: | :----: | :-------------------------------------------------: |
|   key   | String |                  访问秘钥（必填）                   |
|  image  | String | 图片base64数据（必填）: 若无前缀，则文件格式位为jpg |
| imgName | String |     图片名称（选填，默认为随机名称）：最长8字符     |
|  path   | String |    保存文件夹名（选填，默认为image）：例：image     |

* 返回参数：

|  参数   |  类型  |                      说明                       |
| :-----: | :----: | :---------------------------------------------: |
|  time   | String |                     时间戳                      |
|  code   |  int   | 状态码(200:成功; 100:提示; 404:警告; 500:错误） |
|  title  | String |                    消息标题                     |
| message | String |     消息内容（如果成功则返回文件保存路径）      |
|   obj   | Object |           附加消息内容（默认为null）            |

  ## 3.保存MultipartFile类型文件

* 访问路径：```localhost:8080/upload/saveMulFile```
* 请求方式：get/post
* 请求参数：

|  参数   |     类型      |                           说明                           |
| :-----: | :-----------: | :------------------------------------------------------: |
|   key   |    String     |                     访问秘钥（必填）                     |
|  image  | MultipartFile |                     文件（必填）                     |
| imgName |    String     | 文件名称（选填）：最长8字符，默认为上传文件名的前8个字符 |
|  path   |    String     |             保存文件夹名（选填）：例：image              |
* 返回参数：

|  参数   |  类型  |                             说明                             |
| :-----: | :----: | :----------------------------------------------------------: |
|  time   | String |                            时间戳                            |
|  code   |  int   |       状态码(200:成功; 100:提示; 404:警告; 500:错误）        |
|  title  | String |                           消息标题                           |
| message | String | 消息内容（如果成功则返回文件保存路径，例text/星曦向荣网48122_张恒.pdf） |
|   obj   | Object |                  附加消息内容（默认为null）                  |

# 二、文件下载
### 普通下载
* 访问路径：```http://localhost:8080/download/show/+文件保存路径```
* 请求方式：get/post

> 例： ```http://localhost:8080/download/show/text/星曦向荣网48122_张恒.pdf ```
### 断点分片下载
* 访问路径：```http://localhost:8080/download/split/+文件保存路径```
* 请求方式：get/post
> * 例： ```http://localhost:8080/download/split/text/星曦向荣网48122_张恒.pdf ```
> * 注意：此下载需要下载客户端支持断点分片下载功能

> 文件下载不需要请求数据，只需下载路径正确

# 三、文件删除

* 访问路径：```localhost:8080/upload/deleteFile```

* 请求方式：get/post

* 请求参数：

| 参数 |  类型  |       说明       |
| :--: | :----: | :--------------: |
| key  | String | 访问秘钥（必填） |
| path | String |  删除的文件路径  |

* 返回参数：

|  参数   |  类型  |                      说明                       |
| :-----: | :----: | :---------------------------------------------: |
|  time   | String |                     时间戳                      |
|  code   |  int   | 状态码(200:成功; 100:提示; 404:警告; 500:错误） |
|  title  | String |                    消息标题                     |
| message | String |                    消息内容                     |
|   obj   | Object |           附加消息内容（默认为null）            |

