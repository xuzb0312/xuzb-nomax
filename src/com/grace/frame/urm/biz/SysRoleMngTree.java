package com.grace.frame.urm.biz;

import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.ReadXmlUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TreeBuilder;

public class SysRoleMngTree extends TreeBuilder{

	/**
	 * 创建树
	 */
	public void entry(DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("传入的角色Id为空");
		}

		this.sql.setSql(" select jsmc from fw.sys_role where jsid = ? ");
		this.sql.setString(1, jsid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("根据角色ID无法获取到角色信息");
		}
		String jsmc = dsTemp.getString(0, "jsmc");

		DataMap pdm = new DataMap();
		pdm.put("jsid", jsid);
		this.addNode("createChildNode", pdm, "sysroleroot", jsmc, true, "icon-computer", this.buildAttrParaDm(""));
	}

	/**
	 * 创建子节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public void createChildNode(DataMap para) throws Exception {
		String jsid = para.getString("jsid");

		// 超级管理员
		this.addNode(null, null, "jsxxgl", "角色信息管理", false, null, null);
		this.addNode(null, null, "gnqx", "功能权限", false, null, null);
		// 加载自定义菜单
		DataSet dsCustom = ReadXmlUtil.readXml2DataSet("roleMngTreeCustom.xml");
		for (int i = 0, n = dsCustom.size(); i < n; i++) {
			String id = dsCustom.getString(i, "id");
			String name = dsCustom.getString(i, "name");
			String action = dsCustom.getString(i, "action");
			// action增加参数
			action = action + "&jsid=" + jsid;
			// id-防止与原有的id冲突，特殊处理
			id = "custom-" + id;
			this.addNode(null, null, id, name, false, null, this.buildAttrParaDm(action));
		}
		this.addNode(null, null, "czrz", "操作日志", false, null, null);
	}

	/**
	 * 构建参数dm
	 * 
	 * @author wd
	 * @date 创建时间 2016-1-5
	 * @since V1.0
	 */
	private DataMap buildAttrParaDm(String url) {
		DataMap attDm = new DataMap();
		attDm.put("gnsj", url);
		return attDm;
	}
}