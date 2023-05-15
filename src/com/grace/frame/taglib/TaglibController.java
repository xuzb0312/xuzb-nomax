package com.grace.frame.taglib;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.*;
import com.grace.frame.util.ueditor.UeditorUtil;
import com.grace.frame.workflow.BizController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Blob;
import java.util.HashMap;

/**
 * 主要用于标签库的后台请求操作
 * 
 * @author yjc
 */
public class TaglibController extends BizController{

	/**
	 * 获取树的初始化数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	public ModelAndView initTreeData(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String className = para.getString("__classname");// 获取构造树的数据类
		// 对className进行安全字符过滤
		className = StringUtil.htmlEncode(className);

		para.remove("__classname");// 将此参数移除
		if (StringUtil.chkStrNull(className)) {
			throw new AppException("构造树数据时，传入的树构造类为空");
		}

		// 调用树数据构造类
		DataMap rdm = this.doBizMethod(className, "initTree", para);
		String treeJsonData = rdm.getString("__treejsondata");
		ActionUtil.writeMessageToResponse(response, treeJsonData);
		return null;
	}

	/**
	 * 转向框架页面-打开window,tab时使用。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	public ModelAndView fwdPageFrame(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/layout/pageFrame.jsp", para);
	}

	/**
	 * datawindow请求数据使用
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-12
	 * @since V1.0
	 */
	public ModelAndView requestData4Grid(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String uiid = para.getString("__uiid");
		DataSet ds = (DataSet) request.getSession().getAttribute(uiid);// 从session中获取数据

		// 数据输出到前台
		ActionUtil.writeDataSetToResponse(response, ds);

		// 为了节省服务器资源，将该数据从session中移除
		request.getSession().removeAttribute(uiid);
		return null;
	}

	/**
	 * 导出数据的窗口
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-12
	 * @since V1.0
	 */
	public ModelAndView fwdQueryGridExportData(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/exportGridData.jsp");
	}

	/**
	 * 文件上传的暂时缓存类
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-12
	 * @since V1.0
	 */
	public ModelAndView uploadFile4FileBox(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		boolean isH5Upload = false;
		String uiid = para.getString("uiid");
		if (StringUtil.chkStrNull(uiid)) {
			throw new AppException("传入的UIID为空");
		}

		// 获取文件
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("file");
		if (null == multipartFile) {
			isH5Upload = true;
			multipartFile = multipartRequest.getFile("file_data");// 对于H5方式上传的进行一下区分
		}
		if (null == multipartFile) {
			throw new AppException("上传的文件为空");
		}

		// 将文件放置到session中
		request.getSession().setAttribute("file_" + uiid, multipartFile);
		if (isH5Upload) {
			DataMap rdm = new DataMap();
			rdm.put("uiid", uiid);
			ActionUtil.writeDataMapToResponse(response, rdm);// json格式回写
		} else {
			ActionUtil.writeMessageToResponse(response, uiid);// 将uiid返回到客户端
		}
		return null;
	}

	/**
	 * 文件上传的暂时缓存类--兼容模式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-12
	 * @since V1.0
	 */
	public ModelAndView uploadFile4FileBox4Tra(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String uiid = para.getString("uiid");
		String obj_id = para.getString("id");
		String con_id = para.getString("con_id");
		if (StringUtil.chkStrNull(uiid)) {
			throw new AppException("传入的UIID为空");
		}

		// 获取文件
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("file");
		if (null == multipartFile) {
			throw new AppException("上传的文件为空");
		}

		// 将文件放置到session中
		request.getSession().setAttribute("file_" + uiid, multipartFile);
		String fileName = multipartFile.getOriginalFilename();// 文件名称

		// 脚本代码
		DataMap rdm = new DataMap();
		rdm.put("id", obj_id);
		rdm.put("name", fileName);
		rdm.put("con_id", con_id);
		return new ModelAndView("frame/jsp/taglib/fileSelectUpload4TraResult.jsp", rdm);
	}

