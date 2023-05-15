package com.grace.frame.util;

import java.math.BigDecimal;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.opencv.OpencvUtil;

/**
 * 证件照检测工具方法，需要依赖于opencv组件进行检测<br>
 * <p>
 * 程序开发者务必注意该工具方法只是程序根据opencv人脸识别库进行的人脸检测识别，可能存在误差，请在业务系统界面提醒最终使用者确认处理结果图片，
 * 以免识别误差造成业务异常。
 * </p>
 * 
 * @author yjc
 */
public class IDPhotoChkUtil{
	public static int PHOTO_MIN_FILE_SIZE = 25600;// 传入文件大小限制--最小-单位字节-25K；可以通过程序初始化的时候初始大小，全局生效
	public static int PHOTO_MAX_FILE_SIZE = 5242880;// 传入文件大小限制--最大-单位字节-5

	/**
	 * 检测证件照<br>
	 * 要求文件大小在25K~5M间-默认<br>
	 * 文件尺寸大于300*420像素<br>
	 * 人像宽度居中-偏差一倍人脸认为居中，否则任务人像不居中不符合证件照要求
	 * <p>
	 * image:图片<br>
	 * ext:上传文件扩展名，空为不限制图片类型（只允许为jpg,png,bmp这3种，传值多个的化使用逗号(,)间隔）<br>
	 * background:背景色限制，当前只支持白底，蓝底和红底三种，传值为：white,blue,red;多个使用逗号间隔，传空则不限制背景颜色<br>
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-5
	 * @since V1.0
	 */
	public static byte[] check(CommonsMultipartFile image, String ext,
			String background) throws Exception {
		if (null == image) {
			throw new AppException("传入的image图像文件为空");
		}
		byte[] imageByte_In = image.getBytes();// 获取字节数据
		return IDPhotoChkUtil.check(imageByte_In, ext, background);
	}

	/**
	 * 检测证件照<br>
	 * 要求文件大小在25K~5M间--默认<br>
	 * 文件尺寸大于300*420像素<br>
	 * 人像宽度居中-偏差一倍人脸认为居中，否则任务人像不居中不符合证件照要求
	 * <p>
	 * image:图片<br>
	 * ext:上传文件扩展名，空为不限制图片类型（只允许为jpg,png,bmp这3种，传值多个的化使用逗号(,)间隔）<br>
	 * background:背景色限制，当前只支持白底，蓝底和红底三种，传值为：white,blue,red;多个使用逗号间隔，传空则不限制背景颜色<br>
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-5
	 * @since V1.0
	 */
	public static byte[] check(byte[] image, String ext, String background) throws Exception {
		Mat resultMat = IDPhotoChkUtil.check(ext, background, image);// 进行检测处理
		byte[] resultFileByte = OpencvUtil.mat2ImageFileByte(resultMat, 75);// 压缩质量75--不要太低了-造成模糊-压缩完成11k左右吧
		resultMat = null;
		return resultFileByte;
	}

	/**
	 * 检测证件照<br>
	 * <p>
	 * imgQuality：图像压缩质量
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-5
	 * @since V1.0
	 */
	public static byte[] check(byte[] image, String ext, String background,
			int imgQuality) throws Exception {
		Mat resultMat = IDPhotoChkUtil.check(ext, background, image);// 进行检测处理
		byte[] resultFileByte = OpencvUtil.mat2ImageFileByte(resultMat, imgQuality);
		resultMat = null;
		return resultFileByte;
	}

	/**
	 * 图像处理类型
	 * 
	 * @author yjc
	 */
	public enum ImgProType {
		INVERSE, // 反色
		BRIGHTNESS, // 亮度提升
		DARKNESS, // 亮度降低
		GRAY, // 灰度
		SHARPEN, // 锐化
		BLUR, // 高斯模糊
		GRADIENT
		// 梯度
	}

	/**
	 * 检测证件照<br>
	 * <p>
	 * 对图像进行特定处理操作
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-5
	 * @since V1.0
	 */
	public static byte[] check(byte[] image, String ext, String background,
			ImgProType type) throws Exception {
		Mat resultMat = IDPhotoChkUtil.check(ext, background, image);// 进行检测处理
		if (type == ImgProType.INVERSE) {
			resultMat = OpencvUtil.inverse(resultMat);
		} else if (type == ImgProType.BRIGHTNESS) {
			resultMat = OpencvUtil.brightness(resultMat);
		} else if (type == ImgProType.DARKNESS) {
			resultMat = OpencvUtil.darkness(resultMat);
		} else if (type == ImgProType.GRAY) {
			resultMat = OpencvUtil.gray(resultMat);
		} else if (type == ImgProType.SHARPEN) {
			resultMat = OpencvUtil.sharpen(resultMat);
		} else if (type == ImgProType.BLUR) {
			resultMat = OpencvUtil.blur(resultMat);
		} else if (type == ImgProType.GRADIENT) {
			resultMat = OpencvUtil.gradient(resultMat);
		}
		byte[] resultFileByte = OpencvUtil.mat2ImageFileByte(resultMat, 75);// 压缩质量75--不要太低了-造成模糊-压缩完成11k左右吧
		resultMat = null;
		return resultFileByte;
	}

