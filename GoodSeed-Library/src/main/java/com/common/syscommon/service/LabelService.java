/*
 * Copyright (c) 2010 GoodSeed
 * All right reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author			Date				Description
 * -------------	--------------		------------------
 * 		2010. 3. 9.		First Draft.
 */
package com.common.syscommon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.GoodseedHtmlDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.AbstractDatasetFactory;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.string.StringUtil;

/**
 *
 * The class LabelService
 *
 * @author
 * @version 1.0
 *
 */
@Service
public class LabelService extends GoodseedService {

	/**
	 * 라벨리스트 조회
	 *
	 * @ahthor 강정현
	 * @since  4. 17.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getLabelList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_labelList", getSqlManager().queryForGoodseedDataset(inParams, "label.getLabelList"));
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "label.getLabelListTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_labelList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 *  레벨리스트 저장한다.
	 *
	 * @ahthor 강정현
	 * @since  2013. 4. 17.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveLabelList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_labelList");

		if(goodseedDataset != null) {
			/* insert & update */
			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);

				// 신규
				if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {
					// TODO 데이터 칼럼값 html과 m/P쪽 추출함수가 다르며, 현재 데이터 수정 구현후 테스트후 적용 예정임.
					// 중복 체크 조회
					Map inParamCheck = new HashMap();
					inParamCheck.put("LABEL_CD", goodseedDataset.getColumnAsString(row, "LABEL_CD"));
					inParamCheck.put("SYSTEM_CL", goodseedDataset.getColumnAsString(row, "SYSTEM_CL"));

					List checkList = getSqlManager().queryForList(inParamCheck, "label.getCkLabelList");

					try {
						// 레벨 리스트 실제 등록
						getSqlManager().insert(goodseedDataset, "label.inserteLabelList");
					} catch(Exception e) {
						String strAddMsg =
								inParams.getVariableAsString("") + " : '" + goodseedDataset.getColumnAsString(row, "LABEL_CD")
										+ "'";
						throw new UserHandleException("MSG_COM_VAL_029", new String[]{strAddMsg});
					}

					// 다국어 라벨 관리 자동 생성
					//getSqlManager().insert(GoodseedDataset, "label.insertAutoLabelMultilingual");
					//수정
				} else if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(goodseedDataset, "label.updateLabelList");
				}
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	/**
	 * 다국어 라벨관리 조회
	 *
	 * @ahthor 강정현
	 * @since  4. 18.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getLabelMultilinguaList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_labelList",
				getSqlManager().queryForGoodseedDataset(inParams, "label.getLabelMultilinguaList"));

		// 페이지 구하기
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "label.getLabelMultilinguaListTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_labelList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 *
	 * 로케일별 라벨 조회.<br>
	 * <br>
	 * @param inParams
	 * @return
	 * @ahthor Shim Kwang Sub
	 * @since  5. 2.
	 */
	public Parameters getLabelDomainList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_labeldomainList",
				getSqlManager().queryForGoodseedDataset(inParams, "label.getLabelDomainList"));
		if(outParams.getGoodseedDataset("ds_labeldomainList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 다국어 라벨관리 조회 (HTML용)
	 *
	 * @ahthor 강정현
	 * @since  5. 2.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getHtmlLabelMultilinguaList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");

		ArrayList<String> codeList = new ArrayList<String>();

		for(int row = 0; row < comParams.getRowCount(); row++) {
			comParams.setActiveRow(row);
			// 코드
			String cd = comParams.getColumnAsString(row, "COMM_CD");
			codeList.add(cd);

		}

		inParams.put("codeList", codeList);
		List<HashMap> list = getSqlManager().queryForList(inParams, "label.getHtmlLabelList");
		outParams.setGoodseedDataset("ds_labelList",
				AbstractDatasetFactory.getDatasetFactoryByParameters(inParams).makeDataset(StringUtil.getUpperKeyList(list)));

		// 페이지 처리
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "label.getHtmlLabelTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_labelList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 *  라벨관리 다국어 정보를 저장한다.
	 *
	 * @ahthor 강정현
	 * @since  2013. 4. 18.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveLabelMultilinguaList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_labelList");

		if(goodseedDataset != null) {
			/* insert & update */

			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(goodseedDataset, "label.updateLabelMultilinguaList");
				}
			}
			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	/**
	 *  라벨관리 다국어 정보를 저장한다.  (HTML)
	 *
	 * @ahthor 강정현
	 * @since  2013. 4. 18.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveHtmlLabelMultilinguaList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		// 수정자
		String userId = inParams.getVariableAsString("USERID");
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_labelList");
		// 다국어 언어 분류 리스트
		GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");
		if(goodseedDataset != null) {
			/* insert & update */

			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				// 라베코드
				String labelCd = goodseedDataset.getColumnAsString(row, "LABEL_CD");
				// 시스템 구분
				String systemCl = goodseedDataset.getColumnAsString(row, "SYSTEM_CL");
				for(int codeRow = 0; codeRow < comParams.getRowCount(); codeRow++) {
					comParams.setActiveRow(codeRow);

					// 다국어 코드값
					String langCl = comParams.getColumnAsString(codeRow, "COMM_CD");
					// 알수 없는 다국어 칼럼명 구하기
					String upperLangCl = langCl.toUpperCase(Locale.getDefault());

					// 저장할 새 데이터셋 생성
					GoodseedDataset saveDataset = new GoodseedHtmlDataset();
					// 시스템구분
					saveDataset.addParameter("SYSTEM_CL", systemCl);
					// 라벨코드
					saveDataset.addParameter("LABEL_CD", labelCd);
					// 다국어 언어 구분
					saveDataset.addParameter("LANG_CL", langCl);
					// 다국어 라벨명
					saveDataset.addParameter("LANG_VALUE", goodseedDataset.getColumnAsString(row, upperLangCl));
					// 수정자
					saveDataset.addParameter("USERID", userId);

					// 수정
					getSqlManager().update(saveDataset, "label.updateLabelMultilinguaList");
				}
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	/**
	 *  라벨관리 다국어 정보를 저장한다.  (HTML)
	 *
	 * @ahthor 강정현
	 * @since  2013. 4. 18.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveExtJSLabelMultilinguaList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		// 수정자
		String userId = inParams.getVariableAsString("USERID");
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_labelList");
		// 다국어 언어 분류 리스트
		GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");
		if(goodseedDataset != null) {
			/* insert & update */

			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				// 라베코드
				String labelCd = goodseedDataset.getColumnAsString(row, "LABEL_CD");
				// 시스템 구분
				String systemCl = goodseedDataset.getColumnAsString(row, "SYSTEM_CL");
				for(int codeRow = 0; codeRow < comParams.getRowCount(); codeRow++) {
					comParams.setActiveRow(codeRow);

					// 다국어 코드값
					String langCl = comParams.getColumnAsString(codeRow, "COMM_CD");
					// 알수 없는 다국어 칼럼명 구하기
					String upperLangCl = langCl.toUpperCase(Locale.getDefault());

					// 저장할 새 데이터셋 생성
					GoodseedDataset saveDataset = new GoodseedHtmlDataset();
					// 시스템구분
					saveDataset.addParameter("SYSTEM_CL", systemCl);
					// 라벨코드
					saveDataset.addParameter("LABEL_CD", labelCd);
					// 다국어 언어 구분
					saveDataset.addParameter("LANG_CL", langCl);
					// 다국어 라벨명
					saveDataset.addParameter("LANG_VALUE", goodseedDataset.getColumnAsString(row, upperLangCl));
					// 수정자
					saveDataset.addParameter("USERID", userId);

					// 수정
					getSqlManager().update(saveDataset, "label.updateLabelMultilinguaList");
				}
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}
}
