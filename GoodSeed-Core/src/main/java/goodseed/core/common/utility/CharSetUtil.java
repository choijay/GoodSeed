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

import javax.servlet.ServletRequest;

import goodseed.core.utility.config.Config;

/**
 * The class CharSetUtil<p>
 * <p>
 * CharSetUtil 에서는 Encoding 에 대한 캐릭터 메소드를 제공한다.
 *
 * @author jay
 * @version 1.0
 *
 */
public class CharSetUtil {

	/**
	 * 	기본 캐릭터 셋
	 */
	public static final String DEFAULT_CHARSET = resolveDefaultCharSet();

	/**
	 * LOG
	 */
	//private static final Log LOG = LogFactory.getLog(CharSetUtil.class);

	/**
	 * 기본 캐릭터 셋 문자열을 리턴한다.
	 * 
	 * @return
	 */
	public static String resolveDefaultCharSet() {

		/*if(LOG.isDebugEnabled()) {
			LOG.debug("CharSet : " + Config.getString("charset.default", "UTF-8"));
		}*/

		return Config.getString("charset.default", "UTF-8");
	}

	/**
	 *	기본 캐릭터 문자열을 리턴한다.
	 * 
	 * @return
	 */
	public static String getDefaultCharSet() {
		return DEFAULT_CHARSET;
	}

	/**
	 *	JSON 처리 캐릭터 셋을 리턴한다.(JSON 의 경우에는 UTF-8 만 처리함)
	 * 
	 * @return
	 */
	public static String getJsonCharSet() {
		return "UTF-8";
	}

	public static String getDefaultCharSet(ServletRequest request) {
		String reqCharSet = request.getCharacterEncoding();
		/*if(LOG.isDebugEnabled()) {
			LOG.debug("request encoding=[" + reqCharSet + "]");
		}*/

		/**
		 * request 객체에 인코딩 캐릭터 셋이 있을 경우에는 해당
		 * 캐릭터 셋을 반환하고, 없을 경우에는 default charset 을 반환한다.
		 */
		if(reqCharSet == null || "".equals(reqCharSet)) {
			return DEFAULT_CHARSET;
		} else {
			return reqCharSet;
		}
	}

}
