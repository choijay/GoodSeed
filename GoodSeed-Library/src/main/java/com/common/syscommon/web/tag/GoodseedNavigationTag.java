package com.common.syscommon.web.tag;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConstants;

/**
 * The class FrameOneNavigationTag<br>
 * <br>
 * 현재 프로그램의 계층구조를 출력한다.<br>
 * <br>
 * (예) FrameOne >> 템플릿 >> 업무디자인 >> 프로그램전체정보조회
 *
 * @author jay
 * @version 1.0
 * @since  4. 17.
 *
 */
public class GoodseedNavigationTag extends TagSupport {

	private static final long serialVersionUID = 5682923119845102628L;
	private static final Log LOG = LogFactory.getLog(GoodseedNavigationTag.class);

	//어떤 html 태그로 감쌀 것인지
	private String tagName;
	//프로그램 계층간 구분자
	//	private String delim; 
	private String cssClass;
	private String cssStyle;

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/*public void setDelim(String delim) {
		this.delim = delim;
	}*/

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	@Override
	public int doEndTag() throws JspException {

		try {

			//프로그램 계층구조 리스트 - HtmlAuthorityFilter 에서 설정해 준 값이다.
			List<String> progNmHierarchyList =
					(List<String>)pageContext.getRequest().getAttribute(GoodseedConstants.PROG_HIERARCHY_LIST);

			if(progNmHierarchyList != null && progNmHierarchyList.size() > 0) {

				//String resolvedDelim = this.delim;
				String resolvedTagName = this.tagName;
				String resolvedCssClass = this.cssClass;
				String resolvedCssStyle = this.cssStyle;

				StringBuilder html = new StringBuilder("<").append(resolvedTagName);
				if(StringUtils.isNotBlank(resolvedCssClass)) {
					html.append(" class=\"").append(resolvedCssClass).append("\" ");
				}
				if(StringUtils.isNotBlank(resolvedCssStyle)) {
					html.append(" style=\"").append(resolvedCssStyle).append("\" ");
				}
				html.append(" >");

				int listSize = progNmHierarchyList.size();
				html.append(progNmHierarchyList.get(listSize - 1));
				html.append("</").append(resolvedTagName).append(">");
				html.append("<").append(resolvedTagName);
				html.append(" class=\"_navigationList\" style=\"display:none\">");
				for(int i = 0; i < listSize; i++) {
					html.append("<span>"+progNmHierarchyList.get(i)+"</span>");
					if(i < (listSize - 1)) {
						html.append(" > ");
					}
				}
				html.append("</").append(resolvedTagName).append(">");

				pageContext.getOut().write(html.toString());

			}
			//else {
			//프로그램 계층구조 리스트가 request의 속성에 존재하지 않을 경우
			//}

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
