/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.web.listener.adapter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import goodseed.core.utility.config.Config;

//import frameone.core.common.utility.EsapiSetupUtil;
//import frameone.core.utility.config.Config;

/**
 * The class DefaultContextLoaderAdapter<br>
 * <br>
 * TODO 설명을 상세히 입력하세요.<br>
 * <br>
 * 
 * @author jay
 *
 */
public class DefaultContextLoaderAdapter implements ContextLoaderAdapter {

	private static final Log LOG = LogFactory.getLog(DefaultContextLoaderAdapter.class);

	private static WebApplicationContext webApplicationContext;
	private static ApplicationContext applicationContext;

	@Override
	public void beforeInitialize(ServletContext context) {

		// 시스템 설정파일 위치 세팅 (ex: applicationProperties.xml)
		Config.setConfigLocation(context.getInitParameter("systemConfigLocation"));
//
//		EsapiSetupUtil.initialize(context.getInitParameter("esapiConfigLocation"));
	}

	@Override
	public void afterInitialize(ServletContext context) {

		webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(context);

	}

	@Override
	public void beforeDestroy(ServletContext context) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("## beforeInitialize beforeDestroy");
		}
	}

	@Override
	public void afterDestroy(ServletContext context) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("## beforeInitialize afterDestroy");
		}
	}

	/**
	 * Get bean instance.
	 * 
	 * @param beanName String
	 * @return Object
	 */
	public static final Object getBean(final String beanName) {
		if(webApplicationContext == null) {
			if(applicationContext != null) {
				return applicationContext.containsBean(beanName) ? applicationContext.getBean(beanName) : null;
			} else {
				return null;
			}
		} else {
			return webApplicationContext.containsBean(beanName) ? webApplicationContext.getBean(beanName) : null;
		}
	}

	/**
	 * Set Web Application Context.
	 * 
	 * @param request
	 */
	public static final void setWebApplicationContext(HttpServletRequest request) {
		webApplicationContext = RequestContextUtils.findWebApplicationContext(request);
	}

	/**
	 * Set Web Application Context.
	 * 
	 * @param context
	 */
	public static final void setWebApplicationContext(WebApplicationContext context) {
		webApplicationContext = context;
	}

	public static final void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}

	public static final ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Get the Web Applicaton Context.
	 * 
	 * @return
	 */
	public static final WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}
}
