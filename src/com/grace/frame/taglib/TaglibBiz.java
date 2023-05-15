package com.grace.frame.taglib;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.login.LoginUtil;
import com.grace.frame.util.*;
import com.grace.frame.workflow.Biz;
import net.arnx.wmf2svg.gdi.svg.SvgGdi;
import net.arnx.wmf2svg.gdi.wmf.WmfParser;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TaglibBiz extends Biz{


	/**
	 * 选择具有该权限的用户信息
	 *
	 * @author yjc
	 * @date 创建时间 2015-12-14
	 * @since V1.0
	 */
	public final DataMap fwdChooseStdUser4TempRight(final DataMap para) throws Exception {
		String yhbh = para.getString("yhbh");
		String functionid = para.getString("functionid");
		if (StringUtil.chkStrNull(functionid)) {
			throw new BizException("传入的功能ID为空。");
		}
		StringBuffer sqlBF = new StringBuffer();
		if (StringUtil.chkStrNull(yhbh)) {
			yhbh = "%";
		} else {
			yhbh = "%" + yhbh + "%";
		}

		sqlBF.setLength(0);
		sqlBF.setLength(0);
		sqlBF.append(" select a.yhid, a.yhbh, a.yhmc ");
		sqlBF.append("   from fw.sys_user a ");
		sqlBF.append("  where (a.yhbh like ? or a.yhmc like ? or a.zjhm like ? or a.yhmcpy like ?) ");
		sqlBF.append("    and a.yhzt = '1' ");
		sqlBF.append("    and ((a.yhlx = 'B' and ");
		sqlBF.append("        (exists (select 'x' ");
		sqlBF.append("                     from fw.user_func b ");
		sqlBF.append("                    where a.yhid = b.yhid ");
		sqlBF.append("                      and b.gnid = ?) or exists ");
		sqlBF.append("         (select 'x' ");
		sqlBF.append("              from fw.user_role c, fw.role_func d ");
		sqlBF.append("             where a.yhid = c.yhid ");
		sqlBF.append("               and c.jsid = d.jsid ");
		sqlBF.append("               and d.gnid = ?))) or a.yhlx = 'A') ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhbh);
		this.sql.setString(2, yhbh);
		this.sql.setString(3, yhbh);
		this.sql.setString(4, yhbh);
		this.sql.setString(5, functionid);

		this.sql.setString(6, functionid);
		DataSet dsUser = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dsuser", dsUser);
		return dm;
	}

	/**
	 * 选择具有该权限的用户信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-14
	 * @since V1.0
	 */
	public final DataMap fwdChooseSysUser4TempRight(final DataMap para) throws Exception {
		String yhbh = para.getString("yhbh");
		String functionid = para.getString("functionid");
		if (StringUtil.chkStrNull(functionid)) {
			throw new BizException("传入的功能ID为空。");
		}
		StringBuffer sqlBF = new StringBuffer();
		if (StringUtil.chkStrNull(yhbh)) {
			yhbh = "%";
		} else {
			yhbh = "%" + yhbh + "%";
		}

		sqlBF.setLength(0);
		sqlBF.setLength(0);
		sqlBF.append(" select a.yhid, a.yhbh, a.yhmc ");
		sqlBF.append("   from fw.sys_user a ");
		sqlBF.append("  where (a.yhbh like ? or a.yhmc like ? or a.zjhm like ? or a.yhmcpy like ?) ");
		sqlBF.append("    and a.yhzt = '1' ");
		sqlBF.append("    and ((a.yhlx = 'B' and ");
		sqlBF.append("        (exists (select 'x' ");
		sqlBF.append("                     from fw.user_func b ");
		sqlBF.append("                    where a.yhid = b.yhid ");
		sqlBF.append("                      and b.gnid = ?) or exists ");
		sqlBF.append("         (select 'x' ");
		sqlBF.append("              from fw.user_role c, fw.role_func d ");
		sqlBF.append("             where a.yhid = c.yhid ");
		sqlBF.append("               and c.jsid = d.jsid ");
		sqlBF.append("               and d.gnid = ?))) or a.yhlx = 'A') ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhbh);
		this.sql.setString(2, yhbh);
		this.sql.setString(3, yhbh);
		this.sql.setString(4, yhbh);
		this.sql.setString(5, functionid);

		this.sql.setString(6, functionid);
		DataSet dsUser = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dsuser", dsUser);
		return dm;
	}

	/**
	 * 检测临时授权。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-14
	 * @since V1.0
	 */
	public final DataMap checkUserTempRight(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		String password = para.getString("password");
		String functionid = para.getString("functionid");
		if (StringUtil.chkStrNull(functionid)) {
			throw new BizException("传入的功能ID为空。");
		}
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空。");
		}
		if (StringUtil.chkStrNull(password)) {
			throw new BizException("传入的密码为空。");
		}

		// 获取用户编号信息
		this.sql.setSql(" select yhbh from fw.sys_user where yhid = ? ");
		this.sql.setString(1, yhid);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() <= 0) {
			throw new BizException("用户信息不存在。");
		}
		String yhbh = ds.getString(0, "yhbh");

		/**
		 * 首先检测是否登录成功
		 */
		DataMap dm = LoginUtil.chkLoginRight(yhbh, password);
		if (!"0".equals(dm.getString("code"))) {
			throw new BizException("用户密码输入的不正确，无法进行授权。");
		}
		SysUser user = (SysUser) dm.get("sysuser");

		// 判断用户状态
		if ("0".equals(user.getAllInfoDM().getString("yhzt"))) {
			throw new BizException("该用户已经注销，无法进行授权。");
		}

		// 检测用户的功能授权信息是否正确
		if (!"A".equals(user.getYhlx())) {
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" select a.yhid ");
			sqlBF.append("   from fw.user_func a ");
			sqlBF.append("  where a.gnid = ? ");
			sqlBF.append("    and a.yhid = ? ");
			sqlBF.append(" union all ");
			sqlBF.append(" select b.yhid ");
			sqlBF.append("   from fw.user_role b, fw.role_func c ");
			sqlBF.append("  where b.jsid = c.jsid ");
			sqlBF.append("    and b.yhid = ? ");
			sqlBF.append("    and c.gnid = ? ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, functionid);
			this.sql.setString(2, yhid);
			this.sql.setString(3, yhid);
			this.sql.setString(4, functionid);
			ds = this.sql.executeQuery();
			if (ds.size() <= 0) {
				throw new BizException("该用户无法对当前功能进行授权。");
			}
		}

		// 授权用户日志
		BizLogUtil.saveBizLog("SYS-A-YHLSSQCZ", "用户临时授权操作", "A", this.getYhid(), this.getYhbh(), this.getYhmc(), "用户ID为"
				+ yhid
				+ "(用户编号："
				+ user.getYhbh()
				+ ",用户名称："
				+ user.getYhmc()
				+ ")的用户，为用户ID为"
				+ this.getYhid()
				+ "(用户编号："
				+ this.getYhbh()
				+ ",用户名称：" + this.getYhmc() + ")对功能" + functionid + "进行临时授权。", "sqyhid="
				+ yhid
				+ ",bsqyhid="
				+ this.getYhid()
				+ ",functionid="
				+ functionid, this.getIp(), yhid, 0);

		// 被授权用户日志
		this.log("SYS-A-YHLSSQJSCZ", "用户临时授权接收操作", "A", this.getYhid(), this.getYhbh(), this.getYhmc(), "用户ID为"
				+ yhid
				+ "(用户编号："
				+ user.getYhbh()
				+ ",用户名称："
				+ user.getYhmc()
				+ ")的用户，为用户ID为"
				+ this.getYhid()
				+ "(用户编号："
				+ this.getYhbh()
				+ ",用户名称：" + this.getYhmc() + ")对功能" + functionid + "进行临时授权。", "sqyhid="
				+ yhid
				+ ",bsqyhid="
				+ this.getYhid()
				+ ",functionid="
				+ functionid);
		return null;
	}

	/**
	 * 发送电子邮件
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-2
	 * @since V1.0
	 */
	public final DataMap sendEmail4TextInput(final DataMap para) throws Exception {
		String address = para.getString("address");
		String subject = para.getString("subject");
		String content = para.getString("content");
		if (StringUtil.chkStrNull(address)) {
			throw new BizException("传入的收件人地址为空");
		}
		if (StringUtil.chkStrNull(subject)) {
			throw new BizException("传入的邮件主题为空");
		}
		if (StringUtil.chkStrNull(content)) {
			throw new BizException("传入的邮件内容为空");
		}
		String[] arrAdd = address.split(",");

		try {
			// 构建邮件
			EMail email = new EMail();
			email.setSubject(subject);
			email.setTo(arrAdd);

			// 按照标准模板格式发送邮件
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div>");
			strBF.append(" <includetail>");
			strBF.append("  <div style=\"background-color:#f5f5f5\">");
			strBF.append("   <table width=\"720\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin:0 auto\">");
			strBF.append("    <tbody>");
			strBF.append("     <tr>");
			strBF.append("      <td><p style=\"margin:20px 0;font-size:14px;line-height:20px;color:#5a5a5a\">该邮件来自 <strong>")
				.append(GlobalVars.SYS_DBNAME + "." + GlobalVars.APP_NAME)
				.append("</strong></p></td>");
			strBF.append("     </tr>");
			strBF.append("     <tr>");
			strBF.append("      <td></td>");
			strBF.append("     </tr>");
			strBF.append("     <tr>");
			strBF.append("      <td>");
			strBF.append("       <div style=\"padding:20px 40px;border-top:4px solid #0582ed;background-color:#ffffff\">");
			strBF.append(content);
			strBF.append("       </div> </td>");
			strBF.append("     </tr>");
			strBF.append("     <tr>");
			strBF.append("      <td>");
			strBF.append("       <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			strBF.append("        <tbody>");
			strBF.append("         <tr>");
			strBF.append("          <td><p style=\"margin:20px 0;font-size:12px;line-height:20px;color:#999999\">特别提示：该邮件由")
				.append(GlobalVars.SYS_DBNAME + "." + GlobalVars.APP_NAME)
				.append("自动发送，请勿回复。</p></td>");
			strBF.append("          <td width=\"240\">")
				.append(DateUtil.dateToString(DateUtil.getDBTime(), "yyyy年MM月dd日 hh:mm:ss"))
				.append("</td>");
			strBF.append("         </tr>");
			strBF.append("        </tbody>");
			strBF.append("       </table></td>");
			strBF.append("     </tr>");
			strBF.append("    </tbody>");
			strBF.append("   </table>");
			strBF.append("  </div>");
			strBF.append(" </includetail>");
			strBF.append("</div>");
			email.setContent(strBF.toString());

			// 发送
			EMailUtil emailUtil = new EMailUtil(email);
			emailUtil.send();
		} catch (Exception e) {
			SysLogUtil.logError("邮件发送失败，具体原因：" + e.getMessage(), e);// 系统级日志记录发送失败原因
			throw new BizException("邮件发送失败，具体原因：" + e.getMessage());
		}

		// 记录日志
		this.log("SYS-A-FSXTYJ", "发送系统邮件", "A", this.getYhid(), this.getYhbh(), this.getYhmc(), "向"
				+ address + "发送电子邮件，邮件主题为：" + subject, "address=" + address
				+ ",subject=" + subject);

		return null;
	}

	/**
	 * 打印对象的html内容转为html文件进行下载
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-2
	 * @since V1.0
	 */
	public final DataMap downLoadHtml4Printer(final DataMap para) throws Exception {
		String printdata = para.getString("printdata");// html内容
		if (StringUtil.chkStrNull(printdata)) {
			printdata = "<span style=\"font:red;\">【警告：打印文件无内容。】</span>";
		}
		String basePath = TagSupportUtil.getBasePathFromRequest(this.getRequest());

		// 增加解析html下载，拼接文件头；
		StringBuffer htmlBF = new StringBuffer();
		htmlBF.append(" <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> ");
		htmlBF.append(" <html> ");
		htmlBF.append(" 	<head> ");
		htmlBF.append(" 		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> ");
		htmlBF.append(" 		<title>打印</title> ");
		htmlBF.append(" 		<style type=\"text/css\"> ");
		htmlBF.append(" 			/*通用调整样式*/ ");
		htmlBF.append(" 			table { ");
		htmlBF.append(" 				border-collapse: collapse; ");
		htmlBF.append(" 				border-spacing: 0; ");
		htmlBF.append(" 			} ");
		htmlBF.append(" 			/*换页符的样式*/ ");
		htmlBF.append(" 			.print-page-break-div { ");
		htmlBF.append(" 				text-align: center; ");
		htmlBF.append(" 				font-size: 12px; ");
		htmlBF.append(" 				color: blue; ");
		htmlBF.append(" 			} ");
		htmlBF.append(" 			 ");
		htmlBF.append(" 			/*打印时该区域不进行打印*/ ");
		htmlBF.append(" 			@media print { ");
		htmlBF.append(" 				.print-page-break-div { ");
		htmlBF.append(" 					display: none; ");
		htmlBF.append(" 				} ");
		htmlBF.append(" 			} ");
		htmlBF.append(" 		</style> ");
		htmlBF.append(" 		<script type=\"text/javascript\" ");
		htmlBF.append(" 			src=\"")
			.append(basePath)
			.append("frame/js/jquery-1.7.2.min.js?version=1.7.2\"></script> ");
		htmlBF.append(" 		<script type=\"text/javascript\" ");
		htmlBF.append(" 			src=\"")
			.append(basePath)
			.append("frame/plugins/ueditor/ueditor.parse.min.js\"></script> ");
		htmlBF.append(" 	</head> ");
		htmlBF.append(" 	<body>");
		htmlBF.append(" 	<div id=\"printer_content\">")
			.append(printdata)
			.append("</div>");
		htmlBF.append(" 	</body> ");
		htmlBF.append(" 	<script type=\"text/javascript\"> ");
		htmlBF.append(" 		$(function(){ ");
		htmlBF.append(" 			uParse(\"#printer_content\", {rootPath: \""
				+ basePath + "frame/plugins/ueditor/\"}); ");
		htmlBF.append(" 		}); ");
		htmlBF.append(" 	</script> ");
		htmlBF.append(" </html> ");

		// 数据写到前台
		FileIOUtil.writeByteToResponse(htmlBF.toString().getBytes("UTF-8"), "打印文件.html", this.getResponse());

		return null;
	}

	/**
	 * 典型批注修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public final DataMap fwdNoteConfigModify(final DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		if (StringUtil.chkStrNull(pzbh)) {
			throw new BizException("传入的批注编号为空");
		}

		// 批注信息
		this.sql.setSql(" select pzbh, pzmc, pzsm from fw.note_list where pzbh = ? ");
		this.sql.setString(1, pzbh);
		DataSet dsList = this.sql.executeQuery();
		if (dsList.size() <= 0) {
			throw new BizException("根据传入的批注编号" + pzbh + "没有查询到批注信息");
		}

		// 批注配置信息
		this.sql.setSql(" select pznr, xh from fw.note_config where dbid = ? and pzbh = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, pzbh);
		DataSet dsConfig = this.sql.executeQuery();
		dsConfig.sort("xh");

		DataMap rdm = new DataMap();
		rdm.put("dmlist", dsList.getRow(0));
		rdm.put("dsconfig", dsConfig);
		return rdm;
	}

	/**
	 * config信息的修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public final DataMap saveNoteConfigModify(final DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		DataSet dsConfig = para.getDataSet("gridConfig");
		if (StringUtil.chkStrNull(pzbh)) {
			throw new BizException("传入的批注编号为空。");
		}
		if (dsConfig == null) {
			throw new BizException("传入的批注配置信息为空。");
		}

		// 删除--先删后插
		this.sql.setSql(" delete from fw.note_config where dbid = ? and pzbh = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, pzbh);
		this.sql.executeUpdate();

		// 插入code
		this.sql.setSql(" insert into fw.note_config (dbid, pzbh, pznr, xh) values (?, ?, ?, ?) ");
		for (int i = 0, n = dsConfig.size(); i < n; i++) {
			String pznr = dsConfig.getString(i, "pznr");
			int xh = dsConfig.getInt(i, "xh");
			if (StringUtil.chkStrNull(pznr)) {
				throw new BizException("传入的批注内容为空");
			}
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, pzbh);
			this.sql.setString(3, pznr);
			this.sql.setInt(4, xh);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 记录一下日志
		this.log("SYS-A-WHDXPZXX", "维护典型批注信息", "A", this.getYhid(), "维护典型批注信息，典型批注编号："
				+ pzbh, "pzbh=" + pzbh);

		return null;
	}

	/**
	 * word转html-Str
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-7
	 * @since V1.0
	 */
	public final DataMap parseUploadWord(final DataMap para) throws Exception {
		CommonsMultipartFile word = (CommonsMultipartFile) para.get("word");
		if (null == word) {
			throw new BizException("上传的文件为空");
		}
		String fileName = word.getOriginalFilename(); // 文件名
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
			.toLowerCase();

		if (!"doc".equalsIgnoreCase(fileExt)
				&& !"zip".equalsIgnoreCase(fileExt)) {
			throw new BizException("文件类型必须为WORD格式，文件扩展名(.doc或者另存为htm文件的zip压缩包)");
		}

		// 对于输出，放到文件系统
		String uuid = StringUtil.getUUID();
		if ("doc".equalsIgnoreCase(fileExt)) {
			this.parseWord2HtmlStr(word, uuid);
		} else if ("zip".equalsIgnoreCase(fileExt)) {
			this.parseWordZip2HtmlStr(word, uuid);
		}

		// 返回
		DataMap rdm = new DataMap();
		rdm.put("uuid", uuid);
		return rdm;
	}

	/**
	 * 解析word到html字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-8
	 * @since V1.0
	 */
	private void parseWord2HtmlStr(CommonsMultipartFile word, String uuid) throws Exception {
		// 图片等数据临时存放目录
		String imgPath = "temp" + File.separator + GlobalVars.APP_ID
				+ File.separator + uuid;
		FileIOUtil.createPath(imgPath);

		// 文件转换word(.doc)-->html
		InputStream is = word.getInputStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			HWPFDocument wordDocument = new HWPFDocument(is);
			WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.newDocument());
			wordToHtmlConverter.setPicturesManager(new PicturesManager(){
				public String savePicture(byte[] content,
						PictureType pictureType, String suggestedName,
						float widthInches, float heightInches) {
					return suggestedName;
				}
			});
			wordToHtmlConverter.processDocument(wordDocument);

			// 保存图片
			List<Picture> pics = wordDocument.getPicturesTable()
				.getAllPictures();
			if (pics != null) {
				for (int i = 0; i < pics.size(); i++) {
					Picture pic = pics.get(i);
					try {
						FileOutputStream imgFileOs = new FileOutputStream(imgPath
								+ File.separator + pic.suggestFullFileName());
						pic.writeImageContent(imgFileOs);
						imgFileOs.flush();
						imgFileOs.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}

			// 转换操作
			Document htmlDocument = wordToHtmlConverter.getDocument();
			DOMSource domSource = new DOMSource(htmlDocument);
			StreamResult streamResult = new StreamResult(os);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(OutputKeys.METHOD, "html");
			serializer.transform(domSource, streamResult);

			// 得到html串，进行处理--图片的base64处理
			String resultHtmlStr = new String(os.toByteArray(), "UTF-8");
			org.jsoup.nodes.Document doc = Jsoup.parse(resultHtmlStr);
			Elements eles = doc.getElementsByTag("img");
			for (Element ele : eles) {
				String src = ele.attr("src");
				if (StringUtil.chkStrNull(src)) {
					continue;
				}
				src = src.trim();
				if (src.startsWith("data") || src.startsWith("DATA")) {
					continue;
				}
				String base64ImgStr = "";
				String imgExt = src.substring(src.lastIndexOf(".") + 1);
				File imgFile = new File(imgPath + File.separator + src);
				if ("wmf".equalsIgnoreCase(imgExt)) {// 公式等数据
					base64ImgStr = "data:image/svg+xml;base64,"
							+ SecUtil.base64Encode(TaglibBiz.wmfToSvg(FileIOUtil.getBytesFromFile(imgFile)));
				} else {// 图片
					base64ImgStr = "data:image/" + imgExt + ";base64,"
							+ FileIOUtil.getImageStrByImage(imgFile);
				}
				ele.attr("src", base64ImgStr);// 更换数据值
			}

			// 保存结果文件--到会话缓存中
			this.getRequest()
				.getSession()
				.setAttribute("html_result_" + uuid, doc.html());

			// 将图片目录删除
			FileIOUtil.deleteDirectory(imgPath);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * html-word的压缩包-将word另存为筛选过网页htm后将htm和图片文件夹一起压缩，后的zip文件
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2017-9-8
	 * @since V1.0
	 */
	private void parseWordZip2HtmlStr(CommonsMultipartFile word, String uuid) throws Exception {
		// 临时解压目录
		String tempPath = "temp" + File.separator + GlobalVars.APP_ID
				+ File.separator + uuid;
		FileIOUtil.createPath(tempPath);

		// 保存该文件到硬盘
		FileIOUtil.saveCommonsMultipartFileToServer(word, tempPath, "src_word.zip");

		// 解压目录
		String targetPath = tempPath + File.separator + "target";

		// 解压缩
		this.unZipFiles(new File(tempPath + File.separator + "src_word.zip"), targetPath);

		// 到解压目录下，找到html文件
		File targetFile = new File(targetPath);
		File[] arrayFile = targetFile.listFiles();
		if (arrayFile.length <= 0) {
			throw new BizException("上传的压缩文件中没有文件信息。");
		}
		// 循环查找html，htm文件
		File htmlFile = null;
		for (int i = 0; i < arrayFile.length; i++) {
			if (arrayFile[i].isFile()) {
				String name = arrayFile[i].getName();
				String ext = name.substring(name.lastIndexOf(".") + 1);
				if (ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("htm")) {
					htmlFile = arrayFile[i];
					break;
				}
			}
		}
		if (null == htmlFile) {
			throw new BizException("上传的文件中不存在html文件信息");
		}
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlFile, "gb2312");// 由于国内word进行另存为html的时候，默认的编码格式为gb2312,所以此处使用该编码进行读取。
		Elements eles = doc.getElementsByTag("img");
		for (Element ele : eles) {
			String src = ele.attr("src");
			if (StringUtil.chkStrNull(src)) {
				continue;
			}
			src = src.trim();
			if (src.startsWith("data") || src.startsWith("DATA")) {
				continue;
			}
			try {
				String imgFileName = src.substring(src.lastIndexOf("/") + 1);// 只取最后的文件名
				String imgExt = imgFileName.substring(imgFileName.lastIndexOf(".") + 1);
				String base64ImgStr = "data:image/"
						+ imgExt
						+ ";base64,"
						+ FileIOUtil.getImageStrByImage(new File(targetPath
								+ File.separator + imgFileName));
				ele.attr("src", base64ImgStr);// 更换数据值
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 保存结果文件--到会话缓存中
		this.getRequest()
			.getSession()
			.setAttribute("html_result_" + uuid, doc.html());

		// 将临时文件目录删除
		FileIOUtil.deleteDirectory(tempPath);
	}

	/**
	 * 解压文件到指定目录 解压后的文件名，和之前一致[解压目录不保留其层次关系]
	 * 
	 * @param zipFile 待解压的zip文件
	 * @param descDir 指定目录
	 */
	private void unZipFiles(File zipFile, String descDir) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		try {
			File pathFile = new File(descDir);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String zipEntryName = entry.getName();
				if (StringUtil.chkStrNull(zipEntryName)) {
					continue;
				}
				String outPath = (descDir + File.separator + zipEntryName.substring(zipEntryName.lastIndexOf("/") + 1)).replaceAll("\\*", "/");
				// 判断文件全路径是否为文件夹,不需要解压
				if (new File(outPath).isDirectory()) {
					continue;
				}
				FileOutputStream out = new FileOutputStream(outPath);
				InputStream in = zip.getInputStream(entry);
				try {
					byte[] buf1 = new byte[1024];
					int len;
					while ((len = in.read(buf1)) > 0) {
						out.write(buf1, 0, len);
					}

				} finally {
					try {
						in.close();
						out.close();
					} catch (Exception e) {
					}
				}
			}
		} finally {
			try {
				zip.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * wmf转svg
	 * 
	 * @author yjc
	 * @throws
	 * @throws Exception
	 * @date 创建时间 2017-8-7
	 * @since V1.0
	 */
	public static byte[] wmfToSvg(byte[] imgByte) throws Exception {
		boolean compatible = false;
		ByteArrayInputStream bis = new ByteArrayInputStream(imgByte);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] outByte;
		try {
			WmfParser parser = new WmfParser();
			final SvgGdi gdi = new SvgGdi(compatible);
			parser.parse(bis, gdi);
			Document doc = gdi.getDocument();
			TaglibBiz.output4WmfToSvg(doc, bos);
			outByte = bos.toByteArray();
			return outByte;
		} finally {
			try {
				bis.close();
				bos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 输出信息
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-7
	 * @since V1.0
	 */
	public static void output4WmfToSvg(Document doc, OutputStream out) throws Exception {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD SVG 1.1//EN");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
		transformer.transform(new DOMSource(doc), new StreamResult(out));
	}
}
