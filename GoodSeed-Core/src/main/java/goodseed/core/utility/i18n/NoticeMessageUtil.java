/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.i18n;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;

import goodseed.core.common.web.listener.adapter.DefaultContextLoaderAdapter;
import goodseed.core.utility.config.Config;

/**
 * The class NoticeMessageUtil
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class NoticeMessageUtil {

	private static final Log LOG = LogFactory.getLog(NoticeMessageUtil.class);
	private static MessageSourceAccessor messageAccessor = (MessageSourceAccessor)DefaultContextLoaderAdapter
			.getBean("messageSourceAccessor");

	/**
	 * key로 정의된 message를 가져 온다. (대상파일 noticeBundle)<br>
	 *<br>
	 * @param key String key
	 * @return getResultMessage 키에 해당하는 메세지
	 */
	public static String getMessage(String key) {
		try {
			if(messageAccessor == null) {
				messageAccessor = (MessageSourceAccessor)DefaultContextLoaderAdapter.getBean("messageSourceAccessor");
			}
			return messageAccessor.getMessage(key);
		} catch(NoSuchMessageException e) {
			e.printStackTrace();
			return getResultMessage(e, key);
		}
	}

	/**
	 * key로 정의된 message를 가져 온다. (대상파일 noticeBundle)<br>
	 * argument 를 가지는 message.
	 *<br>
	 * @param key String key
	 * @param args message
	 * @return key에 해당하는 메세지
	 */
	public static String getMessage(String key, Object[] args) {
		try {
			if(messageAccessor == null) {
				messageAccessor = (MessageSourceAccessor)DefaultContextLoaderAdapter.getBean("messageSourceAccessor");
			}
			return messageAccessor.getMessage(key, args);
		} catch(NoSuchMessageException e) {
			e.printStackTrace();
			return getResultMessage(e, key);
		}
	}

	/**
	 * 해당 locale의 message를 가져온다.<br>
	 *<br>
	 * @param key String key
	 * @param locale locale정보
	 * @return key에 해당하는 메세지
	 */
	public static String getMessage(String key, Locale locale) {
		try {
			if(messageAccessor == null) {
				messageAccessor = (MessageSourceAccessor)DefaultContextLoaderAdapter.getBean("messageSourceAccessor");
			}
			return messageAccessor.getMessage(key, locale);
		} catch(NoSuchMessageException e) {
			e.printStackTrace();
			return getResultMessage(e, key);
		}
	}

	/**
	 * 해당 locale의 message를 가져온다.<br>
	 * argument 를 가지는 message.
	 *<br>
	 * @param key String key
	 * @param args message
	 * @param locale locale정보
	 * @return key에 해당하는 메세지
	 */
	public static String getMessage(String key, Object[] args, Locale locale) {
		try {
			if(messageAccessor == null) {
				messageAccessor = (MessageSourceAccessor)DefaultContextLoaderAdapter.getBean("messageSourceAccessor");
			}
			return messageAccessor.getMessage(key, args, locale);
		} catch(NoSuchMessageException e) {
			e.printStackTrace();
			return getResultMessage(e, key);
		}
	}

	/**
	 * 
	 * NoSuchMessageException 의 경우 key로 정의된 실패/에러 메세지를 가져 온다.<br>
	 * <br>
	 * @param e NoSuchMessageException
	 * @param key String key
	 * @return 에러 메시지가 담긴 String
	 */
	private static String getResultMessage(NoSuchMessageException e, String key) {
		if(LOG.isWarnEnabled()) {
			LOG.warn(e);
		}
		if(messageAccessor == null) {
			messageAccessor = (MessageSourceAccessor)DefaultContextLoaderAdapter.getBean("messageSourceAccessor");
		}
		return messageAccessor.getMessage(Config.getString("customVariable.msgComErr013"), new String[]{key});
	}

	/**
	 * bind arguments 를 '|' 로 연결된 String 으로 반환.<br>
	 *<br>
	 * @param bindArgs bind argument 배열
	 * @return  '|' 로 연결된 String
	 */
	public static String getBindMessage(Object[] bindArgs) {
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < bindArgs.length; i++) {
			result.append(bindArgs[i].toString());
			if(i != bindArgs.length - 1) {
				result.append("|");
			}
		}
		return result.toString();
	}
	
}
