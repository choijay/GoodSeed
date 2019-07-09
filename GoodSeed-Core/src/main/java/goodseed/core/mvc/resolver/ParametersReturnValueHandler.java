package goodseed.core.mvc.resolver;

/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Iterator;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;


/**
 * This return value handler is intended to be ordered after all others as it 
 * attempts to handle _any_ return value type (i.e. returns {@code true} for
 * all return types).
 * 
 * <p>The return value is handled either with a {@link ModelAndViewResolver}
 * or otherwise by regarding it as a model attribute if it is a non-simple 
 * type. If neither of these succeeds (essentially simple type other than 
 * String), {@link UnsupportedOperationException} is raised.
 * 
 * <p><strong>Note:</strong> This class is primarily needed to support 
 * {@link ModelAndViewResolver}, which unfortunately cannot be properly
 * adapted to the {@link HandlerMethodReturnValueHandler} contract since the 
 * {@link HandlerMethodReturnValueHandler#supportsReturnType} method
 * cannot be implemented. Hence {@code ModelAndViewResolver}s are limited
 * to always being invoked at the end after all other return value 
 * handlers have been given a chance. It is recommended to re-implement 
 * a {@code ModelAndViewResolver} as {@code HandlerMethodReturnValueHandler},
 * which also provides better access to the return type and method information.
 * 
 * @author Rossen Stoyanchev
 * @since 3.1
 */
/**
 * The class ParametersReturnValueHandler<br>
 * <br>
 * TODO Html 용 Parameter Return Type 변환 클래스.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 4. 27.
 *
 */
public class ParametersReturnValueHandler implements HandlerMethodReturnValueHandler {

	//private final ModelAttributeMethodProcessor modelAttributeProcessor = new ModelAttributeMethodProcessor(true);

	private static final MappingJackson2JsonView JACKSON_JSON_VIEW = new MappingJackson2JsonView();

	//	private static final JstlView JSTL_VIEW = new JstlView();

	/**
	 * Create a new instance.
	 */
	/*public ParametersReturnValueHandler() {
		
	}*/

	/**
	 * Always returns {@code true}. See class-level note.
	 */
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> paramType = returnType.getParameterType();
		return Parameters.class.isAssignableFrom(paramType);
	}

	/**
	 * Parameter return type 에 따른 데이타 핸들
	 * aJax 타입 인 경우 aJax 로 변환 하여 리턴
	 */
	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest request) {

		String ajaxType = request.getHeader("AjaxType");

		Iterator it = ((Map)returnValue).keySet().iterator();
		Object key = null;
		Object value = null;
		while(it.hasNext()) {
			key = it.next();
			value = ((Map)returnValue).get(key.toString());

			if(key.toString().startsWith("ds_")) {
				mavContainer.addAttribute((String)key, value);
			} else {
				mavContainer.addAttribute((String)key, value);
			}

		}

		if(!"raw".equals(ajaxType)) {
			mavContainer.setView(JACKSON_JSON_VIEW);
		} else {
			//			mavContainer.setViewName(null);

			//			mavContainer.setView(JSTL_VIEW);
			//			mavContainer.setViewName(null);

			HtmlParameters params = (HtmlParameters)returnValue;
			mavContainer.setRequestHandled(true);

			if(params.containsKey(GoodseedConstants.STATUS_MESSAGE_CODE)) {

				request.setAttribute(GoodseedConstants.STATUS_MESSAGE_CODE,
						params.getVariable(GoodseedConstants.STATUS_MESSAGE_CODE), 0);
				request.setAttribute(GoodseedConstants.STATUS_MESSAGE_TEXT,
						params.getVariable(GoodseedConstants.STATUS_MESSAGE_TEXT), 0);
			}

			if(params.containsKey(GoodseedConstants.MESSAGE_CODE)) {
				request.setAttribute(GoodseedConstants.MESSAGE_CODE, params.getVariable(GoodseedConstants.MESSAGE_CODE), 0);
				request.setAttribute(GoodseedConstants.MESSAGE_TEXT, params.getVariable(GoodseedConstants.MESSAGE_TEXT), 0);
			}

			if(params.containsKey(GoodseedConstants.ERROR_CODE)) {
				request.setAttribute(GoodseedConstants.ERROR_CODE, params.getVariableAsString(GoodseedConstants.ERROR_CODE), 0);
			}

		}

	}
}
