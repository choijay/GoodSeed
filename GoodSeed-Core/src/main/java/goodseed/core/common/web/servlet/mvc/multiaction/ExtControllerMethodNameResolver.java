/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * The class ExtControllerMethodNameResolver
 *
 * @author jay
 *
 */
public class ExtControllerMethodNameResolver implements MethodNameResolver {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.multiaction.MethodNameResolver#getHandlerMethodName(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String getHandlerMethodName(HttpServletRequest arg0) throws NoSuchRequestHandlingMethodException {
		return "extController";
	}

}
