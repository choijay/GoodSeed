/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package com.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.utility.LoggingException2DB;
import goodseed.core.exception.SystemException;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.eni.EniUtil;
import goodseed.core.utility.i18n.NoticeMessageUtil;
import goodseed.core.utility.string.JsonUtil;
import goodseed.core.utility.string.StringUtil;
import goodseed.core.utility.web.RequestUtil;

/**
 *
 * The class HtmlErrorHandlingFilter
 *
 * @author jay
 * @version 1.0
 *
 */
public class HtmlErrorHandlingFilter extends BaseErrorHandlingFilter {

	private static final Log LOG = LogFactory.getLog(HtmlErrorHandlingFilter.class);
	private static final Log LOGGER = LogFactory.getLog("MaxRowsExcessLog");

	/**
	 * doFilter
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpRes = (HttpServletResponse)response;
		httpRes.setCharacterEncoding("UTF-8");
		String ajaxType = StringUtils.defaultString(httpReq.getHeader("AjaxType")).toLowerCase();

		try {
			chain.doFilter(request, response);

			if("raw".equals(ajaxType)) {

				String msgStsCode = (String)httpReq.getAttribute(GoodseedConstants.STATUS_MESSAGE_CODE);
				String msgStsText = (String)httpReq.getAttribute(GoodseedConstants.STATUS_MESSAGE_TEXT);

				String msgCode = (String)httpReq.getAttribute(GoodseedConstants.MESSAGE_CODE);
				String msgText = (String)httpReq.getAttribute(GoodseedConstants.MESSAGE_TEXT);

				//앨럿 메시지
				if(msgStsCode != null && msgStsCode.length() > 0) {
					RequestUtil.addCookie(httpRes, GoodseedConstants.STATUS_MESSAGE_CODE, msgStsCode);
					RequestUtil.addCookie(httpRes, GoodseedConstants.STATUS_MESSAGE_TEXT, StringUtil.encodeUrlUtf8(msgStsText));
				}
				//상태 메시지
				if(msgCode != null && msgCode.length() > 0) {
					RequestUtil.addCookie(httpRes, GoodseedConstants.MESSAGE_CODE, msgCode);
					RequestUtil.addCookie(httpRes, GoodseedConstants.MESSAGE_TEXT, StringUtil.encodeUrlUtf8(msgText));
				}

				//에러코드(성공/실패 여부)
				String errorCode = httpReq.getAttribute(GoodseedConstants.ERROR_CODE).toString();
				RequestUtil.addCookie(httpRes, GoodseedConstants.ERROR_CODE, errorCode);

			}

		} catch(Exception e) {

			LOG.error("Exception", e);

			String errorMessage = null;
			String code = null;

			Map variableMap = new HashMap();

			String uri = httpReq.getRequestURI();
			Map<String, String> params = RequestUtil.getParameterMap(httpReq);

			//requestInfo를 하나의 문자열로 만드는데, MiPlatform과는 달리 컨트롤러 메서드 단위로 호출되므로 서비스 bean 관련 로그를 넣지 않았다.
			StringBuilder sb = new StringBuilder();
			sb.append("===========================================================");
			sb.append("\nURI : ");
			sb.append(uri);
			sb.append("\nparams : ");
			sb.append(params);
			sb.append("\n===========================================================");
			String requestInfo = sb.toString();

			if(e.getCause() instanceof UserHandleException) {
				UserHandleException ue = (UserHandleException)e.getCause();
				/**
				 * UserHandleException을 throw 하더라도 SQL 오류일 경우에는 오류 테이블에 남긴다.
				 */
				if(ExceptionUtils.getRootCause(e) instanceof SQLException) {
					//rootCause가 SystemException이 아닌 SQLException으로 나옴

					String errMsg = getExceptionMessageDetail(e);
//					String excptNo = null;
//					excptNo = EniUtil.getExcptNo();
					//ENI 모듈 추가로 인해 추적성을 위해 Exception 번호를 DB에 남겨서 ENI SMS에서 발송된 exception number 를 바탕으로
					//오류 테이블에서 찾을수 있도록 한다.

					HashMap<String, String> logMap = new HashMap<String, String>();
					logMap.put("svrId", request.getLocalAddr());
					logMap.put("clntAddr", (String)request.getAttribute(GoodseedConstants.CLIENT_IP));
					logMap.put("excptCnts", requestInfo);
					logMap.put("stackTrc", errMsg);
					logMap.put("callUri", uri);
//					logMap.put("excptNo", excptNo);
					LoggingException2DB.loggingError2DB(logMap);
				}
				/**
				 * Controller에서 throw 한 UserHandleException의 displayCode 가 존재하는지 체크후
				 * 존재할 경우에는 displayMessage 를 클라이언트로 기존의 에러 코드, 메시지와 함께 전송
				 * 서버에서만 전달하며, 클라이언트에는 아직 구현 되어 있지 않음
				 *
				 * 프로젝트별로 DISPLAY_CODE, 및 MESSAGE 를 클라이언트에서 출력할지 여부를 결정하는
				 * 부가 기능을 추가함
				 */
				errorMessage = ue.getErrorMessage((Locale)request.getAttribute(GoodseedConstants.FRAMEONE_LOCALE));
				/* 사용자가 정의한 코드의 메세지 */
				variableMap.put(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_USEREXCEP);
				//				variableMap.put(GoodseedConstants.ERROR_MESSAGE_TEXT, errorMessage);
				//바인딩변수 처리가 되지 않기 때문에 아래와 같이 변경
				variableMap.put(GoodseedConstants.ERROR_MESSAGE_TEXT, errorMessage);
				variableMap.put(GoodseedConstants.ERROR_MESSAGE_CODE, ue.getErrorCode());
				if(ue.getArgs() != null) {
					variableMap.put(GoodseedConstants.BIND_MESSAGE, NoticeMessageUtil.getBindMessage(ue.getArgs()));
				}

