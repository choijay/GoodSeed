/**
 * Copyright (c) 2012 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 */
package goodseed.core.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.utility.i18n.NoticeMessageUtil;

/**The class BaseParameters<br>
 * <br>
 * Parameters 기본 구현체. 이 클래스를 상속하여 채널별 Parameters 객체를 구현한다.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 3. 23.
 * 
 */

public abstract class BaseParameters extends HashMap<String, Object> implements Parameters {

	private Locale locale = null;

	/**
	 * the serialVersionUID.
	 */
	private static final long serialVersionUID = 5615764047870555398L;

	/**
	 * 생성자
	 *
	 * @author jay
	 * @since 4. 26.
	 *
	 */
	protected BaseParameters() {
		super();
	}

	/**
	 * Parameters 에 Dataset 을 세팅한다.
	 */
	@Override
	public void setGoodseedDataset(String datasetName, GoodseedDataset dataset) {
		put(datasetName, dataset);
	}

	/**
	 * key 이름의 variable 을 반환한다.
	 */
	@Override
	public Object getVariable(String key) {
		Object obj = get(key);
		if(obj != null) {
			return obj;
		} else {
			return "";
		}
	}

	/**
	 * key 이름의 variable 을 반환한다.
	 */
	@Override
	public List getVariableAsList(String key) {
		if(get(key) == null) {
			return new ArrayList();
		}
		String raw = (String)get(key).toString();
		if(raw.length() <= 2) {
			return new ArrayList();
		}
		raw = raw.substring(1, raw.length() - 1);
		String[] vars = raw.split(",");
		return Arrays.asList(vars);
	}

	/**
	 *  key 이름의 variable 을 반환한다.
	 */
	@Override
	public Integer getVariableAsInteger(String key) {
		Object obj = get(key);
		if(obj != null) {
			return Integer.parseInt(obj.toString());
		} else {
			return 0;
		}
	}

	/**
	 *  key 이름의 variable 을 반환한다.
	 */
	@Override
	public String getVariableAsString(String key) {
		Object obj = get(key);
		if(obj != null) {
			return obj.toString();
		} else {
			return "";
		}
	}

	/**
	 *   에러 메세지를 세팅한다.
	 */
	@Override
	public void setErrorMessage(String code) {
		setVariable(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_USEREXCEP);
		setVariable(GoodseedConstants.ERROR_MESSAGE_CODE, code);
		setVariable(GoodseedConstants.ERROR_MESSAGE_TEXT, NoticeMessageUtil.getMessage(code, locale));

		remove(GoodseedConstants.MESSAGE_CODE);
		remove(GoodseedConstants.MESSAGE_TEXT);

		remove(GoodseedConstants.STATUS_MESSAGE_CODE);
		remove(GoodseedConstants.STATUS_MESSAGE_TEXT);
	}

	/**
	 *  에러 메세지를 세팅한다.
	 */
	@Override
	public void setErrorMessage(String code, Object[] bindArgs) {
		setVariable(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_USEREXCEP);
		setVariable(GoodseedConstants.ERROR_MESSAGE_CODE, code);
		setVariable(GoodseedConstants.BIND_MESSAGE, NoticeMessageUtil.getBindMessage(bindArgs));
		setVariable(GoodseedConstants.ERROR_MESSAGE_TEXT, NoticeMessageUtil.getMessage(code, bindArgs, locale));

		remove(GoodseedConstants.MESSAGE_CODE);
		remove(GoodseedConstants.MESSAGE_TEXT);

		remove(GoodseedConstants.STATUS_MESSAGE_CODE);
		remove(GoodseedConstants.STATUS_MESSAGE_TEXT);
	}

