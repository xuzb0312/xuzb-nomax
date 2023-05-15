package com.grace.frame.util;

import java.util.HashMap;
import java.util.Stack;

import net.sf.json.JSONArray;

import com.grace.frame.exception.AppException;

/**
 * 根据规则进行过滤的过滤器实现
 * 
 * @author yjc
 */
public class DataSetFilterRules implements DataSetFilter{
	private String rules;// 规则
	private DataMap rulesMap;// 解析后的规则map

	/**
	 * filterRules规则：<br>
	 * (column type value) and/or (column type value)<br>
	 * ()：组划分（可循环嵌套）一个()中限定一个条件使用and/or拼接。 <br>
	 * 最外层无需进行组限定。<br>
	 * column:列名<br>
	 * type:<小于,<=小于登录,=等于,<>不等于,>=大于等于,>大于,like包含,in存在其中。<br>
	 * value:对于int,double等直接写入值，对于string请使用''包含。只允许数字类型和string类型比较，in使用[]限定项目<br>
	 * and/or:组运算关系，同一层级只允许一个运算关系<br>
	 * 例如：<br>
	 * (grxl in ['10', '20']) and (nl <= 32)。<br>
	 * 标识过滤：个人学历为10或20且年龄小于等于32的数据 </p>
	 * 
	 * @param rules
	 * @throws AppException
	 */
	public DataSetFilterRules(String rules) throws AppException {
		if (StringUtil.chkStrNull(rules)) {
			throw new AppException("传入的规则串为空");
		}
		this.rules = rules.trim();
		// 对最外层进行组限定和对于or和and的处理
		this.rules = this.rules.replace("and", ")and(");
		this.rules = this.rules.replace("or", ")or(");
		this.rules = "((" + this.rules + "))";
		this.initRulesMap();// 初始化规则
	}

	/**
	 * 初始化规则map
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	private void initRulesMap() throws AppException {
		Stack<DataMap> indexStack = new Stack<DataMap>();// 栈
		indexStack.push(new DataMap());
		this.loopParse(0, indexStack);
		if (indexStack.size() != 1) {
			throw new AppException("传入的过滤规则格式不正确");
		}
		this.rulesMap = indexStack.pop().getDataSet("subrule").getRow(0);// 获取规则
	}

	/**
	 * 递归循环操作
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-5
	 * @since V1.0
	 */
	private int loopParse(int index, Stack<DataMap> indexStack) throws AppException {
		boolean breakFllag = true;// 跳出标志
		int preRuleEndIndex = -1;// 上一个条件的结尾

		for (int n = this.rules.length(); index < n;) {
			char c = this.rules.charAt(index);
			if ('(' == c) {
				// 判断条件or、and
				if (preRuleEndIndex >= 0) {
					String opt = this.rules.substring(preRuleEndIndex, index);
					if (opt.toLowerCase().contains("or")) {
						indexStack.peek().add("opt", "or");
					} else if (opt.toLowerCase().contains("and")) {
						String yOpt = indexStack.peek().getString("opt", "and");
						if ("and".equalsIgnoreCase(yOpt)) {
							indexStack.peek().add("opt", "and");
						}
					}
				}
				DataMap dmRule = new DataMap().add("start", index);
				indexStack.push(dmRule);// 入栈
				index++;
				index = this.loopParse(index, indexStack);// 下一个支付
				breakFllag = false;
			} else if (')' == c) {// 出栈操作
				// 出栈类似操作
				if (breakFllag) {
					break;
				}
				DataMap dmRule = indexStack.pop();
				dmRule.put("end", index);
				int start = dmRule.getInt("start");
				String singleRules = this.rules.substring(start + 1, index);
				singleRules = singleRules.trim();

				DataSet dsRules = indexStack.peek()
					.getDataSet("subrule", new DataSet());
				if (singleRules.contains("(") && singleRules.contains(")")) {
					dsRules.add(dmRule);
				} else {
					dmRule.add("rule", singleRules);
					// 解析运算：<小于,<=小于登录,=等于,<>不等于,>=大于等于,>大于,like包含,in存在其中
					if (singleRules.contains("<=")) {
						this.parseSimpleRuleStr(singleRules, "<=", dmRule);
					} else if (singleRules.contains(">=")) {
						this.parseSimpleRuleStr(singleRules, ">=", dmRule);
					} else if (singleRules.contains("<>")) {
						this.parseSimpleRuleStr(singleRules, "<>", dmRule);
					} else if (singleRules.contains("<")) {
						this.parseSimpleRuleStr(singleRules, "<", dmRule);
					} else if (singleRules.contains(">")) {
						this.parseSimpleRuleStr(singleRules, ">", dmRule);
					} else if (singleRules.contains("=")) {
						this.parseSimpleRuleStr(singleRules, "=", dmRule);
					} else if (singleRules.contains(" in ")) {
						String[] arrTemp = singleRules.split(" in ");
						if (arrTemp.length != 2) {
							throw new AppException("格过滤规则格式不正确");
						}
						String value = arrTemp[1].trim();
						JSONArray arry = JSONArray.fromObject(value);
						HashMap<Object, Object> mapValue = new HashMap<Object, Object>();
						String type = "string";
						if (arry.size() > 0) {
							Object obj = arry.get(0);
							if (obj instanceof String) {
								type = "string";
							} else if (obj instanceof Integer) {
								type = "int";
							} else if (obj instanceof Double) {
								type = "double";
							} else {
								throw new AppException("格过滤规则格式不正确[in数据类型无法解析]");
							}
						}
						for (Object obj : arry) {
							mapValue.put(obj, null);
						}
						dmRule.put("col", arrTemp[0].trim());
						dmRule.put("opt", "in");
						dmRule.put("value", mapValue);
						dmRule.put("type", type);
					} else if (singleRules.contains(" like ")) {
						this.parseSimpleRuleStr(singleRules, " like ", dmRule);
					} else {
						throw new AppException("格过滤规则格式不正确");
					}
					dsRules.addRow(dmRule);
				}
				indexStack.peek().add("subrule", dsRules);
				index++;
				breakFllag = true;
				preRuleEndIndex = index;
			} else {
				index++;
			}
		}
		return index;
	}

