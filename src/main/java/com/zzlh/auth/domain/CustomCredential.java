package com.zzlh.auth.domain;

import org.apereo.cas.authentication.UsernamePasswordCredential;

/**
 * CA 用户凭证信息对象 增加CA使用的类型和随机数
 *
 * Created by Sofar on 2016-04-08.
 */
public class CustomCredential extends UsernamePasswordCredential {
    private static final long serialVersionUID = -700605081472810939L;
    private String type;	// 登录类型 1:用户登录 2:ca登录
    private String random;	// 随机码 ca登录使用
    private String code;// 验证码 用户登录使用

    public CustomCredential() {}

    public CustomCredential(final String userName, final String password) {
        super(userName,password);
    }

    public CustomCredential(String username, String password, String random) {
        super(username,password);
        this.random = random;
    }

    public CustomCredential(String username, String password, String type, String random) {
        super(username,password);
        this.type = type;
        this.random = random;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
