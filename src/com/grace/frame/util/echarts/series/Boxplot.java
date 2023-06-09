
package com.grace.frame.util.echarts.series;

import com.grace.frame.util.echarts.code.Orient;
import com.grace.frame.util.echarts.code.SeriesType;
import com.grace.frame.util.echarts.style.ItemStyle;

/**
 * 『箱形图』、『盒须图』、『盒式图』、『盒状图』、『箱线图』
 *
 * @author yjc
 */
public class Boxplot extends Series<Boxplot> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2890320317366041521L;
	/**
     * 布局方式
     */
    private Orient layout;
    /**
     * box 的宽度的上下限。数组的意思是：[min, max]
     */
    private Object[] boxWidth;
    /**
     * boxplot 图形样式，有 normal 和 emphasis 两个状态，normal 是图形正常的样式，emphasis 是图形高亮的样式，比如鼠标悬浮或者图例联动高亮的时候会使用 emphasis 作为图形的样式
     */
    private ItemStyle itemStyle;

    /**
     * 构造函数
     */
    public Boxplot() {
        this.type(SeriesType.boxplot);
    }

    /**
     * 构造函数,参数:name
     *
     * @param name
     */
    public Boxplot(String name) {
        super(name);
        this.type(SeriesType.boxplot);
    }

    public Orient layout() {
        return this.layout;
    }

    public Boxplot layout(Orient layout) {
        this.layout = layout;
        return this;
    }

    public Object[] boxWidth() {
        return this.boxWidth;
    }

    public Boxplot boxWidth(Object[] boxWidth) {
        this.boxWidth = boxWidth;
        return this;
    }

    public Boxplot boxWidth(Object min, Object max) {
        this.boxWidth = new Object[]{min, max};
        return this;
    }

    public ItemStyle itemStyle() {
        if (this.itemStyle == null) {
            this.itemStyle = new ItemStyle();
        }
        return this.itemStyle;
    }

    public Boxplot itemStyle(ItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }

    public Orient getLayout() {
        return layout;
    }

    public void setLayout(Orient layout) {
        this.layout = layout;
    }

    public Object[] getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(Object[] boxWidth) {
        this.boxWidth = boxWidth;
    }

    @Override
    public ItemStyle getItemStyle() {
        return itemStyle;
    }

    @Override
    public void setItemStyle(ItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }
}
