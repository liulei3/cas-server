package com.zzlh.auth.conf;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * CA登录过程页面流程中随机数生成来完成CA验证工作.
 * Created by Sofar on 2016-04-07.
 */
public class RandomAction extends AbstractAction {
	
	@Value("${home.url}")
	private String homeUrl;
	@Value("${register.url}")
	private String registerUrl;
	@Value("${forgetpwd.url}")
	private String forgetpwdUrl;
	@Autowired
	private CacheBuilderConf cacheBuilderConf;
	@Override
    protected Event doExecute(RequestContext context) throws Exception {
        String random = String.valueOf(new SecureRandom().nextDouble());
        context.getFlowScope().put("random", random);
        cacheBuilderConf.putParam(random, random);
        Random picRandom = new Random();
        context.getFlowScope().put("pic", String.valueOf(picRandom.nextInt(4)+1));
        context.getFlowScope().put("homeUrl", homeUrl);
        context.getFlowScope().put("registerUrl", registerUrl);
        context.getFlowScope().put("forgetpwdUrl", forgetpwdUrl);
        ParameterMap parameters = context.getRequestParameters();
        if(parameters!=null&&parameters.size()>0) {
        	context.getFlowScope().put("username", parameters.get("username"));
        }
        return success();
    }
}
