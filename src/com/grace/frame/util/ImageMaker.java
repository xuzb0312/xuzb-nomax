package com.grace.frame.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 图片生成-根据模板
 * 
 * @author yjc
 */
public class ImageMaker{

	/**
	 * 图片模板
	 */
	private BufferedImage _imageModel;
	// 元素项目
	private DataSet _items;
	// 具体项目信息
	private HashMap<String, Object> _itemValues = new HashMap<String, Object>();
	// 对于字符串类型的数据输入，有对齐方式的需求，则增加居中(center)，右对齐方式(right)。默认左对齐(left)
	private HashMap<String, String> _itemAlign = new HashMap<String, String>();
	// 字符串行距设置
	private HashMap<String, Double> _itemLineHeight = new HashMap<String, Double>();

	/**
	 * 根据mbbh实例化对象
	 * 
	 * @author yjc
	 * @date 创建时间 2016-1-25
	 * @since V1.0
	 */
	public static ImageMaker getInstance(String mbbh) throws Exception {
		Sql sql = new Sql();
		sql.setSql(" select mbid from fw.image_model where mbbh = ? and mbzt = '1' ");
		sql.setString(1, mbbh);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("对于模板编号" + mbbh + "的图片模板在系统中没有找到");
		} else if (dsTemp.size() > 1) {
			throw new BizException("对于模板编号" + mbbh + "的图片模板在系统中找到多条，请检查。");
		}
		String mbid = dsTemp.getString(0, "mbid");
		return new ImageMaker(mbid);
	}

	/**
	 * 根据经办机构和mbbh实例化对象
	 * 
	 * @author yjc
	 * @date 创建时间 2016-1-25
	 * @since V1.0
	 */
	public static ImageMaker getInstance(String jbjgid, String mbbh) throws Exception {
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select mbid ");
		sqlBF.append("   from fw.image_model a ");
		sqlBF.append("  where a.mbbh = ? ");
		sqlBF.append("    and a.mbzt = '1' ");// 状态为启用的
		sqlBF.append("    and exists (select '1' ");
		sqlBF.append("           from fw.image_model_config b ");
		sqlBF.append("          where a.mbid = b.mbid ");
		sqlBF.append("            and b.jbjgid = ?) ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, mbbh);
		sql.setString(2, jbjgid);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("对于模板编号" + mbbh + "的图片模板在系统中没有找到");
		} else if (dsTemp.size() > 1) {
			throw new BizException("对于模板编号" + mbbh + "的图片模板在系统中找到多条，请检查。");
		}
		String mbid = dsTemp.getString(0, "mbid");
		return new ImageMaker(mbid);
	}

	/**
	 * 直接根据内容进行初始化
	 * 
	 * @param image
	 * @param items
	 * @throws Exception
	 */
	public ImageMaker(BufferedImage image, DataSet dsItem) throws Exception {
		// 初始化
		this.initData(image, dsItem);
	}

	/**
	 * 直接根据内容进行初始化
	 * 
	 * @param image
	 * @param items
	 * @throws Exception
	 */
	public ImageMaker(Blob image, DataSet dsItem) throws Exception {
		InputStream in = null;
		try {
			in = image.getBinaryStream();
			BufferedImage bimage = ImageIO.read(in);
			// 初始化
			this.initData(bimage, dsItem);
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	/**
	 * 直接根据内容进行初始化
	 * 
	 * @param image
	 * @param items
	 * @throws Exception
	 */
	public ImageMaker(byte[] image, DataSet dsItem) throws Exception {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(image);
			BufferedImage bimage = ImageIO.read(in);
			// 初始化
			this.initData(bimage, dsItem);
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	/**
	 * 直接根据内容进行初始化
	 * 
	 * @param image
	 * @param items
	 * @throws Exception
	 */
	public ImageMaker(String base64image, DataSet dsItem) throws Exception {
		InputStream in = null;
		try {
			in = new ByteArrayInputStream(SecUtil.base64Decode(base64image));
			BufferedImage bimage = ImageIO.read(in);
			// 初始化
			this.initData(bimage, dsItem);
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	/**
	 * 构造函数--mbid进行初始化的
	 * 
	 * @param mbid
	 */
	public ImageMaker(String mbid) throws AppException, BizException, IOException, SQLException {
		InputStream in = null;
		try {
			Sql sql = new Sql();
			sql.setSql(" select mbnr from fw.image_model where mbid = ? ");
			sql.setString(1, mbid);
			DataSet dsTemp = sql.executeQuery();
			if (dsTemp.size() <= 0) {
				throw new BizException("图片模板【mbid:" + mbid + "】在系统中不存在");
			}
			Blob mbnr = dsTemp.getBlob(0, "mbnr");
			in = mbnr.getBinaryStream();
			BufferedImage image = ImageIO.read(in);

			// 元素项目
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" select xmbh, xmmc, xsys, ztdx, xmkd, ");
			sqlBF.append("        xmgd, xmdj, xmzj, ctbz, ztlx, ");
			sqlBF.append("        xmxh ");
			sqlBF.append("   from fw.image_model_detl ");
			sqlBF.append("  where mbid = ? ");
			sql.setSql(sqlBF.toString());
			sql.setString(1, mbid);
			DataSet dsItem = sql.executeQuery();

			// 初始化
			this.initData(image, dsItem);
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	/**
	 * 数据初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	private void initData(BufferedImage image, DataSet dsItem) throws AppException {
		// 数据赋值
		this._imageModel = image;
		this._items = dsItem;
		this._items.sort("xmxh");
		// 数据清空
		this.clear();
	}

	/**
	 * 增加值内容
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	public void putPara(String key, Object value) {
		if (null == value) {
			return;
		}
		this._itemValues.put(key, value);
	}

	/**
	 * 对齐方式
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public void putPara(String key, Object value, String align) throws BizException {
		if (null == value) {
			return;
		}
		this.putPara(key, value);

		// 增加对齐方式记录
		this._itemAlign.put(key, align);
	}

	/**
	 * 行间距
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public void putPara(String key, Object value, String align,
			double lineHeight) throws BizException {
		if (null == value) {
			return;
		}
		this.putPara(key, value, align);

		// 行间距
		this._itemLineHeight.put(key, lineHeight);
	}

	/**
	 * 行间距
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public void putPara(String key, Object value, double lineHeight) throws BizException {
		if (null == value) {
			return;
		}
		this.putPara(key, value);

		// 行间距
		this._itemLineHeight.put(key, lineHeight);
	}

	/**
	 * 二维码
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	public void putQrcode(String key, String code) throws Exception {
		if (StringUtil.chkStrNull(code)) {
			return;
		}
		byte[] barcodeImg = QrCodeUtil.getQrcodeImgByte(8, code);// 定死为8的级别
		this.putPara(key, barcodeImg);
	}

	/**
	 * 打印条形码(128码-带文字)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put128BarcodeWidthCode(String key, String barcode, int width,
			int height) throws Exception {
		if (width <= 0) {
			throw new AppException("宽度必须大于0");
		}
		if (height <= 0) {
			throw new AppException("高度必须大于0");
		}
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		byte[] barcodeImg = BarCodeUtil.get128BarImgWithCode2Byte(width, height, barcode);
		this.putPara(key, barcodeImg);
	}

	/**
	 * 打印条形码(128码-带文字)--使用jbarcode插件获取条码
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put128JbBarcodeWidthCode(String key, String barcode) throws Exception {
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		byte[] barcodeImg = BarCodeUtil.gen128JbBarImg2Byte(barcode);
		this.putPara(key, barcodeImg);
	}

	/**
	 * 打印条形码(128码-不带文字)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put128Barcode(String key, String barcode, int width, int height) throws Exception {
		if (width <= 0) {
			throw new AppException("宽度必须大于0");
		}
		if (height <= 0) {
			throw new AppException("高度必须大于0");
		}
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		byte[] barcodeImg = BarCodeUtil.get128BarImg2Byte(width, height, barcode);
		this.putPara(key, barcodeImg);
	}

	/**
	 * 打印条形码(39码)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put39Barcode(String key, String barcode, int width, int height) throws Exception {
		if (width <= 0) {
			throw new AppException("宽度必须大于0");
		}
		if (height <= 0) {
			throw new AppException("高度必须大于0");
		}
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		byte[] barcodeImg = BarCodeUtil.get39BarImg2Byte(width, height, barcode);
		this.putPara(key, barcodeImg);
	}

	/**
	 * 返回Byte类型数据（png格式）
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	public byte[] createImageByte(boolean isContainsBackImage) throws Exception {
		ByteArrayOutputStream bos = null;
		try {
			BufferedImage image = this.create(isContainsBackImage);
			bos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bos);
			byte[] byteImage = bos.toByteArray();
			return byteImage;
		} finally {
			if (bos != null) {
				bos.close();
			}
		}
	}

	/**
	 * 返回base64类型数据（png格式）
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	public String createBase64(boolean isContainsBackImage) throws Exception {
		return SecUtil.base64Encode(this.createImageByte(isContainsBackImage));
	}

	/**
	 * 返回base64类型数据（png格式）--给前台img-src使用的
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	public String createBase64ForSrc(boolean isContainsBackImage) throws Exception {
		return "data:image/png;base64,"
				+ this.createBase64(isContainsBackImage);
	}

	/**
	 * 创建pdf文件
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-8
	 * @since V1.0
	 */
	public byte[] createPDFByte(boolean isContainsBackImage) throws Exception {
		ByteArrayOutputStream pdfOs = null;
		try {
			// 字节流
			pdfOs = new ByteArrayOutputStream();
			this.createPDFFile(isContainsBackImage, pdfOs);
			byte[] bytePdf = pdfOs.toByteArray();
			return bytePdf;
		} finally {
			if (null != pdfOs) {
				pdfOs.flush();
				pdfOs.close();
			}
		}
	}

	/**
	 * 方法内部不对os进行关闭操作，谁使用，谁关闭
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-8
	 * @since V1.0
	 */
	public void createPDFFile(boolean isContainsBackImage, OutputStream os) throws Exception {
		byte[] imageByte = this.createImageByte(isContainsBackImage);
		com.lowagie.text.Image pdfImg = com.lowagie.text.Image.getInstance(imageByte);

		// 生成PDF文档
		Document document = new Document(new Rectangle(this._imageModel.getWidth(), this._imageModel.getHeight()), 0, 0, 0, 0);
		PdfWriter.getInstance(document, os);
		document.open();

		// 书写图片
		document.add(pdfImg);

		// 文档关闭
		document.close();
	}

	/**
	 * 方法内部不对os进行关闭操作，谁使用，谁关闭
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-8
	 * @since V1.0
	 */
	public void createImgFile(boolean isContainsBackImage, OutputStream os) throws Exception {
		BufferedImage bImage = this.create(isContainsBackImage);
		ImageIO.write(bImage, "png", os);
	}

	/**
	 * 获取打印html数据
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-5
	 * @since V1.0
	 */
	public String getPrintHtml(boolean isContainsBackImage) throws Exception {
		return "<div id=\"page1\" style=\"width:100%;text-align:center;\"><img style=\"width:100%;max-width:"
				+ this._imageModel.getWidth()
				+ "px;max-height:"
				+ this._imageModel.getHeight()
				+ "px;\" src=\""
				+ this.createBase64ForSrc(isContainsBackImage) + "\"/></div>";// 加上id=page1的目的是兼容打印插件打印
	}

	/**
	 * 生成图片
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	public BufferedImage create(boolean isContainsBackImage) throws Exception {
		BufferedImage backImage = null;
		if (isContainsBackImage) {
			backImage = this._imageModel;
		} else {// 使用透明背景
			BufferedImage buffImgTemp = new BufferedImage(this._imageModel.getWidth(), this._imageModel.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D gdTemp = buffImgTemp.createGraphics();
			backImage = gdTemp.getDeviceConfiguration()
				.createCompatibleImage(this._imageModel.getWidth(), this._imageModel.getHeight(), Transparency.TRANSLUCENT);
			gdTemp.dispose();
		}

		// 循环项目附加数据
		Graphics2D gpd = backImage.createGraphics();
		for (int i = 0, n = this._items.size(); i < n; i++) {
			DataMap itemOne = this._items.get(i);
			this.addItemFunc(itemOne, gpd);// 增加一个项目
		}
		gpd.dispose();

		return backImage;
	}

	/**
	 * 添加一个项目
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws BizException
	 * @throws IOException
	 * @throws SQLException
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	private void addItemFunc(DataMap item, Graphics2D gpd) throws AppException, BizException, IOException, SQLException {
		InputStream is = null;
		try {
			String xmbh = item.getString("xmbh");
			String xsys = item.getString("xsys");
			int ztdx = item.getInt("ztdx");
			int xmkd = item.getInt("xmkd");
			int xmgd = item.getInt("xmgd");
			int xmdj = item.getInt("xmdj");
			int xmzj = item.getInt("xmzj");
			String ctbz = item.getString("ctbz");
			String ztlx = item.getString("ztlx");
			if (StringUtil.chkStrNull(xmbh)) {
				return;
			}
			String align = "left";// 对其方式-不同于其他项目，直接前台设置格式，本相信息，需要后台指定。
			if (this._itemAlign.containsKey(xmbh)) {
				align = this._itemAlign.get(xmbh);
			}
			double lineHeight = 1.3;// 增加行间距的设置
			if (this._itemLineHeight.containsKey(xmbh)) {
				lineHeight = this._itemLineHeight.get(xmbh);
			}

			if (!this._itemValues.containsKey(xmbh)) {
				return;
			}
			Object value = this._itemValues.get(xmbh);
			if (null == value) {
				return;
			}
			if (xmkd <= 10) {
				xmkd = 100;
			}
			if (xmgd <= 10) {
				xmgd = 100;
			}
			if (xmdj < 0) {
				xmdj = 0;
			}
			if (xmzj < 0) {
				xmzj = 0;
			}
			if (StringUtil.chkStrNull(ztlx)) {
				ztlx = "宋体";
			}
			ztdx = ztdx <= 2 ? 12 : ztdx;
			Color color = this.getColor(xsys);

			if (value instanceof Blob) {
				Blob blob_value = (Blob) value;
				is = blob_value.getBinaryStream(); // 将b作为输入流；
				BufferedImage img_value = ImageIO.read(is); // 将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
				gpd.drawImage(img_value, xmzj, xmdj, xmkd, xmgd, null);
				is.close();
			} else if (value instanceof byte[]) {
				byte[] b_value = (byte[]) value;
				is = new ByteArrayInputStream(b_value); // 将b作为输入流；
				BufferedImage img_value = ImageIO.read(is); // 将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
				gpd.drawImage(img_value, xmzj, xmdj, xmkd, xmgd, null);
				is.close();
			} else if (value instanceof Image) {
				Image img_value = (Image) value;
				gpd.drawImage(img_value, xmzj, xmdj, xmkd, xmgd, null);
			} else {// 其他的类型直接转字符串输出
				String str_value = String.valueOf(value);
				// 增加项目
				BufferedImage buffImg = new BufferedImage(xmkd, xmgd, BufferedImage.TYPE_INT_RGB);
				Graphics2D gd = buffImg.createGraphics();
				// 设置透明 start
				buffImg = gd.getDeviceConfiguration()
					.createCompatibleImage(xmkd, xmgd, Transparency.TRANSLUCENT);
				gd = buffImg.createGraphics();
				Font font = new Font(ztlx, "1".equals(ctbz) ? Font.BOLD : Font.PLAIN, ztdx);
				DataMap itemPara = this.dealValueWidth2Arr(str_value, font, xmkd);
				// int fH = itemPara.getInt("height");//使用字体大小进行y轴递增
				Object[] values_str = (Object[]) itemPara.get("values");

				gd.setFont(font); // 设置字体
				gd.setColor(color); // 设置颜色
				for (int i = 0, n = values_str.length; i < n; i++) {
					if (i == n - 1) {
						// 对齐方式仅对最后一行数据生效-由于前置行已经满行了
						if ("right".equalsIgnoreCase(align)) {
							// 右对齐
							FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
							int vaWidth = fm.stringWidth((String) values_str[i]);
							int xStart = xmkd - 2 - vaWidth;// 右对齐策略
							if (xStart < 3) {
								xStart = 3;
							}
							gd.drawString((String) values_str[i], xStart, (int) (ztdx
									* (i + 1) * lineHeight));// 调整行距1.3倍
						} else if ("center".equalsIgnoreCase(align)) {
							// 居中
							FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
							int vaWidth = fm.stringWidth((String) values_str[i]);
							int xStart = ((xmkd - 5 - vaWidth) / 2) + 3;// 居中策略
							if (xStart < 3) {
								xStart = 3;
							}
							gd.drawString((String) values_str[i], xStart, (int) (ztdx
									* (i + 1) * lineHeight));// 调整行距1.3倍
						} else {
							// 左对齐
							gd.drawString((String) values_str[i], 3, (int) (ztdx
									* (i + 1) * lineHeight));// 调整行距1.3倍
						}
					} else {
						gd.drawString((String) values_str[i], 3, (int) (ztdx
								* (i + 1) * lineHeight));// 调整行距1.3倍
					}
				}
				gd.dispose();
				gpd.drawImage(buffImg, xmzj, xmdj, null);// 背景图写入相应内容
			}
		} finally {
			if (null != is) {
				is.close();
			}
		}
	}

	/**
	 * 宽度调整换行操作。
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 * @since mod.yjc.2018年8月14日-调整换行的算法，以满足中英文混排的情况。
	 */
	private DataMap dealValueWidth2Arr(String value, Font font, int width) throws AppException {
		FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
		List<String> list = new ArrayList<String>();
		int vaWidth = fm.stringWidth(value);
		if (vaWidth <= (width - 5)) {
			list.add(value);
		} else {// 分行
			int vaLength = StringUtil.getChnStrLen(value);
			int lineValLen = ((width - 5) * vaLength) / vaWidth;
			lineValLen--;// 减掉一个字符-防止最后被遮挡的情况。
			int len = 0;
			for (int i = 0; i < value.length();) {
				if (((int) value.charAt(i)) > 255) {
					len += 2;
				} else {
					len++;
				}
				i++;

				if (len >= lineValLen) {
					list.add(value.substring(0, i));
					value = value.substring(i);
					len = 0;
					i = 0;
				}
			}
			if (len > 0) {
				list.add(value);
			}
		}
		DataMap rdm = new DataMap();
		rdm.put("height", fm.getHeight());
		rdm.put("values", list.toArray());
		return rdm;
	}

	/**
	 * 获取颜色Color
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	private Color getColor(String color) throws BizException {
		if (StringUtil.chkStrNull(color)) {
			color = "#000000";
		}
		if (color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (color.length() != 6) {
			throw new BizException("颜色代码不合法");
		}
		int r = Integer.parseInt(color.substring(0, 2), 16);
		int g = Integer.parseInt(color.substring(2, 4), 16);
		int b = Integer.parseInt(color.substring(4), 16);
		return new Color(r, g, b);
	}

	/**
	 * 数据清空
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-4
	 * @since V1.0
	 */
	public void clear() {
		this._itemValues.clear();
		this._itemAlign.clear();
	}
}
