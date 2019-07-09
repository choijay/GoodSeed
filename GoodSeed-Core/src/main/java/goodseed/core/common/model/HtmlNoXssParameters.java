/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.model;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import goodseed.core.common.utility.XssShield;
import goodseed.core.common.web.listener.adapter.DefaultContextLoaderAdapter;

/**
 * 
 * The class HtmlNoXssParameters<br>
 * <br>
 * HtmlParameters에 XSS 방어 기능을 추가한 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2016 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 1.0
 * @since  5. 27.
 *
 */
public class HtmlNoXssParameters implements Parameters {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4727131886654187991L;
	
	/**
	 * HtmlNoXssParameters는 HtmlParameters에 일부 기능(XSS필터링)을 추가하기 위해 만든 것으로서,<br>
	 * 모든 중추적인 기능을 HtmlParameters 멤버 객체가 담당한다.<br>
	 */
	private HtmlParameters htmlParameters;

	/**
	 * 	HtmlNoXssParameters 기본 생성자
	 */
	public HtmlNoXssParameters(HtmlParameters htmlParameters) {
		this.htmlParameters = htmlParameters;
	}

	/**
	 * 
	 * HtmlParameters의 setGoodseedDataset 메서드를 호출하여 데이터셋 객체를 입력받은 이름으로 set한다.<br>
	 * <br>
	 * @param datasetName 데이터셋 명
	 * @param dataset 데이터셋 객체
	 * @return
	 * @ahthor KimJiHye
	 */
	public void setGoodseedDataset(String datasetName, GoodseedDataset dataset) {
		htmlParameters.setGoodseedDataset(datasetName, dataset);
	}

	/**
	 * 
	 * GoodseedHtmlDataset 객체를 반환한다.<br>
	 * <br>
	 * @return GoodseedHtmlDataset
	 * @ahthor KimJiHye
	 */
	public GoodseedHtmlDataset getGoodseedDatasetInstance() {
		return htmlParameters.getGoodseedDatasetInstance();
	}

	/**
	 * 
	 * HtmlParameters의 getVariable 메서드를 호출하여 키에 대응하는 variable을 오브젝트 형태로 리턴한다.<br>
	 * <br>
	 * @param key 키
	 * @param Object value 오브젝트
	 * @return
	 * @ahthor KimJiHye
	 */
	public Object getVariable(String key) {
		return htmlParameters.getVariable(key);
	}

	/**
	 * HtmlParameters의 setVariable 메서드를 호출하여 키에 해당하는 variable 오브젝트를 지정한다.<br>
	 * <br>
	 * @param key 키
	 * @param Object value 오브젝트
	 * @return
	 * @ahthor KimJiHye
	 */
	public final void setVariable(String key, Object variable) {
		htmlParameters.setVariable(key, variable);
	}

	/**
	 * 
	 * HtmlParameters의 getVariableAsList 메서드를 호출하여 키에 대응하는 variable을 List 형태로 리턴한다.<br>
	 * <br>
	 * @param key 키
	 * @param List value
	 * @return
	 * @ahthor KimJiHye
	 */
	public List getVariableAsList(String key) {
		return htmlParameters.getVariableAsList(key);
	}

	/**
	 * 
	 * HtmlParameters의 getVariableAsInteger 메서드를 호출하여 키에 대응하는 variable을 Integer 형태로 리턴한다.<br>
	 * <br>
	 * @param key 키
	 * @param Integer value
	 * @return
	 * @ahthor KimJiHye
	 */
	public Integer getVariableAsInteger(String key) {
		return htmlParameters.getVariableAsInteger(key);
	}

	/**
	 * 
	 * HtmlParameters의 getVariableAsString 메서드를 호출하여 키에 대응하는 variable을<br>
	 * XSS필터링이 적용된String 형태로 리턴한다.<br>
	 * <br>
	 * @param key 키
	 * @param String value
	 * @return
	 * @ahthor KimJiHye
	 */
	public String getVariableAsString(String key) {
		return getXssShield().stripXss(htmlParameters.getVariableAsString(key));
	}

