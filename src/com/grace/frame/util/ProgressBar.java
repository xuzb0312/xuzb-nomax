package com.grace.frame.util;

import com.grace.frame.exception.AppException;

/**
 * 进度条对象
 * 
 * @author yjc
 */
public class ProgressBar{
	private String id;// 唯一ID
	private String msg;// 正在操作信息
	private int sum;// 总量
	private int currentNum;// 当前量
	private boolean canceled; // 用户是否点击了取消
	private boolean finish;// 是否已经结束
	private boolean enableAsynBar;// 开启异步bar
	private DataMap rdm4AsynBar;// 异步bar的返回数据
	private Exception ex4AsynBar;// 异步bar捕获的异常信息
	public final static String AsynBarReturnFlagStr = "<!--\r\n//enable_asynbar_0602_grace.easyFrame\r\n-->";// 异步bar时，直接返回的标识字符串

	/**
	 * 构造函数
	 */
	public ProgressBar() {
		this.id = StringUtil.getUUID();
		this.msg = "操作正在进行...";
		this.sum = 0;
		this.currentNum = 0;
		this.canceled = false;
		this.finish = false;
		this.enableAsynBar = false;
		this.rdm4AsynBar = null;
		this.ex4AsynBar = null;
	}

	/**
	 * 从biz方法或者Controller方法的para中获取ProgressBar对象
	 * <p>
	 * para只能是从前台Controller解析完成后的原始DataMap,否则无法获取。
	 * </p>
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public static ProgressBar getProgressBarFromPara(DataMap para) throws AppException {
		Object obj = para.get("__progressbar", null);
		if (obj == null) {
			return null;
		}
		return (ProgressBar) obj;
	}

	/**
	 * 获取百分比
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public int getPercent() {
		if (0 == this.sum) {
			return 100;
		}
		return (this.currentNum * 100) / this.sum;
	}

	/**
	 * 增加进度
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public synchronized void addProgress(int i) {
		if ((this.currentNum + i) <= this.sum) {
			this.currentNum += i;
		} else {
			this.currentNum = this.sum;
		}
	}

	/**
	 * 获取进度条的唯一ID
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public String getId() {
		return id;
	}

	/**
	 * 获取msg信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * 判断进度条是否已经结束
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public boolean isFinish() {
		return finish;
	}

	/**
	 * 设置msg信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public synchronized void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 设置总量
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public synchronized void setSum(int sum) {
		this.sum = sum;
	}

	/**
	 * 重置一下进度条
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-26
	 * @since V1.0
	 */
	public synchronized void resetBar() {
		this.sum = 0;
		this.currentNum = 0;
	}

	/**
	 * 判断是否取消了。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * 设置是否已经取消操作。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public synchronized void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	/**
	 * 结束进度条。在业务代码中禁止调用，业务逻辑返回时，会自动进行调用
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public synchronized void finishByFrameUse() {
		this.finish = true;
	}

	/**
	 * 是否异步bar
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public boolean isEnableAsynBar() {
		return this.enableAsynBar;
	}

	/**
	 * 启动异步bar
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public synchronized void enableAsynBar() {
		this.enableAsynBar = true;
	}

	/**
	 * 返回的业务信息
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public DataMap getRdm4AsynBar() {
		return this.rdm4AsynBar;
	}

	/**
	 * 设置业务信息
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public synchronized void setRdm4AsynBar(DataMap rdm4AsynBar) {
		this.rdm4AsynBar = rdm4AsynBar;
	}

	/**
	 * 返回的业务异常
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public Exception getEx4AsynBar() {
		return this.ex4AsynBar;
	}

	/**
	 * 设置业务异常
	 * 
	 * @author yjc
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	public synchronized void setEx4AsynBar(Exception ex4AsynBar) {
		this.ex4AsynBar = ex4AsynBar;
	}
}
