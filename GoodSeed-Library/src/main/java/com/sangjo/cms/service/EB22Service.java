/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.sangjo.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;

/**
 * The class EB22Service
 * <br>
 * EB22 Service
 * <br>
 * 
 * @author jay
 *
 */
@Service("eb22Service")
public class EB22Service extends GoodseedService {

	@Autowired
	private CMSService cmsService;
	
	/**
	 * EB22 biz process 저장
	 * @param inParams
	 * @param uploadResult
	 * @return
	 */
	public Parameters saveProcess(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		if (! inParams.getVariableAsString("CMS_FILE_NO").startsWith(GoodseedConstants.CMS_FILE_TYPE_EB22)) {
			throw new UserHandleException("MSG_CMS_ERR_014", new String[] {GoodseedConstants.CMS_FILE_TYPE_EB22});
		}
		
		//eb22 자료의 결과값을 eb21 자료에 update
		inParams.setVariable("EB21_CMS_FILE_NO", inParams.getVariableAsString("CMS_FILE_NO").replace(GoodseedConstants.CMS_FILE_TYPE_EB22, GoodseedConstants.CMS_FILE_TYPE_EB21));
		getSqlManager().update(inParams, "eb22.updateCMSFileDataByFailResult");
		getSqlManager().update(inParams, "eb22.updateCMSFileDataBySuccessResult");
		
		//결과가 정상인 데이터를 계약 테이블에 반영
		getSqlManager().update(inParams, "eb22.updateContractForSuccess");
		
		//결과가 실패인 데이터를 계약 테이블에 반영
		getSqlManager().update(inParams, "eb22.updateContractForFail");
		
		//계약 테이블에 연체로 인한 상태변경 반영(연체/3회연체)
		//getSqlManager().update(inParams, "eb22.updateOverduePayStatus");
		
		getSqlManager().update(inParams, "eb22.updatePayComplete");
		
		// EB22 파일에 대한 처리상태 update
		inParams.setVariable("APPLY_YN", "1");
		cmsService.saveApplyFlag(inParams);
		
		outParams.setMessage("MSG_COM_SUC_009");
		
		return outParams;
	}

}
