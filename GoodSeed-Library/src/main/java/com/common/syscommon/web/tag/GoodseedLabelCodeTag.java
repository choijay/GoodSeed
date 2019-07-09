package com.common.syscommon.web.tag;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.utility.LabelUtil;
import goodseed.core.common.utility.LocaleUtil;

/**
 * The class FrameOneCommonCodeTag<br>
 * <br>
 * 해당 라벨코드의 라벨명을 출력<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 16.
 *
 */
public class GoodseedLabelCodeTag extends TagSupport {

	private static final long serialVersionUID = -5217620223656943568L;
	private static final Log LOG = LogFactory.getLog(GoodseedLabelCodeTag.class);

	private String code; //코드
	/**
	 * 	현재페이지 기준 참조 여부 FLAG<br>
	 * <br>
	 * true - 현재 요청URI의 패키지구조를 기준으로 메세지를 찾는다.(기본값)<br>
	 * (ex) 요청URI가 /common/templateGrid/listSample.fo 인 요청에서, 메세지키 common.templateGrid.listSample.lbHobby를 찾을 경우 code에 "lbHobby"만 입력하면 됨.<br>
	 * <br>
	 * false - 현재 JSP페이지의 패키지구조에 관계없이 찾고자 하는 리소스번들의 키를 전부 입력<br>
	 * (ex) frameone.templateGrid.listSample.lbHobby를 찾을 경우 code에 "frameone.templateGrid.listSample.lbHobby" 입력.<br>
	 */
	private boolean relative = true;

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Set relative
	 * 
	 * @param relative 요청URI 기반 상대참조 여부
	 */
	public void setRelative(boolean relative) {
		this.relative = relative;
	}

	@Override
	public int doEndTag() throws JspException {

		try {

			String resolvedCd =  this.code;
			ServletRequest req = (ServletRequest)pageContext.getRequest();
			String reqURI = (String)req.getAttribute(GoodseedConstants.REQ_URI);
			String packageStr = reqURI.substring(reqURI.indexOf('/') + 1, reqURI.lastIndexOf('.') + 1).replaceAll("/", ".");
			if(relative) {
				resolvedCd = packageStr + resolvedCd;
			}

			String languageCode = LocaleUtil.getUserLanguage(pageContext.getSession());
			String codeProperty = LabelUtil.getLabelProperty(resolvedCd, languageCode);
			pageContext.getOut().write(codeProperty);

		} catch(RuntimeException ex) {
			LOG.error(ex.getMessage(), ex);
			throw ex;
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new JspTagException(ex.getMessage());
		}

		return EVAL_PAGE;
	}

	@Override
	public void release() {
		this.code = null;
		this.relative = true;
		super.release();
	}
	
	
}
