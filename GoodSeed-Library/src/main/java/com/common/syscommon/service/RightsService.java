/*
 * Copyright (c) 2010 CJ OliveNetworks
 * All right reserved.
 *
 * This software is the proprietary information of CJ OliveNetworks
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
 * The class RightsService
 * <br>
 *		권한 정보를 조회/저장한다.<br>
 * <br>
 * @author
 * @version 1.0
 *
 */
@Service
public class RightsService extends GoodseedService {

	/**
	 *  사용자  리스트 조회
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getUserDtlList(Parameters inParams) {

		Parameters outParams = ParametersFactory.createParameters(inParams);

		outParams.setGoodseedDataset("ds_userDtl",
				getSqlManager().queryForGoodseedDataset(inParams, "userInformation.getUserDtlList"));
		if(outParams.getGoodseedDataset("ds_userDtl").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 사용자별 권한조회
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getAuthorityGroupSearchForUserList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_authProgDtl",
				getSqlManager().queryForGoodseedDataset(inParams, "rights.getAuthorityGroupSearchForUserList"));
		if(outParams.getGoodseedDataset("ds_authProgDtl").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 권한그룹 조회
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getAuthorityGroupList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_authGrp",
				getSqlManager().queryForGoodseedDataset(inParams, "rights.getAuthorityGroupList"));

		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "rights.getAuthorityGroupListTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_authGrp").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 권한 그룹 관련 프로그램 상세 조회
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getAuthorityDtlList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_authDtl", getSqlManager()
				.queryForGoodseedDataset(inParams, "rights.getAuthorityDtlList"));
		if(outParams.getGoodseedDataset("ds_authDtl").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 권한관리 저장
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParam
	 * @return
	 */
	public Parameters saveAuthorityGroup(Parameters inParam) {
		Parameters outParam = ParametersFactory.createParameters(inParam);
		GoodseedDataset dsAuthGrp = inParam.getGoodseedDataset("ds_authGrp");
		GoodseedDataset dsAuthDtl = inParam.getGoodseedDataset("ds_authDtl");

		if(dsAuthGrp != null || dsAuthDtl != null) {
			if(dsAuthGrp != null) {

				/* delete */
				for(int i = 0; i < dsAuthGrp.getDeleteRowCount(); i++) {
					dsAuthGrp.setActiveRow(i);
					getSqlManager().delete(dsAuthGrp, "rights.deleteAuthority");
					dsAuthGrp.addParameter("LOG_CL", "8");
					dsAuthGrp.addParameter("RMK", "deleteAuthGrpProgram");
					getSqlManager().insert(dsAuthGrp, "userInformation.insertUserAuthorityLog");
					if(i == dsAuthGrp.getDeleteRowCount() - 1) {
						getSqlManager().delete(dsAuthGrp, "rights.deleteAuthorityDtl");
						dsAuthGrp.addParameter("LOG_CL", "8");
						dsAuthGrp.addParameter("RMK", "deleteAuthGrpProgram");
						getSqlManager().insert(dsAuthGrp, "userInformation.insertUserAuthorityLog");
					}
				}

				/* insert & update */
				for(int i = 0; i < dsAuthGrp.getRowCount(); i++) {
					dsAuthGrp.setActiveRow(i);

					// 신규
					if(dsAuthGrp.getRowStatus(i).equals(GoodseedConstants.INSERT)) {
						getSqlManager().insert(dsAuthGrp, "rights.insertAuthority");
						dsAuthGrp.addParameter("LOG_CL", "8");
						dsAuthGrp.addParameter("RMK", "insertAuthGrpProgram");
						getSqlManager().insert(dsAuthGrp, "userInformation.insertUserAuthorityLog");
						// 그룹권한 다국어 자동 생성
						getSqlManager().insert(dsAuthGrp, "rights.insertAutoMultilingualAuthority");
						//수정
					} else if(dsAuthGrp.getRowStatus(i).equals(GoodseedConstants.UPDATE)) {
						dsAuthGrp.addParameter("LOG_CL", "8");
						dsAuthGrp.addParameter("RMK", "updateAuthGrpProgram");
						getSqlManager().insert(dsAuthGrp, "userInformation.insertUserAuthorityLog");
						getSqlManager().update(dsAuthGrp, "rights.updateAuthority");
					}
				}
			}

			if(dsAuthDtl != null) {
				for(int i = 0; i < dsAuthDtl.getRowCount(); i++) {
					dsAuthDtl.setActiveRow(i);
					if(dsAuthDtl.getRowStatus(i).equals(GoodseedConstants.INSERT)
							|| dsAuthDtl.getRowStatus(i).equals(GoodseedConstants.UPDATE)) {
						getSqlManager().delete(dsAuthDtl, "rights.deleteAuthorityDtl");
						getSqlManager().insert(dsAuthDtl, "rights.insertAuthorityDtl");
						//TODO [jay] duplicate key 문제로 주석처리
//						dsAuthDtl.addParameter("LOG_CL", "8");
//						dsAuthDtl.addParameter("RMK", "modifyAuthGrpProgram");
//						getSqlManager().insert(dsAuthDtl, "userInformation.insertUserAuthorityLog");

					}
				}
			}

			outParam.setGoodseedDataset("ds_authGrp", dsAuthGrp);
			outParam.setGoodseedDataset("ds_authDtl", dsAuthDtl);
			outParam.setMessage("MSG_COM_SUC_003");
		}
		return outParam;

	}

