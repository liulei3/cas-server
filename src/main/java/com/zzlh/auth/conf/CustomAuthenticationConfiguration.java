package com.zzlh.auth.conf;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.JdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.QueryEncodeJdbcAuthenticationProperties;
import org.apereo.cas.configuration.support.JpaBeans;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.zzlh.auth.controller.LoginController;
import com.zzlh.auth.handler.CaAuthenticationHandler;
import com.zzlh.auth.handler.UsernameAuthenticationHandler;


/**
 * 参考 {@link org.apereo.cas.web.flow.config.CasWebflowContextConfiguration}
 *  DefaultLoginWebflowConfigurer
 */
@EnableWebMvc
@Configuration
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomAuthenticationConfiguration extends CasWebflowContextConfiguration implements 
		AuthenticationEventExecutionPlanConfigurer {
	
	@Autowired
    private CasConfigurationProperties casProperties;
    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;
    
    @Bean
    public CacheBuilderConf getPasswordRetryHandlerInterceptor() {
    	return new CacheBuilderConf();
    }
    
    @Bean("randomAction")
    public RandomAction createRandomAction() {
    	return new RandomAction();
    }
    @Bean
    public LoginController createLoginController() {
    	return new LoginController();
    }
    
    /**
     * @Description cas登录认证
     * @return
     */
    @Bean
    public AuthenticationHandler customAuthenticationHandler() {
    	final JdbcAuthenticationProperties jdbc = casProperties.getAuthn().getJdbc();
        QueryEncodeJdbcAuthenticationProperties param = jdbc.getEncode().get(0);
    	UsernameAuthenticationHandler handler = new UsernameAuthenticationHandler(param.getName(),
    			servicesManager,new DefaultPrincipalFactory(),param.getOrder(),
    			JpaBeans.newDataSource(param),param.getAlgorithmName(),param.getSql(),
    			param.getPasswordFieldName(),param.getSaltFieldName(),param.getStaticSalt());
        return handler;
    }
   
    /**
     * @Description ca登录认证
     * @return
     */
    @Bean
    public AuthenticationHandler caAuthenticationHandler() {
    	final JdbcAuthenticationProperties jdbc = casProperties.getAuthn().getJdbc();
        QueryEncodeJdbcAuthenticationProperties param = jdbc.getEncode().get(1);
        CaAuthenticationHandler handler = new CaAuthenticationHandler(param.getName(),
    			servicesManager,new DefaultPrincipalFactory(),param.getOrder(),
    			JpaBeans.newDataSource(param),param.getSql());
        return handler;
    }
    
    /**
     * @Description 注册验证器
     * @return
     */
    @Override
    public void configureAuthenticationExecutionPlan(AuthenticationEventExecutionPlan plan) {
        plan.registerAuthenticationHandler(customAuthenticationHandler());
        plan.registerAuthenticationHandler(caAuthenticationHandler());
    }
}
