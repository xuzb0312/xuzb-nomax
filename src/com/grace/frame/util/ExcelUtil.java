package com.grace.frame.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import net.sf.json.JSONNull;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;

/**
 * Excel相关的操作
 * 
 * @author yjc
 */
public class ExcelUtil{
	public final static int ONE_SHEET_ROW_COUNT = 60000;// 每个sheet页放置的数据行数

	/**
	 * 文件导出到excel
	 * 
	 * @author yjc
	 * @throws UnsupportedEncodingException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static void exportData2Response(HttpServletResponse response,
			String fileName, DataSet dsCols, DataSet dsData) throws AppException, UnsupportedEncodingException {
		ExcelUtil.exportData2Response(response, fileName, dsCols, null, dsData);
	}

	/**
	 * 文件导出到excel(表头合并2行-跟前台grid一样)
	 * 
	 * @author yjc
	 * @throws UnsupportedEncodingException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static void exportData2Response(HttpServletResponse response,
			String fileName, DataSet dsCols, DataSet dsMerge, DataSet dsData) throws AppException, UnsupportedEncodingException {
		if (null == response) {
			throw new AppException("response为空", "ExcelUtil");
		}
		if (StringUtil.chkStrNull(fileName)) {
			throw new AppException("fileName为空", "ExcelUtil");
		}
		if (null == dsCols) {
			throw new AppException("dsCols为空", "ExcelUtil");
		}
		if (null == dsData) {
			throw new AppException("dsData为空", "ExcelUtil");
		}

		OutputStream out = null;
		try {
			response.resetBuffer();
			response.setContentType("application/msexcel;charset=UTF-8");// 定义输出类型-excel-屏蔽迅雷等下载工具
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName + ".xls");

			out = response.getOutputStream();
			WritableWorkbook wwb = ExcelUtil.createWorkbook(out);

			if (null == dsMerge || dsMerge.size() <= 0) {// 是否有header合并-没有
				// 超过ExcelUtil.ONE_SHEET_ROW_COUNT行自动创建多个sheet
				int sheetcount = (dsData.size() / (ExcelUtil.ONE_SHEET_ROW_COUNT + 1)) + 1;
				for (int i = 0; i < sheetcount; i++) {
					WritableSheet ws = ExcelUtil.createSheet(wwb, "导出数据("
							+ (i + 1) + ")", i);

					ExcelUtil.writeTitleToSheet(ws, 0, dsCols);
					int start = (i * ExcelUtil.ONE_SHEET_ROW_COUNT);
					int end = (i + 1) * ExcelUtil.ONE_SHEET_ROW_COUNT - 1;
					if (end >= dsData.size()) {
						end = dsData.size() - 1;
					}
					DataSet dsSheetData = dsData.subDataSet(start, end);
					ExcelUtil.writeDataSetToSheet(ws, 1, dsCols, dsSheetData);
					// 进行合计的处理，如果存在合计列，则最后一行增加合计数据
					if (i == (sheetcount - 1)) {// 最后一个sheet
						ExcelUtil.writeSumToSheet(ws, 1 + dsSheetData.size(), dsCols, dsData);
					}
				}
			} else {
				// 超过ExcelUtil.ONE_SHEET_ROW_COUNT行自动创建多个sheet
				int sheetcount = (dsData.size() / (ExcelUtil.ONE_SHEET_ROW_COUNT + 1)) + 1;
				for (int i = 0; i < sheetcount; i++) {
					WritableSheet ws = ExcelUtil.createSheet(wwb, "导出数据("
							+ (i + 1) + ")", i);

					ExcelUtil.writeTitleToSheet4Merge(ws, 0, dsCols, dsMerge);
					int start = (i * ExcelUtil.ONE_SHEET_ROW_COUNT);
					int end = (i + 1) * ExcelUtil.ONE_SHEET_ROW_COUNT - 1;
					if (end >= dsData.size()) {
						end = dsData.size() - 1;
					}

					DataSet dsSheetData = dsData.subDataSet(start, end);
					ExcelUtil.writeDataSetToSheet(ws, 2, dsCols, dsSheetData);// 合并前2行为标题-从第二行写数据

					// 进行合计的处理，如果存在合计列，则最后一行增加合计数据
					if (i == (sheetcount - 1)) {// 最后一个sheet
						ExcelUtil.writeSumToSheet(ws, 2 + dsSheetData.size(), dsCols, dsData);
					}
				}
			}

			ExcelUtil.writeWbootAndClose(wwb);
			ExcelUtil.closeOutputStream(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				throw new AppException("在构建excel文件出现错误。" + e.getMessage());
			}
		}
	}

	/**
	 * 写入合计数据
	 * 
	 * @author yjc
	 */
	public static void writeSumToSheet(WritableSheet rs, int line,
			DataSet dsCols, DataSet dsData) throws AppException, RowsExceededException, WriteException {
		DataSet dsSumData = new DataSet();
		boolean hasSum = false;
		for (int i = 0; i < dsCols.size(); i++) {
			String type = dsCols.getString(i, "type");
			String mask = dsCols.getString(i, "mask");
			if ("number".equalsIgnoreCase(type) && dsCols.getBoolean(i, "sum")) {
				double value = 0.0;
				for (int j = 0, m = dsData.size(); j < m; j++) {
					if (dsData.containsItem(j, dsCols.getString(i, "name"))) {
						value = value
								+ dsData.getDouble(j, dsCols.getString(i, "name"));
					}
				}
				value = MathUtil.round(value, 8);// 设置一下四舍五入
				dsSumData.addRow()
					.add("mask", mask)
					.add("value", value)
					.add("sum", true);
				hasSum = true;// 存在合计
			} else {
				dsSumData.addRow()
					.add("mask", null)
					.add("value", null)
					.add("sum", false);
			}
		}
		if (!hasSum) {
			// 如果没有合计，直接返回
			return;
		}
		for (int i = 0, n = dsSumData.size(); i < n; i++) {
			WritableCell cell;
			if (dsSumData.getBoolean(i, "sum")) {
				double value = dsSumData.getDouble(i, "value");
				String mask = dsSumData.getString(i, "mask");
				NumberFormat nf = new NumberFormat(mask);
				WritableCellFormat cellFormat = new WritableCellFormat(nf);
				cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

				WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
				cellFormat.setFont(font);
				cell = new jxl.write.Number(i, line, value, cellFormat);
			} else {
				String value = "";
				if (i == 0) {
					value = "合计";
				}
				cell = new Label(i, line, value, EFWritableCellFormat.getHeaderCellFormat());
			}
			rs.addCell(cell);
		}
	}

