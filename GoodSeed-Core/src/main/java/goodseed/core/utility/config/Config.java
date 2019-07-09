/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ResourceUtils;

import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.observer.GoodseedConfigSubject;

/**
 * The class Config
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class Config {

	private static final Log LOG = LogFactory.getLog(Config.class);

	private static GoodseedConfigSubject configSubject;
	private static List<Object> objectList;
	
	// DB에 저장된 Config
	private static CombinedConfiguration config = new CombinedConfiguration();

	// xml 파일에 설정한 Config
	private static CombinedConfiguration configOfXML = new CombinedConfiguration();
	
	// Config file location
	private static String configLocation;
	
	//config reload setting
	//startConfigReloadTimer() 가 호출된 이후 몇 millesecond 후에 reload timer를 시작할 것인가 
	private static long reloadDelay; 
	//reload 체크 주기를 몇 millisecond로 할 것인가
	private static long reloadPeriod;

	// 최근 DB 수정 성공한 일시
	private static String recentSuccessLastModified = "";
	
	// xml 파일 별 가장 최근 수정일시
	private static Map<String, String> recentLastModifiedSuccessMap = new HashMap<String, String>();
	
	//private static String type = "";
	private static Timer configChangeCheckTimer;
	
	// Config는 static member만 사용할 것이므로 함부로 생성하지 못하도록 막자
	// bean 으로 생성하기 위하여 주석처리
	//private Config() {
	//} 
	
	public static void setConfigLocation(String configLocation) {
		Config.configLocation = configLocation;
	}
	
	public static long getReloadDelay() {
		return reloadDelay;
	}
	
	public void setReloadDelay(long reloadDelay) {
		this.reloadDelay = reloadDelay;
	}
	
	public static long getReloadPeriod() {
		return reloadPeriod;
	}
	
	public void setReloadPeriod(long reloadPeriod) {
		this.reloadPeriod = reloadPeriod;
	}

	/**
	 * 	config 관련 설정을 읽어서 config 객체에 저장한다.<br>
	 * <br>
	 * @param objList 설정파일 또는 db결과 리스트 
	 */
	public static void loadConfig(List<Object> objList) {
		
		//-------------------------------------------- (1) XML 설정 로드 시작 ---------------------------------------------------
		String[] locations = org.springframework.util.StringUtils.tokenizeToStringArray(configLocation, ",; \t\n");

		if(locations != null) {
			configOfXML = new CombinedConfiguration();

			// 설정파일 갯수만큼 루프 (현재는 파일이 한 개이므로 루프의 의미는 사실상 없다.)
			for(int i = 0; i < locations.length; i++) {
				String path = org.springframework.util.SystemPropertyUtils.resolvePlaceholders(locations[i]).trim();

				try {
					URL u = ResourceUtils.getURL(path);
					XMLConfiguration c = new XMLConfiguration(u);
					//	c.setReloadingStrategy(new FileChangedReloadingStrategy());	//reloading의 한 가지 방법이지만 다음의 이유로 사용하지 않도록 한다.
					//	   		- 프로퍼티 수정시 오류가 발생했을 경우 대안이 없으므로 바로 에러상태가 된다.
					//	   		- 사용자 요청이 1초도 쉬지 않고 들어온다고 했을 때 5초 간격으로 항상 실행된다.
					configOfXML.addConfiguration(c);
					
					if(LOG.isInfoEnabled()) {
						LOG.info(" XML Config '" + path + "' was loaded.");
						Iterator keys = c.getKeys();
						while(keys.hasNext()) {
							String key = (String) keys.next();
							LOG.info(" XML Config key = "+ key + ", value = " + c.getString(key));
						}
					}
				} catch(Exception e) {
					if(LOG.isErrorEnabled()) {
						LOG.error(e, e);
					}
				}
			}
			configOfXML.setForceReloadCheck(true);
		}
		//-------------------------------------------- (1) XML 설정 로드 끝 -----------------------------------------------------
		
		
		
		//-------------------------------------------- (2) DB 설정 로드 시작 ----------------------------------------------------

		// configType = type;
		Map inParam = new HashMap();
		inParam.put("USE_YN", "1");
		
		SqlManager sqlManager = SqlManagerFactory.getSqlManager();
		List<Map<String, String>> configList = sqlManager.queryForList(inParam,"configList.getAllConfigList"); 
		
		for(int i=0; i<configList.size();i++) {
			config.addProperty(configList.get(i).get("VARIABLE"), configList.get(i).get("VALUE"));
		}
		
		if(LOG.isInfoEnabled()) {
			LOG.info(" DB Config was loaded.");
		}
		config.setForceReloadCheck(true);
		//-------------------------------------------- (2) DB 설정 로드 끝 ------------------------------------------------------
			
		
		
		// config 로드가 완료되었으므로 옵저버에게 공지
		objectList = objList;
		configSubject = new GoodseedConfigSubject(objectList);
		configSubject.notifyObserver();
		
		// config 파일 변경체크 모니터 스레드 시작
		startConfigChangeCheckTimer(locations);
	}


	/**
	 * config를 모니터링 하는 타이머를 시작한다.<br>
	 * <br>
	 * @param locations config 설정 파일 위치
	 * @param type DB 에서 읽어올 구분값 (O: 온라인, B: 배치)
	 */
	private static void startConfigChangeCheckTimer(String[] locations) {
		synchronized(Config.class) {
			//기존에 타이머가 존재하지 않을 경우에만 생성한다.
			if(configChangeCheckTimer == null) { 
				//true => daemon thread
				configChangeCheckTimer = new Timer("configChangeCheckTimer", true); 
				configChangeCheckTimer.schedule(new ConfigChangeCheckTimerTask(Thread.currentThread().getContextClassLoader(), locations), getReloadDelay(),
						getReloadPeriod());
			}
		}
	}

	/**
	 * config의 reload를 위해 TimerTask를 구현한 내부 클래스<br>
	 * <br>
	 *
	 * @author jay
	 * @version 1.0
	 * @since 2. 10.
	 *
	 */
	private static class ConfigChangeCheckTimerTask extends TimerTask {

		private ClassLoader classLoader;
		private String[] locationArray;

		// 생성자를 private 처리
		private ConfigChangeCheckTimerTask() {
			
		}

		private ConfigChangeCheckTimerTask(ClassLoader classLoader, String[] locations) {
			this.classLoader = classLoader;
			this.locationArray = locations;
		}

		// config 변경여부를 체크하여, 변경되었으면 config객체를 변경된 것으로 교체한다.
		@Override
		public void run() {
			//현재 Thread에 invoker의 ClassLoader를 셋팅해 주어서, 동일한 ClassLoader에서 로딩한 Config가 동기화의 대상이 되도록 한다. 
			Thread.currentThread().setContextClassLoader(classLoader);

			synchronized(Config.class) {
				//-------------------------------------------- (1) XML 설정 리로드 시작 ---------------------------------------------------
				CombinedConfiguration tmpConfigOfXML = new CombinedConfiguration();

				if(locationArray != null) {

					// 설정파일의 갯수
					int fileCount = 0; 
					// 수정 성공한 설정파일의 갯수
					int modifySuccessFileCount = 0;

					//설정파일 갯수만큼 루프 (현재는 파일이 한 개이므로 루프의 의미는 사실상 없다.)
					for(int i = 0; i < locationArray.length; i++) {
						String path = org.springframework.util.SystemPropertyUtils.resolvePlaceholders(locationArray[i]).trim();

						try {
							
							//파일의 lastModifed 검사
							File file = ResourceUtils.getFile(path);
							//파일이 존재하지 않으면 skip
							if((file == null) || (!file.isFile())) {
								continue; 
							}
							fileCount++;

							//현재 파일의 수정일시 (파싱 실패 포함)
							String currentLastModified = Long.toString(file.lastModified()); 
							//최근 파일의 수정일시 (성공한 케이스만)
							String recentSuccessLastModified = recentLastModifiedSuccessMap.get(path); 

							//현재파일의 변경일자가 최근 저장성공한 변경일자와 같으면 reload를 수행하지 않는다.
							if(currentLastModified.equals(recentSuccessLastModified)) {
								continue;
							}

							if(LOG.isDebugEnabled()) {
								LOG.debug("Config " + path + " was changed. start reloading ...");
							}

							URL u = ResourceUtils.getURL(path);
							XMLConfiguration c = new XMLConfiguration(u);
							tmpConfigOfXML.addConfiguration(c);

							//변경이 완료되면 공유 static 변수 recentLastModifiedSuccessMap 를 갱신한다.
							recentLastModifiedSuccessMap.put(path, currentLastModified);
							modifySuccessFileCount++;

							if(LOG.isDebugEnabled()) {
								LOG.debug(" XML Config " + path + " was reloaded.");
								
								Iterator keys = c.getKeys();
								while(keys.hasNext()) {
									String key = (String) keys.next();
									LOG.debug(" XML Config key = "+ key + ", value = " + c.getString(key));
								}
								
							}

						} catch(Exception e) {
							if(LOG.isErrorEnabled()) {
								LOG.error(e, e);
							}
						}

					}//for

					//존재하는 모든 파일을 다 읽고 에러가 없을 경우에, 기존에 로딩되어 있던 config와 교체
					if((fileCount > 0) && (fileCount == modifySuccessFileCount)) {

						tmpConfigOfXML.setForceReloadCheck(true);
						configOfXML = tmpConfigOfXML;

						if(LOG.isDebugEnabled()) {
							LOG.debug(" XML Config was reloaded");
						}
					}
				}
				
				//-------------------------------------------- (1) XML 설정 리로드 끝 -----------------------------------------------------
				
				
				
				//-------------------------------------------- (2) DB 설정 리로드 시작 ----------------------------------------------------
			
				CombinedConfiguration tmpConfig = new CombinedConfiguration();
			            
				//가장 마지막 변경일자가 최근 저장성공한 변경일자와 같으면 reload를 수행하지 않는다.
			    SqlManager sqlManager = SqlManagerFactory.getSqlManager();
			    String currentLastModified = (String)sqlManager.queryForObject("configList.getMaxUdt");
			
				//가장 마지막 변경일자가 최근 저장성공한 변경일자와 같으면 reload를 수행하지 않는다.
				if(currentLastModified.equals(recentSuccessLastModified)) {
					return;
				}
			
				if(LOG.isDebugEnabled()) {
					LOG.debug(" DB Config was changed. start reloading ...");
				}
				
				Map inParam = new HashMap();
			    inParam.put("USE_YN", "1");
				
				List<Map<String, String>> configList = sqlManager.queryForList(inParam,"configList.getAllConfigList"); 
				for(int i=0; i<configList.size();i++) {
					tmpConfig.addProperty(configList.get(i).get("VARIABLE"), configList.get(i).get("VALUE"));
				}
				
				//변경이 완료되었을 경우 처리
				recentSuccessLastModified = currentLastModified;
			    config.setForceReloadCheck(true);			
				config = tmpConfig;
				
				if(LOG.isDebugEnabled()) {
					LOG.debug(" DB Config was reloaded.");
				}
				
				//-------------------------------------------- (2) DB 설정 리로드 끝 ------------------------------------------------------
				
				
				
				// config 로드가 완료되었으므로 옵저버에게 공지
				configSubject.setObservers(objectList);
				configSubject.notifyObserver();
			
			}
				
		}
	}

	/**
	 * key 값에 해당하는 문자열을 가져온다.<br>
	 * <br>
	 * @param key
	 * @return String
	 */
	public static String getString(String key) {
		return getString(key, null);
	}
	
	/**
	 * key 값에 해당하는 문자열을 가져온다.<br>
	 * @param key
	 * @param defaultValue 값이 null일 때 가져올 기본 값
	 * @return String
	 */
	public static String getString(String key, String defaultValue) {
		String value = defaultValue;
		
		if(configOfXML == null && config == null) {
			if(LOG.isWarnEnabled()) {
				LOG.warn(" Config was not loaded.");
			}
		} else {
			value = configOfXML.getString(key, config.getString(key, defaultValue));
		}
      		
		return value;
	}
	
	/**
	 * key 값에 해당하는 문자열을 정수 형으로 가져온다.<br>
	 * @param key
	 * @param defaultValue 값이 null일 때 가져올 기본 값
	 * @return int
	 */
	public static int getInteger(String key, int defaultValue) {
		int value = defaultValue;
		
		if(configOfXML == null && config == null) {
			if(LOG.isWarnEnabled()) {
				LOG.warn(" Config was not loaded.");
			}
		} else {
			value = configOfXML.getInteger(key,  config.getInteger(key, defaultValue));
		}
		
		return value;
	}

	/**
	 * key 값에 해당하는 문자열을 Long 타입으로 가져온다.<br>
	 * @param key
	 * @param defaultValue 값이 null일 때 가져올 기본 값
	 * @return long
	 */
	public static long getLong(String key, long defaultValue) {
		long value = defaultValue;
		
		if(configOfXML == null && config == null) {
			if(LOG.isWarnEnabled()) {
				LOG.warn(" Config was not loaded.");
			}
		} else {
			value = configOfXML.getLong(key, config.getLong(key, defaultValue));
		}
		
		return value;
	}

	/**
	 * key값에 해당하는 문자열들을 List로 반환한다.<br>
	 * <br>
	 * @param key  
	 * @return List<String> 
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getStringList(String key) {
		if(configOfXML == null && config == null) {
			if(LOG.isWarnEnabled()) {
				LOG.warn(" Config was not loaded.");
			}
			return null;
		}
		List<String> stringList = new ArrayList();
		List list = configOfXML.getList(key);
		for(Object obj : list) {
			stringList.add((String) obj);
		}
		
		if(stringList.size() == 0) {
			int i = 0;
			while(true) {
				String value = config.getString(key + "[" + i + "]");
				if(null == value){
					break;
				}
				stringList.add(value);
				i++;
			}
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(" Config getStringList = " + stringList);
		}
		
		return stringList;
	}
		
		
	/**
	 * 	config 변경 모니터링 타이머를 중지한다.<br>
	 */
	public static void cancelConfigChangeCheckTimer() {
		if(configChangeCheckTimer != null) {
			configChangeCheckTimer.cancel();
			configChangeCheckTimer.purge();
			configChangeCheckTimer = null;

			if(LOG.isDebugEnabled()) {
				LOG.debug("configChangeCheckTimer was destroyed");
			}
		}
	}
	
}
