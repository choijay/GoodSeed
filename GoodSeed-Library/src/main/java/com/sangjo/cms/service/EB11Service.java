/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.sangjo.cms.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sangjo.management.service.ContractService;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;

/**
 * The class CMSService
 * <br>
 * CMS Service
 * <br>
 * 
 * @author jay
 *
 */
@Service("eb11Service")
public class EB11Service extends GoodseedService {

	@Autowired
	private CMSService cmsService;
	
	@Autowired
	private ContractService contractService;
	
	/**
	 * EB11 biz process 저장
	 * @param inParams
	 * @param uploadResult
	 * @return
	 */
	public Parameters saveProcess(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		if (! inParams.getVariableAsString("CMS_FILE_NO").startsWith(GoodseedConstants.CMS_FILE_TYPE_EB11)) {
			throw new UserHandleException("MSG_CMS_ERR_014", new String[] {GoodseedConstants.CMS_FILE_TYPE_EB11});
		}
		
		//이미 반영된 경우, 처리되었다는 메시지를 보여준다.
		Map<String, Object> cmsFile = (Map<String, Object>) cmsService.getCMSFile(inParams).getVariable("cmsFile");
		if ("1".equals((String) cmsFile.get("APPLY_YN"))) {	//반영여부
			throw new UserHandleException("MSG_CMS_ERR_005");
		}
		
		GoodseedDataset ds = cmsService.getFileData(inParams).getGoodseedDataset("ds_fileData");
		
		String reqType = null;
		for(int row = 0; row < ds.getRowCount(); row++) {
			ds.setActiveRow(row);
			
			reqType = ds.getColumnAsString(row, "REQ_TYPE");
			switch (reqType) {
			case "1" :	// 신규
				inParams.setVariable("CONT_STATUS_CD", "03");//계약상태 - 진행중
				inParams.setVariable("PAY_STATUS_CD", "02");//납부상태 - 납부중 
				inParams.setVariable("CMS_APPROVE_STATUS_CD", "02");//CMS승인상태 - 승인완료
				inParams.setVariable("BANK_CD", ds.getColumnAsString(row, "BANK_CD"));	//은행코드
				inParams.setVariable("ACCOUNT_NO", ds.getColumnAsString(row, "ACCOUNT_NO"));	//계좌번호
				inParams.setVariable("ACCOUNT_BIRTH", ds.getColumnAsString(row, "ACCOUNT_BIRTH_ORIGIN"));	//납부자생년월일
				break;
			case "3" :	// 해지
			case "7" : // 임의해지
				//20170707 황선영 요청, 보류로 변경
				inParams.setVariable("CONT_STATUS_CD", "04");//계약상태 - 보류
				inParams.setVariable("PAY_STATUS_CD", "06");//납부상태 - 보류
				inParams.setVariable("CMS_APPROVE_STATUS_CD", "05");//CMS승인상태 - 은행접수해지
				break;
			}
			
			inParams.setVariable("PAYER_NO", ds.getColumnAsString(row, "PAYER_NO"));	//납부자번호
			
			contractService.saveContractStatus(inParams);
		} 
		
		// EB11 파일에 대한 처리상태 update
		inParams.setVariable("APPLY_YN", "1");
		cmsService.saveApplyFlag(inParams);
		
		outParams.setStatusMessage("MSG_COM_SUC_012");
		
		return outParams;
	}

}
