package com.grace.frame.util.captcha;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.grace.frame.constant.GlobalVars;

/**
 * 点选验证码
 * 
 * @author yjc
 */
public class ClickTextCaptcha implements Captcha{

	private String[] words;
	private String[] chkWords;
	private List<Rectangle> rectangles;
	private BufferedImage image;

	public String[] getWords() {
		return words;
	}

	public String[] getChkWords() {
		return chkWords;
	}

	public void setWords(String[] words) {
		this.words = words;
		this.chkWords = new String[GraphicsEngine.WORD_CHK_COUNT];
		for (int i = 0; i < GraphicsEngine.WORD_CHK_COUNT; i++) {
			this.chkWords[i] = this.words[i];
		}
	}

	public List<Rectangle> getRectangles() {
		return rectangles;
	}

	public void setRectangles(List<Rectangle> rectangles) {
		this.rectangles = rectangles;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * 检查输入点是否正确
	 * 
	 * @author yjc
	 * @date 创建时间 2020-4-26
	 * @since V1.0
	 */
	public boolean check(List<ClickPoint> points) {
		if (GlobalVars.DEBUG_MODE) {// 对于开发模式，验证码全部返回验证通过
			return true;
		}
		return GraphicsEngine.checkClickTextCaptcha(points, this.getRectangles());
	}

	/**
	 * 设置已认证通过
	 * 
	 * @author yjc
	 * @date 创建时间 2020-4-29
	 * @since V1.0
	 */
	public static void setValidatePass(HttpSession session) {
		session.setAttribute("login_click_text_captcha_validate_pass", true);
	}

	/**
	 * 获取是否已经认证通过
	 * 
	 * @author yjc
	 * @date 创建时间 2020-4-29
	 * @since V1.0
	 */
	public static boolean getValidatePass(HttpSession session) {
		Boolean pass = (Boolean) session.getAttribute("login_click_text_captcha_validate_pass");
		session.removeAttribute("login_click_text_captcha_validate_pass");// 验证过后，立即释放
		if (pass == null) {
			pass = false;
		}
		return pass;
	}
}