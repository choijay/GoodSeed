package goodseed.core.utility.authority;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;

/**
 * The interface AuthorityRule<br>
 * 사용자 인증정보 관련 Interface
 * <br>
 * @author jay
 *
 */
public interface AuthorityRule {

	/**
	 * 로그인 여부 조회<br>
	 * <br>
	 * @param request
	 * @return 로그인 되어 있으면 true 리턴, 아니면 false
	 */
	boolean isLogin(HttpServletRequest request);

	/**
	 * 로그인 여부 조회 overloading<br>
	 */
	boolean isLogin(ServletRequest request);

	/**
	 * 로그인 한 사용자의 ID를 리턴.<br>
	 * <br>
	 * @return 로그인한 사용자ID
	 */
	String getUserId(HttpServletRequest request);

	/**
	 * 로그인 한 사용자의 ID를 리턴. overloading
	 */
	String getUserId(ServletRequest request);

	/**
	 * 사용자 정보 조회 <br>
	 * <br>
	 * @ahthor KimByungWook
	 * @since 4. 24.
	 *
	 * @param userId
	 * @return
	 */
	HtmlParameters getUserInfo(String userId);

	/**
	 * 로그인 성공시 수행하는 작업들<br>
	 * 		- 사용자 ID 저장 쿠키 생성<br>
	 * 		- 각종 사용자정보 세션에 바인딩 <br>
	 * <br>
	 * @param request
	 * @param response
	 * @param inParams
	 * @param isSaveUserId
	 */
	void processAfterLoginSuccess(HttpServletRequest request, HttpServletResponse response, Parameters inParams, String isSaveUserId);
}