	/**
	 * 
	 * HtmlParameters의 setErrorMessage 메서드를 호출하여 에러메세지를 지정한다.<br>
	 * <br>
	 * @param code 에러코드
	 * @ahthor KimJiHye
	 */
	public void setErrorMessage(String code) {
		htmlParameters.setErrorMessage(code);
	}

	/**
	 * 
	 * HtmlParameters의 setErrorMessage 메서드를 호출하여 arguments가 바인딩된 에러 메세지를 지정한다.<br>
	 * <br>
	 * @param code 에러코드
	 * @param bindArgs
	 * @ahthor KimJiHye
	 */
	public void setErrorMessage(String code, Object[] bindArgs) {
		htmlParameters.setErrorMessage(code, bindArgs);
	}

	/**
	 * 
	 * HtmlParameters의 setMessage 메서드를 호출하여 메세지를 지정한다.<br>
	 * <br>
	 * @param code 코드
	 * @ahthor KimJiHye
	 */
	public void setMessage(String code) {
		htmlParameters.setMessage(code);
	}

	/**
	 * 
	 * HtmlParameters의 setMessage 메서드를 호출하여 arguments가 바인딩된 메세지를 지정한다.<br>
	 * <br>
	 * @param code 코드
	 * @param bindArgs
	 * @ahthor KimJiHye
	 */
	public void setMessage(String code, Object[] bindArgs) {
		htmlParameters.setMessage(code, bindArgs);
	}

	/**
	 * 
	 * HtmlParameters의 setStatusMessage 메서드를 호출하여 상태메세지를 지정한다.<br>
	 * <br>
	 * @param code 코드
	 * @ahthor KimJiHye
	 */
	public void setStatusMessage(String code) {
		htmlParameters.setStatusMessage(code);
	}

	/**
	 * 
	 * HtmlParameters의 setStatusMessage 메서드를 호출하여 arguments가 바인딩된 상태메세지를 지정한다.<br>
	 * <br>
	 * @param code 코드
	 * @param bindArgs
	 * @ahthor KimJiHye
	 */
	public void setStatusMessage(String code, Object[] bindArgs) {
		htmlParameters.setStatusMessage(code, bindArgs);
	}

	/**
	 * 
	 * HtmlParameters의 setErrorCode 메서드를 호출하여 에러코드를 지정한다.<br>
	 * <br>
	 * @param code 에러코드
	 * @ahthor KimJiHye
	 */
	public void setErrorCode(int code) {
		htmlParameters.setErrorCode(code);
	}

	/**
	 * HtmlParameters의 toString 메서드를 호출하여 Parameters 객체의 내용을 한 번에 볼 수 있도록 포맷팅된 문자열로 리턴한다.
	 */
	public String toString() {
		return htmlParameters.toString();
	}

	/**
	 * HtmlParameters의 populateFromHttpRequest 메서드를 호출하여 HttpServletRequest의 파라미터를 추출, Parameters 객체의 프로퍼티로 저장함.
	 * 파라미터가 배열형인 경우 배열로 저장한다.
	 */
	public void populateFromHttpRequest(HttpServletRequest request) {
		htmlParameters.populateFromHttpRequest(request);
	}

	/**
	 * HtmlParameters의 getLocale 메서드를 호출하여 로케일 정보 리턴한다.
	 */
	public Locale getLocale() {
		return htmlParameters.getLocale();
	}

	/**
	 * HtmlParameters의 setLocale 메서드를 호출하여 로케일을 지정한다.
	 */
	public void setLocale(Locale locale) {
		htmlParameters.setLocale(locale);
	}

	/**
	 * HtmlParameters의 size 리턴한다.
	 */
	public int size() {
		return htmlParameters.size();
	}

	/**
	 * HtmlParameters의 isEmpty값 리턴한다. 
	 * key-value 매핑이 없으면 true를 리턴한다.
	 */
	public boolean isEmpty() {
		return htmlParameters.isEmpty();
	}

	/**
	 * HtmlParameters의 equals 메서드를 호출하여 결과값을 리턴한다. 
	 * 두 객체가 같으면 true를 리턴한다.
	 */
	public boolean equals(Object o) {
		return htmlParameters.equals(o);
	}

