package goodseed.core.utility.excel;

/**
 * The class ExcelColumnComponentInfo<br>
 * <br>
 * 엑셀 헤더 및 칼럼 정보<br>
 * 엑셀 헤더는 2줄을 지원하며, headerRow1과 headerRow2로 구분된다.<br>
 * headerRow2가 비어 있는 경우 헤더는 1줄로 처리된다.<br>
 *
 * @author jay
 * @version 1.0
 */
public class ExcelColumnComponentInfo {

	/**
	 * 엑셀 헤더1 칼럼명
	 */
	private String headerRow1ColumnName;
	/**
	 * 엑셀 칼럼 코드
	 */
	private String headerColumnCode;
	/**
	 * 엑셀 헤더2 칼럼명
	 */
	private String headerRow2ColumnName;
	/**
	 * 엑셀 헤더1 스타일 값
	 */
	private String headerRow1CellStyleCode;
	/**
	 * 엑셀 헤더2 스타일 값
	 */
	private String headerRow2CellStyleCode;
	/**
	 * 엑셀 바디 스타일 값
	 */
	private String bodyCellStyleCode;

	/**
	 * 엑셀 헤더1 칼럼명 조회<br>
	 * <br>
	 * @return 헤더 로우1컬럼명
	 */
	public String getHeaderRow1ColumnName() {
		return headerRow1ColumnName;
	}

	/**
	 * 엑셀 헤더1 칼럼 명 등록<br>
	 * <br>
	 * @param headerRow1ColumnName
	 */
	public void setHeaderRow1ColumnName(String headerRow1ColumnName) {
		this.headerRow1ColumnName = headerRow1ColumnName;
	}

	/**
	 * 엑셀 헤더 칼럼 코드 조회<br>
	 * <br>
	 * @return 헤더 컬럼 코드 문자열
	 */
	public String getHeaderColumnCode() {
		return headerColumnCode;
	}

	/**
	 * 엑셀 헤더 칼럼 코드 <br>
	 * sql 결과셋의 컬럼명 자동 연결<br> 
	 * <br> 
	 * @param headerColumnCode
	 */
	public void setHeaderColumnCode(String headerColumnCode) {
		this.headerColumnCode = headerColumnCode;
	}

	/**
	 * 엑셀 헤더2 칼럼 명 조회<br> 
	 * <br> 
	 * @return headerRow2의 컬럼명 문자열
	 */
	public String getHeaderRow2ColumnName() {
		return headerRow2ColumnName;
	}

	/**
	 * 엑셀 헤더2 칼럼 명 조회 <br> 
	 * <br> 
	 * @param headerRow2ColumnName
	 */
	public void setHeaderRow2ColumnName(String headerRow2ColumnName) {
		this.headerRow2ColumnName = headerRow2ColumnName;
	}

	/**
	 * 엑셀 헤더1 칼럼 셀 스타일 조회<br> 
	 * <br> 
	 * @return headerRow1의 컬럼명 문자열
	 */
	public String getHeaderRow1CellStyleCode() {
		return headerRow1CellStyleCode;
	}

	/**
	 * 엑셀 헤더1 셀 스타일 등록<br> 
	 * <br> 
	 * @param headerRow1CellStyleCode
	 */
	public void setHeaderRow1CellStyleCode(String headerRow1CellStyleCode) {
		this.headerRow1CellStyleCode = headerRow1CellStyleCode;
	}

	/**
	 * 엑셀 헤더2 셀 스타일 조회<br> 
	 * <br> 
	 * @return headerRow2의 셀 스타일 문자열
	 */
	public String getHeaderRow2CellStyleCode() {
		return headerRow2CellStyleCode;
	}

	/**
	 * 엑셀 헤더2 셀 스타일 등록<br> 
	 * <br> 
	 * @param headerRow2CellStyleCode
	 */
	public void setHeaderRow2CellStyleCode(String headerRow2CellStyleCode) {
		this.headerRow2CellStyleCode = headerRow2CellStyleCode;
	}

	/**
	 * 엑셀 바디 셀 스타일 조회<br> 
	 * <br> 
	 * @return 바디 영역의 스타일 문자열
	 */
	public String getBodyCellStyleCode() {
		return bodyCellStyleCode;
	}

	/**
	 * 엑셀 바디 셀 스타일 등록<br> 
	 * <br> 
	 * @param bodyCellStyleCode
	 */
	public void setBodyCellStyleCode(String bodyCellStyleCode) {
		this.bodyCellStyleCode = bodyCellStyleCode;
	}

}
