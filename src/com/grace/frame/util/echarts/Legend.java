package com.grace.frame.util.echarts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.grace.frame.util.echarts.code.Align;
import com.grace.frame.util.echarts.code.Orient;
import com.grace.frame.util.echarts.style.TextStyle;

/**
 * @author yjc
 */
public class Legend extends Basic<Legend> implements Data<Legend> ,Component{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4936274802029046851L;
	/**
	 * 布局方式，默认为水平布局，可选为：'horizontal' | 'vertical'
	 * 
	 * @see com.grace.frame.util.echarts.code.Orient
	 */
	private Orient orient;
	/**
	 * 图例图形宽度
	 */
	private Integer itemWidth;
	/**
	 * 图例图形高度
	 */
	private Integer itemHeight;
	/**
	 * 文字样式
	 * 
	 * @see com.grace.frame.util.echarts.style.TextStyle
	 */
	private TextStyle textStyle;
	/**
	 * 选择模式，默认开启图例开关
	 * 
	 * @see com.grace.frame.util.echarts.code.SelectedMode
	 */
	private Object selectedMode;
	/**
	 * 配置默认选中状态，可配合LEGEND.SELECTED事件做动态数据载入
	 */
	private Map<String, Boolean> selected;
	/**
	 * 图例内容数组，数组项通常为{string}，每一项代表一个系列的name。
	 * 
	 * @see com.grace.frame.util.echarts.data.LegendData
	 */
	@SuppressWarnings("unchecked")
	private List data;

	private Align align;
	private String formatter;

	/**
	 * 构造函数
	 */
	public Legend() {}

	/**
	 * 构造函数,参数:values
	 * 
	 * @param values
	 */
	public Legend(Object... values) {
		this.data(values);
	}

	public Align align() {
		return this.align;
	}

	public Legend align(Align align) {
		this.align = align;
		return this;
	}

	public String formatter() {
		return this.formatter;
	}

	public Legend formatter(String formatter) {
		this.formatter = formatter;
		return this;
	}

	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	/**
	 * 设置textStyle值
	 * 
	 * @param textStyle
	 */
	public Legend textStyle(TextStyle textStyle) {
		this.textStyle = textStyle;
		return this;
	}

	/**
	 * 设置data值
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public Legend data(List data) {
		this.data = data;
		return this;
	}

	/**
	 * 获取orient值
	 */
	public Orient orient() {
		return this.orient;
	}

	/**
	 * 设置orient值
	 * 
	 * @param orient
	 */
	public Legend orient(Orient orient) {
		this.orient = orient;
		return this;
	}

	/**
	 * 获取itemWidth值
	 */
	public Integer itemWidth() {
		return this.itemWidth;
	}

	/**
	 * 设置itemWidth值
	 * 
	 * @param itemWidth
	 */
	public Legend itemWidth(Integer itemWidth) {
		this.itemWidth = itemWidth;
		return this;
	}

	/**
	 * 获取itemHeight值
	 */
	public Integer itemHeight() {
		return this.itemHeight;
	}

	/**
	 * 设置itemHeight值
	 * 
	 * @param itemHeight
	 */
	public Legend itemHeight(Integer itemHeight) {
		this.itemHeight = itemHeight;
		return this;
	}

	/**
	 * 文字样式
	 * 
	 * @see com.grace.frame.util.echarts.style.TextStyle
	 */
	public TextStyle textStyle() {
		if (this.textStyle == null) {
			this.textStyle = new TextStyle();
		}
		return this.textStyle;
	}

	/**
	 * 获取selectedMode值
	 */
	public Object selectedMode() {
		return this.selectedMode;
	}

	/**
	 * 设置selectedMode值
	 * 
	 * @param selectedMode
	 */
	public Legend selectedMode(Object selectedMode) {
		this.selectedMode = selectedMode;
		return this;
	}

	/**
	 * 获取selected值
	 * 
	 * @param name
	 */
	public Boolean selected(String name) {
		if (this.selected == null) {
			return null;
		} else {
			return selected.get(name);
		}
	}

	/**
	 * 设置默认选中状态
	 * 
	 * @param name
	 * @param selected
	 * @return
	 */
	public Legend selected(String name, Boolean selected) {
		if (!this.data.contains(name)) {
			throw new RuntimeException("Legend中不包含name为" + name + "的图例");
		}
		if (this.selected == null) {
			this.selected = new LinkedHashMap<String, Boolean>();
		}
		this.selected.put(name, selected);
		return this;
	}

	/**
	 * 获取data值
	 */
	@SuppressWarnings("unchecked")
	public List data() {
		if (this.data == null) {
			this.data = new ArrayList();
		}
		return this.data;
	}

	/**
	 * 添加图例属性
	 * 
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Legend data(Object... values) {
		if (values == null || values.length == 0) {
			return this;
		}
		this.data().addAll(Arrays.asList(values));
		return this;
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
	 * 获取data值
	 */
	@SuppressWarnings("unchecked")
	public List getData() {
		return data;
	}

	/**
	 * 设置data值
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void setData(List data) {
		this.data = data;
	}

	/**
	 * 获取orient值
	 */
	public Orient getOrient() {
		return orient;
	}

	/**
	 * 设置orient值
	 * 
	 * @param orient
	 */
	public void setOrient(Orient orient) {
		this.orient = orient;
	}

	/**
	 * 获取itemWidth值
	 */
	public Integer getItemWidth() {
		return itemWidth;
	}

	/**
	 * 设置itemWidth值
	 * 
	 * @param itemWidth
	 */
	public void setItemWidth(Integer itemWidth) {
		this.itemWidth = itemWidth;
	}

	/**
	 * 获取itemHeight值
	 */
	public Integer getItemHeight() {
		return itemHeight;
	}

	/**
	 * 设置itemHeight值
	 * 
	 * @param itemHeight
	 */
	public void setItemHeight(Integer itemHeight) {
		this.itemHeight = itemHeight;
	}

	/**
	 * 获取selectedMode值
	 */
	public Object getSelectedMode() {
		return selectedMode;
	}

	/**
	 * 设置selectedMode值
	 * 
	 * @param selectedMode
	 */
	public void setSelectedMode(Object selectedMode) {
		this.selectedMode = selectedMode;
	}

	/**
	 * 获取selected
	 */
	public Map<String, Boolean> getSelected() {
		return selected;
	}

	/**
	 * 设置selected
	 * 
	 * @param selected
	 */
	public void setSelected(Map<String, Boolean> selected) {
		this.selected = selected;
	}
}
