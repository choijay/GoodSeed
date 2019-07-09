/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 *
 * Revision History
 * Author			Date				Description
 * -------------	--------------		------------------
 * KongJungil		2010. 6. 16.		First Draft.
 */
package goodseed.core.common.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.utility.config.Config;

/**
 * The class AllowedURIListUtil<br>
 * <br>
 * 권한체크예외URI 관리 클래스<br>
 * HTML 채널에서 사용자가 접근하는 모든 프로그램 URI는 권한체크 필터를 통과하도록 되어 있는데, 권한체크를 하지 않는 예외목록을 정의하는 기능이 필요하여<br>
 * 본 클래스에서 권한체크예외URI 목록을 메모리에 로딩하고 지속적으로 관리하는 기능을 수행한다.<br>  
 *
 * @author jay
 * @version 3.0
 * @since  3. 12.
 *
 */
public final class AllowedURIListUtil{

	private static final Log LOG = LogFactory.getLog(AllowedURIListUtil.class);


	//reload time setting
	private static long RELOAD_DELAY; 
	private static long RELOAD_PERIOD;	

	/**
	 * 	모든 목록을 그대로 저장하는 Map
	 */
	private static Map<String, String> allowedURIMap = new HashMap<String, String>();

	/**
	 * 목록에서 "권한체크예외" URI 목록만 따로 뽑아서 Set으로 만든 객체
	 * 여기여 포함된 URI들은 권한체크를 거치지 않고 그대로 수행된다.
	 */
	private static Set<String> allowedURISet = new HashSet<String>();

	/**
	 * 	목록에서 "로그인되어 있을 경우에 대한 권한체크예외" URI 목록만 따로 뽑아서 Set으로 만든 객체
	 * 여기여 포함된 URI들은 로그인만 되어 있다면 권한체크를 거치지 않고 그대로 수행된다.
	 */
	private static Set<String> userAllowedURISet = new HashSet<String>();

	/**
	 * 	최근 수정 성공한 일시
	 */
	private static String recentSuccessLastModified = "";
	
	/**
	 * 	타이머 정적 객체
	 */
	private static Timer allowedURIListCheckTimer;
	

	//외부 생성 제한
	private AllowedURIListUtil() {
		
	} 

	/**
	 * 	목록을 읽어와 정적변수에 저장한다.
	 * 
	 * @param allowedURIListLocation 프로퍼티 파일 경로
	 */
	public static void loadAllowedURIList() {

			RELOAD_DELAY = Config.getLong("timerTask.allowedURIListUtil.reloadDelay", 500000);
			
			RELOAD_PERIOD = Config.getLong("timerTask.allowedURIListUtil.reloadPeriod", 1000000);
			
			if(LOG.isDebugEnabled())
				LOG.debug("AllowedURIListUtil.java config에 설정된 RELOAD_DELAY:" + RELOAD_DELAY + ", RELOAD_PERIOD:" + RELOAD_PERIOD);
			
			SqlManager sqlManager = SqlManagerFactory.getSqlManager();

			Map inParam = new HashMap();
			inParam.put("USE_YN", "1");
			
			List<Map<String, String>> allowedURIList = sqlManager.queryForList(inParam,"allowedURIList.getAllowedURIList"); 
			
			for(int i=0; i<allowedURIList.size();i++) {

				//allowedURIMap에 넣기.
				allowedURIMap =allowedURIList.get(i);
			}
			
			
			List<Map<String, String>> baseAllowedURIList = sqlManager.queryForList(inParam,"allowedURIList.getBaseAllowedURIList"); 
			
				//allowedURISet에 넣기
			for(int i=0; i<baseAllowedURIList.size();i++) {
					
				allowedURISet.add(baseAllowedURIList.get(i).get("ALLOW_LIST"));
			}
			
			List<Map<String, String>> userAllowedURIList = sqlManager.queryForList(inParam,"allowedURIList.getUserAllowedURIList"); 
			
				//userAllowedURISet에 넣기
			for(int i=0; i<userAllowedURIList.size();i++) {
				
				userAllowedURISet.add(userAllowedURIList.get(i).get("ALLOW_LIST"));
			}

			//모니터링 타이머 시작
			startAllowedURIListCheckTimer(); 
	
	}


	/**
	 *  모니터링 하는 타이머를 시작한다.
	 */
	private static void startAllowedURIListCheckTimer() {
		synchronized(AllowedURIListUtil.class) {
			//기존에 타이머가 존재하지 않을 경우에만 생성한다.
			if(allowedURIListCheckTimer == null) { 
				//true => daemon thread
				allowedURIListCheckTimer = new Timer("allowedURIListCheckTimer", true); 
				allowedURIListCheckTimer.schedule(new AllowedURIListCheckTimerTask(Thread.currentThread().getContextClassLoader()),RELOAD_DELAY, RELOAD_PERIOD);
			}
		}
	}

	/**
	 * <br>
	 *  reload를 위해 TimerTask를 구현한 내부 클래스<br>
	 * <br>
	 *
	 * @author jay
	 * @version 1.0
	 * @since 2. 10.
	 *
	 */
	private static class AllowedURIListCheckTimerTask extends TimerTask {

		private ClassLoader classLoader;

		//생성자를 private 처리
		private AllowedURIListCheckTimerTask() {
			
		}

