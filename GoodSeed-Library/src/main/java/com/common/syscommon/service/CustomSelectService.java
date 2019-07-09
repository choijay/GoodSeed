package com.common.syscommon.service;

import java.util.List;

import org.springframework.stereotype.Service;

import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;

/**
 *
 * The class CommonSelectService
 *
 * @author jay
 * @version 1.0
 * @since   2015. 6. 24.
 *
 */
@Service
public class CustomSelectService extends GoodseedService {

	/**
	 * 콤보생성 Data를 조회한다. 
	 *
	 * @param inParam
	 * @return
	 */
	public List getSelectList(Parameters inParams) {
		return getSqlManager().queryForList(inParams, "customSelectService." + inParams.getVariableAsString("sqlId"));
	}
	
	/**
	 * 
	 * 동적콤보 Data를 조회한다. 
	 *
	 * @param inParam
	 * @return
	 */
	public Parameters getProgramLvl1List(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset ds_commbo = getSqlManager().queryForGoodseedDataset(inParams, "customSelectService." + inParams.getVariableAsString("sqlId"));
		outParams.setGoodseedDataset("ds_commbo", ds_commbo);
		return outParams;
	}

}
