package com.grace.frame.util;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

import com.grace.frame.exception.AppException;

/**
 * 单元格格式
 * 
 * @author yjc
 */
public class EFWritableCellFormat{

	private static final ThreadLocal<DataMap> cfMap = new ThreadLocal<DataMap>();// 因为一个sheet里的format数量有限制的

	/**
	 * 文本格式
	 * 
	 * @author yjc
	 * @throws WriteException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static WritableCellFormat getTextCellFormat() throws AppException, WriteException {
		WritableCellFormat cellFormat;
		DataMap CellFormatMap = (DataMap) cfMap.get();

		if (CellFormatMap == null) {
			CellFormatMap = new DataMap();
		}

		if (CellFormatMap.containsKey("text")) {
			cellFormat = (WritableCellFormat) CellFormatMap.get("text");
		} else {
			cellFormat = new WritableCellFormat(NumberFormats.TEXT);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			CellFormatMap.put("text", cellFormat);
			cfMap.set(CellFormatMap);
		}
		return cellFormat;
	}

	/**
	 * 文本格式-表头
	 * 
	 * @author yjc
	 * @throws WriteException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static WritableCellFormat getHeaderCellFormat() throws AppException, WriteException {
		WritableCellFormat cellFormat;
		DataMap CellFormatMap = (DataMap) cfMap.get();

		if (CellFormatMap == null) {
			CellFormatMap = new DataMap();
		}

		if (CellFormatMap.containsKey("header")) {
			cellFormat = (WritableCellFormat) CellFormatMap.get("header");
		} else {
			cellFormat = new WritableCellFormat(NumberFormats.TEXT);
			WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
			cellFormat.setFont(font);
			cellFormat.setAlignment(Alignment.CENTRE);
			cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			CellFormatMap.put("header", cellFormat);
			cfMap.set(CellFormatMap);
		}
		return cellFormat;
	}

	/**
	 * 数字格式
	 * 
	 * @author yjc
	 * @throws WriteException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static WritableCellFormat getNumberCellFormat(String mask) throws AppException, WriteException {
		WritableCellFormat cellFormat;
		DataMap CellFormatMap = (DataMap) cfMap.get();
		if (CellFormatMap == null) {
			CellFormatMap = new DataMap();
		}
		if (CellFormatMap.containsKey(mask)) {
			cellFormat = (WritableCellFormat) CellFormatMap.get(mask);
		} else {
			NumberFormat nf = new NumberFormat(mask);
			cellFormat = new WritableCellFormat(nf);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			CellFormatMap.put(mask, cellFormat);
			cfMap.set(CellFormatMap);
		}
		return cellFormat;
	}

	/**
	 * 日期格式
	 * 
	 * @author yjc
	 * @throws WriteException
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static WritableCellFormat getDateCellFormat(String mask) throws AppException, WriteException {
		WritableCellFormat cellFormat;
		DataMap CellFormatMap = (DataMap) cfMap.get();
		if (CellFormatMap == null) {
			CellFormatMap = new DataMap();
		}
		if (CellFormatMap.containsKey(mask)) {
			cellFormat = (WritableCellFormat) CellFormatMap.get(mask);
		} else {
			DateFormat df = new DateFormat(mask);
			cellFormat = new WritableCellFormat(df);
			cellFormat.setAlignment(Alignment.CENTRE);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			CellFormatMap.put(mask, cellFormat);
			cfMap.set(CellFormatMap);
		}
		return cellFormat;
	}

	/**
	 * 清空
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static void clearCellFormatMap() {
		cfMap.set(null);
	}
}
