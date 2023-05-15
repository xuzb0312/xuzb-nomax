package com.grace.frame.util.opencv;

import java.awt.Color;
import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * opencv工具方法，需要依赖于opencv组件进行检测<br>
 * opencv版本必须为：3.1.0<br>
 * 下载地址：https://opencv.org/releases.html<br>
 * 选择版本3.1.0.解压后，后续使用的文件在目录build/ java和source/data目录中可以找到<br>
 * 该工具方法：<br>
 ********* Windows平台，在C盘目录下建立以下目录结构 *********<br>
 * C:\\opencv310<br>
 * 包含以下文件：x86\\opencv_java310.dll (32位平台所使用的opencv库)<br>
 * -------------x64\\opencv_java310.dll (64位平台所使用的opencv库)<br>
 * !!!!!!!!!!!!!java程序自动判断平台，无需人员判断，只需在指定目录下放置该文件即可<br>
 * -------------haarcascade_frontalface_alt2.xml (人脸识别分类器)<br>
 ********* Linux平台，在目录下建立以下目录结构 *********<br>
 * /usr/opencv310<br>
 * 包含以下文件：x64/libopencv_java310.so<br>
 * -------------haarcascade_frontalface_alt2.xml (人脸识别分类器)<br>
 * 例外：<br>
 * 对于同一中间件服务器只允许一个应用加载opencv的dll后续的会出现异常，不通中间件服务器不受影响。
 * 
 * @author yjc
 */
