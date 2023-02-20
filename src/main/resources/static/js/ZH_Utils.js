/**
 * 设置下拉框的选择
 * @param selectId 选择框的id
 * @param checkValue 选择的value值
 */
function set_select_checked(selectId, checkValue) {
    var select = document.getElementById(selectId);
    for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].value == checkValue) {
            select.options[i].selected = true;
            break;
        }
    }
}

/**
 * 设置按钮点击延时
 * @param btnId 按钮的id
 * @param time 延时的时间，单位ms
 */
function set_btn_delayed(btnId, time) {
    var btn = document.getElementById(btnId);
    btn.disabled = 'disabled';//只要点击就将按钮的可点击的状态更改为不可以点击的状态
    setTimeout(function () {//设置时间，多久可以改变状态为可以点击
        btn.disabled = '';
    }, time);//time毫秒内不可以重复点击，1000毫秒等于一秒
}

/**
 * 获取时间戳
 * @returns {*}
 */
function getTime() {
    var date = new Date();
    var year = date.getFullYear();    //获取当前年份
    var mon = date.getMonth() + 1;      //获取当前月份   js从0开始取
    var da = date.getDate();          //获取当前日
    var h = date.getHours();          //获取小时
    var m = date.getMinutes();        //获取分钟
    var s = date.getSeconds();        //获取秒
    monthText = mon < 10 ? "0" + mon : mon;
    daText = da < 10 ? "0" + da : da;
    hoursText = h < 10 ? "0" + h : h;
    minutesText = m < 10 ? "0" + m : m;
    secondText = s < 10 ? "0" + s : s;
    return year + monthText + daText + hoursText + minutesText + secondText;
}

/**
 * input输入框字数限制提示，配合oninput()事件使用
 * @param inputId input输入框的id
 * @param tipsId 提示文字的id
 * @param max 最大字数
 */
function titleInput(inputId, tipsId, max) {
    var input = document.getElementById(inputId);
    var i_length = input.value.length;
    var tips = document.getElementById(tipsId);
    if (i_length >= max) {
        // $("#"+input).parent().removeClass("has-success").addClass("has-error");
        tips.style.color = "#f48f0d";
        tips.innerText = "输入内容已达到上限" + max + "字";
        input.style.borderColor = "#f48f0d";
    } else {
        // $("#"+input).parent().removeClass("has-error").addClass("has-success");
        tips.style.color = "#4e4f6b";
        tips.innerText = "字数限制" + max + "字,已输入：" + i_length + "字";
        input.style.borderColor = "green";
    }
}

/**
 * input输入框字数限制，配合onchang()事件使用
 * @param inputId input输入框的id
 * @param max 最大字数
 */
function limitInput(inputId, max) {
    var input = document.getElementById(inputId);
    var i_length = input.value.length;
    if (i_length > max) {
        input.value = input.value.substring(0, max);
        input.style.borderColor = "red";
        alert("提示：输入内容长度超过限制" + max + "字");
    } else {
        input.style.borderColor = "green";
    }

//    文件大小格式转换
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

}

