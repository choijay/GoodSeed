package com.common.syscommon.web.tag;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

import goodseed.core.common.GoodseedConstants;

/**
 * The class FrameOneMessageTag<br>
 * <br>
 * 리소스번들에서 메세지를 조회하여 출력<br>
 * org.springframework.web.servlet.tags.MessageTag를 수정한 클래스<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  4. 9.
 *
 */
public class GoodseedMessageTag extends HtmlEscapingAwareTag {

	private static final long serialVersionUID = 6053899185462527076L;
	private static final Log LOG = LogFactory.getLog(GoodseedMessageTag.class);
	
	/**
	 * Default separator for splitting an arguments String: a comma (",")
	 */
	public static final String DEFAULT_ARGUMENT_SEPARATOR = ",";

	private Object message;

	private String code;

	private Object arguments;

	private String argumentSeparator = DEFAULT_ARGUMENT_SEPARATOR;

	private String text;

	private String var;

	private String scope = TagUtils.SCOPE_PAGE;

	private boolean javaScriptEscape = false;

	/**
	 * 	현재페이지 기준 참조 여부 FLAG (FrameOne에서 추가된 속성)<br>
	 * <br>
	 * true - 현재 요청URI의 패키지구조를 기준으로 메세지를 찾는다.(기본값)<br>
	 * (ex) 요청URI가 /common/templateGrid/listSample.fo 인 요청에서, 메세지키 frameone.templateGrid.listSample.lbHobby를 찾을 경우 code에 "lbHobby"만 입력하면 됨.<br>
	 * <br>
	 * false - 현재 JSP페이지의 패키지구조에 관계없이 찾고자 하는 리소스번들의 키를 전부 입력<br>
	 * (ex) frameone.templateGrid.listSample.lbHobby를 찾을 경우 code에 "frameone.templateGrid.listSample.lbHobby" 입력.<br>
	 */
	private boolean relative = true;

	/**
	 * Set the MessageSourceResolvable for this tag.
	 * Accepts a direct MessageSourceResolvable instance as well as a JSP
	 * expression language String that points to a MessageSourceResolvable.
	 * <p>If a MessageSourceResolvable is specified, it effectively overrides
	 * any code, arguments or text specified on this tag.
	 */
	public void setMessage(Object message) {
		this.message = message;
	}

	/**
	 * Set the message code for this tag.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Set optional message arguments for this tag, as a comma-delimited
	 * String (each String argument can contain JSP EL), an Object array
	 * (used as argument array), or a single Object (used as single argument).
	 */
	public void setArguments(Object arguments) {
		this.arguments = arguments;
	}

	/**
	 * Set the separator to use for splitting an arguments String.
	 * Default is a comma (",").
	 * @see #setArguments
	 */
	public void setArgumentSeparator(String argumentSeparator) {
		this.argumentSeparator = argumentSeparator;
	}

	/**
	 * Set the message text for this tag.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Set PageContext attribute name under which to expose
	 * a variable that contains the resolved message.
	 * @see #setScope
	 * @see javax.servlet.jsp.PageContext#setAttribute
	 */
	public void setVar(String var) {
		this.var = var;
	}

