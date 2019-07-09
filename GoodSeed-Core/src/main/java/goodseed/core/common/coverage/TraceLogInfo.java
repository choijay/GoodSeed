/**
 * Copyright (c) 2015 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 */
package goodseed.core.common.coverage;

import java.util.ArrayList;
import java.util.List;

import goodseed.core.common.coverage.model.TraceLogParameters;
import goodseed.core.utility.uuid.util.UUID;

/**
 * The class TraceLogInfo<br>
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
public class TraceLogInfo {

	private final String uuid;

	public TraceLogInfo() {
		uuid = String.valueOf(new UUID());
	}

	public String getUuid() {
		return uuid;
	}

	private List<TraceLogParameters> traceLogInfoList = new ArrayList<TraceLogParameters>();

	public List<TraceLogParameters> getTraceLogInfoList() {
		return traceLogInfoList;
	}

	public void setTraceLogInfoList(List<TraceLogParameters> traceLogInfoList) {
		this.traceLogInfoList = traceLogInfoList;
	}

	public void remove() {
		traceLogInfoList.clear();
	}
}
