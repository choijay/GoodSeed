package com.common.syscommon.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.utility.string.StringUtil;

/**
 * The class FrameOneElemTag<br>
 * <br>
 * button 출력<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since   2015. 7. 10.
 * 
 */
public class GoodseedButtonTag extends TagSupport {

	private static final long serialVersionUID = -4265466748581452708L;
	private static final Log LOG = LogFactory.getLog(GoodseedButtonTag.class);

	private String id;
	private String value;
	private String label;
	private String onChange;
	private String onClick;
	private String type;
	private String name;
	private boolean disabled;
	private String className;

	public void setValue(String value) {
		this.value = value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			/** 부모태그가 있는경우는 group, 없는 경우는 단독태그 구성 */
			GoodseedGroupTag foBtntag = (GoodseedGroupTag)this.getParent();
			StringBuffer tagStrBuffer = new StringBuffer();

			String resolvedType = foBtntag == null ? this.type : foBtntag.getType();
			String resolvedName = foBtntag == null ? this.name : foBtntag.getName();

			tagStrBuffer.append("<input type='");
			tagStrBuffer.append(resolvedType.toLowerCase());
			tagStrBuffer.append("' ");

			/** 단독태그일 경우 처리 */
			if(foBtntag == null) {
				if("checkbox".equalsIgnoreCase(resolvedType)) {
					tagStrBuffer.append("class='foChkboxBtn' ");
				} else if("radio".equalsIgnoreCase(resolvedType)) {
					tagStrBuffer.append("class='foRadioBtn' ");
				} else if("button".equalsIgnoreCase(resolvedType)) {
					/** 2015-09-24 shks 버튼 클래스 추가 */
					tagStrBuffer.append("class='" + StringUtil.defaultString(this.className, "btn_basic") + "' ");
				}
			}
			if(!StringUtils.isBlank(this.id)) {
				tagStrBuffer.append("id='").append(this.id);
			}
			if(!StringUtils.isBlank(resolvedName)) {
				tagStrBuffer.append("' name='").append(resolvedName);
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

			if(!"button".equals(resolvedType)) {
				tagStrBuffer.append("<label for='").append(this.id).append("'>").append(this.label).append("</label>");
			}

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
