package com.grace.frame.util;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * PDFUTIL-dataset->pdf的转换操作
 * 
 * @author yjc
 */
public class PDFUtil{

	public static int DEFAULT_WIDTH = 10;// 默认宽度
	public static int DEFAULT_HEIGHT = 50;// 默认高度

	public static double SCAL_WIDTH = 15;// 缩放比例

	/**
	 * 将数据导出到文件下载
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-18
	 * @since V1.0
	 */
	public static void exportData2Response(HttpServletResponse response,
			String fileName, DataSet dsCols, DataSet data) throws Exception {
		if (null == response) {
			throw new AppException("response为空", "XMLUtil");
		}
		if (StringUtil.chkStrNull(fileName)) {
			throw new AppException("fileName为空", "XMLUtil");
		}
		if (null == dsCols) {
			throw new AppException("dsCols为空", "XMLUtil");
		}
		if (null == data) {
			throw new AppException("data为空", "XMLUtil");
		}

		// 文件下载的操作
		String pdfTitle = fileName;
		fileName = fileName + ".pdf";
		response.resetBuffer();
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.setContentType("application/x-dbf;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ fileName);

		int pageWidth = 0;
		float[] widths = new float[dsCols.size()];
		for (int i = 0, n = dsCols.size(); i < n; i++) {
			int width = dsCols.getInt(i, "width");
			if (0 == width) {
				width = PDFUtil.DEFAULT_WIDTH;
			}
			pageWidth = pageWidth + width;

			// 列宽
			widths[i] = (float) width;
		}

		double pageWidth_real = ((pageWidth + 6) * PDFUtil.SCAL_WIDTH);
		double pageHeight_real = ((PDFUtil.DEFAULT_HEIGHT + 10) * PDFUtil.SCAL_WIDTH);
		Document document = new Document(new Rectangle((float) pageWidth_real, (float) pageHeight_real), 0, 0, 30, 30);
		OutputStream os = response.getOutputStream();
		try {
			PdfWriter.getInstance(document, os);

			// 文档信息
			document.addAuthor(GlobalVars.APP_NAME);
			document.addCreationDate();
			document.addTitle(pdfTitle);
			document.addSubject(pdfTitle);

			// 页脚
			HeaderFooter footer = new HeaderFooter(new Phrase(), true);
			footer.setBorder(0);// 设置页脚是否有边框,0表示无,1上边框,2下边框,3上下边框都有 默认都有
			footer.setAlignment(Element.ALIGN_CENTER); // 设置页脚的对齐方式
			document.setFooter(footer); // 将页脚添加到文档中

			// 打开文档
			document.open();

			// pdf操作区
			// A.增加一个标题
			BaseFont baseFont = PDFUtil.getChineseFont();
			Font titleFont = new Font(baseFont, 20, Font.NORMAL);
			Paragraph titleP = new Paragraph(pdfTitle + "\n", titleFont);
			titleP.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(titleP);

			// B.内容字体
			Font contentFont = new Font(baseFont, 9, Font.NORMAL);

			// C.构建表头
			PdfPTable table = new PdfPTable(widths);
			table.setSpacingBefore(20f);
			table.setWidthPercentage(95);// 设置表格宽度为95%

			for (int i = 0, n = dsCols.size(); i < n; i++) {
				String header = dsCols.getString(i, "header");
				int width = dsCols.getInt(i, "width");
				if (0 == width) {
					width = PDFUtil.DEFAULT_WIDTH;
				}

				// 单元格
				PdfPCell cellTemp = new PdfPCell(new Paragraph(header, contentFont));
				cellTemp.setBackgroundColor(Color.LIGHT_GRAY);
				cellTemp.setVerticalAlignment(Element.ALIGN_CENTER);
				cellTemp.setHorizontalAlignment(Element.ALIGN_CENTER);

				table.addCell(cellTemp);// 增加单元cell
			}

			// 输出数据
			for (int i = 0, n = data.size(); i < n; i++) {
				for (int j = 0, m = dsCols.size(); j < m; j++) {
					String name = dsCols.getString(j, "name");
					Object obj_value = data.get(i).get(name, "");
					if (null == obj_value) {
						obj_value = "";
					}

					// 单元格
					PdfPCell cellTemp = new PdfPCell(new Paragraph(String.valueOf(obj_value), contentFont));
					cellTemp.setBackgroundColor(Color.WHITE);
					cellTemp.setVerticalAlignment(Element.ALIGN_CENTER);
					cellTemp.setHorizontalAlignment(Element.ALIGN_CENTER);

					table.addCell(cellTemp);// 增加单元cell
				}
			}

			// 增加表格
			document.add(table);

			// 文档关闭
			document.close();
			os.flush();
		} catch (Exception e) {
			throw new AppException("构建PDF异常：" + e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				throw new AppException("构建PDF异常!错误信息为：" + e.getMessage());
			}
		}
	}

	/**
	 * 获取中文字体
	 * 
	 * @author yjc
	 * @date 创建时间 2017-6-8
	 * @since V1.0
	 */
	public static BaseFont getChineseFont() throws DocumentException, IOException {
		BaseFont baseFont = null;
		java.util.Properties prop = System.getProperties();
		String osName = prop.getProperty("os.name").toLowerCase();
		try {
			if (osName.indexOf("linux") > -1) {
				try {
					// 首先从/usr/share/fonts/windowsFonts/目录寻找字体文件，如果不存在，则到上级目录找，如果还是不存在则加载默认
					baseFont = BaseFont.createFont("/usr/share/fonts/windowsFonts/simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				} catch (Exception e) {
					baseFont = BaseFont.createFont("/usr/share/fonts/simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				}
			} else {
				baseFont = BaseFont.createFont("C:/Windows/Fonts/simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			}
		} catch (Exception e) {
			System.out.println("获取字体出现异常：" + e.getMessage()
					+ ",已经自动修正从系统中获取基础字体。");
			baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);// 基础字体
		}
		return baseFont;
	}

	/**
	 * 创建列格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addColumnsRow4PDFUtil(DataSet dsCols, String name,
			String header, int width) throws Exception {
		if (null == dsCols) {
			dsCols = new DataSet();
		}
		if (StringUtil.chkStrNull(name)) {
			throw new AppException("name为空", "TxtUtil");
		}
		if (StringUtil.chkStrNull(header)) {
			header = "";
		}

		// 增加一行
		dsCols.addRow();
		int row = dsCols.size() - 1;
		dsCols.put(row, "name", name);
		dsCols.put(row, "header", header);
		dsCols.put(row, "width", width);

		return dsCols;
	}
}
