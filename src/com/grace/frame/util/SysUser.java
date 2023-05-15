package com.grace.frame.util;

import java.io.Serializable;
import java.util.HashMap;

import com.grace.frame.exception.AppException;

/**
 * 系统的登录用户
 * 
 * @author yjc
 */
public class SysUser implements Serializable{
	private static final long serialVersionUID = 1L;
	private String yhid;// 用户id
	private String yhbh;// 用户编号
	private String yhlx;// 用户类型
	private String password;// 用户密码
	private String yhmc;// 用户名称
	private String zjlx;// 证件类型
	private String zjhm;// 证件号码
	private DataMap allInfoDM;// 个人的所有信息

	public String getYhid() {
		return yhid;
	}

	public void setYhid(String yhid) {
		this.yhid = yhid;
	}

	public String getYhbh() {
		return yhbh;
	}

	public void setYhbh(String yhbh) {
		this.yhbh = yhbh;
	}

	public String getYhmc() {
		return yhmc;
	}

	public void setYhmc(String yhmc) {
		this.yhmc = yhmc;
	}

	public String getZjlx() {
		return zjlx;
	}

	public void setZjlx(String zjlx) {
		this.zjlx = zjlx;
	}

	public String getZjhm() {
		return zjhm;
	}

	public void setZjhm(String zjhm) {
		this.zjhm = zjhm;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getYhlx() {
		return yhlx;
	}

	public void setYhlx(String yhlx) {
		this.yhlx = yhlx;
	}

	public DataMap getAllInfoDM() {
		return allInfoDM;
	}

	public void setAllInfoDM(DataMap allInfoDM) {
		this.allInfoDM = allInfoDM;
	}

	@Override
	public String toString() {
		return "SysUser [yhbh=" + yhbh + ", yhid=" + yhid + ", yhlx=" + yhlx
				+ ", yhmc=" + yhmc + ", zjhm=" + zjhm + ", zjlx=" + zjlx + "]";
	}

	/**
	 * 根据用户编号，从数据库中创建一个SysUser
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-23
	 * @since V1.0
	 */
	public static SysUser bulidSysUserByYhbh(String yhbh) throws AppException {
		if (StringUtil.chkStrNull(yhbh)) {
			throw new AppException("用户编号不能为空", "SysUser");
		}
		Sql sql = new Sql();
		sql.setSql(" select * from fw.sys_user where yhbh = ? ");// 此处直接用*代替查询所有的列,方便之后的user表信息的扩充
		sql.setString(1, yhbh);
		DataSet dsuser = sql.executeQuery();
		if (dsuser.size() <= 0) {
			return null;// 用户不存在
		}
		DataMap dmUser = dsuser.get(0);

		// 设置信息
		SysUser sysUser = new SysUser();
		sysUser.setAllInfoDM(dmUser);
		sysUser.setYhid(dmUser.getString("yhid"));
		sysUser.setYhbh(dmUser.getString("yhbh"));
		sysUser.setYhlx(dmUser.getString("yhlx"));
		sysUser.setYhmc(dmUser.getString("yhmc"));
		sysUser.setPassword(dmUser.getString("password"));
		sysUser.setZjlx(dmUser.getString("zjlx"));
		sysUser.setZjhm(dmUser.getString("zjhm"));

		return sysUser;
	}

	/**
	 * 根据dataset中的yhid列，查询出yhbh、yhmc，返回到原dataset中
	 * 
	 * @author m.yuan
	 * @date 创建时间 2015-9-24
	 * @since V1.0
	 */
	public static DataSet genYhxxDataSet(DataSet vds, String column_yhid,
			String column_yhbh, String column_yhmc) throws Exception {
		DataSet dsTemp = new DataSet();
		String yhid, yhbh, yhmc;
		HashMap<String, String[]> jdxxMap = new HashMap<String, String[]>();
		String[] jdStr;
		Sql sql = new Sql();
		int len;
		// 检验参数
		if (StringUtil.chkStrNull(column_yhid)) {
			throw new AppException("com.grace.util.CommUtil.genYhxxDataSet传入的[ID列]为空");
		}
		if (StringUtil.chkStrNull(column_yhbh)) {
			throw new AppException("com.grace.util.CommUtil.genYhxxDataSet传入的[编号列]为空");
		}
		if (StringUtil.chkStrNull(column_yhmc)) {
			throw new AppException("com.grace.util.CommUtil.genYhxxDataSet传入的[名称列]为空");
		}
		if (null == vds) {
			throw new AppException("com.grace.util.CommUtil.genYhxxDataSet传入的条件[vds]为空");
		}
		// 若传入的vds中没有数据则直接返回
		len = vds.size();
		if (vds.size() < 1) {
			return vds;
		}

		// 处理数据
		for (int j = 0; j < len; j++) {
			yhid = vds.getString(j, column_yhid);
			yhbh = yhid;
			yhmc = yhid;
			if (jdxxMap.containsKey(yhid)) {
				jdStr = jdxxMap.get(yhid);
				yhbh = jdStr[0];
				yhmc = jdStr[1];

				vds.put(j, column_yhbh, yhbh);
				vds.put(j, column_yhmc, yhmc);
			} else {
				sql.setSql("select yhid, yhbh, yhmc from fw.sys_user where yhid = ? ");
				sql.setString(1, yhid);
				dsTemp = sql.executeQuery();
				if (dsTemp.size() > 0) {
					yhbh = dsTemp.getString(0, "yhbh");
					yhmc = dsTemp.getString(0, "yhmc");
				}
				vds.put(j, column_yhbh, yhbh);
				vds.put(j, column_yhmc, yhmc);

				jdStr = new String[] { yhbh, yhmc };
				jdxxMap.put(yhid, jdStr);
			}
		}

		return vds;
	}
}
