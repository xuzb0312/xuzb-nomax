# 使用说明

## 1. 前端

1. 在页面首先引入以下代码（js文件依赖jquery）：

```html
<!--样式引入-->
<link rel="stylesheet" type="text/css" href="./frame/js/captcha/captcha.css?v=1.0" />

<!--脚本引入，需要在jquery引入之后-->
<script type="text/javascript" src="./frame/js/captcha/captcha.js?v=1.0"></script>
```

2. 使用方法：

```html
<!--html代码,根据自己情况书写-->
<div id="clickCode" style="position:absolute;left: 30px;top:125px;display: none;"></div>
```

   

```js
// js脚本
$("#clickCode").show();
$("#clickCode").empty();
$("#clickCode").MyCaptcha({
    success: function() {
        $("#clickCode").hide();
        $("#clickCode").empty();
        
        // 验证成功后的操作....
    }
});
```



## 2. 后台登录方法验证是否通过了验证码验证

```java
if (!ClickTextCaptcha.getValidatePass(request.getSession())) {
	throw new BizException("验证码认证不通过，请重试。");
}
```

