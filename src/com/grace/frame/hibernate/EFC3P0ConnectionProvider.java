package com.grace.frame.hibernate;

import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.C3P0ConnectionProvider;

import com.grace.frame.util.SecUtil;

/**
 * 使用了加密密码的数据库驱动类
 * 
 * @author yjc
 */
public class EFC3P0ConnectionProvider extends C3P0ConnectionProvider{
	public EFC3P0ConnectionProvider() {
		super();
	}

	@Override
	public void configure(Properties props) throws HibernateException {
		String user = props.getProperty(Environment.USER);
		String password = props.getProperty(Environment.PASS);
		props.setProperty(Environment.USER, SecUtil.decryptMode(user));
		props.setProperty(Environment.PASS, SecUtil.decryptMode(password));
		super.configure(props);
	}
}
