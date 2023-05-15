
package com.grace.frame.util.echarts.axis;

import com.grace.frame.util.echarts.code.AxisType;

/**
 * 类目轴
 *
 * @author yjc
 */
public class CategoryAxis extends Axis<CategoryAxis> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3520206173923196047L;

	/**
     * 构造函数
     */
    public CategoryAxis() {
        this.type(AxisType.category);
    }

}
