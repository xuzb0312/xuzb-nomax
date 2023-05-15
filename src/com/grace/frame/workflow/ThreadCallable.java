package com.grace.frame.workflow;

import java.util.concurrent.Callable;

import com.grace.frame.exception.AppException;
import com.grace.frame.hibernate.TransManager;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.ProgressBar;

public class ThreadCallable implements Callable<DataMap>{
	private String bizName;
	private String methodName;
	private DataMap para;
	private DataMap bizPara;
	TransManager tmg;
	private ProgressBar progressBar = null;// 进度条对象

	/**
	 * 构造函数
	 */
	public ThreadCallable(String bizName, String methodName, DataMap para,
		DataMap bizPara) {
		super();
		this.bizName = bizName;
		this.methodName = methodName;
		this.para = para;
		this.bizPara = bizPara;
		this.tmg = new TransManager();
		try {// 进度条对象操作
			if (this.bizPara.containsKey("progressbar")) {
				this.progressBar = (ProgressBar) this.bizPara.get("progressbar");
				this.bizPara.remove("progressbar");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用方法
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-22
	 * @since V1.0
	 */
	public DataMap call() throws Exception {
		try {
			this.tmg.begin();
			DataMap rdm = DelegatorUtil.executeFunc(this.bizName, this.methodName, this.para, this.bizPara);
			this.tmg.commit();

			if (null != this.progressBar) {
				if (this.progressBar.isEnableAsynBar()) {
					this.progressBar.setRdm4AsynBar(rdm);
					this.progressBar.finishByFrameUse();
				}
			}
			return rdm;
		} catch (Exception e) {
			try {
				this.tmg.rollback();
			} catch (AppException op) {
				e.printStackTrace();
			}
			if (null != this.progressBar) {
				if (this.progressBar.isEnableAsynBar()) {
					this.progressBar.setEx4AsynBar(e);
					this.progressBar.finishByFrameUse();
				}
			}
			throw e;
		}
	}
}
