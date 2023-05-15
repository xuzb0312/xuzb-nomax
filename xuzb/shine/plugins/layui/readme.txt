layui 兼容人类正在使用的全部浏览器（IE6/7除外），可作为PC端后台系统与前台界面的速成开发方案。
版本：
v2.2.5
2018-1-3

---
部分源代码个人进行了优化，在升级时请注意：
css/layui.css(文件比对-查看具体修改):
-mod.yjc.2017.12.08-del layui-form-checkbox:hover i color
-mod.yjc.2017.12.08-dl  layui-checkbox-disbaled:hover i color
-mod.yjc.2017.12.08-del layui-radio-disbaled>i:color
-mod.yjc.2017.12.15-mod laydate.js:对于获取元素值全为0的特殊处理，增加TextInputObj.dealDateAllZero处理操作
-mod.yjc.2017.12.15-mod laydate.js:对于只读不应该弹出日期选择框的纠正；增加：if(getObject($(this)).getReadOnly()){return;}
-mod.yjc.2018.02.06-mod.element.js:对于tab删除增加返回参数layid:修改位置：tabDelete:function，后增加返回layid的处理。--,r=o.attr("lay-filter")
-mod.yjc.2018.02.07-mod.layui.css--.layui-tab-item增加width:100%;height:100%;设置
-mod.yjc.2018.02.08-mod.form.js--修改t(document).off..为t(window).off..;解决嵌套iframe有些事件不响应的问题。