	/**
	 * 권한그룹별 사용자  조회
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getAuthorityUserDtlList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_userDtl",
				getSqlManager().queryForGoodseedDataset(inParams, "rights.getAuthorityUserDtlList"));
		if(outParams.getGoodseedDataset("ds_userDtl").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 권한 별 사용자 저장
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParam
	 * @return
	 */
	public Parameters saveAuthorityUser(Parameters inParam) {
		Parameters outParam = ParametersFactory.createParameters(inParam);
		GoodseedDataset dsUserDtl = inParam.getGoodseedDataset("ds_userDtl");

		if(dsUserDtl != null) {

			/* insert & update */
			for(int i = 0; i < dsUserDtl.getRowCount(); i++) {
				dsUserDtl.setActiveRow(i);
				if(dsUserDtl.getRowStatus(i).equals(GoodseedConstants.INSERT)
						|| dsUserDtl.getRowStatus(i).equals(GoodseedConstants.UPDATE)) {
					String useYn = dsUserDtl.getColumnAsString(i, "USE_YN");
					if("0".equals(useYn)) {
						getSqlManager().delete(dsUserDtl, "rights.deleteAuthorityUser");
						dsUserDtl.addParameter("LOG_CL", "8");
						dsUserDtl.addParameter("RMK", "deleteAuthGrpUser");
						getSqlManager().insert(dsUserDtl, "userInformation.insertUserAuthorityLog");
					} else if("1".equals(useYn)) {
						getSqlManager().insert(dsUserDtl, "rights.insertAuthorityUser");
						dsUserDtl.addParameter("LOG_CL", "8");
						dsUserDtl.addParameter("RMK", "insertAuthGrpUser");
						getSqlManager().insert(dsUserDtl, "userInformation.insertUserAuthorityLog");
					}

				}
			}
		}

		outParam.setGoodseedDataset("ds_userDtl", dsUserDtl);
		outParam.setMessage("MSG_COM_SUC_003");
		return outParam;

	}

