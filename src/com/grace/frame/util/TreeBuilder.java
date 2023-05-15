package com.grace.frame.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.workflow.Biz;

/**
 * 树的构造类，均需要继承此类来实现。
 * <p>
 * 其实这是一个森林，可以存在多个根
 * </p>
 * 
 * @author yjc
 */
public abstract class TreeBuilder extends Biz{

	/**
	 * int id;// 树的唯一id <br>
	 * String text;// 展示的文本 <br>
	 * String iconCls;// 图标<br>
	 * String state;// 状态，是展开的还是收起的。<br>
	 * ArrayList<HashMap<String, Object>> children;// 子节点list <br>
	 * DataMap attributes;// 属性<br>
	 *注：由于前台的json数据对大小写敏感，所以此处使用ArrayList<map>存储数据，保留大小写特征；
	 */
	private ArrayList<HashMap<String, Object>> treedataList = new ArrayList<HashMap<String, Object>>();
	private int maxid = 1;// 唯一的树id,最大

	/**
	 * 树的构造入口方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	public DataMap initTree(DataMap para) throws Exception {
		// 子类实现的树构造函数
		this.entry(para);
		JSONArray jarr = JSONArray.fromObject(this.treedataList);
		DataMap dm = new DataMap();
		dm.put("__treejsondata", jarr.toString());
		return dm;
	}

	/**
	 * 获取tree的jsonData数据
	 * 
	 * @author yjc
	 * @date 创建时间 2016-2-18
	 * @since V1.0
	 */
	public String getTreeJsonData() {
		JSONArray jarr = JSONArray.fromObject(this.treedataList);
		return jarr.toString();
	}

	/**
	 * treeDataList获取--非必须，不建议使用。
	 * 
	 * @author yjc
	 * @date 创建时间 2016-2-18
	 * @since V1.0
	 */
	protected ArrayList<HashMap<String, Object>> getTreedataList() {
		return treedataList;
	}

	/**
	 * treeDataList设置--非必须，不建议使用。
	 * 
	 * @author yjc
	 * @date 创建时间 2016-2-18
	 * @since V1.0
	 */
	protected void setTreedataList(
			ArrayList<HashMap<String, Object>> treedataList) {
		this.treedataList = treedataList;
	}

	/**
	 * 数据的构造入口库函数；继承的类需要重写此方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	public abstract void entry(DataMap para) throws Exception;

	/**
	 * 新增节点
	 * 
	 * @param childrenMethod 子类方法，为空说明没有子节点；
	 * @param paras 传递的参数
	 * @param nodeText 节点文字展示
	 * @param isopen 节点是展开还是收起；
	 * @param iconCls 节点样式
	 * @param attr 其他属性
	 * @param checked 对与CheckBox=true的有效
	 * @return id 返回唯一的id
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	protected final HashMap<String, Object> addNode(String childrenMethod,
			DataMap para, String id, String text, boolean isopen,
			String iconCls, boolean checked, DataMap attr) throws Exception {
		if (StringUtil.chkStrNull(text)) {
			throw new AppException("传入的节点展示名称为空");
		}
		if (StringUtil.chkStrNull(id)) {
			id = String.valueOf(this.maxid++);
		}

		// 组装tree节点数据
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("text", text);
		if (!StringUtil.chkStrNull(iconCls)) {
			map.put("iconCls", iconCls);
		}
		if (null != attr && attr.size() > 0) {// 不是空时，将其加入
			map.put("attributes", attr);
		}
		if (checked) {
			map.put("checked", checked);
		}

		// 是否有子类方法
		if (!StringUtil.chkStrNull(childrenMethod)) {
			// 创建子类的datalist
			ArrayList<HashMap<String, Object>> chidrenDataList = new ArrayList<HashMap<String, Object>>();
			ArrayList<HashMap<String, Object>> treedataListTemp = this.treedataList;// 缓存类数据list
			this.treedataList = chidrenDataList;// 将类datalist指向子节点datalist

			// 调用子类方法创建节点
			try {
				Class<?>[] parad = { DataMap.class };
				Method method = this.getClass()
					.getMethod(childrenMethod, parad);
				method.invoke(this, para);// 反射调用
			} catch (InvocationTargetException ie) {// 目的是将反射调用的异常原始异常，取出来。
				Throwable cause = ie.getCause();
				if (cause instanceof BizException) {
					throw (Exception) cause;
				} else {
					throw (Exception) ie.getTargetException();
				}
			}
			// 如果子节点的数量不是零，则增加children
			if (chidrenDataList.size() > 0) {
				if (!isopen) {// easyuitree-默认是展开的-对于叶子节点，不设置此属性。设置也不生效。
					map.put("state", "closed");
				}
				map.put("children", chidrenDataList);// 先放入map中；
			}

			// 将类datalist再转回本层
			this.treedataList = treedataListTemp;
		}
		this.treedataList.add(map);// 将节点放入list

		return map;
	}

	/**
	 * 增加节点-重载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	protected final HashMap<String, Object> addNode(String childrenMethod,
			DataMap para, String id, String text, boolean isopen,
			String iconCls, DataMap attr) throws Exception {
		return this.addNode(childrenMethod, para, id, text, isopen, iconCls, false, attr);
	}

	/**
	 * 增加节点重载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-26
	 * @since V1.0
	 */
	protected final HashMap<String, Object> addNode(String childrenMethod,
			DataMap para, String text) throws Exception {
		return this.addNode(childrenMethod, para, null, text, false, null, null);
	}

	/**
	 * 增加节点重载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-26
	 * @since V1.0
	 */
	protected final HashMap<String, Object> addNode(String childrenMethod,
			DataMap para, String id, String text) throws Exception {
		return this.addNode(childrenMethod, para, id, text, false, null, null);
	}
}
