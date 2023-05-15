package com.grace.frame.util.echarts.feature;

/**
 * @author yjc
 */
public class DataView extends Feature{
	private static final long serialVersionUID = -7356182560642873218L;

	/**
	 * 构造函数
	 */
	public DataView() {
		this.show(true);
		this.title("数据视图");
		this.readOnly(false);
		this.lang(new String[] { "数据视图", "关闭", "刷新" });
	}
}
