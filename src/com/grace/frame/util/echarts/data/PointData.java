package com.grace.frame.util.echarts.data;

/**
 * Description: PointData
 * 
 * @author yjc
 */
public class PointData extends BasicData<PointData>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8011562114322443367L;

	/**
	 * 构造函数
	 */
	public PointData() {
		super();
	}

	/**
	 * 构造函数,参数:name,value
	 * 
	 * @param name
	 * @param value
	 */
	public PointData(String name, Object value) {
		super(name, value);
	}

	/**
	 * 构造函数,参数:name,symbol,symbolSize
	 * 
	 * @param name
	 * @param symbol
	 * @param symbolSize
	 */
	public PointData(String name, Object symbol, Object symbolSize) {
		super(name, symbol, symbolSize);
	}

	/**
	 * 构造函数,参数:value,symbol
	 * 
	 * @param value
	 * @param symbol
	 */
	public PointData(Object value, Object symbol) {
		super(value, symbol);
	}

	/**
	 * 构造函数,参数:value,symbol,symbolSize
	 * 
	 * @param value
	 * @param symbol
	 * @param symbolSize
	 */
	public PointData(Object value, Object symbol, Object symbolSize) {
		super(value, symbol, symbolSize);
	}
}
