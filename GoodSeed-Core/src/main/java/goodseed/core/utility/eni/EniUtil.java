package goodseed.core.utility.eni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.MsMessage;
import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.string.StringUtil;

/**
 * The class EniUtil<br>
 * <br>
 * Emergency Interface Notification EniUtil<br>
 * ENI와 관련된 Util class
 * <br>
 * 
 * 
 * @author jay
 * @version 3.0
 * @since 03.27
 * 
 * 
 * 
 */
public class EniUtil {

	private static final Log LOG = LogFactory.getLog(EniUtil.class);

	// reload time setting
	private static long RELOAD_DELAY;
	private static long RELOAD_PERIOD;

	private static String recentSuccessLastModifiedServiceList = "";
	private static String recentSuccessLastModifiedEmpNoList = "";

	// FRAMEONE_ENILIST 테이블 저장 (pk: servic_nm)
	private static Map<String, Map<String, String>> eniListMap = new HashMap<String, Map<String, String>>();

	// FRAMEONE_ENILIST 테이블에 차단 서비스 이름(servic_nm)만 뽑아서 Set 으로 만든 객체 사용조건:1(사용)
	private static Set<String> eniListSet = new HashSet<String>();

	// FRAMEONE_ENIMGR 테이블 저장 (pk: eni_no, seq)
	private static Map<String, Map<String, String>> eniMgrMap = new HashMap<String, Map<String, String>>();

	// FRAMEONE_ENIMGR 테이블에 ENI번호와 전화번호 목록을 뽑아서 만든 객체 
	private static Map<String, Set<String>> eniMgrSet = new HashMap<String, Set<String>>();

	// private static final String ENI_SERVICE_LIST = "eniServiceListLocation"; //eni 서비스 목록

	/**
	 * 타이머 정적 객체
	 */
	private static Timer eniServiceListCheckTimer;

	private static Timer eniEmpNoListCheckTimer;

	/**
	 * ENI SMS 전송 (sendSmsENI) <br>
	 * eni.eni-enable이 "true"가 아닐경우. 그냥 PASS
	 * <br>
	 * 기본적으로 SYSTEM EXCEPTION이 발생할 경우, SMS로 예외가 발생한 메소드를 기준으로 SMS가 전송이 되지만
	 * 필요에 따라 커스터마이징이 필요할 경우, 아래의 예와 같이 ENI_SMS전송기능을 사용 할 수 있다.<br>
	 * <br>
	 * 사용 예제) 강제로 eniEmpNoList.properties 에 100의 eniNo를 가진 수신자에게 긴급메세지를 보내야 하는 경우<br>
	 * <br>
	 * String msg = "문자를 보낼 내용";<br>
	 * MsMessage msMessage = new MsMessage();<br>
	 * msMessage.setMsg(msg);<br>
	 * EniUtil.sendSmsENI(msMessage, "100");
	 * @return sms발송 (boolean)
	 */
	// 최종적으로 ENI-SMS전송을 위한 메소드
	public static boolean sendSmsENI(MsMessage msMessage, String eniNo) {

		//eni.eni-enable이 "true"인경우
		if("true".equals(StringUtils.defaultString(Config.getString("eni.eni-enable")))) {

			// EniNo에 해당하는 celNo 얻기
			List<String> celNoList = readEmpNoInformationFormConfig(eniNo);

			if(celNoList == null || celNoList.size() == 0) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("eniNo 에 해당하는 celNO 가 없습니다. eniNo = " + eniNo);
				}
				return false;
			}

			// 재전송방지 메소드 호출
			int validateCnt = checkEniSmsBefore(celNoList.size(), msMessage);

