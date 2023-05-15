package com.grace.frame.debug.biz;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.TreeBuilder;

/**
 * 典型批准查询树
 * 
 * @author yjc
 */
public class NoteTree extends TreeBuilder{
	/**
	 * 创建noteTree
	 */
	public void entry(DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		if (null == pzbh) {
			pzbh = "%";
		} else {
			pzbh = "%" + pzbh.trim() + "%";
		}
		pzbh = pzbh.toUpperCase();

		this.sql.setSql(" select pzbh, pzmc from fw.note_list where upper(pzbh) like ? or pzmc like ? ");
		this.sql.setString(1, pzbh);
		this.sql.setString(2, pzbh);
		DataSet ds = this.sql.executeQuery();
		ds = ds.sort("pzbh");
		for (int i = 0, n = ds.size(); i < n; i++) {
			String pzbhT = ds.getString(i, "pzbh");
			String pzmcT = ds.getString(i, "pzmc");
			this.addNode(null, null, pzbhT, pzmcT + "[" + pzbhT + "]", true, "icon-bullet-star", null);
		}
	}
}
