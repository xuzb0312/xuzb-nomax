package com.grace.frame.util.echarts.style;

import com.grace.frame.util.echarts.code.BrushType;

/**
 * Description: NodeStyle
 * 
 * @author yjc
 */
public class NodeStyle extends LinkStyle{
	private static final long serialVersionUID = -7134603759664977115L;
	/**
	 * 可选 'both', 'stroke', 'fill'
	 */
	private BrushType brushType;
	/**
	 * 填充颜色
	 */
	private String color;

	/**
	 * 获取brushType值
	 */
	public BrushType brushType() {
		return this.brushType;
	}

	/**
	 * 设置brushType值
	 * 
	 * @param brushType
	 */
	public NodeStyle brushType(BrushType brushType) {
		this.brushType = brushType;
		return this;
	}

	/**
	 * 获取color值
	 */
	public String color() {
		return this.color;
	}

	/**
	 * 设置color值
	 * 
	 * @param color
	 */
	public NodeStyle color(String color) {
		this.color = color;
		return this;
	}

	/**
	 * 获取brushType值
	 */
	public BrushType getBrushType() {
		return brushType;
	}

	/**
	 * 设置brushType值
	 * 
	 * @param brushType
	 */
	public void setBrushType(BrushType brushType) {
		this.brushType = brushType;
	}

	/**
	 * 获取color值
	 */
	public String getColor() {
		return color;
	}

	/**
	 * 设置color值
	 * 
	 * @param color
	 */
	public void setColor(String color) {
		this.color = color;
	}
}
