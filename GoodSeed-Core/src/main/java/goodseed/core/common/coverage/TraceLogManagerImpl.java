/**
 * Copyright (c) 2015 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 */
package goodseed.core.common.coverage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.coverage.model.TraceLogParameters;
import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.utility.config.Config;

/**
 * The class TraceLogManagerImpl<br>
 * <br>
 * TODO 설명을 상세히 입력하세요.<br>
 * <br>
 * <br>
 * Copyright (c) 2015 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 2.0
 * @since  7. 15.
 *
 */
public class TraceLogManagerImpl implements TraceLogManager {

	private static final Log LOG = LogFactory.getLog(TraceLogManagerImpl.class);

	/* TraceLog AOP를 통해 획득한 정보를 "FRAMEONE_FCC_TRAN", "FRAMEONE_FCC_ANAL"에 저장한다.
	 * @see frameone.core.common.coverage.TraceLogManager#wirteLog(frameone.core.common.coverage.TraceLogInfo)
	 */
	@Override
	public void wirteTraceLog() {

		String opMode = Config.getString("operation.mode").toUpperCase();

		TraceLogInfo traceLogInfo = TraceLogThreadLocalManager.getTraceLogInfo();
		List<TraceLogParameters> traceLogList = traceLogInfo.getTraceLogInfoList();

		SqlManager sqlManager = null;

		sqlManager = SqlManagerFactory.getSqlManager("frameoneSqlManager");
		List<Map<String, String>> rtnList = sqlManager.queryForList("traceMetaInfo.getSystemHistCd");
		Iterator rtnIterator = rtnList.iterator();

		Map<String, String> rtnVal = null;
		String SYSTEM_HIST_CD = "";
		while(rtnIterator.hasNext()) {
			rtnVal = (HashMap)rtnIterator.next();
			SYSTEM_HIST_CD = rtnVal.get("COMM_CD");
		}

		sqlManager = SqlManagerFactory.getSqlManager("frameoneSqlManagerFCC");

		for(TraceLogParameters rowData : traceLogList) {

			Map<String, Object> traceInfoMap = new HashMap<String, Object>();

			traceInfoMap.put(TraceLogInfoConstant.SYSTEM_CL_CD, opMode);
			traceInfoMap.put(TraceLogInfoConstant.SYSTEM_HIST_CD, SYSTEM_HIST_CD);
			traceInfoMap.put(TraceLogInfoConstant.TRAN_ID, rowData.getTRAN_ID());
			traceInfoMap.put(TraceLogInfoConstant.CALLEE_HASH_VAL, rowData.getCALLEE_HASH_VAL());
			traceInfoMap.put(TraceLogInfoConstant.CALLER_HASH_VAL, rowData.getCALLER_HASH_VAL());
			traceInfoMap.put(TraceLogInfoConstant.CALLEE_PKG_NM, rowData.getCALLEE_PKG_NM());
			traceInfoMap.put(TraceLogInfoConstant.CALLEE_METHOD_NM, rowData.getCALLEE_METHOD_NM());
			traceInfoMap.put(TraceLogInfoConstant.CALLER_PKG_NM, rowData.getCALLER_PKG_NM());
			traceInfoMap.put(TraceLogInfoConstant.CALLER_METHOD_NM, rowData.getCALLER_METHOD_NM());
			traceInfoMap.put(TraceLogInfoConstant.CALLEE_TYPE, rowData.getCALLEE_TYPE());

			if(LOG.isDebugEnabled()) {
				LOG.debug("## traceInfoMap" + traceInfoMap);
			}

			sqlManager.insert(traceInfoMap, "traceLog.insertTraceLogAnalList");
			sqlManager.insert(traceInfoMap, "traceLog.insertTraceLogTranList");
		}

	}

}
