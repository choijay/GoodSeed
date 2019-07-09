package com.common.utility;

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.common.service.HtmlAuthorityService;

import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.utility.authority.AuthorityRule;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.web.RequestUtil;

/**
 * The class AuthorityUtil<br>
 * <br>
 * 권한 체크 관련 작업을 수행하는 클래스<br>
 * <br>
 * spring에서 singleton bean으로 관리하며<br>
 * 모든 로그인 상태 관련 판별에 사용된다.<br>
 * frameone-core 의 권한필터(HtmlAuthorityFilter)에서도 사용된다.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 13.
 *
 */
public class AuthorityRuleImpl implements AuthorityRule {

	private static final Log LOG = LogFactory.getLog(AuthorityRuleImpl.class);

	@Autowired
	HtmlAuthorityService authorityService;

	/**
	 * 로그인 여부 조회<br>
	 * <br>
	 * @param request
	 * @return 로그인 되어 있으면 true 리턴, 아니면 false
	 */
	public boolean isLogin(HttpServletRequest request) {

		boolean isLogin = false;

		HttpSession session = request.getSession(false);
		if(session == null) {
			return false;
		}

		if(session.getAttribute("g_userId") != null) {
			isLogin = true;
		}

		//		System.out.println("@@ isLogin : " + isLogin);
		return isLogin;
	}

	/**
	 * 로그인여부 조회 overloading
	 */
	public boolean isLogin(ServletRequest request) {
		return isLogin(castHttpServletRequest(request));
	}

	/**
	 * 로그인 한 사용자의 ID를 리턴.<br>
	 * <br>
	 *
	 * @return 로그인한 사용자ID
	 */
	public String getUserId(HttpServletRequest request) {

		String userId = null;
		Object userIdObj = null;

		HttpSession session = request.getSession(false);
		if(session == null) {
			return "";
		}

		userIdObj = session.getAttribute("g_userId");
		if(userIdObj == null) {
			return "";
		}

		userId = (String)userIdObj;
		return userId;
	}

	public HtmlParameters getUserInfo(String userId) {
		HtmlParameters inParams = new HtmlParameters();
		inParams.setVariable("userId", userId);

		return (HtmlParameters)authorityService.getUserData(inParams);
	}

	/**
	 * 로그인 한 사용자의 ID를 리턴. overloading
	 */
	public String getUserId(ServletRequest request) {
		return getUserId(castHttpServletRequest(request));
	}

	/**
	 * request를 형변환하여 리턴
	 * 
	 * @param request
	 * @return HttpServletRequest 객체
	 */
	private HttpServletRequest castHttpServletRequest(ServletRequest request) {
		return (HttpServletRequest)request;
	}

	/**
	 * 로그인 성공시 수행하는 작업들
	 * 		- 사용자 ID 저장 쿠키 생성
	 * 		- 각종 사용자정보 세션에 바인딩 
	 * 
	 * @param request
	 * @param response
	 * @param inParams
	 * @param isSaveUserId
	 */
	public void processAfterLoginSuccess(HttpServletRequest request, HttpServletResponse response, Parameters inParams,
			String isSaveUserId) {

		if("Y".equals(isSaveUserId)) {
			RequestUtil.addCookie(response, "savedUserId", inParams.getVariableAsString("userId"));
		} else {
			RequestUtil.addCookie(response, "savedUserId", "");
		}

		HttpSession session = request.getSession();

		Parameters userInfoOutParams = (Parameters)authorityService.getUserData(inParams);

		String gLoginDt = userInfoOutParams.getVariableAsString("g_loginDt");
		String gUserId = userInfoOutParams.getVariableAsString("g_userId");
		String gUserNm = userInfoOutParams.getVariableAsString("g_userNm");

		String gUseStsCd = userInfoOutParams.getVariableAsString("g_useStsCd");
		String gRegDt = userInfoOutParams.getVariableAsString("g_regDt");
		String gUpdDt = userInfoOutParams.getVariableAsString("g_updDt");
		String gStartSysCl = userInfoOutParams.getVariableAsString("g_startSysCl");
		String gEmpNo = userInfoOutParams.getVariableAsString("g_empNo");
		String gMobNo = userInfoOutParams.getVariableAsString("g_mobNo");
		String gTelNo = userInfoOutParams.getVariableAsString("g_telNo");
		String gEmailAddr = userInfoOutParams.getVariableAsString("g_emailAddr");
		String gComCd = userInfoOutParams.getVariableAsString("g_comCd");
		String gDeptCd = userInfoOutParams.getVariableAsString("g_deptCd");
		String gDeptNm = userInfoOutParams.getVariableAsString("g_deptNm");
		String gSessionTimeDiff = userInfoOutParams.getVariableAsString("g_sessionTimeDiff");
		String gMultiLoginCheck = userInfoOutParams.getVariableAsString("g_multiLoginCheck");
		String gIpAddr = userInfoOutParams.getVariableAsString("g_ipAddr");

		session.setAttribute("g_userId", gUserId);
		session.setAttribute("g_userNm", gUserNm);
		session.setAttribute("g_loginDt", gLoginDt);
		session.setAttribute("imdgPath", Config.getString("imdg.contextPath"));

		session.setAttribute("g_useStsCd", gUseStsCd);
		session.setAttribute("g_regDt", gRegDt);
		session.setAttribute("g_updDt", gUpdDt);
		session.setAttribute("g_startSysCl", gStartSysCl);
		session.setAttribute("g_empNo", gEmpNo);
		session.setAttribute("g_mobNo", gMobNo);
		session.setAttribute("g_telNo", gTelNo);
		session.setAttribute("g_emailAddr", gEmailAddr);
		session.setAttribute("g_comCd", gComCd);
		session.setAttribute("g_deptCd", gDeptCd);
		session.setAttribute("g_deptNm", gDeptNm);
		session.setAttribute("g_sessionTimeDiff", gSessionTimeDiff);
		session.setAttribute("g_multiLoginCheck", gMultiLoginCheck);
		session.setAttribute("g_ipAddr", gIpAddr);

		//====================== 세션로케일 설정 - start ========================
		//로그인 페이지의 언어 선택 콤보박스에서 받은 ISO 639 2-letter 문자열을 사용하여 세션로케일을 셋팅한다.
//		String locale2Letter = inParams.getVariableAsString("language");
//		Locale locale = LocaleUtil.getLocaleBy2Letter(locale2Letter);
		Locale locale = LocaleUtil.getDefaultLocale();
		//세션로케일 설정
		LocaleUtil.setUserLocale(session, locale); 
		if(LOG.isDebugEnabled()) {
			LOG.debug("## processAfterLoginSuccess() -  세션로케일 설정");
//			LOG.debug("## locale2Letter: " + locale2Letter);
			LOG.debug("## locale: " + locale);
		}
		//====================== 세션로케일 설정 - end =========================

		// TODO 세션종료시간 지정하는 부분 존재여부 확인후 제거요망.
		//session.setMaxInactiveInterval(3);
	}
}
