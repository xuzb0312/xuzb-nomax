package com.grace.frame.util.echarts.feature;

/**
 * @author yjc
 */
public class SaveAsImage extends Feature{
	private static final long serialVersionUID = -8287371191665740999L;

	/**
	 * 构造函数
	 */
	public SaveAsImage() {
		this.show(true);
		this.title("保存为图片");
		this.type("png");
		this.lang(new String[] { "点击保存" });
	}
}