	/**
	 *
	 * 외부 허용 아이피 조회
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getAllowIpList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_allowIpDtl", getSqlManager().queryForGoodseedDataset(inParams, "rights.getAllowIpList"));
		if(outParams.getGoodseedDataset("ds_allowIpDtl").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 사용자 접근 권한 저장
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @param inParam
	 * @return
	 */
	public Parameters saveAllowIp(Parameters inParam) {
		Parameters outParam = ParametersFactory.createParameters(inParam);
		GoodseedDataset dsAllowIpDtl = inParam.getGoodseedDataset("ds_allowIpDtl");
		GoodseedDataset dsChkInsert = new GoodseedHtmlDataset();

		if(dsAllowIpDtl != null) {

			/* delete */
			for(int i = 0; i < dsAllowIpDtl.getDeleteRowCount(); i++) {
				dsAllowIpDtl.setActiveRow(i);
				getSqlManager().delete(dsAllowIpDtl, "rights.deleteAllowIp");

				dsAllowIpDtl.addParameter("LOG_CL", "8");
				dsAllowIpDtl.addParameter("RMK", "deleteAuthUserAllowIp");
				getSqlManager().insert(dsAllowIpDtl, "userInformation.insertUserAuthorityLog");
			}

			/* insert & update */
			String strAddMsg = "";
			for(int i = 0; i < dsAllowIpDtl.getRowCount(); i++) {
				dsAllowIpDtl.setActiveRow(i);
				if(dsAllowIpDtl.getRowStatus(i).equals(GoodseedConstants.INSERT)) {

					try {
						getSqlManager().insert(dsAllowIpDtl, "rights.insertAllowIp");
					} catch(Exception e) {
						strAddMsg =
								"[" + inParam.getVariableAsString("USERID") + ": " + dsAllowIpDtl.getColumnAsString(i, "USER_ID")
										+ ",외부 허용 IP:" + dsAllowIpDtl.getColumnAsString(i, "ALLOW_IP") + "]";
						throw new UserHandleException("MSG_COM_ERR_027", new String[]{strAddMsg}, e);
					}

					dsAllowIpDtl.addParameter("LOG_CL", "8");
					dsAllowIpDtl.addParameter("RMK", "insertAuthUserAllowIp");
					getSqlManager().insert(dsAllowIpDtl, "userInformation.insertUserAuthorityLog");

				} else if(dsAllowIpDtl.getRowStatus(i).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(dsAllowIpDtl, "rights.updateAllowIp");

					dsAllowIpDtl.addParameter("LOG_CL", "8");
					dsAllowIpDtl.addParameter("RMK", "modifyAuthUserAllowIp");
					getSqlManager().insert(dsAllowIpDtl, "userInformation.insertUserAuthorityLog");
				}
			}
		}

		outParam.setGoodseedDataset("ds_allowIpDtl", dsAllowIpDtl);

		// msg - 저장되었습니다.
		outParam.setStatusMessage("MSG_COM_SUC_003");
		outParam.setMessage("MSG_COM_SUC_003");
		return outParam;

	}

	/**
	 * 사용자정보를 조회한다.
	 *
	 * @ahthor Heejo
	 * @since  2012. 2. 29.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getUserList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);

		outParams.setGoodseedDataset("ds_userInfo", getSqlManager().queryForGoodseedDataset(inParams, "rights.getUserList"));
		if(outParams.getGoodseedDataset("ds_userInfo").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 사용자별 부서권한 정보를 조회한다.
	 *
	 * @ahthor Heejo
	 * @since  2012. 2. 29.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getUserDeptList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		// todo.
		outParams.setGoodseedDataset("ds_master", getSqlManager().queryForGoodseedDataset(inParams, "rights.getUserDeptList"));
		if(outParams.getGoodseedDataset("ds_master").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 사용자별 부서권한 정보를 저장한다.
	 *
	 * @ahthor Heejo
	 * @since  2012. 2. 29.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveUserDept(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_master");
		GoodseedDataset dsChkInsert = new GoodseedHtmlDataset();

		if(goodseedDataset != null) {

			/* delete */
			for(int row = 0; row < goodseedDataset.getDeleteRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				getSqlManager().delete(goodseedDataset, "rights.deleteAuthUserdept");

				goodseedDataset.addParameter("LOG_CL", "8");
				goodseedDataset.addParameter("RMK", "deleteAuthUserDept");
				getSqlManager().insert(goodseedDataset, "userInformation.insertUserAuthorityLog");
			}

