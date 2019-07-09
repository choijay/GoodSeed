/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.observer.ConfigCheck;
import goodseed.core.observer.ConfigObserver;
import goodseed.core.utility.config.Config;

/**
 * The Class RequestUtil.<br>
 * <br>
 * @author jay
 * @version 1.0
 */
//@ConfigCheck
public class RequestUtil implements ConfigObserver {

	private static final Log LOG = LogFactory.getLog(RequestUtil.class);

//	/**
//	 * 웹서버에서 현재 요청의 SSL여부를 전달하기 위해 설정하는 헤더의 이름<br>
//	 */
//	private static String sslHeaderName;
//
//	/**
//	 * 	웹서버에서 현재 요청의 SSL여부를 전달하기 위해 설정하는 헤더의 값<br>
//	 */
//	private static String sslHeaderValue;
//	
//	/**
//	 * 정적 초기화 블럭<br>
//	 */
//	static {
//		setSSLHeaderConstants();
//	}

	/**
	 * request에 들어있는 모든 파라미터들을 Map으로 리턴한다.<br>
	 * request.getParamterMap() 메서드는 Map의 value로서 String[]을 리턴하기 때문에<br>
	 * 사용하기 어려운 면이 있어서 메서드를 별도로 작성하였다.<br>
	 * <br>
	 * @param request
	 * @return Map형태로 결과를 리턴
	 */
	public static Map<String, String> getParameterMap(HttpServletRequest request) {
		Map<String, String> paramMap = new HashMap<String, String>();
		Enumeration<String> en = request.getParameterNames();
		while(en.hasMoreElements()) {
			String paramName = (String)en.nextElement();
			String paramValue = request.getParameter(paramName);
			paramMap.put(paramName, paramValue);
		}
		return paramMap;
	}

	//==================== 현재 요청이 SSL요청인지를 판단하는 부분 - start ======================
	

//	/**
//	 *	 sslHeaderName, sslHeaderValue의 값을 프로퍼티 파일로부터 읽어와서 바인딩한다. <br>
//	 */
//	private static void setSSLHeaderConstants() {
//		sslHeaderName = Config.getString("sslCondition.name");
//		sslHeaderValue = Config.getString("sslCondition.value");
//	}

	/**
	 *	현재 요청이 SSL요청인지 여부를 판단하여 리턴한다.<br>
	 * 프로퍼티 파일(ex: applicationProperties.xml)의 &lt;sslCondition&gt; 엘리먼트 참조<br>  	
	 * <br>
	 * @param request
	 * @return SSL 요청여부의 True or False
	 */
	public static boolean isSSL(HttpServletRequest request) {
		boolean ret = request.isSecure();
		return ret;
	}

	//==================== 현재 요청이 SSL요청인지를 판단하는 부분 - end =======================

	/**
	 * 	간단한 세션쿠키를 생성하는 메서드<br>
	 * <br>
	 * @param httpRes HttpServletResponse
	 * @param cookieKey 쿠키키값
	 * @param cookieValue 쿠키키에 넣을 값
	 */
	public static void addCookie(HttpServletResponse httpRes, String cookieKey, Object cookieValue) {
		Cookie cookie = new Cookie(cookieKey, String.valueOf(cookieValue));
		cookie.setPath("/");
		httpRes.addCookie(cookie);
	}

	/**
	 * applicationProperties 내 환경변수가 변경되었을 때 변수 업데이트
	 */
	@Override
	public void update() {
//		setSSLHeaderConstants();
//		if(LOG.isDebugEnabled()) {
//			LOG.debug(" sslHeaderName : " + this.sslHeaderName);
//			LOG.debug(" sslHeaderValue : " + this.sslHeaderValue);
//		}
	}
}
