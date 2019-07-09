/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.web.listener.adapter;

import javax.servlet.ServletContext;

/**
 * @author jay
 *
 */
public interface ContextLoaderAdapter {

	void beforeInitialize(ServletContext context);
	
	 void afterInitialize(ServletContext context);
	
	 void beforeDestroy(ServletContext context);
	
	 void afterDestroy(ServletContext context);
	 
}
