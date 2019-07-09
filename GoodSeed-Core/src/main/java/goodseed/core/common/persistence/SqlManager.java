/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.handler.CommonCsvHandler;
import goodseed.core.common.handler.CommonExcelHandler;
import goodseed.core.common.handler.CommonNewExcelHandler;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.AbstractDatasetFactory;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;
import goodseed.core.exception.UserHandleException;
import goodseed.core.observer.ConfigObserver;
import goodseed.core.utility.config.Config;

/**
 * The class SqlManager
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class SqlManager extends SqlSessionDaoSupport implements ConfigObserver {

	private static final Log LOG = LogFactory.getLog(SqlManager.class);

	//문자열 리터럴 중복 사용 제거 (20130522 Sonar 처리)
	public static final String TRANSACTION_MANAGER = "transactionManager";
	private static int defaultMaxRows;

	static {
		defaultMaxRows = Config.getInteger("defaultMaxRows", Integer.MAX_VALUE - 1);
	}

	@Autowired(required = false)
	@Override
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param params GoodseedDataset and variables.
	 * @param sqlId query id.
	 * @return Dataset
	 */
	public GoodseedDataset queryForGoodseedDataset(Parameters params, String sqlId) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		List result = getSqlSession().selectList(sqlId, params, new RowBounds(0, defaultMaxRows + 1));
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		if(result.size() > defaultMaxRows) {
			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
					new String[]{Integer.toString(defaultMaxRows)});
		}
		return AbstractDatasetFactory.getDatasetFactoryByParameters(params).makeDataset(result);
	}

	/**
	 * SELECT 공통 메소드.(데이터셋명을 입력받음)
	 *
	 * @param params GoodseedDataset and variables.
	 * @param sqlId query id.
	 * @return Dataset
	 */