	/**
	 * 检测证件照-返回mat<br>
	 * 要求文件大小在25K~5M间-默认<br>
	 * 文件尺寸大于300*420像素<br>
	 * 人像宽度居中-偏差一倍人脸认为居中，否则任务人像不居中不符合证件照要求
	 * <p>
	 * image:图片<br>
	 * ext:上传文件扩展名，空为不限制图片类型（只允许为jpg,png,bmp这3种，传值多个的化使用逗号(,)间隔）<br>
	 * background:背景色限制，当前只支持白底，蓝底和红底三种，传值为：white,blue,red;多个使用逗号间隔，传空则不限制背景颜色<br>
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-5
	 * @since V1.0
	 */
	public static Mat check(String ext, String background, byte[] image) throws Exception {
		// 初始化检测组件
		OpencvUtil.init();

		// 入参检测
		if (null == image) {
			throw new AppException("传入的image图像文件为空");
		}
		if (StringUtil.chkStrNull(ext)) {
			ext = "jpg,png,bmp";
		}
		if (StringUtil.chkStrNull(background)) {
			background = "white,blue,red";
		}
		String imgType = OpencvUtil.getImageType(image);// 获取扩展名
		if (!ext.contains(imgType)) {
			throw new AppException("证件照文件格式不符合限定条件(只允许" + ext
					+ "类型图像，强制修改文件扩展名后上传的文件认定为原类型文件)");
		}
		// 文件大小（25K~5M间-默认）
		if (image.length < IDPhotoChkUtil.PHOTO_MIN_FILE_SIZE
				|| image.length > IDPhotoChkUtil.PHOTO_MAX_FILE_SIZE) {
			throw new AppException("证件照文件大小不符合限定条件(只允许"
					+ IDPhotoChkUtil.getStrFileSize(IDPhotoChkUtil.PHOTO_MIN_FILE_SIZE)
					+ "~"
					+ IDPhotoChkUtil.getStrFileSize(IDPhotoChkUtil.PHOTO_MAX_FILE_SIZE)
					+ "大小的文件)");
		}

		// 转换mat
		Mat matImage = OpencvUtil.imageFileByte2Mat(image);
		image = null;// 转换完成后，需要立即释放资源

		// 文件宽高
		if (matImage.width() < 300) {
			throw new AppException("证件照宽度不符合限定条件(只允许大于300px宽度的文件)");
		}
		if (matImage.height() < 420) {
			throw new AppException("证件照高度不符合限定条件(只允许大于420px高度的文件)");
		}
		// 初始化opencv人脸检测分类器
		CascadeClassifier faceDetector = new CascadeClassifier(OpencvUtil.OPENCV_FACE_ClASSIFIER_PATH);

		// 在图片中检测人脸
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(matImage, faceDetections);
		// 检测结果
		Rect[] rects = faceDetections.toArray();
		if (rects == null || rects.length < 1) {
			throw new AppException("证件照中未检测到人像，不符合证件照要求");
		}
		// 选择最大的人脸结果集
		Rect rect = rects[0];
		for (int i = 1, n = rects.length; i < n; i++) {
			if (rect.width < rects[i].width) {
				rect = rects[i];
			}
		}
		// 判断人脸是否在图像中间-宽度中间
		int faceMarginLeft = rect.x;
		int faceMarginRight = matImage.width() - rect.x - rect.width;
		if (MathUtil.abs(faceMarginLeft - faceMarginRight - 0.0) > rect.width * 1.0) {// 偏差一倍人脸认为居中，否则任务人像不居中不符合证件照要求
			throw new AppException("证件照人像不居中（宽度方向）");
		}
		if (rect.x < (int) MathUtil.round(rect.width * 0.1, 0)) {
			throw new AppException("证件照人像不居中（宽度方向）");
		}
		if ((matImage.width() - rect.x - rect.width) < (int) MathUtil.round(rect.width * 0.1, 0)) {
			throw new AppException("证件照人像不居中（宽度方向）");
		}
		// 高度中间
		if (rect.y < (int) MathUtil.round(rect.height * 0.2, 0)) {
			throw new AppException("证件照人像不居中（高度方向）");
		}
		if ((matImage.height() - rect.y - rect.height) < (int) MathUtil.round(rect.height * 0.3, 0)) {
			throw new AppException("证件照人像不居中（高度方向）");
		}
		// 检查背景色（取左上角1个像素，右上角1个像素，人脸左上延伸边缘1个像素，人脸右上延伸边缘1个像素，人脸左下延伸边缘1个像素，人脸右下延伸边缘1个像素）
		String color_1 = OpencvUtil.getColorByPoint(matImage, 1, 1);
		String color_2 = OpencvUtil.getColorByPoint(matImage, 1, matImage.width() - 2);
		String color_3 = OpencvUtil.getColorByPoint(matImage, rect.y, 1);
		String color_4 = OpencvUtil.getColorByPoint(matImage, rect.y, matImage.width() - 2);
		String color_5 = OpencvUtil.getColorByPoint(matImage, rect.y
				+ rect.height, 1);
		String color_6 = OpencvUtil.getColorByPoint(matImage, rect.y
				+ rect.height, matImage.width() - 2);
		// 判断颜色
		if (!color_1.equals(color_2) || !color_1.equals(color_2)
				|| !color_1.equals(color_3) || !color_1.equals(color_4)
				|| !color_1.equals(color_5) || !color_1.equals(color_6)) {
			throw new AppException("证件照底色不是纯色（存在多种颜色掺杂或不是要求的底色）");
		}
		if (!background.contains(color_1)) {
			throw new AppException("证件照底色不符合要求（只允许"
					+ background.replace("red", "红")
						.replace("blue", "蓝")
						.replace("white", "白") + "的底色）");
		}

		// 图片处理--截图和缩放
		int posX = rect.x - (int) MathUtil.round((rect.width * 0.25), 0);
		int cutWidth = rect.width + (int) MathUtil.round((rect.width * 0.5), 0);

		if (posX < 0) {
			cutWidth = cutWidth + posX;
			posX = 0;
		}
		if (cutWidth + posX > matImage.width()) {
			posX = posX + (cutWidth + posX - matImage.width());
			cutWidth = matImage.width() - posX;
		}
		int cutHeight = (int) MathUtil.round(cutWidth * 1.4, 0);// 保证截取照片的比例
		int posY = rect.y - (int) MathUtil.round((rect.height * 0.6), 0);
		if (posY < 0) {
			posY = 0;
		}
		if (posY + cutHeight > matImage.height()) {
			posY = posY - (cutHeight - matImage.height());
		}
		if (posY < 0) {
			throw new AppException("证件照人像不居中（无法裁切）");
		}
		Rect cutRect = new Rect(posX, posY, cutWidth, cutHeight);
		Mat subImage = matImage.submat(cutRect);

		// 缩放
		Mat targetImage = new Mat();
		Size size = new Size(295, 413);
		Imgproc.resize(subImage, targetImage, size);

		// 资源释放
		matImage = null;
		subImage = null;

		return targetImage;
	}

