let Result = null;
let Path = null;
let Code = null;

function fileShowSubmit(ownDom, keyDom, pathDom, searchDom) {
    ownDom.setAttribute('disabled', true);
    let key = keyDom.val();
    let path = pathDom.val();
    let search_name = searchDom.val();
    try {
        if (search_name && Path === path) {
            console.log("模糊查询:", path, search_name);
            buildHtml(fuzzyQuery(Result, search_name));
            return;
        }
        console.log("精确查询:", path);
        if (!Code || Math.random() > 0.7) {
            Code = null;
            captcha_model_show("requestFileList('" + key + "','" + path + "')");
        } else {
            requestFileList(key, path);
        }
    } finally {
        // console.log("查询结果:", Result);
        Path = path;
        ownDom.removeAttribute('disabled');
    }

}

function requestFileList(key, path) {
    if (!Code) {
        Code = $("#code").val();
    }
    checkCookie();
    $.ajax({
        url: ZH_URL.getFileList,
        method: "post",
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        data: {
            key: zh_md5(getCookie('zhangheng0805_cid') + key + getCookie('zhangheng0805_sid')),
            code: Code,
            path: path
        },
        success: function (res) {
            console.log('成功', res);
            if (res.success) {
                Result = res.obj;
                buildHtml(Result);
            } else {
                alert(res.message);
            }
            $("#files_num").html(res.message);
        },
        error: function (e) {
            console.log('错误', e);
            ajax_error(e);
        }
    });
    if (path!=='/'){
        document.getElementById("btn_fileListBack").removeAttribute("style");
    }else {
        document.getElementById("btn_fileListBack").setAttribute("style","display:none");
    }
}


function buildHtml(data) {
    let html = [];
    for (let i = 0; i < data.length; i++) {
        let file = data[i];
        let icon = getFileIcon(file);
        if (file.isFile) {
            html.push('<div class="panel panel-success" style="margin-top: 8px">');
        } else {
            html.push('<div class="panel panel-warning" style="margin-top: 8px">');
        }
        html.push('<div class="panel-heading" style="text-transform: none">');
        if (icon.startsWith("static/")) {
            html.push('<img lsrc="' + icon + '" src="" onerror="loadImg(this)" style="height: 40px;margin-right: 15px;">');
        } else {
            html.push('<img src="' + icon + '" style="height: 40px;margin-right: 15px;">');
        }
        html.push('<label class="panel-title" style="margin-right: 25px">' + file.name + '</label>');
        if (file.isFile) {
            html.push('<label class="label label-primary">' + fileSize(file.size) + '</label>');
        }
        html.push('<code>' + file.update_time + '</code>');
        html.push('</div>');
        let bgColor = file.isFile ? '#afd9ee' : '#eee6af'
        html.push('<div class="panel-body" style="background-color: ' + bgColor + '">');
        html.push('<div class="form-group">');
        let folder = file.isFile ? "文件" : "文件夹";
        html.push("<p>" + folder + "路径: " + file.path + "</p>")
        html.push('</div">');
        html.push('<div class="form-group">');
        if (file.isFile) {
            html.push('<div class="btn-group"><button data-toggle="dropdown" class="btn btn-search dropdown-toggle btn_' + i + '" ">复制链接<span class="caret"></span></button>');
            html.push('<ul role="menu" class="dropdown-menu">');
            html.push('<li><a id="c1_' + i + '" onclick="copeText(\'c1_' + i + '\');" title="普通的单个文件直接下载">普通下载地址</a></li>');
            html.push('<li><a id="c2_' + i + '" onclick="copeText(\'c2_' + i + '\');" title="断点分片快速下载，需要下载端支持">分片下载地址</a></li>');
            html.push('</ul></div>');
            html.push('<div class="btn-group"><a class="btn btn-primary" target="_blank" id="btn' + i + '_download">直接访问</a></div>');
            if (file.auth === "Admin") {
                html.push('<div class="btn-group"><a class="btn btn-danger" target="_blank" onclick="del(\'' + file.path + '\')">删除文件</a></div>');
                html.push('<div class="btn-group"><a class="btn btn-warning" target="_blank" onclick="rename(\'' + file.path + '\',\'' + file.name + '\')">文件重命名</a></div>');
            }
        } else {
            html.push('<div class="btn-group"><a class="btn btn-primary" target="_blank" onclick="enterFolder(\'' + file.path + '\')">进入文件夹</a></div>');
        }

        html.push("</div></div></div>");
    }
    $("#files_result").html(html.join(""));
    buildHtmlAttr(data);
}

function buildHtmlAttr(data) {
    for (let i = 0; i < data.length; i++) {
        let file = data[i];
        if (file.isFile) {
            let pathArr = file.path.split("/");
            for (let n = 0; n < pathArr.length; n++) {
                pathArr[n] = encodeURIComponent(pathArr[n]);
            }
            let path = pathArr.join("/");
            let url1 = window.location.href + "download/show/" + path;
            let url2 = window.location.href + "download/split/" + path;
            $("#c1_" + i).attr("data-clipboard-text", url1);
            $("#c2_" + i).attr("data-clipboard-text", url2);
            $("#btn" + i + "_download").attr("href", url1);
        }
    }
}