		private AllowedURIListCheckTimerTask(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		// 변경여부를 체크하여, 변경되었으면 프로퍼티 Map을 변경된 것으로 교체한다.
		@Override
		public void run() {

			//현재 Thread에 invoker의 ClassLoader를 셋팅해 주어서, 동일한 ClassLoader에서 로딩한 Config가 동기화의 대상이 되도록 한다. 
			Thread.currentThread().setContextClassLoader(classLoader);

			synchronized(AllowedURIListUtil.class) {

				//목록을 새로 읽은 후에 별 이상이 없으면, 원래의 정적멤버와 교체하기 위한 Map, Set
				Map<String, String> tempAllowedURIMap = new HashMap<String, String>();
				Set<String> tempAllowedURISet = new HashSet<String>();
				Set<String> tempUserAllowedURISet = new HashSet<String>();
					
					SqlManager sqlManager = SqlManagerFactory.getSqlManager();
					
					//가장 마지막 변경일자가 최근 저장성공한 변경일자와 같으면 reload를 수행하지 않는다.
				
					String currentLastModified = (String)sqlManager.queryForObject("allowedURIList.getMaxUdt");
					
					if(currentLastModified.equals(recentSuccessLastModified)) {					
						return;
					}

					//-------------------------------------------- 이하 - 목록 내용이수정된 경우 ------------------------------------------------------

				
					if(LOG.isDebugEnabled()) {
						LOG.debug("allowedURIList was changed. start reloading ...");
					}
								
					Map inParam = new HashMap();
					inParam.put("USE_YN", "1");
					
					List<Map<String, String>> allowedURIList = sqlManager.queryForList(inParam,"allowedURIList.getAllowedURIList"); 
					
					for(int i=0; i<allowedURIList.size();i++) {

						//allowedURIMap에 넣기
						tempAllowedURIMap =allowedURIList.get(i);
					}
					
					List<Map<String, String>> baseAllowedURIList = sqlManager.queryForList(inParam,"allowedURIList.getBaseAllowedURIList"); 
					
						//allowedURISet에 넣기
					for(int i=0; i<baseAllowedURIList.size();i++) {
							
						tempAllowedURISet.add(baseAllowedURIList.get(i).get("ALLOW_LIST"));
					}
				
					List<Map<String, String>> userAllowedURIList = sqlManager.queryForList(inParam,"allowedURIList.getUserAllowedURIList"); 
					
						//userAllowedURISet에 넣기
					for(int i=0; i<userAllowedURIList.size();i++) {
						
							tempUserAllowedURISet.add(userAllowedURIList.get(i).get("ALLOW_LIST"));
					}



					//변경이 완료되었을 경우 처리
					recentSuccessLastModified = currentLastModified;
					allowedURIMap = tempAllowedURIMap;
					allowedURISet = tempAllowedURISet;
					userAllowedURISet = tempUserAllowedURISet;

					
					if(LOG.isDebugEnabled()) {
						LOG.debug("allowedURIList was reloaded.");
					}
					if(LOG.isInfoEnabled()) {
						LOG.info("allowedURILists was reloaded.");
						LOG.info("allowedURISet count : " + allowedURISet.size());
						LOG.info("userAllowedURISet count : " + userAllowedURISet.size());
					}

			}

		}
	}

	/**
	 * 	 모든 내용을 문자열로 리턴한다.<br><삭제>
	 * 
	 * @return  모든 내용이 담긴 String
	 */
	public static String getAllContentsToString() {
		StringBuilder sb = new StringBuilder();
		for(Entry e : allowedURIMap.entrySet()) {
			sb.append("\n").append((String)e.getKey()).append("=").append((String)e.getValue());
		}
		return sb.toString();
	}

	/**
	 * 	권한체크예외URI목록 Set을 리턴한다.
	 * 
	 * @return 권한체크예외URI목록 Set
	 */
	public static Set<String> getAllowedURISet() {
		return allowedURISet;
	}

	/**
	 * 	로그인 되어 있을 경우에 대한 권한체크예외URI목록 Set을 리턴한다.
	 * 
	 * @return 로그인 되어 있을 경우에 대한 권한체크예외URI목록 Set
	 */
	public static Set<String> getUserAllowedURISet() {
		return userAllowedURISet;
	}

	/**
	 * 	 내용을 키로 조회하여 해당 키의 값을 리턴한다.<삭제>
	 * 
	 * @param key 키
	 * @return 해당 키값에 대한 프로퍼티 파일의 value
	 */
	public static String getString(String key) {

		String ret = "";
		if(StringUtils.isBlank(key)) {
			return "";
		}
		for(Entry e : allowedURIMap.entrySet()) {
			if(((String)e.getKey()).equals(key)) {
				ret = (String)e.getValue();
				break;
			}
		}
		return ret;
	}

	/**
	 * 	권한체크예외URI목록 모니터링 타이머를 중지한다.
	 */
	public static void cancelAllowedURIListCheckTimer() {
		if(allowedURIListCheckTimer != null) {
			allowedURIListCheckTimer.cancel();
			allowedURIListCheckTimer.purge();
			allowedURIListCheckTimer = null;
			if(LOG.isDebugEnabled()) {
				LOG.debug("allowedURIListCheckTimer was destroyed");
			}
		}
	}

}
