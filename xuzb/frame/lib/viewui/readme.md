[TOC]

------------

# 库说明
> ViewUI库的引入。ViewUI版本：`v4.0.2`
> View UI，即原先的 iView，是一套基于 Vue.js 的开源 UI 组件库，主要服务于 PC 界面的中后台产品。
> 官方网址：[https://www.iviewui.com/docs/introduce](https://www.iviewui.com/docs/introduce "https://www.iviewui.com/docs/introduce")

# 依赖
> 依赖于Vue库。请在使用该库时，首先引入Vue库。引入方式：
```Java
<jsp:include page="[实际路径]/lib/vue/import.jsp"></jsp:include>
```

# 使用说明
> 引入代码如下（根据实际路径调整）--head头部引入：
```Java
<jsp:include page="[实际路径]/lib/viewui/import.jsp"></jsp:include>
```
> 主题（目前该库采用的是默认主题），如果需要自定义主题，官方[https://www.iviewui.com/docs/guide/theme](https://www.iviewui.com/docs/guide/theme "https://www.iviewui.com/docs/guide/theme")介绍已经不适合当前版本的样式定义了。
如需自定义样式：请前往：[https://github.com/view-design/ViewUI](https://github.com/view-design/ViewUI) 下载项目源码（注意版本匹配），并修改`src\styles\custom.less`样式。然后重新编译。

**注意：** 如果无法获取到4.0.2版本，请从该地址获取。[https://github.com/yangjuncheng/ViewUI](https://github.com/yangjuncheng/ViewUI)

**相关命令：**
1. 根据以上地址下载项目源码。
2. 执行npm install或者cnpm install。
3. 修改src\styles\custom.less样式定义文件。
4. 执行：`npm run dist:style`
5. 在dist目录获取编译完成的样式文件。

**使用自定义主题：**
1. 在lib/viewui/dist下新建custom目录。
2. 将编译生成的css文件和font文件copy到cutsom目录。
3. 修改lib/viewui/import文件的样式路径地址为custom路径地址。

```html
<link rel="stylesheet"
	href="./frame/lib/viewui/dist/styles/iview.css?v=4.0.2" type="text/css">
<!--修改为-->
<link rel="stylesheet"
	href="./frame/lib/viewui/dist/custom/iview.css?v=4.0.2" type="text/css">
```	

------------

> 实例代码（具体请参照ViewUI官网使用说明）：
```html
<div id="app" style="margin: 20px; padding: 10px;">
	<radio-group v-model="buttonSize" type="button">
		<radio label="large">
			Large
		</radio>
		<radio label="default">
			Default
		</radio>
		<radio label="small">
			small
		</radio>
	</radio-group>
	<i-button @click="show" type="success" :size="buttonSize">
		Click me!
	</i-button>
	<modal v-model="visible" title="Welcome">
		{{content}}
	</modal>
	<i-circle :percent="percent">
		<span class="demo-Circle-inner" style="font-size: 24px">{{percent}}%</span>
	</i-circle>
	<i-circle :percent="100" stroke-color="#5cb85c">
		<icon type="ios-checkmark" size="60" style="color:#5cb85c"></icon>
	</i-circle>
	<i-circle :percent="35" stroke-color="#ff5500">
		<span class="demo-Circle-inner">
			<icon type="ios-close" size="50" style="color:#ff5500"></icon>
		</span>
	</i-circle>
</div>
<script type="text/javascript">
	new Vue({
		el: '#app',
		data: {
			visible: false,
			buttonSize: 'defalut',
			content: 'Welcome to ViewUI',
			percent: 0
		},
		methods: {
			show: function() {
				this.percent = 0;
				this.visible = true;
				var url = new URL("http://localhost/tpm/demo.jsp");
				AjaxUtil.asyncBizRequest(url, data => {
					this.content = data;
				});
				var intV = setInterval(() => {
					console.log("good！");
					console.log(this.percent);
					if (this.percent >= 100) {
						clearInterval(intV);
						return;
					}
					this.percent = this.percent + 1;
				}, 100);
			}
		}
	});
</script>
```


# 注意事项
> 无