package com.grace.frame.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;

import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code128Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.WidthCodedPainter;

import com.grace.frame.exception.AppException;
import com.lowagie.text.pdf.Barcode128;

/**
 * 条形码工具类操作的封装
 * 
 * @author yjc
 */
public class BarCodeUtil{
	/**
	 * 获得条形码图片（带条形码）的base64编码字符串
	 * <p>
	 * 128条码方式
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static String get128BarImgWithCode(int width, int height, String code) throws Exception {
		byte[] data = BarCodeUtil.get128BarImgWithCode2Byte(width, height, code);
		// 转base64串
		String barcodpic = SecUtil.base64Encode(data);

		return barcodpic;
	}

	/**
	 * 获得条形码图片（带条形码）的base64编码字符串
	 * <p>
	 * 128条码方式
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static byte[] get128BarImgWithCode2Byte(int width, int height,
			String code) throws Exception {
		int imageWidth = width;
		int imageHeight = height + 45;

		BufferedImage bufferImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graph = (Graphics2D) bufferImage.getGraphics();
		graph.fillRect(0, 0, imageWidth, imageHeight);
		Font font = new java.awt.Font("abc", java.awt.Font.PLAIN, 24);
		FontRenderContext fontRenderContext = graph.getFontRenderContext();

		int codeWidth = (int) font.getStringBounds(code, fontRenderContext)
			.getWidth();
		int codeHeight = (int) font.getStringBounds(code, fontRenderContext)
			.getHeight();

		Barcode128 barcode128 = new Barcode128();
		barcode128.setCode(code);
		Image codeImg = barcode128.createAwtImage(Color.black, Color.white);
		graph.drawImage(codeImg, 0, 0, width, height, Color.white, null);

		// 为图片添加条形码（文字），位置为条形码图片的下部居中
		AttributedString ats = new AttributedString(code);
		ats.addAttribute(TextAttribute.FONT, font, 0, code.length());
		AttributedCharacterIterator iter = ats.getIterator();

		// 设置条形码（文字）的颜色为黑色
		graph.setColor(Color.black);

		// 绘制条形码（文字）
		graph.drawString(iter, (width - codeWidth) / 2, height + codeHeight);
		graph.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufferImage, "JPEG", out);
		byte[] data = out.toByteArray();
		return data;
	}

	/**
	 * 获得条形码图片（不带条形码）的base64编码字符串
	 * <p>
	 * 128条码方式
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static String get128BarImg(int width, int height, String code) throws Exception {
		byte[] data = BarCodeUtil.get128BarImg2Byte(width, height, code);
		String barcodpic = SecUtil.base64Encode(data);

		return barcodpic;
	}

	/**
	 * 获得条形码图片（不带条形码）的base64编码字符串
	 * <p>
	 * 128条码方式
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static byte[] get128BarImg2Byte(int width, int height, String code) throws Exception {
		Barcode128 barcode128 = new Barcode128();
		barcode128.setCode(code);
		Image codeImg = barcode128.createAwtImage(Color.black, Color.white);

		// 设置图像大小
		codeImg = codeImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);

		// 内存中创建图像
		BufferedImage bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// 获取图形上下文
		Graphics2D graph = (Graphics2D) bufferImage.getGraphics();

		// 绘制图像到目标位置
		graph.drawImage(codeImg, 0, 0, codeImg.getWidth(null), codeImg.getHeight(null), 0, 0, width, height, null);

		// 销毁Graphics
		graph.dispose();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufferImage, "JPEG", out);
		byte[] data = out.toByteArray();
		return data;
	}

	/**
	 * 获取39条形码的base64位编码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static String get39BarImg(int width, int height, String code) throws IOException, AppException {
		if (null == code || 0 == code.length()) {
			return "";
		}
		byte[] data = BarCodeUtil.get39BarImg2Byte(width, height, code);
		String barcodpic = SecUtil.base64Encode(data);
		return barcodpic;
	}

	/**
	 * 获取39条形码的base64位编码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static byte[] get39BarImg2Byte(int width, int height, String code) throws IOException, AppException {
		if (null == code || 0 == code.length()) {
			return null;
		} else {
			code = "*" + code + "*";
		}

		int rate = 3;
		int nImageWidth = (code.length() * (3 * rate + 7) * width) + 20;

		BufferedImage bi = new BufferedImage(nImageWidth, height + 15, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, nImageWidth, height);

		g.setColor(Color.BLACK);
		int startx = 10;
		for (int i = 0; i < code.length(); i++) {
			short sCode = getCharCode(code.charAt(i));
			for (int j = 0; j < 9; j++) {
				int temp_width = width;

				if (((0x100 >>> j) & sCode) != 0) {
					temp_width *= rate;
				}

				if ((j & 0x1) == 0) {
					g.fillRect(startx, 10, temp_width, height);
				}

				startx += temp_width;
			}
			startx = startx + width;
		}

		g.setColor(Color.WHITE);
		g.fillRect(0, height, nImageWidth, 15);
		g.setColor(Color.BLACK);
		g.drawString(code, (nImageWidth - code.length() * 5) / 2, height + 12);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bi, "BMP", out);
		byte[] data = out.toByteArray();
		return data;
	}

	/**
	 * 获取code对应编码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	private static short getCharCode(char ch) {
		char[] m_chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '-', '.', ' ', '*', '$', '/', '+', '%' };

		short[] m_codes = { 0x34, 0x121, 0x61, 0x160, 0x31, 0x130, 0x70, 0x25, 0x124, 0x64, 0x109, 0x49, 0x148, 0x19, 0x118, 0x58, 0xd, 0x10c, 0x4c, 0x1c, 0x103, 0x43, 0x142, 0x13, 0x112, 0x52, 0x7, 0x106, 0x46, 0x16, 0x181, 0xc1, 0x1c0, 0x91, 0x190, 0xd0, 0x85, 0x184, 0xc4, 0x94, 0xa8, 0xa2, 0x8a, 0x2a };
		for (int i = 0; i < m_chars.length; i++) {
			if (ch == m_chars[i])
				return m_codes[i];
		}
		return 0;
	}

	/**
	 * 使用jbarcode生成128码，无法控制图片大小
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-22
	 * @since V1.0
	 */
	public static String gen128JbBarImg(String code) throws Exception {
		byte[] data = BarCodeUtil.gen128JbBarImg2Byte(code);
		String barcodpic = SecUtil.base64Encode(data);
		return barcodpic;
	}

	/**
	 * 使用jbarcode生成128码，无法控制图片大小
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-22
	 * @since V1.0
	 */
	public static byte[] gen128JbBarImg2Byte(String code) throws Exception {
		JBarcode jbar = new JBarcode(Code128Encoder.getInstance(), WidthCodedPainter.getInstance(), BaseLineTextPainter.getInstance());
		BufferedImage bfimg = jbar.createBarcode(code);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bfimg, "PNG", out);
		byte[] data = out.toByteArray();

		out.flush();
		out.close();
		return data;
	}
}
