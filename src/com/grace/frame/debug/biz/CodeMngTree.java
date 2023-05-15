package com.grace.frame.debug.biz;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.TreeBuilder;

/**
 * Tree管理
 * 
 * @author yjc
 */
public class CodeMngTree extends TreeBuilder{

	/**
	 * 创建codeTree
	 */
	public void entry(DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		if (null == dmbh) {
			dmbh = "%";
		} else {
			dmbh = "%" + dmbh.trim() + "%";
		}
		dmbh = dmbh.toUpperCase();

		this.sql.setSql(" select dmbh, dmmc from fw.code_list where dmbh like ? or dmmc like ? ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, dmbh);
		DataSet ds = this.sql.executeQuery();
		ds = ds.sort("dmbh");
		for (int i = 0, n = ds.size(); i < n; i++) {
			String dmbhT = ds.getString(i, "dmbh");
			String dmmcT = ds.getString(i, "dmmc");
			this.addNode(null, null, dmbhT, dmmcT, true, "icon-bullet-star", null);
		}
	}
}
