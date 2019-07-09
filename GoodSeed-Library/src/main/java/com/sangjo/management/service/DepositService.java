/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.sangjo.management.service;

import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;

/**
 * The class DepositService
 * <br>
 * 예치금 관리 Service<br>
 * <br>
 * 
 * @author jay
 *
 */
@Service
public class DepositService extends GoodseedService {

	/**
	 * 예치금 리스트 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 06.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getDepositList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_depositList", getSqlManager().queryForGoodseedDataset(inParams, "deposit.getDepositList"));
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "deposit.getDepositListTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_depositList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}
	
	

}
