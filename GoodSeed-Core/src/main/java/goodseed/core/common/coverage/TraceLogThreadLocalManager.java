/**
 * Copyright (c) 2015 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 */
package goodseed.core.common.coverage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NamedThreadLocal;

/**
 * The class TraceLogThreadLocalManager<br>
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
public class TraceLogThreadLocalManager {

	private static final Log LOG = LogFactory.getLog(TraceLogThreadLocalManager.class);

	private static ThreadLocal<TraceLogInfo> traceLogThreadLocal = new NamedThreadLocal<TraceLogInfo>(
			"Trace Log Info ThreadLocal");

	public static void setTraceLogInfo(TraceLogInfo traceLogInfo) {
		traceLogThreadLocal.set(traceLogInfo);
	}

	public static TraceLogInfo getTraceLogInfo() {
		if(traceLogThreadLocal.get() == null) {
			traceLogThreadLocal.set(new TraceLogInfo());
		}
		return traceLogThreadLocal.get();
	}

	public static void clear() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("ThreadLocal clear is called!!!");
		}
		traceLogThreadLocal.remove();
	}
}
