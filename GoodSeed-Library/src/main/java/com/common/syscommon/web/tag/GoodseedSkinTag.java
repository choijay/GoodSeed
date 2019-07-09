package com.common.syscommon.web.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.utility.SkinUtil;

/*
 * 스킨(테마) 이름을 리턴하는 커스텀태그<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  6. 3.
 *
 */
public class GoodseedSkinTag extends TagSupport {

	private static final long serialVersionUID = -3218443440711652409L;
	private static final Log LOG = LogFactory.getLog(GoodseedSkinTag.class);
	
	//JSTL 변수명
	private String var; 

	@Override
	public int doEndTag() throws JspException {

		try {

			String skin = SkinUtil.getJQueryUISkin((HttpServletRequest)pageContext.getRequest());
			String resolvedVar = this.var;
			if(var != null) {
				//페이지에 출력 하지 않고 JSTL 변수로 바인딩
				pageContext.setAttribute(resolvedVar,skin); 
			} else {
				//페이지에 출력
				pageContext.getOut().write(skin); 
			}

		} catch(Exception e) {
			e.printStackTrace();
			LOG.error("Exception", e);
		}

		return EVAL_PAGE;
	}

	public void setVar(String var) {
		this.var = var;
	}
}
