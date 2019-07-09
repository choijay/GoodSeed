package com.common.syscommon.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.common.utility.LocaleUtil;
import goodseed.core.exception.UserHandleException;

/**
 * The class CodeUtil<br>
 * <br>
 * 공통코드 조회 유틸 클래스<br>
 * <br>
 *
 */
public class CodeUtil {

	/**
	 * 	시스템 기동시 공통그룹코드 및 공통코드 정보를 담는 공유 객체
	 */
	private static Map<String, Map<String, Object>> commonGroupCodeMap = null;
	private static Map<String, Map<String, Map<String, Object>>> commonCodeListMap = null;

	private static List groupCodeList = null;
	private static List codeList = null;
	
	//그룹코드 이름
	public static final String GRPCDCOL_COMM_CL_CD_NM = "CLCDNM"; 
	//코드 이름
	public static final String CDCOL_COMM_CD_NM = "CDNM"; 
	/**
	 * 	최초 로딩과 이후 refersh를 구분하기 위해 클래스 전역변수 flag 선언
	 */
	private static boolean isInitialized = false;
	
	//외부 생성 제한
	private CodeUtil() {
	} 
	
	//TODO:동시성제어 필요성 검토?
	public static synchronized void init() { 

		Map<String, Map<String, Object>> tempCommonGroupCodeMap = null;

		if(!isInitialized) {
			commonGroupCodeMap = new HashMap<String, Map<String, Object>>();
			commonCodeListMap = new HashMap<String, Map<String, Map<String, Object>>>();
		}

		tempCommonGroupCodeMap = new HashMap<String, Map<String, Object>>();

		SqlManager sqlManager = SqlManagerFactory.getSqlManager();

		groupCodeList = sqlManager.queryForList("commonCode.getCommonGroupCodeListCodeUtil");

		Map<String, Object> groupCodeLanguageMap = null;

		String gClCd = null;
		String gClLangCl = null;

		for(Map groupRow : (List<Map>)groupCodeList) {

			gClCd = (String)groupRow.get("CLCD");
			gClLangCl = (String)groupRow.get("LANGCL");

			if(tempCommonGroupCodeMap.get(gClCd) == null) {
				groupCodeLanguageMap = new HashMap<String, Object>();
				tempCommonGroupCodeMap.put(gClCd, groupCodeLanguageMap);
			} else {
				groupCodeLanguageMap = tempCommonGroupCodeMap.get(gClCd);
			}
			groupCodeLanguageMap.put(gClLangCl, groupRow);
		}

		//매장/약관 그룹 추가
		tempCommonGroupCodeMap.put("SHOP", new HashMap<String, Object>());
		tempCommonGroupCodeMap.put("TERMS", new HashMap<String, Object>());
		
		commonGroupCodeMap = tempCommonGroupCodeMap;

		Map<String, Map<String, Map<String, Object>>> tempCommonCodeListMap = null;
		tempCommonCodeListMap = new HashMap<String, Map<String, Map<String, Object>>>();
		
		//코드 추가
		codeList = sqlManager.queryForList("commonCode.getCommonCodeListCodeUtil");
		addCodeList(codeList, tempCommonCodeListMap);
		
		//매장 추가
		List<Map> shopList = (List<Map>) sqlManager.queryForList("shop.getShopListCodeUtil");
		addCodeList(shopList, tempCommonCodeListMap);
		
		//매장, 약관 추가
		List<Map> termsList = (List<Map>) sqlManager.queryForList("terms.getTermsListCodeUtil");
		addCodeList(termsList, tempCommonCodeListMap);

		commonCodeListMap = tempCommonCodeListMap;

		if(!isInitialized) {
			isInitialized = true;
		}

	}

	private static void addCodeList(List<Map> list, Map<String, Map<String, Map<String, Object>>> tempCommonCodeListMap) {
		
		Map<String, Map<String, Object>> codeListsMap = null;
		Map<String, Object> codeLanguageMap = null;

		String clCd = null;
		String cd = null;
		String clLangCl = null;

		for(Map row : (List<Map>) list) {
			
			clCd = (String)row.get("CLCD");
			cd = (String)row.get("CD");
			clLangCl = (String)row.get("LANGCL");

			if(tempCommonCodeListMap.get(clCd) == null) {
				codeListsMap = new LinkedHashMap<String, Map<String, Object>>();
				tempCommonCodeListMap.put(clCd, codeListsMap);
			} else {
				codeListsMap = tempCommonCodeListMap.get(clCd);
			}

			if(codeListsMap.get(cd) == null) {
				codeLanguageMap = new HashMap<String, Object>();
				codeListsMap.put(cd, codeLanguageMap);
			} else {
				codeLanguageMap = codeListsMap.get(cd);
			}

			codeLanguageMap.put(clLangCl, row);
		}

	}
	
