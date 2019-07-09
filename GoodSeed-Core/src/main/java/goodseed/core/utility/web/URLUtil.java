/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * The class URLUtil<br>
 * <br>
 * Request 에서 이전 페이지에 대한 BackURL/BackURI 를 생성하여 주소와 파라미터를 Map으로 리턴하는 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2011 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 1.0
 *
 */
public class URLUtil {

	/**
	 * 복호화 할 때 사용할 UTF-8 문자열
	 */
	public static final String UTF_8 = "UTF-8";

	private static final Log LOG = LogFactory.getLog(URLUtil.class);

	/**
	 * 
	 * 이전 페이지 url 주소를 주소와 파라미터(key,value)로 구분하여 HashMap형태로 리턴한다<br>
	 * 이 때, 파라미터value는 URLDecoder를 사용하여 캐릭터 셋 UTF-8 복호화한다.<br>
	 * <br>
	 * @param backURL 이전 페이지 url
	 * @return
	 * @ahthor KimJiHye
	 */
	public static HashMap<String, Object> getKeyValues(String backURL) {
		return getKeyValues(backURL, UTF_8);
	}

	/**
	 * 
	 * 이전 페이지 url 주소를 주소와 파라미터(key,value)로 구분하여 HashMap형태로 리턴한다.<br>
	 * 이 때, 파라미터value는 URLDecoder를 사용하여 입력받은 캐릭터 셋으로 복호화한다.<br>
	 * <br>
	 * @param backURL 이전 페이지 url
	 * @param charset 파라미터들을 URLDecoder를 통해 복호화 할 때 사용할 캐릭터 셋
	 * @return
	 * @ahthor KimJiHye
	 */
	public static HashMap<String, Object> getKeyValues(String backURL, String charset) {

		HashMap<String, Object> keyValues = new HashMap<String, Object>();

		String[] urlParams = StringUtils.splitPreserveAllTokens(backURL, "?");

		if(urlParams != null && urlParams.length == 2) {

			keyValues.put("http_request_back_url_key", urlParams[0]);

			String[] params = StringUtils.splitPreserveAllTokens(urlParams[1], "&");

			if(params != null && params.length > 0) {
				for(int i = 0; i < params.length; i++) {
					String[] keyValue = StringUtils.splitPreserveAllTokens(params[i], "=");
					if(keyValue != null && keyValue.length == 2) {
						try {
							keyValues.put(keyValue[0], URLDecoder.decode(keyValue[1], charset));
						} catch(Exception ex) {
							LOG.error("exception", ex);
							continue;
						}
					}
				}
			}

		}

		return keyValues;

	}

	/**
	 * 
	 * Request 에서 이전 페이지에 대한 BackURL 을 생성한다.<br>
	 * URL 과 request parameter 모두를 붙여서 생성한다. UTF-8 인코딩.<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @return BackURL
	 * @ahthor KimJiHye
	 */
	public static String makeBackURL(HttpServletRequest request) {
		return makeBackURL(request, UTF_8);
	}

	/**
	 * 
	 * Request 에서 이전 페이지에 대한 BackURI 를 생성한다.<br>
	 * URI 와 request parameter 모두를 붙여서 생성한다. UTF-8 인코딩.<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @return BackURI
	 * @ahthor KimJiHye
	 */
	public static String makeBackURI(HttpServletRequest request) {
		return makeBackURI(request, UTF_8);
	}

	/**
	 * 
	 * Request 에서 parameter key, value 값을 String의 형태로 반환한다.<br>
	 * {key=value} 의 형태의 String. UTF-8 인코딩<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @return
	 * @ahthor KimJiHye
	 */
	public static String getParameterKeyValue(HttpServletRequest request) {
		return getParameterKeyValue(request, UTF_8);
	}

