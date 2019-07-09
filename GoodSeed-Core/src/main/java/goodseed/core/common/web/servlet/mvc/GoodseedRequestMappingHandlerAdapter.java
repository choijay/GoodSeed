package goodseed.core.common.web.servlet.mvc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

public class GoodseedRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

	private static final Log LOG = LogFactory.getLog(GoodseedRequestMappingHandlerAdapter.class);
	
	/**
	 * parameter argument resolver DI
	 */
	private HandlerMethodArgumentResolver parametersArgumentResolver = null;

	/**
	 * return value handler DI
	 */
	private HandlerMethodReturnValueHandler parametersReturnValueHandler = null;

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
		resolvers.add(this.parametersArgumentResolver);
		resolvers.addAll(getArgumentResolvers());
		this.setArgumentResolvers(resolvers);
		
		List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>(); 
		returnValueHandlers.add(this.parametersReturnValueHandler);
		returnValueHandlers.addAll(getReturnValueHandlers());
		this.setReturnValueHandlers(returnValueHandlers);
	}

	public HandlerMethodArgumentResolver getParametersArgumentResolver() {
		return parametersArgumentResolver;
	}

	public void setParametersArgumentResolver(HandlerMethodArgumentResolver parametersArgumentResolver) {
		this.parametersArgumentResolver = parametersArgumentResolver;
	}

	public HandlerMethodReturnValueHandler getParametersReturnValueHandler() {
		return parametersReturnValueHandler;
	}

	public void setParametersReturnValueHandler(HandlerMethodReturnValueHandler parametersReturnValueHandler) {
		this.parametersReturnValueHandler = parametersReturnValueHandler;
	}

}