	/**
	 * fileBox获取缓存文件信息
	 * 
	 * @author yjc
	 * @date 创建时间 2016-5-26
	 * @since V1.0
	 */
	public ModelAndView downloadUploadFile4FileBox(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String uiid = para.getString("uiid");
		if (StringUtil.chkStrNull(uiid)) {
			throw new AppException("传入的UIID为空");
		}

		// 文件信息
		MultipartFile multipartFile = (MultipartFile) request.getSession()
			.getAttribute("file_" + uiid);
		if (null != multipartFile) {// 文件不为空，将文件信息写到下载流中
			OutputStream outputstream = null;
			ByteArrayInputStream inputstream = null;
			try {
				String filename = multipartFile.getOriginalFilename();
				String fileExt = filename.substring(filename.lastIndexOf(".") + 1)
					.toLowerCase();
				filename = URLEncoder.encode(filename, "UTF-8");
				response.resetBuffer();
				response.setContentType(FileIOUtil.getMime(fileExt));
				response.addHeader("Content-Disposition", "attachment;filename="
						+ filename);
				outputstream = response.getOutputStream();
				inputstream = new ByteArrayInputStream(multipartFile.getBytes());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inputstream.read(buffer)) != -1) {
					outputstream.write(buffer, 0, len);
				}
				outputstream.flush();
			} catch (Exception e) {
				throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			} finally {
				try {
					if (outputstream != null) {
						outputstream.close();
					}
					if (inputstream != null) {
						inputstream.close();
					}
				} catch (Exception e) {
					throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
							+ e.getMessage());
				}
			}
		}
		return null;
	}

	/**
	 * session数据的清空
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-12
	 * @since V1.0
	 */
	public ModelAndView clearUploadFile4FileBox(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String uiid = para.getString("uiid");
		if (StringUtil.chkStrNull(uiid)) {
			throw new AppException("传入的UIID为空");
		}
		// 将文件放置到session中
		request.getSession().removeAttribute("file_" + uiid);
		return null;
	}

	/**
	 * 转向文件上传页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdSelectFileWindow(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/fileSelectUpload.jsp", para);
	}

	/**
	 * 转向文件上传页面-传统方式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdSelectFileWindow4Tra(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/fileSelectUpload4Tra.jsp", para);
	}

	/**
	 * 转向文件上传页面-H5方式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdSelectFileWindow4H5(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/fileSelectUpload4H5.jsp", para);
	}

	/**
	 * 转向文件上传页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView cacheData4Export(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		// 获取参数
		String dcms = para.getString("dcms");// 1.界面数据导出，2.原始数据导出。
		String wjlx = para.getString("wjlx");// 目前支持txt,xls,dbf三种格式
		DataSet dsGridData = para.getDataSet("griddata");
		String colcof = para.getString("colcof");
		String title = para.getTextString("title");// 过滤html字符
		DataSet dsColumnGroup = DataSet.fromObject(para.getString("columngroupds"));// 表头合并信息
		String uiid = StringUtil.getUUID();
		if (StringUtil.chkStrNull(dcms)) {
			dcms = "1";
		}
		if (StringUtil.chkStrNull(wjlx)) {
			wjlx = "xls";
		}
		if (StringUtil.chkStrNull(title)) {
			title = "数据窗口";
		}
		title = DateUtil.dateToString(DateUtil.getDBTime(), "yyyy.MM.dd-hhmmss")
				+ title;
		if (null == dsGridData || dsGridData.size() == 0) {
			throw new BizException("没有数据需要进行导出");
		}

		// 解析列
		DataSet dsCols = new DataSet();
		HashMap<String, HashMap<String, String>> mapCode = new HashMap<String, HashMap<String, String>>();
		JSONArray jsonArr = JSONArray.fromObject(colcof);
		for (int i = 0, n = jsonArr.size(); i < n; i++) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			String type = jsonObj.getString("type");
			JSONObject columnpara = jsonObj.getJSONObject("columnpara");
			DataMap dmTemp = ActionUtil.jsonObject2DataMap(columnpara);
			if (dmTemp.getBoolean("hidden")) {
				continue;
			} else if ("columntext".equalsIgnoreCase(type)) {
				// 文本
				String nameTmp = dmTemp.getString("name");
				String labelTmp = dmTemp.getTextString("label");
				String dataTypeTmp = dmTemp.getString("datatype");
				int widthTemp = dmTemp.getInt("width");
				String mask = dmTemp.getString("mask", "");

				dsCols.addRow();
				dsCols.put(dsCols.size() - 1, "type", "string");
				dsCols.put(dsCols.size() - 1, "name", nameTmp);
				dsCols.put(dsCols.size() - 1, "label", labelTmp);
				dsCols.put(dsCols.size() - 1, "datatype", dataTypeTmp);
				dsCols.put(dsCols.size() - 1, "width", widthTemp);
				dsCols.put(dsCols.size() - 1, "mask", mask);
				if ("number".equals(dataTypeTmp)) {
					dsCols.put(dsCols.size() - 1, "sum", dmTemp.getBoolean("sum", false));
				}
			} else if ("columncheckbox".equalsIgnoreCase(type)) {
				String nameTmp = dmTemp.getString("name");
				String labelTmp = dmTemp.getTextString("label");
				int widthTemp = dmTemp.getInt("width");

				dsCols.addRow();
				dsCols.put(dsCols.size() - 1, "type", "string");
				dsCols.put(dsCols.size() - 1, "name", nameTmp);
				dsCols.put(dsCols.size() - 1, "label", labelTmp);
				dsCols.put(dsCols.size() - 1, "datatype", "string");
				dsCols.put(dsCols.size() - 1, "width", widthTemp);
			} else if ("columndropdown".equalsIgnoreCase(type)) {
				String nameTmp = dmTemp.getString("name");
				String labelTmp = dmTemp.getTextString("label");
				int widthTemp = dmTemp.getInt("width");

				dsCols.addRow();
				dsCols.put(dsCols.size() - 1, "type", "code");
				dsCols.put(dsCols.size() - 1, "name", nameTmp);
				dsCols.put(dsCols.size() - 1, "label", labelTmp);
				dsCols.put(dsCols.size() - 1, "datatype", "string");
				dsCols.put(dsCols.size() - 1, "code", "code_" + nameTmp);
				dsCols.put(dsCols.size() - 1, "width", widthTemp);

				// map
				if ("1".equals(dcms)) {
					HashMap<String, String> mapTemp = new HashMap<String, String>();
					DataSet dsCodeTmp = dmTemp.getDataSet("dscode");
					for (int j = 0, m = dsCodeTmp.size(); j < m; j++) {
						String codeTemp = dsCodeTmp.getString(j, "code");
						String contentTemp = dsCodeTmp.getRow(j)
							.getTextString("content");
						mapTemp.put(codeTemp, contentTemp);
					}
					mapCode.put("code_" + nameTmp, mapTemp);
				}
			} else if ("columnmultidropdown".equalsIgnoreCase(type)) {
				String nameTmp = dmTemp.getString("name");
				String labelTmp = dmTemp.getTextString("label");
				int widthTemp = dmTemp.getInt("width");

				dsCols.addRow();
				dsCols.put(dsCols.size() - 1, "type", "multicode");
				dsCols.put(dsCols.size() - 1, "name", nameTmp);
				dsCols.put(dsCols.size() - 1, "label", labelTmp);
				dsCols.put(dsCols.size() - 1, "datatype", "string");
				dsCols.put(dsCols.size() - 1, "code", "code_" + nameTmp);
				dsCols.put(dsCols.size() - 1, "width", widthTemp);

				// map
				if ("1".equals(dcms)) {
					HashMap<String, String> mapTemp = new HashMap<String, String>();
					DataSet dsCodeTmp = dmTemp.getDataSet("dscode");
					for (int j = 0, m = dsCodeTmp.size(); j < m; j++) {
						String codeTemp = dsCodeTmp.getString(j, "code");
						String contentTemp = dsCodeTmp.getRow(j)
							.getTextString("content");
						mapTemp.put(codeTemp, contentTemp);
					}
					mapCode.put("code_" + nameTmp, mapTemp);
				}
			} else if ("columnbuttons".equalsIgnoreCase(type)) {// 对于button的导出，导出值内容为空，为了兼容表头合并的情形
				// 文本
				String nameTmp = dmTemp.getString("name");
				String labelTmp = dmTemp.getTextString("label");
				int widthTemp = dmTemp.getInt("width");

				dsCols.addRow();
				dsCols.put(dsCols.size() - 1, "type", "string");
				dsCols.put(dsCols.size() - 1, "name", nameTmp);
				dsCols.put(dsCols.size() - 1, "label", labelTmp);
				dsCols.put(dsCols.size() - 1, "datatype", "string");
				dsCols.put(dsCols.size() - 1, "width", widthTemp);
				dsCols.put(dsCols.size() - 1, "mask", "");
			}
		}

		// 数据解析
		DataSet dsData = new DataSet();
		if ("1".equals(dcms)) {
			for (int i = 0, n = dsGridData.size(); i < n; i++) {
				// 每一行
				DataMap dmTmp = new DataMap();
				for (int j = 0, m = dsCols.size(); j < m; j++) {
					String type = dsCols.getString(j, "type");
					String name = dsCols.getString(j, "name");
					if ("code".equals(type)) {
						HashMap<String, String> mapTemp = mapCode.get("code_"
								+ name);
						String value = dsGridData.getRow(i).getString(name, "");
						if (mapTemp.containsKey(value)) {
							dmTmp.put(name, mapTemp.get(value));
						} else {
							dmTmp.put(name, value);
						}
					} else if ("multicode".equals(type)) {
						HashMap<String, String> mapTemp = mapCode.get("code_"
								+ name);
						String value = dsGridData.getRow(i).getString(name, "");
						dmTmp.put(name, this.getContentStrByCodeStr(value, mapTemp));
					} else {
						Object tmpObjVal = dsGridData.getRow(i).get(name, null);
						if (tmpObjVal instanceof String) {
							String tmpStrVal = (String) tmpObjVal;
							tmpStrVal = StringUtil.html2Text(tmpStrVal);
							dmTmp.put(name, tmpStrVal);
						} else {
							dmTmp.put(name, tmpObjVal);
						}
					}
				}
				dsData.addRow(dmTmp);
			}
		} else {
			for (int i = 0, n = dsGridData.size(); i < n; i++) {
				// 每一行
				DataMap dmTmp = new DataMap();
				for (int j = 0, m = dsCols.size(); j < m; j++) {
					String name = dsCols.getString(j, "name");
					dmTmp.put(name, dsGridData.getRow(i).get(name, null));
				}
				dsData.addRow(dmTmp);
			}
		}

		DataMap cacheDm = new DataMap();
		cacheDm.put("wjlx", wjlx);
		cacheDm.put("title", title);
		cacheDm.put("dscols", dsCols);
		cacheDm.put("dsdata", dsData);
		cacheDm.put("columngroupds", dsColumnGroup);

		// 放到session中
		request.getSession().setAttribute("grid_data_" + uiid, cacheDm);

		ActionUtil.writeMessageToResponse(response, uiid);
		return null;
	}

	/**
	 * 根据codeStr获取contentStr
	 * <p>
	 * 1,2-男,女
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-8
	 * @since V1.0
	 */
	private String getContentStrByCodeStr(String codeStr,
			HashMap<String, String> codeMap) {
		if (StringUtil.chkStrNull(codeStr)) {
			return "";
		}
		StringBuffer contentBF = new StringBuffer();
		String[] codeArr = codeStr.split(",");
		for (int i = 0, n = codeArr.length; i < n; i++) {
			String code = codeArr[i];
			if (StringUtil.chkStrNull(code)) {
				continue;
			}
			if (codeMap.containsKey(code)) {
				contentBF.append(codeMap.get(code));
			} else {
				contentBF.append(code);
			}
			contentBF.append(",");
		}
		if (contentBF.length() > 0) {
			contentBF.setLength(contentBF.length() - 1);
		}
		return contentBF.toString();
	}

	/**
	 * 转向文件上传页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView downloadQueryGridData(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String uiid = para.getString("cacheuiid");
		DataMap cacheDm = (DataMap) request.getSession()
			.getAttribute("grid_data_" + uiid);
		request.getSession().removeAttribute("grid_data_" + uiid);

		String wjlx = cacheDm.getString("wjlx");
		String title = cacheDm.getString("title");
		DataSet dsCols = cacheDm.getDataSet("dscols");
		DataSet dsData = cacheDm.getDataSet("dsdata");
		DataSet dsColumnGroup = cacheDm.getDataSet("columngroupds");

		if ("xls".equalsIgnoreCase(wjlx)) {
			// 生成新的格式列
			DataSet dsColumns = new DataSet();
			for (int i = 0, n = dsCols.size(); i < n; i++) {
				String nameT = dsCols.getString(i, "name");
				String labelT = dsCols.getString(i, "label");
				String dataTypeT = dsCols.getString(i, "datatype");
				String maskT = dsCols.getRow(i).getString("mask", "");
				String mask = "";
				boolean sum = dsCols.getRow(i).getBoolean("sum", false);
				if ("date".equalsIgnoreCase(dataTypeT)) {
					if (StringUtil.chkStrNull(maskT)) {
						mask = "yyyy-MM-dd hh:mm:ss";
					} else {
						mask = maskT;
					}
				} else if ("number".equalsIgnoreCase(dataTypeT)) {
					if (StringUtil.chkStrNull(maskT)) {
						mask = "################################0.00";
					} else {
						if (maskT.endsWith("%")) {
							if (maskT.contains(".")) {
								mask = maskT.substring(0, maskT.length() - 1)
										+ "00";
							} else {
								mask = maskT.substring(0, maskT.length() - 1)
										+ ".00";
							}
						} else {
							mask = maskT;
						}
					}
				}
				ExcelUtil.addColumnsRow4ExcelUtil(dsColumns, nameT, labelT, dataTypeT, mask, sum);
			}

			// Excel
			ExcelUtil.exportData2Response(response, title, dsColumns, dsColumnGroup, dsData);
		} else if ("txt".equalsIgnoreCase(wjlx)) {
			// 生成新的格式列
			DataSet dsColumns = new DataSet();
			for (int i = 0, n = dsCols.size(); i < n; i++) {
				String name = dsCols.getString(i, "name");
				String label = dsCols.getString(i, "label");
				TxtUtil.addColumnsRow4TxtUtil(dsColumns, name, label);
			}
			// txt
			TxtUtil.exportData2Response(response, title, dsColumns, dsData);
		} else if ("dbf".equalsIgnoreCase(wjlx)) {
			// 生成新的格式列
			DataSet dsColumns = new DataSet();
			for (int i = 0, n = dsCols.size(); i < n; i++) {
				String nameT = dsCols.getString(i, "name");
				String dataTypeT = dsCols.getString(i, "datatype");
				String maskT = dsCols.get(i).getString("mask", "");
				int length = 254;
				int decimalcount = 0;
				if ("date".equalsIgnoreCase(dataTypeT)) {
					length = 20;
				} else if ("number".equalsIgnoreCase(dataTypeT)) {
					if (StringUtil.chkStrNull(maskT)) {
						length = 16;
						decimalcount = 2;
					} else {
						maskT = maskT.trim();
						if (maskT.endsWith("%")) {
							int dotIndex = maskT.indexOf(".");
							if (dotIndex < 0) {
								length = maskT.length() - 1;
								decimalcount = 2;
							} else {
								length = maskT.length() - 2;
								decimalcount = length - dotIndex + 2;
							}
						} else {
							int dotIndex = maskT.indexOf(".");
							if (dotIndex < 0) {
								length = maskT.length();
								decimalcount = 0;
							} else {
								length = maskT.length() - 1;
								decimalcount = length - dotIndex;
							}
						}
					}
					System.out.println(length + "-->" + decimalcount);
				}
				DBFUtil.addColumnsRow4DBFUtil(dsColumns, nameT, dataTypeT, length, decimalcount);
			}
			// dbf
			DBFUtil.exportData2Response(response, title, dsColumns, dsData);
		} else if ("xml".equalsIgnoreCase(wjlx)) {
			// 生成新的格式列
			DataSet dsColumns = new DataSet();
			for (int i = 0, n = dsCols.size(); i < n; i++) {
				String name = dsCols.getString(i, "name");
				String label = dsCols.getString(i, "label");
				XMLUtil.addColumnsRow4XMLUtil(dsColumns, name, label);
			}
			// xml
			XMLUtil.exportData2Response(response, title, dsColumns, dsData);
		} else if ("pdf".equals(wjlx)) {
			// 生成新的格式列
			DataSet dsColumns = new DataSet();
			for (int i = 0, n = dsCols.size(); i < n; i++) {
				String name = dsCols.getString(i, "name");
				String label = dsCols.getString(i, "label");
				int width = dsCols.getInt(i, "width");
				PDFUtil.addColumnsRow4PDFUtil(dsColumns, name, label, width);
			}

			// pdf
			PDFUtil.exportData2Response(response, title, dsColumns, dsData);
		} else if ("json".equals(wjlx)) {// 导出json文件
			DataMap dmData = new DataMap();
			dmData.put("data", dsData);
			dmData.put("header", dsCols);
			String outStr = JSONObject.fromObject(dmData).toString(1);// 转成json数据-并格式化
			FileIOUtil.writeByteToResponse(outStr.getBytes("UTF-8"), title
					+ ".json", response);
		} else {
			throw new BizException("传入的文件类型不合法，目前只支持xls,txt,dbf,xml,pdf文件格式的导出。");
		}

		// 记录文件的下载日志
		SysUser user = this.getSysUser(request);
		String yhid = "";
		if (null != user) {
			yhid = user.getYhid();
		}
		BizLogUtil.saveBizLog("SYS-B-GRIDDATAEXPORT", "数据表格数据导出", "B", "", "", "", "数据表格数据导出(文件名称："
				+ title + "." + wjlx + ")", "wjlx=" + wjlx + ",title=" + title, ActionUtil.getRemoteHost(request), yhid, 0);

		return null;
	}

	/**
	 * 转向打印frame页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdPrinterPageFrame(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/layout/printerPageFrame.jsp", para);
	}

	/**
	 * grid的新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView fwdQueryGridDataEdit4Add(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String columnds = para.getString("columnds");
		DataMap dm = new DataMap();

		DataSet dsColumn = new DataSet();

		// 解析数据
		JSONArray jsonArr = JSONArray.fromObject(columnds);
		for (int i = 0, n = jsonArr.size(); i < n; i++) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			String type = jsonObj.getString("type");
			JSONObject columnpara = jsonObj.getJSONObject("columnpara");
			if (columnpara.getBoolean("hidden")) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "hiddeninput");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
			} else if ("columntext".equalsIgnoreCase(type)) {
				// 文本
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "textinput");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "mask", columnpara.getString("mask"));
				dsColumn.put(dsColumn.size() - 1, "sourcemask", columnpara.getString("sourcemask"));
				dsColumn.put(dsColumn.size() - 1, "datatype", columnpara.getString("datatype"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
			} else if ("columncheckbox".equalsIgnoreCase(type)) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "checkboxlist");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
			} else if ("columndropdown".equalsIgnoreCase(type)) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "dropdownlist");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
				dsColumn.put(dsColumn.size() - 1, "dscode", "dscode_"
						+ columnpara.getString("name"));
				DataMap dmTemp = ActionUtil.jsonObject2DataMap(columnpara);
				dm.put("dscode_" + columnpara.getString("name"), dmTemp.getDataSet("dscode"));
			} else if ("columnmultidropdown".equalsIgnoreCase(type)) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "multidropdownlist");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
				dsColumn.put(dsColumn.size() - 1, "dscode", "dscode_"
						+ columnpara.getString("name"));
				DataMap dmTemp = ActionUtil.jsonObject2DataMap(columnpara);
				dm.put("dscode_" + columnpara.getString("name"), dmTemp.getDataSet("dscode"));
			}
		}
		dm.put("dscolumns", dsColumn);
		return new ModelAndView("frame/jsp/taglib/winQueryGridDataEdit4Add.jsp", dm);
	}

	/**
	 * grid的修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView fwdQueryGridDataEdit4Modify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String data = para.getString("data");
		String columnds = para.getString("columnds");
		DataMap dm = new DataMap();

		DataSet dsColumn = new DataSet();

		// 解析数据
		JSONArray jsonArr = JSONArray.fromObject(columnds);
		for (int i = 0, n = jsonArr.size(); i < n; i++) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			String type = jsonObj.getString("type");
			JSONObject columnpara = jsonObj.getJSONObject("columnpara");
			if (columnpara.getBoolean("hidden")) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "hiddeninput");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
			} else if ("columntext".equalsIgnoreCase(type)) {
				// 文本
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "textinput");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "mask", columnpara.getString("mask"));
				dsColumn.put(dsColumn.size() - 1, "sourcemask", columnpara.getString("sourcemask"));
				dsColumn.put(dsColumn.size() - 1, "datatype", columnpara.getString("datatype"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
			} else if ("columncheckbox".equalsIgnoreCase(type)) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "checkboxlist");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
			} else if ("columndropdown".equalsIgnoreCase(type)) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "dropdownlist");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
				dsColumn.put(dsColumn.size() - 1, "dscode", "dscode_"
						+ columnpara.getString("name"));
				DataMap dmTemp = ActionUtil.jsonObject2DataMap(columnpara);
				dm.put("dscode_" + columnpara.getString("name"), dmTemp.getDataSet("dscode"));
			} else if ("columnmultidropdown".equalsIgnoreCase(type)) {
				dsColumn.addRow();
				dsColumn.put(dsColumn.size() - 1, "type", "multidropdownlist");
				dsColumn.put(dsColumn.size() - 1, "name", columnpara.getString("name"));
				dsColumn.put(dsColumn.size() - 1, "label", columnpara.getString("label"));
				dsColumn.put(dsColumn.size() - 1, "readonly", columnpara.getBoolean("readonly"));
				dsColumn.put(dsColumn.size() - 1, "dscode", "dscode_"
						+ columnpara.getString("name"));
				DataMap dmTemp = ActionUtil.jsonObject2DataMap(columnpara);
				dm.put("dscode_" + columnpara.getString("name"), dmTemp.getDataSet("dscode"));
			}
		}
		dm.put("dscolumns", dsColumn);
		dm.put("datasource", ActionUtil.jsonObject2DataMap(JSONObject.fromObject(data)));

		return new ModelAndView("frame/jsp/taglib/winQueryGridDataEdit4Modify.jsp", dm);
	}

	/**
	 * 创建progressBar
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public ModelAndView createProgressBar(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		// 实例化一个progressbar
		ProgressBar pgBar = new ProgressBar();
		String pbid = pgBar.getId();
		request.getSession().setAttribute("bgbar_" + pbid, pgBar);

		// 将pgbar的唯一标识ID传递到前台
		ActionUtil.writeMessageToResponse(response, pbid);
		return null;
	}

	/**
	 * 销毁ProgressBar
	 * <p>
	 * 销毁的时机有两个： <br>
	 * 1.前台请求时，即该方法的调用，导致progressbar销毁。 <br>
	 * 2.session超时自动销毁。<br>
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public ModelAndView destroyProgressBar(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String pbid = para.getString("pbid");

		// 修改progressBar为前台已经取消操作。
		ProgressBar pgBar = (ProgressBar) request.getSession()
			.getAttribute("bgbar_" + pbid);
		if (null != pgBar) {
			pgBar.setCanceled(true);
		}
		// 将其从session中移除
		request.getSession().removeAttribute("bgbar_" + pbid);
		return null;
	}

	/**
	 * 获取ProgressBar的相关进行，进度和提示信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public ModelAndView getProgressBarInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String pbid = para.getString("pbid");
		// 获取Bar
		ProgressBar pgBar = (ProgressBar) request.getSession()
			.getAttribute("bgbar_" + pbid);

		DataMap dm = new DataMap();
		if (null != pgBar) {
			// 信息写到前台
			dm.put("msg", pgBar.getMsg());
			dm.put("percent", pgBar.getPercent());
			dm.put("finish", pgBar.isFinish());
			if (pgBar.isFinish()) {
				dm.put("enableasynbar", pgBar.isEnableAsynBar());
				if (null != pgBar.getEx4AsynBar()) {
					if (pgBar.getEx4AsynBar() instanceof NullPointerException) {
						dm.put("errmsg", "空指针异常[NullPointerException]，请联系开发人员在系统后台排查问题原因。");
					} else {
						dm.put("errmsg", pgBar.getEx4AsynBar().getMessage());
					}
				} else {
					String return_data = "{}";
					if (null != pgBar.getRdm4AsynBar()) {
						return_data = pgBar.getRdm4AsynBar().toJsonString();
					}
					dm.put("return_data", return_data);
				}
			}
		} else {
			dm.put("msg", "");
			dm.put("percent", 0);
			dm.put("finish", false);
		}
		ActionUtil.writeDataMapToResponse(response, dm);

		return null;
	}

	/**
	 * 文件下载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-14
	 * @since V1.0
	 */
	public ModelAndView downloadSysFile(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String wjbs = para.getString("wjbs");
		if (StringUtil.chkStrNull(wjbs)) {
			throw new AppException("传入的文件标识为空。");
		}

		// 获取文件内容
		Sql sql = new Sql();
		sql.setSql(" select wjmc, wjgs, wjnr from fw.file_model where dbid = ? and wjbs = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		sql.setString(2, wjbs);
		DataSet dsTemp = sql.executeQuery();

		if (dsTemp.size() <= 0) {
			throw new AppException("数据库中没有DBID=" + GlobalVars.SYS_DBID
					+ ",WJBS=" + wjbs + "的系统文件。");
		}
		Blob file = dsTemp.getBlob(0, "wjnr");
		String wjmc = dsTemp.getString(0, "wjmc");
		String wjgs = dsTemp.getString(0, "wjgs");

		// 把文件写到前台下载
		FileIOUtil.writeBlobToResponse(file, wjmc + "." + wjgs, response);
		return null;
	}

	/**
	 * cookie本地设置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdClientCookieSetting(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winClientCookieSetting.jsp", para);
	}

	/**
	 * 进入临时授权页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdApplyTempRightPage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winApplyTempRightPage.jsp", para);
	}

	/**
	 * 进入临时授权页面-用户选择
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdChooseSysUser4TempRight(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "fwdChooseSysUser4TempRight", para);
		return new ModelAndView("frame/jsp/taglib/winChooserUser4TempRight.jsp", dm);
	}

	/**
	 * 进入临时授权页面-用户选择
	 *
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdChooseStdUser4TempRight(HttpServletRequest request,
												   HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "fwdChooseSysUser4TempRight", para);
		return new ModelAndView("frame/jsp/taglib/winStdChooserUser4TempRight.jsp", dm);
	}


	/**
	 * 进行临时授权
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-14
	 * @since V1.0
	 */
	public ModelAndView checkUserTempRight(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "checkUserTempRight", para);
		ActionUtil.writeMessageToResponse(response, "true");
		return null;
	}

	/**
	 * 发送电子邮件--textInput
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdSendEmailPage4TextInput(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winSendEmailPage4TextInput.jsp", para);
	}

	/**
	 * 发送邮件
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-14
	 * @since V1.0
	 */
	public ModelAndView sendEmail4TextInput(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "sendEmail4TextInput", para);
		return null;
	}

	/**
	 * 打印对象，支持下载Html文件
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView downLoadHtml4Printer(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "downLoadHtml4Printer", para);
		return null;
	}

	/**
	 * 转向大文件上传的窗口页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public ModelAndView fwdbigSizeFileUploadWindow(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/bigSizeFileUpload.jsp", para);
	}

	/**
	 * 典型批注修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdNoteConfigModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "fwdNoteConfigModify", para);
		return new ModelAndView("frame/jsp/taglib/winNoteConfigModify.jsp", rdm);
	}

	/**
	 * 保存config修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView saveNoteConfigModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "saveNoteConfigModify", para);
		return null;
	}

	/**
	 * 图片查看页面
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdImageViewPage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winImageView.jsp", para);
	}

	/**
	 * 压缩包查看页面
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdZipViewPage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String zipsessionkey = para.getString("zipsessionkey");
		DataMap rdm = (DataMap) request.getSession()
			.getAttribute(zipsessionkey);
		request.getSession().removeAttribute(zipsessionkey);// 移除
		return new ModelAndView("frame/jsp/taglib/winZipViewPage.jsp", rdm);
	}

	/**
	 * GRID部分选择
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdQueryGridPartSelect(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winQueryGridPartSelect.jsp", para);
	}

	/**
	 * 解析Word数据
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-6
	 * @since V1.0
	 */
	public ModelAndView fwdWordParsePage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winWordParse.jsp", para);
	}

	/**
	 * 解析word文档
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-7
	 * @since V1.0
	 */
	public ModelAndView parseUploadWord(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.taglib.TaglibBiz", "parseUploadWord", para);
		ActionUtil.writeMessageToResponse(response, rdm.getString("uuid"));
		return null;
	}

	/**
	 *查看word解析结果
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-8
	 * @since V1.0
	 */
	public ModelAndView fwdWordParseResultView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String uuid = para.getString("uuid");
		if (StringUtil.chkStrNull(uuid)) {
			throw new BizException("传入的UUID为空");
		}
		// 从会话中获取信息，写到前台
		String htmlStr = (String) request.getSession()
			.getAttribute("html_result_" + uuid);
		request.getSession().removeAttribute("html_result_" + uuid);
		ActionUtil.writeMessageToResponse(response, htmlStr);
		return null;
	}

	/**
	 * 图片模板编辑器
	 * 
	 * @author yjc
	 * @date 创建时间 2017-12-27
	 * @since V1.0
	 */
	public ModelAndView fwdImgModelEditor(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winImgModelEditor.jsp", para);
	}

	/**
	 * 信息采集输入框
	 * 
	 * @author yjc
	 * @date 创建时间 2017-12-27
	 * @since V1.0
	 */
	public ModelAndView fwdPromptWindow(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/taglib/winPrompt.jsp", para);
	}

	/**
	 * 下载Ueditor文件
	 * 
	 * @author yjc
	 * @date 创建时间 2019-2-12
	 * @since V1.0
	 */
	public ModelAndView downloadUeditorFile(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String wjid = para.getString("wjid");
		if (StringUtil.chkStrNull(wjid)) {
			throw new BizException("文件ID为空");
		}

		// 缓存路径
		String cachePath = request.getSession()
			.getServletContext()
			.getRealPath("/")
				+ UeditorUtil.CACHE_PATH;
		if (!UeditorUtil.CACHEFILE.containsKey(wjid)) {
			// 缓存操作
			// 目录验证
			File cacheDir = new File(cachePath);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}

			Sql sql = new Sql();
			sql.setSql(" select wjgs, wjnr from fw.ueditor_file where wjid = ? ");
			sql.setString(1, wjid);
			DataSet dsAttch = sql.executeQuery();
			if (dsAttch.size() <= 0) {
				return null;
			}
			Blob wjnr = dsAttch.getBlob(0, "wjnr");
			String wjgs = dsAttch.getString(0, "wjgs");
			// 文件保存
			String fileName = wjid + "." + wjgs;
			FileIOUtil.saveBlobToFile(wjnr, new File(cachePath + fileName));

			// 加入缓存，下一次则不进行重新静态化了。
			UeditorUtil.CACHEFILE.put(wjid, fileName);
		}
		String fileName = UeditorUtil.CACHEFILE.get(wjid);
		try {
			FileIOUtil.writeFileToResponse(cachePath + fileName, fileName, response);
		} catch (Exception e) {
			// 出现流读写异常，不进行任何处理，防止日志过大
			if (GlobalVars.DEBUG_MODE) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
