/**
 *
 * The class ImgButtonsTag
 *
 * @author jay
 * @version 1.0
 *
 */
package com.common.syscommon.web.tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.utility.LabelUtil;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.utility.string.StringUtil;

/**
 * 권한에 맞는 버튼을 생성
 * 
 * @author jay
 * @since  03. 23.
 */
public class GoodseedCommonButtonsTag extends TagSupport {

	private static final long serialVersionUID = -734008226099019074L;
	private static final Log LOG = LogFactory.getLog(GoodseedCommonButtonsTag.class);

	//권한버튼 id정의
	private static final String[] COMM_BTN_ID = {"SELBTNUSE", "NEWBTNUSE", "DELBTNUSE", "SAVBTNUSE", "PRTBTNUSE"};

	// 프로그램 코드
	private String pgCode;
	// 공통 버튼 Display 여부 
	private String showCommBtn;
	// 사용자 버튼 Display 여부 
	private String showUserBtn;
	//사용자 정의 버튼에 class 부여
	private String cssClass;
	// 모바일 여부
	private boolean mobileYn;

	public void setPgCode(String pgCode) {
		this.pgCode = pgCode;
	}

	public void setShowCommBtn(String showCommBtn) {
		this.showCommBtn = showCommBtn;
	}

	public void setShowUserBtn(String showUserBtn) {
		this.showUserBtn = showUserBtn;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setMobileYn(boolean mobileYn) {
		this.mobileYn = mobileYn;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		HttpSession session = pageContext.getSession();
		Locale currentLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		if(LOG.isDebugEnabled()) {
			LOG.debug("currentLocale:" + currentLocale);
		}

		// 공통버튼 Display 여부
		String resolvedShowCommBtn = StringUtil.defaultString(this.showCommBtn);
		String[] arrShowCommBtn = resolvedShowCommBtn.split(",");

		// 사용자정의버튼 Display 여부
		String resolvedShowUserBtn = StringUtil.defaultString(this.showUserBtn);
		String[] arrShowUserBtn = resolvedShowUserBtn.split(",");

		// HTML 메뉴 정보 조회
		Parameters inParams = ParametersFactory.createParameters(HtmlParameters.class);

		// 사용자ID 
		inParams.setVariable("g_userId", session.getAttribute("g_userId"));

		//pgCode = (String)pageContext.getRequest().getAttribute(GoodseedConstants.PROG_CD);

		// 프로그램 코드
		inParams.setVariable("prog_cd", pgCode);

		List<Map<String, String>> progBtnAuthList =
				(List<Map<String, String>>)pageContext.getRequest().getAttribute(GoodseedConstants.PROG_BTNAUTH_LIST);

		// 버튼정보 List 생성
		List<Map<String, String>> btnList = new ArrayList<Map<String, String>>();
		Map<String, String> btnMap = new HashMap<String, String>();

		// 버튼을 생성한다.
		StringBuilder html = new StringBuilder();

		if(progBtnAuthList != null && progBtnAuthList.size() > 0) {

			//--------------------------------------------------------------------------------------//
			// 1. 공통버튼 (조회, 신규, 삭제, 저장, 인쇄) List 생성 
			//--------------------------------------------------------------------------------------//
			// 버튼권한 여부  - 공통, 사용자정의 10개. 0  - false, 1 - true
			String btnPrmYn = "0";
			int btnIndex = 0;

			for(int i = 0; i < arrShowCommBtn.length; i++) {
				btnIndex = i;
				// 버튼권한 여부.
				btnPrmYn = StringUtils.defaultString(progBtnAuthList.get(0).get(COMM_BTN_ID[btnIndex] + "YN"));

				if("1".equals(arrShowCommBtn[btnIndex])) {
					btnMap.put("ID", COMM_BTN_ID[btnIndex]);
					btnMap.put("PERM_YN", btnPrmYn);
					btnList.add(btnMap);

					makeImageBtnString(html, btnPrmYn, COMM_BTN_ID[btnIndex], mobileYn);
				}
				// end for(공통 버튼)
			}

			//--------------------------------------------------------------------------------------//
			// 2. 사용자 정의 버튼  List 생성 
			//--------------------------------------------------------------------------------------//
			// 사용자 정의 버튼 Lable ID 
			String btnUserLblId;
			String btnPrmNm;
			// 공통버튼 Display 여부
			String cssClassNm = StringUtil.defaultString(this.cssClass);

			for(int i = 0; i < arrShowUserBtn.length; i++) {
				// 버튼권한 여부. 
				btnPrmYn = StringUtils.defaultString(progBtnAuthList.get(0).get("BTN" + (i + 1) + "USEYN"));
				btnPrmNm = StringUtils.defaultString(progBtnAuthList.get(0).get("BTN" + (i + 1) + "NM"));
				if("1".equals(arrShowUserBtn[i])) {
					if(progBtnAuthList.get(0).get("BTN" + (i + 1) + "NM") != null) {
						btnUserLblId = progBtnAuthList.get(0).get("BTN" + (i + 1) + "NM");
					} else {
						btnUserLblId = "";
					}
					btnMap.put("ID", "USER" + (i + 1));
					btnMap.put("PERM_YN", btnPrmYn);
					btnList.add(btnMap);

					makeBtnString(html, btnPrmYn, ("USER" + (i + 1)), btnPrmNm, cssClassNm, mobileYn);
				}
			}
			// end for(사용자 정의 버튼)
		}

		// getHTMLMenuList

		try {
			pageContext.getOut().print(html.toString());
		} catch(IOException e) {
			e.printStackTrace();
			LOG.error("Exception", e);
		}

		return super.doEndTag();
	}

	/**
	 * 버튼을 생성한다.
	 * @param htmlString  : html string
	 * @param btnPerm : 권한에 따라 버튼 사용유무를 설정한다(메뉴  권한)(1:사용, 0:미사용)
	 * @param btnId   : 버튼 ID 
	 * @return
	 */
	private void makeImageBtnString(StringBuilder htmlString, String btnPerm, String btnId, boolean mobileYn) {

		//함수명을 맞추어주기 위하여 ID를 변경한다.
		if("SELBTNUSE".equals(btnId)) {
			btnId = "search";
		} else if("NEWBTNUSE".equals(btnId)) {
			btnId = "new";
		} else if("DELBTNUSE".equals(btnId)) {
			btnId = "delete";
		} else if("SAVBTNUSE".equals(btnId)) {
			btnId = "save";
		} else if("PRTBTNUSE".equals(btnId)) {
			btnId = "print";
		}

		// 버튼 클릭시 실행될 함수명
		String btnFunc = btnId + "ButtonOnClick(this)";
		String languageCode = LocaleUtil.getUserLanguage(pageContext.getSession());

		// 버튼 권한이 없으면 종료
		if("0".equals(btnPerm)) {
			return;
		}

		String btnText = "", btnIconClass = "";

		if("search".equals(btnId)) {
			btnText = LabelUtil.getLabelProperty("BUTTON.SEARCH", languageCode);
			btnIconClass = "btn_basic btn_search";
		} else if("new".equals(btnId)) {
			btnText = LabelUtil.getLabelProperty("BUTTON.NEW", languageCode);
			btnIconClass = "btn_basic btn_new";
		} else if("delete".equals(btnId)) {
			btnText = LabelUtil.getLabelProperty("BUTTON.DELETE", languageCode);
			btnIconClass = "btn_basic btn_delete";
		} else if("save".equals(btnId)) {
			btnText = LabelUtil.getLabelProperty("BUTTON.SAVE", languageCode);
			btnIconClass = "btn_basic btn_save";
		} else if("print".equals(btnId)) {
			btnText = LabelUtil.getLabelProperty("BUTTON.PRINT", languageCode);
			btnIconClass = "btn_basic btn_print";
		}

		if(mobileYn) {
			// 실행 함수명에서 this 제거
			btnFunc = btnId + "ButtonOnClick()";
			htmlString.append("<a class='" + btnIconClass + "' href='javascript:" + btnFunc + ";'>" + btnText + "</a>");
		} else {
			htmlString.append("<button id=foBtn" + btnId + " alt='" + btnText + "' class='" + btnIconClass
					+ "' style='text-align:right; ' onClick='javascript:" + btnFunc + "'>" + btnText + "</button>");
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("### defalut commonBtn : " + htmlString);
			LOG.debug("### defalut commonBtn Id : " + btnId);
		}

		return;
	}

	/**
	 * 버튼을 생성한다.
	 * @param htmlString  : html string
	 * @param btnPerm : 권한에 따라 버튼 사용유무를 설정한다(메뉴  권한)(1:사용, 0:미사용)
	 * @param btnId   : 버튼 ID 
	 * @param btnAlt  : Alt 로 보여줄  버튼명
	 * @return
	 */
	private void makeBtnString(StringBuilder htmlString, String btnPerm, String btnId, String btnAlt, String cssClassNm,
			boolean mobileYn) {
		// 버튼 클릭시 실행될 함수명
		String btnFunc = btnId.toLowerCase() + "ButtonOnClick(this)";

		// 버튼 권한이 없으면 종료
		if("0".equals(btnPerm)) {
			return;
		}

		/*사용자 정의 버튼에 글자 길이를 구함 */
		int btnTextLength = 0;
		try {
			btnTextLength = btnAlt.getBytes("euc-kr").length;
		} catch(UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("Exception", e);
		}

		/*사용자 정의 버튼 글자 길이에 따라서 클래서 동적으로 지정
		String classNm = "comm_userbtn20";

		if(btnTextLength <= 4) {
			classNm = "comm_userbtn6";
		} else if(btnTextLength <= 6) {
			classNm = "comm_userbtn8";
		} else if(btnTextLength <= 8) {
			classNm = "comm_userbtn10";
		} else if(btnTextLength <= 10) {
			classNm = "comm_userbtn12";
		} else if(btnTextLength <= 12) {
			classNm = "comm_userbtn14";
		} else if(btnTextLength <= 14) {
			classNm = "comm_userbtn16";
		} else if(btnTextLength <= 16) {
			classNm = "comm_userbtn18";
		}*/

		/*		htmlString
						.append("<input type=\"button\" id=BTN_" + btnId + " value='" + btnAlt + "' class=\"" + classNm + "\" alt='"
								+ btnAlt).append("' style='margin-left:5px;' onclick='javascript:" + btnFunc + "' >").append("</input>");
		*/

		if(mobileYn) {
			htmlString.append("<a class='btn_save' href='javascript:" + btnFunc + ";'>" + btnAlt + "</a>");
		} else {
			htmlString.append("<button id=foBtn" + btnId + " alt='" + btnAlt + "' class='btn_basic' onClick='javascript:"
					+ btnFunc + "'>" + btnAlt + "</button>");
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("## user btnSize : " + btnTextLength);
			LOG.debug("## user classNm : " + cssClassNm);
			LOG.debug("## user commonBtn : " + htmlString);
		}

		return;
	}

	/**
	 * 이미지의 onclick에 사용할 스크립트를 생성한다.
	 * @param script
	 * @return
	 */
	/*
	private StringBuilder makeScript(String script) {
		StringBuilder html = new StringBuilder();
		html.append(script).append("();");
		return html;
	}*/

}
