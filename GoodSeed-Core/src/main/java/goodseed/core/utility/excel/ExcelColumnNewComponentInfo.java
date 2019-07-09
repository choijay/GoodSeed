package goodseed.core.utility.excel;

import java.util.ArrayList;

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
public class ExcelColumnNewComponentInfo {

	private ArrayList<ExcelColumnHeaderRow> row = new ArrayList<ExcelColumnHeaderRow>();

	private ExcelColumnBodyRow excelColumnBodyRow;

	private ExcelSheetColumnWidth excelSheetColumnWidth;

	public ExcelColumnBodyRow getExcelColumnBodyRow() {
		return excelColumnBodyRow;
	}

	public void setExcelColumnBodyRow(ExcelColumnBodyRow excelColumnBodyRow) {
		this.excelColumnBodyRow = excelColumnBodyRow;
	}

	public ArrayList<ExcelColumnHeaderRow> getRows() {
		return row;
	}

	public void setRow(ExcelColumnHeaderRow row) {
		this.row.add(row);
	}

	public ExcelSheetColumnWidth getExcelSheetColumnWidth() {
		return excelSheetColumnWidth;
	}

	public void setExcelSheetColumnWidth(ExcelSheetColumnWidth excelSheetColumnWidth) {
		this.excelSheetColumnWidth = excelSheetColumnWidth;
	}

}
