package goodseed.core.utility.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.ResultContext;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Demonstrates a workaround you can use to generate large workbooks and avoid OutOfMemory exception.
 *
 * The trick is as follows:
 * 1. create a template workbook, create sheets and global objects such as cell styles, number formats, etc.
 * 2. create an application that streams data in a text file
 * 3. Substitute the sheet in the template with the generated data
 *
 * <p>
 *      Since 3.8-beta3 POI provides a low-memory footprint SXSSF API which implementing the "BigGridDemo" strategy.
 *      XSSF is an API-compatible streaming extension of XSSF to be used when
 *      very large spreadsheets have to be produced, and heap space is limited.
 *      SXSSF achieves its low memory footprint by limiting access to the rows that
 *      are within a sliding window, while XSSF gives access to all rows in the
 *      document. Older rows that are no longer in the window become inaccessible,
 *      as they are written to the disk.
 * </p>
 * See <a "http://poi.apache.org/spreadsheet/how-to.html#sxssf">
 *     http://poi.apache.org/spreadsheet/how-to.html#sxssf</a>.

 *
 * @author Yegor Kozlov
 * @modifier KimByungWook
 */

/**
 * 
 * The class SpreadsheetWriter<br>
 * <br>
 * 엑셀파일로 데이터를 스트리밍하기 위한 클래스<br>
 * SpreadSheet를 BufferedWriter를 사용하여 XML형식으로 write한다.<br>
 * <br>
 * Copyright (c) 2016 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @version 2.0
 * @since  5. 21.
 *
 */
public interface Spreadsheet {

	
	/**
	 * 스프레드쉬트 XML sheetData 시작<br>
	 * <br>
	 * @throws IOException
	 */
	public void beginSheet() throws IOException;
	
	
	
	
	
	
	/**
	 * 스프레드 쉬드 XML sheetData 종료<br>
	 * <br>
	 * @throws IOException
	 */
	public void endSheet() throws IOException;
	/**
	 * 
	 * 스프레드 쉬트 머지 정보 등록<br>
	 * <br>
	 * @param cellRangeAddressList 엑셀 로우 칼럼 머지 값 정보
	 * @throws IOException
	 */
	
	
	public void insertMergeCells(ExcelColumnNewComponentInfo columnList) throws IOException;
	
	/** 
	 * 스프레드쉬트 XML worksheet 종료<br>
	 * <br>
	 * @throws IOException
	 */
	public void endworksheet() throws IOException;
	/**
	 * 
	 * 입력받은 rownum 줄에 <row> write하여 행를 추가한다.<br>
	 * <br>
	 * @param rownum 0-based row number
	 * @throws IOException
	 */
	public void insertRow(int rownum) throws IOException ;
	
	/**
	 * 
	 * </row>를 write하여 행를 종료한다. <br>
	 * <br>
	 * @throws IOException
	 */
	public void endRow() throws IOException ;
	/**
	 * 스프레드 쉬트 XML 로우 데이타 생성 <br>
	 * <br>
	 * @param wb 생성한 XSSFWorkbook
	 * @param columnIndex 컬럼 인덱스
	 * @param value 셀 데이타
	 * @param styleIndex 스타일 인덱스 값
	 * @throws IOException
	 */
	public void bodyCreateCell(XSSFWorkbook wb, int columnIndex, Object value, int styleIndex) throws IOException;
	
	public void headerCreateCell(XSSFWorkbook wb, String cellKey, Object value, int styleIndex) throws IOException ;

	public BufferedWriter get_out();
	public void set_out(BufferedWriter out);
	
	public void setStyle(Map<String, XSSFCellStyle> styles);
	
	public void runBodyRows(ResultContext resultContext);

	public void runHeaderRows();
	
	public void setColumnList(ExcelColumnNewComponentInfo columnList);
	
	public void setWorkBook(XSSFWorkbook workbook,String sheetName);
	
	public File setFile(String fileName) throws IOException;
	
	public void setSheet(XSSFSheet sheet);
	
	public void setColsWidth(HashMap<Integer ,Integer> cellInfo) throws IOException	;
	
	public void close();
	
}
