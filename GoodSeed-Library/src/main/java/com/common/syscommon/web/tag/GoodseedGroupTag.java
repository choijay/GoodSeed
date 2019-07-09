package com.common.syscommon.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class FrameOneButtonTag<br>
 * <br>
 * button 출력<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since   2015. 7. 10.
 *
 */
public class GoodseedGroupTag extends TagSupport {

	private static final long serialVersionUID = 3277356789848991239L;

	private static final Log LOG = LogFactory.getLog(GoodseedGroupTag.class);

	private String type;
	private String name;

	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer tagStrBuffer = new StringBuffer();
			tagStrBuffer.append("<div id='");
			tagStrBuffer.append(this.name);

			if("checkbox".equalsIgnoreCase(this.type)) {
				tagStrBuffer.append("' class='foChkboxGroup'>");
			} else if("radio".equalsIgnoreCase(this.type)) {
				tagStrBuffer.append("' class='foRadioGroup'>");
			}

			pageContext.getOut().write(tagStrBuffer.toString());

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
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().write("</div>");
		} catch(RuntimeException ex) {
			LOG.error(ex.getMessage(), ex);
			throw ex;
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new JspTagException(ex.getMessage());
		}
		return EVAL_PAGE;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
