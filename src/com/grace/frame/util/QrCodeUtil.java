package com.grace.frame.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.grace.frame.exception.AppException;
import com.swetake.util.Qrcode;

/**
 * 二维码生成工具
 * 
 * @author yjc
 */
public class QrCodeUtil{
	/**
	 * 获取二维码base64图片
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static String getQrcodeImg(int size, String content) throws Exception {
		byte[] data = QrCodeUtil.getQrcodeImgByte(size, content);
		// 转base64串
		String base64img = SecUtil.base64Encode(data);
		return base64img;
	}

	/**
	 * 获取二维码base64图片
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public static byte[] getQrcodeImgByte(int size, String content) throws Exception {
		// 首先修正size-传入的默认为像素
		if (size <= 0) {
			throw new AppException("size设置的大小不合适，请调整。");
		}
		if (size > 40) {
			throw new AppException("size设置的大小不合适，请调整。");
		}

		BufferedImage bufImg = null;
		Qrcode qrcodeHandler = new Qrcode();
		// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
		qrcodeHandler.setQrcodeErrorCorrect('M');
		qrcodeHandler.setQrcodeEncodeMode('B');
		// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
		qrcodeHandler.setQrcodeVersion(size);
		// 获得内容的字节数组，设置编码格式
		byte[] contentBytes = content.getBytes("utf-8");
		// 图片尺寸
		int imgSize = 67 + 12 * (size - 1);
		bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D gs = bufImg.createGraphics();
		// 设置背景颜色
		gs.setBackground(Color.WHITE);
		gs.clearRect(0, 0, imgSize, imgSize);

		// 设定图像颜色> BLACK
		gs.setColor(Color.BLACK);
		// 设置偏移量，不设置可能导致解析出错
		int pixoff = 2;
		// 输出内容> 二维码
		if (contentBytes.length > 0 && contentBytes.length < 800) {
			boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
			for (int i = 0; i < codeOut.length; i++) {
				for (int j = 0; j < codeOut.length; j++) {
					if (codeOut[j][i]) {
						gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
					}
				}
			}
		} else {
			throw new Exception("QRCode content bytes length = "
					+ contentBytes.length + " not in [0, 800].");
		}
		gs.dispose();
		bufImg.flush();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bufImg, "JPEG", out);
		byte[] data = out.toByteArray();

		return data;
	}
}