public class OpencvUtil{
	public static String OPENCV_PATH = "C:\\opencv310\\";// opencv的目录path
	public static String OPENCV_FACE_ClASSIFIER_PATH = "C:\\opencv310\\haarcascade_frontalface_alt2.xml";// 人脸识别分类器地址
	static {
		String os = System.getProperty("os.name");
		boolean isLiunx = false;
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			isLiunx = true;
		}
		if (isLiunx) {
			// linux系统的识别程序，切换路径
			OpencvUtil.OPENCV_PATH = "/usr/opencv310/";
			OpencvUtil.OPENCV_FACE_ClASSIFIER_PATH = "/usr/opencv310/haarcascade_frontalface_alt2.xml";
		}
		// 检查opencv是否正确配置
		if (!(new File(OpencvUtil.OPENCV_PATH)).exists()) {
			System.out.println("opencv未正确配置，相关功能无法正常使用,请按要求检查相关配置。");
			System.out.println("==================================================================================================*");
			System.out.println("* opencv工具方法，需要依赖于opencv组件进行检测");
			System.out.println("* opencv版本必须为：3.1.0");
			System.out.println("* 下载地址：https://opencv.org/releases.html");
			System.out.println("* 选择版本3.1.0.解压后，后续使用的文件在目录build/ java和source/data目录中可以找到");
			System.out.println("* 该工具方法：");
			System.out.println("********* Windows平台，在C盘目录下建立以下目录结构  *********");
			System.out.println("* C:\\opencv310");
			System.out.println("* 包含以下文件：x86\\opencv_java310.dll (32位平台所使用的opencv库)");
			System.out.println("* -------------x64\\opencv_java310.dll (64位平台所使用的opencv库)");
			System.out.println("* !!!!!!!!!!!!!java程序自动判断平台，无需人员判断，只需在指定目录下放置该文件即可");
			System.out.println("* -------------haarcascade_frontalface_alt2.xml (人脸识别分类器)");
			System.out.println("********* Linux平台，在目录下建立以下目录结构  *********");
			System.out.println("* /usr/opencv310");
			System.out.println("* 包含以下文件：x64/libopencv_java310.so");
			System.out.println("* -------------haarcascade_frontalface_alt2.xml (人脸识别分类器)");
			System.out.println("==================================================================================================*");
		} else {
			// 加载opencv库
			String opencvDllName;
			if (isLiunx) {// linux下全部为x64平台
				opencvDllName = OpencvUtil.OPENCV_PATH + File.separator + "x64"
						+ File.separator + "libopencv_java310.so";
			} else {
				String jdkmodel = System.getProperty("sun.arch.data.model"); // 判断是32位还是64位
				String dllmodelStr = "x32";
				if ("64".equals(jdkmodel)) {
					dllmodelStr = "x64";
				}
				// 载入opencv的dll库
				opencvDllName = OpencvUtil.OPENCV_PATH + File.separator
						+ dllmodelStr + File.separator
						+ Core.NATIVE_LIBRARY_NAME + ".dll";
			}
			System.load(opencvDllName);
			// 加载成功
			System.out.println("**opencv加载成功...");
		}
	}

	/**
	 * 初始化opencv使用，方法为空，主要是为了执行static方法，初始化util工具，加载完成opencv库
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-5
	 * @since V1.0
	 */
	public static void init() {}

	/**
	 * 图像文件byte转Mat
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat imageFileByte2Mat(byte[] imageFile) {
		Mat encodedImageTemp = new Mat(1, imageFile.length, CvType.CV_8U);
		encodedImageTemp.put(0, 0, imageFile);
		Mat matImage = Imgcodecs.imdecode(encodedImageTemp, Imgcodecs.IMREAD_COLOR);

		// 转换完成后，需要立即释放资源
		imageFile = null;
		encodedImageTemp = null;
		return matImage;
	}

	/**
	 * Mat转图像文件byte <br>
	 * imgQuality:图片质量
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static byte[] mat2ImageFileByte(Mat mat, int imgQuality) {
		MatOfByte matbyteOut = new MatOfByte();
		MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, imgQuality);// 压缩质量
		Imgcodecs.imencode(".jpg", mat, matbyteOut, params);// 输出全部转换为jpg格式
		byte[] resultByte = matbyteOut.toArray();

		// 资源释放
		mat = null;
		matbyteOut = null;
		return resultByte;
	}

	/**
	 * 方法描述：根据文件路径获取文件头信息
	 * 
	 * @param filePath 文件路径
	 * @return 文件头信息
	 */
	public static String getImageType(byte[] imageByte) {
		if (imageByte.length < 4) {
			return "other";
		}
		// 文件头
		byte[] b = new byte[4];
		b[0] = imageByte[0];
		b[1] = imageByte[1];
		b[2] = imageByte[2];
		b[3] = imageByte[3];

		// 获取字符
		StringBuilder builder = new StringBuilder();
		String hv;
		for (int i = 0; i < b.length; i++) {
			// 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
			hv = Integer.toHexString(b[i] & 0xFF).toUpperCase();
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
		String bytesToHexString = builder.toString();

		// 判断
		if (bytesToHexString.startsWith("424D")) {
			return "bmp";
		} else if (bytesToHexString.startsWith("FFD8")) {
			return "jpg";
		} else if (bytesToHexString.startsWith("4749")) {
			return "gif";
		} else if (bytesToHexString.startsWith("8950")) {
			return "png";
		}
		return "other";
	}

	/**
	 * 根据位置获取颜色(取位置周围的几个点进行判断-共9个，边缘的不够9个-平均值)(MAT的颜色通道为BGR，注意了)
	 * <p>
	 * 返回：red,white,blue,other
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-6
	 * @since V1.0
	 */
	public static String getColorByPoint(Mat image, int rows, int colms) {
		int pointNums = 0;
		double B = 0;
		double G = 0;
		double R = 0;
		for (int i = rows - 1; i <= rows + 1 && i >= 0 && i < image.height(); i++) {
			for (int j = colms - 1; j <= colms + 1 && j >= 0
					&& j < image.width(); j++) {
				pointNums++;
				double[] onePoint = image.get(i, j);
				B = B + onePoint[0];
				G = G + onePoint[1];
				R = R + onePoint[2];
			}
		}
		if (pointNums > 0) {
			B = B / pointNums;
			G = G / pointNums;
			R = R / pointNums;
		}

		// 转换到hsv模型进行判断颜色范围更加准确
		float[] hsv = new float[3];
		Color.RGBtoHSB((int) R, (int) G, (int) B, hsv);
		float h = hsv[0];
		float s = hsv[1];
		float v = hsv[2];

		// 判断是否为白色范围
		if (v >= 0.9 && s < 0.15) {
			return "white";
		}

		// 红色范围
		if ((h < 0.042 || h > 0.92) && v >= 0.6 && s > 0.6) {
			return "red";
		}
		// 蓝色范围
		if (h < 0.75 && h > 0.486 && v >= 0.6 && s > 0.6) {
			return "blue";
		}

		return "other";
	}

	/**
	 * 获取BGR最大的分量
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static double maxBGR(double B, double G, double R) {
		double temp = B;
		if (temp < G) {
			temp = G;
		}
		if (temp < R) {
			temp = R;
		}
		return temp;
	}

	/**
	 * 获取BGR最小的分量
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static double minBGR(double B, double G, double R) {
		double temp = B;
		if (temp > G) {
			temp = G;
		}
		if (temp > R) {
			temp = R;
		}
		return temp;
	}

	/**
	 * 反色处理
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat inverse(Mat image) {
		int width = image.cols();
		int height = image.rows();
		int dims = image.channels();
		byte[] data = new byte[width * height * dims];
		image.get(0, 0, data);

		int index = 0;
		int r = 0, g = 0, b = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width * dims; col += dims) {
				index = row * width * dims + col;
				b = data[index] & 0xff;
				g = data[index + 1] & 0xff;
				r = data[index + 2] & 0xff;

				r = 255 - r;
				g = 255 - g;
				b = 255 - b;

				data[index] = (byte) b;
				data[index + 1] = (byte) g;
				data[index + 2] = (byte) r;
			}
		}
		image.put(0, 0, data);
		return image;
	}

	/**
	 * 图像亮度提升
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat brightness(Mat image) {
		Mat dst = new Mat();
		Mat black = Mat.zeros(image.size(), image.type());
		Core.addWeighted(image, 1.2, black, 0.5, 0, dst);
		return dst;
	}

	/**
	 * 图像亮度降低
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat darkness(Mat image) {
		Mat dst = new Mat();
		Mat black = Mat.zeros(image.size(), image.type());
		Core.addWeighted(image, 0.8, black, 0.6, 0, dst);
		return dst;
	}

	/**
	 * 图像灰度
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat gray(Mat image) {
		Mat gray = new Mat();
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
		return gray;
	}

	/**
	 * 锐化
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat sharpen(Mat image) {
		Mat dst = new Mat();
		float[] sharper = new float[] { 0, -1, 0, -1, 5, -1, 0, -1, 0 };
		Mat operator = new Mat(3, 3, CvType.CV_32FC1);
		operator.put(0, 0, sharper);
		Imgproc.filter2D(image, dst, -1, operator);
		return dst;
	}

	/**
	 * 高斯模糊
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat blur(Mat image) {
		Mat dst = new Mat();
		Imgproc.GaussianBlur(image, dst, new Size(5, 5), 0);
		return dst;
	}

	/**
	 * 梯度
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public static Mat gradient(Mat image) {
		// 梯度
		Mat grad_x = new Mat();
		Mat grad_y = new Mat();
		Mat abs_grad_x = new Mat();
		Mat abs_grad_y = new Mat();

		Imgproc.Sobel(image, grad_x, CvType.CV_32F, 1, 0);
		Imgproc.Sobel(image, grad_y, CvType.CV_32F, 0, 1);
		Core.convertScaleAbs(grad_x, abs_grad_x);
		Core.convertScaleAbs(grad_y, abs_grad_y);
		grad_x.release();
		grad_y.release();
		Mat gradxy = new Mat();
		Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 10, gradxy);
		return gradxy;
	}
}
