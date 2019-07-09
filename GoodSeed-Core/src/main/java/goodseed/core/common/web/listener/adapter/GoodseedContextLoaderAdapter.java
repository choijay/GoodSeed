/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.web.listener.adapter;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import goodseed.core.common.utility.AllowedURIListUtil;
import goodseed.core.observer.ConfigCheck;
import goodseed.core.observer.GoodseedAnnotationFinder;
import goodseed.core.utility.config.Config;

/**
 * The class GoodSeedContextLoaderAdapter
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class GoodseedContextLoaderAdapter extends DefaultContextLoaderAdapter {

	private static final Log LOG = LogFactory.getLog(GoodseedContextLoaderAdapter.class);

	/**
	 * servletcontext initialize 마지막에 실행
	 */
	@Override
	public void afterInitialize(ServletContext context) {
		
		// WebApplicationContext initialize.
		setWebApplicationContext(WebApplicationContextUtils.getWebApplicationContext(context));

		// Find Annotation
		GoodseedAnnotationFinder finder = new GoodseedAnnotationFinder(context);
		List<Object> objList = finder.find(ConfigCheck.class);
		Config.loadConfig(objList);

//		//라이센스 체크
//		try {
//			LicenseManager.check();
//		} catch(Exception e) {
//			LOG.error("exception", e);
//			if(LOG.isErrorEnabled()) {
//				LOG.error("ERROR: Failed to pass the license check");
//			}
//
//			//System.exit(1);
//		}
//
//		//시스템 버전 체크 후  버전 체크 모니터 스레드 기동
//		VersionUtil.loadVersion();
//
//		//차단서비스목록파일 로딩 후 목록파일 갱신 모니터 스레드 기동
//		BlockServiceListUtil.loadBlockServiceList();
//
		//권한체크예외URI 목록파일 로딩 후 목록파일 갱신 모니터 스레드 기동
		AllowedURIListUtil.loadAllowedURIList();
//
//		//APC(Adaptive Performance Control) 초기화
//		ExploreSystemResourceUtil.initialize();
//
//		//ENI
//		EniUtil.loadEniServiceList();
//		EniUtil.loadEniEmpNoList();
//
//		//Service 갱신 모니터 스레드 기동
//		ServiceUtil.loadService();
//
//		//웹 서비스 초기화
//		WebserviceBeanLoader.initialized();
//
//		//웹 서비스 오퍼레이션 별 통계 정보
//		WebserviceStatisticsLogging.start();
//
//		//모바일 푸시 대량전송용 Queue 기동
//		MobilePushQueue.loadQueue();
	}

	/**
	 * servletcontext destory 하기 전에 실행됨.
	 */
	@Override
	public void beforeDestroy(ServletContext context) {

		super.beforeDestroy(context);

		//각종 Timer를 종료 및 자원 해제를 수행한다.
		//설정파일 갱신 체크 타이머
		Config.cancelConfigChangeCheckTimer();
		
//		//버전 체크 타이머
//		VersionUtil.cancelVersionTimer();
//		//차단서비스목록파일 갱신 체크 타이머
//		BlockServiceListUtil.cancelBlockServiceListCheckTimer();
		//권한체크예외URI목록파일 갱신 체크 타이머
		AllowedURIListUtil.cancelAllowedURIListCheckTimer();
//		//시스템 자원 조회 모니터 타이머 종료 및 기타 자원 해제
//		ExploreSystemResourceUtil.destroy();
//		EniUtil.cancelEniServiceListCheckTimer();
//		EniUtil.cancelEniEmpNoListCheckTimer();
//
//		//웹 서비스 갱신 체크 타이머 종료
//		WebserviceBeanLoader.cancelWebserviceChangeCheckTimer();
//		//웹 서비스 오퍼레이션 별 통계 기록 타이머 종료
//		WebserviceStatisticsLogging.cancelStatisticsChangeCheckTimer();

	}
}
