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
 * The class EB14Service
 * <br>
 * EB14 Service
 * <br>
 * 
 * @author jay
 *
 */
@Service("eb14Service")
public class EB14Service extends GoodseedService {

	@Autowired
	private CMSService cmsService;
	
	/**
	 * EB14 biz process 저장
	 * @param inParams
	 * @param uploadResult
	 * @return
	 */
	public Parameters saveProcess(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		if (! inParams.getVariableAsString("CMS_FILE_NO").startsWith(GoodseedConstants.CMS_FILE_TYPE_EB14)) {
			throw new UserHandleException("MSG_CMS_ERR_014", new String[] {GoodseedConstants.CMS_FILE_TYPE_EB14});
		}
		
		//eb14 자료의 결과값을 eb13 자료에 update
		inParams.setVariable("EB13_CMS_FILE_NO", inParams.getVariableAsString("CMS_FILE_NO").replace(GoodseedConstants.CMS_FILE_TYPE_EB14, GoodseedConstants.CMS_FILE_TYPE_EB13));
		getSqlManager().update(inParams, "eb14.updateCMSFileDataForResult");
		
		//결과가 정상인 데이터를 계약 테이블에 반영
		getSqlManager().update(inParams, "eb14.updateContractForSuccess");
		
		//결과가 정상인 데이터 중 해지건을 CMS_PAYER 테이블에 반영
		getSqlManager().update(inParams, "eb14.updateContractForSuccessOfTerminate");
		
		//결과가 실패인 데이터를 계약 테이블에 반영
		getSqlManager().update(inParams, "eb14.updateContractForFail");
		
		//계약이 모두 해지된 고객은 종료처리
		getSqlManager().update(inParams, "eb14.updateCustomerStatus");
		
		// EB14 파일에 대한 처리상태 update
		inParams.setVariable("APPLY_YN", "1");
		cmsService.saveApplyFlag(inParams);
		
		outParams.setMessage("MSG_COM_SUC_009");
		
		return outParams;
	}

}
