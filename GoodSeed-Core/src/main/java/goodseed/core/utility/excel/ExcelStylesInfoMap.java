package goodseed.core.utility.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * The Class ExcelStylesInfoMap<br>
 * 엑셀 셀 스타일 맵
 * <br>
 * @author jay
 * @version 1.0
 *
 */
public class ExcelStylesInfoMap {

	/**
	 * 엑셀 셀 스타일 맵<br>
	 */
	private Map<String, XSSFCellStyle> styles = new HashMap();

	/**
	 * 엑셀 셀 스티맵 등록<br>
	 * <br>
	 * @param styleCode 생성한 스타일의 코드 값
	 * @param cellStyle 스타일 정보 
	 */
	public void setStyle(String styleCode, XSSFCellStyle cellStyle) {
		styles.put(styleCode, cellStyle);
	}

	/**
	 * 엑셀 셀 스티일 맵 조회<br>
	 * <br>
	 * @return Map<생성한 스타일의 코드값, 스타일 정보>
	 */
	public Map<String, XSSFCellStyle> getStyles() {
		return styles;
	}

}
