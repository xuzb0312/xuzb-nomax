package com.grace.frame.util.echarts.axis;

import com.grace.frame.util.echarts.AbstractData;
import com.grace.frame.util.echarts.Component;
import com.grace.frame.util.echarts.code.AxisType;
import com.grace.frame.util.echarts.code.X;
import com.grace.frame.util.echarts.code.Y;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 坐标轴
 * 
 * @author yjc
 */
public abstract class Axis<T> extends AbstractData<T> implements Component{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7231267575886880435L;
	/**
	 * 是否显示
	 */
	private Boolean show;
	/**
	 * 坐标轴类型，横轴默认为类目型'category'，纵轴默认为数值型'value'
	 * 
	 * @see com.grace.frame.util.echarts.code.AxisType
	 */
	private AxisType type;
	/**
	 * 坐标轴类型，横轴默认为类目型'bottom'，纵轴默认为数值型'left'，可选为：'bottom' | 'top' | 'left' |
	 * 'right'
	 * 
	 * @see com.grace.frame.util.echarts.code.X
	 * @see com.grace.frame.util.echarts.code.Y
	 */
	private Object position;
	/**
	 * 坐标轴名称，默认为空
	 */
	private String name;
	/**
	 * 坐标轴线，默认显示，属性show控制显示与否，属性lineStyle（详见lineStyle）控制线条样式
	 * 
	 * @see com.grace.frame.util.echarts.axis.AxisLine
	 * @see com.grace.frame.util.echarts.style.LineStyle
	 */
	private AxisLine axisLine;
	/**
	 * 坐标轴小标记，默认不显示，属性show控制显示与否，属性length控制线长，属性lineStyle（详见lineStyle）控制线条样式
	 * 
	 * @see com.grace.frame.util.echarts.axis.AxisTick
	 * @see com.grace.frame.util.echarts.style.LineStyle
	 */
	private AxisTick axisTick;
	/**
	 * 坐标轴文本标签，详见axis.axisLabel
	 * 
	 * @see com.grace.frame.util.echarts.axis.AxisLabel
	 */
	private AxisLabel axisLabel;
	/**
	 * 分隔线，默认显示，属性show控制显示与否，属性lineStyle（详见lineStyle）控制线条样式
	 * 
	 * @see com.grace.frame.util.echarts.axis.SplitLine
	 */
	private SplitLine splitLine;
	/**
	 * 分隔区域，默认不显示，属性show控制显示与否，属性areaStyle（详见areaStyle）控制区域样式
	 */
	private SplitArea splitArea;
	/**
	 * 一级层叠控制
	 */
	private Integer zlevel;
	/**
	 * 二级层叠控制
	 */
	private Integer z;
	/**
	 * x 轴所在的 grid 的索引，默认位于第一个 grid
	 */
	private Integer gridIndex;
	/**
	 * 坐标轴名称与轴线之间的距离
	 */
	private Integer nameGap;
	/**
	 * 是否是反向坐标轴。ECharts 3 中新加
	 */
	private Boolean inverse;
	/**
	 * 坐标轴两边留白策略，类目轴和非类目轴的设置和表现不一样
	 */
	private Object boundaryGap;
	/**
	 * 坐标轴刻度最小值，在类目轴中无效
	 */
	private Object min;
	/**
	 * 坐标轴刻度最大值，在类目轴中无效
	 */
	private Object max;
	/**
	 * 只在数值轴中（type: 'value'）有效
	 */
	private Boolean scale;
	/**
	 * 坐标轴分割间隔
	 */
	private Object interval;

	public Boolean scale() {
		return this.scale;
	}

	@SuppressWarnings("unchecked")
	public T scale(Boolean scale) {
		this.scale = scale;
		return (T) this;
	}

	public Object interval() {
		return this.interval;
	}

