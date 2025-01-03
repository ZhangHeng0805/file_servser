var TYPE;
var Data = null;

function getAllFileType() {
    checkCoookie();
    $.ajax({
        url: ZH_URL.download_getFileType,
        method: "post",
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function (d) {
            var html = [];
            var html2 = [];
            for (var i = 0; i < d.length; i++) {
                if (d[i].code == 200) {
                    html.push('<option value="' + d[i].message + '">' + d[i].title + '</option>');
                    if (d[i].message != '$all$')
                        html2.push('<option>' + d[i].title + '</option>');
                } else {
                    alert("类型查询错误：" + d[i].message);
                    console.warn("类型查询错误:" + d[i])
                }
            }
            $("#type").html(html.join(''));
            $("#upload_path_list").html(html2.join(''));
            set_select_checked("type", TYPE);
        },
        error: function (e) {
            ajax_error(e);
        }
    });
    if (isData(Data)) {
        $("#query_file").show();
        $("#query_name").val('');
    } else {
        $("#query_file").hide();
    }
}

function isData(data) {
    return data != null && data.length > 0 && data[0].code === 200;
}

function sub2() {
    let query_name = $("#query_name").val();
    if (query_name != null && query_name.length > 0 && Data != null) {
        if (TYPE !== $("#type").val()) {
            set_select_checked("type", TYPE);
        }
        handle(fuzzyQuery(Data, query_name));
        return;
    }
    let key = $("#keys").val();
    if (key.length > 0) {
        if (getFileList_c % 5 === 0)
            captcha_model_show("getFileList('" + key + "')");
        else
            getFileList(key);
    } else {
        alert("请输入秘钥");
    }
}

var getFileList_c = 0;

function getFileList(key) {
    $("#btn_sub2").attr('disabled', true);
    checkCoookie();
    $.ajax({
        url: ZH_URL.download_getFileList,
        method: "post",
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        data: {
            key: zh_md5(getCookie('zhangheng0805_cid') + key + getCookie('zhangheng0805_sid')),
            type: $("#type").val(),
            code: $("#code").val()
        },
        success: function (d) {
            // console.log(d);
            d = Array.isArray(d) ? d : new Array(d);
            let isSuccess = isData(d);
            if (isSuccess || getFileList_c !== 0)
                getFileList_c++;
            if (isSuccess) {
                Data = d;
                TYPE = $("#type").val();
            } else {
                Data = null;
                if (TYPE !== $("#type").val()) ;
                set_select_checked("type", TYPE);
                console.warn(d);
                getFileList_c = 0;
            }
            //处理数据
            handle(d);
            //刷新下拉框数据
            getAllFileType();
            $("#btn_sub2").attr('disabled', false);

        },
        error: function (e) {
            $("#btn_sub2").attr('disabled', false);
            ajax_error(e);
            getFileList_c = 0;
        }
    })
}

