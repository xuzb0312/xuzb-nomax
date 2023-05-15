package com.grace.frame.util.echarts.json;

import net.sf.json.JSONObject;

import com.grace.frame.util.echarts.Option;

/**
 * 增强的Option - 主要用于测试、演示
 * 
 * @author yjc
 */
public class EchartsOption extends Option{

	private static final long serialVersionUID = 4802633668839558338L;

	/**
	 * 在浏览器中查看
	 */
	public void view() {
		OptionUtil.browse(this);
	}

	/**
	 * 获取toString值
	 */
	@Override
	public String toString() {
		return GsonUtil.format(this);
	}

	/**
	 * 获取toPrettyString值
	 */
	public String toPrettyString() {
		return GsonUtil.prettyFormat(this);
	}

	/**
	 * jsonString
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-24
	 * @since V1.0
	 */
	public String toJsonString() {
		return this.toString();
	}

	/**
	 * 转换为jsonObject
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-24
	 * @since V1.0
	 */
	public JSONObject toJSONObject() {
		return JSONObject.fromObject(this.toString());
	}

	/**
	 * 导出到指定文件名
	 * 
	 * @param fileName
	 * @return 返回html路径
	 */
	public String exportToHtml(String fileName) {
		return exportToHtml(System.getProperty("java.io.tmpdir"), fileName);
	}

	/**
	 * 导出到指定文件名
	 * 
	 * @param fileName
	 * @return 返回html路径
	 */
	public String exportToHtml(String filePath, String fileName) {
		return OptionUtil.exportToHtml(this, filePath, fileName);
	}
}