	/**
	 * 创建列格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addColumnsRow4ExcelUtil(DataSet dsCols, String name,
			String header, String type, String mask) throws Exception {
		return ExcelUtil.addColumnsRow4ExcelUtil(dsCols, name, header, type, mask, false);
	}

	/**
	 * 创建列格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addColumnsRow4ExcelUtil(DataSet dsCols, String name,
			String header, String type, String mask, boolean sum) throws Exception {
		if (null == dsCols) {
			dsCols = new DataSet();
		}
		if (StringUtil.chkStrNull(name)) {
			throw new AppException("name为空", "ExcelUtil");
		}
		if (StringUtil.chkStrNull(type)) {
			throw new AppException("type为空", "ExcelUtil");
		}
		if (StringUtil.chkStrNull(header)) {
			header = "";
		}
		if (StringUtil.chkStrNull(mask)) {
			mask = "";
		}

		// 增加一行
		dsCols.addRow();
		int row = dsCols.size() - 1;
		dsCols.put(row, "name", name);
		dsCols.put(row, "type", type);
		dsCols.put(row, "header", header);
		dsCols.put(row, "mask", mask);
		dsCols.put(row, "sum", sum);// 是否合计

		return dsCols;
	}

	/**
	 * 创建列格式--对于插入图片格式的列设置
	 * <p>
	 * 对于int width, int height的单位不明确。 提供的jar包也不明确是何意思。 在使用的时候自己根据展示效果调整。 <br>
	 * width单位较大，但不明确具体是什么单位（网上资料显示是字符宽度）。height单位较小，看网上资料显示约为1/20个像素。<br>
	 * 所以在使用过程中根据实际情况进行微调。<br>
	 * https://blog.csdn.net/liuyukuan/article/details/79177319
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addImageColumnsRow4ExcelUtil(DataSet dsCols,
			String name, String header, int width, int height) throws Exception {
		if (null == dsCols) {
			dsCols = new DataSet();
		}
		if (StringUtil.chkStrNull(name)) {
			throw new AppException("name为空", "ExcelUtil");
		}
		if (StringUtil.chkStrNull(header)) {
			header = "";
		}
		if (width <= 0) {
			throw new AppException("width参数必须大于0", "ExcelUtil");
		}
		if (height <= 0) {
			throw new AppException("height参数必须大于0", "ExcelUtil");
		}
		// 增加一行
		dsCols.addRow();
		int row = dsCols.size() - 1;
		dsCols.put(row, "name", name);
		dsCols.put(row, "type", "image");
		dsCols.put(row, "header", header);
		dsCols.put(row, "mask", "");
		dsCols.put(row, "width", width);
		dsCols.put(row, "height", height);
		return dsCols;
	}

	/**
	 * 根据流来获取可写的WorkBook
	 * 
	 * @author yjc
	 */
	public static WritableWorkbook createWorkbook(OutputStream out) throws AppException {
		WritableWorkbook rwb = null;
		try {
			rwb = Workbook.createWorkbook(out);
		} catch (Exception e) {
			throw new AppException("文件写入异常，写文件流时出错!错误信息为：" + e.getMessage());
		}
		return rwb;
	}

