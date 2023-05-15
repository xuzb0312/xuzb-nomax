package com.grace.frame.debug.biz;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * debugbiz的类
 * 
 * @author yjc
 */
public class BizClassDebugBiz extends Biz{

	/**
	 * 执行biz
	 * 
	 * @author yjc
	 * @date 创建时间 2016-9-24
	 * @since V1.0
	 */
	public final DataMap runBizClassDebug(final DataMap para) throws Exception {
		DataMap rdm = new DataMap();
		DataMap pdm = new DataMap();
		String bizName = "";
		String methodName = "";
		String errcode = "0";
		String errmsg = "";
		String errmsgdetl = "";
		DataMap runResult = new DataMap();
		String mljb = para.getString("mljb");
		try {
			if (StringUtil.chkStrNull(mljb)) {
				throw new BizException("传入的命令脚本为空");
			}
			String[] mljbArr = mljb.split(":");

			bizName = mljbArr[0].substring(0, mljbArr[0].lastIndexOf("."));
			methodName = mljbArr[0].substring(mljbArr[0].lastIndexOf(".") + 1);

			pdm.clear();
			for (int i = 1, n = mljbArr.length; i < n; i++) {
				String[] paraArr = mljbArr[i].split("=");
				if (paraArr.length > 1) {
					pdm.put(paraArr[0].toLowerCase(), paraArr[1]);
				} else {
					pdm.put(paraArr[0].toLowerCase(), "");
				}
			}
			runResult = this.doBizMethod(bizName, methodName, pdm);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			errcode = "1";
			errmsg = e.getMessage();
			errmsgdetl = sw.toString();
		}

		// 所有操作完成后事物回滚，因为本工具仅供调试，禁止产生业务
		this.sql.rollback();

		rdm.clear();
		rdm.put("result", runResult);
		rdm.put("errmsgdetl", errmsgdetl);
		rdm.put("errmsg", errmsg);
		rdm.put("errcode", errcode);
		rdm.put("mljb", mljb);
		rdm.put("bizname", bizName);
		rdm.put("methodname", methodName);
		rdm.put("para", pdm.toString());
		rdm.put("bizpara", this.getBizPara().toString());
		return rdm;
	}
}
