package com.grace.frame.urm.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.SeqUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 考生管理Biz
 * 
 * @author xzb
 */
public class StdUserMngBiz extends Biz {
    /**
     * 选择考生
     *
     * @author yjc
     * @date 创建时间 2015-8-7
     * @since V1.0
     */
    public final DataMap fwdChooseStdUser(final DataMap para) throws Exception {
        String xm = para.getString("xm");
        StringBuffer sqlBF = new StringBuffer();
        if (StringUtil.chkStrNull(xm)) {
            xm = "%";
        } else {
            xm = "%" + xm + "%";
        }
        sqlBF.setLength(0);
        sqlBF.append(" select ryid, xm, xmpy, xb, yxzjlx, ");
        sqlBF.append("        yxzjhm, nl, lxdh, sjhm, jtzz, ");
        sqlBF.append("        round(bzje,2) bzje, rzrq, to_char(xxgxsj, 'yyyymmddhh24miss') xxgxsj ");
        sqlBF.append("   from fw.std_info a ");
        sqlBF.append("  where a.xm like ? ");
        sqlBF.append("     or a.xmpy like ? ");
        sqlBF.append("     or a.yxzjhm like ? ");

        this.sql.setSql(sqlBF.toString());
        this.sql.setString(1, xm);
        this.sql.setString(2, xm);
        this.sql.setString(3, xm);
        DataSet stdUser = this.sql.executeQuery();

        stdUser.sort("xm");// 增加默认排序
            // 获取经办机构mcDs
        this.sql.setSql(" select jbjgid code, jbjgmc content from fw.sys_agency ");
        DataSet dsJbjg = this.sql.executeQuery();

        DataMap dm = new DataMap();
        //将信息显示到查询页面
        dm.put("stduser", stdUser);
        dm.put("dsjbjg", dsJbjg);
        return dm;
    }


    /**
     * 服务访问权限管理
     *
     * @author yjc
     * @date 创建时间 2015-8-10
     * @since V1.0
     */
    public final DataMap fwdStdUserFwfwqxMng(final DataMap para) throws Exception {
        String ryid = para.getString("ryid");
        if (StringUtil.chkStrNull(ryid)) {
            throw new BizException("传入的用户ID为空");
        }
        DataMap rdm = this.queryeStdUserFwfwqx(para);
        rdm.put("ryid", ryid);
        return rdm;
    }
    /**
     * 查询用户的服务访问权限信息
     *
     * @author yjc
     * @date 创建时间 2015-8-10
     * @since V1.0
     */
    public final DataMap queryeStdUserFwfwqx(final DataMap para) throws Exception {
        String ryid = para.getString("ryid");
        if (StringUtil.chkStrNull(ryid)) {
            throw new BizException("传入的用户ID为空");
        }

        // 可以进入该页面的均为普通业务用户
        StringBuffer sqlBF = new StringBuffer();
        sqlBF.setLength(0);
        sqlBF.append(" select a.fwmc, a.fwff, a.biz, a.bizff ");
        sqlBF.append("   from fw.service_config a, fw.service_right b ");
        sqlBF.append("  where a.fwmc = b.fwmc ");
        sqlBF.append("    and a.fwff = b.fwff ");
        sqlBF.append("    and a.dbid = ? ");
        sqlBF.append("    and b.yhid = ? ");

        this.sql.setSql(sqlBF.toString());
        this.sql.setString(1, GlobalVars.SYS_DBID);
        this.sql.setString(2, ryid);
        DataSet dsFwfwqx = this.sql.executeQuery();

        DataMap rdm = new DataMap();
        rdm.put("fwfwqxinfo", dsFwfwqx);
        return rdm;
    }


