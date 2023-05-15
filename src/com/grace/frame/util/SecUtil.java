package com.grace.frame.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;

/**
 * 安全相关的加密解密工具类
 * 
 * @author yjc
 */
public class SecUtil{

	/**
	 * 通过md5进行加密操作
	 * 
	 * @author yjc
	 * @throws NoSuchAlgorithmException
	 * @date 创建时间 2015-5-23
	 * @since V1.0
	 */
	public static String encodeStrByMd5(String paraStr) throws AppException {
		try {
			if (null == paraStr) {
				paraStr = "";
			}
			// 对字符串进行增加本地标识
			paraStr = "com.grace.frame." + paraStr;
			paraStr = String.valueOf(paraStr.length()) + paraStr;
			return SecUtil.encodeStrByOriginalMd5(paraStr);
		} catch (Exception e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	private static final String MD5_CHAESET = "UTF-8";// md5使用的字符格式

	/**
	 * 通过md5进行加密操作(原生的MD加密方式)
	 * 
	 * @author yjc
	 * @throws NoSuchAlgorithmException
	 * @date 创建时间 2015-5-23
	 * @since V1.0
	 */
	public static String encodeStrByOriginalMd5(String paraStr) throws AppException {
		try {
			if (null == paraStr) {
				paraStr = "";
			}
			// MD5进行加密处理
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(paraStr.getBytes(SecUtil.MD5_CHAESET));
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			String encodeStr = buf.toString();

			return encodeStr.toUpperCase();
		} catch (Exception e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	/**
	 * byte转base64字符码
	 */
	public static String base64Encode(byte[] src) throws AppException {
		Base64 base64 = new Base64();
		byte[] bin = base64.encode(src);
		return new String(bin);
	}

	/**
	 * base64码转byte
	 */
	public static byte[] base64Decode(String src) throws AppException {
		Base64 base64 = new Base64();
		byte[] binBytes;
		binBytes = base64.decode(src.getBytes());
		return binBytes;
	}

	/**
	 * DES加密介绍 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
	 * 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
	 * 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现 。
	 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数<br>
	 * 加密操作
	 */
	public static String encryptDES(String str, String password) throws Exception {
		if (StringUtil.chkStrNull(str)) {
			str = "";
		}
		if (StringUtil.chkStrNull(password)) {
			throw new AppException("DES加密时传入的密钥为空");
		}
		if (password.length() % 8 != 0) {
			throw new AppException("DES加密时传入的密钥不合法，长度必须为8的倍数");
		}

		// 加密操作
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		// 现在，获取数据并加密
		// 正式执行加密操作
		byte[] btye_jmjg = cipher.doFinal(str.getBytes());// 加密结果

		// 转为base64str返回
		String jmjg = SecUtil.base64Encode(btye_jmjg);
		return jmjg;
	}

	/**
	 * DES加密介绍 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
	 * 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
	 * 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现 。
	 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数<br>
	 * 解密操作
	 */
	public static String decryptDES(String str, String password) throws Exception {
		if (StringUtil.chkStrNull(str)) {
			throw new BizException("DES解密操作时，传入的密文为空");
		}
		if (StringUtil.chkStrNull(password)) {
			throw new AppException("DES解密时传入的密钥为空");
		}
		if (password.length() % 8 != 0) {
			throw new AppException("DES解密时传入的密钥不合法，长度必须为8的倍数");
		}
		// 将base64str转为byte
		byte[] byte_jmjg = SecUtil.base64Decode(str);

		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		byte[] byte_src = cipher.doFinal(byte_jmjg);
		String src = new String(byte_src);// 获取明文
		return src;
	}

	/**
	 * 新加密算法--各种类型DESede的加密算法
	 */
	private static final String Algorithm = "DESede"; // 定义 加密算法,可用
	// DES,DESede,Blowfish
	private static final byte[] keyBytes = { -50, 79, -123, 82, 64, 1, 7, -116, -53, -48, 44, 44, 13, -111, 41, -85, -26, 4, 4, 107, -77, 112, 59, 8 };// 24字节的密钥(自动生成)

	/**
	 * keybyte为加密密钥，长度为24字节 <br>
	 * src为被加密的数据缓冲区（源）
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-19
	 * @since V1.0
	 */
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, SecUtil.Algorithm);
			// 加密
			Cipher c1 = Cipher.getInstance(SecUtil.Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 增加String2String的加密
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-19
	 * @since V1.0
	 */
	public static String encryptMode(byte[] keybyte, String src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, SecUtil.Algorithm);

			// 加密
			Cipher c1 = Cipher.getInstance(SecUtil.Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			byte[] srcBytes = src.getBytes("UTF-8");
			byte[] resultBytes = c1.doFinal(srcBytes);
			Base64 base64 = new Base64();
			return new String(base64.encode(resultBytes), "UTF-8");
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-19
	 * @since V1.0
	 */
	public static String encryptMode(String src) {
		return SecUtil.encryptMode(SecUtil.keyBytes, src);
	}

	/**
	 * keybyte为加密密钥，长度为24字节 <br>
	 * src为被加密的数据缓冲区（源）
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-19
	 * @since V1.0
	 */
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, SecUtil.Algorithm);

			// 解密
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 增加String2String的解密
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-19
	 * @since V1.0
	 */
	public static String decryptMode(byte[] keybyte, String src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, SecUtil.Algorithm);

			// 解密
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			Base64 base64 = new Base64();
			byte[] srcBytes = base64.decode(src.getBytes("UTF-8"));
			byte[] resultBytes = c1.doFinal(srcBytes);
			return new String(resultBytes, "UTF-8");
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	/**
	 * 增加String2String的解密
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-19
	 * @since V1.0
	 */
	public static String decryptMode(String src) {
		return SecUtil.decryptMode(SecUtil.keyBytes, src);
	}

	/**
	 * 检测请求来源和请求地址域名是否同源
	 * <p>
	 * 目的是防止CSRF攻击
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2017-10-18
	 * @since V1.0
	 */
	public static boolean checkRefererAndRequestURLIsSame(
			HttpServletRequest request) throws Exception {
		String referer = request.getHeader("Referer");// 获取请求来源，如果是地址栏直接输入地址，或者服务接口调用，则为空
		if (null == referer || "".equals(referer)) {
			// 对用空的情况，认为一致
			return true;
		} else {
			String basePath = TagSupportUtil.getBasePathFromRequest(request);// 请求路径
			// 获取域名
			String refererDomain = StringUtil.getDomain(referer);// 源-域名
			if (GlobalVars.NO_CHK_SAME_REFERE.containsKey(refererDomain.toLowerCase())) {// 对于例外的不验证同源的源
				return true;
			}
			String basePathDomain = StringUtil.getDomain(basePath);// 请求域名

			// 检测是否一致
			if (!basePathDomain.equalsIgnoreCase(refererDomain)) {// 域名不一致
				// 系统级日志，记录一下
				SysLogUtil.logInfo("跨站请求伪造（CSRF）攻击嫌疑--攻击源：" + referer);
				return false;
			}
		}
		return true;
	}
}
