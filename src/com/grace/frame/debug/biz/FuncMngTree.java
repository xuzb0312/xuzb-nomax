package com.grace.frame.debug.biz;

import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TreeBuilder;

/**
 * 权限管理配置tree
 * 
 * @author yjc
 */
public class FuncMngTree extends TreeBuilder{
	/**
	 * 构建tree的entry
	 */
	public void entry(DataMap para) throws Exception {
		String ywlyid = para.getString("ywlyid");
		if (StringUtil.chkStrNull(ywlyid)) {
			throw new BizException("传入的业务领域ID为空");
		}
		this.sql.setSql(" select gnmc from fw.func_doc where gnid = ? and gnlx = 'A' ");
		this.sql.setString(1, ywlyid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("系统中不存在该业务领域的doc信息");
		}
		String gnmc = dsTemp.getString(0, "gnmc");

		DataMap pdm = new DataMap();
		pdm.put("fgn", ywlyid);
		this.addNode("createChildrenTree", pdm, ywlyid, gnmc, true, "icon-computer", null);
	}

	/**
	 * 创建子树
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public void createChildrenTree(DataMap para) throws Exception {
		String fgn = para.getString("fgn");
		if (StringUtil.chkStrNull(fgn)) {
			throw new BizException("传入FGN为空");
		}

		this.sql.setSql(" select gnid, gnmc from fw.func_doc where fgn = ? ");
		this.sql.setString(1, fgn);
		DataSet ds = this.sql.executeQuery();
		ds.sort("gnid");
		for (int i = 0, n = ds.size(); i < n; i++) {
			String gnid = ds.getString(i, "gnid");
			String gnmc = ds.getString(i, "gnmc");

			DataMap pdm = new DataMap();
			pdm.put("fgn", gnid);
			this.addNode("createChildrenTree", pdm, gnid, gnmc, false, null, null);// 增加节点
		}
	}
}
