package com.grace.frame.util.echarts;

import com.grace.frame.util.echarts.code.Orient;
import com.grace.frame.util.echarts.code.Tool;
import com.grace.frame.util.echarts.feature.Feature;
import com.grace.frame.util.echarts.style.ItemStyle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yjc
 */
public class Toolbox extends Basic<Toolbox> implements Component{
	private static final long serialVersionUID = 8406366917131667314L;
	/**
	 * 启用功能，目前支持feature见下，工具箱自定义功能回调处理
	 */
	private Map<String, Feature> feature;
	/**
	 * 布局方式，默认为水平布局，可选为：'horizontal' | 'vertical'
	 * 
	 * @see com.grace.frame.util.echarts.code.Orient
	 */
	private Orient orient;
	/**
	 * 工具箱背景颜色，默认透明
	 */
	private List<Object> color;
	/**
	 * 无效颜色
	 */
	private String disableColor;
	/**
	 * 激活颜色
	 */
	private String effectiveColor;
	/**
	 * 工具箱icon大小，单位（px）
	 */
	private Integer itemSize;
	/**
	 * 是否显示工具箱文字提示，默认启用
	 */
	private Boolean showTitle;
	/**
	 * 公用的 icon 样式设置
	 */
	private ItemStyle iconStyle;

	public ItemStyle iconStyle() {
		return this.iconStyle;
	}

	public Toolbox iconStyle(ItemStyle iconStyle) {
		this.iconStyle = iconStyle;
		return this;
	}

	public ItemStyle getIconStyle() {
		return iconStyle;
	}

	public void setIconStyle(ItemStyle iconStyle) {
		this.iconStyle = iconStyle;
	}

	/**
	 * 设置color值
	 * 
	 * @param color
	 */
	public Toolbox color(List<Object> color) {
		this.color = color;
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
	public Toolbox orient(Orient orient) {
		this.orient = orient;
		return this;
	}

	/**
	 * 工具箱背景颜色，默认透明
	 */
	public List<Object> color() {
		if (this.color == null) {
			this.color = new ArrayList<Object>();
		}
		return this.color;
	}

	/**
	 * 获取disableColor值
	 */
	public String disableColor() {
		return this.disableColor;
	}

	/**
	 * 设置disableColor值
	 * 
	 * @param disableColor
	 */
	public Toolbox disableColor(String disableColor) {
		this.disableColor = disableColor;
		return this;
	}

	/**
	 * 获取effectiveColor值
	 */
	public String effectiveColor() {
		return this.effectiveColor;
	}

	/**
	 * 设置effectiveColor值
	 * 
	 * @param effectiveColor
	 */
	public Toolbox effectiveColor(String effectiveColor) {
		this.effectiveColor = effectiveColor;
		return this;
	}

	/**
	 * 获取itemSize值
	 */
	public Integer itemSize() {
		return this.itemSize;
	}

	/**
	 * 设置itemSize值
	 * 
	 * @param itemSize
	 */
	public Toolbox itemSize(Integer itemSize) {
		this.itemSize = itemSize;
		return this;
	}

	/**
	 * 获取showTitle值
	 */
	public Boolean showTitle() {
		return this.showTitle;
	}

	/**
	 * 设置showTitle值
	 * 
	 * @param showTitle
	 */
	public Toolbox showTitle(Boolean showTitle) {
		this.showTitle = showTitle;
		return this;
	}

	/**
	 * 启用功能，目前支持feature见下，工具箱自定义功能回调处理
	 */
	public Map<String, Feature> feature() {
		if (this.feature == null) {
			this.feature = new LinkedHashMap<String, Feature>();
		}
		return this.feature;
	}

	/**
	 * 添加组件
	 * 
	 * @param value
	 * @return
	 */
	private Toolbox _addFeature(Feature value) {
		if (value == null) {
			return this;
		}
		// 第一个字母转小写
		String name = value.getClass().getSimpleName();
		name = name.substring(0, 1).toLowerCase() + name.substring(1);
		_addFeatureOnce(name, value);
		return this;
	}

	/**
	 * 添加组件
	 * 
	 * @param values
	 * @return
	 */
	@SuppressWarnings("null")
	public Toolbox feature(Object... values) {
		if (values == null && values.length == 0) {
			return this;
		}
		if (this.feature == null) {
			this.feature = new LinkedHashMap<String, Feature>();
		}
		for (Object t : values) {
			if (t instanceof Feature) {
				_addFeature((Feature) t);
			} else if (t instanceof Tool) {
				switch ((Tool) t) {
					case dataView:
						_addFeatureOnce(t, Feature.dataView);
						break;
					case dataZoom:
						_addFeatureOnce(t, Feature.dataZoom);
						break;
					case magicType:
						_addFeatureOnce(t, Feature.magicType);
						break;
					case mark:
						_addFeatureOnce(t, Feature.mark);
						break;
					case restore:
						_addFeatureOnce(t, Feature.restore);
						break;
					case saveAsImage:
						_addFeatureOnce(t, Feature.saveAsImage);
						break;
					default:
						// ignore
				}
			}
		}
		return this;
	}

	/**
	 * 同一种组件只添加一次
	 * 
	 * @param name
	 * @param feature
	 */
	private void _addFeatureOnce(Object name, Feature feature) {
		String _name = String.valueOf(name);
		if (!this.feature().containsKey(_name)) {
			this.feature().put(_name, feature);
		}
	}

	/**
	 * 获取color值
	 */
	public List<Object> getColor() {
		return color;
	}

	/**
	 * 设置color值
	 * 
	 * @param color
	 */
	public void setColor(List<Object> color) {
		this.color = color;
	}

	/**
	 * 获取feature值
	 */
	public Map<String, Feature> getFeature() {
		return feature;
	}

	/**
	 * 设置feature值
	 * 
	 * @param feature
	 */
	public void setFeature(Map<String, Feature> feature) {
		this.feature = feature;
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
	 * 获取disableColor值
	 */
	public String getDisableColor() {
		return disableColor;
	}

	/**
	 * 设置disableColor值
	 * 
	 * @param disableColor
	 */
	public void setDisableColor(String disableColor) {
		this.disableColor = disableColor;
	}

	/**
	 * 获取effectiveColor值
	 */
	public String getEffectiveColor() {
		return effectiveColor;
	}

	/**
	 * 设置effectiveColor值
	 * 
	 * @param effectiveColor
	 */
	public void setEffectiveColor(String effectiveColor) {
		this.effectiveColor = effectiveColor;
	}

	/**
	 * 获取itemSize值
	 */
	public Integer getItemSize() {
		return itemSize;
	}

	/**
	 * 设置itemSize值
	 * 
	 * @param itemSize
	 */
	public void setItemSize(Integer itemSize) {
		this.itemSize = itemSize;
	}

	/**
	 * 获取showTitle值
	 */
	public Boolean getShowTitle() {
		return showTitle;
	}

	/**
	 * 设置showTitle值
	 * 
	 * @param showTitle
	 */
	public void setShowTitle(Boolean showTitle) {
		this.showTitle = showTitle;
	}
}