    /**
     * 进入考生新增
     *  暂时没用
     * @author yjc
     * @date 创建时间 2015-8-7
     * @since V1.0
     */
    public final DataMap fwdStdUserAdd(final DataMap para) throws Exception {
        DataMap dm = new DataMap();
        return dm;
    }
    /**
     * 保存用户新增。
     *
     * @author yjc
     * @date 创建时间 2015-8-7
     * @since V1.0
     */
    public final DataMap saveStdUserAdd(final DataMap para) throws Exception {
        String xm = para.getString("xm");// 考生姓名
        String xb = para.getString("xb");// 考生性别
        String yxzjlx = para.getString("yxzjlx");// 有效证件类型
        String yxzjhm = para.getString("yxzjhm");// 有效证件号码
        Integer nl = Integer.valueOf(para.getString("nl"));// 年龄
        String lxdh = para.getString("lxdh");// 联系电话
        String sjhm = para.getString("sjhm");// 手机号码
        Double bzje = Double.valueOf(para.getString("bzje"));// 补助金额
        String rzrq = para.getString("rzrq");// 入职日期
        String jtzz = para.getString("jtzz");// 入职日期
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String xxgxsj = formatter.format(date);//信息更新时间
        if (StringUtil.chkStrNull(xm)) {
            throw new BizException("考生姓名为空");
        }

        if (StringUtil.chkStrNull(xb)) {
            throw new BizException("性别为空");
        }
        if (StringUtil.chkStrNull(yxzjhm)) {
            throw new BizException("证件号码为空");
        }
        String xmpy = StringUtil.getPy(xm);
        if (StringUtil.chkStrNull(xmpy)) {
            throw new BizException("姓名拼音为空");
        }
        // 检查用户证件号是否已经存在了
        this.sql.setSql(" select yxzjhm from fw.std_info where yxzjhm = ? ");
        this.sql.setString(1, yxzjhm);
        DataSet dsTemp = this.sql.executeQuery();
        if (dsTemp.size() > 0) {
            throw new BizException("该考生已经存在无法再次新增");
        }

        String ryid = SeqUtil.getId("fw.sq_ryid");// 考生ID  使用序列
        StringBuffer sqlBF = new StringBuffer();
        sqlBF.append(" insert into fw.std_info ");
        sqlBF.append("   (ryid, xm, xmpy, xb, yxzjlx, ");
        sqlBF.append("    yxzjhm, nl, lxdh, sjhm, jtzz, ");
        sqlBF.append("    bzje, rzrq, xxgxsj)");
        sqlBF.append(" values ");
        sqlBF.append("   (?, ?, ?, ?, ?, ");
        sqlBF.append("    ?, ?, ?, ?, ?, ");
        sqlBF.append("    ?,?, ?) ");

        this.sql.setSql(sqlBF.toString());
        this.sql.setString(1, ryid);
        this.sql.setString(2, xm);
        this.sql.setString(3, xmpy);
        this.sql.setString(4, xb);
        this.sql.setString(5, yxzjlx);

        this.sql.setString(6, yxzjhm);
        this.sql.setInt(7, nl);
        this.sql.setString(8, lxdh);
        this.sql.setString(9, sjhm);
        this.sql.setString(10, jtzz);

        this.sql.setDouble(11, bzje);
        this.sql.setString(12, rzrq);
        this.sql.setDate(13, date);
        this.sql.executeUpdate();


        // 记录日志
//        this.log("SYS-A-YHXZ", "考生新增", "A", yhid, "新增用户ID:" + yhid + ",用户编号:"
//                + yhbh + ",用户名称:" + yhmc, "yhid=" + yhid + ",yhbh=" + yhbh
//                + ",yhmc=" + yhmc + ",yhlx:" + yhlx + ",sjqxpz:" + sjqxpz);

        DataMap rdm = new DataMap();
        rdm.put("ryid", ryid);
        rdm.put("xmpy", xmpy);
        rdm.put("yxzjhm", yxzjhm);
        return rdm;
    }

    /**
     * 用户信息管理页面
     *
     * @author yjc
     * @date 创建时间 2015-8-10
     * @since V1.0
     */
    public final DataMap fwdStdUserGrxxglMng(final DataMap para) throws Exception {
        String ryid = para.getString("ryid");
        if (StringUtil.chkStrNull(ryid)) {
            throw new BizException("传入的考生ID为空");
        }

        StringBuffer sqlBF = new StringBuffer();
        sqlBF.append(" select ryid, xm, xmpy, xb, yxzjlx, ");
        sqlBF.append("        yxzjhm, nl, lxdh, sjhm, jtzz, ");
        sqlBF.append("        round(bzje,2) bzje, rzrq, to_char(xxgxsj, 'yyyymmddhh24miss') xxgxsj ");// add.yjc.2017年4月30日-服务的验证方式调整，使用签名的方式，该处密文密码为提供给使用放的密钥
        sqlBF.append("   from fw.std_info ");
        sqlBF.append("  where ryid = ? ");

        this.sql.setSql(sqlBF.toString());
        this.sql.setString(1, ryid);
        DataSet stdUser = this.sql.executeQuery();
        if (stdUser.size() <= 0) {
            throw new BizException("用户ID为" + ryid + "的用户在系统中不存在。");
        }

        DataMap dmUser = stdUser.getRow(0);


        DataMap rdm = new DataMap();
        rdm.put("userinfo", dmUser);
        return rdm;
    }
    /**
     * 考生信息修改
     *
     * @author yjc
     * @date 创建时间 2015-8-10
     * @since V1.0
     */
    public final DataMap fwdStdUserInfoModify(final DataMap para) throws Exception {
        String ryid = para.getString("ryid");
        if (StringUtil.chkStrNull(ryid)) {
            throw new BizException("传入的用户ID为空");
        }

        StringBuffer sqlBF = new StringBuffer();
        sqlBF.append(" select ryid, xm, xmpy, xb, yxzjlx, ");
        sqlBF.append("        yxzjhm, nl, lxdh, sjhm, jtzz, ");
        sqlBF.append("        bzje, rzrq, xxgxsj ");// add.yjc.2017年4月30日-服务的验证方式调整，使用签名的方式，该处密文密码为提供给使用放的密钥
        sqlBF.append("   from fw.std_info ");
        sqlBF.append("  where ryid = ? ");
        this.sql.setSql(sqlBF.toString());
        this.sql.setString(1, ryid);
        DataSet dsUser = this.sql.executeQuery();
        if (dsUser.size() <= 0) {
            throw new BizException("用户ID为" + ryid + "的用户在系统中不存在。");
        }

        // 是否可以更改用户类别-如果为超级管理员，则可以更改，否则不可更
//        boolean ggyhlx = true;
//        if ("A".equals(this.getSysUser().getYhlx())) {
//            ggyhlx = false;
//        }

        DataMap rdm = new DataMap();
        rdm.put("userinfo", dsUser.getRow(0));
//        rdm.put("ggyhlx", ggyhlx);
        return rdm;
    }

