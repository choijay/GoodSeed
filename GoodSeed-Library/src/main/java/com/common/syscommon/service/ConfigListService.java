package com.common.syscommon.service;

import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;

/**
 * The class ConfigListService<br>
 * <br>
 * 		ConfigList 조회/등록한다.<br>
 * <br>
 *
 * @author jay
 * @version 3.0
 *
 */
@Service
public class ConfigListService extends GoodseedService {

	/**
	 * 
	 * ConfigList 정보를 조회한다.<br>
	 * 
	 * <br>
	 * @param inParams
	 * @return outParams
	 * @author 
	 * @since 
	 */
	public Parameters getConfigList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_configList", getSqlManager().queryForGoodseedDataset(inParams, "configList.getConfigList"));
		outParams.setStatusMessage("MSG_COM_SUC_011");
		return outParams;
	}

	/**
	 * ConfigList 정보를 저장한다. 
	 * 
	 * @ahthor 
	 * @since  
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveConfigList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		/**
		 * 데이터 가운데 파일 경로 등에 \가 포함될 수 있으므로
		 * XSS 필터링하는 XssShieldImpl.java 의 stripXss 메서드를 피하기 위하여 
		 * 특별히 getOriginalDataset을 사용하여 원본 데이터셋을 사용한다.
		 */
		GoodseedDataset frameOneDataset = inParams.getGoodseedDataset("ds_configList").getOriginalDataset();

		if(frameOneDataset != null) {

			/* delete */
			for(int row = 0; row < frameOneDataset.getDeleteRowCount(); row++) {
				frameOneDataset.setActiveRow(row);
				getSqlManager().delete(frameOneDataset, "configList.deleteConfigList");
			}
			
			/* insert & update */
			for(int row = 0; row < frameOneDataset.getRowCount(); row++) {
				frameOneDataset.setActiveRow(row);
				frameOneDataset.setColumn(row, "UPDR_ID", inParams.getVariable("g_userId").toString());

				// 신규
				if(frameOneDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {
					getSqlManager().insert(frameOneDataset, "configList.insertConfigList");
				//수정
				} else if(frameOneDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(frameOneDataset, "configList.updateConfigList");
				}
			}
			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	
}
