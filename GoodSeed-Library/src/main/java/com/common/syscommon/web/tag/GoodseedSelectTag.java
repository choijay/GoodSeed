package com.common.syscommon.web.tag;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.common.syscommon.service.CustomSelectService;

import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;
import goodseed.core.exception.UserHandleException;

/**
 * The class FrameOneCommonSelectTag<br>
 * <br>
 * selectbox 출력<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since   2015. 6. 24.
 *
 */
public class GoodseedSelectTag extends TagSupport {

	private static final long serialVersionUID = 6635661755728630543L;
	private static final Log LOG = LogFactory.getLog(GoodseedSelectTag.class);

	/**
	 * HTML 엘리먼트 id
	 */
	private String id;
	/**
	 * HTML 엘리먼트 name
	 */
	private String name;
	/**
	 * commonSelectService에 정의된 sql id
	 */
	private String sqlId;
	/**
	 * 쿼리 조회 parameter, 단건:params /다건:param1|param2|param2
	 */
	private String params;
	private String selectedValue;
	private String cssClass;
	private String cssStyle;
	private String required;
	private String onChange;
	private String attrOpts;
	private static final String CODE = "CODE";
	private static final String NAME = "NAME";

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public void setOnchange(String onChange) {
		this.onChange = onChange;
	}

	public void setAttrOpts(String attrOpts) {
		this.attrOpts = attrOpts;
	}

	@Override
	public int doEndTag() throws JspException {

		try {
			HttpSession session = pageContext.getSession();
			HtmlParameters inParams = ParametersFactory.createParameters(HtmlParameters.class);

			// 조회쿼리 id 셋팅
			inParams.setVariable("sqlId", this.sqlId);

			// 조회쿼리에 사용 할 파라메타가 있는 경우
			if(this.params != null) {
				String[] paramArr = params.split("\\|");
				for(int i = 0; i < paramArr.length; i++) {
					inParams.setVariable("param" + (i + 1), paramArr[i]);
				}
			}

			// 세션값 inParams 에 바인딩
			Enumeration e = session.getAttributeNames();
			String sessionName = null;
			String sessionValue = null;
			while(e.hasMoreElements()) {
				sessionName = StringUtils.defaultString((String)e.nextElement());
				if(!sessionName.startsWith("g_")) {
					continue; //skip
				}
				sessionValue = StringUtils.defaultString((String)session.getAttribute(sessionName));
				inParams.setVariable(sessionName, sessionValue);
			}

			// sevice bean 호출
			CustomSelectService customSelectService =
					(CustomSelectService)GoodseedContextLoaderAdapter.getBean("customSelectService");
			List<Map<String, Object>> rstSelectList = customSelectService.getSelectList(inParams);

			// select element 생성
			pageContext.getOut().write(makeSelectElement(rstSelectList).toString()); //출력

		} catch(RuntimeException ex) {
			LOG.error(ex.getMessage(), ex);
			throw ex;
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new JspTagException(ex.getMessage());
		}

		return EVAL_PAGE;
	}

	private String makeSelectElement(List<Map<String, Object>> rstSelectList) {
		StringBuffer htmlSb = new StringBuffer();
		boolean isExistAddAttr = false;
		String[] arrExtraAttr = null;

		htmlSb.append("<select");
		if(!StringUtils.isBlank(this.id)) {
			htmlSb.append(" id='").append(this.id).append("'");
		}
		if(!StringUtils.isBlank(this.name)) {
			htmlSb.append(" name='").append(this.name).append("'");
		}
		if(!StringUtils.isBlank(this.cssClass)) {
			htmlSb.append(" class='").append(this.cssClass).append("'");
		}
		if(!StringUtils.isBlank(this.cssStyle)) {
			htmlSb.append(" style='").append(this.cssStyle).append("'");
		}
		if(!StringUtils.isBlank(this.required)) {
			htmlSb.append(" required");
		}
		if(!StringUtils.isBlank(this.onChange)) {
			htmlSb.append(" onchange='").append(this.onChange).append("'");
		}
		if(!StringUtils.isBlank(this.cssStyle)) {
			htmlSb.append(" style='").append(this.cssStyle).append("'");
		}
		htmlSb.append(">");

		if(!StringUtils.isBlank(this.attrOpts)) {
			isExistAddAttr = true;
		}

		// 옵션생성 - start
		if(rstSelectList != null && rstSelectList.size() > 0) {
			boolean isAddOpts = false;
			Set<String> keys = null;
			if(!rstSelectList.get(0).containsKey(NAME) || !rstSelectList.get(0).containsKey(CODE)) {
				throw new UserHandleException("MSG_COM_SUC_999", "**** NAME, CODE IS MANDANTORY *****");
			} else {
				isAddOpts = rstSelectList.get(0).keySet().size() > 2 ? true : false;
				keys = rstSelectList.get(0).keySet();
			}
			for(Map<String, Object> selectListMap : rstSelectList) {
				htmlSb.append("<option value='").append(selectListMap.get(CODE)).append("'");
				if(isAddOpts) {
					for(String key : keys) {
						if((!NAME.equals(key) && !CODE.equals(key)) && selectListMap.get(key) != null) {
							htmlSb.append(" ").append(key).append("='").append(selectListMap.get(key)).append("'");
						}
					}
				}
				if(selectedValue != null && selectedValue.equals(selectListMap.get(CODE))) {
					htmlSb.append(" selected");
				}

				htmlSb.append("/>");
				htmlSb.append(selectListMap.get(NAME));
				htmlSb.append("</option>");
			}
		}
		// 옵션생성 - end
		htmlSb.append("</select>");

		return htmlSb.toString();
	}

}
