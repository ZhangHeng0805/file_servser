//设置下拉框的选择
function set_select_checked(selectId, checkValue){
    var select = document.getElementById(selectId);
    for (var i = 0; i < select.options.length; i++){
        if (select.options[i].value == checkValue){
            select.options[i].selected = true;
            break;
        }
    }
}
//设置按钮点击延时

function set_btn_delayed(btnId,time) {
    var btn = document.getElementById(btnId);
    btn.disabled = 'disabled';//只要点击就将按钮的可点击的状态更改为不可以点击的状态
    setTimeout(function(){//设置时间，多久可以改变状态为可以点击
        btn.disabled = '';
    },time);//time毫秒内不可以重复点击，1000毫秒等于一秒
}