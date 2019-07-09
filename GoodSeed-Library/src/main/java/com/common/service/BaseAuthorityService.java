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
package com.common.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;

/**
 *
 * The class AuthorityService
 *
 * @author 
 * @version 1.0
 *
 */
@Service
public class BaseAuthorityService extends GoodseedService {

	private static final Log LOG = LogFactory.getLog(BaseAuthorityService.class);

	/**
	 * 로그아웃 처리
	 * 
	 * @param inParams
	 * @return outParams
	 */
	public Parameters logout(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		// TODO : logout
		return outParams;
	}

	/**
	 * 사용자 패스워드 변경
	 *
	 * @param inParams
	 * @return
	 *      result : 000 - 성공
	 *               010 - 패스워드 변경 실패(데이터 오류 확률 높음)
	 *               020 - 이전 패스워드와 중복
	 *               030 - 1일 1회 변경 제한
	 *               040 - 현재 패스워드 불일치
	 */
	public Parameters updatePassword(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
		inDs.addParameter("userId", inParams.getVariable("g_userId"));

		String curPwd = (String)getSqlManager().queryForObject(inDs, "authority.getPwd");

		//System.out.println("curPwd : " + curPwd);
		//System.out.println("newPwd : " + inParams.getVariable("newPwd"));
		if(LOG.isDebugEnabled()) {
			LOG.debug("current saved password : " + curPwd);
			LOG.debug("current user input password : " + inParams.getVariable("newPwd"));
		}

		if(curPwd == null || !curPwd.equals(inParams.getVariableAsString("pwd"))) {
			// 현재 패스워드 불일치
			outParams.setVariable("result", "040");
			return outParams;
		}

		//이전 사용했던 패스워드도 사용가능하도록 아래 주석처리
//		Integer selectCount = (Integer)getSqlManager().queryForObject(inDs, "authority.getCheckPrevPwdLog");
//		if(selectCount > 0) {
//			// 이전 패스워드와 중복
//			outParams.setVariable("result", "020");
//			return outParams;
//		}

		//24시간 이내, 여러번 변경가능하도록 주석처리. 이전 시스템 연계시 1일 1회 제한하도록 구현된 기능인듯.
//		Integer updateCount = (Integer)getSqlManager().queryForObject(inDs, "authority.getCheckPwdUpdate24H");
//		if(updateCount > 0) {
//			GoodseedDataset dataset = getSqlManager().queryForGoodseedDataset(inDs, "authority.getUserData");
//			String tmpPwdYn = (String)dataset.get("TMPPWDYN");
//			//관리자가 비밀 번호 초기화 한 경우 예외 처리 
//			if("0".equals(tmpPwdYn)) {
//				// 1일 1회 변경 제한
//				outParams.setVariable("result", "030");
//				return outParams;
//			}
//		}

		inDs.addParameter("tmpPwdYn", "0");
		inDs.addParameter("userStsCd", "01");
		int count = getSqlManager().update(inDs, "authority.updatePwd");
		//-----------------------

		if(count == 0) {
			// 업데이트 실패
			outParams.setVariable("result", "010");
		} else {
			// 업데이트 성공
			outParams.setVariable("result", "000");
			//getSqlManager().insert(inDs, "authority.insertPwdChangeLog");
			inDs.addParameter("log_cl", "4");
			inDs.addParameter("pwd_no", inParams.getVariable("newPwd"));
			inDs.addParameter("rmk", "changePassword");
			inDs.addParameter("userId", inParams.getVariable("g_userId"));
			String remoteNetworkAddress = inParams.getVariableAsString(GoodseedConstants.CLIENT_IP);
			inDs.addParameter("ipaddr", remoteNetworkAddress);

			getSqlManager().insert(inDs, "authority.insertUserPwdChangeLog");

		}
		//log.debug(outParams);
		return outParams;
	}

	/**
	 * 
	 * 로그인 이력 조회
	 *
	 * @param inParams
	 * @return outParams
	 */
	public Parameters getLoginHstList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_loginHst", getSqlManager()
				.queryForGoodseedDataset(inParams, "authority.getLoginHstList"));
		//log.debug(outParams);
		return outParams;
	}

	/**
	 * 
	 * 로그아웃 체크
	 *
	 * @param inParams
	 * @return outParams
	 * 		logoutYn: 0 - 로그아웃 안된 상태
	 * 				  1 - 로그아웃 시킨 상태
	 */
	public Parameters getLogoutCheck(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset ds = getSqlManager().queryForGoodseedDataset(inParams, "authority.getLogoutCheck");
		if(ds != null) {
			String lotDt = ds.getColumnAsString(0, "LOTDT");
			if(lotDt == null) {
				//로그아웃 안된 상태
				outParams.setVariable("logoutYn", "0");
			} else {
				//로그아웃 시킨 상태 
				outParams.setVariable("logoutYn", "1");
			}
		} else {
			outParams.setVariable("logoutYn", "0");
		}
		return outParams;
	}

	/**
	 * 로그 아웃 정보 기록
	 *
	 * @param inParams
	 * @return outParams
	 */
	public Parameters saveLogout(Parameters inParams) {

		String remoteNetworkAddress = inParams.getVariableAsString(GoodseedConstants.CLIENT_IP);
		inParams.setVariable("ipaddr", remoteNetworkAddress);

		Parameters outParams = ParametersFactory.createParameters(inParams);
		//로그아웃 성공 로그 기록 
		GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
		inDs.addParameter("userId", inParams.getVariable("g_userId"));
		inDs.addParameter("loginDt", inParams.getVariable("g_loginDt"));

		getSqlManager().update(inDs, "authority.updateUserLogoutLog");

		//사용자 테이블 최종 로그아웃 시간 기록 
		inDs.addParameter("logOutSuccess", "true");
		getSqlManager().update(inDs, "authority.updateIntgUser");

		return outParams;
	}

	/**
	 * 메뉴 클릭 시 로그 생성 
	 * 
	 * @ahthor KongJungil
	 * @since  1. 26.
	 *
	 * @param inParams
	 * @return outParams
	 */
	public Parameters saveUserMenuClickLog(Parameters inParams) {
		String remoteNetworkAddress = inParams.getVariableAsString(GoodseedConstants.CLIENT_IP);

		Parameters outParams = ParametersFactory.createParameters(inParams);
		GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
		inDs.addParameter("ipaddr", remoteNetworkAddress);
		inDs.addParameter("rmk", "clickMenuByUser");
		inDs.addParameter("logCl", "3");
		//inDs.addParameter("rmk", "callProgram");
		//getSqlManager().insert(inDs, "authority.insertUserMenuClickLog");
		return outParams;
	}

	/**
	 * Ip 주소를 Long 형으로 변환
	 * 
	 * @ahthor KongJungil
	 * @since  2. 27.
	 *
	 * @param addr
	 * @return num - Long 형으로 변환된 IP 주소
	 */
	public static long ipToLong(String addr) {
		String[] addrArray = addr.split("\\.");

		long num = 0;
		for(int i = 0; i < addrArray.length; i++) {
			int power = 3 - i;

			num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
		}
		return num;
	}

}
