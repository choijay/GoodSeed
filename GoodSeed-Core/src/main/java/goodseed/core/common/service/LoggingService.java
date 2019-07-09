package goodseed.core.common.service;

import java.util.Map;

import goodseed.core.common.model.Parameters;

/**
 * 로깅을 위한 인터페이스
 * 
 * loggingError2DB => 에러가 발생한 시점에 object, exception 정보를 DB에 남긴다.
 * saveServiceLog => 서비스 호출 시 로그를 기록하도록 한다.
 */
public interface LoggingService {

	void loggingError2DB(Map logMap);

	void saveServiceLog(Parameters inParams);
}