			// 30초간 재전송 방지를 위한 처리
			if(validateCnt > 0) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("해당 서비스는 30초 전 발송이력이 존재하여 SMS발송을 중단합니다.");
				}
				return false;
			} else {
				return sendSmsENI(msMessage, celNoList, GoodseedConstants.DEFAULT_SQL_MANAGER);
			}

		} else {
			//<eni-enable>이 "true"가 아닐경우. 그냥 PASS
			if(LOG.isDebugEnabled()) {
				LOG.debug("Status of ENI module is disable.");
			}
			return false;
		}
	}

	/**
	 * celNo 리스트에 해당하는 번호로 message 전송<br>
	 * @param msMessage 메세지내용
	 * @param celNo 등록된 리스트
	 * @param sqlManagerId 
	 * @return boolean
	 */
	private static boolean sendSmsENI(MsMessage msMessage, List<String> celNo, String sqlManagerId) {
		boolean ret = true;
		// 송신번호 하이픈 제거
		msMessage.setCallback(StringUtils.defaultString(msMessage.getCallback()).replaceAll("-", ""));

		// 수신자 번호 체크
		//if(celNo.size() == 0) {
		if(celNo.isEmpty() && LOG.isDebugEnabled()) {
			LOG.debug("수신자 전화번호 누락");
			return false;
		}

		// SMS 발송 Queue테이블 기록을 위해 SqlManager 획득
		SqlManager sqlManagerForSmsSend = SqlManagerFactory.getSqlManager(sqlManagerId);

		// ------------------------------------------- 수신자별 루프 - start --------------------------------------------
		for(Object phone : celNo) {
			// 수신번호 하이픈 제거
			msMessage.setPhone(StringUtils.defaultString((String)phone).replaceAll("-", ""));
			if(LOG.isDebugEnabled()) {
				LOG.debug("### ENI msMessage : " + msMessage);
			}

			// SMS 전송 테이블(SMS_MSG) INSERT
			// result은 SMS_MSG의 insert 결과값임.
			// MsMessage생성자의 인자로
			String result = saveSmsMsgENI(msMessage, sqlManagerForSmsSend);
			msMessage.setMsgKey(result);

		}
		// ------------------------------------------- 수신자별 루프 - end --------------------------------------------
		return ret;
	}

	/**
	 * ENI SMS 전송 테이블(saveSmsMsgENI) INSERT<br>
	 * <br>
	 * 
	 * @param msMessage sms메세지 내용
	 * @param sqlManager sqlManager 객체
	 * @return String sms발송 테이블의 pk(MSGKEY)
	 */
	public static String saveSmsMsgENI(MsMessage msMessage, SqlManager sqlManager) {

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("phone", msMessage.getPhone());
		paramMap.put("callback", msMessage.getCallback());
		paramMap.put("msg", msMessage.getMsg());
		paramMap.put("etc1", msMessage.getEtc1());
		paramMap.put("etc2", msMessage.getEtc2());
		paramMap.put("etc3", msMessage.getEtc3());
		paramMap.put("etc4", msMessage.getEtc4());
		// 2.0추가
		paramMap.put("etc5", msMessage.getEtc5());
		// 2.0추가
		paramMap.put("etc6", msMessage.getEtc6());
		paramMap.put("id", msMessage.getId());

		// callback이 없을 경우 기본값 세팅 추가.
		if(paramMap.get("callback").isEmpty()) {
			// eniEmpNoList의 000에 해당하는 번호로 기본값 설정
			String cbDefault = "000";
			List<String> cBackList = EniUtil.readEmpNoInformationFormConfig(cbDefault);
			if(cBackList.isEmpty() || cBackList.size() == 0) {
				paramMap.put("callback", "no callback");
			} else {
				paramMap.put("callback", cBackList.get(0));
			}
		}

		// SMS 테이블 Insert
		// FrameOne v2.0
		// return (String)sqlManager.insert(paramMap, "eniSms.createEniSmsMsg"); 

		// FrameOne v3.0, insert() ==> insert된 행의 갯수 (myBatis 에서 변경)
		String msgKey = null;
		sqlManager.insert(paramMap, "eniSms.createEniSmsMsg");
		// java.math.BigDecimal 타입
		msgKey = String.valueOf(paramMap.get("MSGKEY"));
		return msgKey;
	}

	/**
	 * SystemException이 발생된 ServiceName이 eniServiceList.properties에 등록된 서비스명과 일치하는지 검출하는 메소드
	 * <br>
	 * @param servNm SystemException이 발생된 ServiceName
	 * @return String ServiceName에 해당하는 eniNo
	 */
	public static String readServiceListFromConfig(String servNm) {

		loadEniServiceList();

		if(eniListMap.containsKey(servNm)) {
			return eniListMap.get(servNm).get("ENI_NO");
		} else {
			return null;
		}
	}

	/**
	 * SystemException이 발생된 eniServiceList.properties에 등록된 ServiceName.methodName에 해당하는 celNo(수신번호)를 얻는다.<br>
	 * EniNo에 해당하는 celNo 얻기 <br> 
	 * 
	 * @param eniNo config에 등록된 eni 넘버
	 * @return List<String> 수신번호가 담긴 List
	 */
	public static List<String> readEmpNoInformationFormConfig(String eniNo) {

		loadEniEmpNoList();

		if(eniMgrSet.containsKey(eniNo)) {
			return new ArrayList<String>(eniMgrSet.get(eniNo));

		} else {
			if(LOG.isDebugEnabled()) {
				LOG.debug("eniEmpNoList에 등록되지 않은 eniNo입니다.");
			}
			return null;
		}

	}

	/**
	 * ENI 서비스 목록 (FRAMEONE_ENILIST) 조회<br>
	 */
	public static void loadEniServiceList() {

		RELOAD_DELAY = Config.getLong("timerTask.eniUtil.reloadDelay", 5000);

		RELOAD_PERIOD = Config.getLong("timerTask.eniUtil.reloadPeriod", 10000);

		SqlManager sqlManager = SqlManagerFactory.getSqlManager();
		Map<String, String> inParam = new HashMap<String, String>();

		//사용조건(1)
		inParam.put("USE_YN", "1");

		List<Map<String, String>> eniServiceList = sqlManager.queryForList(inParam, "eniServiceList.getEniServiceList");

		for(int i = 0; i < eniServiceList.size(); i++) {
			eniListMap.put(eniServiceList.get(i).get("SERVICE_NM"), eniServiceList.get(i));
			eniListSet.add(eniServiceList.get(i).get("SERVICE_NM"));
		}
		startEniServiceListCheckTimer();
	}

	/**
	 * ENI 서비스에 해당하는 담당자 전화번호 목록 (FRAMEONE_ENIMGR) 조회<br>
	 */
	public static void loadEniEmpNoList() {

		RELOAD_DELAY = Config.getLong("timerTask.eniUtil.reloadDelay", 5000);

		RELOAD_PERIOD = Config.getLong("timerTask.eniUtil.reloadPeriod", 10000);

		SqlManager sqlManager = SqlManagerFactory.getSqlManager();
		Map<String, String> inParam = new HashMap<String, String>();

		//담당자 목록 사용여부 추가
		inParam.put("USE_YN", "1");
		List<Map<String, String>> eniMgrList = sqlManager.queryForList(inParam, "eniMgrList.getEniMgrList");

		Set<String> eniNoSet = new HashSet<String>();
		for(int i = 0; i < eniMgrList.size(); i++) {
			String tmpEniNo = String.valueOf(eniMgrList.get(i).get("ENI_NO"));
			String tmpSeq = String.valueOf(eniMgrList.get(i).get("SEQ"));
			eniMgrMap.put(tmpEniNo + "-" + tmpSeq, eniMgrList.get(i));
			// {100, 200, 300}
			eniNoSet.add(eniMgrList.get(i).get("ENI_NO"));
		}
		Iterator<String> it = eniNoSet.iterator();
		while(it.hasNext()) {
			String eniNo = it.next();
			Set<String> noSet = new HashSet<String>();
			for(int j = 0; j < eniMgrList.size(); j++) {
				if(eniNo.equals(eniMgrList.get(j).get("ENI_NO"))) {
					noSet.add(eniMgrList.get(j).get("CEL_NO"));
				}
			}
			eniMgrSet.put(eniNo, noSet);
		}

		// 모니터링 타이머 시작
		startEniEmpNoListCheckTimer();
	}

	/**
	 * SMS 문자열 80바이트로 자르기<br>
	 * @param msg
	 * @return String 80바이트로 가공된 SMS내용이 담긴 String
	 */

	public static String smsMsgManufacture(String msg) {
		String tmpMsg = msg;
		byte[] msgByte;
		msgByte = tmpMsg.getBytes();
		int msgByteLen = msgByte.length;

		LOG.debug("%%%msgByteLen = " + msgByteLen);

		if(msgByteLen > 140) {
			tmpMsg = new StringBuilder().append(StringUtil.substringByByteUTF8(msg, 136)).append("...").toString();
		}
		return tmpMsg;
	}

	/**
	 * ENI - SMS 30초 이내 재전송방지를 위한 Validation Check
	 * <br>
	 * @param empNoSize 전화번호 리스트 size
	 * @param msMessage 메세지 내용
	 * @return int 동일내용의 SMS 건수가 담긴 int 
	 */
	private static int checkEniSmsBefore(int empNoSize, MsMessage msMessage) {
		SqlManager sqlManager = SqlManagerFactory.getSqlManager();

		// Config의 eni.sms-logTable-default 값이 true일 때만 SMS_MSG_LOG_YYYYMM 테이블을 사용하고
		// 나머지 경우에는 해당 값 이름의 테이블을 명을 사용함
		String tableName = "";

		if("true".equals(Config.getString("eni.sms-logTable-default", "true"))) {
			String yyyymm = (String)sqlManager.queryForObject("eniSms.selectTmpYYYYMM");
			tableName = new StringBuilder().append("SMS_MSG_LOG_").append(yyyymm).toString();
		} else {
			tableName = Config.getString("eni.sms-logTable-default");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tableName", tableName);
		paramMap.put("empNoCnt", empNoSize);
		paramMap.put("etc5", msMessage.getEtc5());

		//30초 이내 재전송방지 이력Cnt조회
		return (Integer)sqlManager.queryForObject(paramMap, "eniSms.selectEniSentCnt");
	}

	/**
	 * Exception_NO를 들고 오기 위한 메소드
	 * <br>
	 * @return 예외 발생번호가 담긴 String 
	 */
	public static String getExcptNo() {
		SqlManager sqlManager = SqlManagerFactory.getSqlManager();
		return (String)sqlManager.queryForObject("eniSms.selectEniExcptNo");
	}

	/**
	 * FRAMEONE_ENILIST 테이블 모니터링 하는 타이머를 시작한다.<br>
	 */
	private static void startEniServiceListCheckTimer() {
		synchronized(EniUtil.class) {
			// 기존에 타이머가 존재하지 않을 경우에만 생성한다.
			if(eniServiceListCheckTimer == null) {
				// true => daemon thread
				eniServiceListCheckTimer = new Timer("eniServiceListCheckTimer", true);
				eniServiceListCheckTimer.schedule(
						new EniServiceListCheckTimerTask(Thread.currentThread().getContextClassLoader()), RELOAD_DELAY,
						RELOAD_PERIOD);
			}
		}
	}

	/**
	 * <br>
	 * FRAMEONE_ENILIST 테이블 reload를 위해 TimerTask를 구현한 내부 클래스<br>
	 * <br>
	 */
	private static class EniServiceListCheckTimerTask extends TimerTask {

		private ClassLoader classLoader;

		// 생성자를 private 처리
		private EniServiceListCheckTimerTask() {

		}

		private EniServiceListCheckTimerTask(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		// 변경여부를 체크하여, 변경되었으면 Map을 변경된 것으로 교체한다.
		@Override
		public void run() {

			// 현재 Thread에 invoker의 ClassLoader를 셋팅해 주어서, 동일한 ClassLoader에서 로딩한 Config가 동기화의 대상이 되도록 한다.
			Thread.currentThread().setContextClassLoader(classLoader);

			synchronized(EniUtil.class) {

				// 파일을 새로 읽은 후에 별 이상이 없으면, 원래의 정적멤버와 교체하기 위한 Map, Set
				Map<String, Map<String, String>> tempEniServiceMap = new HashMap<String, Map<String, String>>();
				Set<String> tempEniServiceSet = new HashSet<String>();

				try {

					// 리스트의 변경일자가 최근 저장성공한 변경일자와 같으면 reload를 수행하지 않는다.
					SqlManager sqlManager = SqlManagerFactory.getSqlManager();
					String currentLastModified = (String)sqlManager.queryForObject("eniServiceList.getMaxUdt");
					if(currentLastModified == null || currentLastModified.equals(recentSuccessLastModifiedServiceList)) {
						return;
					}
					// -------------------------------------------- 이하 - 파일이 수정된 경우 ------------------------------------------------------

					if(LOG.isInfoEnabled()) {
						LOG.info("eniServiceList was changed. start reloading ...");
					}

					Map<String, String> inParam = new HashMap<String, String>();
					inParam.put("USE_YN", "1");
					List<Map<String, String>> eniServiceList =
							sqlManager.queryForList(inParam, "eniServiceList.getEniServiceList");

					for(int i = 0; i < eniServiceList.size(); i++) {
						tempEniServiceMap.put(eniServiceList.get(i).get("SERVICE_NM"), eniServiceList.get(i));
						tempEniServiceSet.add(eniServiceList.get(i).get("SERVICE_NM"));
					}

					// 변경이 완료되었을 경우 처리
					recentSuccessLastModifiedServiceList = currentLastModified;
					eniListMap = tempEniServiceMap;
					eniListSet = tempEniServiceSet;

					if(LOG.isInfoEnabled()) {
						LOG.info("eniServiceList.properties was reloaded.");
						LOG.info("eniServiceList count : " + eniListSet.size());
					}

				} catch(Exception e) {
					if(LOG.isErrorEnabled()) {
						LOG.error("error : failed to reload eniServiceList");
						LOG.error(e, e);
					}
					e.printStackTrace();
				}

			}

		}
	}

	/**
	 * 프로퍼티 파일을 모니터링 하는 타이머를 시작한다.<br>
	 */
	private static void startEniEmpNoListCheckTimer() {
		synchronized(EniUtil.class) {
			// 기존에 타이머가 존재하지 않을 경우에만 생성한다.
			if(eniEmpNoListCheckTimer == null) {
				// true => daemon thread
				eniEmpNoListCheckTimer = new Timer("eniEmpNoListCheckTimer", true);
				eniEmpNoListCheckTimer.schedule(new EniEmpNoListCheckTimerTask(Thread.currentThread().getContextClassLoader()),
						RELOAD_DELAY, RELOAD_PERIOD);
			}
		}
	}

	/**
	 * <br>
	 * FRAMEONE_ENIMGR 테이블 reload를 위해 TimerTask를 구현한 내부 클래스<br>
	 * <br>
	 */
	private static class EniEmpNoListCheckTimerTask extends TimerTask {

		private ClassLoader classLoader;

		// 생성자를 private 처리
		private EniEmpNoListCheckTimerTask() {

		}

		private EniEmpNoListCheckTimerTask(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		// 변경여부를 체크하여, 변경되었으면 Map을 변경된 것으로 교체한다.
		@Override
		public void run() {

			// 현재 Thread에 invoker의 ClassLoader를 셋팅해 주어서, 동일한 ClassLoader에서 로딩한 Config가 동기화의 대상이 되도록 한다.
			Thread.currentThread().setContextClassLoader(classLoader);

			synchronized(EniUtil.class) {

				// 파일을 새로 읽은 후에 별 이상이 없으면, 원래의 정적멤버와 교체하기 위한 Map, Set
				Map<String, Map<String, String>> tempEniEmpNoMap = new HashMap<String, Map<String, String>>();
				Map<String, Set<String>> tempEniEmpNoSet = new HashMap<String, Set<String>>();

				try {

					// 리스트의 변경일자가 최근 저장성공한 변경일자와 같으면 reload를 수행하지 않는다.
					SqlManager sqlManager = SqlManagerFactory.getSqlManager();
					String currentLastModified = (String)sqlManager.queryForObject("eniMgrList.getMaxUdt");
					if(currentLastModified == null || currentLastModified.equals(recentSuccessLastModifiedEmpNoList)) {
						return;
					}
					// -------------------------------------------- 이하 - 파일이 수정된 경우 ------------------------------------------------------

					if(LOG.isInfoEnabled()) {
						LOG.info("eniMgrList was changed. start reloading ...");
					}

					Map<String, String> inParam = new HashMap<String, String>();
					inParam.put("USE_YN", "1");
					List<Map<String, String>> eniMgrList = sqlManager.queryForList(inParam, "eniMgrList.getEniMgrList");

					Set<String> eniNoSet = new HashSet<String>();
					for(int i = 0; i < eniMgrList.size(); i++) {
						String tmpEniNo = String.valueOf(eniMgrList.get(i).get("ENI_NO"));
						String tmpSeq = String.valueOf(eniMgrList.get(i).get("SEQ"));
						eniMgrMap.put(tmpEniNo + "-" + tmpSeq, eniMgrList.get(i));
						// {100, 200, 300}
						eniNoSet.add(eniMgrList.get(i).get("ENI_NO"));
					}

					Iterator<String> it = eniNoSet.iterator();
					while(it.hasNext()) {
						String eniNo = it.next();
						Set<String> noSet = null;

						for(int j = 0; j < eniMgrList.size(); j++) {
							noSet = new HashSet<String>();
							if(eniNo.equals(eniMgrList.get(j).get("ENI_NO"))) {
								noSet.add(eniMgrList.get(j).get("CEL_NO"));
							}
						}
						eniMgrSet.put(eniNo, noSet);
					}

					// 변경이 완료되었을 경우 처리
					recentSuccessLastModifiedEmpNoList = currentLastModified;
					eniMgrMap = tempEniEmpNoMap;
					eniMgrSet = tempEniEmpNoSet;

					if(LOG.isInfoEnabled()) {
						LOG.info("eniServiceList.properties was reloaded.");
					}

				} catch(Exception e) {
					if(LOG.isErrorEnabled()) {
						LOG.error("error : failed to reload eniEmpNoList");
						LOG.error(e, e);
					}

				}

			}

		}
	}

	/**
	 * 프로퍼티 파일의 내용을 키로 조회하여 해당 키의 값을 리턴한다.
	 * 
	 * @param key 키
	 * @return String 해당 키값에 대한 프로퍼티 파일의 value
	 */
	//	public static String getString(String key) {
	//
	//		String ret = "";
	//		if(StringUtils.isBlank(key)) {
	//			return "";
	//		}
	//		for(Entry e : eniListMap.entrySet()) {
	//			if(((String)e.getKey()).equals(key)) {
	//				ret = (String)e.getValue();
	//				break;
	//			}
	//		}
	//		return ret;
	//	}

	/**
	 * 프로퍼티 파일의 내용을 키로 조회하여 해당 키의 값을 리턴한다.
	 * 
	 * @param key 키
	 * @return String 해당 키값에 대한 프로퍼티 파일의 value
	 */
	//	public static String getStringEmpNo(String key) {
	//
	//		String ret = "";
	//		if(StringUtils.isBlank(key)) {
	//			return "";
	//		}
	//		for(Entry e : eniMgrMap.entrySet()) {
	//			if(((String)e.getKey()).equals(key)) {
	//				ret = (String)e.getValue();
	//				break;
	//			}
	//		}
	//		return ret;
	//	}

	/**
	 * 프로퍼티 파일의 모든 내용을 문자열로 리턴한다.<br>
	 * 
	 * @return 프로퍼티 파일의 모든 내용이 담긴 String
	 */
	//	public static String getAllContentsToString() {
	//		StringBuilder sb = new StringBuilder();
	//		for(Entry e : eniListMap.entrySet()) {
	//			sb.append("\n").append((String)e.getKey()).append("=").append((String)e.getValue());
	//		}
	//		return sb.toString();
	//	}

	/**
	 * ENI 리스트 모니터링 타이머를 중지한다.<br>
	 */
	public static void cancelEniServiceListCheckTimer() {
		if(eniServiceListCheckTimer != null) {
			eniServiceListCheckTimer.cancel();
			eniServiceListCheckTimer.purge();
			eniServiceListCheckTimer = null;
			if(LOG.isDebugEnabled()) {
				LOG.debug("eniServiceListCheckTimer was destroyed");
			}
		}
	}

	/**
	 * ENI 해당 MGR 리스트 모니터링 타이머를 중지한다.<br>
	 */
	public static void cancelEniEmpNoListCheckTimer() {
		if(eniEmpNoListCheckTimer != null) {
			eniEmpNoListCheckTimer.cancel();
			eniEmpNoListCheckTimer.purge();
			eniEmpNoListCheckTimer = null;
			if(LOG.isDebugEnabled()) {
				LOG.debug("eniEmpNoListCheckTimer was destroyed");
			}
		}
	}

}