//	public GoodseedDataset queryForFrameOneDataset(Parameters params, String sqlId, String datasetName) {
//
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//
//		List result = getSqlSession().selectList(sqlId, params, new RowBounds(0, defaultMaxRows + 1));
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		if(result.size() > defaultMaxRows) {
//			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
//					new String[]{Integer.toString(defaultMaxRows)});
//		}
//		return AbstractDatasetFactory.getDatasetFactoryByParameters(params).makeDataset(result, datasetName);
//	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param params GoodseedDataset and variables.
	 * @param sqlId query id.
	 * @param type Parameter type
	 * @return Dataset
	 */
//	public GoodseedDataset queryForFrameOneDataset(Map params, String sqlId, Parameters type) {
//
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//
//		List result = getSqlSession().selectList(sqlId, params, new RowBounds(0, defaultMaxRows + 1));
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		if(result.size() > defaultMaxRows) {
//			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
//					new String[]{Integer.toString(defaultMaxRows)});
//		}
//		return AbstractDatasetFactory.getDatasetFactoryByParameters(type).makeDataset(result);
//	}

	/**
	 * SELECT 공통 메소드.(데이터셋명을 입력받음)
	 *
	 * @param params GoodseedDataset and variables.
	 * @param sqlId query id.
	 * @param datasetName datasetName
	 * @param type Parameter type
	 * @return Dataset
	 */
//	public GoodseedDataset queryForFrameOneDataset(Map params, String sqlId, String datasetName, Parameters type) {
//
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//
//		List result = getSqlSession().selectList(sqlId, params, new RowBounds(0, defaultMaxRows + 1));
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		if(result.size() > defaultMaxRows) {
//			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
//					new String[]{Integer.toString(defaultMaxRows)});
//		}
//		return AbstractDatasetFactory.getDatasetFactoryByParameters(type).makeDataset(result, datasetName);
//	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param dataset GoodseedDataset and variables.
	 * @param sqlId query id.
	 * @return Dataset
	 */
	public GoodseedDataset queryForGoodseedDataset(GoodseedDataset dataset, String sqlId) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List result = getSqlSession().selectList(sqlId, dataset, new RowBounds(0, defaultMaxRows + 1));
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		if(result.size() > defaultMaxRows) {
			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
					new String[]{Integer.toString(defaultMaxRows)});
		}
		return AbstractDatasetFactory.getDatasetFactory(dataset).makeDataset(result);
	}

	/**
	 * SELECT 공통 메소드.(데이터셋을 입력받음)
	 *
	 * @param dataset GoodseedDataset and variables.
	 * @param sqlId query id.
	 * @return Dataset
	 */
//	public GoodseedDataset queryForFrameOneDataset(GoodseedDataset dataset, String sqlId, String datasetName) {
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		List result = getSqlSession().selectList(sqlId, dataset, new RowBounds(0, defaultMaxRows + 1));
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		if(result.size() > defaultMaxRows) {
//			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
//					new String[]{Integer.toString(defaultMaxRows)});
//		}
//		return AbstractDatasetFactory.getDatasetFactory(dataset).makeDataset(result, datasetName);
//	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param sqlId query id.
	 * @return GoodseedDataset.
	 * @deprecated
	 */
//	@Deprecated
//	public GoodseedDataset queryForFrameOneDataset(String sqlId) {
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//
//		/**
//		 * getSqlSession().selectList API 는
//		 * 
//		 * selectList(String)
//		 * selectList(String, Object)
//		 * selectList(String, Object, RowBounds)
//		 * 
//		 * 이므로 Parameter Object 가 없는 경우에는 빈 객체를 넣어서 호출한다.
//		 */
//		// List result = getSqlSession().selectList(sqlId, new RowBounds(0, defaultMaxRows + 1));
//		List result = getSqlSession().selectList(sqlId, new HashMap(), new RowBounds(0, defaultMaxRows + 1));
//
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		if(result.size() > defaultMaxRows) {
//			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
//					new String[]{Integer.toString(defaultMaxRows)});
//		}
//		return AbstractDatasetFactory.getDefaultDatasetFactory().makeDataset(result);
//	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param sqlId query id.
	 * @return TODO
	 */
	public List queryForList(String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		/**
		 * getSqlSession().selectList API 는
		 * 
		 * selectList(String)
		 * selectList(String, Object)
		 * selectList(String, Object, RowBounds)
		 * 
		 * 이므로 Parameter Object 가 없는 경우에는 빈 객체를 넣어서 호출한다.
		 */
		// List result = getSqlSession().selectList(sqlId, new RowBounds(0, defaultMaxRows + 1));
		List result = getSqlSession().selectList(sqlId, new HashMap(), new RowBounds(0, defaultMaxRows + 1));

		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		if(result.size() > defaultMaxRows) {
			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
					new String[]{Integer.toString(defaultMaxRows)});
		}
		return result;
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param params GoodseedDataset and variables.
	 * @param sqlId query id.
	 * @return TODO
	 */
	public List queryForList(Parameters params, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List result = getSqlSession().selectList(sqlId, params, new RowBounds(0, defaultMaxRows + 1));
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		if(result.size() > defaultMaxRows) {
			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
					new String[]{Integer.toString(defaultMaxRows)});
		}
		return result;
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return TODO
	 */
	public List queryForList(Map mapData, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List result = getSqlSession().selectList(sqlId, mapData, new RowBounds(0, defaultMaxRows + 1));
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		if(result.size() > defaultMaxRows) {
			throw new UserHandleException(Config.getString("customVariable.msgComErr021"),
					new String[]{Integer.toString(defaultMaxRows)});
		}
		return result;
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param sqlId query id.
	 * @return TODO
	 */
	public Object queryForObject(String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object result = getSqlSession().selectOne(sqlId);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param params Parameters
	 * @param sqlId query id.
	 * @return TODO
	 */
	public Object queryForObject(Parameters params, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object result = getSqlSession().selectOne(sqlId, params);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return TODO
	 */
	public Object queryForObject(Map mapData, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object result = getSqlSession().selectOne(sqlId, mapData);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param frameOneDataset frameOneDataset
	 * @param sqlId query id.
	 * @return TODO
	 */
//	public Object queryForObject(GoodseedDataset frameOneDataset, String sqlId) {
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		Object result = getSqlSession().selectOne(sqlId, frameOneDataset);
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		return result;
//	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param sqlId
	 * @param rowHandler
	 */
	public void queryWithRowHandler(String sqlId, ResultHandler resultHandler) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		getSqlSession().select(sqlId, resultHandler);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
	}

	/**
	 * SELECT 공통 메소드.
	 *
	 * @param sqlId
	 * @param rowHandler
	 */
	public void queryWithRowHandler(Object params, String sqlId, ResultHandler resultHandler) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		getSqlSession().select(sqlId, params, resultHandler);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
	}

	/**
	 * 대량 데이터 조회용 메소드.<br>
	 * 수만건의 데이터를 사용자 화면에 출력해야 하는 특수한 업무 기능 구현 시 사용한다.<br>
	 * 이 메소드는 내부적으로 Disk I/O를 사용하며,<br>
	 * 빈번한 호출은 전체 시스템의 성능을 심각하게 저하시킬 수 있으므로 유의하여야 한다.<br>
	 * 또한, 대량 데이터 조회는 Database 시스템에도 치명적인 성능 문제를 유발할 수 있으므로,
	 * 대부분의 업무 기능은 Page 처리를 구현하여 사용하도록 하며,<br>
	 * 일부 특이한 경우의 업무에 이 메소드를 활용하도록 한다.<br>
	 * <br>
	 * 이 메소드 사용 시는 1개의 데이터셋만 반환할 수 있다.<br>
	 *
	 * @param inParams 입력 파라미터 객체.
	 * @param sqlId 실행할 SQL ID.
	 * @param outParams 반환할 출력 파라미터 객체. null이 입력되어서는 안되며,<br>Service 메소드 반환 시 반드시 여기에 입력했던 객체를 반환하여야 한다.
	 * @param datasetName 반환할 데이터셋 이름.
	 * @return Service 메소드가 반환할 파라미터 객체
	 */
//	public Parameters queryForLargeDataset(Object inParams, String sqlId, Parameters outParams, String datasetName) {
//
//		GoodseedLargeDataHandler rowHandler = null;
//
//		if(GoodseedConstants.MIPLATFORM_PARAMETERS.equals(outParams.getClass().getName())) {
//			rowHandler = new CommonLargeDataRowHandler(datasetName);
//		} else if(GoodseedConstants.EXT_PARAMETERS.equals(outParams.getClass().getName())) {
//			rowHandler = new CommonLargeExtDataRowHandler(datasetName);
////		} else {
////			throw new SystemException(GoodseedDataset.UNSUPPORTED_METHOD);
//		}
//
//		rowHandler.setParams(outParams);
//		try {
//			queryWithRowHandler(inParams, sqlId, rowHandler);
//		} finally {
//			rowHandler.close();
//		}
//		return outParams;
//	}

	/**
	 * SELECT 공통 메소드 (대용량 엑셀 )
	 *
	 * @param params
	 * @param excelHandler 엑셀 로우 핸들러
	 * @param sqlId
	 * @return TODO
	 */
	public Parameters queryForExcelDownload(Object params, CommonExcelHandler excelHandler, String sqlId) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		getSqlSession().select(sqlId, params, excelHandler);
		excelHandler.close();
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);

		return excelHandler.getOutParams();
	}

	/**
	 * SELECT 공통 메소드 (대용량 엑셀 )
	 *
	 * @param params
	 * @param excelHandler 엑셀 로우 핸들러
	 * @param sqlId
	 * @return TODO
	 */
	public Parameters queryForNewExcelDownload(Object params, CommonNewExcelHandler excelHandler, String sqlId) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		getSqlSession().select(sqlId, params, excelHandler);
		excelHandler.close();
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);

		return excelHandler.getOutParams();
	}

	/**
	 * SELECT 공통 메소드(대용량 csv)
	 *
	 * @param params
	 * @param csvHandler csv 로우 핸들러
	 * @param sqlId
	 * @return TODO
	 */
	public Parameters queryForCsvDownload(Object params, CommonCsvHandler csvHandler, String sqlId) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		getSqlSession().select(sqlId, params, csvHandler);
		csvHandler.close();
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);

		return csvHandler.getOutParams();

	}

	/**
	 * 총 개수를 가져온다.
	 *
	 * @param sqlId query id.
	 * @return TODO
	 */
	public int getTotalCount(String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Integer result = (Integer)getSqlSession().selectOne(sqlId);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * 총 개수를 가져온다.
	 *
	 * @param params GoodseedDataset and variable.
	 * @param sqlId query id.
	 * @return TODO
	 */
	public int getTotalCount(Parameters params, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Integer result = (Integer)getSqlSession().selectOne(sqlId, params);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * DELETE 공통 메소드.
	 *
	 * @param frameOneDataset frameOneDataset.
	 * @param sqlId delete query id.
	 * @return 삭제된 행의 갯수
	 */
//	public int delete(GoodseedDataset frameOneDataset, String sqlId) {
//		int deleteCount = 0;
//		try {
//			frameOneDataset.setDelete(true);
//			StopWatch stopWatch = new StopWatch();
//			stopWatch.start();
//			deleteCount = getSqlSession().delete(sqlId, frameOneDataset);
//			stopWatch.stop();
//			loggingElapsedTime(stopWatch, sqlId);
//		} catch(RuntimeException e) {
//			LOG.error("exception", e);
//			throw e;
//		} finally {
//			frameOneDataset.setDelete(false);
//		}
//		return deleteCount;
//	}

	/**
	 * DELETE 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return 삭제된 행의 갯수
	 */
	public int delete(Map mapData, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int result = getSqlSession().delete(sqlId, mapData);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * DELETE 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return 삭제된 행의 갯수
	 */
	public int delete(String sqlId, Map mapData) {
		return delete(mapData, sqlId);
	}

	/**
	 * DELETE 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return 삭제된 행의 갯수
	 */
	public int delete(String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int result = getSqlSession().delete(sqlId);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * 삭제된 ROW의 데이터로 UPDATE 실행.
	 *
	 * @param frameOneDataset
	 * @param sqlId update query id.
	 * @return update된 행의 갯수
	 */
//	public int updateDeleted(GoodseedDataset frameOneDataset, String sqlId) {
//		int updateCount = 0;
//		try {
//			frameOneDataset.setDelete(true);
//			StopWatch stopWatch = new StopWatch();
//			stopWatch.start();
//			updateCount = getSqlSession().update(sqlId, frameOneDataset);
//			stopWatch.stop();
//			loggingElapsedTime(stopWatch, sqlId);
//		} catch(RuntimeException e) {
//			LOG.error("exception", e);
//			throw e;
//		} finally {
//			frameOneDataset.setDelete(false);
//		}
//		return updateCount;
//	}

	/**
	 * UPDATE 공통 메소드.
	 *
	 * @param frameOneDataset GoodseedDataset
	 * @param sqlId update query id.
	 * @return update된 행의 갯수
	 */
//	public int update(GoodseedDataset frameOneDataset, String sqlId) {
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		int result = getSqlSession().update(sqlId, frameOneDataset);
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		return result;
//	}

	/**
	 * UPDATE 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return update된 행의 갯수
	 */
	public int update(Map mapData, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int result = getSqlSession().update(sqlId, mapData);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * UPDATE 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return
	 */
	public int update(String sqlId, Map mapData) {
		return update(mapData, sqlId);
	}

	/**
	 * INSERT 공통 메소드.
	 *
	 * @param frameOneDataset
	 * @param sqlId
	 * @return insert된 행의 pk -> insert된 행의 갯수 (myBatis 에서 변경)
	 */
//	public int insert(GoodseedDataset frameOneDataset, String sqlId) {
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		int result = getSqlSession().insert(sqlId, frameOneDataset);
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//		return result;
//	}

	/**
	 * INSERT 공통 메소드
	 *
	 * @param mapData
	 * @param sqlId
	 * @return insert된 행의 pk -> insert된 행의 갯수 (myBatis 에서 변경)
	 */
	public int insert(Map mapData, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		int result = getSqlSession().insert(sqlId, mapData);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
		return result;
	}

	/**
	 * INSERT Required New 공통 메소드.
	 *
	 * @param frameOneDataset
	 * @param sqlId
	 * @param transactionMgr - transactionManager 이외의 다른 매니저를 사용할 경우	 * 
	 * @return
	 */
//	public Object insertRequiresNew(final GoodseedDataset frameOneDataset, final String sqlId) {
//		return insertRequiresNew(frameOneDataset, sqlId, TRANSACTION_MANAGER);
//	}

	/**
	 * INSERT Required New 공통 메소드.
	 *
	 * @param frameOneDataset
	 * @param sqlId
	 * @param transactionMgr - transactionManager 이외의 다른 매니저를 사용할 경우	 * 
	 * @return
	 */
//	public Object insertRequiresNew(final GoodseedDataset frameOneDataset, final String sqlId, String transactionMgr) {
//
//		PlatformTransactionManager transactionManager =
//				(PlatformTransactionManager)GoodseedContextLoaderAdapter.getBean(transactionMgr);
//		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
//		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//		return transactionTemplate.execute(new TransactionCallback<Object>() {
//
//			public Object doInTransaction(TransactionStatus status) {
//				StopWatch stopWatch = new StopWatch();
//				stopWatch.start();
//				Object result = getSqlSession().insert(sqlId, frameOneDataset);
//				stopWatch.stop();
//				loggingElapsedTime(stopWatch, sqlId);
//				return result;
//			}
//		});
//
//	}

	/**
	 * INSERT Required New 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return
	 */
	public Object insertRequiresNew(final Map mapData, final String sqlId) {
		return insertRequiresNew(mapData, sqlId, TRANSACTION_MANAGER);
	}

	/**
	 * INSERT Required New 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @param transactionMgr - transactionManager 이외의 다른 매니저를 사용할 경우	 * 
	 * @return
	 */
	public Object insertRequiresNew(final Map mapData, final String sqlId, String transactionMgr) {

		PlatformTransactionManager transactionManager =
				(PlatformTransactionManager)GoodseedContextLoaderAdapter.getBean(transactionMgr);
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return transactionTemplate.execute(new TransactionCallback<Object>() {

			public Object doInTransaction(TransactionStatus status) {
				StopWatch stopWatch = new StopWatch();
				stopWatch.start();
				Object result = getSqlSession().insert(sqlId, mapData);
				stopWatch.stop();
				loggingElapsedTime(stopWatch, sqlId);
				return result;
			}
		});

	}

	/**
	 * UPDATE Required New 공통 메소드.
	 *
	 * @param frameOneDataset
	 * @param sqlId
	 * @return
	 */
//	public Object updateRequiresNew(final GoodseedDataset frameOneDataset, final String sqlId) {
//		return updateRequiresNew(frameOneDataset, sqlId, TRANSACTION_MANAGER);
//	}

	/**
	 * UPDATE Required New 공통 메소드.
	 *
	 * @param frameOneDataset
	 * @param sqlId
	 * @param transactionMgr - transactionManager 이외의 다른 매니저를 사용할 경우	 * 
	 * @return
	 */
//	public Object updateRequiresNew(final GoodseedDataset frameOneDataset, final String sqlId, String transactionMgr) {
//
//		PlatformTransactionManager transactionManager =
//				(PlatformTransactionManager)GoodseedContextLoaderAdapter.getBean(transactionMgr);
//		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
//		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//		return transactionTemplate.execute(new TransactionCallback<Object>() {
//
//			public Object doInTransaction(TransactionStatus status) {
//				StopWatch stopWatch = new StopWatch();
//				stopWatch.start();
//				Object result = getSqlSession().update(sqlId, frameOneDataset);
//				stopWatch.stop();
//				loggingElapsedTime(stopWatch, sqlId);
//				return result;
//			}
//		});
//
//	}

	/**
	 * UPDATE Required New 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return
	 */
	public Object updateRequiresNew(final Map mapData, final String sqlId) {
		return updateRequiresNew(mapData, sqlId, TRANSACTION_MANAGER);
	}

	/**
	 * UPDATE Required New 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @param transactionMgr - transactionManager 이외의 다른 매니저를 사용할 경우
	 * @return
	 */
	public Object updateRequiresNew(final Map mapData, final String sqlId, String transactionMgr) {

		PlatformTransactionManager transactionManager =
				(PlatformTransactionManager)GoodseedContextLoaderAdapter.getBean(transactionMgr);
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return transactionTemplate.execute(new TransactionCallback<Object>() {

			public Object doInTransaction(TransactionStatus status) {
				StopWatch stopWatch = new StopWatch();
				stopWatch.start();
				Object result = getSqlSession().update(sqlId, mapData);
				stopWatch.stop();
				loggingElapsedTime(stopWatch, sqlId);
				return result;
			}
		});

	}

	/**
	 * DELETE Required New 공통 메소드.
	 *
	 * @param frameOneDataset
	 * @param sqlId
	 * @return
	 */
//	public Object deleteRequiresNew(final GoodseedDataset frameOneDataset, final String sqlId) {
//		return deleteRequiresNew(frameOneDataset, sqlId, TRANSACTION_MANAGER);
//	}

	/**
	 * DELETE Required New 공통 메소드.
	 *
	 * @param frameOneDataset
	 * @param sqlId
	 * @param transactionMgr - transactionManager 이외의 다른 매니저를 사용할 경우	 * 
	 * @return
	 */
//	public Object deleteRequiresNew(final GoodseedDataset frameOneDataset, final String sqlId, String transactionMgr) {
//
//		PlatformTransactionManager transactionManager =
//				(PlatformTransactionManager)GoodseedContextLoaderAdapter.getBean(transactionMgr);
//		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
//		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//		return transactionTemplate.execute(new TransactionCallback<Object>() {
//
//			public Object doInTransaction(TransactionStatus status) {
//				StopWatch stopWatch = new StopWatch();
//				stopWatch.start();
//				Object result = getSqlSession().delete(sqlId, frameOneDataset);
//				stopWatch.stop();
//				loggingElapsedTime(stopWatch, sqlId);
//				return result;
//			}
//		});
//
//	}

	/**
	 * DELETE Required New 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @return
	 */
	public Object deleteRequiresNew(final Map mapData, final String sqlId) {
		return deleteRequiresNew(mapData, sqlId, TRANSACTION_MANAGER);
	}

	/**
	 * DELETE Required New 공통 메소드.
	 *
	 * @param mapData
	 * @param sqlId
	 * @param transactionMgr - transactionManager 이외의 다른 매니저를 사용할 경우	 * 
	 * @return
	 */
	public Object deleteRequiresNew(final Map mapData, final String sqlId, String transactionMgr) {

		PlatformTransactionManager transactionManager =
				(PlatformTransactionManager)GoodseedContextLoaderAdapter.getBean(transactionMgr);
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		return transactionTemplate.execute(new TransactionCallback<Object>() {

			public Object doInTransaction(TransactionStatus status) {
				StopWatch stopWatch = new StopWatch();
				stopWatch.start();
				Object result = getSqlSession().delete(sqlId, mapData);
				stopWatch.stop();
				loggingElapsedTime(stopWatch, sqlId);
				return result;
			}
		});

	}

	/**
	 * Batch용 SqlSession 리턴
	 * @return
	 */
	public SqlSession getBatchSqlSession() {
		SqlSessionFactory sqlSessionFactory = ((SqlSessionTemplate)getSqlSession()).getSqlSessionFactory();
		return sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		// 		return new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
	}

	/**
	 * Batch Insert 공통 메소드.
	 * 
	 * @param sqlSession
	 * @param frameOneDataset
	 * @param sqlId
	 * @return
	 */
//	public void insertBatch(SqlSession sqlSession, GoodseedDataset frameOneDataset, String sqlId) {
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		sqlSession.insert(sqlId, frameOneDataset);
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//	}

	/**
	 * Batch Insert 공통 메소드.
	 * 
	 * @param sqlSession
	 * @param mapData
	 * @param sqlId
	 */
	public void insertBatch(SqlSession sqlSession, Map mapData, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		sqlSession.insert(sqlId, mapData);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
	}

	/**
	 * Batch Update 공통 메소드.
	 * 
	 * @param sqlSession
	 * @param frameOneDataset
	 * @param sqlId
	 * @return
	 */
//	public void updateBatch(SqlSession sqlSession, GoodseedDataset frameOneDataset, String sqlId) {
//		StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		sqlSession.update(sqlId, frameOneDataset);
//		stopWatch.stop();
//		loggingElapsedTime(stopWatch, sqlId);
//	}

	/**
	 * Batch Update 공통 메소드.
	 * 
	 * @param sqlSession
	 * @param mapData
	 * @param sqlId
	 */
	public void updateBatch(SqlSession sqlSession, Map mapData, String sqlId) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		sqlSession.update(sqlId, mapData);
		stopWatch.stop();
		loggingElapsedTime(stopWatch, sqlId);
	}

	/**
	 * Batch Flush
	 * 
	 * BatchExecutor를 이용하면 flushStatements() 가 호출되어 세션이 끝날 때 까지 
	 * DB로 보내지지 않고 모든 업데이트 문이 캐시된다.
	 * 
	 * 따라서, 얼마나 많은 레코드가 바뀌었는지 알 수 없기 때문에 유효하지 않은 범위의 음수를 리턴한다.
	 * insert/update/delete 어디에서든 affected rows 대신 -2147482646을 리턴한다.
	 * 
	 * @param sqlSession
	 * @return
	 */
	public List<BatchResult> flushStatements(SqlSession sqlSession) {
		return sqlSession.flushStatements();
	}

	/**
	 * 배치 SqlSession commit
	 * @param sqlSession
	 */
	public void batchCommit(SqlSession sqlSession) {
		sqlSession.commit();
	}

	/**
	 * 배치 SqlSession commit
	 * @param sqlSession
	 */
	public void batchRollback(SqlSession sqlSession) {
		sqlSession.rollback();
	}

	/**
	 * 배치 SqlSession close
	 * @param sqlSession
	 */
	public void batchClose(SqlSession sqlSession) {
		sqlSession.clearCache();
		sqlSession.close();
	}

	/**
	 * 시작 row 와 끝 row 를 계산해서 map 에 담는다. (startRow, endRow)
	 *
	 * @param params
	 */
	@SuppressWarnings("unused")
	private void putStartEndRow(Map params) {
		int perPage = 10;
		int pageNo = 1;
		if(params.get(GoodseedConstants.PER_PAGE) != null && params.get(GoodseedConstants.PAGE_NO) != null) {
			perPage = parseInt(params.get(GoodseedConstants.PER_PAGE).toString());
			pageNo = parseInt(params.get(GoodseedConstants.PAGE_NO).toString());
			
			params.put(GoodseedConstants.START_ROW, (pageNo - 1) * perPage + 1);
			params.put(GoodseedConstants.END_ROW, ((pageNo - 1) * perPage + 1) + perPage - 1);
		}
	}

	/**
	 * String 을 int 로 변환.
	 *
	 * @param str
	 * @return Integer로 변환된 입력 문자열
	 */
	private int parseInt(String str) {
		if(StringUtils.isNotEmpty(str) && StringUtils.isNumeric(str)) {
			return Integer.parseInt(str);
		} else {
			return 0;
		}
	}

	/**
	 * Query elapsed time logging.
	 *
	 * @param stopWatch
	 * @param sqlId
	 */
	private void loggingElapsedTime(StopWatch stopWatch, String sqlId) {
		/* logging elapsed time */
		if(LOG.isInfoEnabled()) {
			StringBuilder elapsedTime = new StringBuilder("Sql [");
			elapsedTime.append(sqlId).append("] elapsed time : ");
			elapsedTime.append(stopWatch.getTotalTimeMillis()).append(" ms.");
			LOG.info(elapsedTime);
		}
	}

	@Override
	public String toString() {
		//		return "DS = " + this.getDataSource().toString() + ", SqlMapClient = " + this.getSqlMapClient();
		return "SqlSession = " + this.getSqlSession();
	}

	/**
	 * applicationProperties 내 환경변수가 변경되었을 때 변수 업데이트
	 */
	@Override
	public void update() {
		defaultMaxRows = Config.getInteger("defaultMaxRows", Integer.MAX_VALUE - 1);
		if(LOG.isInfoEnabled()) {
			LOG.info("DefaultMaxRows : " + this.defaultMaxRows);
		}
	}
	
}
