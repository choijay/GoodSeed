package com.common.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.utility.config.Config;

/**
 * 인증관련 정보를 관리한다.(HTML)
 * - 로그인/로그아웃 처리
 * - 사용자 정보 조회
 * - 로그인 여부 체크
 * - 사용자 정보 세션설정
 * - 비밀번호 변경(만료된 경우 포함) 처리
 * - 언어셋 조회
 * @author jay
 *
 */
@Service
public class HtmlAuthorityService extends BaseAuthorityService {

	private static final Log LOG = LogFactory.getLog(HtmlAuthorityService.class);

	/**
	 * 로그인 정보 조회
	 *
	 * @param inParams
	 * @return
	 *      result : 000 - 성공
	 *               010 - ID or PWD 불일치
	 *               020 - 최근 로그인 후 90일 초과 -> 계정 사용 불가
	 *               030 - 최초 로그인 사용자 -> 비밀번호 변경 후 재 로그인
	 *               040 - 비밀번호 사용 기간 만료 -> 비밀번호 변경 후 재 로그인
	 *               050 - 비밀번호 오류 횟수 초과 -> 계정 사용 불가
	 *               060 - 임시 비밀번호 사용 -> 비밀번호 변경 후 재 로그인
	 *               070 - 비밀번호 오류 횟수 3회에 도달하여 계정을 잠금처리함.
	 *               080 - 잠긴 계정 사용 불가.
	 *               090 - 잠긴 계정 사용 불가.
	 *               100 - 네트워크 접근 통제로 로그인 불가.
	 */
	public Parameters getLoginDataItg(Parameters inParams) {

		Parameters outParams = ParametersFactory.createParameters(inParams);

		GoodseedDataset dsAuth = getSqlManager().queryForGoodseedDataset(inParams, "authority.getAuthData");

		String remoteNetworkAddress = inParams.getVariableAsString(GoodseedConstants.CLIENT_IP);
		inParams.setVariable("ipaddr", remoteNetworkAddress);

		//remoteNetworkAddress = "54.0.0.1";
		boolean bIpAccess = false;
		String allowAllIpYn = dsAuth.getColumnAsString(0, "ALLOW_ALL_IP_YN");

		if(LOG.isDebugEnabled()) {
			LOG.debug("allowAllIpYn ====>> " + allowAllIpYn);
		}

		//모든 아이피 허용
		if("1".equals(allowAllIpYn)) {
			LOG.debug("allowAllIpYn... ");
			bIpAccess = true;
		}
		//네트워크 접근 통제
		//모든 아이피 허용 이거나 로컬 아이피 인경우 통과
		if(bIpAccess || GoodseedConstants.LOCAL_IP.equals(remoteNetworkAddress)) {
			LOG.debug("bIpAccess1... " + bIpAccess);
			LOG.debug("remoteNetworkAddress... " + remoteNetworkAddress);
			bIpAccess = true;
		} else {
			LOG.debug("bIpAccess2... " + bIpAccess);
			List configAccessIpRangeList = Config.getStringList("accessIpRange");
			if(configAccessIpRangeList != null) {
				int ipRangeSize = configAccessIpRangeList.size();
				for(int index = 0; index < ipRangeSize; index++) {
					String accessIpRange = (String)configAccessIpRangeList.get(index);

					String[] arrIp = accessIpRange.split("~");
					String fromIp = null;
					String toIp = null;
					long iFromIp = 0;
					long iToIp = 0;
					if(arrIp != null && arrIp.length > 0) {
						fromIp = arrIp[0];
						iFromIp = ipToLong(fromIp);
						toIp = arrIp[1];
						iToIp = ipToLong(toIp);
					}
					long iRemoteNetworkAddress = 0;
					if(remoteNetworkAddress != null) {
						//remoteNetworkAddress = remoteNetworkAddress.replace(".", "");
						iRemoteNetworkAddress = ipToLong(remoteNetworkAddress);
					}

					//System.out.println("2 fromIp : " + iFromIp + " toIp : " + iToIp + " iRemoteNetworkAddress : " + iRemoteNetworkAddress);
					//내부 아이피 통과 여부 조회
					if(iRemoteNetworkAddress >= iFromIp && iRemoteNetworkAddress <= iToIp) {
						bIpAccess = true;
						break;
					}
				}

				//허용 아이피에 해당 되는지 검색 (내부 아이피로 통과 안된 경우에만 조회)
				if(!bIpAccess) {
					GoodseedDataset ds = getSqlManager().queryForGoodseedDataset(inParams, "authority.getAllowIpListForCheck");
					for(int index = 0; index < ds.getRowCount(); index++) {
						String allowIp = ds.getColumnAsString(index, "allow_ip");
						if(remoteNetworkAddress.equals(allowIp)) {
							bIpAccess = true;
							break;
						}
					}
				}

			}
		}

		//네트워크 접근 통제에 걸린 경우
		if(!bIpAccess) {
			outParams.setVariable("result", "100");
			// 로그인 실패 로그 기록
			GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
			inDs.put("loginSuccYn", "0");
			getSqlManager().insert(inDs, "authority.insertUserLogByLogin");

			if(LOG.isDebugEnabled()) {
				LOG.debug("result = 100");
			}
			return outParams;
		}

		// 인증 정보를 조회하지 못한 경우
		if(dsAuth.getRowCount() == 0) {
			// ID or PWD 불일치.
			outParams.setVariable("result", "010");
			if(LOG.isDebugEnabled()) {
				LOG.debug("result = 010");
			}
			return outParams;
		} else {

			// 상태 코드
			String useStsCd = StringUtils.defaultString((String)dsAuth.get("USERSTSCD"), "02");

			// 현재 비밀번호 불일치 카운트
			int pwdFailCnt = (Integer) dsAuth.get("FAILINCNT");
			// 최근 비밀번호 변경일로 부터 현재까지의 날짜
			int pwdRevDyDiff = 0;
			if((dsAuth.get("PWDCHGDT") != null) && (dsAuth.get("PWDREVBYDIFF") != null)) {
				pwdRevDyDiff = Integer.parseInt(StringUtils.defaultString((String)dsAuth.get("PWDREVBYDIFF"), "0"), 10);
			}

			String pwd = StringUtils.defaultString((String)dsAuth.get("PWDNO"), "");
			String inPwd = inParams.getVariableAsString("pwd");
			String tmpPwdYn = (String)dsAuth.get("TMPPWDYN");

			// 잠긴 계정
			if("02".equals(useStsCd) && !"1".equals(tmpPwdYn)) {
				outParams.setVariable("result", "080");
				// 로그인 실패 로그 기록
				GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
				inDs.put("loginSuccYn", "0");
				getSqlManager().insert(inDs, "authority.insertUserLogByLogin");
				if(LOG.isDebugEnabled()) {
					LOG.debug("result = 080");
				}
				return outParams;
			}

			GoodseedDataset dsLogin = getSqlManager().queryForGoodseedDataset(inParams, "authority.getLoginHst");

			// 비밀번호 오류 횟수 초과
			if(pwdFailCnt >= 3) {
				outParams.setVariable("result", "050");
				//outParams.setErrorMessage("MSG_LGN_ERR_050");
				// 로그인 실패 로그 기록
				GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
				inDs.put("loginSuccYn", "0");
				getSqlManager().insert(inDs, "authority.insertUserLogByLogin");
				if(LOG.isDebugEnabled()) {
					LOG.debug("result = 050");
				}
				return outParams;
			}

			// 비밀번호 사용기간 만료
			if(pwdRevDyDiff > 180) {
				// 비밀번호 사용기간 만료
				outParams.setVariable("result", "040");
				//outParams.setErrorMessage("MSG_LGN_ERR_040");
				// 로그인 실패 로그 기록
				GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
				inDs.put("loginSuccYn", "0");
				getSqlManager().insert(inDs, "authority.insertUserLogByLogin");
				if(LOG.isDebugEnabled()) {
					LOG.debug("result = 040");
				}
				return outParams;
			}

			// 비밀번호 불일치
			if(!pwd.equals(inPwd)) {
				pwdFailCnt = pwdFailCnt + 1;
				GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
				if(pwdFailCnt == 3) {
					// 비밀번호 오류 횟수 3회에 도달하여 해당 계정을 잠금 처리함
					outParams.setVariable("result", "070");
					inDs.put("pwdFail3Times", "true");
				} else {
					// ID or PWD 불일치.
					outParams.setVariable("result", "010");
				}

				// 비밀번호 불일치 카운트 증가
				inDs.put("pwdFail", "true");
				getSqlManager().update(inDs, "authority.updateIntgUser");
				// 로그인 실패 로그 기록
				inDs.put("loginSuccYn", "0");
				getSqlManager().insert(inDs, "authority.insertUserLogByLogin");
				if(LOG.isDebugEnabled()) {
					LOG.debug("result = 010, pwd = " + pwd + ", inPwd = " + inPwd);
				}
				return outParams;
			}

			//  최근 로그인 후 90일이 지난 사용자
			int loginDtDiff = 0;
			if(dsAuth.get("LOGINDTDIFF") != null) {
				loginDtDiff = Integer.parseInt(StringUtils.defaultString((String)dsAuth.get("LOGINDTDIFF")), 10);
			}

			if(loginDtDiff > 90) {
				// 최근 로그인 후 90일이 지난 사용자 계정 잠금
				outParams.setVariable("result", "020");
				//outParams.setErrorMessage("MSG_LGN_ERR_020");
				GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
				inDs.put("userStsCd", "02");
				getSqlManager().update(inDs, "authority.updateIntgUser");
				// 로그인 실패 로그 기록
				inDs.put("loginSuccYn", "0");
				getSqlManager().insert(inDs, "authority.insertUserLogByLogin");
				if(LOG.isDebugEnabled()) {
					LOG.debug("result = 020");
				}
				return outParams;
			}

			/**
			 * 아래 최초 로그인과 초기 패스워드 상태 중 초기 패스워드 로직은 항상 안타는데???
			 */
			// 최초 로그인 사용자 - 패스워드 변경 후 재 로그인
			if(dsLogin.getRowCount() == 0 && "1".equals(tmpPwdYn)) {
				// 최초 로그인 사용자 비밀번호 변경
				outParams.setVariable("result", "030");
				return outParams;
			}

			// 초기 패스워드 상태
			if("03".equals(useStsCd) && "1".equals(tmpPwdYn)) {
				// 최초 로그인 사용자 비밀번호 변경
				outParams.setVariable("result", "090");
				return outParams;
			}

			// 임시 비밀번호 발급 상태
			if("02".equals(useStsCd) && "1".equals(tmpPwdYn)) {
				// 임시 비밀번호 발급상태
				outParams.setVariable("result", "060");
				return outParams;
			}

			//직전 로그인이 비정상 되었는지 조회
			String multiLoginCheck = Config.getString("multiLoginCheck");
			String loginDtBefore = null;
			String lotDtBefore = null;
			//정상 종료 또는 다중로그인 체크 안할때.
			String logoutFailYn = "0";

			if("true".equals(multiLoginCheck)) {
				GoodseedDataset loginHist = getSqlManager().queryForGoodseedDataset(inParams, "authority.getLoginHstBeforeList");

				if(loginHist != null && loginHist.getRowCount() > 1) {
					loginDtBefore = loginHist.getColumnAsString(1, "LOGINDT");
					lotDtBefore = loginHist.getColumnAsString(1, "LOTDT");
					if(loginDtBefore != null && lotDtBefore == null) {
						//비정상종료(또는 다른 로그인 존재)
						logoutFailYn = "1";
					}
				}
			}

			outParams.setVariable("logoutFailYn", logoutFailYn);
			outParams.setVariable("g_logoutFailDt", loginDtBefore);

			// 로그인 성공.
			outParams.setVariable("result", "000");
			outParams.setVariable("userId", dsAuth.getString("USERID"));
			outParams.setVariable("userNm", dsAuth.getString("USERNM"));
			//outParams.setVariable("authSys", authSys);

		}
		//log.debug(outParams);
		if(LOG.isDebugEnabled()) {
			LOG.debug("result = 000");
		}

		return outParams;
	}

