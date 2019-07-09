/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.common.master.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;


import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;

/**
 * The class TermsService
 * <br>
 * 약관 Service
 * <br>
 * 
 * @author jay
 *
 */
@Service
public class TermsService extends GoodseedService {

	private static final Log LOG = LogFactory.getLog(TermsService.class);
	
	/**
	 * 약관 리스트 조회
	 *
	 * @ahthor jay
	 * @since 2016. 11. 24.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getTermsList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_termsList", getSqlManager().queryForGoodseedDataset(inParams, "terms.getTermsList"));
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "terms.getTermsListTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_termsList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 *  약관 리스트 저장한다.
	 *
	 * @ahthor jay
	 * @since 2016. 11. 24.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveTermsList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_termsList");

		if(goodseedDataset != null) {
			/* insert & update */
			for(int rowIdx = 0; rowIdx < goodseedDataset.getRowCount(); rowIdx++) {
				goodseedDataset.setActiveRow(rowIdx);

				// 신규
				if(goodseedDataset.getRowStatus(rowIdx).equals(GoodseedConstants.INSERT)) {

					try {
						// 약관 리스트 실제 등록
						getSqlManager().insert(goodseedDataset, "terms.inserteTermsList");
					} catch(Exception e) {
						if(LOG.isErrorEnabled())
							LOG.error(e);
						
						String strAddMsg = new StringBuilder()
								.append(inParams.getVariableAsString("TERMSCD")).append(" : '")
								.append(goodseedDataset.getColumnAsString(rowIdx, "TERMS_CD")).append("'").toString();
						throw new UserHandleException("MSG_COM_VAL_029", new String[]{strAddMsg});
					}

					//수정
				} else if(goodseedDataset.getRowStatus(rowIdx).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(goodseedDataset, "terms.updateTermsList");
				}
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

}
