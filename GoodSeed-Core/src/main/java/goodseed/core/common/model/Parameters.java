/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * The class ParamMap
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public interface Parameters extends Map<String, Object>, Serializable {

	/**
	 * ParamMap 에 있는 Dataset을 가져온다.
	 * 
	 * @param datasetName Dataset name.
	 * @return 해당 이름을 가진 GoodseedDataset 
	 */
	GoodseedDataset getGoodseedDataset(String datasetName);

	/**
	 * Variable 값들을 가지는 비어있는 Dataset 을 만든다.
	 * 
	 * @return 현재 ParamMap 객체가 갖고 있는 Variable을 모두 포함하는 빈 GoodseedDataset 객체
	 */
	GoodseedDataset getGoodseedDatasetInstance();

	/**
	 *     ParamMap 에 Dataset 을 세팅한다.<br>
	 * <br>
	 * @param datasetName GoodseedDataset의 이름
	 * @param dataset 해당 GoodseedDataset
	 */
	void setGoodseedDataset(String datasetName, GoodseedDataset dataset);

	/**
	 * key 이름의 variable 을 반환한다.<br>
	 * HtmlParamMap 를 사용할 경우에는 XSS(Cross Site Scripting) 가 적용되어<br>
	 * 특정 문자일 경우 스페이스로 치환될수 있음.
	 * 
	 * @param key variable name.
	 * @return key에 해당하는 Variable 객체
	 */
	Object getVariable(String key);

	/**
	 * key 이름의 variable 을 세팅한다.
	 * 
	 * @param key variable name.
	 * @param variable value.
	 */
	void setVariable(String key, Object variable);

	/**
	 * JS에서 Array 형태로 입력된 variable 을 List 형태로 반환한다.<br>
	 * 주의 : JS에서 전달된 Array의 특성상 ','로 구분된 데이터에 대한 처리이므로,<br>
	 * 데이터 자체에 ',' 문자가 포함된 경우 오작동 할 수 있다.
	 * 
	 * @param key variable name.
	 * @return TODO
	 */
	List getVariableAsList(String key);

	/**
	 * key 이름의 variable 을 반환한다.
	 * 
	 * @param key variable name.
	 * @return key에 해당하는 Variable의 Integer 객체
	 */
	Integer getVariableAsInteger(String key);

	/**
	 * key 이름의 variable 을 반환한다.<br>
	 * <br>
	 * @param key variable name.
	 * @return key에 해당하는 Variable의 문자열 객체
	 */
	String getVariableAsString(String key);

	/**
	 *     에러 메세지를 세팅한다.<br>
	 * <br>
	 * @param code 에러 메세지 코드
	 */
	void setErrorMessage(String code);

	/**
	 * 에러 메세지를 세팅한다.
	 * 
	 * @param code 에러메세지 코드
	 * @param bindArgs 바인딩 되는 메세지
	 */
	void setErrorMessage(String code, Object[] bindArgs);

	/**
	 * 메세지를 세팅한다.
	 * 
	 * @param code 메세지.
	 */
	void setMessage(String code);

	/**
	 * 메세지를 세팅한다.
	 * 
	 * @param code 메세지 코드.
	 * @param bindArgs 바인딩되는 메세지
	 */
	void setMessage(String code, Object[] bindArgs);

	/**
	 * 상태 메시지를 설정.
	 *
	 * @param code 메시지 코드.
	 */
	void setStatusMessage(String code);

	/**
	 * 상태 메시지를 설정.
	 *
	 * @param code 메시지 코드.
	 * @param bindArgs 바인드 되는 메시지.
	 */
	void setStatusMessage(String code, Object[] bindArgs);

	/**
	 * 사용자 정의 ErrorCode 를 설정.
	 *
	 * @param code 에러 코드.
	 */
	void setErrorCode(int code);

	/**
	 * 	ParamMap 객체의 내용을 한 번에 볼 수 있도록 포맷팅된 문자열로 리턴<br>
	 * <br>
	 * @return Parameter객체의 모든 내용을 담은 문자열
	 */
	String toString();

	/**
	 * HttpServletRequest의 파라미터를 추출하여. ParamMap 객체의 프로퍼티로 저장함.<br>
	 * HTTP 파라미터가 배열형인 경우 배열로 저장함.
	 * 
	 * @ahthor KongJungil
	 * @since 2. 23.
	 *
	 * @param request 원본 파라미터가 저장된 HTTP REQUEST 객체
	 */
	void populateFromHttpRequest(HttpServletRequest request);

	/**
	 * 데코레이터로 감싸기 전의 원본 ParamMap 객체를 리턴해 주는 메서드<br>
	 * 
	 * @return
	 */
	Parameters getOriginalParameters();
	
}
