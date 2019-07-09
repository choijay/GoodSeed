package com.common.syscommon.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.utility.LocaleUtil;

/**
 * The class FrameOneElemTag<br>
 * <br>
 * datebox 출력<br>
 * <br>
 */
public class GoodseedMobileDateBoxTag extends TagSupport {

	private static final long serialVersionUID = -4265466748581452708L;
	private static final Log LOG = LogFactory.getLog(GoodseedMobileDateBoxTag.class);

	private String id;
	private String value;
	private String onChange;
	private String onClick;
	private String name;
	private boolean disabled;
	private String style;

	public void setValue(String value) {
		this.value = value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			StringBuffer tagStrBuffer = new StringBuffer();
			String languageCode = LocaleUtil.getUserLanguage(pageContext.getSession());

			tagStrBuffer
					.append("<input type='text' class='calendar' data-role='datebox' data-options='{&quot;useLang&quot;:&quot;"
							+ languageCode + "&quot;,&quot;mode&quot;:&quot;");

			if(!StringUtils.isBlank(this.style)) {
				if("datebox".equalsIgnoreCase(this.style)) {
					tagStrBuffer.append("datebox&quot;}'");
				} else if("flipbox".equalsIgnoreCase(this.style)) {
					tagStrBuffer.append("flipbox&quot;}'");
				} else if("calbox".equalsIgnoreCase(this.style)) {
					tagStrBuffer.append("calbox&quot;}'");
				}
			}
			if(!StringUtils.isBlank(this.id)) {
				tagStrBuffer.append("id='").append(this.id);
			}
			if(!StringUtils.isBlank(this.name)) {
				tagStrBuffer.append("' name='").append(this.name);
			}
			if(!StringUtils.isBlank(this.value)) {
				tagStrBuffer.append("' value='").append(this.value);
			}
			if(!StringUtils.isBlank(this.onChange)) {
				tagStrBuffer.append("' onChange='").append(this.onChange);
			}
			if(!StringUtils.isBlank(this.onClick)) {
				tagStrBuffer.append("' onClick='").append(this.onClick);
			}
			if(this.disabled) {
				tagStrBuffer.append("' disabled='").append(this.disabled ? "disabled" : "");
			}
			tagStrBuffer.append("' />");

			if(LOG.isDebugEnabled()) {
				LOG.debug("@@ tagStrBuffer:" + tagStrBuffer.toString());
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
}
