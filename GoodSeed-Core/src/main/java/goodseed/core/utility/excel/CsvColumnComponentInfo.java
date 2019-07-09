/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.excel;

/**
 * The class CsvColumnComponentInfo
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class CsvColumnComponentInfo {

	/**
	 * CSV 칼럼 코드
	 */
	private String csvColumnCode;

	/**
	 * CSV 칼럼 명
	 */
	private String csvColumnName;

	/**
	 * 
	 * CSV 칼럼 코드 조회<br>
	 * <br>
	 * @return 컬럼의 매핑 code
	 */
	public String getCsvColumnCode() {
		return csvColumnCode;
	}

	/**
	 * CSV 칼럼 코드 등록<br>
	 * sql 결과셋의 컬럼명 자동 연결 <br>
	 * <br>
	 * @param csvColumnCode
	 */
	public void setCsvColumnCode(String csvColumnCode) {
		this.csvColumnCode = csvColumnCode;
	}

	/**
	 * CSV 칼럼 명 조회<br>
	 * <br>
	 * @return 컬럼의 헤더 이름
	 */
	public String getCsvColumnName() {
		return csvColumnName;
	}

	/**
	 * CSV 칼럼 명 등록<br>
	 * <br>
	 * @param csvColumnName
	 */
	public void setCsvColumnName(String csvColumnName) {
		this.csvColumnName = csvColumnName;
	}

}