	/**
	 * 往sheet中回写title
	 */
	public static void writeTitleToSheet(WritableSheet rs, int line,
			DataSet dsCols) throws AppException, RowsExceededException, WriteException {
		for (int i = 0; i < dsCols.size(); i++) {
			WritableCell cell = new Label(i, line, dsCols.getString(i, "header"), EFWritableCellFormat.getHeaderCellFormat());
			rs.addCell(cell);

			// 在设置标题时，同时设置宽度20
			rs.setColumnView(i, 20);
		}
	}

	/**
	 * 往sheet中回写title-标题合并
	 */
	public static void writeTitleToSheet4Merge(WritableSheet rs, int line,
			DataSet dsCols, DataSet dsMerge) throws AppException, RowsExceededException, WriteException {
		// 合并信息
		HashMap<String, Object[]> mapMerge = new HashMap<String, Object[]>();
		for (int i = 0, n = dsMerge.size(); i < n; i++) {
			String startname = dsMerge.getString(i, "startname");
			int num = dsMerge.getInt(i, "num");
			String title = dsMerge.getString(i, "title");
			mapMerge.put(startname, new Object[] { num, title });
		}
		int mergeNum = 0;
		for (int i = 0; i < dsCols.size(); i++) {
			String name = dsCols.getString(i, "name");
			if (mapMerge.containsKey(name)) {
				Object[] arrMerge = mapMerge.get(name);
				mergeNum = (Integer) arrMerge[0];
				String title = (String) arrMerge[1];

				// 合并-横向
				rs.mergeCells(i, line, i + mergeNum - 1, line);

				// 放合并标题
				WritableCell cellM = new Label(i, line, title, EFWritableCellFormat.getHeaderCellFormat());
				rs.addCell(cellM);

				// 放单独标题
				WritableCell cellC = new Label(i, line + 1, dsCols.getString(i, "header"), EFWritableCellFormat.getHeaderCellFormat());
				rs.addCell(cellC);

				mergeNum--;// 此处已经合并一个
			} else {
				if (mergeNum > 0) {
					WritableCell cellC = new Label(i, line + 1, dsCols.getString(i, "header"), EFWritableCellFormat.getHeaderCellFormat());
					rs.addCell(cellC);
					mergeNum--;// 此处已经合并一个
				} else {
					// 合并-纵向
					rs.mergeCells(i, line, i, line + 1);

					WritableCell cell = new Label(i, line, dsCols.getString(i, "header"), EFWritableCellFormat.getHeaderCellFormat());
					rs.addCell(cell);
				}
			}
			rs.setColumnView(i, 20);// 在设置标题时，同时设置宽度20
		}
	}

	/**
	 * 产生一个可写的sheet
	 * 
	 * @author yjc
	 */
	public static WritableSheet createSheet(WritableWorkbook rwb,
			String sheetName, int sheetNumber) throws AppException {
		WritableSheet rs = rwb.createSheet(sheetName, sheetNumber);
		return rs;
	}

