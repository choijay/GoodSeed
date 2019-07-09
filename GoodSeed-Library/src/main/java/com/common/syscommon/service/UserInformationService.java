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

import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.MailMessage;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.mail.MailUtil;
import goodseed.core.utility.security.EncryptionUtil;

/**
 *
 * The class RightsService
 * <br>
 *		사용자 관리를 조회/저장한다.<br>
 * <br>
 * @author jay
 * @version 1.0
 *
 */
@Service
public class UserInformationService extends GoodseedService {

	private static final String INITIAL_PASSWORD = "1111";
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
	 *  사용자 리스트 조회 (Mobile)
	 *
	 * @ahthor KimSooJung
	 * @since  9. 21.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters getUserDtlListForMobile(Parameters inParams) {

		Parameters outParams = ParametersFactory.createParameters(inParams);

		outParams.setGoodseedDataset("ds_userDtl",
				getSqlManager().queryForGoodseedDataset(inParams, "userInformation.getUserDtlListForMobile"));
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "userInformation.getUserDtlListForMobileTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_userDtl").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 *  사용자 조회 (Mobile)
	 *  
	 * @ahthor KimSooJung
	 * @since  9. 21.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters getUserDtlForMobile(Parameters inParams) {

		Parameters outParams = ParametersFactory.createParameters(inParams);

		outParams.setGoodseedDataset("ds_userDtl",
				getSqlManager().queryForGoodseedDataset(inParams, "userInformation.getUserDtlForMobile"));

		if(outParams.getGoodseedDataset("ds_userDtl").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}

	/**
	 * 사용자 관리 저장
	 *
	 * @ahthor KimByungWook
	 * @since  2. 6.
	 *
	 * @ahthor shks
	 * @since  7. 21.
	 * 사용자 계정 프로세스
	 * 1. 관리자가 신규 사용자 등록
	 * 	- USER_STS_CD(03), TMP_PWD_YN(1), USE_YN(1)
	 * 2. 관리자가 사용자 비밀번호 초기화
	 *  - USER_STS_CD(02), TMP_PWD_YN(1)
	 * 3. 관리자가 사용자 미사용 처리
	 *  - 사용여부 체크 : USER_STS_CD(99)
	 *  - 사용여부 언체크 : USER_STS_CD(01)
	 * 4. 사용자가 비밀번호 변경
	 *  - USER_STS_CD(01), TMP_PWD_YN(0)
	 * @param inParam
	 * @return
	 */
	public Parameters saveUser(Parameters inParam) {
		/**
		 * client IP address
		 */
		String remoteNetworkAddress = inParam.getVariableAsString(GoodseedConstants.CLIENT_IP);

		Parameters outParam = ParametersFactory.createParameters(inParam);
		GoodseedDataset dsUserDtl = inParam.getGoodseedDataset("ds_userDtl");

		GoodseedDataset inDs = inParam.getGoodseedDatasetInstance();

		if(dsUserDtl != null) {
			/**
			 * Insert and Update
			 */
			// 생성한 패스워드
			String strPwdNo;
			// 암호화된 패스워드
			String strEncryptPwdNo;

			for(int i = 0; i < dsUserDtl.getRowCount(); i++) {
				//strPwdNo = this.generateNewTempPwd();
				strPwdNo = INITIAL_PASSWORD;
				dsUserDtl.setActiveRow(i);
				String userId = dsUserDtl.getColumnAsString(i, "USER_ID");
				inDs.put("USER_ID", userId);
				inDs.put("IP_ADDR", remoteNetworkAddress);

				if(dsUserDtl.getRowStatus(i).equals(GoodseedConstants.INSERT)) {
					/**
					 * Insert
					 */
					strEncryptPwdNo = EncryptionUtil.digestSHA256(strPwdNo);
					dsUserDtl.setColumn(i, "PWD_NO", strEncryptPwdNo);
					getSqlManager().insert(dsUserDtl, "userInformation.insertUser");

					inDs.put("LOG_CL", "5");
					inDs.put("RMK", "createUser");
					getSqlManager().insert(inDs, "userInformation.insertUserAuthorityLog");
				} else if(dsUserDtl.getRowStatus(i).equals(GoodseedConstants.UPDATE)) {
					/**
					 * Update
					 */
					String initYn = dsUserDtl.getColumnAsString(i, "INIT_YN");

					/**
					 * 관리자에 의한 비밀번호 초기화
					 */
					if("1".equals(initYn)) {
						strEncryptPwdNo = EncryptionUtil.digestSHA256(strPwdNo);

						dsUserDtl.setColumn(i, "NEW_PWD", strEncryptPwdNo);
						dsUserDtl.setColumn(i, "USER_STS_CD", "02");
						dsUserDtl.setColumn(i, "TMP_PWD_YN", "1");
						getSqlManager().update(dsUserDtl, "userInformation.updatePwd");

						/**
						 * 임시비밀번호 생성
						 */
						inDs.put("LOG_CL", "9");
						inDs.put("RMK", "initializePassword");
						inDs.put("PWD_NO", strEncryptPwdNo);
						getSqlManager().insert(inDs, "userInformation.insertUserPwdChangeLog");

						if(dsUserDtl.getColumnAsString(i, "MAIL_ADDR") != null) {
							sendNotifiedMail(Config.getString("email.config.senderAddr"),
									inParam.getVariableAsString("g_userId"), dsUserDtl.getColumnAsString(i, "MAIL_ADDR"),
									dsUserDtl.getColumnAsString(i, "USER_NM"), strPwdNo);
						}
					}

					String useYn = dsUserDtl.getColumnAsString(i, "USE_YN");

					/**
					 * 사용/사용안함 설정
					 */
					if("0".equals(initYn)) {
						dsUserDtl.setColumn(i, "USER_STS_CD", "0".equals(useYn) ? "99" : "01");
					}

					/**
					 * 그룹보안정책 적용(사용자 정보 변경사항 기록)
					 */
					inDs.put("LOG_CL", "0".equals(useYn) ? "7" : "6");
					inDs.put("RMK", "0".equals(useYn) ? "deleteUser" : "modifyUser");
					getSqlManager().insert(inDs, "userInformation.insertUserAuthorityLog");

					/**
					 * 사용자 정보 수정
					 */
					getSqlManager().update(dsUserDtl, "userInformation.updateUser");
				}
			}
		}

		outParam.setGoodseedDataset("ds_userDtl", dsUserDtl);
		outParam.setMessage("MSG_COM_SUC_003");
		outParam.setStatusMessage("MSG_COM_SUC_999", new String[] {"비밀번호 초기화 : 1111"});
		return outParam;
	}

	/**
	 * 임시비밀번호를 사용자에게 메일로 통지<br>
	 * <br>
	 * 
	 * @ahthor shks
	 * @since 07.22
	 * 
	 */
	private void sendNotifiedMail(String senderEmailAddress, String senderId, String receiptEmailAddress, String receiptName,
			String plainTextPassword) {

		/**
		 * 생성자 인수 - (로그파일타입, 서비스명)
		 */
		MailMessage mailMessage = new MailMessage(MailUtil.LOG_TP_DB, "UserInformationService.saveUser");
		/**
		 * 발송 할  이메일 주소
		 */
		mailMessage.addRecipient(receiptEmailAddress);
		/**
		 * 메일 템플릿
		 */
		mailMessage.setId("tmpNewPwd.mtp");
		/**
		 * 보내는 사람 이메일 주소
		 */
		mailMessage.setSender(senderEmailAddress);
		/**
		 * 메일 제목
		 */
		mailMessage.setTitle("비밀번호 초기화 이메일 발송");
		/**
		 * 보내는 사람 아이디
		 */
		mailMessage.setSenderId(senderId);
		/**
		 * 템플릿 바인딩 변수
		 */
		mailMessage.addContentsArg("initPwd", plainTextPassword);
		/**
		 * 템플릿 바인딩 변수
		 */
		mailMessage.addContentsArg("memberName", receiptName);

		MailUtil.sendMail(mailMessage, "UTF-8");
	}

	/**
	 *	임시 비밀번호 생성 .
	 *
	 * @ahthor 김혜리
	 * @since 03.03
	 *
	 * @return
	 **/
	private String generateNewTempPwd() {
		StringBuilder sbStraUpw = new StringBuilder();
		StringBuilder sbStrAUpw = new StringBuilder();
		StringBuilder sbNumUpw = new StringBuilder();
		StringBuilder sbSpeUpw = new StringBuilder();

		/**
		 * 소문자 a
		 */
		char a = 97;
		for(int i = 0; i < 2; ++i) {
			int r = (int)Math.round(Math.random() * 25);
			sbStraUpw.append(String.valueOf((char)(a + r)));
		}

		/**
		 * 대문자 A
		 */
		char capitalA = 65;
		for(int i = 0; i < 2; ++i) {
			int r = (int)Math.round(Math.random() * 25);
			sbStrAUpw.append(String.valueOf((char)(capitalA + r)));
		}

		for(int i = 0; i < 2; ++i) {
			int r = (int)Math.round(Math.random() * 9);
			sbNumUpw.append(Integer.toString(r));
		}

		char spe = 33;
		for(int i = 0; i < 2; ++i) {
			int r = (int)Math.round(Math.random() * 12);
			sbSpeUpw.append(String.valueOf((char)(spe + r)));
		}

		return sbStraUpw.toString() + sbStrAUpw.toString() + sbNumUpw.toString() + sbSpeUpw.toString();
	}

}
