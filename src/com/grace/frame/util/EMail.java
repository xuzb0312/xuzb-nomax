package com.grace.frame.util;

import java.io.File;
import java.io.Serializable;

import com.grace.frame.exception.AppException;

/**
 * 邮件发送的基本信息
 * 
 * @author yjc
 */
public class EMail implements Serializable{
	private static final long serialVersionUID = 1L;
	private String subject;
	private String from;
	private String smtp;
	private String smtpPort;
	private String smtpUser;
	private String smtpPassword;
	private String[] to;
	private String[] copyTo;
	private File[] attachment;
	private String content;
	private String mimetype;

	/**
	 * 获取系统的默认设置
	 * 
	 * @throws AppException
	 */
	public EMail() throws AppException {
		super();
		this.from = SysParaUtil.getPara("email_from");
		this.smtp = SysParaUtil.getPara("email_smtp");
		this.smtpPort = SysParaUtil.getPara("email_smtp_port");
		this.smtpUser = SysParaUtil.getPara("email_smtp_user");
		this.smtpPassword = SysParaUtil.getPara("email_smtp_password");
		this.mimetype = "text/html";
	}

	public EMail(String from, String smtp, String smtpPort, String smtpUser,
		String smtpPassword) {
		super();
		this.from = from;
		this.smtp = smtp;
		this.smtpPort = smtpPort;
		this.smtpUser = smtpUser;
		this.smtpPassword = smtpPassword;
		this.mimetype = "text/html";
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String[] getCopyTo() {
		return copyTo;
	}

	public void setCopyTo(String[] copyTo) {
		this.copyTo = copyTo;
	}

	public File[] getAttachment() {
		return this.attachment;
	}

	public void setAttachment(File[] attachment) {
		this.attachment = attachment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

}