	/**
	 * Set the scope to export the variable to.
	 * Default is SCOPE_PAGE ("page").
	 * @see #setVar
	 * @see org.springframework.web.util.TagUtils#SCOPE_PAGE
	 * @see javax.servlet.jsp.PageContext#setAttribute
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Set JavaScript escaping for this tag, as boolean value.
	 * Default is "false".
	 */
	public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
		this.javaScriptEscape = javaScriptEscape;
	}

	/**
	 * Set relative (FrameOne에서 추가한 메서드)
	 * 
	 * @param relative 요청URI 기반 상대참조 여부
	 */
	public void setRelative(boolean relative) {
		this.relative = relative;
	}

	/**
	 * Resolves the message, escapes it if demanded,
	 * and writes it to the page (or exposes it as variable).
	 * @see #resolveMessage()
	 * @see org.springframework.web.util.HtmlUtils#htmlEscape(String)
	 * @see org.springframework.web.util.JavaScriptUtils#javaScriptEscape(String)
	 * @see #writeMessage(String)
	 */
	@Override
	protected final int doStartTagInternal() throws JspException, IOException {
		try {
			// Resolve the unescaped message.
			String msg = resolveMessage();

			// HTML and/or JavaScript escape, if demanded.
			msg = isHtmlEscape() ? HtmlUtils.htmlEscape(msg) : msg;
			msg = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(msg) : msg;

			// Expose as variable, if demanded, else write to the page.
			String resolvedVar =this.var;
			if(resolvedVar != null) {
				String resolvedScope = this.scope;
				pageContext.setAttribute(resolvedVar, msg, TagUtils.getScope(resolvedScope));
			} else {
				writeMessage(msg);
			}

			return EVAL_BODY_INCLUDE;
		} catch(NoSuchMessageException ex) {
			throw new JspTagException(getNoSuchMessageExceptionDescription(ex));
		}
	}

	/**
	 * Resolve the specified message into a concrete message String.
	 * The returned message String should be unescaped.
	 */
	protected String resolveMessage() throws JspException, NoSuchMessageException {
		MessageSource messageSource = getMessageSource();
		if(messageSource == null) {
			throw new JspTagException("No corresponding MessageSource found");
		}
	
		// Evaluate the specified MessageSourceResolvable, if any.
				MessageSourceResolvable resolvedMessage = null;
				if(this.message instanceof MessageSourceResolvable) {
					resolvedMessage = (MessageSourceResolvable)this.message;
				} else if(this.message != null) {
//					String expr = this.message.toString();
					resolvedMessage = (MessageSourceResolvable)this.message;
				}
		if(resolvedMessage != null) {
			// We have a given MessageSourceResolvable.
			return messageSource.getMessage(resolvedMessage, getRequestContext().getLocale());
		}

		String resolvedCode = this.code;
		String resolvedText = this.text;

		//-------------------------------- FrameOne에서 추가한 부분 - start ------------------------------------
		// 기능설명 - relative 변수 선언부 참조

		//사용자가 요청한 URI
		ServletRequest req = (ServletRequest)pageContext.getRequest();
		//요청 URI
		String reqURI = (String)req.getAttribute(GoodseedConstants.REQ_URI); 
		// "/common/templateGrid/listSample.fo" 의 형태
		//		System.out.println("@@@ reqURI : " + reqURI); 
		
		//URI로부터 패키지구조 형태 추출
		String packageStr = reqURI.substring(reqURI.indexOf('/') + 1, reqURI.lastIndexOf('.') + 1).replaceAll("/", ".");
		// "frameone.templateGrid.listSample." 의 형태
		//		System.out.println("@@ packageStr : " + packageStr); 
		
		//relative가 true일 경우 해당 요청URI에서 읽어들인 기본 패키지 구조를 자동으로 붙여준다.
		if(relative) {
			resolvedCode = packageStr + resolvedCode;
		}
		//		System.out.println("@@ resolvedCode : " + resolvedCode);
		//-------------------------------- FrameOne에서 추가한 부분 - end -------------------------------------

		if(resolvedCode != null || resolvedText != null) {
			// We have a code or default text that we need to reseolve.
			Object[] argumentsArray = resolveArguments(this.arguments);

			try {

				if(resolvedText != null) {
					// We have a fallback text to consider.
					return messageSource.getMessage(resolvedCode, argumentsArray, resolvedText, getRequestContext().getLocale());
				} else {
					// We have no fallback text to consider.
					return messageSource.getMessage(resolvedCode, argumentsArray, getRequestContext().getLocale());
				}

			} catch(Exception e) {
				//예외상황일 경우 공백문자 리턴
				LOG.error("Exception", e);
				return ""; 
			}
		}

		// All we have is a specified literal text.
		return resolvedText;
	}

	/**
	 * Resolve the given arguments Object into an arguments array.
	 * @param arguments the specified arguments Object
	 * @return the resolved arguments as array
	 * @throws JspException if argument conversion failed
	 * @see #setArguments
	 */
	protected Object[] resolveArguments(Object arguments) throws JspException {
		Object[] result = null;
		if(arguments instanceof String) {
			//System.out.println("@@ arguments : String");
			String[] stringArray = StringUtils.delimitedListToStringArray((String)arguments, this.argumentSeparator);
			if(stringArray.length == 1) {
				Object argument = stringArray[0];
				if(argument != null && argument.getClass().isArray()) {
					result = ObjectUtils.toObjectArray(argument);
				} else {
					result = new Object[]{argument};
				}
			} else {
				Object[] argumentsArray = new Object[stringArray.length];
				for(int i = 0; i < stringArray.length; i++) {
					argumentsArray[i] = stringArray[i];
				}
				result = argumentsArray;
			}
		} else if(arguments instanceof Object[]) {
			//System.out.println("@@ arguments : Object[]");
			result = (Object[])arguments;
		} else if(arguments instanceof Collection) {
			//System.out.println("@@ arguments : Collection");
			result = ((Collection)arguments).toArray();
		} else if(arguments != null) {
			// Assume a single argument object.
			result = new Object[]{arguments};
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * Write the message to the page.
	 * <p>Can be overridden in subclasses, e.g. for testing purposes.
	 * @param msg the message to write
	 * @throws IOException if writing failed
	 */
	protected void writeMessage(String msg) throws IOException {
		pageContext.getOut().write(String.valueOf(msg));
	}

	/**
	 * Use the current RequestContext's application context as MessageSource.
	 */
	protected MessageSource getMessageSource() {
		return getRequestContext().getMessageSource();
	}

	/**
	 * Return default exception message.
	 */
	protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
		return ex.getMessage();
	}

}
