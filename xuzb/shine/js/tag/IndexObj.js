/**
 * 目录索引标签
 */
function IndexObj(obj) {
	this.obj = obj;
	this.obj_data = this.obj.data("obj_data");// 后台写过来的-标签数据HashMap数据
};
/**
 * 初始化
 */
IndexObj.prototype.init = function(mapData) {
	// 参数初始化
	this.obj_data = mapData;
	this.obj.data("obj_data", mapData);

	// 绑定单击事件
	var objTmp = this.obj;
	this.obj.find("a").on("click", function() {
		objTmp.find("a").removeClass("this");
		$(this).addClass("this");
	});

	// 展示
	this.show();
};

/**
 * 显示目录
 * 
 * @param mapData
 * @return
 */
IndexObj.prototype.show = function() {
	var title = this.obj_data.get("title");
	var objTmp = this.obj;
	layui.use("layer", function() {
		var layer = layui.layer;
		layer.ready(function() {
			layer.open( {
				title : title,
				type : 1,
				content : objTmp,
				area : "190px",
				offset : "r",
				shade : false,
				shadeClose : false,
				anim : 2,
				id : "_" + objTmp.attr("id"),
				success : function(layero, index) {
					layer.style(index, {
						marginLeft : -10,
						marginTop : -80
					});
				}
			});
		});
	});
};