package com.grace.frame.util.echarts.style;

import java.io.Serializable;

/**
 * 区域填充样式
 * 
 * @author yjc
 */
public class AreaStyle implements Serializable{

	private static final long serialVersionUID = -6547716731700677234L;

	/**
	 * 颜色
	 */
	private Object color;
	/**
	 * 填充样式，目前仅支持'default'(实填充)
	 */
	private Object type;

	/**
	 * 获取color值
	 */
	public Object color() {
		return this.color;
	}

	/**
	 * 设置color值
	 * 
	 * @param color
	 */
	public AreaStyle color(Object color) {
		this.color = color;
		return this;
	}

	/**
	 * 获取type值
	 */
	public Object type() {
		return this.type;
	}

	/**
	 * 设置type值
	 * 
	 * @param type
	 */
	public AreaStyle type(Object type) {
		this.type = type;
		return this;
	}

	/**
	 * 获取typeDefault值
	 */
	public AreaStyle typeDefault() {
		this.type = "default";
		return this;
	}

	/**
	 * 获取color值
	 */
	public Object getColor() {
		return color;
	}

	/**
	 * 设置color值
	 * 
	 * @param color
	 */
	public void setColor(Object color) {
		this.color = color;
	}

	/**
	 * 获取type值
	 */
	public Object getType() {
		return type;
	}

	/**
	 * 设置type值
	 * 
	 * @param type
	 */
	public void setType(Object type) {
		this.type = type;
	}
}
