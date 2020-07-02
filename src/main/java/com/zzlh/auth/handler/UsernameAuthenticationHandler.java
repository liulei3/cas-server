package com.zzlh.auth.handler;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zzlh.auth.conf.CacheBuilderConf;
import com.zzlh.auth.domain.CustomCredential;
import com.zzlh.auth.util.AssitUtil;
import com.zzlh.auth.util.ShaMessageDigest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UsernameAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {
	@Autowired
	private CacheBuilderConf cacheBuilderConf;
	private int expired=180;
	private String callback;
	
    protected String algorithmName;
    protected String sql;
    // 密码字段
    protected String passwordFieldName;
    // 盐字段
    protected String saltFieldName;
    // 自定义盐
    protected String staticSalt;
    private static String ID = "id";
    private static String PASS_CHANGED_TIME = "pass_changed_time";
    
	public UsernameAuthenticationHandler(String name, ServicesManager servicesManager,
			PrincipalFactory principalFactory, Integer order, DataSource dataSource, String algorithmName, String sql,
			String passwordFieldName, String saltFieldName, String staticSalt) {
		super(name, servicesManager, principalFactory, order, dataSource);
		this.algorithmName = algorithmName;
		this.sql = sql;
		this.passwordFieldName = passwordFieldName;
		this.saltFieldName = saltFieldName;
		this.staticSalt = staticSalt;
	}

	@Override
    public boolean supports(Credential credential) {
        if(!"2".equals(((CustomCredential)credential).getType())) {
            return true;
        }
        return false;
    }
    
	@Override
    protected AuthenticationHandlerExecutionResult doAuthentication(final Credential credential) throws GeneralSecurityException, PreventedException {
		final CustomCredential userPass = (CustomCredential) credential;
        if (StringUtils.isBlank(userPass.getUsername().trim()) || StringUtils.isBlank(userPass.getPassword().trim())) {
            throw new AccountNotFoundException("Username or Password is null.");
        }
        cacheBuilderConf.vaildCode(userPass.getCode());
        return authenticateUsernamePasswordInternal(userPass, userPass.getPassword());
    }
	 
    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
    		final UsernamePasswordCredential transformedCredential,final String originalPassword)
        throws GeneralSecurityException, PreventedException {
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
    	cacheBuilderConf.vaildTime(request);
    	
        final String username = transformedCredential.getUsername();
        try {
            final Map<String, Object> values = getJdbcTemplate().queryForMap(this.sql, new Object[] {username,username,username});
            final String oldPassword = ShaMessageDigest.encode(originalPassword, null);
            final String saltPassword = ShaMessageDigest.encode(originalPassword, staticSalt);
            final String id = String.valueOf(values.get(ID));
            final String dbPassword = String.valueOf(values.get(this.passwordFieldName));
            final Date lastUpdateTime = (Date) values.get(PASS_CHANGED_TIME);
            if (!dbPassword.equals(oldPassword) && !dbPassword.equals(saltPassword)) {
            	cacheBuilderConf.countTime(request);
            	log.error("密码不正确!");
                throw new AccountNotFoundException("Password does not match value on record.");
            }
//            if( (AssitUtil.getDatePoor(new Date(),lastUpdateTime)>expired) || AssitUtil.isSimple(originalPassword)){
        	if( (AssitUtil.getDatePoor(new Date(),lastUpdateTime)>expired)){
                request.setAttribute("id",id);
                request.setAttribute("username",username);
                request.setAttribute("callback",callback);
                log.error("账户过期!");
                throw new AccountLockedException("account expired");
            }
            return createHandlerResult(transformedCredential, this.principalFactory.createPrincipal(username), new ArrayList<>(0));
        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
            	log.error("{} 账户不存在!",username);
                throw new AccountNotFoundException(username + " not found with SQL query");
            }
            log.error("{} 有多个记录!",username);
            throw new FailedLoginException("Multiple records found for " + username);
        } catch (final DataAccessException e) {
        	log.error("SQL执行异常!{}",e);
            throw new PreventedException("SQL exception while executing query for " + username, e);
        }
    }

}
