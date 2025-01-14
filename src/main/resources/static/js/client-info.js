// (function() {
var ua = window.navigator.userAgent,
    e = ua.toLowerCase(),
    isPc = /(WindowsNT)|(Windows NT)|(Macintosh)/i.test(ua),
    isMobile = /mobile/.test(e) || isOperaMini,
    isWindows = !isMobile && /windows/.test(e),
    isWebOS = /web0s|webos/.test(e),
    isLinux = !isMobile && /linux/.test(e),
    isMacOS = !isMobile && /macintosh/.test(e),
    isChromeOS = !isMobile && /CrOS/.test(e),
    isiOS = isMobile && /iphone|ipad|ipod/.test(e),
    isAndroid = /android/.test(e),

    osName = isWindows ? "Windows" : isLinux ? "Linux" : isMacOS ? "MacOS" : isChromeOS ? "Chrome OS" : isAndroid ? "Android" : isiOS ? "iOS" : isWebOS ? "WebOS" : "",


    isEdg = /\sedg\//.test(e),
    isEdge = /\sedge\//.test(e),
    isChrome = !isEdge && /chrome|crios/.test(e),
    isSafari = !isEdge && !isChrome && /safari/.test(e),
    isFirefox = !isEdge && !isChrome && !isSafari && /firefox/.test(e),
    isOpera = !isEdge && !isChrome && !isSafari && /opera|opr/.test(e),
    isOperaMini = !isEdge && !isChrome && !isSafari && /opera\s*mini/.test(e),
    isIE = !isEdge && !isChrome && !isSafari && !isFirefox && /trident|msie/.test(e),

    browserName = isEdg ? "Edg" : isEdge ? "Edge" : isIE ? "IE" : isChrome ? "Chrome" : isSafari ? "Safari" : isFirefox ? "Firefox" : isOpera ? "Opera" : isOperaMini ? "Opera Mini" : "",


    inWindowWechat = /WindowsWechat/i.test(ua),
    inMacWechat = /wechat.*mac os/i.test(ua),
    inWechat = /MicroMessenger/.test(ua),
    inAli = /AlipayClient/i.test(ua),
    inWeibo = /weibo/i.test(e),
    inQQ = /QQ\//.test(ua),
    inUC = /UC/.test(ua),
    inBaidu = / baiduboxapp\//i.test(e),
    inWxMiniProgram = isAndroid && /miniprogram/.test(e) || "miniprogram" == window.__wxjs_environment,

    appName = inAli ? "Alipay" : inWxMiniProgram ? "WxMini" : inWindowWechat ? "WinWechat" : inMacWechat ? "MacWechat" : inWechat ? "Wechat" : inQQ ? "QQ" : inWeibo ? "Weibo" : inUC ? "UC" : inBaidu ? "Baidu" : "",


    xiaomi = /\(.*Android.*(MI|Mi|Redmi).*\)/.test(ua),
    samsung = /\(.*Android.*(SAMSUNG|SM-).*\)/.test(ua),
    huawei = /\(.*Android.*(HUAWEI|HONOR).*\)/i.test(ua),
    oppo = /\(.*Android.*OPPO.*\)/.test(ua),
    vivo = /\(.*Android.*(vivo|VIVO).*\)/.test(ua),
    oneplus = /oneplus/i.test(e),
    meizu = /meizu/i.test(e),
    sony = /sony/i.test(e),

    brandName = isiOS ? "苹果" : huawei ? "华为" : xiaomi ? "小米" : oppo ? "OPPO" : vivo ? "vivo" : samsung ? "三星" : oneplus ? "一加" : meizu ? "魅族" : sony ? "索尼" : isPc ? "PC端" : "";


const getOS = () => {
    var l = ua.match(/(android)\s([\d\.]+)/i),
        b = ua.match(/(ipod).*\s([\d_]+)/i),
        p = ua.match(/(ipad).*\s([\d_]+)/i),
        x = ua.match(/(iphone)\sos\s([\d_]+)/i),
        t = ua.match(/Mac\sOS\sX\s(\d+[\.|_]\d+)/),
        n = ua.match(/Windows(\s+\w+)?\s+?(\d+\.\d+)/),
        a = ua.match(/Linux\s/);


    var os = '',
        v = '';
    l && (os = 'Android', v = l[2]),
    b && (os = 'iPod', v = b[2].replace(/_/g, ".")),
    p && (os = 'iPad', v = p[2].replace(/_/g, ".")),
    x && (os = 'iPhone', v = x[2].replace(/_/g, ".")),
    n && (os = 'Windows', v = n[2] == '10.0' ? '10' : n[2] == '6.3' ? '8.1' : n[2] == '6.2' ? '8' : n[2] == '6.1' ? '7' : n[2]),
    t && (os = 'Mac', v = t[1].split(/_|\./).join('.')),
    a && (os = 'Linux', v = '');
    return {os: os, version: v};

}

