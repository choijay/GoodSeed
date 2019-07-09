/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.common.master.service;

import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.common.utility.LabelUtil;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.exception.UserHandleException;

/**
 * The class ShopService
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
@Service
public class ShopService extends GoodseedService {

	/**
	 * 매장 리스트 조회
	 *
	 * @ahthor jay
	 * @since 2016. 11. 24.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getShopList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_shopList", getSqlManager().queryForGoodseedDataset(inParams, "shop.getShopList"));
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "shop.getShopListTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_shopList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 *  매장 리스트 저장한다.
	 *
	 * @ahthor jay
	 * @since 2016. 11. 24.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveShopList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_shopList");

		if(goodseedDataset != null) {
			/* insert & update */
			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);

				// 신규
				if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {

					try {
						// 레벨 리스트 실제 등록
						getSqlManager().insert(goodseedDataset, "shop.inserteShopList");
					} catch(Exception e) {
						String strAddMsg = new StringBuilder()
//								.append(LabelUtil.getLabelProperty("SHOP_CD", LocaleUtil.getDefaultLanguage())).append(" : '")
								.append(inParams.getVariableAsString("SHOPCD")).append(" : '")
								.append(goodseedDataset.getColumnAsString(row, "SHOP_CD")).append("'").toString();
						throw new UserHandleException("MSG_COM_VAL_029", new String[]{strAddMsg});
					}

					//수정
				} else if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(goodseedDataset, "shop.updateShopList");
				}
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

}
