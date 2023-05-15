package com.grace.frame.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

import com.grace.frame.exception.BizException;

/**
 * 字段验证类，主要提供给DataMap.getString使用，用于验证字段具体实际类型
 * 
 * @author yjc
 */
public abstract class FieldValidation{
	/**
	 * 验证方法
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public abstract String validate(String value, String keyName) throws BizException;

	/**
	 * 检测是否为空
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation NotEmpty() {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (StringUtil.chkStrNull(value)) {
					throw new BizException("【" + keyName + "】信息项不允许为空，请检查！");
				}
				return value;
			}
		};
	}

	/**
	 * 默认规则
	 * <p>
	 * 对xss字符等进行过滤
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation Default() {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (null == value) {
					return value;
				}
				value = HtmlUtils.htmlEscape(value);
				value = value.replace("&bull;", "•")
					.replace("%3C", "&lt;")
					.replace("%3E", "&gt;")
					.replace("(", "（")
					.replace(")", "）")
					.replace("%28", "（")
					.replace("%29", "）");
				return value;
			}
		};
	}

	/**
	 * 把html代码全部转为text
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation Html2Text() {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (null == value) {
					return value;
				}
				return StringUtil.html2Text(value);
			}
		};
	}

	/**
	 * 把html代码内的script标签进行转换
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation ScriptHtml2SaftText() {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (null == value) {
					return value;
				}
				return StringUtil.scriptHtml2SaftText(value);
			}
		};
	}

	/**
	 * 把html代码内的script标签进行转换
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation HtmlEncode() {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (null == value) {
					return value;
				}
				return StringUtil.htmlEncode(value);
			}
		};
	}

	/**
	 * 过滤sql危险字符
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation FilterSqlStr() {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (null == value) {
					return value;
				}
				return StringUtil.filterSqlStr(value);
			}
		};
	}

	/**
	 * 正则表达式验证
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation Pattern(final String pattern) {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (StringUtil.chkStrNull(value)) {
					return value;
				}
				if (!Pattern.matches(pattern, value)) {
					throw new BizException("【" + keyName + "】信息项不符合规则验证要求，请检查！");
				}
				return value;
			}
		};
	}

	/**
	 * 正在表达式过滤
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static FieldValidation Filter(final String pattern) {
		return new FieldValidation(){
			@Override
			public String validate(String value, String keyName) throws BizException {
				if (StringUtil.chkStrNull(value)) {
					return value;
				}
				Pattern sqlPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
				Matcher matcher = sqlPattern.matcher(value);
				value = matcher.replaceAll("").trim();
				return value;
			}
		};
	}
}
