//版本控制
var script = document.getElementsByTagName("script");
for (var i = 0; i < script.length; i++) {
    if (script[i].getAttribute("version")) {
        if (script[i].getAttribute("version") !== localStorage["version"]) {
            localStorage.clear();
            localStorage["version"] = script[i].getAttribute("version");
        }
    }
}

//js
function loadJs(jsUrl) {
    if (!localStorage[jsUrl]) {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", jsUrl, false);
        xmlhttp.send();
        localStorage[jsUrl] = xmlhttp.responseText;
    }
    return localStorage[jsUrl]
}

//img
function loadImg(img) {
    if (img.getAttribute("lsrc")) {
        if (!localStorage[img.getAttribute("lsrc")]) {
            var x = new XMLHttpRequest();
            x.responseType = "blob";
            x.onload = function (e) {
                if (this.status == 200) {
                    var reader = new FileReader();
                    reader.readAsDataURL(this.response);
                    reader.onload = function () {
                        try {
                            localStorage[img.getAttribute("lsrc")] = this.result;
                            img.src = this.result;
                        }catch (e) {
                            img.src="static/img/img.png"
                        }
                    }
                }
            }
            x.open("get", img.getAttribute("lsrc"), true);
            x.send();
        } else {
            let str64=localStorage[img.getAttribute("lsrc")];
            // console.log(str64);
            img.src = str64;
        }
    }
}

//CSS
function loadCss(url) {
    if (!localStorage[url]) {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", url, false);
        xmlhttp.send();
        localStorage[url] = xmlhttp.responseText;
    }
    var s = document.createElement("style");
    s.innerHTML = localStorage[url];
    document.getElementsByTagName("head")[0].appendChild(s)
}