package goodseed.core.common.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.utility.string.StringUtil;

/**
 * The class LabelUtil<br>
 * Label과 관련된 Util Class
 * 다국어처리를 위한 초기 라벨정보를 로딩, 라벨에 대한 유효성 검사 및 라벨코드에 대한 라벨명 반환
 * HTML UI에서 사용한다.
 * <br>
 * @author jay
 *
 */

public class LabelUtil {

	/**
	 * 	시스템 기동시 HTML Label 정보를 담는 공유 객체
	 */
	private static Map<String, Map<String, String>> commonLabel = null;

	private static List labelList = null;
	/**
	 * 	최초 로딩과 이후 refersh를 구분하기 위해 클래스 전역변수 flag 선언
	 */
	private static boolean isInitialized = false;

	/**
	 * 현재 사용중인 메세지 레코드들 중 가장 최근 수정일자의 Timestamp
	 */
	private static long recentLastUpdateDttm = 0;

	/**
	 * 외부 생성 제한
	 */
	private LabelUtil() {

	}

	/**
	 * 
	 * 초기 라벨정보 로딩.
	 * 
	 * 1. sqlManager로 db에서 라벨 정보를 가져와 list에 담는다.
	 * 2. 최근 변경된 라벨이 있는지 확인하여 loadLabelList를 수행한다.
	 * 
	 * <br>
	 * @ahthor shks
	 * @since  5. 29.
	 */
	public static synchronized void loadLabel() {

		SqlManager sqlManager = SqlManagerFactory.getSqlManager();
		List<Map> recentLastUpdateDttmList = (List<Map>)sqlManager.queryForList("label.getRecentModDateForHTML");

		long tempRecentLastUpdateDttmList = 0;
		if(!recentLastUpdateDttmList.isEmpty()) {
			for(Map recentMap : recentLastUpdateDttmList) {
				tempRecentLastUpdateDttmList = Long.parseLong(StringUtil.defaultString(recentMap.get("UPD_DTM")));
			}
		}

		if(recentLastUpdateDttm < tempRecentLastUpdateDttmList) {
			recentLastUpdateDttm = tempRecentLastUpdateDttmList;
			loadLabelList();
		}
	}

	/**
	 * 
	 * 초기 라벨 로딩 후, 최근 변경된 라벨이 있을 경우 수행되는 함수
	 * 변경된 라벨리스트만 판단하여 labelLanguageMap에 put
	 * <br>
	 * @ahthor shks
	 * @since  5. 29.
	 */
	private static void loadLabelList() {
		SqlManager sqlManager = SqlManagerFactory.getSqlManager();

		Map<String, Map<String, String>> tempCommonLabelMap = null;
		if(!isInitialized) {
			commonLabel = new HashMap<String, Map<String, String>>();
		}
		tempCommonLabelMap = new HashMap<String, Map<String, String>>(commonLabel);
		Map<String, String> labelLanguageMap = null;

		List<Map> languageList = (List<Map>)sqlManager.queryForList("commonCode.getLangCode");

		for(Map language : languageList) {
			String langCD = StringUtil.defaultString(language.get("COMM_CD"));
			String labelCd = null;
			String labelNm = null;
			Map<String, String> inParam = new HashMap<String, String>();
			inParam.put("g_lang", langCD);
			labelList = sqlManager.queryForList(inParam, "label.getLabelListForHTML");
			for(Map labelRow : (List<Map>)labelList) {
				labelCd = StringUtil.defaultString(labelRow.get("LABEL_CD"));
				labelNm = StringUtil.defaultString(labelRow.get("LABEL_NM"));
				if(tempCommonLabelMap.get(langCD) == null) {
					labelLanguageMap = new HashMap<String, String>();
					tempCommonLabelMap.put(langCD, labelLanguageMap);
				} else {
					labelLanguageMap = tempCommonLabelMap.get(langCD);
				}
				labelLanguageMap.put(labelCd, labelNm);
			}
		}
		commonLabel = tempCommonLabelMap;
		if(!isInitialized) {
			isInitialized = true;
		}

	}

	/**
	 * 
	 * 라벨코드 유효성 검토.<br>
	 * <br>
	 * @param labelCode 라벨코드
	 * @param languageCd 언어코드
	 * @return true/false
	 * @ahthor shks
	 * @since  5. 29.
	 */
	public static boolean validateLabelCode(String labelCode, String languageCd) {
		if(commonLabel.get(languageCd) == null) {
			return false;
		} else {
			if(commonLabel.get(languageCd).get(labelCode) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * 라벨코드에 대한 라벨명 반환.<br>
	 * <br>
	 * @param labelCode 라벨코드
	 * @param languageCd 언어코드
	 * @return 라벨명
	 * @ahthor shks
	 * @since  5. 29.
	 */
	public static String getLabelProperty(String labelCode, String languageCd) {
		if(languageCd == null || "".equals(languageCd)) {
			return labelCode;
		} else {
			if(!validateLabelCode(labelCode, languageCd)) {
				return labelCode;
			}
		}

		return StringUtil.defaultString(((Map)commonLabel.get(languageCd)).get(labelCode));
	}
	
	/**
	 * 
	 * 라벨코드에 대한 (기본 언어) 라벨명 반환.<br>
	 * <br>
	 * @param labelCode 라벨코드
	 * @return 라벨명
	 * @ahthor shks
	 * @since  5. 29.
	 */	
	public static String getLabelProperty(String labelCode) {
		return getLabelProperty(labelCode, LocaleUtil.getDefaultLanguage());
	}
	
	public static String getURLEncode(String value) {
		return getURLEncode(value, "UTF-8");
	}
	
	public static String getURLEncode(String value, String charset) {
		try {
			return URLEncoder.encode(value, charset).replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}
}
