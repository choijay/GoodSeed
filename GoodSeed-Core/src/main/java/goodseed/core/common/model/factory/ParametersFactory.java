/**
 * Copyright (c) 2012 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 */
package goodseed.core.common.model.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.model.HtmlNoXssParameters;
import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;

/**The class ParametersFactory<br>
 * <br>
 * Parameters Factory Class.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 3. 23.
 *
 */

public final class ParametersFactory {

	private static final Log LOG = LogFactory.getLog(ParametersFactory.class);

	private ParametersFactory() {

	}

	public static final <T extends Parameters> T createParameters(Class<T> parametersClass) {
		if(parametersClass.equals(HtmlParameters.class)) {
			try {
				return parametersClass.newInstance();
			} catch(InstantiationException e) {
				if(LOG.isErrorEnabled()) {
					LOG.error("Exception", e);
				}
				e.printStackTrace();
			} catch(IllegalAccessException e) {
				if(LOG.isErrorEnabled()) {
					LOG.error("Exception", e);
				}
				e.printStackTrace();
			}
		}

		return null;
	}

	public static final <T extends Parameters> T createParameters(T parameters) {
		if(parameters.getClass().equals(HtmlParameters.class)) {
			return (T)getHtmlParameters((HtmlParameters)parameters);
		} else if(parameters.getClass().equals(HtmlNoXssParameters.class)) {
			//HtmlNoXssParameters 객체가 인수로 넘어올 경우를 위한 분기
			Parameters originalParameters = parameters.getOriginalParameters();
			if(null != originalParameters && originalParameters.getClass().equals(HtmlParameters.class)) {
				return (T)getHtmlParameters((HtmlParameters)originalParameters);
			}
		}
		return null;
	}

	private static final HtmlParameters getHtmlParameters(HtmlParameters parameters) {
		HtmlParameters htmlParameters = new HtmlParameters();
		//인수 Parameters의 Locale 전달
		htmlParameters.setLocale(parameters.getLocale());
		return htmlParameters;
	}
}
