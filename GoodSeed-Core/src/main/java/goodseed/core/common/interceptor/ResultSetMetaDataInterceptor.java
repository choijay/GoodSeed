package goodseed.core.common.interceptor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.utility.config.Config;

//import org.springframework.util.StopWatch;

/**
 * The class ResultSetMetaDataInterceptor<br>
 * <br> ResultSetMetaDataInterceptor 를 통해서 resultSetMetaData 정보 로깅
 * <br>
 * Copyright (c) 2016 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 3.0
 * @since 3.5
 * 
 */

@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class ResultSetMetaDataInterceptor implements Interceptor {

	private static final Log LOG = LogFactory.getLog(ResultSetMetaDataInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		/**
		 * resultSetMetaData 가져오는 부분으로써 환경설정으로 true 일 경우에만
		 * 가져오도록 한다. 
		 * 
		 */
		if("true".equalsIgnoreCase(Config.getString("resultMetaData.enable"))) {
			getResultSetMetaData(invocation);
		}

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object result = invocation.proceed();

		stopWatch.stop();

		return result;
	}

	private void getResultSetMetaData(Invocation invocation) throws SQLException {
		Object[] args = invocation.getArgs();
		Statement statement = (Statement)args[0];
		ResultSet rs = statement.getResultSet();

		if(rs != null) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			Object[] metaData = new Object[columnCount];
			for(int i = 1; i <= columnCount; i++) {
				Map columnMeta = new LinkedHashMap();

				String columnName = rsmd.getColumnName(i);
				int columnDisplaySize = rsmd.getColumnDisplaySize(i);
				String columnClassName = rsmd.getColumnClassName(i);
				int precision = rsmd.getPrecision(i);
				int scale = rsmd.getScale(i);

				columnMeta.put("columnName", columnName);
				columnMeta.put("columnDisplaySize", columnDisplaySize);
				columnMeta.put("columnClassName", columnClassName);
				columnMeta.put("precision", precision);
				columnMeta.put("scale", scale);

				metaData[i - 1] = columnMeta;
			}

			/**
			 * HttpServletRequest 에 담아둔다.
			 */
			ServletRequestAttributes servletRequestAttributes =
					(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
			if(servletRequestAttributes != null) {
				HttpServletRequest curRequest = servletRequestAttributes.getRequest();
				curRequest.setAttribute(GoodseedConstants.RESULTSET_METADATA, metaData);
			}

			if(LOG.isDebugEnabled()) {
				LOG.debug("metaData : " + metaData.toString());
			}

		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("resultset is null");
			}
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

}
