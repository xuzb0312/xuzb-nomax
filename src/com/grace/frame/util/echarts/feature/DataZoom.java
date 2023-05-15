package com.grace.frame.util.echarts.feature;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yjc
 */
public class DataZoom extends Feature{
	private static final long serialVersionUID = -3251071457854725410L;

	/**
	 * 构造函数
	 */
	@SuppressWarnings("unchecked")
	public DataZoom() {
		this.show(true);
		Map title = new HashMap<String, String>();
		title.put("dataZoom", "区域缩放");
		title.put("dataZoomReset", "区域缩放后退");
		this.title(title);
	}
}
