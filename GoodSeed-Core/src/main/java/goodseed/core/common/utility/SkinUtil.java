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

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.utility.config.Config;

/**
 * The class SkinUtil
 *
 * @author jay
 * @version 1.0
 *
 */
public final class SkinUtil {

	private static final Log LOG = LogFactory.getLog(SkinUtil.class);

	//	public static final List<String> JQUERY_UI_SKIN_LIST = retrieveJQueryUISkinList();

	//굳이 static으로 까지 할 필요가 있나 싶다.
	/*	*//**
			* jQuery-UI 스킨 중, 사용 가능한 스킨의 이름 목록을 리턴.(프로퍼티 조회) <br>
			* 
			* @return
			*/
	/*
	private static List<String> retrieveJQueryUISkinList() {
	return Config.getStringList("skin");
	}*/

	/**
	 * jQuery-UI 스킨 중, 사용 가능한 스킨의 이름 목록을 리턴.(static 객체 리턴)<br>
	 * 
	 * @return
	 */
	public static List getJQueryUISkinList() {
		return Config.getStringList("skin.list");
	}

	/**
	 * 현재 선택(쿠키 저장)된 jQuery-UI의 스킨(테마) 이름을 리턴.<br>
	 * 쿠키값이 없을 경우 프로퍼티에 설정한 기본 스킨 이름을 리턴<br>
	 * 
	 * @param hReq HttpServletRequest
	 * @return skin 스킨(테마)이름
	 */
	public static String getJQueryUISkin(HttpServletRequest hReq) {
		String skin = null;
		Cookie[] cookies = hReq.getCookies();
		String cookieName;
		String cookieValue;
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				cookieName = cookie.getName();
				cookieValue = cookie.getValue();
				if(LOG.isDebugEnabled()) {
					LOG.debug("cookie: " + cookieName + "=" + cookieValue);
				}
				if("skin".equals(cookieName)) {
					skin = cookieValue;
					break;
				}
			}
		}
		return StringUtils.defaultString(skin, getJQueryUIDefaultSkin());
	}

	/**
	 * 스킨 선택 쿠키값이 존재하지 않을 경우 기본으로 사용할 스킨의 이름 리턴. 
	 * 
	 * @return FrameOne Config 에 있는 skin.default 값을 리턴.
	 */
	public static String getJQueryUIDefaultSkin() {
		return Config.getString("skin.default");
	}

}
