package com.grace.frame.urm.biz;

import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.ReadXmlUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TreeBuilder;

/**
 * 系统用户管理树
 * 
 * @author yjc
 */
public class SysUserMngTree extends TreeBuilder{

	/**
	 * 创建树
	 */
	public void entry(DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户Id为空");
		}

		this.sql.setSql(" select yhmc, yhlx from fw.sys_user where yhid = ? ");
		this.sql.setString(1, yhid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("根据用户ID无法获取到用户信息");
		}
		String yhlx = dsTemp.getString(0, "yhlx");
		String yhmc = dsTemp.getString(0, "yhmc");

		DataMap pdm = new DataMap();
		pdm.put("yhlx", yhlx);
		pdm.put("yhid", yhid);
		this.addNode("createChildNode", pdm, "sysuserroot", yhmc, true, "icon-computer", this.buildAttrParaDm(""));
	}

	/**
	 * 创建子节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public void createChildNode(DataMap para) throws Exception {
		String yhlx = para.getString("yhlx");
		String yhid = para.getString("yhid");
		// 超级管理员
		this.addNode(null, null, "grxxgl", "个人信息管理", false, null, null);
		if ("A".equals(yhlx)) {
			// 超级管理员
		} else if ("B".equals(yhlx)) {
			// 普通管理员
			this.addNode(null, null, "jsgl", "角色管理", false, null, null);
			this.addNode(null, null, "gnqx", "功能权限", false, null, null);
			this.addNode(null, null, "sjqx", "数据权限", false, null, null);
			this.addNode(null, null, "tsqx", "特殊权限", false, null, null);

			// 加载自定义菜单
			DataSet dsCustom = ReadXmlUtil.readXml2DataSet("userMngTreeCustom.xml");
			for (int i = 0, n = dsCustom.size(); i < n; i++) {
				String id = dsCustom.getString(i, "id");
				String name = dsCustom.getString(i, "name");
				String action = dsCustom.getString(i, "action");
				// action增加参数
				action = action + "&yhid=" + yhid + "&yhlx=" + yhlx;
				// id-防止与原有的id冲突，特殊处理
				id = "custom-" + id;
				this.addNode(null, null, id, name, false, null, this.buildAttrParaDm(action));
			}
		} else if ("C".equals(yhlx)) {
			// 服务用户
			this.addNode(null, null, "fwfwqx", "服务访问权限", false, null, null);
			this.addNode(null, null, "tsqx", "特殊权限", false, null, null);
		} else {
			// 其他情况
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
