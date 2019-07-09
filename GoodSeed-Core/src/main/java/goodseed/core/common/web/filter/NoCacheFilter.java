/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class NoCacheFilter
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class NoCacheFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(NoCacheFilter.class);

	@Override
	public void destroy() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("## NoCacheFilter destory");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletResponse hresponse = (HttpServletResponse)response;
		hresponse.setHeader("Pragma", "no-cache");
		hresponse.setHeader("Cache-Control", "no-cache");
		hresponse.setDateHeader("Expires", 0);
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("## NoCacheFilter init");
		}
	}

}
