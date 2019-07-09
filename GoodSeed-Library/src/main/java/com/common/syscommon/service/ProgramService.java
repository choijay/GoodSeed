/*
 * Copyright (c) 2016 GoodSeed

 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package com.common.syscommon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.common.syscommon.utility.TreeDatasetUtil;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.GoodseedHtmlDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.AbstractDatasetFactory;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.utility.string.StringUtil;

/**
 * The class ProgramService<br>
 * <br>
 *		프로그램 정보를 조회/등록한다.<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since  2. 5.
 * 
 */
@Service
public class ProgramService extends GoodseedService {

	/**
	 * 프로그램정보를 조회한다.
	 * 
	 * @ahthor Heejo
	 * @since  2012. 2. 5.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getProgramList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset ds_master = getSqlManager().queryForGoodseedDataset(inParams, "program.getProgramList");
		
		/** tree dataset구성*/
		if("1".equals(inParams.getVariableAsString("isTreeDs"))){
			TreeDatasetUtil.makeTreeDataset(ds_master, inParams);
		}
		outParams.setGoodseedDataset("ds_master", ds_master);
		
		if(outParams.getGoodseedDataset("ds_master").isDataSetEmpty()){
			outParams.setStatusMessage("MSG_COM_ERR_007");
		}else{
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 프로그램정보 를 저장한다. 
	 * 
	 * @ahthor Heejo
	 * @since  2012. 2. 5.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveProgram(Parameters inParams) {
		GoodseedDataset GoodseedDataset = inParams.getGoodseedDataset("ds_master");

		if(GoodseedDataset != null) {
			/* -----------------------------------------------------------------------------
			 * 프로그램 삭제 로직 제거 (사용여부 사용)
			 * 일자 : 2013.04.30
			 * 작성자 : 강정현
			 *	for(int row = 0; row < GoodseedDataset.getDeleteRowCount(); row++) {
			 *		GoodseedDataset.setActiveRow(row);
			 *		getSqlManager().delete(GoodseedDataset, "program.deleteProgram");
			 *		getSqlManager().delete(GoodseedDataset, "program.deleteAuthProg");
			 *		getSqlManager().delete(GoodseedDataset, "program.deleteAuthProgDept");
			 * }
			 * ----------------------------------------------------------------------------- */

			/* insert & update */
			for(int row = 0; row < GoodseedDataset.getRowCount(); row++) {
				GoodseedDataset.setActiveRow(row);

				// 신규
				if(GoodseedDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {
					getSqlManager().insert(GoodseedDataset, "program.insertProgram");

					// 다국어 - 프로그램 관리 자동 생성
					//getSqlManager().insert(GoodseedDataset, "program.insertAutoProgramMultilingual");
				// 수정
				} else if(GoodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(GoodseedDataset, "program.updateProgram");
				}
			}
		}
		Parameters outParams = this.getProgramList(inParams);
		outParams.setMessage("MSG_COM_SUC_003");
		return outParams;
	}

	/**
	 * 프로그램 다국어 정보를 조회한다.
	 * 
	 * @ahthor 강정현
	 * @since  2013. 4. 9.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getProgramMultilinguaList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);

		// WEB 페이지 화면 (2013.05.06, 강정현)
		if("HTML".equals(inParams.getVariableAsString("webType"))) {
			GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");

			ArrayList<String> codeList = new ArrayList<String>();

			for(int row = 0; row < comParams.getRowCount(); row++) {
				comParams.setActiveRow(row);
				// 코드	
				String cd = comParams.getColumnAsString(row, "COMM_CD"); 		
				codeList.add(cd);

			}

			inParams.put("codeList", codeList);
			//GoodseedDataset dsset = getSqlManager().queryForGoodseedDataset(inParams, "program.getHtmlProgramList");
			List<HashMap> list = getSqlManager().queryForList(inParams, "program.getHtmlProgramList");

			//System.out.println("dsset : ==" + dsset);
			outParams.setGoodseedDataset("ds_master",
					AbstractDatasetFactory.getDatasetFactoryByParameters(inParams).makeDataset(StringUtil.getUpperKeyList(list)));
		// Miplatform 페이지 화면
		} else {
			outParams.setGoodseedDataset("ds_master", getSqlManager().queryForGoodseedDataset(inParams, "program.getProgramMultilinguaList"));
		}

		if(outParams.getGoodseedDataset("ds_master").isDataSetEmpty()){
			outParams.setStatusMessage("MSG_COM_ERR_007");
		}else{
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 프로그램 사용자 버튼 다국어 정보를 조회한다.
	 * 
	 * @ahthor 강정현
	 * @since  2013. 4. 10.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getProgramBtnMultilinguaList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);

		// WEB 페이지 화면 (2013.05.06, 강정현)
		if("HTML".equals(inParams.getVariableAsString("webType"))) {
			GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");

			ArrayList<String> codeList = new ArrayList<String>();

			for(int row = 0; row < comParams.getRowCount(); row++) {
				comParams.setActiveRow(row);
				// 코드	
				String cd = comParams.getColumnAsString(row, "COMM_CD"); 		
				codeList.add(cd);

			}

			inParams.put("codeList", codeList);
			List<HashMap> list = getSqlManager().queryForList(inParams, "program.getHtmlProgramBtnList");
			outParams.setGoodseedDataset("ds_detail",
					AbstractDatasetFactory.getDatasetFactoryByParameters(inParams).makeDataset(StringUtil.getUpperKeyList(list)));
		// Miplatform 페이지 화면
		} else {
			outParams.setGoodseedDataset("ds_detail", getSqlManager().queryForGoodseedDataset(inParams, "program.getProgramBtnList"));
		}

		if(outParams.getGoodseedDataset("ds_detail").isDataSetEmpty()){
			outParams.setStatusMessage("MSG_COM_ERR_007");
		}else{
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 다국어 프로그램 저장 (Miplatform 용)
	 * 
	 * @ahthor 강정현
	 * @since  2013. 4. 9.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveProgramMultilingua(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset GoodseedDataset = inParams.getGoodseedDataset("ds_master");
		GoodseedDataset detailDataset = inParams.getGoodseedDataset("ds_detail");

		// 다국어 프로그램 목록 저장
		if(GoodseedDataset != null) {
			for(int row = 0; row < GoodseedDataset.getRowCount(); row++) {
				GoodseedDataset.setActiveRow(row);
				// 수정 & 신규 저장
				getSqlManager().insert(GoodseedDataset, "program.insertProgramMultilingua");
			}
		}

		// 다국어 사용자버튼 목록 저장
		if(detailDataset != null) {
			for(int row = 0; row < detailDataset.getRowCount(); row++) {
				detailDataset.setActiveRow(row);
				getSqlManager().insert(detailDataset, "program.insertProgramBtn");
			}
		}

		outParams.setMessage("MSG_COM_SUC_003");
		return outParams;
	}

	/**
	 * 다국어 프로그램 저장 (HTML용) 
	 * 
	 * @ahthor 강정현
	 * @since  2013. 5. 6.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveHtmlProgramMultilingua(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset masterDataset = inParams.getGoodseedDataset("ds_master");
		GoodseedDataset detailDataset = inParams.getGoodseedDataset("ds_detail");
		// 다국어 언어 분류 리스트
		GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode"); 

		if(masterDataset != null || detailDataset != null) {
			// 다국어 프로그램 목록 저장
			if(masterDataset != null) {
				for(int row = 0; row < masterDataset.getRowCount(); row++) {
					masterDataset.setActiveRow(row);
					// 시스템 구분
					String systemCl = masterDataset.getColumnAsString(row, "SYSTEM_CL"); 
					// 프로그램 코드
					String progCd = masterDataset.getColumnAsString(row, "PROG_CD"); 

					for(int codeRow = 0; codeRow < comParams.getRowCount(); codeRow++) {
						comParams.setActiveRow(codeRow);
						
						// 다국어 코드값
						String langCl = comParams.getColumnAsString(codeRow, "COMM_CD"); 
						// 알수 없는 다국어 칼럼명 구하기
						String upperLangCl = langCl.toUpperCase(Locale.getDefault()); 

						// 저장할 새 데이터셋 생성 
						GoodseedDataset saveDataset = new GoodseedHtmlDataset();
						// 시스템구분 코드
						saveDataset.addParameter("SYSTEM_CL", systemCl); 
						// 프로그램코드
						saveDataset.addParameter("PROG_CD", progCd); 
						// 다국어 언어 구분
						saveDataset.addParameter("LANG_CL_CD", langCl); 
						// 다국어 라벨명
						saveDataset.addParameter("LANG_VALUE", masterDataset.getColumnAsString(row, upperLangCl)); 

						getSqlManager().update(saveDataset, "program.insertProgramMultilingua");
					}
				}
			}

			// 사용자버튼 목록 저장
			if(detailDataset != null) {
				for(int row = 0; row < detailDataset.getRowCount(); row++) {
					detailDataset.setActiveRow(row);
					// 프로그램 코드
					String progCd = detailDataset.getColumnAsString(row, "PROG_CD"); 
					String btnTypeCd = detailDataset.getColumnAsString(row, "BTN_TYPE_CD");
					for(int codeRow = 0; codeRow < comParams.getRowCount(); codeRow++) {
						comParams.setActiveRow(codeRow);
						
						// 다국어 코드값
						String langCl = comParams.getColumnAsString(codeRow, "COMM_CD"); 
						// 알수 없는 다국어 칼럼명 구하기
						String upperLangCl = langCl.toUpperCase(Locale.getDefault()); 

						// 저장할 새 데이터셋 생성 
						GoodseedDataset saveDataset = new GoodseedHtmlDataset();
						// 공통그룹코드
						saveDataset.addParameter("PROG_CD", progCd); 
						// 다국어 언어 구분
						saveDataset.addParameter("COMM_CD", langCl); 
						// 다국어 언어 구분
						saveDataset.addParameter("BTN_TYPE_CD", btnTypeCd); 
						// 다국어 라벨명
						saveDataset.addParameter("BTN_LANG_VALUE", detailDataset.getColumnAsString(row, upperLangCl)); 

						getSqlManager().insert(saveDataset, "program.insertProgramBtn");
					}
				}
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}
}