function handle(d) {
    var html = [];
    var icon = "static/img/unknown.png";
    var title = "";
    let update_time = "";
    let type = "";
    let auth = "";
    var size = "";
    let isFile = true;
    let isDirectory = false;
    for (var i = 0; i < d.length; i++) {
        var msg = d[i];
        update_time = msg.obj == null ? "" : msg.obj.update_time;
        type = msg.obj == null ? "" : msg.obj.type;
        isFile = msg.obj == null ? true : msg.obj.isFile;
        isDirectory = msg.obj == null ? false : msg.obj.isDirectory;
        title = msg.title;
        if (msg.code === 200) {
            if (isFile) {
                switch (type) {
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
                        icon = "download/show/" + encodeURI(msg.message);
                        break;
                    case "application":
                        icon = "static/img/application.png";
                        break;
                    default:
                        icon = "static/img/unknown.png";
                        break;
                }
                html.push('<div class="panel panel-success">');
            } else {
                icon = "static/img/folder.png";
                html.push('<div class="panel panel-primary">');
            }
            title = msg.obj == null ? "" : msg.obj.name;
            size = msg.obj == null ? "" : getFileSizeFormat(msg.obj.size);
        } else if ((msg.code + '').startsWith('5')) {
            html.push('<div class="panel panel-danger">');
        } else {
            html.push('<div class="panel panel-warning">');
        }
        html.push('<div class="panel-heading">');
        html.push('<img lsrc="' + icon + '" src="" onerror="loadImg(this)" style="height: 40px;margin-right: 15px;box-shadow:0 0 10px black;">');
        html.push('<h3 class="panel-title" style="margin-right: 25px">' + title + '</h3>');
        html.push('<label class="label label-primary">' + size + '</label>');
        html.push('<code>' + update_time + '</code>');
        html.push('</div>');
        html.push('<div class="panel-body" style="background-color: #afd9ee">');
        // '<label class="control-label" for="type">文件路径：</label>' +
        html.push('<div class="form-group">');
        html.push('<input class="form-control file_path_' + i + '" type="text" readonly>');
        html.push('</div>');
        if (msg.code === 200) {
            html.push('<div class="form-group">');
            html.push('<div class="btn-group"><button data-toggle="dropdown" class="btn btn-search dropdown-toggle btn_' + i + '" ">复制链接<span class="caret"></span></button>');
            html.push('<ul role="menu" class="dropdown-menu">');
            html.push('<li><a id="c1_' + i + '" onclick="copeText(\'c1_' + i + '\');" title="普通的单个文件直接下载">普通下载地址</a></li>');
            html.push('<li><a id="c2_' + i + '" onclick="copeText(\'c2_' + i + '\');" title="断点分片快速下载，需要下载端支持">分片下载地址</a></li>');
            html.push('</ul></div>');
            html.push('<div class="btn-group"><a class="btn btn-primary" target="_blank" id="btn' + i + '_download">直接访问</a></div>');
            auth = msg.obj == null ? "" : msg.obj.auth;
            if (auth != null && auth === "Admin") {
                html.push('<div class="btn-group"><a class="btn btn-danger" target="_blank" onclick="del(\'' + msg.message + '\')">删除文件</a></div>');
                html.push('<div class="btn-group"><a class="btn btn-warning" target="_blank" onclick="rename(\'' + msg.message + '\',\'' + title + '\')">文件重命名</a></div>');
            }
        }
        html.push('</div></div></div>');
    }
    $("#files_result").html(html.join(""));
    $("#files_num").html('总计:' + d.length + '个文件/夹');
    initData(d);
}

function initData(d) {
    for (var i = 0; i < d.length; i++) {
        $(".file_path_" + i).val(d[i].message);
        let arr = d[i].message.split("/");
        for (let n = 0; n < arr.length; n++) {
            arr[n] = encodeURIComponent(arr[n]);
        }
        let path = arr.join("/");
        let url1 = window.location.href + "download/show/" + path;
        let url2 = window.location.href + "download/split/" + path;
        $("#c1_" + i).attr("data-clipboard-text", url1);
        $("#c2_" + i).attr("data-clipboard-text", url2);
        $("#btn" + i + "_download").attr("href", url1);
    }
}

var clip;

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


function del(path) {
    if (confirm("确定删除该文件吗？")) {
        if (del_file_c % 3 === 0)
            captcha_model_show("del_file('" + path + "')");
        else
            del_file(path);
    }
}

var del_file_c = 0;

function del_file(path) {
    checkCoookie();
    let key = $("#keys").val();
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
                if (d.code === 200) {
                    getFileList(key);
                } else {
                    del_file_c = 0;
                    console.warn(d);
                }
                if (d.code === 200 || del_file_c !== 0)
                    del_file_c++;
                alert(d.message);
            },
            error: function (e) {
                del_file_c = 0;
                ajax_error(e);
            }
        })
    } else {
        alert("请输入管理访问秘钥");
    }
}

function rename(path, oldName) {
    if (rename_file_c % 4 === 0)
        captcha_model_show("rename_file('" + path + "','" + oldName + "')");
    else
        rename_file(path, oldName);
}

var rename_file_c = 0;

function rename_file(path, oldName) {
    let key = $("#keys").val();
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
                            if (d.code === 200) {
                                getFileList(key);
                            } else {
                                rename_file_c = 0;
                                console.warn(d);
                            }
                            if (d.code === 200 || rename_file_c !== 0)
                                rename_file_c++;
                            alert(d.title + '\n' + d.message);
                        },
                        error: function (e) {
                            rename_file_c = 0;
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

/*模糊查询过滤*/
function fuzzyQuery(list, keyWord) {
    var arr = list.filter(function (item) {
        var reg = new RegExp(keyWord, "gi");
        return reg.test(item.title)
    });
    return arr
}

function scrollToTop() {
    // 滚动到指定位置
    $('html, body').animate({
        scrollTop: $("body").offset().top
    }, 1000);
}