				// display message 도 전달함
				String displayCode = ue.getDisplayCode();

				if(displayCode != null) {
					String displayMessage = ue.getDisplayMessage((Locale)request.getAttribute(GoodseedConstants.FRAMEONE_LOCALE));
					/* 사용자가 정의한 코드의 메세지 */
					variableMap.put(GoodseedConstants.DISPLAY_CODE, GoodseedConstants.ERROR_CODE_USEREXCEP);
					//				variableMap.put(GoodseedConstants.ERROR_MESSAGE_TEXT, errorMessage);
					//바인딩변수 처리가 되지 않기 때문에 아래와 같이 변경
					variableMap.put(GoodseedConstants.DISPLAY_MESSAGE_TEXT, displayMessage);
					variableMap.put(GoodseedConstants.DISPLAY_MESSAGE_CODE, ue.getDisplayCode());
					if(ue.getDisplayArgs() != null) {
						variableMap.put(GoodseedConstants.DISPLAY_BIND_MESSAGE,
								NoticeMessageUtil.getBindMessage(ue.getDisplayArgs()));
					}
				}

				//공통 ajax 스크립트에서 넘어온 경우
				if("raw".equals(ajaxType)) {
					RequestUtil.addCookie(httpRes, GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_USEREXCEP);
					RequestUtil.addCookie(httpRes, GoodseedConstants.ERROR_MESSAGE_CODE, ue.getErrorCode());
					RequestUtil.addCookie(httpRes, GoodseedConstants.ERROR_MESSAGE_TEXT, StringUtil.encodeUrlUtf8(errorMessage));
				}

