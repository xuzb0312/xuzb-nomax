package com.grace.frame.util;

import java.util.HashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * code操作工具类
 * 
 * @author yjc
 */
public class CodeUtil{
	/**
	 * 对于code代码进行解析，通过code获取到content的值，如果没有则返回默认值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static String discode(String dmbh, String code, String defalut) throws AppException {
		if (StringUtil.chkStrNull(dmbh)) {
			throw new AppException("传入的代码编号为空", "CodeUtil");
		}
		if (StringUtil.chkStrNull(code)) {
			return "";
		}
		dmbh = dmbh.toUpperCase();
		code = code.toUpperCase();
		if (GlobalVars.CODE_MAP.containsKey(dmbh)) {
			HashMap<String, Object[]> mapTemp = GlobalVars.CODE_MAP.get(dmbh);
			if (mapTemp.containsKey(code)) {
				return (String) mapTemp.get(code)[0];
			} else {
				return defalut;
			}
		} else {
			return defalut;
		}
	}

	/**
	 * 对于code代码进行解析，通过code获取到content的值，如果没有则抛出异常
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static String discode(String dmbh, String code) throws AppException {
		if (StringUtil.chkStrNull(dmbh)) {
			throw new AppException("传入的代码编号为空", "CodeUtil");
		}
		if (StringUtil.chkStrNull(code)) {
			return "";
		}
		dmbh = dmbh.toUpperCase();
		code = code.toUpperCase();
		if (GlobalVars.CODE_MAP.containsKey(dmbh)) {
			HashMap<String, Object[]> mapTemp = GlobalVars.CODE_MAP.get(dmbh);
			if (mapTemp.containsKey(code)) {
				return (String) mapTemp.get(code)[0];
			} else {
				throw new AppException("根据DMBH=" + dmbh + ",CODE=" + code
						+ "没有找到对应的代码含义");
			}
		} else {
			throw new AppException("根据DMBH=" + dmbh + ",CODE=" + code
					+ "没有找到对应的代码含义");
		}
	}

	/**
	 * 对于code代码进行解析，通过code获取到content的值，如果没有则返回原代码值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static String discode2Code(String dmbh, String code) throws AppException {
		return CodeUtil.discode(dmbh, code, code);
	}

	/**
	 * 对于多值中间使用逗号间隔的code解析
	 * 
	 * @author yjc
	 * @date 创建时间 2020-2-20
	 * @since V1.0
	 */
	public static String discodes(String dmbh, String codes) throws AppException {
		if (StringUtil.chkStrNull(codes)) {
			return "";
		}
		String[] arrCode = codes.split(",");
		StringBuffer codeBF = new StringBuffer();
		for (int i = 0, n = arrCode.length; i < n; i++) {
			codeBF.append(CodeUtil.discode2Code(dmbh, arrCode[i])).append(",");
		}
		codeBF.setLength(codeBF.length() - 1);
		return codeBF.toString();
	}

	/**
	 * 根据代码content获取code;查询不到抛出异常
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static String getCodeByContent(String dmbh, String content) throws AppException {
		if (StringUtil.chkStrNull(dmbh)) {
			throw new AppException("传入的代码编号为空", "CodeUtil");
		}
		if (StringUtil.chkStrNull(content)) {
			throw new AppException("传入的代码含义为空", "CodeUtil");
		}
		dmbh = dmbh.toUpperCase();
		if (GlobalVars.CODE_MAP.containsKey(dmbh)) {
			HashMap<String, Object[]> mapTemp = GlobalVars.CODE_MAP.get(dmbh);
			Object[] arrKey = mapTemp.keySet().toArray();
			for (int i = 0, n = arrKey.length; i < n; i++) {
				String key = (String) arrKey[i];
				if (content.equals(mapTemp.get(key)[0])) {
					return key;
				}
			}
			throw new AppException("系统根据DMBH=" + dmbh + "和CONTENT=" + content
					+ "在系统中未找到代码信息");
		} else {
			throw new AppException("系统未配置DMBH=" + dmbh + "的代码信息");
		}
	}

	/**
	 * 根据代码content获取code;查询不到返回默认值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static String getCodeByContent(String dmbh, String content,
			String defalut) throws AppException {
		if (StringUtil.chkStrNull(dmbh)) {
			throw new AppException("传入的代码编号为空", "CodeUtil");
		}
		if (StringUtil.chkStrNull(content)) {
			return "";
		}
		dmbh = dmbh.toUpperCase();
		if (GlobalVars.CODE_MAP.containsKey(dmbh)) {
			HashMap<String, Object[]> mapTemp = GlobalVars.CODE_MAP.get(dmbh);
			Object[] arrKey = mapTemp.keySet().toArray();
			for (int i = 0, n = arrKey.length; i < n; i++) {
				String key = (String) arrKey[i];
				if (content.equals(mapTemp.get(key)[0])) {
					return key;
				}
			}
			return defalut;
		} else {
			return defalut;
		}
	}

	/**
	 * 转换ds中的code
	 * 
	 * @author m.yuan
	 * @date 创建时间 2016年6月3日
	 * @since V1.0
	 */
	public static DataSet parseCode4Ds(DataSet ds, String dmbh, String key,
			String putKey) throws Exception {
		DataSet dsCode = CodeUtil.getDsByDmbh(dmbh);
		if (ds.size() > 0) {
			for (int i = 0, len = ds.size(); i < len; i++) {
				String code = ds.getString(i, key);
				if (StringUtil.chkStrNull(code)) {
					continue;
				}
				for (int j = 0, gzdjLen = dsCode.size(); j < gzdjLen; j++) {
					if (code.equals(dsCode.getString(j, "code"))) {
						ds.put(i, putKey, dsCode.getString(j, "content"));
					}
				}
			}
		}
		return ds;
	}

