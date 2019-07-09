package com.common.syscommon.web.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.TagUtils;

import com.common.syscommon.utility.CodeUtil;

import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.exception.UserHandleException;

/**
 * The class FrameOneCommonCodeListTag<br>
 * <br>
 * 해당 그룹코드의 코드목록을 여러가지 형태로 출력<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 16.
 *
 */
public class GoodseedCommonCodeListTag extends TagSupport {

	private static final long serialVersionUID = 8177649061516755187L;
	private static final Log LOG = LogFactory.getLog(GoodseedCommonCodeListTag.class);

	/**
	 * 	공통코드 목록의 HTML 출력 형태 선택용 상수
	 */
	//콤보박스 : <select/>
	private static final String OUT_TYPE_SELECT = "select"; 
	//라디오박스 : <input type="radio"/>
	private static final String OUT_TYPE_RADIO = "radio"; 
	//체크박스 : <input type="checkbox"/>
	private static final String OUT_TYPE_CHECK = "checkbox"; 
	//jqGrid에서 정의한 콤보박스 등을 구성하기 위한 문자열 데이터형식 = "01:부가세포함;02:부가세별도"
	private static final String OUT_TYPE_JQGRID = "jqGrid"; 
	//JSTL 변수
	private static final String OUT_TYPE_VAR = "var"; 
	
	//그룹코드
	private String clCd; 
	//코드테이블에서 코드명이 아닌 다른 값을 HTML 노출값으로 사용하려 할 경우 지정. CodeUtil의 CDCOL_계열 상수를 사용한다.
	private String codeColumnName; 
	//출력형식
	private String outType; 
	//HTML 엘리먼트의 name 속성
	private String elementName; 
	//콤보박스 생성시 선택 기본값 이름 (예: "선택", "전체" etc...)
	private String selectDefaultName; 
	//콤보박스 생성시 선택 기본값 (예: "", "*" etc...)
	private String selectDefaultValue; 
	//콤보박스 생성시 다중선택 콤보박스로 하고 싶은 경우 multiple="multiple" 로 입력함.
	private String multiple; 
	//콤보, 라디오, 체크박스의 리스트에서 최초에 선택된 상태로 있어야 하는 값의 정의. 콤보와 체크박스의 경우 다중선택이 가능하므로 콤마(,)로 구분된 문자열이나 EL로 표현된 리스트성 객체를 받을 수 있다.
	private Object selectedValue; 
	//HTML CSS class
	private String cssClass; 
	//HTML inline style code
	private String cssStyle; 
	//라디오, 체크박스에 각각 붙는 label 엘리먼트에 대한 HTML CSS class
	private String labelCssClass; 
	//라디오, 체크박스에 각각 붙는 label 엘리먼트에 대한 HTML inline style code
	private String labelCssStyle; 
	//JSTL 변수명
	private String var; 
	//변수의 유효범위
	private String scope = TagUtils.SCOPE_PAGE; 
	//필수입력 체크를 위한 사용자정의 attribute (true/false)
	private String required; 
	//title attribute
	private String title; 
	//onchange attribute
	private String onchange; 
	// 칼럼ID (html 필드 이벤트 바인딩시 그리드 데이터셋으로 데이터 이동시 필요)
	private String colId; 

	public void setClCd(String clCd) {
		this.clCd = clCd;
	}

	public void setCodeColumnName(String codeColumnName) {
		this.codeColumnName = codeColumnName;
	}

