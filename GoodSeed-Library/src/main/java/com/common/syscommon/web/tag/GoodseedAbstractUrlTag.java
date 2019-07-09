package com.common.syscommon.web.tag;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.utility.config.Config;
import goodseed.core.utility.web.RequestUtil;

/**
 * The class FrameOneAbstractUrlTag<br>
 * <br>
 * 정적 자원의 기본경로를 생성해 주는 커스텀 태그 클래스들의 부모 클래스<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  5. 20.
 *
 */
abstract class GoodseedAbstractUrlTag extends TagSupport {

	private static final long serialVersionUID = -4497243460414485814L;
	private static final Log LOG = LogFactory.getLog(GoodseedAbstractUrlTag.class);

	/**
	 * 	프로토콜 상수
	 */
	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final Set<String> PROTOCOL_SET = getDefinedProtocolSet();

	/**
	 * 정적 자원의 종류를 구분하는 접두사
	 */
	//이미지
	public static final String TYPE_IMAGE = "image"; 
	//자바스크립트
	public static final String TYPE_JS = "js"; 
	//CSS
	public static final String TYPE_CSS = "css"; 

	/**
	 * 	프로퍼티에서 읽어온 기본 경로들을 정적 배열에 저장.<br>
	 * <br>
	 * 배열의 0번째 요소 - 기본 경로<br>
	 * 배열의 1번째 요소 - SSL요청일 경우의 기본 경로<br>
	 */
	protected static final String[] IMAGE_BASEPATH_ARRAY = new String[]{getBasePathFromConfig(TYPE_IMAGE, false), getBasePathFromConfig(TYPE_IMAGE, true)};
	protected static final String[] JS_BASEPATH_ARRAY = new String[]{getBasePathFromConfig(TYPE_JS, false), getBasePathFromConfig(TYPE_JS, true)};
	protected static final String[] CSS_BASEPATH_ARRAY = new String[]{getBasePathFromConfig(TYPE_CSS, false), getBasePathFromConfig(TYPE_CSS, true)};
	
	//사용자가 입력한 자원의 경로
	protected String uri; 
	//http, https 중 하나를 기입하면, 모든 조건을 무시하고 해당 프로토콜로 URL을 작성한다.
	protected String protocol; 
	//image, js, css	
	protected String type; 

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public int doEndTag() throws JspException {

		try {
			pageContext.getOut().write(getFullUrlByCustomTag());
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
	 *	정적 자원의 기본 경로를 리턴한다. (커스텀 태그를 통해 입력받은 정보를 사용) 
	 * 
	 * @return
	 * @throws JspException
	 */
	private String getFullUrlByCustomTag() throws JspException {
		return getFullUrl(this.type,this.uri, (HttpServletRequest)pageContext.getRequest(),
			 this.protocol);
	}

	/**
	 * 정적 자원의 기본 경로를 리턴한다.<br>
	 * 커스텀태그 뿐만 아니라, java에서도 호출할 수 있도록 public static 메서드로 작성하였다.<br>
	 *<br> 
	 * 	1. protocol attribute(http or https)를 정확히 입력했을 경우<br>
	 * 		- 입력한 프로토콜을 강제로 적용한다.<br>
	 * <br>
	 * 2. protocol attribute(http or https)를 입력하지 않았거나, 부정확하게 입력했을 경우<br>
	 * 		2.1 웹서버에서 셋팅한 request header의 값과 프로퍼티 파일에 정의한 sslCondition header의 값이 일치하면 (SSL요청이면)<br>
	 * 			-  sslBasePath를 적용한다.<br>
	 * 		2.2 그 외의 경우<br>
	 * 			- basePath를 적용한다.<br>  
	 * <br>
	 * @param type 정적 자원 종류 (ex: TYPE_IMAGE)
	 * @param resourceUri 정적자원 URI (ex: "/images/login/login_top.png")
	 * @param hReq HttpServletRequest
	 * @param protocol http or https
	 * @return
	 */
	public static String getFullUrl(String type, String resourceUri, HttpServletRequest hReq, String protocol) {

		String[] basePathArray = getBasePathArrayByType(type);
		//일반 요청일 경우의(http) basePath - ex) http://localhost
		String basePath = basePathArray[0]; 
		//SSL 요청일 경우의(https) basePath - ex) https://localhost
		String sslBasePath = basePathArray[1]; 
		String returnBasePath = "";

		//커스텀태그에 protocol(http or https) attribute를 입력하지 않았거나, 입력했더라도 잘못 입력했을 경우
		if(getValidProtocol(protocol) == null) {
			returnBasePath = RequestUtil.isSSL(hReq) ? sslBasePath : basePath;
		//커스텀태그에 정확한 protocol attribute를 입력했을 경우
		} else {
			if(HTTPS.equals(protocol)) {
				returnBasePath = sslBasePath;
			} else if(HTTP.equals(protocol)) {
				returnBasePath = basePath;
			}
		}
		
		//ex) /backoffice
		String contextPath = hReq.getContextPath(); 
		StringBuilder sbTemp = new StringBuilder();
		sbTemp.append(returnBasePath);
		sbTemp.append(contextPath);
		sbTemp.append(resourceUri);
		String resourceFullUrl = sbTemp.toString();

		if(LOG.isDebugEnabled()) {
			LOG.debug("resourceFullUrl: " + resourceFullUrl);
		}

		return resourceFullUrl;
	}

	/**
	 *	 정적자원 종류를 인수로 받아서 basePath 정보 배열객체를 리턴한다.
	 * 
	 * @param type 정적 자원 종류 (ex: TYPE_IMAGE) 
	 * @return
	 */
	protected static String[] getBasePathArrayByType(String type) {
		String[] ret = null;
		if(TYPE_IMAGE.equals(type)) {
			ret = IMAGE_BASEPATH_ARRAY;
		} else if(TYPE_JS.equals(type)) {
			ret = JS_BASEPATH_ARRAY;
		} else if(TYPE_CSS.equals(type)) {
			ret = CSS_BASEPATH_ARRAY;
		}
		return ret;
	}

	/**
	 * 프로퍼티에서 해당 기본경로의 값을 읽어 리턴한다.
	 * 
	 * @param type 정적 자원 종류 (ex: TYPE_IMAGE) 
	 * @param isSSL SSL요청이면 true
	 * @return
	 */
	protected static String getBasePathFromConfig(String type, boolean isSSL) {
		return Config.getString(type + (isSSL ? ".sslBasePath" : ".basePath"));
	}

	/**
	 *	인수로 받은 프로토콜이 유효할 경우 해당 프로토콜을 리턴하고, 유효하지 않을 경우 null을 리턴한다.
	 * 	
	 * @param protocol
	 * @return
	 */
	protected static String getValidProtocol(String protocol) {
		return PROTOCOL_SET.contains(protocol) ? protocol : null;
	}

	/**
	 * 프로토콜 문자열 Set을 리턴한다.
	 * 
	 * @return
	 */
	protected static Set<String> getDefinedProtocolSet() {
		Set<String> set = new HashSet<String>();
		set.add(HTTP);
		set.add(HTTPS);
		return set;
	}
}
