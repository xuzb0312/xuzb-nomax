package com.grace.frame.util.echarts.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.grace.frame.util.echarts.code.MarkType;
import com.grace.frame.util.echarts.code.Symbol;
import com.grace.frame.util.echarts.style.ItemStyle;
import com.grace.frame.util.echarts.style.TextStyle;

/**
 * Description: BasicData
 * 
 * @author yjc
 */
public abstract class BasicData<T> implements Serializable{

	private static final long serialVersionUID = 3510060011221090087L;

	private String name;
	private String text;
	private Object value;
	/**
	 * 饼图、雷达图、力导、和弦图使用x,y
	 */
	private Object x;
	private Object y;
	/**
	 * 在存在直角坐标系的图表如折线、柱形、K线、散点图中 除了通过直接指定位置外，如果希望标注基于直角系的定位，可以通过xAxis，yAxis
	 */
	private Integer xAxis;
	private Integer yAxis;
	private MarkType type;
	private Object symbol;
	private Object symbolSize;
	private ItemStyle itemStyle;
	/**
	 * 特殊样式
	 * 
	 * @see com.github.abel533.echarts.style.TextStyle
	 */
	private TextStyle textStyle;

	/**
	 * 构造函数
	 */
	public BasicData() {}

	/**
	 * 构造函数,参数:name
	 * 
	 * @param name
	 */
	protected BasicData(String name) {
		this.name = name;
	}