	/**
	 * 根据dmbh获取ds,并按照xh进行排序,并进行开始和结尾的数据筛选
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static DataSet getDsByDmbh(String dmbh, String prefix, String suffix) throws AppException {
		if (StringUtil.chkStrNull(dmbh)) {
			throw new AppException("传入的代码编号为空", "CodeUtil");
		}
		String[] arrPrefix = {};
		String[] arrSuffix = {};
		if (!StringUtil.chkStrNull(prefix)) {
			arrPrefix = prefix.split(",");
		}
		if (!StringUtil.chkStrNull(suffix)) {
			arrSuffix = suffix.split(",");
		}

		dmbh = dmbh.toUpperCase();
		if (GlobalVars.CODE_MAP.containsKey(dmbh)) {
			DataSet ds = new DataSet();
			HashMap<String, Object[]> mapTemp = GlobalVars.CODE_MAP.get(dmbh);
			Object[] arrKey = mapTemp.keySet().toArray();
			for (int i = 0, n = arrKey.length; i < n; i++) {
				String key = (String) arrKey[i];
				Object[] arrValue = mapTemp.get(key);

				// 前缀判断
				boolean isRetain = false;// 该项是否保留
				if (arrPrefix.length <= 0) {
					isRetain = true;
				}
				for (int j = 0, m = arrPrefix.length; j < m; j++) {
					if (StringUtil.chkStrNull(arrPrefix[j])) {
						isRetain = true;// 为空的保留
						break;
					}
					if (key.startsWith(arrPrefix[j])) {
						isRetain = true;// 依此开头的保留
						break;
					}
				}

				// 如果不是保留，则不再增加
				if (!isRetain) {
					continue;
				}

				// 后缀判断
				isRetain = false;
				if (arrSuffix.length <= 0) {
					isRetain = true;
				}
				for (int j = 0, m = arrSuffix.length; j < m; j++) {
					if (StringUtil.chkStrNull(arrSuffix[j])) {
						isRetain = true;// 为空的保留
						break;
					}
					if (key.endsWith(arrSuffix[j])) {
						isRetain = true;// 依此开头的保留
						break;
					}
				}

				// 如果不是保留，则不再增加
				if (!isRetain) {
					continue;
				}

				ds.addRow();
				ds.put(ds.size() - 1, "code", key);
				ds.put(ds.size() - 1, "content", arrValue[0]);
				ds.put(ds.size() - 1, "xh", arrValue[1]);
			}
			ds = ds.sort("code");// mod.yjc.2015年9月22日 先按照code排序。
			ds = ds.sort("xh");
			return ds;
		} else {
			throw new AppException("系统中不存在DMBH=" + dmbh + "的代码信息");
		}
	}

	/**
	 * 根据dmbh获取ds,并按照xh进行排序-并进行开始和结尾的数据筛选
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static DataSet getDsByDmbh(String dmbh) throws AppException {
		return CodeUtil.getDsByDmbh(dmbh, null, null);
	}

}