	@SuppressWarnings("unchecked")
	public T interval(Object interval) {
		this.interval = interval;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T interval(Double interval) {
		this.interval = interval;
		return (T) this;
	}

	public Integer gridIndex() {
		return this.gridIndex;
	}

	@SuppressWarnings("unchecked")
	public T gridIndex(Integer gridIndex) {
		this.gridIndex = gridIndex;
		return (T) this;
	}

	public Integer nameGap() {
		return this.nameGap;
	}

	@SuppressWarnings("unchecked")
	public T nameGap(Integer nameGap) {
		this.nameGap = nameGap;
		return (T) this;
	}

	public Boolean inverse() {
		return this.inverse;
	}

	@SuppressWarnings("unchecked")
	public T inverse(Boolean inverse) {
		this.inverse = inverse;
		return (T) this;
	}

	public Object boundaryGap() {
		return this.boundaryGap;
	}

	@SuppressWarnings("unchecked")
	public T boundaryGap(Object boundaryGap) {
		this.boundaryGap = boundaryGap;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T boundaryGap(Object[] boundaryGap) {
		this.boundaryGap = boundaryGap;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T boundaryGap(Object o1, Object o2) {
		this.boundaryGap = new Object[] { o1, o2 };
		return (T) this;
	}

	public Object min() {
		return this.min;
	}

	@SuppressWarnings("unchecked")
	public T min(Object min) {
		this.min = min;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T min(Double min) {
		this.min = min;
		return (T) this;
	}

	public Object max() {
		return this.max;
	}

	@SuppressWarnings("unchecked")
	public T max(Object max) {
		this.max = max;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T max(Double max) {
		this.max = max;
		return (T) this;
	}

	/**
	 * 设置zlevel值
	 * 
	 * @param zlevel
	 */
	@SuppressWarnings("unchecked")
	public T zlevel(Integer zlevel) {
		this.zlevel = zlevel;
		return (T) this;
	}

	/**
	 * 获取zlevel值
	 */
	public Integer zlevel() {
		return this.zlevel;
	}

	/**
	 * 设置z值
	 * 
	 * @param z
	 */
	@SuppressWarnings("unchecked")
	public T z(Integer z) {
		this.z = z;
		return (T) this;
	}

	/**
	 * 获取z值
	 */
	public Integer z() {
		return this.z;
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
	 * 获取type值
	 */
	public AxisType type() {
		return this.type;
	}

	/**
	 * 获取type值
	 */
	public AxisType getType() {
		return type;
	}

	/**
	 * 设置type值
	 * 
	 * @param type
	 */
	public void setType(AxisType type) {
		this.type = type;
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
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 设置type值
	 * 
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public T type(AxisType type) {
		this.type = type;
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
	public T position(X position) {
		this.position = position;
		return (T) this;
	}

	/**
	 * 设置position值
	 * 
	 * @param position
	 */
	@SuppressWarnings("unchecked")
	public T position(Y position) {
		this.position = position;
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
	 * 坐标轴线，默认显示，属性show控制显示与否，属性lineStyle（详见lineStyle）控制线条样式
	 * 
	 * @see com.grace.frame.util.echarts.axis.AxisLine
	 * @see com.grace.frame.util.echarts.style.LineStyle
	 */
	public AxisLine axisLine() {
		if (this.axisLine == null) {
			this.axisLine = new AxisLine();
		}
		return this.axisLine;
	}

	/**
	 * 设置axisLine值
	 * 
	 * @param axisLine
	 */
	@SuppressWarnings("unchecked")
	public T axisLine(AxisLine axisLine) {
		this.axisLine = axisLine;
		return (T) this;
	}

	/**
	 * 坐标轴小标记，默认不显示，属性show控制显示与否，属性length控制线长，属性lineStyle（详见lineStyle）控制线条样式
	 * 
	 * @see com.grace.frame.util.echarts.axis.AxisTick
	 * @see com.grace.frame.util.echarts.style.LineStyle
	 */
	public AxisTick axisTick() {
		if (this.axisTick == null) {
			this.axisTick = new AxisTick();
		}
		return this.axisTick;
	}

	/**
	 * 设置axisTick值
	 * 
	 * @param axisTick
	 */
	@SuppressWarnings("unchecked")
	public T axisTick(AxisTick axisTick) {
		this.axisTick = axisTick;
		return (T) this;
	}

	/**
	 * 坐标轴文本标签，详见axis.axisLabel
	 * 
	 * @see com.grace.frame.util.echarts.axis.AxisLabel
	 */
	public AxisLabel axisLabel() {
		if (this.axisLabel == null) {
			this.axisLabel = new AxisLabel();
		}
		return this.axisLabel;
	}

	/**
	 * @param label
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T axisLabel(AxisLabel label) {
		this.axisLabel = label;
		return (T) this;
	}

	/**
	 * 分隔线，默认显示，属性show控制显示与否，属性lineStyle（详见lineStyle）控制线条样式
	 * 
	 * @see com.grace.frame.util.echarts.axis.SplitLine
	 */
	public SplitLine splitLine() {
		if (this.splitLine == null) {
			this.splitLine = new SplitLine();
		}
		return this.splitLine;
	}

	/**
	 * 设置splitLine值
	 * 
	 * @param splitLine
	 */
	@SuppressWarnings("unchecked")
	public T splitLine(SplitLine splitLine) {
		if (this.splitLine == null) {
			this.splitLine = splitLine;
		}
		return (T) this;
	}

	/**
	 * 分隔区域，默认不显示，属性show控制显示与否，属性areaStyle（详见areaStyle）控制区域样式
	 */
	public SplitArea splitArea() {
		if (this.splitArea == null) {
			this.splitArea = new SplitArea();
		}
		return this.splitArea;
	}

	/**
	 * 设置splitArea值
	 * 
	 * @param splitArea
	 */
	@SuppressWarnings("unchecked")
	public T splitArea(SplitArea splitArea) {
		this.splitArea = splitArea;
		return (T) this;
	}

	/**
	 * 添加坐标轴的类目属性
	 * 
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T data(Object... values) {
		if (values == null || values.length == 0) {
			return (T) this;
		}
		if (this.data == null) {
			if (this.type == AxisType.category) {
				data = new ArrayList<Object>();
			} else {
				throw new RuntimeException("数据轴不能添加类目信息!");
			}
		}
		this.data.addAll(Arrays.asList(values));
		return (T) this;
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
	 * 获取axisLine值
	 */
	public AxisLine getAxisLine() {
		return axisLine;
	}

	/**
	 * 设置axisLine值
	 * 
	 * @param axisLine
	 */
	public void setAxisLine(AxisLine axisLine) {
		this.axisLine = axisLine;
	}

	/**
	 * 获取axisTick值
	 */
	public AxisTick getAxisTick() {
		return axisTick;
	}

	/**
	 * 设置axisTick值
	 * 
	 * @param axisTick
	 */
	public void setAxisTick(AxisTick axisTick) {
		this.axisTick = axisTick;
	}

	/**
	 * 获取axisLabel值
	 */
	public AxisLabel getAxisLabel() {
		return axisLabel;
	}

	/**
	 * 设置axisLabel值
	 * 
	 * @param axisLabel
	 */
	public void setAxisLabel(AxisLabel axisLabel) {
		this.axisLabel = axisLabel;
	}

	/**
	 * 获取splitLine值
	 */
	public SplitLine getSplitLine() {
		return splitLine;
	}

	/**
	 * 设置splitLine值
	 * 
	 * @param splitLine
	 */
	public void setSplitLine(SplitLine splitLine) {
		this.splitLine = splitLine;
	}

	/**
	 * 获取splitArea值
	 */
	public SplitArea getSplitArea() {
		return splitArea;
	}

	/**
	 * 设置splitArea值
	 * 
	 * @param splitArea
	 */
	public void setSplitArea(SplitArea splitArea) {
		this.splitArea = splitArea;
	}

	/**
	 * 获取zlevel值
	 */
	public Integer getZlevel() {
		return zlevel;
	}

	/**
	 * 设置zlevel值
	 * 
	 * @param zlevel
	 */
	public void setZlevel(Integer zlevel) {
		this.zlevel = zlevel;
	}

	/**
	 * 获取z值
	 */
	public Integer getZ() {
		return z;
	}

	/**
	 * 设置z值
	 * 
	 * @param z
	 */
	public void setZ(Integer z) {
		this.z = z;
	}

	public Integer getGridIndex() {
		return gridIndex;
	}

	public void setGridIndex(Integer gridIndex) {
		this.gridIndex = gridIndex;
	}

	public Integer getNameGap() {
		return nameGap;
	}

	public void setNameGap(Integer nameGap) {
		this.nameGap = nameGap;
	}

	public Boolean getInverse() {
		return inverse;
	}

	public void setInverse(Boolean inverse) {
		this.inverse = inverse;
	}

	public Object getBoundaryGap() {
		return boundaryGap;
	}

	public void setBoundaryGap(Object boundaryGap) {
		this.boundaryGap = boundaryGap;
	}

	public Object getMin() {
		return min;
	}

	public void setMin(Object min) {
		this.min = min;
	}

	public Object getMax() {
		return max;
	}

	public void setMax(Object max) {
		this.max = max;
	}

	public Boolean getScale() {
		return scale;
	}

	public void setScale(Boolean scale) {
		this.scale = scale;
	}

	public Object getInterval() {
		return interval;
	}

	public void setInterval(Object interval) {
		this.interval = interval;
	}
}
