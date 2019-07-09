/*
 * Copyright (c) 2010 GoodSeed
 * All right reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author           Date                Description
 * -------------    --------------      ------------------
 * Jaesang Jeong    2010. 4. 21.        First Draft.
 */

package com.common.syscommon.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;

@Service
public class ComBackOfficeService extends GoodseedService {

	/**
	 * 사용자 바로가기를 조회한다.
	 *
	 * @param inParams
	 * @return Parameters
	 *
	 */
	public Parameters getUserUrl(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_userUrl", getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getUserUrl"));
		return outParams;
	}

	/**
	 * 사용자 URL을  저장한다.
	 *
	 * @param   inParams
	 * @return  Parameters
	 */
	public Parameters saveUserUrl(Parameters inParams) {
		Parameters outParam = ParametersFactory.createParameters(inParams);
		GoodseedDataset GoodseedDataset = inParams.getGoodseedDataset("ds_userUrl");

		if(null != GoodseedDataset && GoodseedDataset.getRowCount() > 0) {

			for(int row = 0; row < GoodseedDataset.getRowCount(); row++) {
				GoodseedDataset.setActiveRow(row);
				if(row == 0) {
					getSqlManager().delete(GoodseedDataset, "comBackOffice.deleteUserUrl");
				}

				getSqlManager().insert(GoodseedDataset, "comBackOffice.insertUserUrl");
			}
		}

		return outParam;
	}

	/**
	 * 마이메뉴를 조회한다.(Miplatform)<br>
	 * <br>
	 * @param inParams
	 * @return Parameters
	 *
	 */
	public Parameters getUserMenu(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_favorite", getSqlManager()
				.queryForGoodseedDataset(inParams, "comBackOffice.getUserMenu"));
		return outParams;
	}

	/**
	 * 마이메뉴를 조회한다.(EXTJS)<br>
	 * <br>
	 * @param inParams
	 * @return Parameters
	 * 
	 */
	public Parameters getUserMenuEXT(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_favorite",
				getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getUserMenuEXT"));
		return outParams;
	}

	/**
	 * 마이메뉴를 조회한다.(HTML4.01)<br>
	 * <br>
	 * @param inParams
	 * @return Parameters
	 * 
	 */
	public Parameters getUserMenuHTML(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_favorite",
				getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getUserMenuHTML"));
		return outParams;
	}

	/**
	 * 마이메뉴를 조회한다.(MOBILE)<br>
	 * <br>
	 * @param inParams
	 * @return Parameters
	 * 
	 */
	public Parameters getUserMenuMOBILE(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_favorite",
				getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getUserMenuMOBILE"));
		return outParams;
	}

	/**
	 * 마이메뉴를 저장한다.
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParam
	 * @return
	 */
	public Parameters saveUserMenu(Parameters inParam) {
		Parameters outParam = ParametersFactory.createParameters(inParam);
		GoodseedDataset dsFavorite = inParam.getGoodseedDataset("ds_favorite");

		if(dsFavorite != null) {

			/* delete */
			for(int i = 0; i < dsFavorite.getDeleteRowCount(); i++) {
				dsFavorite.setActiveRow(i);
				getSqlManager().delete(dsFavorite, "comBackOffice.deleteUserMenu");
			}

			/* insert  */
			for(int i = 0; i < dsFavorite.getRowCount(); i++) {
				dsFavorite.setActiveRow(i);
				if(dsFavorite.getRowStatus(i).equals(GoodseedConstants.INSERT)) {
					getSqlManager().insert(dsFavorite, "comBackOffice.insertUserMenu");

				}
			}
		}

		outParam.setGoodseedDataset("ds_favorite", dsFavorite);
		outParam.setMessage("MSG_COM_SUC_003");
		return outParam;

	}

	/**
	 * 메뉴사용 내역를 조회한다.(Miplatform)
	 *
	 * @param inParams
	 * @return Parameters
	 *
	 */
	public Parameters getRecentMenu(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_recent", getSqlManager()
				.queryForGoodseedDataset(inParams, "comBackOffice.getRecentMenu"));
		return outParams;
	}

	/**
	 * 메뉴사용 내역를 조회한다.(EXTJS)
	 * @param inParams
	 * @return
	 */
	public Parameters getRecentMenuEXT(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_recent",
				getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getRecentMenuEXT"));
		return outParams;
	}

	/**
	 * 메뉴사용 내역를 조회한다.(HTML4.01)
	 * @param inParams
	 * @return
	 */
	public Parameters getRecentMenuHTML(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_recent",
				getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getRecentMenuHTML"));
		return outParams;
	}

	/**
	 * 메뉴사용 내역를 조회한다.(MOBILE)
	 * @param inParams
	 * @return
	 */
	public Parameters getRecentMenuMOBILE(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_recent",
				getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getRecentMenuMOBILE"));
		return outParams;
	}

	/**
	 * 사용자 메모장을 조회한다.
	 *
	 * @param inParams
	 * @return Parameters
	 *
	 */
	public Parameters getUserMemo(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_userMemo", getSqlManager()
				.queryForGoodseedDataset(inParams, "comBackOffice.getUserMemo"));
		return outParams;
	}

	/**
	 * 사용자 메모장을  저장한다.
	 *
	 * @param   inParams
	 * @return  Parameters
	 */
	public Parameters saveUserMemo(Parameters inParams) {
		Parameters outParam = ParametersFactory.createParameters(inParams);
		GoodseedDataset GoodseedDataset = inParams.getGoodseedDataset("ds_userMemo");

		int updateCount = 0;
		if(null != GoodseedDataset && GoodseedDataset.getRowCount() > 0) {
			for(int row = 0; row < GoodseedDataset.getRowCount(); row++) {
				GoodseedDataset.setActiveRow(row);
				if(GoodseedDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {
					getSqlManager().insert(GoodseedDataset, "comBackOffice.insertUserMemo");
				} else if(GoodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					updateCount += getSqlManager().update(GoodseedDataset, "comBackOffice.updateUserMemo");
				}
			}
		}

		return outParam;
	}

	/**
	 * 그리드 상태정보를 조회한다.(EXTJS)
	 * @param inParams
	 * @return
	 */
	public Parameters getStateProvider(Parameters inParams) {
		Parameters outParam = ParametersFactory.createParameters(inParams);
		outParam.setGoodseedDataset("stateVar",
				getSqlManager().queryForGoodseedDataset(inParams, "comBackOffice.getStateProvider"));
		return outParam;
	}

	/**
	 * 그리드 상태정보를 저장한다.(EXTJS)
	 * @param inParams
	 * @return
	 */
	public Parameters saveStateProvider(Parameters inParams) {
		Parameters outParam = ParametersFactory.createParameters(inParams);

		String act = inParams.getVariableAsString("act");

		HashMap<String, Object> inputMap = new HashMap<String, Object>();
		inputMap.put("g_userId", inParams.getVariable("g_userId"));
		inputMap.put("stateId", inParams.getVariable("stateId"));
		inputMap.put("stateVal", inParams.getVariable("stateVal"));

		if(act.equals(GoodseedConstants.INSERT)) {

			//해당 STATE ID 에대한 설정이 존재하면 삭제하고 다시 등록
			List prov = getSqlManager().queryForList(inputMap, "comBackOffice.getStateProvider");
			if(prov.size() > 0) {
				getSqlManager().delete(inputMap, "comBackOffice.deleteStateProvider");
			}

			//STATE 저장
			getSqlManager().insert(inputMap, "comBackOffice.insertStateProvider");
			outParam.setMessage("MSG_COM_SUC_003");
		} else if(act.equals(GoodseedConstants.DELETE)) {
			//삭제
			getSqlManager().delete(inputMap, "comBackOffice.deleteStateProvider");
			outParam.setMessage("MSG_COM_SUC_006");
		} else {
			// else nothing
		}

		return outParam;
	}

}
