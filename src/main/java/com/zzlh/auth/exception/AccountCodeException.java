package com.zzlh.auth.exception;
/**
 * @Description 账户验证码异常
 * @author liulei
 * @date 2018年12月5日 上午11:03:26
 */
public class AccountCodeException extends javax.security.auth.login.AccountException {

	private static final long serialVersionUID = 1L;

	public AccountCodeException() {
		super();
	}

	public AccountCodeException(String arg0) {
		super(arg0);
	}
}