	/**
	 * 유효한 그룹코드인지 검사<br>
	 * 유효하지 않으면 UserHandleException 리턴<br>
	 * <br>
	 * @param groupCode 그룹코드
	 * @throws UserHandleException
	 */
	public static boolean validateGroupCode(String groupCode) throws UserHandleException {

		if(commonGroupCodeMap.get(groupCode) == null) {
			return false;
		}
		return true;
	}

	public static boolean validateGroupCodeLanguageCd(String groupCode, String languageCd) {
		if(commonGroupCodeMap.get(groupCode).get(languageCd) == null) {
			return false;
		}
		return true;
	}

	/**
	 * 유효한 코드인지 검사<br>
	 * 유효하지 않으면 UserHandleException 리턴<br>
	 * <br>
	 * @param groupCode 그룹코드
	 * @param code 코드
	 * @throws UserHandleException
	 */
	public static boolean validateCode(String groupCode, String code) throws UserHandleException {
		if(!validateGroupCode(groupCode)) {
			return false;
		}
		if(commonCodeListMap.get(groupCode).get(code) == null) {
			return false;
		}
		return true;
	}

	public static boolean validateCodeLanguageCd(String groupCode, String code, String languageCd) {
		if(commonCodeListMap.get(groupCode).get(code).get(languageCd) == null) {
			return false;
		}
		return true;
	}

	/**
	 * 	그룹코드의 해당 속성(칼럼)값을 리턴한다.<br>
	 * <br>
	 * @param groupCode 그룹코드
	 * @param groupCodeColumnName 속성(칼럼)명
	 * @return 속성(칼럼)값 - 그룹코드명, 그룹코드상세이름, Owner시스템구분코드, etc...
	 */
	public static String getGroupCodeProperty(String groupCode, String languageCode, String groupCodeColumnName) {
		if(!validateGroupCode(groupCode)) {
			//해당 그룹코드가 정상이 아닐 경우 코드값이 아닌 코드를 그냥 리턴한다.
			return groupCode; 
		}
		if(!validateGroupCodeLanguageCd(groupCode, languageCode)) {
			return groupCode;
		}
		return (String)((Map)commonGroupCodeMap.get(groupCode).get(languageCode)).get(groupCodeColumnName);
	}

	/**
	 * 	그룹코드명을 리턴한다.<br>
	 * <br>
	 * @param groupCode 그룹코드
	 * @return 그룹코드명
	 */
	public static String getGroupCodeName(String groupCode, String languageCode) {
		return getGroupCodeProperty(groupCode, languageCode, GRPCDCOL_COMM_CL_CD_NM);
	}

	/**
	 * 	해당 그룹코드의 코드목록을 Map의 형태로 리턴한다.<br>
	 * <br>
	* @param groupCode 그룹코드
	* @return 코드목록
	 */
	public static Map<String, Map<String, Object>> getCodeListMap(String groupCode) {

		Map<String, Map<String, Object>> codeListMap = new LinkedHashMap<String, Map<String, Object>>();
		if(!validateGroupCode(groupCode)) {
			//해당 그룹코드가 정상이 아닐 경우 빈 Map 객체를 리턴한다.
			return codeListMap; 
		}
		//신규생성 Map 객체 사용
		codeListMap.putAll(commonCodeListMap.get(groupCode)); 
		return codeListMap;
	}

	/**
	 * 	해당 그룹코드와 코드에 해당하는 코드속성(칼럼)값을 리턴한다.<br>
	 * <br>
	 * @param groupCode 그룹코드
	 * @param code 코드
	 * @param codeColumnName 코드칼럼명
	 * @return 코드속성(칼럼)값
	 */
	public static String getCodeProperty(String groupCode, String code, String languageCode, String codeColumnName) {
		if(!validateCode(groupCode, code)) {
			//해당 코드가 정상이 아닐 경우 코드를 그냥 리턴한다.
			return code; 
		}
		if(!validateCodeLanguageCd(groupCode, code, languageCode)) {
			return code;
		}
		return (String)((Map)commonCodeListMap.get(groupCode).get(code).get(languageCode)).get(codeColumnName);
	}

	/**
	 * 해당 그룹코드와 코드에 해당하는 코드명을 리턴한다.<br>
	 * <br>
	 * @param groupCode	 그룹코드
	 * @param code 코드
	 * @return 코드명
	 */
	public static String getCodeName(String groupCode, String code, String languageCode) {
		return getCodeProperty(groupCode, code, languageCode, CDCOL_COMM_CD_NM);
	}

	/**
	 * 공통 코드 전체 목록을 반환한다.
	 * 
	 * @ahthor 
	 * @since  8. 15.
	 *
	 * @return
	 */
	public static List getCommonCodeList() {
		return codeList;
	}
}
