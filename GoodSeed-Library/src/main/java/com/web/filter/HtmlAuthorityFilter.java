/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.web.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.common.utility.AllowedURIListUtil;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;
import goodseed.core.utility.authority.AuthorityRule;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.i18n.NoticeMessageUtil;
import goodseed.core.utility.string.StringUtil;
import goodseed.core.utility.web.RequestUtil;

/**
 * 
 * The class HtmlAuthorityFilter (접근권한 체크 필터)<br>
 * <br>
 * 수행로직<br>
 * (1) 세션에서 사용자 ID를 획득한다.<br>
 * (2) 요청 URI가 권한체크 예외목록에 포함되어 있는지 검사해서<br> 
 * 		- 예외처리 URL일 경우 권한체크를 생략한다.<br>
 * 		- 예외처리 URL이 아닐 경우 사용자ID를 받는 쿼리를 실행해서 실행권한이 있는지 검사한다. 권한이 없으면 코드 수행을 중지하고 메세지페이지로 포워딩한다.<br>
 * (3) 해당 URI 프로그램의 최상위 프로그램부터 계층구조를 따라 내려오면서 각 계층별 PROG_NM(프로그램명)을 저장한 리스트를 request의 attribute에 저장한다.<br>			 
 * (4) 해당 URI 프로그램이 프로그램목록 테이블에 등록되어 있다면 접근로그를 INSERT 한다.<br>
 * 
 * @author jay
 * @version 1.0
 * 
 */
