package com.grace.frame.util;

/**
 * 调试时使用的方便工具类
 * 
 * @author yjc
 */
public class Debugger{
	private long start_time;// 用于统计记录起始时间，方便统计程序运行时间

	public Debugger() {
		this.start_time = System.currentTimeMillis();
	}

	/**
	 * 打印执行时间
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-12
	 * @since V1.0
	 */
	public void logRunTime() {
		this.logRunTime(null);
	}

	/**
	 * 打印执行时间
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-12
	 * @since V1.0
	 */
	public void logRunTime(String tipMsg) {
		this.logRunTime(tipMsg, null);
	}

	/**
	 * 打印执行时间
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-12
	 * @since V1.0
	 */
	public void logRunTime(String tipMsg, Object obj) {
		long now = System.currentTimeMillis();
		long runTime = now - this.start_time;
		this.start_time = now;
		System.err.println("运行时间："
				+ runTime
				+ (StringUtil.chkStrNull(tipMsg) ? "ms" : ("ms ----->" + tipMsg)));
		if (obj == null) {
			return;
		}
		try {
			if (obj instanceof DataMap) {
				System.out.println(((DataMap) obj).toJsonString(1));
			} else if (obj instanceof DataSet) {
				System.out.println(((DataSet) obj).toJsonString(1));
			} else {
				System.out.println(obj.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
