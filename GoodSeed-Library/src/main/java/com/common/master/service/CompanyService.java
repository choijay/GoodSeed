/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.common.master.service;

import org.springframework.stereotype.Service;

import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;

/**
 * The class CompanyService
 * <br>
 * 회사 정보 관리 Service
 * <br>
 * 
 * @author jay
 *
 */
@Service
public class CompanyService extends GoodseedService {

	/**
	 * 회사 정보 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 24.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getCompanyInfo(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setVariable("company",  getSqlManager().queryForObject(inParams, "company.getCompany"));
		return outParams;
	}

	/**
	 *  회사 정보를 수정한다.
	 *
	 * @ahthor jay
	 * @since 2016. 12. 24.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveCompany(Parameters inParams) {
		
		getSqlManager().update(inParams, "company.updateCompany");
		
		Parameters outParams = this.getCompanyInfo(inParams);
		outParams.setMessage("MSG_COM_SUC_003");
		
		return outParams;
	}

}