public class HtmlAuthorityFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(HtmlAuthorityFilter.class);
	private FilterConfig config;
	private static final int ZERO = 0;
	private static final String CHECKOPMODE = "WKR";
	private static final String CHECKURL = "/common/syscommon/authority/getLoginDataItg.do";
	public static final String LOGIN_SUCCESS_YN = "LOGIN_SUCCESS_YN";
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest hReq = (HttpServletRequest)request;
		HttpSession session = hReq.getSession();

		//세션에서 사용자ID, 로그인여부 획득
		AuthorityRule authorityRule = (AuthorityRule)GoodseedContextLoaderAdapter.getBean("authorityRule");
		String userId = authorityRule.getUserId(request);
		String gLang = LocaleUtil.getUserLanguage(session);

		//해당 URI 프로그램의 최상위 프로그램부터 계층구조를 따라 내려오면서 각 계층별 PRGO_CD와 PROG_NM(프로그램명)을 저장한 리스트
		List<Map<String, String>> progHierarchyList = null;
		//프로그램코드 - FRAMEONE_PROGRAM 테이블의 PK
		String progCd = null; 
		//요청 URI
		String reqURI = (String)request.getAttribute(GoodseedConstants.REQ_URI); 
		//클라이언트 IP
		String ipaddr = (String)request.getAttribute(GoodseedConstants.CLIENT_IP); 
		
		//ajax 형식 (ex: frameone, raw ... )
		String ajaxType = StringUtils.defaultString(hReq.getHeader("AjaxType")).toLowerCase().trim(); 
		//true이면 ajax 요청, false이면 페이지 요청이다.
		boolean isAjaxReq = StringUtils.isNotBlank(ajaxType); 		

		if(LOG.isDebugEnabled()) {
			LOG.debug(" reqURI : " + reqURI);
		}

		//인증이 필요 없는 메뉴일 경우 검사하지 않고 통과하도록 처리. allowedURIList.properties을 읽어들인 권한체크예외URI 목록 HashSet
		//무조건 통과 URI 목록
		Set<String> allowedURISet = AllowedURIListUtil.getAllowedURISet(); 
		//로그인한 유저에 한해서 무조건 통과 URI 목록
		Set<String> userAllowedURISet = AllowedURIListUtil.getUserAllowedURISet(); 

		//해당 메뉴의 최상위메뉴까지의 구조를 최상위로부터 각각 List의 요소로 삽입하여 request attribute로 저장		
		Map<String, String> progParamMap = new HashMap<String, String>();
		progParamMap.put("g_userId", userId);
		progParamMap.put("progURI", reqURI);
		progParamMap.put("g_lang", gLang);
		int progHierachyListSize = 0;

		SqlManager sqlManager = SqlManagerFactory.getSqlManager();
		//어플리케이션 동작모드	
		String opMode = Config.getString("operation.mode").toUpperCase(); 	
		//############################## 권한체크가 불필요한 요청 - start #################################
		if(allowedURISet.contains(reqURI)) {

			if(LOG.isDebugEnabled()) {
				LOG.debug(" 권한체크 불필요 요청");
			}

			progHierarchyList = sqlManager.queryForList(progParamMap, "htmlAuthority.selectProgramHierachyListNoAuth");

			//############################## 권한체크가 불필요한 요청 - end #################################

			//###################### 로그인한 유저에 한해서 권한체크가 불필요한 요청 - start ##########################
		} else if(userAllowedURISet.contains(reqURI)) {

			// 로컬 개발 모드 인 경우 dev01 아이디로 강제 로그인 처리
			setDevUserLoggedOn(request, response, authorityRule, authorityRule.isLogin(request), reqURI, opMode);

			//============================= 로그인 한 경우 - start =====================================
			if(authorityRule.isLogin(request)) {
				if(LOG.isDebugEnabled()) {
					LOG.debug(" 로그인한 유저에 대하여 권한체크 불필요 요청");
				}

				progHierarchyList = sqlManager.queryForList(progParamMap, "htmlAuthority.selectProgramHierachyListNoAuth");
				//============================= 로그인 한 경우 - end =====================================

				//============================= 로그인 하지 않은 경우 - start =====================================
			} else {
				if(LOG.isDebugEnabled()) {
					LOG.debug(" 로그인이 필요합니다.");
				}
				
				// 코드 : -10 => 로그인 페이지 유도
				int errorCode = GoodseedConstants.ERROR_CODE_NOAUTH;
				String svcErrMsgCd = Config.getString("customVariable.msgComErr029");
				String svcErrMsgText = NoticeMessageUtil.getMessage(Config.getString("customVariable.msgComErr029"), LocaleUtil.getUserLocale(hReq));

				// 유저아이디가 존재하지 않은 경우 최초로그인으로 간주하여 에러메세지 초기화 시킨다.
				// 예외화면으로 이동 체크시 에러메세지가 없을 경우 로그인 화면으로 자동 이동되도록 수정되어있음.
				if(("").equals(userId) && request.getParameter("progNo") == null) {
					svcErrMsgText = "";
				}

				//-------------------- ajax 요청일 경우 - start ------------------------
				if(ajaxType.length() > ZERO) {

					// FrameOne 기본 ajax 형식 (JSON String)
					if(("frameone").equals(ajaxType)) {
						processJSONMessage(response, errorCode, svcErrMsgCd, svcErrMsgText);
						//이하 코드 수행 중지
						return; 

						// raw ajax
					} else if(("raw").equals(ajaxType)) {

						processCookieMessage(response, errorCode, svcErrMsgCd, svcErrMsgText);
						//이하 코드 수행 중지
						return; 

					}
					//-------------------- ajax 요청일 경우 - end ------------------------

					//------------- 일반 페이지 요청일 경우 (non-ajax) - start -----------------
				} else {
					processPageMessage(request, response, errorCode, svcErrMsgText);
					//이하 코드 수행 중지
					return; 

				}
				//------------- 일반 페이지 요청일 경우 (non-ajax) - end -----------------				
			}
			//============================= 로그인 하지 않은 경우 - end =====================================

			//###################### 로그인한 유저에 한해서 권한체크가 불필요한 요청 - end ##########################

			//############################## 권한체크가 필요한 요청 - start #################################
		} else {

			// 로컬 개발 모드 인 경우 dev01 아이디로 강제 로그인 처리
			setDevUserLoggedOn(request, response, authorityRule, authorityRule.isLogin(request), reqURI, opMode);

			if(LOG.isDebugEnabled()) {
				LOG.debug(" 권한체크 필요한 요청");
			}

			if(LOG.isDebugEnabled()) {
				LOG.debug(" 로그인여부 : " + authorityRule.isLogin(request));
			}

			//============================= 로그인 하지 않은 경우 - start =====================================
			//로그인하지 않은 경우 - alert 이후 로그인페이지로 이동
			if(!authorityRule.isLogin(request)) { 

				if(LOG.isDebugEnabled()) {
					LOG.debug(" 로그인이 필요합니다.");
				}
				
				// 코드 : -10 => 로그인 페이지 유도
				int errorCode = GoodseedConstants.ERROR_CODE_NOAUTH;
				String svcErrMsgCd = Config.getString("customVariable.msgComErr029");
				String svcErrMsgText = NoticeMessageUtil.getMessage(Config.getString("customVariable.msgComErr029"), LocaleUtil.getUserLocale(hReq));

				//-------------------- ajax 요청일 경우 - start ------------------------
				if(ajaxType.length() > ZERO) {

					// FrameOne 기본 ajax 형식 (JSON String)
					if(("frameone").equals(ajaxType)) {
						processJSONMessage(response, errorCode, svcErrMsgCd, svcErrMsgText);
						//이하 코드 수행 중지
						return; 

						// raw ajax
					} else if(("raw").equals(ajaxType)) {
						processCookieMessage(response, errorCode, svcErrMsgCd, svcErrMsgText);
						//이하 코드 수행 중지
						return; 

					}
					//-------------------- ajax 요청일 경우 - end ------------------------

					//------------- 일반 페이지 요청일 경우 (non-ajax) - start -----------------
				} else {
					processPageMessage(request, response, errorCode, svcErrMsgText);
					//이하 코드 수행 중지
					return;

				}
				//------------- 일반 페이지 요청일 경우 (non-ajax) - end -----------------				

				//============================= 로그인 하지 않은 경우 - end =====================================

				//============================= 로그인 한 경우 - start =====================================
			} else {
				
				//해당 유저가 해당 메뉴에 권한이 있으면 메뉴계층목록을 리턴
				progHierarchyList = sqlManager.queryForList(progParamMap, "htmlAuthority.selectProgramHierachyList"); 

				//------------------------------ 해당 메뉴에 대한 권한이 없을 경우 - start ---------------------------
				if(progHierarchyList.isEmpty()) {

					if(LOG.isDebugEnabled()) {
						LOG.debug(" 해당 메뉴에 대한 권한이 없습니다.");
					}
					
					// 코드 : -1  => 뒤로가기 유도
					int errorCode = GoodseedConstants.ERROR_CODE_USEREXCEP;
					String svcErrMsgCd = Config.getString("customVariable.msgComErr030");
					String svcErrMsgText = NoticeMessageUtil.getMessage(Config.getString("customVariable.msgComErr030"), LocaleUtil.getUserLocale(hReq));

					//-------------------- ajax 요청일 경우 - start ------------------------
					if(ajaxType.length() > ZERO) {

						// FrameOne 기본 ajax 형식 (JSON String)
						if(("frameone").equals(ajaxType)) {
							processJSONMessage(response, errorCode, svcErrMsgCd, svcErrMsgText);
							//이하 코드 수행 중지
							return; 

							// raw ajax
						} else if(("raw").equals(ajaxType)) {

							processCookieMessage(response, errorCode, svcErrMsgCd, svcErrMsgText);
							//이하 코드 수행 중지
							return; 

						}
						//-------------------- ajax 요청일 경우 - end ------------------------

						//------------- 일반 페이지 요청일 경우 (non-ajax) - start -----------------
					} else {
						processPageMessage(request, response, errorCode, svcErrMsgText);
						//이하 코드 수행 중지
						return; 

					}
					//------------- 일반 페이지 요청일 경우 (non-ajax) - end -----------------

					//------------------------------ 해당 메뉴에 대한 권한이 없을 경우 - end ---------------------------

					//------------------------------ 해당 메뉴에 대한 권한이 있을 경우 - start ---------------------------
				}
				//else {

				//일반적인 케이스 - 로그인 상태이고 해당URL의 메뉴 권한도 있다.
				//}
				//------------------------------ 해당 메뉴에 대한 권한이 있을 경우 - end ---------------------------

			}
			//============================= 로그인 한 경우 - start =====================================

		}
		//############################## 권한체크가 필요한 요청 - end #################################

		if((progHierarchyList != null) && (!progHierarchyList.isEmpty())) {
				progHierachyListSize = progHierarchyList.size();
			}
		
		if(progHierachyListSize > ZERO) {

			//프로그램코드를 request에 저장
			//마지막 row의 PROG_CD가 프로그램코드이다.
			progCd = progHierarchyList.get(progHierachyListSize - 1).get("PROG_CD"); 
			request.setAttribute(GoodseedConstants.PROG_CD, progCd);
			if(LOG.isDebugEnabled()) {
				LOG.debug(" 현재 요청에 대한 프로그램 코드 : " + progCd);
			}

			//프로그램명 계층 리스트를 request에 저장
			List<String> progNmHierarchyList = new ArrayList<String>(progHierachyListSize);
			for(Map<String, String> map : progHierarchyList) {
				progNmHierarchyList.add(map.get("PROG_NM"));
			}
			request.setAttribute(GoodseedConstants.PROG_HIERARCHY_LIST, progNmHierarchyList);

		}
		
		/**
		 * 2014.06 추가 => 프로그램 버튼 권한 정보 Setting(커스텀 태그의 사용성 이슈로 인한 변경)
		 * 페이지 요청일 경우
		 */
		if(!isAjaxReq) { 
			if(progCd != null && !("").equals(progCd)) {
				progParamMap.put("prog_cd", progCd);

				List<Map<String, String>> progBtnAuthList = sqlManager.queryForList(progParamMap, "menu.getHTMLButtonPerm");
				request.setAttribute(GoodseedConstants.PROG_BTNAUTH_LIST, progBtnAuthList);

				if(LOG.isDebugEnabled()) {
					LOG.debug(" 현재  프로그램에 대한 버튼 권한 : " + progBtnAuthList);
				}	
			}		
		}	

		//progCd가 존재할 경우에만 로그를 남긴다. (메뉴에 등록된 URI만)
		if(progCd != null && !("").equals(progCd)) {
			Map<String, String> logParamMap = new ConcurrentHashMap<String, String>();
			logParamMap.put("progCd", progCd);
			logParamMap.put("g_userId", userId);
			logParamMap.put("rmk", "clickMenuByUser");
			logParamMap.put("logCl", "3");
			logParamMap.put("ipaddr", ipaddr);
			//sqlManager.insert(logParamMap, "authority.insertUserMenuClickLog");
		}
		
		//sqlManager를 해제해 주지 않으면 다음 필터체인까지 물고있을 가능성이 있다.
		sqlManager = null; 

		//체인 넘기기
		chain.doFilter(request, response);
	}

	/**
	 * SessionLocaleResolver를 사용하여 현재 로케일을 획득한다.
	 * 
	 * @param request
	 * @return locale 로케일
	 */
	private Locale getLocale(HttpServletRequest hReq) {

		WebApplicationContext webApplicationContext =
				WebApplicationContextUtils.getWebApplicationContext(config.getServletContext(), FrameworkServlet.SERVLET_CONTEXT_PREFIX + "dispatcher");
		//에러메세지 출력시 국제화 적용을 위한 변수
		SessionLocaleResolver sessionLocaleResolver = null; 
		if(webApplicationContext == null) {
			//log.debug(" webApplicationContext is null");
			sessionLocaleResolver = new SessionLocaleResolver();
		} else {
			//log.debug(" webApplicationContext is not null");
			sessionLocaleResolver = (SessionLocaleResolver)webApplicationContext.getBean("sessionLocaleResolver");
		}

		Locale locale = sessionLocaleResolver.resolveLocale(hReq);
		if(LOG.isDebugEnabled()) {
			LOG.debug(" locale : " + locale);
		}

		return locale;
	}

	/**
	 * 에러메세지와 함께 에러페이지로 forward (일반 요청 : non-ajax 일 경우)
	 * 
	 * @param request
	 * @param response
	 * @param errorCode
	 * @param svcErrMsgText
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processPageMessage(ServletRequest request, ServletResponse response, int errorCode, String svcErrMsgText) throws ServletException, IOException {
		
		//클라이언트로 리턴할 에러정보		
		Map<String, Object> exceptionInfo = new ConcurrentHashMap<String, Object>(); 
		//TODO:exceptionInfo.jsp 분기처리 확인할 것
		String forwardURL = "/common/syscommon/exception/exceptionInfo.do"; 
		// 에러메세지 텍스트가 존재할 경우에만 예외페이지로 이동 및 에러메세지 정보를 가진다.
		//if(svcErrMsgText.equals("")) {
		//	errorCode = 0;
		//}
		if(("").equals(svcErrMsgText)) {
			exceptionInfo.put(GoodseedConstants.ERROR_CODE, errorCode);
		} else {
			exceptionInfo.put(GoodseedConstants.ERROR_CODE, 0);
		}
		exceptionInfo.put(GoodseedConstants.ERROR_MESSAGE_TEXT, svcErrMsgText);
		//TODO:어떤 식으로 클라이언트에 값을 전달할 것인지 고민해 보자
		request.setAttribute("ExceptionInfo", exceptionInfo); 
		//TODO:실패했을 경우 포워딩하는 페이지 만들어지면 수정할 것
		request.getRequestDispatcher(forwardURL).forward(request, response); 
	}

	/**
	 * 	에러메세지를 cookie로 처리 (raw type ajax 일 경우)
	 * 
	 * @param response
	 * @param errorCode
	 * @param svcErrMsgCd
	 * @param svcErrMsgText
	 */
	private void processCookieMessage(ServletResponse response, int errorCode, String svcErrMsgCd, String svcErrMsgText) {

		HttpServletResponse hRes = (HttpServletResponse)response;

		// 에러메세지텍스트가 없을 경우 쿠키에 에러정보를 기록하지 않음.
		//if(svcErrMsgText.isEmpty()) {
		//	errorCode = 0;
		//}
		if(svcErrMsgText.isEmpty()) {
			RequestUtil.addCookie(hRes, GoodseedConstants.ERROR_CODE, errorCode);
		} else {
			RequestUtil.addCookie(hRes, GoodseedConstants.ERROR_CODE, 0);
		}
		RequestUtil.addCookie(hRes, GoodseedConstants.ERROR_MESSAGE_CODE, svcErrMsgCd);
		RequestUtil.addCookie(hRes, GoodseedConstants.ERROR_MESSAGE_TEXT, StringUtil.encodeUrlUtf8(svcErrMsgText));
	}

	/**
	 * 	에러메세지를 JSON 응답으로 처리 (FrameOne 표준 ajax 형식일 경우)
	 * 
	 * @param response
	 * @param errorCode
	 * @param svcErrMsgCd
	 * @param svcErrMsgText
	 * @throws IOException
	 */
	private void processJSONMessage(ServletResponse response, int errorCode, String svcErrMsgCd, String svcErrMsgText) {

		//예시 - {"ErrorCode":"-10","SVC_ERR_MSG_CD":"MSG_COM_ERR_029","SVC_ERR_MSG_TEXT":"로그인이 필요한 서비스입니다. 로그인 해 주십시오."}
		StringBuilder json =
				new StringBuilder("{\"").append(GoodseedConstants.ERROR_CODE).append("\":\"").append(errorCode).append("\",\"")
						.append(GoodseedConstants.ERROR_MESSAGE_CODE).append("\":\"").append(svcErrMsgCd).append("\",\"")
						.append(GoodseedConstants.ERROR_MESSAGE_TEXT).append("\":\"").append(svcErrMsgText).append("\"}");

		if(LOG.isDebugEnabled()) {
			LOG.debug(" json : " + json.toString());
		}

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");

		Writer out = null;
		try {
			out = response.getWriter();
			out.write(json.toString());
			out.flush();
		} catch(IOException e) {
			e.printStackTrace();
			LOG.error("Exception", e);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch(Exception e) {
					e.printStackTrace();
					LOG.error("Exception", e);
				}
			}
		}
	}

	/**
	 * 	로컬 개발환경에서 개발의 편의를 위해서 특정 사용자로 로그인 이후 세션정보 처리를 해 주는 부분<br> 
	 * 
	 * @param request
	 * @param authorityRule
	 * @param isLogin
	 * @param reqURI
	 * @param opMode
	 */
	private void setDevUserLoggedOn(ServletRequest request, ServletResponse response, AuthorityRule authorityRule, boolean isLogin, String reqURI, String opMode) {

		if(CHECKURL.equals(reqURI) && CHECKOPMODE.equals(opMode) && !isLogin) {
			if(LOG.isDebugEnabled()) {
				LOG.debug(" 로컬 개발환경을 위한 테스트유저 강제 로그인 실행");
			}
			//로컬 자동 로그인에 사용할 userId
			String userId = "dev01"; 
			Parameters params = ParametersFactory.createParameters(HtmlParameters.class);
			params.setVariable("userId", userId);
			authorityRule.processAfterLoginSuccess((HttpServletRequest)request, (HttpServletResponse)response, params, "N");
			//AuthorityController에서 두번 로그를 남기지 않기 위해서 플래그를 넣어준다.
			request.setAttribute(LOGIN_SUCCESS_YN, "Y");
		}
	}

	public void init(FilterConfig config) throws ServletException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("HtmlAuthorityFilter init()");
		}

		this.config = config;
	}

	public void destroy() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("HtmlAuthorityFilter destroy()");
		}
	}

}