	/**
	 * 解析简单规则操作
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-5
	 * @since V1.0
	 */
	private void parseSimpleRuleStr(String singleRules, String opt,
			DataMap dmRule) throws AppException {
		String[] arrTemp = singleRules.split(opt);
		if (arrTemp.length != 2) {
			throw new AppException("格过滤规则格式不正确");
		}
		Object objValue;
		String value = arrTemp[1].trim();
		String type;
		if (value.contains("'")) {
			objValue = value.substring(1, value.length() - 1);
			type = "string";
		} else if (value.contains(".")) {
			objValue = Double.parseDouble(value);
			type = "double";
		} else {
			objValue = Integer.parseInt(value);
			type = "int";
		}
		dmRule.put("col", arrTemp[0].trim());
		dmRule.put("opt", opt.trim());
		dmRule.put("value", objValue);
		dmRule.put("type", type);
	}

	/**
	 * 获取规则字符串
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-5
	 * @since V1.0
	 */
	public String getRulesString() throws AppException {
		return this.parseRulesMap2String(this.rulesMap);
	}

	/**
	 * 规则字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-8
	 * @since V1.0
	 */
	@Override
	public String toString() {
		try {
			return this.getRulesString();
		} catch (AppException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 转JsonString
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-8
	 * @since V1.0
	 */
	public String toJsonString(int indentFactor) throws AppException {
		return this.rulesMap.toJsonString(indentFactor);
	}

	/**
	 * 解析规则
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-5
	 * @since V1.0
	 */
	private String parseRulesMap2String(DataMap mapRule) throws AppException {
		if (mapRule.containsKey("rule")) {
			String rule = mapRule.getString("rule");
			return "(" + rule + ")";
		} else if (mapRule.containsKey("subrule")) {
			DataSet dsRule = mapRule.getDataSet("subrule");
			String opt = mapRule.getString("opt", "and");
			StringBuffer ruleBF = new StringBuffer();
			ruleBF.append("(");
			for (int i = 0, n = dsRule.size(); i < n; i++) {
				DataMap oneRule = dsRule.get(i);
				ruleBF.append(this.parseRulesMap2String(oneRule));
				if (i < n - 1) {
					ruleBF.append(" ").append(opt).append(" ");
				}
			}
			ruleBF.append(")");
			return ruleBF.toString();
		} else {
			return "";
		}
	}

	/**
	 * 递归筛选
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-5
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	private boolean filterFunc(DataMap para, DataMap mapRule) throws AppException {
		if (mapRule.containsKey("rule")) {
			String col = mapRule.getString("col");
			String opt = mapRule.getString("opt");
			Object value = mapRule.get("value");
			String type = mapRule.getString("type");
			Object targetValue = para.get(col);// 目标值
			// <小于,<=小于登录,=等于,<>不等于,>=大于等于,>大于,like包含,in存在其中
			if ("string".equals(type)) {
				// 字符串类型比较
				String strTargetValue = (String) targetValue;
				if (strTargetValue == null) {
					strTargetValue = "";
				}
				if ("in".equals(opt)) {
					HashMap<Object, Object> mapValue = (HashMap<Object, Object>) value;
					return mapValue.containsKey(strTargetValue);
				} else {
					String strValue = (String) value;
					if ("<".equals(opt)) {
						return strTargetValue.compareTo(strValue) < 0;
					} else if ("<=".equals(opt)) {
						return strTargetValue.compareTo(strValue) <= 0;
					} else if (">".equals(opt)) {
						return strTargetValue.compareTo(strValue) > 0;
					} else if (">=".equals(opt)) {
						return strTargetValue.compareTo(strValue) >= 0;
					} else if ("=".equals(opt)) {
						return strTargetValue.equals(strValue);
					} else if ("<>".equals(opt)) {
						return !strTargetValue.equals(strValue);
					} else if ("like".equals(opt)) {
						return strTargetValue.contains(strValue);
					} else {
						throw new AppException("格过滤规则格式不正确[opt出错]");
					}
				}
			} else if ("double".equals(type)) {
				// 浮点型数据比较
				double dTargetValue = (Double) targetValue;

				if ("in".equals(opt)) {
					HashMap<Object, Object> mapValue = (HashMap<Object, Object>) value;
					return mapValue.containsKey(dTargetValue);
				} else {
					double dValue = (Double) value;
					if ("<".equals(opt)) {
						return dTargetValue < dValue;
					} else if ("<=".equals(opt)) {
						return dTargetValue <= dValue;
					} else if (">".equals(opt)) {
						return dTargetValue > dValue;
					} else if (">=".equals(opt)) {
						return dTargetValue >= dValue;
					} else if ("=".equals(opt)) {
						return dTargetValue == dValue;
					} else if ("<>".equals(opt)) {
						return dTargetValue != dValue;
					} else if ("like".equals(opt)) {
						throw new AppException("格过滤规则格式不正确[double类型无法使用like运算符]");
					} else {
						throw new AppException("格过滤规则格式不正确[opt出错]");
					}
				}
			} else if ("int".equals(type)) {
				// 整型数据比较
				int iTargetValue = (Integer) targetValue;
				if ("in".equals(opt)) {
					HashMap<Object, Object> mapValue = (HashMap<Object, Object>) value;
					return mapValue.containsKey(iTargetValue);
				} else {
					int iValue = (Integer) value;
					if ("<".equals(opt)) {
						return iTargetValue < iValue;
					} else if ("<=".equals(opt)) {
						return iTargetValue <= iValue;
					} else if (">".equals(opt)) {
						return iTargetValue > iValue;
					} else if (">=".equals(opt)) {
						return iTargetValue >= iValue;
					} else if ("=".equals(opt)) {
						return iTargetValue == iValue;
					} else if ("<>".equals(opt)) {
						return iTargetValue != iValue;
					} else if ("like".equals(opt)) {
						throw new AppException("格过滤规则格式不正确[int类型无法使用like运算符]");
					} else {
						throw new AppException("格过滤规则格式不正确[opt出错]");
					}
				}
			} else {
				throw new AppException("格过滤规则格式不正确[type出错]");
			}
		} else if (mapRule.containsKey("subrule")) {
			DataSet dsRule = mapRule.getDataSet("subrule");
			String opt = mapRule.getString("opt", "and");
			if ("or".equals(opt)) {
				boolean tSubResult = false;
				for (int i = 0, n = dsRule.size(); i < n; i++) {
					DataMap oneRule = dsRule.get(i);
					boolean subResult = this.filterFunc(para, oneRule);
					if (tSubResult || subResult) {
						return true;
					}
				}
				return false;
			} else {
				boolean tSubResult = true;
				for (int i = 0, n = dsRule.size(); i < n; i++) {
					DataMap oneRule = dsRule.get(i);
					boolean subResult = this.filterFunc(para, oneRule);
					tSubResult = tSubResult && subResult;
					if (!tSubResult) {
						return tSubResult;
					}
				}
				return true;
			}
		} else {
			throw new AppException("格过滤规则格式不正确[关键字查找出错]");
		}
	}

	/**
	 * 过滤操作
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	@Override
	public boolean filter(DataMap para) throws Exception {
		return this.filterFunc(para, this.rulesMap);
	}
}