	/**
	 * Request 에서 이전 페이지에 대한 BackURL 을 생성한다.<br/>
	 * URL 과 request parameter 모두를 붙여서 생성한다.<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @param charset the charset
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	public static String makeBackURL(HttpServletRequest request, String charset) {
		String requestURI = request.getRequestURI();

		String tempURL = "http://" + request.getServerName() + ":" + request.getServerPort() + requestURI;

		StringBuilder backURL = new StringBuilder(tempURL);
		Enumeration enumer = request.getParameterNames();

		if(enumer.hasMoreElements()) {
			backURL.append("?");
		}

		String paramKey = null;
		while(enumer.hasMoreElements()) {
			paramKey = (String)enumer.nextElement();

			String[] paramValues = request.getParameterValues(paramKey);
			if(paramValues == null || paramValues.length == 0) {
				paramValues = new String[0];
			}

			try {
				if(paramValues.length > 1) {
					for(int index = 0; index < paramValues.length; index++) {
						backURL.append(paramKey).append("=").append(URLEncoder.encode(paramValues[index], charset)).append("&");
					}
				} else {
					backURL.append(paramKey).append("=").append(URLEncoder.encode(request.getParameter(paramKey), charset))
							.append("&");
				}
			} catch(UnsupportedEncodingException e) {
				LOG.error("exception", e);
				e.printStackTrace();
			}
		}

		String retStr = backURL.toString();

		if("&".equals(retStr.substring(retStr.length() - 1, retStr.length()))) {
			retStr = retStr.substring(0, retStr.lastIndexOf('&'));
		}
		return retStr;
	}

	/**
	 * Request 에서 이전 페이지에 대한 BackURI 을 생성한다.<br/>
	 * URI 과 request parameter 모두를 붙여서 생성한다.<br>
	 * @param request HttpServletRequest
	 * @param charset the charset
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	public static String makeBackURI(HttpServletRequest request, String charset) {
		String requestURI = request.getRequestURI();

		StringBuilder backURI = new StringBuilder(requestURI);
		Enumeration enumer = request.getParameterNames();

		if(enumer.hasMoreElements()) {
			backURI.append("?");
		}

		String paramKey = null;
		while(enumer.hasMoreElements()) {
			paramKey = (String)enumer.nextElement();

			String[] paramValues = request.getParameterValues(paramKey);
			if(paramValues == null || paramValues.length == 0) {
				paramValues = new String[0];
			}

			try {
				if(paramValues.length > 1) {
					for(int index = 0; index < paramValues.length; index++) {
						backURI.append(paramKey).append("=").append(URLEncoder.encode(paramValues[index], charset)).append("&");
					}
				} else {
					backURI.append(paramKey).append("=").append(URLEncoder.encode(request.getParameter(paramKey), charset))
							.append("&");
				}
			} catch(UnsupportedEncodingException e) {
				LOG.error("exception", e);
				e.printStackTrace();
			}
		}

		String retStr = backURI.toString();

		if("&".equals(retStr.substring(retStr.length() - 1, retStr.length()))) {
			retStr = retStr.substring(0, retStr.lastIndexOf('&'));
		}
		return retStr;
	}

	/**
	 * Context 에서 정의한 context-param 의 값을 가져온다.<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @param key key value
	 * @return Context 에서 정의한 context-param 의 값 
	 */
	public static String getInitParam(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		ServletContext context = null;
		String value = "";
		if(session != null) {
			context = session.getServletContext();
		}

		if(context != null) {
			value = context.getInitParameter(key);
		}

		return value == null ? "" : value;

	}

	/**
	 * Request 에서 parameter key, value 값을 String의 형태로 반환한다.<br/>
	 * {key=value} 의 형태의 String.<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @param charset the character set
	 * @return parameter key, value 값을 String의 형태로 반환한 {key=value}형태의 String
	 */
	@SuppressWarnings("unchecked")
	public static String getParameterKeyValue(HttpServletRequest request, String charset) {
		StringBuilder returnValue = new StringBuilder();

		Enumeration enumer = request.getParameterNames();

		String paramKey = null;
		while(enumer.hasMoreElements()) {
			paramKey = (String)enumer.nextElement();

			String[] paramValues = request.getParameterValues(paramKey);
			if(paramValues == null || paramValues.length == 0) {
				paramValues = new String[0];
			}

			try {
				if(paramValues.length > 1) {
					for(int index = 0; index < paramValues.length; index++) {
						returnValue.append("{").append(paramKey).append("=")
								.append(URLEncoder.encode(paramValues[index], charset)).append("},");
					}
				} else {
					returnValue.append("{").append(paramKey).append("=")
							.append(URLEncoder.encode(request.getParameter(paramKey), charset)).append("},");
				}
			} catch(UnsupportedEncodingException e) {
				LOG.error("exception", e);
				e.printStackTrace();
			}
		}

		/* 마지막의 ',' 을 제거한다. */
		String returnString = returnValue.toString();
		if(StringUtils.isNotEmpty(returnString) && ",".equals(returnString.substring(returnString.length() - 1))) {
			returnString = returnString.substring(0, returnString.lastIndexOf(','));
		}

		return returnString;
	}

}
