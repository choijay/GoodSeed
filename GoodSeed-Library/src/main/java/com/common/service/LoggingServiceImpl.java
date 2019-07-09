/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of Systems
 */
package com.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import goodseed.core.common.model.Parameters;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.common.service.LoggingService;

/**
 * The class LoggingService
 *
 * @author jay
 * @version 1.0
 *
 */
@Service
public class LoggingServiceImpl implements LoggingService {

	/*
	 * UserHandleException이 아닌 Exception 발생시 에러 로그 기록
	 * 	- MiPlatformErrorHandlingFilter, HtmlErrorHandlingFilter 등에서 호출한다.
	 * 
	 * @param logMap
	 */
	public void loggingError2DB(Map logMap) {
		SqlManagerFactory.getSqlManager().insert(logMap, "logging.loggingError2DB");
	}

	/**
	 * 서비스 호출시 로그 기록
	 * 
	 * @param inParams
	 */
	public void saveServiceLog(Parameters inParams) {

		/*
		 *	각 사이트별로 서비스 호출 로그 테이블에 추가적으로 기록하고자 하는 데이터가 존재할 경우,
		 *	inParams.setVariable() 을 사용하여 쿼리 파라미터를 추가하도록 처리한다.
		 *	일반적인 request 파라미터들은 inParams 안에 모두 담겨져 있는 상태이다.
		 */

		SqlManagerFactory.getSqlManager().insert(inParams, "logging.saveServiceLog");
	}
}