const getBrowser = () => {
    var v1 = e.match(/(?:edg|edge).(\d+)/);
    var v2 = e.match(/(?:chrome|crios|version|firefox|msie|rv).(\d+)\.(\d+)/);
    var v3 = e.match(/(?:applewebkit|gecko|trident).(\d+)/);
    var v = v1 ? parseInt(v1[1], 10) : v2 ? parseInt(v2[1], 10) : v3 ? parseInt(v3[1], 10) : '';
    return {browser: browserName, version: v};
}


const getSpider = () => {
    var isRobot = ua.match(/baiduspider|bingbot|googlebot|sosospider|360spider|yisouspider|sogou|msn|yahoo|bot|robot|spider|crawler|crawling|curl/i);
    return isRobot ? isRobot[0] : '';
}


const getReferrer = () => {
    var rurl = document.referrer || '',
        se = '',
        kw = '';
    if (rurl) {
        var p = parseURL(rurl);
        if (p.hostname != location.hostname) {
            var s = p.hostname.match(/baidu|bing|google|sogou|so/i);
            if (s && s[0]) {
                var sv = s[0];
                rurl = p.hostname, se = sv == 'baidu' ? '百度' : sv == 'bing' ? '必应' : sv == 'google' ? '谷歌' : sv == 'sogou' ? '搜狗' : sv == 'so' ? '360' : sv;

                var bd = rurl.match(/baidu\.com.+?[?&](wd|word)=([^&]+)/);
                var binq = rurl.match(/bing\.com.+?[?&](q)=([^&]+)/);
                var g = rurl.match(/google\.com.+?[?&](q)=([^&]+)/);
                var sg = rurl.match(/sogou\.com.+?[?&](query|keyword)=([^&]+)/);
                var so = rurl.match(/so\.com.+?[?&](q)=([^&]+)/);
                if (bd && bd[2]) {
                    kw = decodeURIComponent(bd[2]);
                } else if (binq && binq[2]) {
                    kw = decodeURIComponent(binq[2]);
                } else if (g && g[2]) {
                    kw = decodeURIComponent(g[2]);
                } else if (sg && sg[2]) {
                    kw = decodeURIComponent(sg[2]);
                } else if (so && so[2]) {
                    kw = decodeURIComponent(so[2]);
                }
            } else {
                if (rurl.length > 100) {
                    rurl = p.hostname + p.pathname;
                }
            }

        } else {
            rurl = '', se = '';
        }


    }

    return {rurl: rurl, se: se, kw: kw}

}


const getParamStr = (str, name) => {
    var m = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var n = str.split('?')[1].match(m);
    if (n != null) {
        return decodeURIComponent(n[2]);
    }
    return null;
}
const parseURL = (t) => {
    var e = document.createElement("a");
    return e.href = t, {
        hash: e.hash,
        hostname: e.hostname,
        pathname: e.pathname,
        protocol: e.protocol,
        search: e.search
    }
}

function setCookie(key, value) {
    let host = window.location.host;
    // let date = new Date();
    let expiresTime =(60 * 60 * 24);
    document.cookie = key + "=" + value ;
}

// const setCookie = (a, b) => {
//
//         // e = {
//         //     "com.cn": 1,
//         //     "net.cn": 1,
//         //     "gov.cn": 1,
//         //     "com.hk": 1,
//         //     "co.nz": 1,
//         //     "org.cn": 1,
//         //     "top.cn": 1,
//         //     "edu.cn": 1,
//         //     "asia.cn": 1
//         // },
//         // f = d.split(".");
//     var millisecond = new Date().getTime();
//     var expiresTime = new Date(millisecond + 60 * 1000 * 15);
//     // 2 < f.length && (d = (e[f.slice(-2).join(".")] ? f.slice(-3) : f.slice(-2)).join("."));
//     document.cookie = a + "=" + b + ";path=/;domain=" + d + ";expires="+expiresTime;
// }

// const getCookie = (a) => {
//     return (a = RegExp("(^| )" + a + "=([^;]*)(;|$)").exec(document.cookie)) ? a[2] : w;
// }
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

const getSid = () => {
    var sid = getCookie("JSESSIONID");
        sid = sid.length > 0 ? sid : Math.random().toString(36).slice(-8);
    var a = "zhangheng0805_sid";
    window.localStorage.setItem(a, sid);
    setCookie(a, sid);
    return window.btoa(sid);
}

