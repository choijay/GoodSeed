/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.utility;

import java.util.Map;

import goodseed.core.common.service.LoggingService;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;

/**
 * 
 * The class LoggingException2DB
 *
 * @author jay
 * @version 1.0
 *
 */
public class LoggingException2DB {

	/**
	 * TODO: 이 코드가 항상 loggingService가 initialized 된 이후에 호출된다고 보장할 수 있는가?
	 */
	private static LoggingService loggingService = (LoggingService)GoodseedContextLoaderAdapter.getBean("loggingService");

	/**
	 * Gets the loggingService
	 *
	 * @return the loggingService
	 */
	public static LoggingService getLoggingService() {
		return loggingService;
	}

	/**
	 * Set the loggingService
	 *
	 * @param loggingService the loggingService to set
	 */
	public static void setLoggingService(LoggingService loggingService) {
		LoggingException2DB.loggingService = loggingService;
	}

	/**
	 * 에러가 발생한 시점에 object, exception 정보를 DB에 남긴다.
	 *
	 * @param object - TODO : @param argument is not a parameter name.
	 * @param exception
	 */
	public static void loggingError2DB(Map logMap) {
		loggingService.loggingError2DB(logMap);
	}
}
