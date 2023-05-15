[TOC]

------------

# 库说明
> Editor.md库的引入。版本：`v1.5.0`
> markdown编辑器器
> 官网地址：[https://pandao.github.io/editor.md/](https://pandao.github.io/editor.md/ "https://pandao.github.io/editor.md/")

# 依赖
> 需要依赖于jquery。
> 由于框架默认集成了jquery,对于前端页面可以使用:
```java
<jsp:include page="frame/jsp/layout/lightweightImportFile.jsp"></jsp:include>
```
> 引入框架轻量级库。


# 使用说明
> 引入代码如下（根据实际路径调整）--head头部引入：
```Java
<jsp:include page="[实际路径]/lib/mdeditor/import.jsp"></jsp:include>
```
> 使用事例:
```html
<div id="test-editormd"> 
	<textarea style="display:none;"> </textarea> 
</div> 
```
```javascript
var testEditor;
$(function() {
	testEditor = editormd("test-editormd", {
		width: "90%",
		height: 640,
		syncScrolling: "single",
		path: "./frame/lib/mdeditor/dist/lib/"
	});
});
```
or
```javascript
var testEditor;
$(function() {
	testEditor = editormd({
		id: "test-editormd",
		width: "90%",
		height: 640,
		path: "./frame/lib/mdeditor/dist/lib/"
	});
});
```

# 注意事项
> **path**:根据实际情况来书写，一般为：`./frame/lib/mdeditor/dist/lib/`；（默认指向：`./frame/lib/mdeditor/dist/lib/` 无特殊情况，不用指定）。