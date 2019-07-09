package goodseed.core.common.conversion;

import java.io.BufferedReader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedHtmlDataset;
import goodseed.core.common.model.GoodseedHtmlNoXssDataset;
import goodseed.core.common.model.HtmlNoXssParameters;
import goodseed.core.common.model.HtmlParameters;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.AbstractDatasetFactory;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.utility.CharSetUtil;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.exception.SystemException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.i18n.NoticeMessageUtil;

/**  * A  that delegates to  * ,  
* but also ensured that the request body is validated.  *  
* Currently, this class only supports (and assumes) a request content type of <CODE>application/json</CODE>.  
*/

/**
 * The class ParametersArgumentResolver<br>
 * <br>
 * Parameter 타입 Argument Data Binding.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 3. 23.
 *
 */
public class ParametersArgumentResolver implements HandlerMethodArgumentResolver {

	private static final Log LOG = LogFactory.getLog(ParametersArgumentResolver.class);

	/**
	 * Parameters 타입 인경우에 Argument Data 바인딩
	 * Json Text 인경우 객체 타입으로 변환하여 바인딩 한다.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		if (LOG.isDebugEnabled()) {
			LOG.debug("============여기까지 왔음.");
		}
		
		HttpServletRequest requestInfo = (HttpServletRequest)webRequest.getNativeRequest();

		String contentType = StringUtils.defaultString(requestInfo.getContentType());

		Map<String, String[]> paramMap = webRequest.getParameterMap();

		Iterator it = paramMap.keySet().iterator();
		Object key = null;
		String[] value = null;
		HtmlNoXssParameters inParams = new HtmlNoXssParameters(ParametersFactory.createParameters(HtmlParameters.class));
		inParams.setVariable(GoodseedConstants.CLIENT_IP, (String)requestInfo.getAttribute(GoodseedConstants.CLIENT_IP));

		HttpSession session = ((HttpServletRequest)requestInfo).getSession();
		inParams.setLocale(LocaleUtil.getUserLocale(session));
		requestInfo.setAttribute(GoodseedConstants.FRAMEONE_LOCALE, inParams.getLocale());

		Enumeration attrNames = session.getAttributeNames();
		while(attrNames.hasMoreElements()) {
			String attrName = (String)attrNames.nextElement();
			inParams.setVariable(attrName, session.getAttribute(attrName));
		}

		/**
		 * 최대 파라미터 갯수를 제한하여 더이상의 파라미터가 넘어올 경우 차단하도록 함
		 * 기본 파라미터는 100개로 제한을 두어 100개 이상될 경우에만 차단하며, 그 이상 필요할 
		 * 경우에는 parameter.maxcount 를 Config 테이블에 등록하여 그 갯수를 늘리도록 한다.
		 */
		int paramMaxCnt = Config.getInteger("parameter.maxcount", 100);

		String page = null;
		String rows = null;

		int paramCnt = 0;
		//일반적인 ContentType의 경우 (ex:application/x-www-form-urlencoded)
		while(it.hasNext()) {

			++paramCnt;

			if(paramMaxCnt < paramCnt) {
				if(LOG.isErrorEnabled()) {
					LOG.error("paramMaxCnt:[" + paramMaxCnt + "] exceeded");
					LOG.error("파라미터는 최대 파라미터 건수를 초과할수 없습니다.");
				}
				throw new SystemException(NoticeMessageUtil.getMessage("MSG_COM_ERR_062"));
			}

			key = it.next();
			value = (String[])paramMap.get(key);

			inParams.setVariable(key.toString(), URLDecoder.decode(value[0], CharSetUtil.getDefaultCharSet(requestInfo)));
			if("page".equals(key)) {
				page = value[0];
			}
			if("rows".equals(key)) {
				rows = value[0];
			}
		}

		//ContentType이 application/json인 경우 getParameter() 등으로 읽을 수 없고 InputStream을 통해서 읽어야 한다.
		//firefox에서 application/json 뒤에 ;UTF-8을 붙여서 오기 때문에 equals()에서 contains로 변경함.
		if(contentType.contains("application/json")) {
			webRequest.setAttribute("contentType", "application/json", 0);
			StringBuilder jsonText = new StringBuilder();
			String line = null;
			ObjectMapper mapper = new ObjectMapper();
			try {
				BufferedReader reader = requestInfo.getReader();
				while((line = reader.readLine()) != null) {
					jsonText.append(line);
				}
				// 읽어들인 jsonText가 있을 때만 수행하도록 체크
				String jsonTxt = jsonText.toString();
				if(!"".equals(jsonTxt)) {

					Map<String, Object> jsonMap = mapper.readValue(jsonTxt, new TypeReference<HashMap<String, Object>>() {});

					String jsonKey;
					Object jsonValue;

					for(Entry<String, Object> e : jsonMap.entrySet()) {
						jsonKey = (String)e.getKey();
						jsonValue = e.getValue();

						if(jsonValue instanceof String) {
							inParams.setVariable(jsonKey, jsonValue);

						} else {
							if(jsonKey.startsWith("ds_")) {
								Map map = (Map)jsonValue;
								List dataRows = (List)map.get("data");
								List deletedDataRows = (List)map.get("deletedData");
								GoodseedHtmlNoXssDataset goodseedHtmlNoXssDataset =
										new GoodseedHtmlNoXssDataset((GoodseedHtmlDataset)AbstractDatasetFactory
												.getDatasetFactory(GoodseedHtmlDataset.class).makeDataset(dataRows,
														deletedDataRows, jsonKey));
								inParams.setGoodseedDataset(jsonKey, goodseedHtmlNoXssDataset);

							} else {
								if(jsonKey.equals("page")) {
									page = jsonValue.toString();
									continue;
								}
								if(jsonKey.equals("rows")) {
									rows = jsonValue.toString();
									continue;
								}
								inParams.setVariable(jsonKey, jsonValue);
							}
						}

					}
				}

			} catch(Exception e) {
				LOG.error("exception", e);
				e.printStackTrace();
			}
		}

		//page 와 rows 정보가 있는 경우 startPage 와 endPage 정보를 HtmlParameters 에 넣는다.
		if(page != null && rows != null) {
			int iPage = Integer.parseInt(page);
			int iRows = Integer.parseInt(rows);
			int startPage = ((iPage - 1) * iRows) + 1;
			int endPage = iPage * iRows;
			inParams.setVariable(GoodseedConstants.START_ROW, startPage);
			inParams.setVariable(GoodseedConstants.END_ROW, endPage);

		}

		return inParams;
	}

	/**
	 * MethodParameter 타입이 Parameters 타입인지 여부 
	 * Parameters 타입 인경우에 resolveArgument 호출
	 */
	@Override
	public boolean supportsParameter(MethodParameter arg0) {
		Class<?> paramType = arg0.getParameterType();
		return Parameters.class.isAssignableFrom(paramType);
	}

}