			/* insert & update */
			String strAddMsg = "";
			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {
					dsChkInsert = getSqlManager().queryForGoodseedDataset(goodseedDataset, "rights.getUserDeptList");

					if(dsChkInsert.getRowCount() > 0) {
						strAddMsg =
								"[" + inParams.getVariableAsString("SYSTEMCL") + ": "
										+ goodseedDataset.getColumnAsString(row, "SYSTEM_CL") + ","
										+ inParams.getVariableAsString("DEPTNM") + ":"
										+ goodseedDataset.getColumnAsString(row, "DEPT_NM") + "]";
						throw new UserHandleException("MSG_COM_ERR_027", new String[]{strAddMsg});
					}

					goodseedDataset.setColumn(row, "USESTART_DT", goodseedDataset.getColumnAsString(row, "USESTART_DT")
							.replaceAll("-", ""));
					goodseedDataset.setColumn(row, "USEEND_DT",
							goodseedDataset.getColumnAsString(row, "USEEND_DT").replaceAll("-", ""));

					getSqlManager().insert(goodseedDataset, "rights.insertAuthUserdept");

					goodseedDataset.addParameter("LOG_CL", "8");
					goodseedDataset.addParameter("RMK", "insertAuthUserDept");
					getSqlManager().insert(goodseedDataset, "userInformation.insertUserAuthorityLog");
					//수정
				} else if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(goodseedDataset, "rights.updateAuthUserdept");