	/**
	 * HtmlParameters의 containsKey 메서드를 호출하여 결과값을 리턴한다. 
	 * 해당 key 오브젝트가 있으면 true를 리턴한다.
	 */
	public boolean containsKey(Object key) {
		return htmlParameters.containsKey(key);
	}

	/**
	 * HtmlParameters의 put 메서드를 호출하여 key에 value를 지정한다. 
	 */
	public Object put(String key, Object value) {
		return htmlParameters.put(key, value);
	}

	/**
	 * HtmlParameters의 hashCode 메서드를 호출하여 값을 리턴한다.
	 */
	public int hashCode() {
		return htmlParameters.hashCode();
	}

	/**
	 * HtmlParameters의 putAll 메서드를 호출하여 입력받은 Map을 htmlParameters에 모두 put한다.
	 */
	public void putAll(Map m) {
		htmlParameters.putAll(m);
	}

	/**
	 * HtmlParameters의 remove 메서드를 호출하여 입력받은 key에 해당하는 매핑을 삭제한다.
	 */
	public Object remove(Object key) {
		return htmlParameters.remove(key);
	}

	/**
	 * HtmlParameters의 clear 메서드를 호출하여 Map을 비운다.
	 */
	public void clear() {
		htmlParameters.clear();
	}

	/**
	 * HtmlParameters의 containsValue 메서드를 호출하여 결과값을 리턴한다. 
	 * 해당 value 오브젝트가 있으면 true를 리턴한다.
	 */
	public boolean containsValue(Object value) {
		return htmlParameters.containsValue(value);
	}

	/**
	 * HtmlParameters의 clone 메서드를 호출하여 복사한 객체를 리턴한다.
	 */
	public Object clone() {
		return htmlParameters.clone();
	}

	/**
	 * HtmlParameters의 keySet 메서드를 호출하여 키 목록을 Set 형태로 리턴한다.
	 */
	public Set keySet() {
		return htmlParameters.keySet();
	}

	/**
	 * HtmlParameters의 values 메서드를 호출하여 value 목록을 Collection 형태로 리턴한다.
	 */
	public Collection values() {
		return htmlParameters.values();
	}

	/**
	 * HtmlParameters의 entrySet 메서드를 호출하여 key-value 엘리먼트 목록을 Set 형태로 리턴한다.
	 */
	public Set entrySet() {
		return htmlParameters.entrySet();
	}

	/**
	 * 	본 클래스로 감싸기 전의 HtmlParameters 객체 자체를 리턴한다.
	 */
	public Parameters getOriginalParameters() {
		return htmlParameters;
	}

	/**
	 * Parameters 에서 입력한 datasetName에 해당하는 GoodseedHtmlDataset 을 가져 온다.
	 */
	public GoodseedDataset getGoodseedDataset(String datasetName) {
		Object obj = get(datasetName);
		if(obj instanceof GoodseedDataset) {
			GoodseedDataset data = (GoodseedDataset)obj;
			if(data != null && this.size() > 0) {
				data.putAll(htmlParameters.getVariableMap());
			}
			return data;
		} else {
			return null;
		}
	}

	/**
	 * HtmlParameters의 get 메서드를 호출하여 key에 해당하는 value 오브젝트를 리턴받고,<br>
	 * 리턴된 value에 XSS필터링을 수행하도록 처리한다.<br>
	 * 본 객체가 INSERT/UPDATE 계열 쿼리의 인수로 들어가는 상황을 감안한 코드<br>
	 */
	public Object get(Object key) {
		Object value = htmlParameters.get(key);
		if(value != null) {
			//XSS공격을 하는 파라미터는 script의 형태를 띠고 있을 것이므로, 문자열일 경우에만 XSS필터링을 해 준다.
			if(value instanceof String) {
				value = getXssShield().stripXss((String)value);
			}
			return value;
		}
		return null;
	}

	/**
	 * XssShield bean을 context에서 얻어서 리턴한다.<br>
	 * <br>
	 * @return
	 */
	public XssShield getXssShield() {
		return (XssShield)DefaultContextLoaderAdapter.getBean("xssShield");
	}

	/**
	 * HtmlParameters의 getVariableMap 메서드를 호출하여 VariableMap을 리턴한다.
	 */
	public Map getVariableMap() {
		return htmlParameters.getVariableMap();
	}

}
