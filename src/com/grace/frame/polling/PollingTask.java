package com.grace.frame.polling;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.SysLogUtil;
import com.grace.frame.util.SysUser;
import com.grace.frame.workflow.DelegatorUtil;

/**
 * 轮询任务
 * 
 * @author yjc
 */
public class PollingTask extends TimerTask{
	/**
	 * 轮询服务程序
	 */
	@Override
	public void run() {
		try {
			Date nowTime = DateUtil.getDBTime();// 获取到当前时间
			String nowStr = DateUtil.dateToString(nowTime, "yyyy-MM-dd HH:mm:ss");
			System.out.println("***轮询服务程序执行中:" + nowStr);
			SysLogUtil.logInfo(PollingTask.class, "***轮询服务程序执行中:" + nowStr);

			this.pollingRun(nowTime);

			System.out.println("***执行结束***");
			SysLogUtil.logInfo(PollingTask.class, "***执行结束***");
		} catch (Exception e) {
			System.err.println("轮询服务程序执行过程中发生异常:" + e.getMessage());
			e.printStackTrace();
			SysLogUtil.logError(PollingTask.class, "轮询服务程序执行过程中发生异常:"
					+ e.getMessage(), e);
		}

	}

	/**
	 * 轮询服务程序执行过程
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-17
	 * @since V1.0
	 */
	private void pollingRun(Date nowTime) throws Exception {
		// 如果轮询配置信息为空或者没有数据则直接取消掉轮询监控
		if (!GlobalVars.IS_START_POLLING
				|| null == GlobalVars.POLLING_CONFIG_DS
				|| 0 == GlobalVars.POLLING_CONFIG_DS.size()) {
			this.cancel();
		}

		// 对于多服务器，轮询程序同时执行的情况，只对数据库管控最大号与当前业务服务器一致的进行轮询，其他的返回，不进行操作
		Sql sql = new Sql();
		sql.setSql(" select maxnum from fw.polling_mng where appid = ? ");
		sql.setString(1, GlobalVars.APP_ID);
		DataSet dsTemp = sql.executeQuery();
		int now_polling_mng_maxnum = dsTemp.getInt(0, "maxnum");
		if (now_polling_mng_maxnum != GlobalVars.POLLING_MNG_MAXNUM) {
			return;// 程序不会结束轮询遍历，只是不再进行业务配置的轮询操作。
		}
		Calendar cl = Calendar.getInstance();
		cl.setTime(nowTime);
		int nowHour = cl.get(Calendar.HOUR_OF_DAY);

		// 循环操作
		DataSet dsPolling = GlobalVars.POLLING_CONFIG_DS;
		for (int i = 0, n = dsPolling.size(); i < n; i++) {
			String lxmc, lxbiz, lxff, lxcs;
			int qssj, zzsj, sjjg;
			Date zhzxsj;
			int nowHourTemp;

			lxmc = dsPolling.getString(i, "lxmc");// 轮询名称
			lxbiz = dsPolling.getString(i, "lxbiz");// 轮询biz
			lxff = dsPolling.getString(i, "lxff");// 轮询方法
			lxcs = dsPolling.getString(i, "lxcs");// 轮询参数

			qssj = dsPolling.getInt(i, "qssj");// 起始时间
			zzsj = dsPolling.getInt(i, "zzsj");// 终止时间
			sjjg = dsPolling.getInt(i, "sjjg");// 时间间隔

			zhzxsj = dsPolling.getDate(i, "zhzxsj");// 最后执行时间

			// 开始判断执行时机是否达到
			nowHourTemp = nowHour;
			if (qssj > zzsj) {
				qssj = qssj - 24;
				nowHourTemp = nowHourTemp - 24;
			}
			if (qssj <= nowHourTemp && zzsj >= nowHourTemp) {// 执行时间范围达到
				// 查看时间间隔是否达到
				Calendar clTemp = Calendar.getInstance();
				clTemp.setTime(nowTime);
				clTemp.add(Calendar.SECOND, 10);// 保证等于1分钟的情况下，正常轮询（解决出现1分钟误差的bug）
				clTemp.add(Calendar.MINUTE, -sjjg);
				if (clTemp.getTime().after(zhzxsj)) {// 时间间隔也达到条件
					// 执行任务
					System.out.println("***开始执行" + lxmc + "...");
					try {
						DataMap dmPara = new DataMap();
						// 轮询biz参数
						SysUser pollingUser = new SysUser();
						pollingUser.setYhid("polling");
						pollingUser.setYhbh("polling");
						pollingUser.setYhmc("轮询服务程序");

						dmPara.put("__jbjgid", "00000000");
						dmPara.put("__jbjgqxfw", "00000000");
						dmPara.put("__yhid", "");
						dmPara.put("__ip", "127.0.0.1");
						dmPara.put("__sysuser", pollingUser);
						dmPara.put("__request", null);
						dmPara.put("__response", null);

						// 轮询业务参数
						dmPara.put("lxcs", lxcs);
						DelegatorUtil.execute(lxbiz, lxff, dmPara);
					} catch (Exception e) {
						e.printStackTrace();
						SysLogUtil.logError(PollingTask.class, "执行任务" + lxmc
								+ "失败[data=" + dsPolling.get(i).toString()
								+ "]", e);
					}
					dsPolling.put(i, "zhzxsj", nowTime);
				}
			}

		}
	}
}
