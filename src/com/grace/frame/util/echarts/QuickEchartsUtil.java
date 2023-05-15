package com.grace.frame.util.echarts;

import com.grace.frame.exception.BizException;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.echarts.axis.CategoryAxis;
import com.grace.frame.util.echarts.axis.ValueAxis;
import com.grace.frame.util.echarts.code.Orient;
import com.grace.frame.util.echarts.code.Tool;
import com.grace.frame.util.echarts.code.Trigger;
import com.grace.frame.util.echarts.code.X;
import com.grace.frame.util.echarts.code.Y;
import com.grace.frame.util.echarts.data.Data;
import com.grace.frame.util.echarts.json.EchartsOption;
import com.grace.frame.util.echarts.series.Bar;
import com.grace.frame.util.echarts.series.Line;
import com.grace.frame.util.echarts.series.Pie;

/**
 * 对于一些简单、常用的echarts进行构建
 * 
 * @author yjc
 */
public class QuickEchartsUtil{
	/**
	 * 创建饼图--快速<br>
	 * title:标题，subtitle,二级标题 arrItemName :元素名称，arrItemValue:饼图元素值集合 <br>
	 * dm.put("test", QuickEchartsUtil.buildPie("测试", "测试", new String[] { "测试",
	 * "测试2" }, new Object[] { 100, 20 }));
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2017-8-24
	 * @since V1.0
	 */
	public static EchartsOption buildPie(String title, String subTitle,
			String[] arrItemName, Object[] arrItemValue) throws BizException {
		if (StringUtil.chkStrNull(title)) {
			title = "";
		}
		if (StringUtil.chkStrNull(subTitle)) {
			subTitle = "";
		}
		if (arrItemName == null) {
			throw new BizException("arrItemName参数不允许为空");
		}
		if (arrItemValue == null) {
			throw new BizException("arrItemValue参数不允许为空");
		}
		if (arrItemName.length != arrItemValue.length) {
			throw new BizException("arrItemName参数长度与arrItemValue参数长度不一致");
		}

		EchartsOption option = new EchartsOption();
		option.title().text(title).subtext(subTitle).x(X.left).y(Y.top);
		option.tooltip().show(true).formatter("{b} : {c} ({d}%)");
		option.legend()
			.orient(Orient.horizontal)
			.left(X.center)
			.top(Y.bottom)
			.data((Object[]) arrItemName);
		option.toolbox().show(true).feature(Tool.restore);

		// 数据处理
		Object[] valueData = new Object[arrItemName.length];
		for (int i = 0, n = arrItemName.length; i < n; i++) {
			valueData[i] = new Data(arrItemName[i], arrItemValue[i]);
		}
		Pie pie = new Pie();
		pie.data(valueData);
		option.series(pie);
		return option;
	}

	/**
	 * 创建折线图--快速<br>
	 * title:标题，subtitle,二级标题，xUnit:X轴单位，yUnit：Y轴单位，xData:x轴展示数据元素集合，arrItemName
	 * :折线元素名称，arrItemValue:折线元素值集合<br>
	 * dm.put("test", QuickEchartsUtil.buildLine("测试", "呵呵", "", "人", new
	 * Object[] { "2017年8月22日", "2017年8月23日", "2017年8月24日" }, new String[] {
	 * "报名成功", "报名失败" }, new Object[] { new Object[] { 100, 250, 200 }, new
	 * Object[] { 600, 500, 900 } }));
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2017-8-24
	 * @since V1.0
	 */
	public static EchartsOption buildLine(String title, String subTitle,
			String xUnit, String yUnit, Object[] xData, String[] arrItemName,
			Object[] arrItemValue) throws BizException {
		EchartsOption option = new EchartsOption();
		option.title().text(title).subtext(subTitle).x(X.center).y(Y.top);
		option.toolbox().show(true).feature(Tool.restore);
		option.legend()
			.orient(Orient.horizontal)
			.left(X.center)
			.top(Y.bottom)
			.data((Object[]) arrItemName);
		option.tooltip().trigger(Trigger.axis);
		option.calculable(true);

		// y轴数据
		ValueAxis valueAxis = new ValueAxis();
		valueAxis.axisLabel().formatter("{value}" + yUnit);
		option.yAxis(valueAxis);

		// x轴数据
		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.axisLine().onZero(false);
		categoryAxis.axisLabel().formatter("{value}" + xUnit);
		categoryAxis.boundaryGap(false);
		categoryAxis.data(xData);
		option.xAxis(categoryAxis);

		for (int i = 0, n = arrItemValue.length; i < n; i++) {
			Line line = new Line();
			line.smooth(true)
				.name(arrItemName[i])
				.data((Object[]) arrItemValue[i]);
			option.series(line);
		}

		return option;
	}

	/**
	 * 创建柱状图--快速<br>
	 * title:标题，subtitle,二级标题，xUnit:X轴单位，yUnit：Y轴单位，xData:x轴展示数据元素集合，arrItemName
	 * :元素名称，arrItemValue:元素值集合<br>
	 * dm.put("test", QuickEchartsUtil.buildBar("测试", "呵呵", "", "人", new
	 * Object[] { "2017年8月22日", "2017年8月23日", "2017年8月24日" }, new String[] {
	 * "报名成功", "报名失败" }, new Object[] { new Object[] { 100, 250, 200 }, new
	 * Object[] { 600, 500, 900 } }));
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2017-8-24
	 * @since V1.0
	 */
	public static EchartsOption buildBar(String title, String subTitle,
			String xUnit, String yUnit, Object[] xData, String[] arrItemName,
			Object[] arrItemValue) throws BizException {
		EchartsOption option = new EchartsOption();
		option.title().text(title).subtext(subTitle).x(X.center).y(Y.top);
		option.toolbox().show(true).feature(Tool.restore);
		option.legend()
			.orient(Orient.horizontal)
			.left(X.center)
			.top(Y.bottom)
			.data((Object[]) arrItemName);
		option.tooltip().trigger(Trigger.axis);
		option.calculable(true);

		// y轴数据
		ValueAxis valueAxis = new ValueAxis();
		valueAxis.axisLabel().formatter("{value}" + yUnit);
		option.yAxis(valueAxis);

		// x轴数据
		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.axisLine().onZero(false);
		categoryAxis.axisLabel().formatter("{value}" + xUnit);
		categoryAxis.boundaryGap(true);
		categoryAxis.data(xData);
		option.xAxis(categoryAxis);

		for (int i = 0, n = arrItemValue.length; i < n; i++) {
			Bar bar = new Bar();
			bar.name(arrItemName[i]).data((Object[]) arrItemValue[i]);
			option.series(bar);
		}

		return option;
	}
}
