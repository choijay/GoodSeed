/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import goodseed.core.utility.logging.ActionLogger;

/**
 * The class ControllerLogger
 *
 * @author jay
 * @version 1.0
 *
 */
public class ControllerLogger extends HandlerInterceptorAdapter{
    
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {

        // initialize WebApplicationContext.
        ActionLogger.requestLogger(request);

        return true;
    }
}