	/**
	 * 사용자 정보 조회
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getUserData(Parameters inParams) {

		String remoteNetworkAddress = inParams.getVariableAsString(GoodseedConstants.CLIENT_IP);
		inParams.setVariable("ipaddr", remoteNetworkAddress);

		Parameters outParams = ParametersFactory.createParameters(inParams);

		GoodseedDataset dataset = getSqlManager().queryForGoodseedDataset(inParams, "authority.getUserData");
		if(dataset == null || dataset.getRowCount() == 0) {
			outParams.setVariable("RESULT_CD", "-1");
			// 사용자 정보를 찾을 수 없음
			outParams.setVariable("RESULT_MSG", "MSG_LGN_ERR_002");
			return outParams;
		} else {
			outParams.setVariable("g_userId", dataset.get("USERID"));
			outParams.setVariable("g_userNm", dataset.get("USERNM"));
			outParams.setVariable("g_useStsCd", dataset.get("USERSTSCD"));
			outParams.setVariable("g_regDt", dataset.get("REGDTM"));
			outParams.setVariable("g_updDt", dataset.get("UPDDTM"));
			outParams.setVariable("g_startSysCl", dataset.get("STARTSYSCL"));
			outParams.setVariable("g_empNo", dataset.get("EMPNO"));
			outParams.setVariable("g_mobNo", dataset.get("MOBPNO"));
			outParams.setVariable("g_telNo", dataset.get("TELNO"));
			outParams.setVariable("g_emailAddr", dataset.get("EMAILADDR"));
			outParams.setVariable("g_comCd", dataset.get("COMCD"));
			outParams.setVariable("g_deptCd", dataset.get("DEPTCD"));
			outParams.setVariable("g_deptNm", dataset.get("DEPTNM"));
			outParams.setVariable("g_sessionTimeDiff", Config.getString("sessionTimeExpired"));
			outParams.setVariable("g_multiLoginCheck", Config.getString("multiLoginCheck"));
			outParams.setVariable("g_ipAddr", remoteNetworkAddress);
		}

		// 로그인 성공 로그 기록
		GoodseedDataset inDs = inParams.getGoodseedDatasetInstance();
		inDs.put("loginSuccYn", "1");

		// FrameOne v3.0, insert() ==> insert된 행의 갯수 (myBatis 에서 변경)
		getSqlManager().insert(inDs, "authority.insertUserLogByLogin");
		String loginDt = (String)inDs.getVariable("loginDt");

		/**
		 * 사용자 테이블 최종 로그인 시간 기록<br>
		 * <br>
		 * 로그인 실패기록 초기화<br>
		 * <br>
		 */
		inDs.put("loginSuccess", "true");

		//멀티로그인 체크를 위해서 로그인 날짜를 비교 키값으로 사용한다.
		//sys_user 테이블의 loginCheck 컬럼에 로그인 날짜를 입력한다.
		inDs.put("loginChk", loginDt);
		getSqlManager().update(inDs, "authority.updateIntgUser");

		outParams.setVariable("g_loginDt", loginDt);
		outParams.setVariable("RESULT_CD", "0");

		return outParams;
	}

}
