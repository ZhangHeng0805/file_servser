function resetImgCode() {
    $("#code").val(null);
    $("#img-code").attr("src", window.location.href+"getVerify/math?t=" + new Date().getTime());
}
function sub1() {
    hideModel();
    var key = $("#key").val();
    var file = $("#file").val();
    // var code = $("#code").val();
    if (key.length > 0) {
        if (file.length > 0) {
                // checkCode(code)
                //resetImgCode();
                captcha_model_show("upload()");
        } else {
            alert("请选择文件");
        }
    } else {
        alert("请输入秘钥");
    }
    return false;
}

function checkCode(code) {
    $.ajax({
        url: window.location.href+ZH_URL.captcha_checking,
        type: "post",
        dataType: "json",
        data: {
            code: code,
            isClear: false
        },
        success: function (d) {
            if (d.code == 200) {
                $("#wait_icon").show();
                $("#btn_upload").attr('disabled', true);
                $("#btn_upload").val('上传中...');
                upload();
            } else {
                showModel(d);
                resetImgCode();
                console.warn(d);
                return false;
            }
        },
        error: function (e) {
            ajax_error(e);
            return false;
        }
    });
}

function upload() {
    $("#wait_icon").show();
    $("#btn_upload").attr('disabled', true);
    $("#btn_upload").val('上传中...');
    checkCoookie();
    var form = new FormData(document.getElementById("upload_form"));
    var code = $("#code").val();
    let key=zh_md5(getCookie('zhangheng0805_cid')+ $("#key").val() +getCookie('zhangheng0805_sid'));
    form.set("key",key);
    form.set("code",code);
    // console.log("upload-form",form.get("file"));
    let time=new Date().getTime();
    let size=form.get('file').size;
    let name=code+'zh0805'+form.get('fileName').length;
    var s = time+key+size+name;
    // console.log(s)
    $.ajax({
        url: ZH_URL.upload_file,
        type: "post",
        dataType: "json",
        headers:{
            'x-t':time,
            'x-size':size,
            'x-signature':zh_md5(s),
        },
        xhrFields: {
            withCredentials: true
        },
        data: form,
        processData: false,
        contentType: false,
        xhr: function () { //用以显示上传进度
            var xhr = $.ajaxSettings.xhr();
            if (xhr.upload) {
                xhr.upload.addEventListener('progress', function (event) {
                    var percent = Math.floor(event.loaded / event.total * 100);
                    let pro = percent - 1;
                    pro = pro > 0 ? pro : 0;
                    $("#btn_upload").val(pro + "%" + '上传中...');
                }, false);
            }
            return xhr
        },
        success: function (d) {
            showModel(d);
            if (d.code == 200) {
                d.time=d.time?d.time:d.timestamp;
                alert("上传成功！耗时:"+((Math.abs(Date.parse(new Date(d.time))-time))/1000).toFixed(2)+"秒")
                upload_reset();
            } else {
                console.warn(d);
            }
            $("#btn_upload").attr("disabled", false);
            $("#btn_upload").val('上传');
            $("#wait_icon").hide();
        },
        error: function (e) {
            $("#btn_upload").attr("disabled", false);
            $("#btn_upload").val('上传');
            $("#wait_icon").hide();

            ajax_error(e);
        }
    })
}

function ajax_error(e) {
    if (e.readyState===4){
        if (e.responseJSON!=null) {
            e.responseJSON.title=e.responseJSON.title?e.responseJSON.title:e.responseJSON.error;
            alert(e.responseJSON.title+'\n'+e.responseJSON.message);
            showModel(e.responseJSON);
        }else if (e.responseText!=null) {
            alert("请求错误："+e.responseText);
        }
    }else{
        alert("请求失败！")
    }
    console.error(e);
}

function showModel(d) {
    d.code=d.code?d.code:d.status;
    d.title=d.title?d.title:d.error;
    d.time=d.time?d.time:d.timestamp;
    var html = "";
    if ((d.code + '').startsWith('2')) {
        html += '<div  class="alert alert-success fade in">';
    } else if ((d.code + '').startsWith('5')) {
        html += '<div  class="alert alert-danger fade in">';
    } else if ((d.code + '').startsWith('4')) {
        html += '<div  class="alert alert-warning fade in">';
    } else if ((d.code + '').startsWith('1')) {
        html += '<div  class="alert alert-info fade in">';
    }
    html += '<button type="button" class="close close-sm" data-dismiss="alert">' +
        '      <i class="fa fa-times"></i>' +
        '  </button>' +
        '<h4><strong>' + d.title + '</strong></h4>' +
        '<div class="text-right">'+d.time+'</div>';
    if (d.code == 200) {
        let arr = d.message.split("/");
        for (let n = 0; n < arr.length; n++) {
            arr[n] = encodeURIComponent(arr[n]);
        }
        let path = arr.join("/");
        html += '<label><a target="_blank" href="download/show/' + path + '">' + d.message + '</a></label>';
    } else {
        html += '<label>' + d.message + '</label>';
    }
    html += '</div>';
    $("#upload_alert").html(html);
}

function hideModel() {
    $("#upload_alert").html('');
}

function upload_reset(state) {
    if (!$("#remember").is(':checked')) {
        $("#key").val(null);
    }
    if (state != 1)
        $("#file").val(null);

    $("#fileName").val(null);
    $("#fileSize").text('');
    $("#path").val(null);
    // resetImgCode();
    document.getElementById('fileName_tips').style.display='none';
    document.getElementById('filePath_tips').style.display='none';
}

function getFileSizeFormat(size) {
    if (size >= 0 && size < (1024 * 1000)) {
        return (size / 1024).toFixed(2) + "Kb";
    } else if (size >= (1024 * 1000) && size < (1024 * 1024 * 1000)) {
        return (size / (1024 * 1024)).toFixed(2) + "Mb"
    } else if (size >= (1024 * 1024 * 1000)) {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + "Gb"
    } else {
        return size + "B";
    }
}