	/**
	 * 往sheet中回写vds exportInfo中的结构必须是：name,header,type,mask.
	 * 
	 * @author yjc
	 */
	public static void writeDataSetToSheet(WritableSheet rs, int startLine,
			DataSet dsCols, DataSet dsData) throws AppException, RowsExceededException, WriteException {
		for (int i = 0; i < dsCols.size(); i++) {
			String type = dsCols.getString(i, "type");
			String mask = dsCols.getString(i, "mask");
			for (int j = startLine; j < startLine + dsData.size(); j++) {
				WritableCell cell;
				if ("string".equalsIgnoreCase(type)) {
					String value = "";
					if (dsData.containsItem(j - startLine, dsCols.getString(i, "name"))) {
						Object obj = dsData.getObject(j - startLine, dsCols.getString(i, "name"));
						if (null != obj && !(obj instanceof JSONNull)) {
							value = String.valueOf(obj);
						}
					}
					cell = new Label(i, j, value, EFWritableCellFormat.getTextCellFormat());
				} else if ("date".equalsIgnoreCase(type)) {
					Date value = null;
					if (dsData.containsItem(j - startLine, dsCols.getString(i, "name"))) {
						value = dsData.getDate(j - startLine, dsCols.getString(i, "name"));
					}
					cell = writeDateCellValue(i, mask, j, value);
				} else if ("number".equalsIgnoreCase(type)) {
					double value = 0.0;
					if (dsData.containsItem(j - startLine, dsCols.getString(i, "name"))) {
						value = dsData.getDouble(j - startLine, dsCols.getString(i, "name"));
					}
					cell = writeNumberCellValue(i, mask, j, value);
				} else if ("image".equalsIgnoreCase(type)) {// 图片格式--特殊
					byte[] imageData = (byte[]) dsData.getObject(j - startLine, dsCols.getString(i, "name"));
					int width = dsCols.getInt(i, "width");
					int height = dsCols.getInt(i, "height");
					rs.setColumnView(i, width);// 调整单元格的高和宽
					rs.setRowView(j, height);
					WritableImage image = new WritableImage(i, j, 1, 1, imageData);
					rs.addImage(image);
					continue;// 对于图片插入和cell插入不一致，所以此处进行调整
				} else {
					String value = "";
					if (dsData.containsItem(j - startLine, dsCols.getString(i, "name"))) {
						Object obj = dsData.getObject(j - startLine, dsCols.getString(i, "name"));
						if (null != obj && !(obj instanceof JSONNull)) {
							value = String.valueOf(obj);
						}
					}
					cell = new Label(i, j, value, EFWritableCellFormat.getTextCellFormat());
				}
				rs.addCell(cell);
			}
		}
	}

	/**
	 * 写日期格式单元格
	 * 
	 * @author yjc
	 * @throws WriteException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static WritableCell writeDateCellValue(int i, String mask, int j,
			Date value) throws AppException, WriteException {
		WritableCell cell;
		if (value == null) {
			cell = new jxl.write.Blank(i, j);
			cell.setCellFormat(EFWritableCellFormat.getDateCellFormat(mask));
		} else {
			cell = new jxl.write.DateTime(i, j, value, EFWritableCellFormat.getDateCellFormat(mask));
		}
		return cell;
	}

	/**
	 * 写数字格式单元格
	 * 
	 * @author yjc
	 * @throws WriteException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static WritableCell writeNumberCellValue(int i, String mask, int j,
			double value) throws AppException, WriteException {
		WritableCell cell;
		cell = new jxl.write.Number(i, j, value, EFWritableCellFormat.getNumberCellFormat(mask));
		return cell;
	}

	/**
	 * 关闭可写的sheet
	 */
	public static void writeWbootAndClose(WritableWorkbook rwb) throws Exception {
		try {
			EFWritableCellFormat.clearCellFormatMap();
			rwb.write();
			rwb.close();
		} catch (Exception e) {
			// 用户取消下载
		}
	}

	/**
	 * 关闭输出流
	 */
	public static void closeOutputStream(OutputStream out) throws AppException {
		try {
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			throw new AppException("在下载时构建excel文件出现错误。" + e.getMessage());
		}
	}