	/**
	 * 构造函数,参数:name,value
	 * 
	 * @param name
	 * @param value
	 */
	public BasicData(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * 构造函数,参数:name,symbol,symbolSize
	 * 
	 * @param name
	 * @param symbol
	 * @param symbolSize
	 */
	public BasicData(String name, Object symbol, Object symbolSize) {
		this.name = name;
		this.symbol = symbol;
		this.symbolSize = symbolSize;
	}

	/**
	 * 构造函数,参数:value,symbol
	 * 
	 * @param value
	 * @param symbol
	 */
	public BasicData(Object value, Object symbol) {
		this.value = value;
		this.symbol = symbol;
	}

	/**
	 * 构造函数,参数:value,symbol,symbolSize
	 * 
	 * @param value
	 * @param symbol
	 * @param symbolSize
	 */
	public BasicData(Object value, Object symbol, Object symbolSize) {
		this.value = value;
		this.symbol = symbol;
		this.symbolSize = symbolSize;
	}

	/**
	 * 获取textStyle值
	 */
	public TextStyle textStyle() {
		if (this.textStyle == null) {
			this.textStyle = new TextStyle();
		}
		return this.textStyle;
	}

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
	 * 获取text值
	 */
	public String text() {
		return this.text;
	}

	/**
	 * 设置text值
	 * 
	 * @param text
	 */
	@SuppressWarnings("unchecked")
	public T text(String text) {
		this.text = text;
		return (T) this;
	}

	/**
	 * 获取name值
	 */
	public String name() {
		return this.name;
	}

	/**
	 * 设置name值
	 * 
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public T name(String name) {
		this.name = name;
		return (T) this;
	}

	/**
	 * 获取value值
	 */
	public Object value() {
		return this.value;
	}

	/**
	 * 设置value值
	 * 
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public T value(Object value) {
		this.value = value;
		return (T) this;
	}

	/**
	 * 设置value值
	 * 
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	public T value(Object... values) {
		if (values == null || values.length == 0) {
			return (T) this;
		}
		if (this.value == null) {
			this.value = new ArrayList<Object>(values.length);
		}
		if (this.value instanceof List) {
			((List) this.value).addAll(Arrays.asList(values));
		}
		return (T) this;
	}

	/**
	 * 获取x值
	 */
	public Object x() {
		return this.x;
	}

	/**
	 * 设置x值
	 * 
	 * @param x
	 */
	@SuppressWarnings("unchecked")
	public T x(Object x) {
		this.x = x;
		return (T) this;
	}

	/**
	 * 获取y值
	 */
	public Object y() {
		return this.y;
	}

	/**
	 * 设置y值
	 * 
	 * @param y
	 */
	@SuppressWarnings("unchecked")
	public T y(Object y) {
		this.y = y;
		return (T) this;
	}

	/**
	 * 获取xAxis值
	 */
	public Integer xAxis() {
		return this.xAxis;
	}

	/**
	 * 设置xAxis值
	 * 
	 * @param xAxis
	 */
	@SuppressWarnings("unchecked")
	public T xAxis(Integer xAxis) {
		this.xAxis = xAxis;
		return (T) this;
	}

	/**
	 * 获取yAxis值
	 */
	public Integer yAxis() {
		return this.yAxis;
	}

	/**
	 * 设置yAxis值
	 * 
	 * @param yAxis
	 */
	@SuppressWarnings("unchecked")
	public T yAxis(Integer yAxis) {
		this.yAxis = yAxis;
		return (T) this;
	}

	/**
	 * 获取type值
	 */
	public MarkType type() {
		return this.type;
	}

	/**
	 * 设置type值
	 * 
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public T type(MarkType type) {
		this.type = type;
		return (T) this;
	}

	/**
	 * 获取symbol值
	 */
	public Object symbol() {
		return this.symbol;
	}

	/**
	 * 设置symbol值
	 * 
	 * @param symbol
	 */
	@SuppressWarnings("unchecked")
	public T symbol(Object symbol) {
		this.symbol = symbol;
		return (T) this;
	}

	/**
	 * 设置symbol值
	 * 
	 * @param symbol
	 */
	@SuppressWarnings("unchecked")
	public T symbol(Symbol symbol) {
		this.symbol = symbol;
		return (T) this;
	}

	/**
	 * 获取symbolSize值
	 */
	public Object symbolSize() {
		return this.symbolSize;
	}

	/**
	 * 设置symbolSize值
	 * 
	 * @param symbolSize
	 */
	@SuppressWarnings("unchecked")
	public T symbolSize(Object symbolSize) {
		this.symbolSize = symbolSize;
		return (T) this;
	}

	/**
	 * 获取itemStyle值
	 */
	public ItemStyle itemStyle() {
		if (this.itemStyle == null) {
			this.itemStyle = new ItemStyle();
		}
		return this.itemStyle;
	}

	/**
	 * 设置itemStyle值
	 * 
	 * @param itemStyle
	 */
	@SuppressWarnings("unchecked")
	public T itemStyle(ItemStyle itemStyle) {
		this.itemStyle = itemStyle;
		return (T) this;
	}

	/**
	 * 获取name值
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置name值
	 * 
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public T setName(String name) {
		this.name = name;
		return (T) this;
	}

	/**
	 * 获取value值
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * 设置value值
	 * 
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public T setValue(Object value) {
		this.value = value;
		return (T) this;
	}

	/**
	 * 获取x值
	 */
	public Object getX() {
		return x;
	}

	/**
	 * 设置x值
	 * 
	 * @param x
	 */
	@SuppressWarnings("unchecked")
	public T setX(Object x) {
		this.x = x;
		return (T) this;
	}

	/**
	 * 获取y值
	 */
	public Object getY() {
		return y;
	}

	/**
	 * 设置y值
	 * 
	 * @param y
	 */
	@SuppressWarnings("unchecked")
	public T setY(Object y) {
		this.y = y;
		return (T) this;
	}

	/**
	 * 获取xAxis值
	 */
	public Integer getxAxis() {
		return xAxis;
	}

	/**
	 * 设置xAxis值
	 * 
	 * @param xAxis
	 */
	@SuppressWarnings("unchecked")
	public T setxAxis(Integer xAxis) {
		this.xAxis = xAxis;
		return (T) this;
	}

	/**
	 * 获取yAxis值
	 */
	public Integer getyAxis() {
		return yAxis;
	}

	/**
	 * 设置yAxis值
	 * 
	 * @param yAxis
	 */
	@SuppressWarnings("unchecked")
	public T setyAxis(Integer yAxis) {
		this.yAxis = yAxis;
		return (T) this;
	}

	/**
	 * 获取type值
	 */
	public MarkType getType() {
		return type;
	}

	/**
	 * 设置type值
	 * 
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public T setType(MarkType type) {
		this.type = type;
		return (T) this;
	}

	/**
	 * 获取symbol值
	 */
	public Object getSymbol() {
		return symbol;
	}

	/**
	 * 设置symbol值
	 * 
	 * @param symbol
	 */
	@SuppressWarnings("unchecked")
	public T setSymbol(Object symbol) {
		this.symbol = symbol;
		return (T) this;
	}

	/**
	 * 获取symbolSize值
	 */
	public Object getSymbolSize() {
		return symbolSize;
	}

	/**
	 * 设置symbolSize值
	 * 
	 * @param symbolSize
	 */
	@SuppressWarnings("unchecked")
	public T setSymbolSize(Object symbolSize) {
		this.symbolSize = symbolSize;
		return (T) this;
	}

	/**
	 * 获取itemStyle值
	 */
	public ItemStyle getItemStyle() {
		return itemStyle;
	}

	/**
	 * 设置itemStyle值
	 * 
	 * @param itemStyle
	 */
	@SuppressWarnings("unchecked")
	public T setItemStyle(ItemStyle itemStyle) {
		this.itemStyle = itemStyle;
		return (T) this;
	}

	/**
	 * 获取text值
	 */
	public String getText() {
		return text;
	}

	/**
	 * 设置text值
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
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
}