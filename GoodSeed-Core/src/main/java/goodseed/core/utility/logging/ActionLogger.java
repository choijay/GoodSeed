/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.logging;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.utility.CharSetUtil;
import goodseed.core.utility.web.URLUtil;

/**
 * The class ActionLogger<br>
 * <br>
 * request 정보를 로깅하기 위해 만든 클래스<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 *
 */
public class ActionLogger {

	private static final Log LOG = LogFactory.getLog(ActionLogger.class);

	private static final String DEFAULT_CHARACTER_SET = CharSetUtil.getDefaultCharSet();

	/**
	 * request 정보를 logging.<br>
	 *<br>
	 * @param request HttpServletRequest
	 */
	public static void requestLogger(HttpServletRequest request) {
		if(!LOG.isInfoEnabled()) {
			return;
		}

		/* 사용할 local 변수 선언 */
		//String employeeNumber = null;

		//		String requestIP = request.getRemoteHost();
		String requestIP = (String)request.getAttribute(GoodseedConstants.CLIENT_IP);

		String requestURL = request.getRequestURL().toString();
		String paramKeyValue = null;
		StringBuilder logMessage = new StringBuilder();

		/* 로그인 유져의 employee number 값을 가져온다. */

		/* parameter 값이 있을때 paramKeyValue에 값 세팅 */
		if(StringUtils.isNotEmpty(getParameterKeyValue(request))) {
			paramKeyValue = getParameterKeyValue(request);
		} else {
			paramKeyValue = "none";
		}

		/* log 메세지를 만든다 */
		//logMessage.append("[UserId : ").append(employeeNumber).append("]");
		logMessage.append("[IP : ").append(requestIP).append("]");
		logMessage.append("[URL : ").append(requestURL).append("]");
		logMessage.append("[Request Param : ").append(paramKeyValue).append("]");

		LOG.info(logMessage.toString());
	}

	/**
	 * request 에서 parameter key, value 를 {key=value} 의 형태로 반환한다.<br/>
	 * default character set is 'UTF-8'<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @return {key=value} 의 형태 string
	 */
	private static String getParameterKeyValue(HttpServletRequest request) {
		return getParameterKeyValue(request, DEFAULT_CHARACTER_SET);
	}

	/**
	 * request 에서 parameter key, value 를 {key=value} 의 형태로 반환한다.<br/>
	 * <br>
	 * @param request HttpServletRequest
	 * @param charset the character set
	 * @return {key=value} 의 형태 string
	 */
	@SuppressWarnings("unchecked")
	private static String getParameterKeyValue(HttpServletRequest request, String charset) {
		return URLUtil.getParameterKeyValue(request, charset);
	}
}
