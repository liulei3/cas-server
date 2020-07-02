package com.zzlh.auth.handler;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

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

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义验证处理器 主要用来完成CA key验证工作
 *
 * Created by Sofar on 2016-04-08.
 */
@Slf4j
public class CaAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {
	@Autowired
	private CacheBuilderConf cacheBuilderConf;
	
	@NotNull
	private String sql;
	
	public CaAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory,
			Integer order, DataSource dataSource,String sql) {
		super(name, servicesManager, principalFactory, order, dataSource);
		this.sql = sql;
	}

	@Override
    public boolean supports(Credential credential) {
        if("2".equals(((CustomCredential)credential).getType())) {
            return true;
        }
        return false;
    }
	
	@Override
	protected final AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
			UsernamePasswordCredential credential, String originalPassword)
			throws GeneralSecurityException, PreventedException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String username = null;
		try {
			String caId= cacheBuilderConf.vaildCAParam(request);
			System.out.println("caId: "+caId);
			username = getJdbcTemplate().queryForObject(sql, String.class, caId);
			if (username == null) {
				cacheBuilderConf.countTime(request);
				log.error("caId {} 不存在", username);
				throw new FailedLoginException("no records found for caID [" + username + "]");
			}
		} catch (final IncorrectResultSizeDataAccessException e) {
			if (e.getActualSize() == 0) {
				log.error("{} 用户不存在", username);
				throw new AccountNotFoundException(username + " not found with SQL query");
			} else {
				log.error("{} 用户有多条记录", username);
				throw new FailedLoginException("Multiple records found for " + username);
			}
		} catch (final DataAccessException e) {
			log.error("SQL执行异常 {}", e);
			throw new PreventedException("SQL exception while executing query for " + username, e);
		}
		credential.setUsername(username);
		return createHandlerResult(credential, this.principalFactory.createPrincipal(username), new ArrayList<>(0));
	}
}
