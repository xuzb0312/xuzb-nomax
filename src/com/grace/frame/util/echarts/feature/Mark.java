package com.grace.frame.util.echarts.feature;

import com.grace.frame.util.echarts.code.LineType;
import com.grace.frame.util.echarts.style.LineStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * 辅助线标志，上图icon左数1/2/3，分别是启用，删除上一条，删除全部，可设置更多属性
 * 
 * @author yjc
 */
public class Mark extends Feature{
	private static final long serialVersionUID = -3571163856818897270L;

	/**
	 * 构造函数
	 */
	@SuppressWarnings("unchecked")
	public Mark() {
		this.show(true);
		Map title = new HashMap<String, String>();
		title.put("mark", "辅助线开关");
		title.put("markUndo", "删除辅助线");
		title.put("markClear", "清空辅助线");
		this.title(title);
		this.lineStyle(new LineStyle());
		this.lineStyle().width(2);
		this.lineStyle().color("#1e90ff");
		this.lineStyle().type(LineType.dashed);
	}
}
