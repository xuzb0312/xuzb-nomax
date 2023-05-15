
package com.grace.frame.util.echarts.series;

import com.grace.frame.util.echarts.code.DataFilter;
import com.grace.frame.util.echarts.code.SeriesType;
import com.grace.frame.util.echarts.style.AreaStyle;

/**
 * @author yjc
 */
public class Line extends Series<Line> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6056234498887804557L;
	/**
     * 平滑曲线
     */
    private Boolean smooth;
    /**
     * ECharts 会在折线图的数据数量大于实际显示的像素宽度（高度）的时候会启用优化，对显示在一个像素宽度内的数据做筛选，该选项是指明数据筛选的策略
     *
     * @see DataFilter
     */
    private Object dataFilter;
    /**
     * 区域填充样式。
     */
    private AreaStyle areaStyle;

    /**
     * 构造函数
     */
    public Line() {
        this.type(SeriesType.line);
    }

    /**
     * 构造函数,参数:name
     *
     * @param name
     */
    public Line(String name) {
        super(name);
        this.type(SeriesType.line);
    }

    /**
     * 获取dataFilter值
     */
    public Object dataFilter() {
        return this.dataFilter;
    }

    /**
     * 设置dataFilter值
     *
     * @param dataFilter
     */
    public Line dataFilter(Object dataFilter) {
        this.dataFilter = dataFilter;
        return this;
    }

    /**
     * 设置dataFilter值
     *
     * @param dataFilter
     */
    public Line dataFilter(DataFilter dataFilter) {
        this.dataFilter = dataFilter;
        return this;
    }

    /**
     * 获取smooth值
     */
    public Boolean smooth() {
        return this.smooth;
    }

    /**
     * 设置smooth值
     *
     * @param smooth
     */
    public Line smooth(Boolean smooth) {
        this.smooth = smooth;
        return this;
    }

    /**
     * 获取smooth值
     */
    public Boolean getSmooth() {
        return smooth;
    }

    /**
     * 设置smooth值
     *
     * @param smooth
     */
    public void setSmooth(Boolean smooth) {
        this.smooth = smooth;
    }

    /**
     * 获取dataFilter值
     */
    public Object getDataFilter() {
        return dataFilter;
    }

    /**
     * 设置dataFilter值
     *
     * @param dataFilter
     */
    public void setDataFilter(Object dataFilter) {
        this.dataFilter = dataFilter;
    }


    /**
     * 新建并返回areaStyle
     *
     * @return
     */
    public AreaStyle areaStyle() {
        if (this.areaStyle == null) {
            this.areaStyle = new AreaStyle();
        }
        return this.areaStyle;
    }

    /**
     * 设置areaStyle
     *
     * @param areaStyle
     * @return
     */
    public Line areaStyle(AreaStyle areaStyle) {
        this.areaStyle = areaStyle;
        return this;
    }

    /**
     * 设置areaStyle
     *
     * @param areaStyle
     */
    public void setAreaStyle(AreaStyle areaStyle) {
        this.areaStyle = areaStyle;
    }

    /**
     * 获取areaStyle
     *
     * @return
     */
    public AreaStyle getAreaStyle() {
        return areaStyle;
    }
}