	/**
	 * 根据CommonsMultipartFile来获取输入流.
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static InputStream getStreamByFile(CommonsMultipartFile file) throws AppException, BizException {
		InputStream inputstream = null;
		if (file != null) {
			String fileName = file.getOriginalFilename();
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
				.toLowerCase();// 获取文件扩展名
			if ("xlsx".equals(fileExt)) {
				throw new BizException("目前暂不支持office2007，请用转成xls文件");
			}
			if (!"xls".equals(fileExt)) {
				throw new BizException("无法获取非Excel文件的输入流。");
			}
			try {
				inputstream = new ByteArrayInputStream(file.getBytes());
			} catch (Exception e) {
				throw new AppException("文件读取异常，获取文件流时出错!错误信息为："
						+ e.getMessage());
			}
		}
		return inputstream;
	}

	/**
	 * 根据输入流来获取Workbook.
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static Workbook getExcelFileByStream(InputStream inputstream) throws AppException {
		Workbook rwb = null;
		try {
			WorkbookSettings ws = new WorkbookSettings();
			ws.setCellValidationDisabled(true);
			rwb = Workbook.getWorkbook(inputstream, ws);
		} catch (Exception e) {
			throw new AppException("文件读取异常，获取文件流时出错!错误信息为：" + e.getMessage());
		}
		return rwb;
	}

	/**
	 * 根据WorkBook获取Sheet页.
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static Sheet getSheet(Workbook rwb, int sheetNumber) throws AppException {
		Sheet rs = rwb.getSheet(sheetNumber);
		return rs;
	}

	/**
	 * 关闭读取的Excel文件.
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static void closeWorkbook(Workbook rwb) throws AppException {
		try {
			rwb.close();
		} catch (Exception e) {
			throw new AppException("文件读取异常，关闭文件流时出错!错误信息为：" + e.getMessage());
		}
	}

	/**
	 * 关闭输入流.
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static void closeInputStream(InputStream inputstream) throws AppException {
		try {
			if (inputstream != null) {
				inputstream.close();
				inputstream = null;
			}
		} catch (Exception e) {
			throw new AppException("文件读取异常，关闭文件流时出错!错误信息为：" + e.getMessage());
		}
	}

	/**
	 * 当文本列中，没有文本（包括只含有空格或者制表符的情况），日期列为null，数字列为0.0的情况 用来去除excel读取出来的数据中的空行.
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public static DataSet removeBlankRowWithTrim(DataSet pds) throws AppException {
		DataSet ds = new DataSet();
		for (int i = 0; i < pds.size(); i++) {
			boolean blankRow = true;
			DataMap row = pds.get(i);

			Iterator iterator = row.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				// 判断是否为null,是否为空格，是否为0.0
				if (entry.getValue() != null
						&& !entry.getValue().toString().trim().equals("")
						&& !entry.getValue().equals(new Double(0.0))) {
					blankRow = false;
					break;
				}
			}
			if (!blankRow) {
				ds.add(row);
			}
		}
		return ds;
	}

	/**
	 * 根据开发人员提供的tableInfo来解析导入的数据。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static DataSet getDataSetBySheet(Sheet rs, int titleLine,
			int startLine, int endLine, DataSet tableInfo) throws Exception {
		DataSet vds = new DataSet();
		DataMap temp;
		String[] title = null;
		int length = 0;

		if (titleLine >= startLine) {
			throw new BizException("标题行行号大于等于开始行数，请检查");
		}

		if (startLine > endLine) {
			throw new BizException("开始行行号大于结束行行号");
		}
		if (rs.getRows() < endLine) {
			endLine = rs.getRows();
		}
		int titleLenght = 0;
		if (titleLine != -1) {
			title = ExcelUtil.getColumnsBySheet(rs, titleLine);
			titleLenght = title.length;
			if (tableInfo == null || tableInfo.size() == 0) {
				tableInfo = new DataSet();
				length = titleLenght;
			} else {
				length = tableInfo.size();
			}
		} else {
			titleLenght = rs.getColumns();
			if (tableInfo == null || tableInfo.size() == 0) {
				throw new BizException("没有定义列结构，不能导入");
			} else {
				length = tableInfo.size();
			}
		}

		String[] keyArray = new String[length];
		String[] valueArray = new String[length];
		String[] typeArray = new String[length];
		String[] requiredArray = new String[length];

		for (int i = 0; i < length; i++) {
			if (tableInfo.size() > 0 && tableInfo.containsItem(i, "columnname")) {
				valueArray[i] = tableInfo.getString(i, "columnname");
			} else {
				valueArray[i] = title[i];
			}
			if (tableInfo.size() > 0 && tableInfo.containsItem(i, "name")) {
				keyArray[i] = tableInfo.getString(i, "name");
			} else {
				keyArray[i] = valueArray[i];
			}
			if (tableInfo.size() > 0 && tableInfo.containsItem(i, "type")) {
				typeArray[i] = tableInfo.getString(i, "type");
			}
			if (tableInfo.size() > 0 && tableInfo.containsItem(i, "required")) {
				requiredArray[i] = tableInfo.getString(i, "required");
			} else {
				requiredArray[i] = "true";
			}
		}

		if (typeArray.length > 0 && typeArray[0] != null
				&& !typeArray[0].equalsIgnoreCase("")) {
			LinkedHashMap<String, String> typeMap = new LinkedHashMap<String, String>();
			for (int i = 0; i < typeArray.length; i++) {
				typeMap.put(keyArray[i], typeArray[i].toLowerCase());
			}
			vds.setTypeList(typeMap);
		}

		// 解析列设置
		String key, type, columnName, required;
		for (int i = startLine; i < endLine; i++) {
			temp = new DataMap();
			for (int j = 0; j < keyArray.length; j++) {
				key = keyArray[j];
				type = typeArray[j];
				columnName = valueArray[j];
				required = requiredArray[j];
				int index = -1;
				if (title != null) {
					for (int j2 = 0; j2 < title.length; j2++) {
						if (title[j2].equalsIgnoreCase(columnName)) {
							index = j2;
							break;
						}
					}
					if (index == -1) {
						if (required.equalsIgnoreCase("true")) {
							throw new BizException("Excel中不存在列名为【" + columnName
									+ "】的列");
						} else {
							continue;
						}
					}
				} else {
					index = j;
				}
				Cell cell = rs.getCell(index, i);
				if (type != null && !type.equalsIgnoreCase("")) {
					if (type.equalsIgnoreCase("string")) {
						String value = "";
						if (cell.getType() == CellType.LABEL) {
							LabelCell textCell = (LabelCell) cell;
							value = textCell.getString();
						} else if (cell.getType() == CellType.NUMBER) {
							value = cell.getContents();
						} else if (cell.getType() == CellType.DATE) {
							DateCell dateCell = (DateCell) cell;
							value = DateUtil.dateToString(dateCell.getDate());
						} else if (cell.getType() == CellType.EMPTY) {
							value = "";
						} else {
							value = cell.getContents();
						}
						temp.put(key, value);
					} else if (type.equalsIgnoreCase("number")) {
						double value;
						if (cell.getType() == CellType.LABEL) {
							LabelCell textCell = (LabelCell) cell;
							try {
								String text = textCell.getString();
								if (text.trim().equalsIgnoreCase("")) {
									value = 0.0;// 当数字列中有空格的处理方式
								} else {
									value = Double.valueOf(textCell.getString())
										.doubleValue();
								}
							} catch (Exception e) {
								throw new BizException("第" + (i + 1) + "行第"
										+ (j + 1) + "列期望数字类型的值，但存在文本类型的值");
							}
						} else if (cell.getType() == CellType.NUMBER
								|| cell.getType() == CellType.NUMBER_FORMULA) {
							NumberCell numberCell = (NumberCell) cell;
							value = numberCell.getValue();
						} else if (cell.getType() == CellType.DATE) {
							throw new BizException("第" + (i + 1) + "行第"
									+ (j + 1) + "列期望数字类型的值，但存在日期类型的值");
						} else if (cell.getType() == CellType.EMPTY) {
							value = 0.0;
						} else {
							throw new BizException("第" + (i + 1) + "行第"
									+ (j + 1) + "列期望数字类型的值，但存在非法类型的值");
						}
						temp.put(key, value);
					} else if (type.equalsIgnoreCase("date")) {
						Date date = null;

						if (cell.getType() == CellType.LABEL) {
							LabelCell textCell = (LabelCell) cell;
							try {
								String text = textCell.getString();
								if (text.trim().equalsIgnoreCase("")) {
									date = null;// //当日期列中有空格的处理方式
								} else {
									date = DateUtil.stringToDate(textCell.getString());
								}
							} catch (Exception e) {
								throw new BizException("第" + (i + 1) + "行第"
										+ (j + 1) + "列期望日期类型的值，但存在文本类型的值");
							}
						} else if (cell.getType() == CellType.NUMBER) {
							throw new BizException("第" + (i + 1) + "行第"
									+ (j + 1) + "列期望日期类型的值，但存在数字类型的值");
						} else if (cell.getType() == CellType.DATE
								|| cell.getType() == CellType.DATE_FORMULA) {
							DateCell dateCell = (DateCell) cell;
							date = dateCell.getDate();
						} else if (cell.getType() == CellType.EMPTY) {
							date = null;
						} else {
							throw new BizException("第" + (i + 1) + "行第"
									+ (j + 1) + "列期望日期类型的值，但存在非法类型的值");
						}
						temp.put(key, date);
					} else {
						throw new BizException("第" + (j + 1) + "列定义了非法的数据类型");
					}
				} else {
					if (cell.getType() == CellType.LABEL) {
						LabelCell textCell = (LabelCell) cell;
						temp.put(key, textCell.getString());
					} else if (cell.getType() == CellType.NUMBER) {
						NumberCell numberCell = (NumberCell) cell;
						temp.put(key, numberCell.getValue());
					} else if (cell.getType() == CellType.DATE) {
						DateCell dateCell = (DateCell) cell;
						temp.put(key, dateCell.getDate());
					} else if (cell.getType() == CellType.EMPTY) {
						temp.put(key, null);
					} else {
						temp.put(key, cell.getContents());
					}
				}
			}
			vds.add(temp);
		}
		return vds;
	}

	/**
	 * 创建列格式-用于解析ds数据
	 * 
	 * @param columnName Excel的列名
	 * @param name ds的列名
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet buildTableInfo4ParseDataSet(DataSet dsCols,
			String columnName, String name, String type, boolean required) throws Exception {

		if (null == dsCols) {
			dsCols = new DataSet();
		}
		if (StringUtil.chkStrNull(name)) {
			throw new AppException("name为空", "ExcelUtil");
		}
		if (StringUtil.chkStrNull(columnName)) {
			throw new AppException("columnName为空", "ExcelUtil");
		}
		if (StringUtil.chkStrNull(type)) {
			throw new AppException("type为空", "ExcelUtil");
		}

		// 增加一行
		dsCols.addRow();
		int row = dsCols.size() - 1;
		dsCols.put(row, "columnname", columnName);
		dsCols.put(row, "name", name);
		dsCols.put(row, "type", type);
		dsCols.put(row, "required", required ? "true" : "false");

		return dsCols;
	}

	/**
	 * 从sheet中获取标题行
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static String[] getColumnsBySheet(Sheet rs, int line) throws AppException {
		Cell[] title = rs.getRow(line);
		String[] keySet = new String[title.length];
		for (int i = 0; i < title.length; i++) {
			keySet[i] = title[i].getContents();
		}
		return keySet;
	}

	/**
	 * 从excel文件中解析出dataSet数据
	 * <p>
	 * Excel格式必须是第一行标题.
	 * </p>
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static DataSet parseExcel2DataSet(CommonsMultipartFile file,
			DataSet tableinfo) throws Exception {
		InputStream inputStream = ExcelUtil.getStreamByFile(file);
		Workbook wb = ExcelUtil.getExcelFileByStream(inputStream);
		Sheet sheet = ExcelUtil.getSheet(wb, 0);
		DataSet ds = ExcelUtil.getDataSetBySheet(sheet, 0, 1, sheet.getRows(), tableinfo);
		ds = ExcelUtil.removeBlankRowWithTrim(ds); // 去除空行

		ExcelUtil.closeWorkbook(wb);
		ExcelUtil.closeInputStream(inputStream);

		return ds;
	}
}