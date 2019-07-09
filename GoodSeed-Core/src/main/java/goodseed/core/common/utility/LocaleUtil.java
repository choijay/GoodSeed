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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import goodseed.core.common.model.Parameters;
import goodseed.core.utility.config.Config;

/**
 * The class LocaleUtil<p>
 * <p>
 * LocaleUtil에서는 SessionLocaleResolver의 resolveLocale()을 직접 호출하지는 않지만,<p>
 * SessionLocaleResolver의 세션명을 그대로 사용하고 있고, 또 localeResolver가 bean으로 등록되어 있기 때문에,<p>
 * RequestContextUtils.getLocale(request) 과 같은 메서드에도 대응할 수 있다.
 *
 * @author jay
 * @version 1.0
 *
 */
public class LocaleUtil {

	/**
	 * 	기본 언어의 ISO-639 2-letter 형식 문자열
	 */
	public static final String DEFAULT_LANG = resolveDefaultLanguage();

	/**
	 * 	기본 언어의 로케일 객체
	 */
	public static final Locale DEFAULT_LOCALE = new Locale(DEFAULT_LANG);

	/**
	 * 기본 언어의 ISO-639 2-letter 형식 문자열을 리턴한다.
	 * 
	 * @return
	 */
	private static String resolveDefaultLanguage() {
//		System.out.println("로케일!!"+Config.getString("locale.defaultLang"));
		return Config.getString("locale.defaultLang");
	}

	/**
	 *	기본 언어의 ISO-639 2-letter 형식 문자열을 리턴한다.
	 * 
	 * @return
	 */
	public static String getDefaultLanguage() {
		return DEFAULT_LANG;
	}

	/**
	 *	 기본 언어의 Locale 객체를 리턴한다.
	 */
	public static Locale getDefaultLocale() {
		return DEFAULT_LOCALE;
	}

	/**
	 * 	현재 사용자 세션 언어의 ISO-639 2-letter 형식 문자열을 리턴한다.<p>
	 * 세션 언어가 존재하지 않을 경우 (ex: 로그인 하지 않았을 경우) 기본 언어의 ISO-639 2-letter 형식 문자열을 리턴한다. 
	 * 	
	 * @param inParams
	 * @return
	 */
	public static String getUserLanguage(Parameters inParams) {
		String lang = inParams.getVariableAsString("g_lang");
		return StringUtils.isNotBlank(lang) ? lang : getDefaultLanguage();
	}

	/**
	 * 	현재 사용자 세션 언어의 ISO-639 2-letter 형식 문자열을 리턴한다.<p>
	 * 세션 언어가 존재하지 않을 경우 (ex: 로그인 하지 않았을 경우) 기본 언어의 ISO-639 2-letter 형식 문자열을 리턴한다. 
	 * 
	 * @param session
	 * @return
	 */
	public static String getUserLanguage(HttpSession session) {
		if(session == null) {
			return getDefaultLanguage();
		}
		Object lang = session.getAttribute("g_lang");
		return (lang != null) ? (String)lang : getDefaultLanguage();
	}

	/**
	 * 	현재 사용자 세션 언어의 ISO-639 2-letter 형식 문자열을 리턴한다.<p>
	 * 세션 언어가 존재하지 않을 경우 (ex: 로그인 하지 않았을 경우) 기본 언어의 ISO-639 2-letter 형식 문자열을 리턴한다. 
	 * 
	 * @param request
	 * @return
	 */
	public static String getUserLanguage(HttpServletRequest request) {
		return getUserLanguage(request.getSession(false));
	}

	/**
	 * 현재 사용자 세션의 Locale 객체를 리턴한다.<p>
	 * 세션 Locale 객체가 존재하지 않을 경우 (ex: 로그인 하지 않았을 경우) 기본 언어 Locale 객체를 리턴한다.
	 * 
	 * @param inParams
	 * @return
	 */
	public static Locale getUserLocale(Parameters inParams) {
		Object locale = inParams.getVariable(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		return (!"".equals(locale)) ? (Locale)locale : getDefaultLocale();
	}

	/**
	 * 현재 사용자 세션의 Locale 객체를 리턴한다.<p>
	 * 세션 Locale 객체가 존재하지 않을 경우 (ex: 로그인 하지 않았을 경우) 기본 언어 Locale 객체를 리턴한다.
	 *  
	 * @param session
	 * @return
	 */
	public static Locale getUserLocale(HttpSession session) {
		if(session == null) {
			return getDefaultLocale();
		}
		Object locale = session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		return (locale != null) ? (Locale)locale : getDefaultLocale();
	}

	public static Locale getUserLocale(HttpServletRequest request) {
		return getUserLocale(request.getSession(false));
	}

	/**
	 * 현재 사용자 세션에 인수로 받은 Locale객체를 바인딩한다.<p>
	 * g_lang 이라는 이름의 ISO-639-1 (2-letter standard) 문자열도 세션에 바인딩한다. 
	 * 
	 * @param session
	 * @param locale
	 */
	public static void setUserLocale(HttpSession session, Locale locale) {
		session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
		session.setAttribute("g_lang", locale.getLanguage());
	}

	/**
	 * ISO-639-1 (2-letter standard) 문자열을 인수로 받아서 Locale 객체를 생성하여 리턴한다.
	 * 
	 * @param lang2Letter
	 * @return
	 */
	public static Locale getLocaleBy2Letter(String lang2Letter) {

		//변경된 Locale 객체
		Locale changedLocale = null; 
		//일치하는 값이 없을 경우 기본값으로 사용할 로케일 (이런 케이스는 거의 없을 듯)
		Locale backupLocale = null; 
		//ISO-639-1 (2-letter standard)
		String tmpLocale2Letter = null;
		for(Locale tmpLocale : Locale.getAvailableLocales()) {
			tmpLocale2Letter = tmpLocale.getLanguage();
			if(tmpLocale2Letter.equals(lang2Letter)) {
				changedLocale = tmpLocale;
				break;
			}
			if(tmpLocale2Letter.equals(DEFAULT_LANG)) {
				backupLocale = tmpLocale;
			}
		}
		if(changedLocale == null) {
			changedLocale = backupLocale;
		}
		return changedLocale;
	}

}
