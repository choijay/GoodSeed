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
 * The class FrameOneGroupCodeTag<br>
 * <br>
 * 그룹코드명을 조회하여 출력<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 16.
 *
 */
public class GoodseedGroupCodeTag extends TagSupport {

	private static final long serialVersionUID = 8177649061516755187L;
	private static final Log LOG = LogFactory.getLog(GoodseedGroupCodeTag.class);

	private String clCd;
	//그룹코드테이블에서 코드명이 아닌 다른 값으로 노출하려 할 경우 지정. CodeUtil의 GRPCDCOL_계열 상수를 사용한다.	
	private String groupCodeColumnName; 
	private String var;
	private String scope = TagUtils.SCOPE_PAGE;

	public void setClCd(String clCd) {
		this.clCd = clCd;
	}

	public void setGroupCodeColumnName(String groupCodeColumnName) {
		this.groupCodeColumnName = groupCodeColumnName;
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
			String languageCode = LocaleUtil.getUserLanguage(pageContext.getSession());
			String resolvedClCd = this.clCd;
			//groupCodeColumnName에 별도의 값을 입력하지 않았을 경우, HTML에 노출할 기본값은 "코드명" 이다.
			String resolvedGroupCodeColumnName =
					StringUtils.defaultString(this.groupCodeColumnName, CodeUtil.GRPCDCOL_COMM_CL_CD_NM); 			

			//String groupCodeProperty = CodeUtil.getGroupCodeProperty(resolvedClCd, resolvedGroupCodeColumnName);
			//			System.out.println("@@ groupCodeProperty : " + groupCodeProperty);

			String groupCodeProperty = CodeUtil.getGroupCodeProperty(resolvedClCd, languageCode, resolvedGroupCodeColumnName);

			// Expose as variable, if demanded, else write to the page.
			String resolvedVar = this.var;
			if(resolvedVar != null) { 
				//JSTL변수로 출력
				String resolvedScope = this.scope;
				pageContext.setAttribute(resolvedVar, groupCodeProperty, TagUtils.getScope(resolvedScope));
			} else { 
				//JSP에 출력
				pageContext.getOut().write(String.valueOf(groupCodeProperty));
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