	/**
	 * 在证件上增加证件号码长度18位
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat addZjhm(Mat image, String zjhm) throws AppException {
		if (image.width() != 295 || image.height() != 413) {
			throw new AppException("传入图像image大小必须为295*413像素");
		}
		// 置黑底
		for (int i = image.rows() - 5; i >= image.rows() - 22 && i > 0; i--) {
			for (int j = 39, n = image.cols() - 39; j < n; j++) {
				double[] arr = { 255, 255, 255 };
				image.put(i, j, arr);
			}
		}
		// 写文字
		Point p = new Point(56.0, image.rows() - 8);
		Imgproc.putText(image, zjhm, p, Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(30, 30, 255), 1);
		return image;
	}

	/**
	 * 获取文件大小-字符串的大小
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-12
	 * @since V1.0
	 */
	private static String getStrFileSize(long size) {
		double value = (double) size;
		if (value < 1024) {
			return String.valueOf(value) + "B";
		}

		value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN)
			.doubleValue();
		if (value < 1024) {
			return String.valueOf(value) + "KB";
		}

		value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN)
			.doubleValue();
		if (value < 1024) {
			return String.valueOf(value) + "MB";
		}

		// 否则如果要以GB为单位的，先除于1024再作同样的处理
		value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN)
			.doubleValue();
		return String.valueOf(value) + "GB";
	}
}
