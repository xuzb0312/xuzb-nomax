
package com.grace.frame.util.echarts.series;

import com.grace.frame.util.echarts.code.SeriesType;

/**
 * 平行坐标系
 *
 * @author yjc
 */
public class Parallel extends Series<Parallel> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1644516652038360177L;
	/**
     * 使用的平行坐标系的 index，在单个图表实例中存在多个平行坐标系的时候有用
     */
    private Integer parallelIndex;
    /**
     * 框选时，未被选中的条线会设置成这个『透明度』（从而可以达到变暗的效果）
     */
    private Double inactiveOpacity;
    /**
     * 框选时，选中的条线会设置成这个『透明度』（从而可以达到高亮的效果）
     */
    private Double activeOpacity;

    /**
     * 构造函数
     */
    public Parallel() {
        this.type(SeriesType.parallel);
    }

    /**
     * 构造函数,参数:name
     *
     * @param name
     */
    public Parallel(String name) {
        super(name);
        this.type(SeriesType.parallel);
    }

    public Integer parallelIndex() {
        return this.parallelIndex;
    }

    public Parallel parallelIndex(Integer parallelIndex) {
        this.parallelIndex = parallelIndex;
        return this;
    }

    public Double inactiveOpacity() {
        return this.inactiveOpacity;
    }

    public Parallel inactiveOpacity(Double inactiveOpacity) {
        this.inactiveOpacity = inactiveOpacity;
        return this;
    }

    public Double activeOpacity() {
        return this.activeOpacity;
    }

    public Parallel activeOpacity(Double activeOpacity) {
        this.activeOpacity = activeOpacity;
        return this;
    }

    public Integer getParallelIndex() {
        return parallelIndex;
    }

    public void setParallelIndex(Integer parallelIndex) {
        this.parallelIndex = parallelIndex;
    }

    public Double getInactiveOpacity() {
        return inactiveOpacity;
    }

    public void setInactiveOpacity(Double inactiveOpacity) {
        this.inactiveOpacity = inactiveOpacity;
    }

    public Double getActiveOpacity() {
        return activeOpacity;
    }

    public void setActiveOpacity(Double activeOpacity) {
        this.activeOpacity = activeOpacity;
    }
}
