package com.grace.frame.util;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * 电子邮件操作类-具体使用方法：<br>
 * EMail mail=new Email();<br>
 * mail.setSubject("测试");<br>
 * mail.setContent("内容");<br>
 * EMailUtil eu=new EMailUtil(mail);<br>
 * eu.send();<br>
 * 
 * @author yjc
 */
public class EMailUtil{
	private MimeMessage mimeMsg; // MIME邮件对象
	private Session session; // 邮件会话对象
	private Properties props; // 系统属性
	private Multipart mp;
	private EMail email;

	/**
	 * 构造函数
	 * 
	 * @param email
	 * @throws AppException
	 */
	public EMailUtil(EMail email) throws AppException {
		this.email = email;
		this.setSmtpHost(email.getSmtp());
		this.setSmtpHostPort(email.getSmtpPort());// 端口
		this.createMimeMessage();
		this.setNeedAuth(true);
		this.setSubject(email.getSubject());
		this.setBody(email.getContent(), email.getMimetype());
		if (email.getAttachment() != null) {
			this.addFileAffix(email.getAttachment());
		}
		this.setFrom(email.getFrom());
		this.setTo(email.getTo());
		if (email.getCopyTo() != null) {
			this.setCopyTo(email.getCopyTo());
		}
	}

	/**
	 * 设置主机
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setSmtpHost(String hostName) {
		if (props == null) {
			props = System.getProperties(); // 获得系统属性对象
		}

		props.put("mail.smtp.host", hostName); // 设置SMTP主机
	}

	/**
	 * 设置主机端口
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setSmtpHostPort(String hostPort) {
		if (props == null) {
			props = System.getProperties(); // 获得系统属性对象
		}
		if ("465".equals(hostPort)) {
			// 如果是465端口，则认为服务器启用了ssl加密发送
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			// 邮箱发送服务器端口,这里设置为465端口
			props.setProperty("mail.smtp.port", "465");
			props.setProperty("mail.smtp.socketFactory.port", "465");
		} else {
			props.put("mail.smtp.port", hostPort); // 设置SMTP主机端口
		}
	}

	/**
	 * 准备工作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void createMimeMessage() throws AppException {
		try {
			session = Session.getInstance(props, null); // 获得邮件会话对象
			if (GlobalVars.DEBUG_MODE) {
				session.setDebug(true);// 是否在控制台显示debug信息
			}
		} catch (Exception e) {
			throw new AppException("获取邮件会话对象时发生错误！" + e.getMessage(), "EMailUtil.createMimeMessage");
		}
		try {
			mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
			mp = new MimeMultipart();
		} catch (Exception e) {
			throw new AppException("创建MIME邮件对象失败！" + e.getMessage(), "EMailUtil.createMimeMessage");
		}
	}

	/**
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setNeedAuth(boolean need) {
		if (props == null)
			props = System.getProperties();
		if (need) {
			props.put("mail.smtp.auth", "true");
		}
	}

	/**
	 * 设置主题
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setSubject(String mailSubject) throws AppException {
		try {
			mimeMsg.setSubject(mailSubject);
		} catch (Exception e) {
			throw new AppException("设置邮件主题发生错误！" + e.getMessage(), "EMailUtil.setSubject");
		}
	}

	/**
	 * 设置邮件正文
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setBody(String mailBody, String mimetype) throws AppException {
		try {
			BodyPart bp = new MimeBodyPart();
			bp.setContent(mailBody, mimetype.trim() + ";charset=UTF-8");
			mp.addBodyPart(bp);
		} catch (Exception e) {
			throw new AppException("设置邮件正文时发生错误！" + e.getMessage(), "EMailUtil.setBody");
		}
	}

	/**
	 * 附件
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void addFileAffix(File[] file) throws AppException {
		for (int i = 0; i < file.length; i++) {
			if (file[i] == null) {
				continue;
			}
			String fileName = file[i].getName();
			if (StringUtil.chkStrNull(fileName)) {
				fileName = "匿名文件";
			}
			try {
				BodyPart bp = new MimeBodyPart();
				FileDataSource fileds = new FileDataSource(file[i]);
				bp.setDataHandler(new DataHandler(fileds));
				bp.setFileName(MimeUtility.encodeText(fileName));
				mp.addBodyPart(bp);
			} catch (Exception e) {
				throw new AppException("增加邮件附件：" + fileName + "发生错误！"
						+ e.getMessage(), "EMailUtil.addFileAffix");
			}

		}
	}

	/**
	 * 发信人
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setFrom(String from) throws AppException {
		try {
			mimeMsg.setFrom(new InternetAddress("\""
					+ MimeUtility.encodeText(GlobalVars.SYS_DBNAME + "."
							+ GlobalVars.APP_NAME) + "\" <" + from + ">")); // 设置发信人
		} catch (Exception e) {
			throw new AppException("设置发信人时出错" + e.getMessage(), "EMailUtil.setFrom");
		}
	}

	/**
	 * 收件人
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setTo(String[] to) throws AppException {
		if (to == null || to.length == 0)
			throw new AppException("设置收信邮箱时，收信邮箱为空", "EMailUtil.setTo");
		try {
			InternetAddress address[] = new InternetAddress[to.length];
			for (int i = 0; i < address.length; i++)
				address[i] = new InternetAddress(to[i]);
			mimeMsg.setRecipients(Message.RecipientType.TO, address);
		} catch (Exception e) {
			throw new AppException("设置收信邮箱时，收信邮箱为空" + e.getMessage(), "EMailUtil.setTo");
		}
	}

	/**
	 * 抄送
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	private void setCopyTo(String[] copyto) throws AppException {
		try {
			if (copyto.length > 0) {
				InternetAddress address[] = new InternetAddress[copyto.length];
				for (int i = 0; i < address.length; i++)
					address[i] = new InternetAddress(copyto[i]);
				mimeMsg.setRecipients(Message.RecipientType.CC, address);
			}
		} catch (Exception e) {
			throw new AppException("设置抄送时，抄送邮箱为空" + e.getMessage(), "EMailUtil.setTo");
		}
	}

	/**
	 * 邮件发送
	 * <p>
	 * </p>
	 * 
	 * @param
	 * @param
	 * @return
	 * @return
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	public void send() throws AppException {
		try {
			mimeMsg.setSentDate(new Date());
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			Transport transport = session.getTransport("smtp");
			transport.connect(email.getSmtp(), email.getSmtpUser(), email.getSmtpPassword());
			transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			throw new AppException("邮件发送失败！" + e.getMessage(), "EMailUtil.send");
		}
	}
}