    /**
     * 保存用户信息修改
     *
     * @author yjc
     * @date 创建时间 2015-8-10
     * @since V1.0
     */
    public final DataMap saveStdUserModify(final DataMap para) throws Exception {
        String ryid = para.getString("ryid");// 用户ID
        String xm = para.getString("xm");// 用户编号
        String xmpy = para.getString("xmpy");// 用户类型
        String xb = para.getString("xb");// 用户名称
        Integer nl = Integer.valueOf(para.getString("nl"));// 证件类型
        String yxzjlx = para.getString("yxzjlx");// 证件号码
        String yxzjhm = para.getString("yxzjhm");// 所属经办机构
        String jtzz = para.getString("jtzz");// 所属机构名称
        Double bzje = Double.valueOf(para.getString("bzje"));// 所属机构部门
        String lxdh = para.getString("lxdh");
        String sjhm = para.getString("sjhm");
        String rzrq = para.getString("rzrq");// 是否同时配置数据权限

        if (StringUtil.chkStrNull(ryid)) {
            throw new BizException("考生ID为空");
        }
        if (StringUtil.chkStrNull(xm)) {
            throw new BizException("考生姓名为空");
        }
        if (StringUtil.chkStrNull(yxzjhm)) {
            throw new BizException("有效证件号码为空");
        }

        // 检查用户编号是否已经存在了
        this.sql.setSql(" select ryid from fw.std_info where yxzjhm = ? and ryid <> ? ");
        this.sql.setString(1, yxzjhm);
        this.sql.setString(2, ryid);
        DataSet dsTemp = this.sql.executeQuery();
        if (dsTemp.size() > 0) {
            throw new BizException("该用户编号已经存在无法再次新增");
        }

        // 密码加密
        StringBuffer sqlBF = new StringBuffer();
        sqlBF.append(" update fw.std_info ");
        sqlBF.append("    set xm     = ?, ");
        sqlBF.append("        xmpy     = ?, ");
        sqlBF.append("        xb     = ?, ");
        sqlBF.append("        nl     = ?, ");
        sqlBF.append("        yxzjlx     = ?, ");
        sqlBF.append("        yxzjhm     = ?, ");
        sqlBF.append("        jtzz = ?, ");
        sqlBF.append("        bzje   = ?, ");
        sqlBF.append("        lxdh   = ?, ");
        sqlBF.append("        sjhm   = ?, ");
        sqlBF.append("        rzrq   = ?, ");
        sqlBF.append("        xxgxsj   = ? ");
        sqlBF.append("  where ryid = ? ");

        this.sql.setSql(sqlBF.toString());
        this.sql.setString(1, xm);
        this.sql.setString(2, xmpy);
        this.sql.setString(3, xb);
        this.sql.setInt(4, nl);
        this.sql.setString(5, yxzjlx);

        this.sql.setString(6, yxzjhm);
        this.sql.setString(7, jtzz);
        this.sql.setDouble(8, bzje);
        this.sql.setString(9, lxdh);
        this.sql.setString(10, sjhm);
        this.sql.setString(11, rzrq);
        Date date = new Date(System.currentTimeMillis());
        this.sql.setDate(12, date);
        this.sql.setString(13, ryid);


        this.sql.executeUpdate();


        return null;
    }


    /**
     * 删除考生
     *
     * @author yjc
     * @date 创建时间 2015-8-10
     * @since V1.0
     */
    public final DataMap saveStdUserDel(final DataMap para) throws Exception {
        String ryid = para.getString("ryid");
        if (StringUtil.chkStrNull(ryid)) {
            throw new BizException("传入的考生ID为空");
        }



        this.sql.setSql(" delete from fw.std_info  where ryid = ? ");
        this.sql.setString(1, ryid);
        this.sql.executeUpdate();

        return null;
    }


}