	/**
	 * 메세지를 세팅한다.
	 */
	@Override
	public void setMessage(String code) {
		setVariable(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_SUCCESS);
		setVariable(GoodseedConstants.MESSAGE_CODE, code);
		setVariable(GoodseedConstants.MESSAGE_TEXT, NoticeMessageUtil.getMessage(code, locale));

		remove(GoodseedConstants.STATUS_MESSAGE_CODE);
		remove(GoodseedConstants.STATUS_MESSAGE_TEXT);
	}

	/**
	 * 메세지를 세팅한다.
	 */
	@Override
	public void setMessage(String code, Object[] bindArgs) {
		setVariable(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_SUCCESS);
		setVariable(GoodseedConstants.MESSAGE_CODE, code);
		setVariable(GoodseedConstants.BIND_MESSAGE, NoticeMessageUtil.getBindMessage(bindArgs));
		setVariable(GoodseedConstants.MESSAGE_TEXT, NoticeMessageUtil.getMessage(code, bindArgs, locale));

		remove(GoodseedConstants.STATUS_MESSAGE_CODE);
		remove(GoodseedConstants.STATUS_MESSAGE_TEXT);
	}

	/**
	 *  상태 메시지를 설정.
	 */
	@Override
	public void setStatusMessage(String code) {
		setVariable(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_SUCCESS);
		setVariable(GoodseedConstants.STATUS_MESSAGE_CODE, code);
		setVariable(GoodseedConstants.STATUS_MESSAGE_TEXT, NoticeMessageUtil.getMessage(code, locale));

		remove(GoodseedConstants.MESSAGE_CODE);
		remove(GoodseedConstants.MESSAGE_TEXT);

	}

	/**
	 *  상태 메시지를 설정.
	 */
	@Override
	public void setStatusMessage(String code, Object[] bindArgs) {
		setVariable(GoodseedConstants.ERROR_CODE, GoodseedConstants.ERROR_CODE_SUCCESS);
		setVariable(GoodseedConstants.STATUS_MESSAGE_CODE, code);
		setVariable(GoodseedConstants.BIND_MESSAGE, NoticeMessageUtil.getBindMessage(bindArgs));
		setVariable(GoodseedConstants.STATUS_MESSAGE_TEXT, NoticeMessageUtil.getMessage(code, bindArgs, locale));

		remove(GoodseedConstants.MESSAGE_CODE);
		remove(GoodseedConstants.MESSAGE_TEXT);
	}

	/**
	 * 사용자 정의 ErrorCode 를 설정.
	 */
	@Override
	public void setErrorCode(int code) {
		setVariable(GoodseedConstants.ERROR_CODE, code);
	}

	/**
	 * Parameters 객체의 내용을 한 번에 볼 수 있도록 포맷팅된 문자열로 리턴
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{ ");
		for(Object key : keySet()) {
//			if(get(key) instanceof FrameOneMiPlatformDataset) {
//				result.append(key).append("=").append(((FrameOneMiPlatformDataset)get(key)).getDataset()).append(", ").append("\n");
//			} else {
				result.append(key).append("=").append(get(key)).append(", ");
//			}
		}
		if(result.lastIndexOf(",") > 0) {
			result.deleteCharAt(result.lastIndexOf(","));
		}
		result.append(" }");
		return result.toString();
	}

	/**
	 * HttpServletRequest의 파라미터를 추출하여. Parameters 객체의 프로퍼티로 저장함.<br>
	* HTTP 파라미터가 배열형인 경우 배열로 저장함.
	 */
	@Override
	public void populateFromHttpRequest(HttpServletRequest request) {

		for(Object obj : request.getParameterMap().entrySet()) {
			Map.Entry<String, Object> mapEntry = (Map.Entry<String, Object>) obj;
			if(mapEntry.getValue() != null && mapEntry.getValue() instanceof Object[]) {
				Object[] objArr = (Object[])mapEntry.getValue();
				if(objArr.length == 1) {
					this.put(mapEntry.getKey(), objArr[0]);
				} else {
					this.put(mapEntry.getKey(), objArr);
				}
			} else {
				this.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
