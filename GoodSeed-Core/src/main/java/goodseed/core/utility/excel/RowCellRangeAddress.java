package goodseed.core.utility.excel;

/**
 * The class RowCellRangeAddress<br>
 * 엑셀 로우 칼럼 머지 값 정보<br>
 * 엑셀 헤더1에만 적용
 * <br>
 * @author jay
 * @version 1.0
 *
 */
public class RowCellRangeAddress {

	/**
	 * 시작 칼럼 인덱스 값
	 */
	private int startColumnIndex;

	/**
	 * 종료 칼럼 인덱스 값
	 */
	private int endColumnIndex;

	/**
	 * 엑셀 헤더1(엑셀 로우 첫번째 )머지 정보 등록 <br>
	 * 예) RowCellRangeAddress(0,1) => 엑셀 A1 과  B1 머지<br>
	 * <br>
	 * @param startColumnIndex 시작 칼럼 인덱스 값
	 * @param endColumnIndex 종료 칼럼 인덱스 값
	 */
	public RowCellRangeAddress(int startColumnIndex, int endColumnIndex) {
		this.startColumnIndex = startColumnIndex;
		this.endColumnIndex = endColumnIndex;
	}

	/**
	 * get startColumnIndex
	 * @return int 시작 칼럼 인덱스 값
	 */
	public int getStartColumnIndex() {
		return startColumnIndex;
	}

	/**
	 * set startColumnIndex
	 * @param startColumnIndex 시작 칼럼 인덱스 값
	 */
	public void setStartColumnIndex(int startColumnIndex) {
		this.startColumnIndex = startColumnIndex;
	}

	/**
	 * get endColumnIndex
	 * @return int 종료 칼럼 인덱스 값
	 */
	public int getEndColumnIndex() {
		return endColumnIndex;
	}

	/**
	 * set endColumnIndex
	 * @param endColumnIndex 종료 칼럼 인덱스 값
	 */
	public void setEndColumnIndex(int endColumnIndex) {
		this.endColumnIndex = endColumnIndex;
	}

}