	public void setOutType(String outType) {
		this.outType = outType;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public void setSelectDefaultName(String selectDefaultName) {
		this.selectDefaultName = selectDefaultName;
	}

	public void setSelectDefaultValue(String selectDefaultValue) {
		this.selectDefaultValue = selectDefaultValue;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public void setSelectedValue(Object selectedValue) {
		this.selectedValue = selectedValue;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public void setLabelCssClass(String labelCssClass) {
		this.labelCssClass = labelCssClass;
	}

	public void setLabelCssStyle(String labelCssStyle) {
		this.labelCssStyle = labelCssStyle;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	@Override
	public int doEndTag() throws JspException {

		try {

			//Map<String, Object> codeListMap = CodeUtil.getCodeListMap(clCd); 
			//해당 그룹코드의 코드목록 Map
			Map<String, Map<String, Object>> codeListMap = CodeUtil.getCodeListMap(clCd); 

			String resolvedOutType = this.outType;
			String resolvedVar = this.var;
			//codeColumnName에 별도의 값을 입력하지 않았을 경우, HTML에 노출할 기본값은 "코드명" 이다.
			String resolvedCodeColumnName =
					StringUtils.defaultString(this.codeColumnName,
							CodeUtil.CDCOL_COMM_CD_NM); 

			String languageCode = LocaleUtil.getUserLanguage(pageContext.getSession());

			StringBuilder html = null;
			Set<Entry<String, Map<String, Object>>> codeListSet = codeListMap.entrySet();
			int codeListSetSize = codeListSet.size();
			//			System.out.println("@@ codeListSet's size : " + codeListSetSize);
			
			//JSTL 변수로 출력
			if(resolvedOutType.equals(OUT_TYPE_VAR)) { 

				if(StringUtils.isBlank(resolvedVar)) {
					throw new UserHandleException("MSG_COM_VAL_036");
				}

				//해당 그룹코드의 코드목록을 Map의 List로 만들어 var에 박아준다.
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

				String code = null;
				String codeValue = null;
				Map<String, Object> outputMap = null;

				for(Entry<String, Map<String, Object>> e : codeListSet) {
					code = e.getKey();
					codeValue = CodeUtil.getCodeProperty(clCd, code, languageCode, resolvedCodeColumnName);

					outputMap = new HashMap<String, Object>();
					outputMap.put("CODE", code);
					outputMap.put("NAME", codeValue);
					list.add(outputMap);
				}

				String resolvedScope = this.scope;
				pageContext.setAttribute(resolvedVar, list, TagUtils.getScope(resolvedScope));

			} else { 
				//HTML 엘리먼트로 출력
				String resolvedElementName =this.elementName;
				String resolvedCssClass =  this.cssClass;
				String resolvedCssStyle = this.cssStyle;
				String resolvedLabelCssClass =this.labelCssClass;
				String resolvedLabelCssStyle =this.labelCssStyle;
				String resolvedOnChange = this.onchange;
				String resolvedOnColId = this.colId;

				Object[] resolvedSelectedValue = resolveArguments(selectedValue);
				//사용자가 입력한 selectedValue들을 List의 형태로 얻는다.
				List selectedValueList = resolvedSelectedValue == null ? new ArrayList() : Arrays.asList(resolvedSelectedValue); 
				
				//콤보박스(select박스)
				if(resolvedOutType.equals(OUT_TYPE_SELECT)) { 
					
					//다중선택 콤보박스 여부
					String resolvedMultiple =
							StringUtils.defaultString(this.multiple); 
					String multipleProperty = "multiple".equals(resolvedMultiple) ? "multiple=\"multiple\"" : "";

					//필수입력 체크 attribute 추가 
					String resolvedRequired =
							StringUtils.defaultString(this.required);
					String requiredProperty = "true".equals(resolvedRequired) ? " required " : "";

					//title attribute 
					String titleProperty =
							" title=\""
									+ StringUtils.defaultString(this.title) + "\" ";
					html =
							createElement("<select ", resolvedOutType, "", resolvedElementName, resolvedCssClass,
									resolvedCssStyle, resolvedOnChange, resolvedOnColId).append(multipleProperty)
									.append(requiredProperty).append(titleProperty).append(">");

					//콤보박스 기본값이 정의되었을 경우 option 만들어 삽입
					String resolvedSelectDefaultName = this.selectDefaultName;
					String resolvedSelectDefaultValue =
							StringUtils.defaultString(this.selectDefaultValue);
					
					//공통코드 항목이외에 option에 바인딩 할 attribute
					boolean isExistAddAttr = false;
					String[] arrExtraAttr = null; 
					
					if(StringUtils.isNotBlank(resolvedSelectDefaultName)) {
						html.append("<option value=\"").append(StringUtils.defaultString(resolvedSelectDefaultValue))
								.append("\">").append(resolvedSelectDefaultName).append("</option>");
					}

					for(Entry<String, Map<String, Object>> e : codeListSet) {
						String cd = e.getKey();
						String cdValue = CodeUtil.getCodeProperty(clCd, cd, languageCode, resolvedCodeColumnName);
						html.append("<option value=\"").append(cd).append("\" ");
						if(isExistAddAttr && arrExtraAttr!=null){
							for(int i=0; i<arrExtraAttr.length; i++){
								String addAttr = CodeUtil.getCodeProperty(clCd, cd, languageCode, arrExtraAttr[i]);
								if(addAttr!=null){
									html.append(" ")
									.append(arrExtraAttr[i])
									.append("=\"").append(addAttr).append("\"");
								}
							}
						}
						if(selectedValueList.contains(cd)) {
							//사용자가 입력한 selectedValue와 일치하면 선택된 상태로 처리
							html.append("selected=\"selected\""); 
						}
						html.append(" >").append(cdValue).append("</option>");
					}

					html.append("</select>");
					
					//라디오박스
				} else if(resolvedOutType.equals(OUT_TYPE_RADIO)) { 

					String inputType = "radio";
					html =
							createRadioOrCheckbox(resolvedOutType, resolvedCodeColumnName, codeListSet, resolvedElementName,
									resolvedCssClass, resolvedCssStyle, resolvedLabelCssClass, resolvedLabelCssStyle,
									selectedValueList, inputType, languageCode);
					
					//체크박스
				} else if(resolvedOutType.equals(OUT_TYPE_CHECK)) { 

					String inputType = "checkbox";
					html =
							createRadioOrCheckbox(resolvedOutType, resolvedCodeColumnName, codeListSet, resolvedElementName,
									resolvedCssClass, resolvedCssStyle, resolvedLabelCssClass, resolvedLabelCssStyle,
									selectedValueList, inputType, languageCode);
					//jqGrid
				} else if(resolvedOutType.equals(OUT_TYPE_JQGRID)) { 

					html = new StringBuilder();

					//콤보박스 기본값이 정의되었을 경우 option 만들어 삽입
					//jqGrid가 받아들이는 값은 "코드값:코드명;코드값:코드명;..." 의 형태이다.
					String resolvedSelectDefaultName = this.selectDefaultName;
					String resolvedSelectDefaultValue =
							StringUtils.defaultString(this.selectDefaultValue);
					if(StringUtils.isNotBlank(resolvedSelectDefaultName)) {
						html.append(resolvedSelectDefaultValue).append(":").append(resolvedSelectDefaultName).append(";");
					}

					int idx = 0;
					for(Entry<String, Map<String, Object>> e : codeListSet) {

						String cd = e.getKey();
						String cdValue = CodeUtil.getCodeProperty(clCd, cd, languageCode, resolvedCodeColumnName);
						html.append(cd).append(":").append(cdValue);
						if(idx < codeListSetSize - 1) {
							html.append(";");
						}
						idx++;
					}

				} else {
					
					//유효하지 않은 outType
					throw new UserHandleException("MSG_COM_VAL_037"); 
				}

				//				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				//				System.out.println(html.toString());
				//				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				//출력
				pageContext.getOut().write(html.toString()); 
			}

		} catch(JspException ex) {
			LOG.error(ex.getMessage(), ex);
			throw ex;
		} catch(RuntimeException ex) {
			LOG.error(ex.getMessage(), ex);
			throw ex;
		} catch(Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new JspTagException(ex.getMessage());
		}

		return EVAL_PAGE;
	}

	/**
	 * 코드리스트를 사용하여 라디오버튼이나 체크박스를 생성한다.
	 * 
	 * @param resolvedOutType
	 * @param resolvedCodeColumnName
	 * @param codeListSet
	 * @param resolvedElementName
	 * @param resolvedCssClass
	 * @param resolvedCssStyle
	 * @param resolvedLabelCssClass
	 * @param resolvedLabelCssStyle
	 * @param selectedValueList
	 * @param tagName
	 * @return
	 * @throws JspException
	 */
	private StringBuilder createRadioOrCheckbox(String resolvedOutType, String resolvedCodeColumnName,
			Set<Entry<String, Map<String, Object>>> codeListSet, String resolvedElementName, String resolvedCssClass,
			String resolvedCssStyle, String resolvedLabelCssClass, String resolvedLabelCssStyle, List selectedValueList,
			String tagName, String languageCode) throws JspException {

		StringBuilder html;
		html = new StringBuilder();
		for(Entry<String, Map<String, Object>> e : codeListSet) {
			String cd = e.getKey();
			String cdValue = CodeUtil.getCodeProperty(clCd, cd, languageCode, resolvedCodeColumnName);
			//라디오나 체크박스일 경우 label과 맵핑해 주기 위한 id를 셋팅하는데, "elementName_공통코드" 형식으로 만든다.
			String elementId = new StringBuilder(StringUtils.defaultString(elementName)).append("_").append(cd).toString();
			String labelString = cdValue;
			html.append(
					createElement(new StringBuilder("<input type=\"").append(tagName).append("\" ").toString(), resolvedOutType,
							elementId, resolvedElementName, resolvedCssClass, resolvedCssStyle, "", "")).append(" value=\"")
					.append(cd).append("\" ");
			if(selectedValueList.contains(cd)) {
				//사용자가 입력한 selectedValue와 일치하면 선택된 상태로 처리
				html.append(" checked=\"checked\" "); 
			}
			html.append(" />");
			
			//label 엘리먼트 붙이기
			attachLabelElement(html, resolvedLabelCssClass, resolvedLabelCssStyle, elementId, labelString); 
		}
		return html;
	}

	/**
	 * 	라디오, 체크박스를 위한 라벨을 생성하고 스타일정보가 존재하면 삽입한다.
	 * 
	 * @param html
	 * @param labelCssClass
	 * @param labelCssStyle
	 * @param elementId
	 * @param labelString
	 */
	private void attachLabelElement(StringBuilder html, String labelCssClass, String labelCssStyle, String elementId,
			String labelString) {

		html.append("<label for=\"").append(elementId).append("\" ");
		if(StringUtils.isNotBlank(labelCssClass)) {
			html.append(" class=\"").append(labelCssClass).append("\" ");
		}
		if(StringUtils.isNotBlank(labelCssStyle)) {
			html.append(" style=\"").append(labelCssStyle).append("\" ");
		}
		html.append(">").append(labelString).append("</label>");
	}

	/**
	 * 	HTML 엘리먼트 생성 후 attribute 붙이기<br>
	 * <br>
	 * @param elementStartStructure
	 * @param loopIdx
	 * @return
	 * @throws JspException
	 */
	private StringBuilder createElement(String elementStartStructure, String outType, String elementId, String elementName,
			String cssClass, String cssStyle, String onchange, String colId) throws JspException {

		StringBuilder sb = new StringBuilder(elementStartStructure);

		if(StringUtils.isNotBlank(elementName)) {
			sb.append(" name=\"").append(elementName).append("\" ");
		}
		//라디오나 체크박스일 경우 label과 맵핑해 주기 위한 id를 셋팅하는데, "elementName_공통코드" 형식으로 만든다. 
		if("radio".equals(outType) || "checkbox".equals(outType)) {
			sb.append(" id=\"").append(elementId).append("\" ");
		}
		//라디오나 체크박스일 경우 label과 맵핑해 주기 위한 id를 셋팅하는데, "elementName_공통코드" 형식으로 만든다. 
		if("select".equals(outType)) {
			sb.append(" id=\"").append(elementName).append("\" ");
		}
		if(StringUtils.isNotBlank(cssClass)) {
			sb.append(" class=\"").append(cssClass).append("\" ");
		}
		if(StringUtils.isNotBlank(cssStyle)) {
			sb.append(" style=\"").append(cssStyle).append("\" ");
		}
		if(StringUtils.isNotBlank(onchange)) {
			sb.append(" onchange=\"").append(onchange).append("\" ");
		}
		if(StringUtils.isNotBlank(colId)) {
			sb.append(" colId=\"").append(colId).append("\" ");
		}

		return sb;
	}

	/**
	 * Resolve the given arguments Object into an arguments array.
	 * @param arguments the specified arguments Object
	 * @return the resolved arguments as array
	 * @throws JspException if argument conversion failed
	 * @see #setArguments
	 * org.springframework.web.servlet.tags.MessageTag에서 얻어온 메서드
	 */
	private Object[] resolveArguments(Object arguments) throws JspException {

		if(arguments == null) {
			return null;
		}

		if(arguments instanceof String) {
			//문자열의 나열일 경우 컴마 구분자만 허용
			String[] stringArray = org.springframework.util.StringUtils.delimitedListToStringArray((String)arguments, ","); 
			if(stringArray.length == 1) {
				Object argument = stringArray[0];
				if(argument != null && argument.getClass().isArray()) {
					return ObjectUtils.toObjectArray(argument);
				} else {
					return new Object[]{argument};
				}
			} else {
				Object[] argumentsArray = new Object[stringArray.length];
				for(int i = 0; i < stringArray.length; i++) {
					argumentsArray[i] =stringArray[i];
				}
				return argumentsArray;
			}
		} else if(arguments instanceof Object[]) {
			return (Object[])arguments;
		} else if(arguments instanceof Collection) {
			return ((Collection)arguments).toArray();
		} else if(arguments != null) {
			// Assume a single argument object.
			return new Object[]{arguments};
		} else {
			return null;
		}
	}

}
