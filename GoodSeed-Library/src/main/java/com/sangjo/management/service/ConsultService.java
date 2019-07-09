/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.sangjo.management.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;

/**
 * The class ConsultService
 * <br>
 * 상담관리 Service<br>
 * <br>
 * 
 * @author jay
 *
 */
@Service
public class ConsultService extends GoodseedService {

	/**
	 * 상담 리스트 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 23.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getConsultList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_consultList", getSqlManager().queryForGoodseedDataset(inParams, "consult.getConsultList"));
		if(outParams.getGoodseedDataset("ds_consultList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}
	
	/**
	 * 고객의 상담정보 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 23.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters getConsult(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setVariable("consult",  getSqlManager().queryForObject(inParams, "consult.getConsult"));
		return outParams;
	}
	
	/**
	 * 상담 정보 저장
	 *
	 * @ahthor jay
	 * @since 2016. 12. 23.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters saveConsult(Parameters inParams) {
		
		String contNo = inParams.getVariableAsString("CONT_NO");
		if (StringUtils.isBlank(contNo)) {
			throw new UserHandleException("MSG_DPS_ERR_002");
		}
		
		String consultSeq = inParams.getVariableAsString("CONSULT_SEQ");
		
		if (StringUtils.isBlank(consultSeq)) {
			//신규 입력
			getSqlManager().insert(inParams, "consult.insertConsult");
			
		} else {
			//수정
			getSqlManager().update(inParams, "consult.updateConsult");
			
		}
		
		Parameters outParams = this.getConsult(inParams);
		outParams.setMessage("MSG_COM_SUC_003");
		return outParams;
	}

	public Parameters saveAssign(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		getSqlManager().update(inParams, "consult.updateCustomerOfContract");
		outParams.setMessage("MSG_COM_SUC_003");
		return outParams;
	}
}