				if(ue.getErrorCode().equals(Config.getString("customVariable.msgComErr021"))) {
					LOGGER.error("Max rows limit excess.\n" + requestInfo);
				}

			} else {
				e.printStackTrace();
				errorMessage =
						NoticeMessageUtil.getMessage(Config.getString("customVariable.msgComErr001"),
								(Locale)request.getAttribute(GoodseedConstants.FRAMEONE_LOCALE));

				/* System Error 메세지. */
				variableMap.put(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_SYSEXCEP);
				variableMap.put(GoodseedConstants.ERROR_MESSAGE_TEXT, errorMessage);
				variableMap.put(GoodseedConstants.ERROR_MESSAGE_CODE, Config.getString("customVariable.msgComErr001"));
				String errMsg = getExceptionMessageDetail(e);
				variableMap.put(GoodseedConstants.ERROR_DETAIL, errMsg);

				/**
				 * SystemException 일 경우에 exceptionNo 받아서 logging 에 남겨줌
				 */
				String excptNo = null;
				if(e.getCause() instanceof SystemException) {
					excptNo = ((SystemException)e.getCause()).getExceptionNo();
					if(LOG.isErrorEnabled()) {
						LOG.error("exceptionNo[" + excptNo + "] accepted.");
					}
				}

				//공통 ajax 스크립트에서 넘어온 경우
				if("raw".equals(ajaxType)) {
					RequestUtil.addCookie(httpRes, GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_SYSEXCEP);
					RequestUtil.addCookie(httpRes, GoodseedConstants.ERROR_MESSAGE_CODE,
							Config.getString("customVariable.msgComErr001"));
					RequestUtil.addCookie(httpRes, GoodseedConstants.ERROR_MESSAGE_TEXT, StringUtil.encodeUrlUtf8(errorMessage));
				}

				//DB insert 에러가 없도록 varchar2의 한계에 맞추어 substring 처리
				errMsg = StringUtil.substringByByteUTF8(errMsg, 4000);
				//DB insert 에러가 없도록 varchar2의 한계에 맞추어 substring 처리
				requestInfo = StringUtil.substringByByteUTF8(requestInfo, 4000);

				HashMap<String, String> logMap = new HashMap<String, String>();
				logMap.put("svrId", request.getLocalAddr());
				logMap.put("clntAddr", (String)request.getAttribute(GoodseedConstants.CLIENT_IP));
				logMap.put("excptCnts", requestInfo);
				logMap.put("stackTrc", errMsg);
				logMap.put("callUri", uri);
				//ENI 모듈 추가로 인해 추적성을 위해 Exception 번호를 DB에 남겨서 ENI SMS에서 발송된 exception number 를 바탕으로
				//오류 테이블에서 찾을수 있도록 한다.
				logMap.put("excptNo", excptNo);
				LoggingException2DB.loggingError2DB(logMap);

				/*
				 *	 excptNo 값이 logMap에 들어가지 않을 것 같은데 왜 이렇게 코딩이 되었는지 모르겠다. - TODO: 분석?
				 */
				variableMap.put(GoodseedConstants.BIND_MESSAGE, excptNo);
			}

			if(LOG.isErrorEnabled()) {
				LOG.error(code + ", " + errorMessage);
			}

			//프레임원 ajax 스크립트에서 넘어온 경우
			if(!StringUtils.isEmpty(ajaxType)) {
				String jsonText = null;
				try {
					jsonText = JsonUtil.marshallingJson(variableMap);
				} catch(Exception e1) {
					e1.printStackTrace();
					LOG.error("Exception", e1);
				}

				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				try {
					out.println(jsonText);
				} finally {
					out.close();
				}

				//ParametersAndView 인경우
			} else {

				request.setAttribute("ExceptionInfo", variableMap);
				HttpSession session = ((HttpServletRequest)request).getSession();
				ServletContext context = session.getServletContext();
				context.getRequestDispatcher("/common/syscommon/exception/exceptionInfo.do").forward(request, response);
			}

			// 다른 필터로 exception 을 던진다.
			//throw new ServletException(e);

		}

	}

}
