var TYPE;
var Data = null;

function getAllFileType() {
    checkCoookie();
    $.ajax({
        url: window.location.href + "download/getAllFileType",
        method: "post",
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function (d) {
            var html = "";
            for (var i = 0; i < d.length; i++) {
                if (d[i].code == 200) {
                    html += '<option value="' + d[i].message + '">' + d[i].title + '</option>';
                } else {
                    alert("类型查询错误：" + d[i].message);
                    console.warn("类型查询错误:" + d[i])
                }
            }
            $("#type").html(html);
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
    return data != null && data.length > 0 && data[0].code == 200;
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
        $("#btn_sub2").attr('disabled', true);
        checkCoookie();
        $.ajax({
            url: window.location.href + "download/findFileList",
            method: "post",
            dataType: "json",
            xhrFields: {
                withCredentials: true
            },
            data: {
                key: zh_md5(getCookie('zhangheng0805_cid') + key + getCookie('zhangheng0805_sid')),
                type: $("#type").val()
            },
            success: function (d) {
                // console.log(d);
                d = Array.isArray(d) ? d : new Array(d);
                if (isData(d)) {
                    Data = d;
                    TYPE = $("#type").val();
                } else {
                    Data = null;
                    if (TYPE !== $("#type").val()) ;
                    set_select_checked("type", TYPE);
                    console.warn(d)
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
            }
        })
    } else {
        alert("请输入秘钥")
    }

}

function handle(d) {
    var html = "";
    var icon = "static/img/unknown.png";
    var title = "";
    let update_time = "";
    let type = "";
    let auth = "";
    var size = "";
    for (var i = 0; i < d.length; i++) {
        var msg = d[i];
        update_time = msg.obj == null ? "" : msg.obj.update_time;
        type = msg.obj == null ? "" : msg.obj.type;
        title = msg.title;
        if (msg.code == 200) {
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
            title = msg.obj == null ? "" : msg.obj.name;
            size = msg.obj == null ? "" : getFileSizeFormat(msg.obj.size);
            html += '<div class="panel panel-success">';
        } else if ((msg.code + '').startsWith('5')) {
            html += '<div class="panel panel-danger">';
        } else {
            html += '<div class="panel panel-warning">';
        }
        html += '<div class="panel-heading">' +
            '<img lsrc="' + icon + '" src="" onerror="loadImg(this)" style="height: 40px;margin-right: 15px;box-shadow:0 0 10px black;">' +
            '<label style="margin-right: 25px">' + title + '</label>' +
            '<label class="label label-primary">' + size + '</label>' +
            '<code>' + update_time + '</code>' +
            '</div>' +
            '<div class="panel-body" style="background-color: #afd9ee">' +
            // '<label class="control-label" for="type">文件路径：</label>' +
            '    <div class="form-group">' +
            '       <input class="form-control file_path_' + i + '" type="text" readonly>' +
            '     </div>';
        if (msg.code == 200) {
            html += '<div class="form-group">' +
                '<div class="btn-group"><button data-toggle="dropdown" class="btn btn-search dropdown-toggle btn_' + i + '" ">复制链接<span class="caret"></span></button>' +
                '<ul role="menu" class="dropdown-menu">' +
                '<li><a id="c1_' + i + '" onclick="copeText(\'c1_' + i + '\');" title="普通的单个文件直接下载">普通下载地址</a></li>' +
                '<li><a id="c2_' + i + '" onclick="copeText(\'c2_' + i + '\');" title="断点分片快速下载，需要下载端支持">分片下载地址</a></li>' +
                '</ul></div>';
            html += '<div class="btn-group"><a class="btn btn-primary" target="_blank" id="btn' + i + '_download">直接访问</a></div>';
            auth = msg.obj == null ? "" : msg.obj.auth;
            if (auth != null && auth.length > 0 && auth == "admin") {
                html += '<div class="btn-group"><a class="btn btn-danger" target="_blank" onclick="del(\'' + msg.message + '\')">删除文件</a></div>';
                html += '<div class="btn-group"><a class="btn btn-warning" target="_blank" onclick="rename(\'' + msg.message + '\',\'' + title + '\')">文件重命名</a></div>';
            }
        }
        html += '</div></div></div>';
    }
    $("#files_result").html(html);
    $("#files_num").html('总计:' + d.length + '个文件');
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

function checkCoookie() {
    hideModel();
    if (getCookie('zhangheng0805_cid').length <= 0)
        setCookie('zhangheng0805_cid', window.localStorage.getItem('zhangheng0805_cid'));
    if (getCookie('zhangheng0805_sid').length <= 0)
        setCookie('zhangheng0805_sid', window.localStorage.getItem('zhangheng0805_sid'));
}

function del(path) {
    checkCoookie();
    if (confirm("确定删除该文件吗？")) {
        let key = $("#keys").val();
        if (key.length > 0) {
            $.ajax({
                url: window.location.href + 'deleteFile',
                method: "post",
                dataType: "json",
                xhrFields: {
                    withCredentials: true
                },
                data: {
                    key: zh_md5(getCookie('zhangheng0805_cid') + key + getCookie('zhangheng0805_sid')),
                    path: path
                },
                success: function (d) {
                    if (d.code == 200) {
                        sub2();
                    } else {
                        console.warn(d);
                    }
                    alert(d.message);
                },
                error: function (e) {
                    ajax_error(e);
                }
            })
        } else {
            alert("请输入管理访问秘钥");
        }
    }
}

function rename(path, oldName) {
    let key = $("#keys").val();
    if (key.length > 0) {
        let renameFile = prompt("文件重命名，请输入新的文件名:", oldName);
        if (renameFile) {
            if (renameFile != null && renameFile.length > 0) {
                if (renameFile != oldName) {
                    $.ajax({
                        url: window.location.href + 'renameFile',
                        method: "post",
                        dataType: "json",
                        xhrFields: {
                            withCredentials: true
                        },
                        data: {
                            key: zh_md5(getCookie('zhangheng0805_cid') + key + getCookie('zhangheng0805_sid')),
                            path: path,
                            newName: renameFile,
                        },
                        success: function (d) {
                            if (d.code == 200) {
                                sub2();
                            } else {
                                console.warn(d);
                            }
                            alert(d.title+'\n'+d.message);
                        },
                        error: function (e) {
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