/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.common.syscommon.web.listener;

import javax.servlet.ServletContextEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoaderListener;

import goodseed.core.common.web.listener.adapter.ContextLoaderAdapter;

/**
 * The class ContextLoaderListener
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class GoodseedContextLoaderListener extends ContextLoaderListener {

	private static final Log LOG = LogFactory.getLog(ContextLoaderListener.class);

	private ContextLoaderAdapter adapter;

	private void loadContextLoaderAdapter(ServletContextEvent event) {

		String adapterClassName = event.getServletContext().getInitParameter("contextLoaderAdapter");

		if(StringUtils.isEmpty(adapterClassName)) {
			return;
		}

		try {
			Class<?> adapterClass = Class.forName(adapterClassName);
			adapter = (ContextLoaderAdapter)adapterClass.newInstance();
		} catch(ClassNotFoundException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} catch(InstantiationException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} catch(IllegalAccessException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		}
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		loadContextLoaderAdapter(event);

		if(adapter != null) {
			adapter.beforeInitialize(event.getServletContext());
		}

		super.contextInitialized(event);

		if(adapter != null) {
			adapter.afterInitialize(event.getServletContext());
		}

//		CodeUtil.init();

	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(adapter != null) {
			adapter.beforeDestroy(event.getServletContext());
		}

//		Scheduler scheduler =
//				(Scheduler)WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()).getBean("scheduler");
//		if(scheduler != null) {
//			try {
//				scheduler.shutdown(true);
//			} catch(SchedulerException e) {
//				e.printStackTrace();
//				LOG.error("Exception", e);
//			}
//		}
		super.contextDestroyed(event);

		if(adapter != null) {
			adapter.afterDestroy(event.getServletContext());
		}
		
	}
	
	
}
