/*
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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConstants;

/**
 * The class EnvironmentSetupFilter<br>
 * <br>
 * 시스템 전반적으로 참조할 정보를 가공하여 저장하는 필터<br>
 * <br>
 * 수행로직<br>
 * 	(1) 요청URI를 가공하여 request의 속성으로 저장한다.<br> 
 * 	(2) 클라이언트 IP를 얻어서 request의 속성으로 저장한다.<br> 
 *
 * @author jay
 * @version 1.0
 * @since 4. 9.
 *
 */
public class EnvironmentSetupFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(EnvironmentSetupFilter.class);

	/**
	 * 시스템 전반적으로 참조할 정보를 가공하여 저장하는 메서드<br>
	 * <br>
	 * 
	 * @param ServletRequest	URI, URL, 클라이언트 IP를 추출할 대상
	 * @param ServletResponse
	 * @param FilterChain
	 * @return void
	 * @throws IOException
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		//----------------------------------- 요청 URI 작업 - start ---------------------------------------------
		//요청 URI를 contextPath를 제거한 형태로 가공하여 request에 저장
		HttpServletRequest hReq = (HttpServletRequest)request;

		String contextPath = hReq.getContextPath();
		String reqURL = hReq.getRequestURL().toString();
		//요청 URI - get 파라미터가 제거된 상태
		String reqURI = hReq.getRequestURI();

		if(LOG.isDebugEnabled()) {
			LOG.debug("reqURL : " + reqURL);
			LOG.debug("contextPath : " + contextPath);
		}

		//contextPath를 삭제한 URI를 allowedURISet과 비교한다.
		if(reqURI.startsWith(contextPath)) {
			reqURI = reqURI.substring(reqURI.indexOf(contextPath) + contextPath.length());
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("reqURI : " + reqURI);
		}

		//요청 URI를 request에 저장
		request.setAttribute(GoodseedConstants.REQ_URI, reqURI);
		//요청 URI를 request에 저장
		request.setAttribute(GoodseedConstants.REQ_URL, reqURL);

		//----------------------------------- 요청 URI 작업 - end ----------------------------------------------

		//----------------------------------- 클라이언트 IP 작업 - start -----------------------------------------
		//클라이언트 IP를 획득하여 request에 저장
		String ipaddr = hReq.getHeader("X-Forwarded-For");
		if(ipaddr == null || "".equals(ipaddr)) {
			ipaddr = hReq.getHeader("Proxy-Client-IP");
			if(ipaddr == null || "".equals(ipaddr)) {
				ipaddr = hReq.getRemoteAddr();
			}
		}
		//		if(LOG.isDebugEnabled()) {
		//			LOG.debug("@@ client ip address : " + ipaddr);
		//		}

		request.setAttribute(GoodseedConstants.CLIENT_IP, ipaddr);
		//----------------------------------- 클라이언트 IP 작업 - end -----------------------------------------

		//체인 넘기기
		chain.doFilter(request, response);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("## EnvironmentSetupFilter init");
		}
	}

	@Override
	public void destroy() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("## EnvironmentSetupFilter destroy");
		}
	}
}