function getFileIcon(file) {
    let icon = "static/img/unknown.png";
    if (file.isFile) {
        switch (file.type) {
            case "text":
                icon = "static/img/text.png";
                break;
            case "video":
                icon = "static/img/video.png";
                break;
            case "audio":
                icon = "static/img/audio.png";
                break;
            case "image":
                icon = "download/show/" + encodeURI(file.path);
                break;
            case "application":
                icon = "static/img/application.png";
                break;
            default:
                icon = "static/img/unknown.png";
                break;
        }

    } else {
        icon = "static/img/folder.png";
    }
    return icon;
}

function enterFolder(path) {
    $("#fls_path").val(path);
    let key = $("#fls_keys").val();
    if (!Code || Math.random() > 0.9) {
        Code = null;
        captcha_model_show("requestFileList('" + key + "','" + path + "')");
    } else {
        requestFileList(key, path);
    }
}

function del(path) {
    if (confirm("确定删除该文件吗？")) {
        if (!Code || Math.random() > 0.7) {
            captcha_model_show("del_file('" + path + "')");
        } else {
            del_file(path);
        }
    }
}

function del_file(path) {
    let key = $("#fls_keys").val();
    let code = $("#code").val();
    if (key.length > 0) {
        $.ajax({
            url: ZH_URL.delete_file,
            method: "post",
            dataType: "json",
            xhrFields: {
                withCredentials: true
            },
            data: {
                key: zh_md5(getCookie('zhangheng0805_cid') + key + getCookie('zhangheng0805_sid')),
                path: path,
                code: code,
            },
            success: function (d) {
                if (d.success) {
                    var lastIndexOf = path.lastIndexOf('/');
                    let p = lastIndexOf > 0 ? path.substring(0, lastIndexOf) : path;
                    requestFileList(key, p);
                }
                alert(d.message);
            },
            error: function (e) {
                console.error("文件删除", e)
                ajax_error(e);
            }
        })
    } else {
        alert("请输入管理访问秘钥");
    }
}

function rename(path, oldName) {
    if (!Code || Math.random() > 0.7) {
        captcha_model_show("rename_file('" + path + "','" + oldName + "')");
    } else {
        rename_file(path, oldName);
    }
}

function rename_file(path, oldName) {
    let key = $("#fls_keys").val();
    let code = $("#code").val();
    if (key.length > 0) {
        let renameFile = prompt("文件重命名，请输入新的文件名:", oldName);
        if (renameFile) {
            if (renameFile.length > 0) {
                if (renameFile !== oldName) {
                    $.ajax({
                        url: ZH_URL.rename_file,
                        method: "post",
                        dataType: "json",
                        xhrFields: {
                            withCredentials: true
                        },
                        data: {
                            key: zh_md5(getCookie('zhangheng0805_cid') + key + getCookie('zhangheng0805_sid')),
                            path: path,
                            code: code,
                            newName: renameFile,
                        },
                        success: function (d) {
                            if (d.success) {
                                var lastIndexOf = path.lastIndexOf('/');
                                let p = lastIndexOf > 0 ? path.substring(0, lastIndexOf) : path;
                                requestFileList(key, p);
                            }
                            alert(d.title + '\n' + d.message);
                        },
                        error: function (e) {
                            console.error("文件重命名", e)
                            ajax_error(e);
                        }
                    })
                } else {
                    alert("新旧文件名不能相同！");
                }
            } else {
                alert("文件重命名不能为空");
            }
        }
    } else {
        alert("请输入访问秘钥！");
    }
}

function checkCookie() {
    hideModel();
    if (getCookie('zhangheng0805_cid').length <= 0)
        setCookie('zhangheng0805_cid', window.localStorage.getItem('zhangheng0805_cid'));
    if (getCookie('zhangheng0805_sid').length <= 0)
        setCookie('zhangheng0805_sid', window.localStorage.getItem('zhangheng0805_sid'));
}

/*模糊查询过滤*/
function fuzzyQuery(list, keyWord) {
    if (list) {
        var arr = list.filter(function (item) {
            var reg = new RegExp(keyWord, "gi");
            return reg.test(item.title)
        });
        return arr
    } else {
        return list;
    }
}

/**
 * 滚动到指定位置
 */
function scrollToTop() {
    // 滚动到指定位置
    $('html, body').animate({
        scrollTop: $("body").offset().top
    }, 1000);
}

function fileSize(size) {
    if (size == null) {
        return "0";
    } else {
        let length = size;
        let unit = ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"];
        let i;
        for (i = 0; !(length < 1024.0); ++i) {
            length /= 1024.0;
        }
        return i === 0 ? length + " " + unit[i] : length.toFixed(2) + " " + unit[i];
    }
}

function copeText(id) {
    // set_btn_delayed("btn_"+i,4000);
    clip = new Clipboard("#" + id);
    // console.log(clipboard);
    clip.on('success', function (e) {
        e.clearSelection();
        // console.info('Action:', e.action);
        console.info('复制Text:', e.text);
        // console.info('Trigger:', e.trigger);
        alert('复制成功');
        clip.destroy();
    });
    clip.on('error', function (e) {
        alert('复制失败');
        console.error('Action:', e.action);
        console.error('Trigger:', e.trigger);
    });

}