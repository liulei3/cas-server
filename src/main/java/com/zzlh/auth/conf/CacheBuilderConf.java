package com.zzlh.auth.conf;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.FailedLoginException;
import javax.security.cert.X509Certificate;
import javax.servlet.http.HttpServletRequest;

import org.apereo.cas.authentication.exceptions.InvalidLoginTimeException;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zzlh.auth.exception.AccountCodeException;
import com.zzlh.auth.exception.AccountSignNameException;
import com.zzlh.auth.util.AssitUtil;

public class CacheBuilderConf{
    private Integer loginTime = 15;
    private Integer randomTime = 60;
    private Integer codeTime = 60;

    private LoadingCache<String, Integer> cache= CacheBuilder.newBuilder().expireAfterAccess(loginTime , TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
        public Integer load(String key) throws Exception {
            return 0;
        }
    });

    private LoadingCache<String, String> randomCache = CacheBuilder.newBuilder().expireAfterAccess(randomTime , TimeUnit.SECONDS).build(new CacheLoader<String, String>() {
        public String load(String key) throws Exception {
            return "";
        }
    });
    
    private LoadingCache<String, String> codeCache = CacheBuilder.newBuilder().expireAfterAccess(codeTime , TimeUnit.SECONDS).build(new CacheLoader<String, String>() {
    	public String load(String key) throws Exception {
    		return "";
    	}
    });
    
    /**
     * @Description 校验登录次数
     * @param request
     * @throws InvalidLoginTimeException
     */
    public void vaildTime(HttpServletRequest request) throws FailedLoginException {
        try {
            if(cache.get(constructKey(request))>5){
                throw new FailedLoginException();
            }
        } catch (ExecutionException e) {
            throw new FailedLoginException(e.getMessage());
        }
    }
    
    /**
     * @Description ca校验
     * @param request
     * @return
     * @throws FailedLoginException
     */
    public String vaildCAParam(HttpServletRequest request) throws GeneralSecurityException {
		vaildTime(request);
		String param = request.getParameter("random");
		return verifySign(param);
    }

	/**
     * @Description 记录登录次数
     * @param request
     * @throws InvalidLoginTimeException
     */
    public void countTime(HttpServletRequest request) throws InvalidLoginTimeException{
        try {
            cache.put(constructKey(request),cache.get(constructKey(request))+1);
        } catch (ExecutionException e) {
            throw new InvalidLoginTimeException(e.getMessage());
        }
    }
    
    /**
     * @Description 添加随机缓存
     * @param key
     * @param value
     */
    public void putParam(String key,String value) {
		randomCache.put(key,value);
    }
    
    /**
     * @Description 格式化缓存key值
     * @param request
     * @return
     */
    private String constructKey(final HttpServletRequest request) {
        final String username = request.getParameter("username");
        if (username == null) {
            return request.getRemoteAddr();
        }
        return ClientInfoHolder.getClientInfo().getClientIpAddress() + ';' + username.toLowerCase();
    }
    
    /**
     * @Description 校验签名
     * @param base64Encode 加密数据:经Base64编码的ca证书id,经Base64编码的待签名数据,经Base64编码的签名证书,经Base64编码的签名结果
     * @return 用户名
     * @throws Exception
     */
    private String verifySign(String base64Encode) throws AccountSignNameException {
    	String[] str = base64Encode.split(",");
		X509Certificate instance;
		try {
			instance = X509Certificate.getInstance(Base64.decode(str[2].getBytes()));
			instance.checkValidity(new Date());
			byte[] data = Base64.decode(str[1].getBytes());
			if(!AssitUtil.verifySign(instance,data,str[3])) {
				throw new AccountSignNameException("签名无效!");
			}
			if(StringUtils.isEmpty(randomCache.get(new String(data)))) {
				throw new AccountSignNameException("随机码无效!");
			}
			return new String(Base64.decode(str[0].getBytes()));
		} catch (Exception e) {
			throw new AccountSignNameException(e.getMessage());
		}
	}
   
    /**
     * @Description 添加验证码
     * @param key 验证码key
     * @param value 验证码
     */
    public void putCode(String key,String value) {
    	codeCache.put(key,value);
    }
    
    /**
     * @Description 校验验证码
     * @param code 验证码
     * @throws FailedLoginException
     */
    public void vaildCode(String code) throws AccountCodeException {
    	try {
    		String string = codeCache.get(code).toLowerCase();
    		System.out.println(string);
    		if(StringUtils.isEmpty(code) || !string.equals(code)) {
    			throw new AccountCodeException("验证码无效!");
    		}
    	} catch (Exception e) {
    		throw new AccountCodeException(e.getMessage());
    	}
    }
}