const getCid = () => {
    var a = "zhangheng0805_cid";
    var cid = window.localStorage ? window.localStorage.getItem(a) : getCookie(a);
    var b = (new Date).getUTCMilliseconds();
    cid = Math.round(2147483647 * Math.abs(Math.random() - 1)) * b % 1E10;
    window.localStorage.setItem(a, cid);
    setCookie(a, cid);
    return cid;
}

const serialize = (obj) => {
    var z = [];
    for (d in obj)
        obj.hasOwnProperty(d) && obj[d] && z.push(encodeURIComponent(d) + "=" + encodeURIComponent(obj[d]));
    return z.join("&");
};
const dongle=(a,b,c,d)=>{
    let e=window.btoa(a+b);
    e=window.btoa(e+c);
    return window.btoa(e+d);
}
const sendImg = (url, data) => {
    var d = new Image(1, 1);
    d.onload = function () {
        d.onload = null;
    };
    d.onerror = function () {
        d.onerror = null;
    };
    d.src = url + "?" + data;
    return d;
}

const sendXhr = (url, data) => {
    var url = url + "?" + data;
    var e = window.XMLHttpRequest;
    if (!e)
        return !1;
    var g = new e;
    if (!("withCredentials" in g))
        return !1;
    g.open("GET", url, !0);
    g.withCredentials = !0;
    g.setRequestHeader("Content-Type", "text/plain");
    g.send(null);
    return !0
}

const SendData = (json) => {
    var url = 'static/client';
    var r = navigator.sendBeacon && navigator.sendBeacon(url, JSON.stringify(json));
}
/**
 * @desc  函数防抖---“立即执行版本” 和 “非立即执行版本” 的组合版本
 * 立即执行版 触发事件后函数不会立即执行，而是在 n 秒后执行，如果在 n 秒内又触发了事件，则会重新计算函数执行时间
 * 非立即执行版 指的是 触发事件后函数会立即执行，然后 n 秒内不触发事件才能继续执行函数的效果。
 * @param  func 需要执行的函数
 * @param  wait 延迟执行时间（毫秒）
 * @param  immediate---true 表立即执行，false 表非立即执行
 **/
function debounce(func,wait,immediate) {
    let timer;
    return function () {
        let context = this;
        let args = arguments;

        if (timer) clearTimeout(timer);
        if (immediate) {
            var callNow = !timer;
            timer = setTimeout(() => {
                timer = null;
            }, wait)
            if (callNow) func.apply(context, args)
        } else {
            timer = setTimeout(function () {
                func.apply(context, args)
            }, wait);
        }
    }
}


function client_result() {
    var sid = getSid();
    var cid = getCid();
    var os = getOS();
    var browser = getBrowser();
    var spider = getSpider();
    var ref = getReferrer();
    var b = window.navigator,
        c = window.screen,
        h = document.documentElement,
        j = document.body,
        r=new Date().getTime();
    var g = (e = document.body) && e.clientWidth && e.clientHeight,
        ca = [];
    h && h.clientWidth && h.clientHeight && ("CSS1Compat" === document.compatMode || !g) ? ca = [h.clientWidth, h.clientHeight] : g && (ca = [e.clientWidth, e.clientHeight]);
    var vp = 0 >= ca[0] || 0 >= ca[1] ? "" : ca.join("x");

    var ClientData = {
        sid: sid,
        cid: cid, //Cookie ID
        dt: document.title || '', //页面title
        dl: location.href, //页面地址
        dr: ref.rurl,
        df: ref.se + spider,
        dw: ref.kw,
        os: osName, //操作系统
        osv: os.version, //操作系统版本
        bs: browser.browser, //浏览器
        bsv: browser.version, //浏览器版本
        ul: (b && (b.language || b.browserLanguage) || "").toLowerCase(), //浏览器的语言版本
        sr: c.width + "x" + c.height, //窗口大小
        vp: vp,
        sd: c.colorDepth + "-bit", //显示图像的调色板的位深度
        je: b.javaEnabled ? 1 : 0, //浏览器是否已启用 Java 
        app: appName, //APP名
        br: brandName, //设备名
        t: dongle(sid,cid,r,browser.version),
        r:  r //时间戳
    }
    // console.log(ClientData);
    debounce(SendData(ClientData), 500, true);
    return ClientData;
}
window.onload = client_result();
var ZH_URL={
    captcha_getBase64 : 'getVerify/base64',
    captcha_checking : 'getVerify/checking',
    upload_file : 'upload/saveMulFile',
    download_getFileType : 'download/getAllFileType',
    download_getFileList : 'download/findFileList',
    delete_file : 'deleteFile',
    rename_file : 'renameFile',
    getFileList : 'getFileList',
};