package com.grace.frame.util.echarts;

import com.grace.frame.util.echarts.code.Position;
import com.grace.frame.util.echarts.style.TextStyle;

import java.io.Serializable;

/**
 * Description: Label
 * 
 * @author yjc
 */
public abstract class AbstractLabel<T> implements Serializable{

	private static final long serialVersionUID = -6908403517815789999L;

	/**
	 * 是否显示，在Timeline中默认true
	 */
	private Boolean show;
	/**
	 * 挑选间隔，默认为'auto'，可选为：'auto'（自动隐藏显示不下的） | 0（全部显示） | {number}
	 */
	private Object interval;
	/**
	 * rotate : 旋转角度，默认为0，不旋转，正值为逆时针，负值为顺时针，可选为：-90 ~ 90
	 */
	private Integer rotate;
	/**
	 * 间隔名称格式器：{string}（Template） | {Function}
	 */
	private Object formatter;
	/**
	 * 文字样式（详见{@link com.grace.frame.util.echarts.style.TextStyle}）
	 */
	private TextStyle textStyle;
	/**
	 * 位置
	 */
	private Object position;
	/**
	 * [Axis有效]坐标轴文本标签是否可点击
	 */
	private Boolean clickable;
	/**
	 * 坐标轴文本标签与坐标轴的间距
	 */
	private Integer margin;
	/**
	 * 颜色
	 */
	private String color;

	/**
	 * 设置textStyle值
	 * 
	 * @param textStyle
	 */
	@SuppressWarnings("unchecked")
	public T textStyle(TextStyle textStyle) {
		this.textStyle = textStyle;
		return (T) this;
	}

	/**
	 * 获取show值
	 */
	public Boolean show() {
		return this.show;
	}

	/**
	 * 设置show值
	 * 
	 * @param show
	 */
	@SuppressWarnings("unchecked")
	public T show(Boolean show) {
		this.show = show;
		return (T) this;
	}

	/**
	 * 获取position值
	 */
	public Object position() {
		return this.position;
	}

	/**
	 * 设置position值
	 * 
	 * @param position
	 */
	@SuppressWarnings("unchecked")
	public T position(Object position) {
		this.position = position;
		return (T) this;
	}

	/**
	 * 设置position值
	 * 
	 * @param position
	 */
	@SuppressWarnings("unchecked")
	public T position(Position position) {
		this.position = position;
		return (T) this;
	}

	/**
	 * 获取interval值
	 */
	public Object interval() {
		return this.interval;
	}

	/**
	 * 设置interval值
	 * 
	 * @param interval
	 */
	@SuppressWarnings("unchecked")
	public T interval(Object interval) {
		this.interval = interval;
		return (T) this;
	}

	/**
	 * 获取rotate值
	 */
	public Integer rotate() {
		return this.rotate;
	}

	/**
	 * 设置rotate值
	 * 
	 * @param rotate
	 */
	@SuppressWarnings("unchecked")
	public T rotate(Integer rotate) {
		this.rotate = rotate;
		return (T) this;
	}

	/**
	 * 获取clickable值
	 */
	public Boolean clickable() {
		return this.clickable;
	}

	/**
	 * 设置clickable值
	 * 
	 * @param clickable
	 */
	@SuppressWarnings("unchecked")
	public T clickable(Boolean clickable) {
		this.clickable = clickable;
		return (T) this;
	}

	/**
	 * 获取formatter值
	 */
	public Object formatter() {
		return this.formatter;
	}

	/**
	 * 设置formatter值
	 * 
	 * @param formatter
	 */
	@SuppressWarnings("unchecked")
	public T formatter(Object formatter) {
		this.formatter = formatter;
		return (T) this;
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
	@SuppressWarnings("unchecked")
	public T color(String color) {
		this.color = color;
		return (T) this;
	}

	/**
	 * 文字样式（详见{@link com.grace.frame.util.echarts.style.TextStyle}）
	 */
	public TextStyle textStyle() {
		if (this.textStyle == null) {
			this.textStyle = new TextStyle();
		}
		return this.textStyle;
	}

	/**
	 * 获取margin值
	 */
	public Integer margin() {
		return this.margin;
	}

	/**
	 * 设置margin值
	 * 
	 * @param margin
	 */
	@SuppressWarnings("unchecked")
	public T margin(Integer margin) {
		this.margin = margin;
		return (T) this;
	}

	/**
	 * 获取textStyle值
	 */
	public TextStyle getTextStyle() {
		return textStyle;
	}

	/**
	 * 设置textStyle值
	 * 
	 * @param textStyle
	 */
	public void setTextStyle(TextStyle textStyle) {
		this.textStyle = textStyle;
	}

	/**
	 * 获取show值
	 */
	public Boolean getShow() {
		return show;
	}

	/**
	 * 设置show值
	 * 
	 * @param show
	 */
	public void setShow(Boolean show) {
		this.show = show;
	}

	/**
	 * 获取position值
	 */
	public Object getPosition() {
		return position;
	}

	/**
	 * 设置position值
	 * 
	 * @param position
	 */
	public void setPosition(Object position) {
		this.position = position;
	}

	/**
	 * 获取interval值
	 */
	public Object getInterval() {
		return interval;
	}

	/**
	 * 设置interval值
	 * 
	 * @param interval
	 */
	public void setInterval(Object interval) {
		this.interval = interval;
	}

	/**
	 * 获取rotate值
	 */
	public Integer getRotate() {
		return rotate;
	}

	/**
	 * 设置rotate值
	 * 
	 * @param rotate
	 */
	public void setRotate(Integer rotate) {
		this.rotate = rotate;
	}

	/**
	 * 获取clickable值
	 */
	public Boolean getClickable() {
		return clickable;
	}

	/**
	 * 设置clickable值
	 * 
	 * @param clickable
	 */
	public void setClickable(Boolean clickable) {
		this.clickable = clickable;
	}

	/**
	 * 获取formatter值
	 */
	public Object getFormatter() {
		return formatter;
	}

	/**
	 * 设置formatter值
	 * 
	 * @param formatter
	 */
	public void setFormatter(Object formatter) {
		this.formatter = formatter;
	}

	/**
	 * 获取margin值
	 */
	public Integer getMargin() {
		return margin;
	}

	/**
	 * 设置margin值
	 * 
	 * @param margin
	 */
	public void setMargin(Integer margin) {
		this.margin = margin;
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