					goodseedDataset.addParameter("LOG_CL", "8");
					goodseedDataset.addParameter("RMK", "updateAuthUserDept");
					getSqlManager().insert(goodseedDataset, "userInformation.insertUserAuthorityLog");
				}
			}
			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	/**
	 * 사용자정보/프로그램정보를 조회한다.
	 *
	 * @ahthor Heejo
	 * @since  2012. 3. 05.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getUserProgList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_userInfo", getSqlManager().queryForGoodseedDataset(inParams, "rights.getUserList"));
		outParams.setGoodseedDataset("ds_progInfo", getSqlManager().queryForGoodseedDataset(inParams, "rights.getProgList"));
		outParams.setStatusMessage("MSG_COM_SUC_011");
		return outParams;
	}

	/**
	 * 사용자 프로그램별 부서권한 정보를 조회한다.
	 *
	 * @ahthor Heejo
	 * @since  2012. 3. 05.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters getProgDeptList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_master", getSqlManager().queryForGoodseedDataset(inParams, "rights.getProgDeptList"));
		if(outParams.getGoodseedDataset("ds_master").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 사용자 프로그램별 부서권한 정보를 저장한다.
	 *
	 * @ahthor Heejo
	 * @since  2012. 3. 05.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveProgDept(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_master");
		GoodseedDataset dsChkInsert = new GoodseedHtmlDataset();

		if(goodseedDataset != null) {

			/* delete */
			for(int row = 0; row < goodseedDataset.getDeleteRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				getSqlManager().delete(goodseedDataset, "rights.deleteAuthProgdept");

				goodseedDataset.addParameter("LOG_CL", "8");
				goodseedDataset.addParameter("RMK", "deleteAuthUserProgDept");
				getSqlManager().insert(goodseedDataset, "userInformation.insertUserAuthorityLog");

			}

			/* insert & update */
			String strAddMsg = "";
			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.INSERT)) {

					goodseedDataset.setColumn(row, "USESTART_DT", goodseedDataset.getColumnAsString(row, "USESTART_DT")
							.replaceAll("-", ""));
					goodseedDataset.setColumn(row, "USEEND_DT",
							goodseedDataset.getColumnAsString(row, "USEEND_DT").replaceAll("-", ""));

					try {
						getSqlManager().insert(goodseedDataset, "rights.insertAuthProgdept");
					} catch(Exception e) {
						strAddMsg =
								"[" + inParams.getVariableAsString("PROGCD") + ": "
										+ goodseedDataset.getColumnAsString(row, "PROG_CD") + ","
										+ inParams.getVariableAsString("DEPTNM") + ":"
										+ goodseedDataset.getColumnAsString(row, "DEPT_NM") + "]";
						throw new UserHandleException("MSG_COM_ERR_027", new String[]{strAddMsg}, e);
					}

					goodseedDataset.addParameter("LOG_CL", "8");
					goodseedDataset.addParameter("RMK", "insertAuthUserProgDept");
					getSqlManager().insert(goodseedDataset, "userInformation.insertUserAuthorityLog");

				} else if(goodseedDataset.getRowStatus(row).equals(GoodseedConstants.UPDATE)) {
					getSqlManager().update(goodseedDataset, "rights.updateAuthProgdept");
					goodseedDataset.addParameter("LOG_CL", "8");
					goodseedDataset.addParameter("RMK", "updateAuthUserProgDept");
					getSqlManager().insert(goodseedDataset, "userInformation.insertUserAuthorityLog");

				}
			}
			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	/**
	 * 다국어 권한그룹 조회
	 *
	 * @ahthor 강정현
	 * @since  4. 16.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getAuthorityGroupMultilinguaList(Parameters inParams) {
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
			List<HashMap> list = getSqlManager().queryForList(inParams, "rights.getHtmlAuthorityGroupMultilinguaList");
			outParams.setGoodseedDataset("ds_authGrp", AbstractDatasetFactory.getDatasetFactoryByParameters(inParams)
					.makeDataset(StringUtil.getUpperKeyList(list)));
			// Miplatform 페이지 화면
		} else {
			outParams.setGoodseedDataset("ds_authGrp",
					getSqlManager().queryForGoodseedDataset(inParams, "rights.getAuthorityGroupMultilinguaList"));
		}
		if(outParams.getGoodseedDataset("ds_authGrp").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 그룹권한 다국어 정보를 저장한다. (Miplatform 용)
	 *
	 * @ahthor 강정현
	 * @since  2013. 4. 16.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveAuthorityGroupMultilingua(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_authGrp");

		if(goodseedDataset != null) {
			/* insert & update */

			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);
				getSqlManager().insert(goodseedDataset, "rights.insertAuthorityGroupMultilinguaList");
			}
			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	/**
	 * 그룹권한 다국어 정보를 저장한다. (HTML 용)
	 *
	 * @ahthor 강정현
	 * @since  2013. 5. 6.
	 *
	 * @param  inParams
	 * @return outParams
	 */
	public Parameters saveHtmlAuthorityGroupMultilingua(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset masterDataset = inParams.getGoodseedDataset("ds_authGrp");
		// 다국어 언어 분류 리스트
		GoodseedDataset comParams = getSqlManager().queryForGoodseedDataset(inParams, "commonCode.getLangCode");

		// 다국어 프로그램 목록 저장
		if(masterDataset != null) {

			for(int row = 0; row < masterDataset.getRowCount(); row++) {
				masterDataset.setActiveRow(row);
				// 시스템 구분
				String systemCl = masterDataset.getColumnAsString(row, "SYSTEM_CL");
				// 권한그룹코드
				String authCd = masterDataset.getColumnAsString(row, "AUTH_CD");
				// 권한그룹분류
				String authCl = masterDataset.getColumnAsString(row, "AUTH_CL");

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
					// 권한그룹코드
					saveDataset.addParameter("AUTH_CD", authCd);
					// 권한그룹분류
					saveDataset.addParameter("AUTH_CL", authCl);
					// 다국어 언어 구분
					saveDataset.addParameter("LANG_CL", langCl);
					// 다국어 라벨명
					saveDataset.addParameter("LANG_VALUE", masterDataset.getColumnAsString(row, upperLangCl));

					getSqlManager().update(saveDataset, "rights.insertAuthorityGroupMultilinguaList");
				}
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}

		return outParams;
	}

	/**
	 * 권한그룹 조회(Mobile 단건)
	 *
	 * @ahthor KimSooJung
	 * @since  9. 23.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getAuthorityGroupForMobile(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_authGrp",
				getSqlManager().queryForGoodseedDataset(inParams, "rights.getAuthorityGroupForMobile"));

		if(outParams.getGoodseedDataset("ds_authGrp").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}
}
