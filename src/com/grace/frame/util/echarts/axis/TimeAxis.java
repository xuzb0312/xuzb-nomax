package com.grace.frame.util.echarts.axis;

import com.grace.frame.util.echarts.code.AxisType;

/**
 * 时间型坐标轴用法同数值型，只是目标处理和格式化显示时会自动转变为时间，并且随着时间跨度的不同自动切换需要显示的时间粒度
 * 
 * @author yjc
 */
public class TimeAxis extends ValueAxis{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6228197271678155954L;

	/**
	 * 构造函数
	 */
	public TimeAxis() {
		this.type(AxisType.time);
	}
}
