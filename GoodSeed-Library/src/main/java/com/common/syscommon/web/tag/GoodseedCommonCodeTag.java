package com.common.syscommon.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.TagUtils;

import com.common.syscommon.utility.CodeUtil;

import goodseed.core.common.utility.LocaleUtil;

/**
 * The class FrameOneCommonCodeTag<br>
 * <br>
 * 해당 공통코드의 코드정보를 출력<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 16.
 *
 */
public class GoodseedCommonCodeTag extends TagSupport {

	private static final long serialVersionUID = -5217620223656943568L;
	private static final Log LOG = LogFactory.getLog(GoodseedCommonCodeTag.class);
	
	//그룹코드
	private String clCd; 
	//코드
	private String cd; 
	//코드테이블에서 코드명이 아닌 다른 값으로 노출하려 할 경우 지정. CodeUtil의 CDCOL_계열 상수를 사용한다.
	private String codeColumnName; 
	//JSTL 변수명
	private String var; 
	//변수의 유효범위
	private String scope = TagUtils.SCOPE_PAGE; 

	public void setClCd(String clCd) {
		this.clCd = clCd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public void setCodeColumnName(String codeColumnName) {
		this.codeColumnName = codeColumnName;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public int doEndTag() throws JspException {

		try {

			String resolvedClCd = this.clCd;
			String resolvedCd = this.cd;
			//codeColumnName에 별도의 값을 입력하지 않았을 경우, HTML에 노출할 기본값은 "코드명" 이다.
			String resolvedCodeColumnName =
					StringUtils.defaultString(this.codeColumnName,CodeUtil.CDCOL_COMM_CD_NM); 
			String resolvedVar = this.var;
			String languageCode = LocaleUtil.getUserLanguage(pageContext.getSession());
			String codeProperty = CodeUtil.getCodeProperty(resolvedClCd, resolvedCd, languageCode, resolvedCodeColumnName);
			
			//JSTL변수로 출력
			if(resolvedVar != null) {
				String resolvedScope = this.scope;
				pageContext.setAttribute(resolvedVar, codeProperty, TagUtils.getScope(resolvedScope));
			} else {
				//JSP에 출력
				pageContext.getOut().write(codeProperty);
			}

		} catch(RuntimeException ex) {
			LOG.error(ex.getMessage(), ex);
			throw ex;
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new JspTagException(ex.getMessage());
		}

		return EVAL_PAGE;
	}
}
