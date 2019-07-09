/*
 * Copyright (c) 2009 GoodSeed
 * All right reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author			Date				Description
 * -------------	--------------		------------------
 * KwonkiYoon		2010. 2. 24.		First Draft.
 */
package com.common.syscommon.service;

import java.util.List;

import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;

/**
 * The class MenuService
 * 
 * @author jay
 * @version 1.0
 * 
 */
@Service
public class MenuService extends GoodseedService {

	/**
	 * 메뉴 목록을 조회한다.(Miplatform)
	 * @param inParams
	 * @return
	 */
	public Parameters getMenuList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList", getSqlManager().queryForGoodseedDataset(inParams, "menu.getMenuList"));
		outParams
				.setGoodseedDataset("ds_buttonPerm", getSqlManager().queryForGoodseedDataset(inParams, "menu.getUserButtonPerm"));
		return outParams;
	}

	/**
	 * Top 메뉴 목록을 조회한다.(HTML)
	 * @param inParams
	 * @return
	 */
	public Parameters getMenuListAppliedForAuthForSystemClTop(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList",
				getSqlManager().queryForGoodseedDataset(inParams, "menu.getMenuListAppliedForAuthForSystemClTop"));
		//outParams.setGoodseedDataset("ds_buttonPerm", getSqlManager().queryForGoodseedDataset(inParams, "menu.getUserButtonPerm"));
		return outParams;
	}

	/**
	 * LEFT 메뉴 목록을 조회한다.(HTML)
	 * @param inParams
	 * @return
	 */
	public Parameters getMenuListAppliedForAuthForSystemCl(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList",
				getSqlManager().queryForGoodseedDataset(inParams, "menu.getMenuListAppliedForAuthForSystemCl"));
		//outParams.setGoodseedDataset("ds_buttonPerm", getSqlManager().queryForGoodseedDataset(inParams, "menu.getUserButtonPerm"));
		return outParams;
	}

	/**
	 * 메뉴를 검색한다.(HTML)
	 * @param inParams
	 * @return
	 */
	public Parameters getMenuListByKeyword(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList", getSqlManager()
				.queryForGoodseedDataset(inParams, "menu.getMenuListByKeyword"));
		return outParams;
	}

	/**
	 * 즐겨찾기 메뉴 목록을 조회한다.(Miplatform)
	 * @param inParams
	 * @return
	 */
	public Parameters getMenuListByFavorite(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList",
				getSqlManager().queryForGoodseedDataset(inParams, "menu.getMenuListByFavorite"));
		return outParams;
	}

	/**
	 * 메뉴목록을 조회한다.
	 * @param inParams
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public Parameters getMenuListBySystemCl(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList",
				getSqlManager().queryForGoodseedDataset(inParams, "menu.getMenuListBySystemCl"));
		return outParams;
	}

	/**
	 * 프로시져 테스트용
	 * @param inParams
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public Parameters creTest(Parameters inParams) {
		Parameters outParam = ParametersFactory.createParameters(inParams);
		GoodseedDataset GoodseedDataset = inParams.getGoodseedDataset("ds_proc");
		getSqlManager().queryForObject(GoodseedDataset, "menu.creTest");

		return outParam;
	}

	/**
	 * 프로시져 테스트용
	 * @param inParams
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public Parameters dropTest(Parameters inParams) {
		Parameters outParam = ParametersFactory.createParameters(inParams);
		GoodseedDataset GoodseedDataset = inParams.getGoodseedDataset("ds_proc");
		getSqlManager().queryForObject(GoodseedDataset, "menu.dropTest");

		return outParam;
	}

	/**
	 * 메뉴  목록을 조회한다. (HTML)
	 *
	 * @return
	 */
	public Parameters getPageNaviList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_pageNavi", getSqlManager().queryForGoodseedDataset(inParams, "menu.getPageNaviList"));
		return outParams;
	}

	/**
	 * 메뉴  목록을 조회한다. (HTML)
	 *
	 * @return
	 */
	public List<?> getCommPageNaviList() {
		return getSqlManager().queryForList("menu.getPageNaviList");
	}

	/**
	 * 버튼권한 목록을 조회한다.(HTML)
	 * @param inParams
	 * @return
	 */
	public Parameters getHTMLMenuList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_MenuList", getSqlManager().queryForGoodseedDataset(inParams, "menu.getHTMLButtonPerm"));
		return outParams;
	}

	/**
	 * TOP 메뉴 목록을 조회한다.(EXTJS)
	 * @param inParams
	 * @return
	 */
	public Parameters getEXTTopMenuList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_MenuList", getSqlManager().queryForGoodseedDataset(inParams, "menu.getEXTTopMenuList"));
		return outParams;
	}

	/**
	 * LEFT 메뉴 목록을 조회한다.(EXTJS)
	 * @param inParams
	 * @return
	 */
	public Parameters getEXTLeftMenuList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_MenuList", getSqlManager().queryForGoodseedDataset(inParams, "menu.getEXTLeftMenuList"));
		return outParams;
	}

	/**
	 * 버튼권한 목록을 조회한다.(EXTJS)
	 * @param inParams
	 * @return
	 */
	public Parameters getEXTButtonPermissionList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_buttonPerm",
				getSqlManager().queryForGoodseedDataset(inParams, "menu.getEXTUserButtonPerm"));
		return outParams;
	}

	/**
	 * Mobile 메뉴 목록을 조회한다.(Mobile)
	 * @param inParams
	 * @return
	 */
	public Parameters getMenuListForMobile(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList", getSqlManager()
				.queryForGoodseedDataset(inParams, "menu.getMenuListForMobile"));
		return outParams;
	}

	/**
	 * 메뉴를 검색한다.(Mobile)
	 * @param inParams
	 * @return
	 */
	public Parameters getMenuListByKeywordForMobile(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_menuList",
				getSqlManager().queryForGoodseedDataset(inParams, "menu.getMenuListByKeywordForMobile"));
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
		outParam.setMessage("MSG_COM_SUC_003");
		return outParam;
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
}
