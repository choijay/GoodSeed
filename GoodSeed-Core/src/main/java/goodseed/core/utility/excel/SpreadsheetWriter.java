package goodseed.core.utility.excel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import goodseed.core.common.utility.CharSetUtil;

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
public class SpreadsheetWriter {

	/**
	 * XML 인코딩 타입. CharSetUtil에서 넘겨주는 기본 캐릭터셋을 받는다.
	 */
	private static final String XML_ENCODING = CharSetUtil.getDefaultCharSet();

	/**
	 * 문자를 읽어 들이거나 문자 출력 스트림으로 문자를 내보낼 때 버퍼링을 하는 writer
	 */
	private final BufferedWriter _out;
	
	private int rownum;

	public SpreadsheetWriter(BufferedWriter out) {
		_out = out;
	}

	/**
	 * 스프레드쉬트 XML sheetData 시작<br>
	 * <br>
	 * @throws IOException
	 */
	public void beginSheet() throws IOException {
		_out.write("<?xml version=\"1.0\" encoding=\"" + XML_ENCODING + "\"?>"
				+ "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
		_out.write("<sheetData>\n");
	}

	/**
	 * 스프레드 쉬드 XML sheetData 종료<br>
	 * <br>
	 * @throws IOException
	 */
	public void endSheet() throws IOException {
		_out.write("</sheetData>");
	}

	/**
	 * 
	 * 스프레드 쉬트 머지 정보 등록<br>
	 * <br>
	 * @param cellRangeAddressList 엑셀 로우 칼럼 머지 값 정보
	 * @throws IOException
	 */
	public void insertMergeCells(ArrayList<RowCellRangeAddress> cellRangeAddressList) throws IOException {
		_out.write("<mergeCells>");
		Iterator iter = cellRangeAddressList.iterator();
		String mergeCells = "";
		while(iter.hasNext()) {
			RowCellRangeAddress rowCellRangeAddress = (RowCellRangeAddress)iter.next();
			String startColumn = new CellReference(0, rowCellRangeAddress.getStartColumnIndex()).formatAsString();
			String endColumn = new CellReference(0, rowCellRangeAddress.getEndColumnIndex()).formatAsString();
			mergeCells = mergeCells + "<mergeCell ref=\"" + startColumn + ":" + endColumn + "\"/>";

		}
		_out.write(mergeCells);
		_out.write("</mergeCells>");
	}

	/** 
	 * 스프레드쉬트 XML worksheet 종료<br>
	 * <br>
	 * @throws IOException
	 */
	public void endworksheet() throws IOException {
		_out.write("</worksheet>");
	}

	/**
	 * 
	 * 입력받은 rownum 줄에 <row> write하여 행를 추가한다.<br>
	 * <br>
	 * @param rownum 0-based row number
	 * @throws IOException
	 */
	public void insertRow(int rownum) throws IOException {
		_out.write("<row r=\"" + (rownum + 1) + "\">\n");
		this.rownum = rownum;
	}
	
	/**
	 * 
	 * </row>를 write하여 행를 종료한다. <br>
	 * <br>
	 * @throws IOException
	 */
	public void endRow() throws IOException {
		_out.write("</row>\n");
	}

	/**
	 * 스프레드 쉬트 XML 로우 데이타 생성 <br>
	 * <br>
	 * @param wb 생성한 XSSFWorkbook
	 * @param columnIndex 컬럼 인덱스
	 * @param value 셀 데이타
	 * @param styleIndex 스타일 인덱스 값
	 * @throws IOException
	 */
	public void createCell(XSSFWorkbook wb, int columnIndex, Object value, int styleIndex) throws IOException {
		Object localValue = value;

		String ref = new CellReference(rownum, columnIndex).formatAsString();
		_out.write("<c r=\"" + ref + "\" t=\"" + ((localValue instanceof Integer) ? "n" : "inlineStr") + "\"");
		if(styleIndex != -1) {
			_out.write(" s=\"" + styleIndex + "\"");
		}
		_out.write(">");

		if(localValue instanceof String) {
			localValue = StringEscapeUtils.escapeXml((String)localValue);
			_out.write("<is><t>" + localValue + "</t></is>");
		} else if(localValue instanceof Integer) {
			_out.write("<v>" + (Integer)localValue + "</v>");
		} else if(localValue instanceof Double) {
			_out.write("<is><t>" + (Double)localValue + "</t></is>");
		} else if(localValue instanceof BigDecimal) {
			_out.write("<is><t>" + ((BigDecimal)localValue).doubleValue() + "</t></is>");
		} else if(localValue instanceof Date) {
			XSSFDataFormat fmt = wb.createDataFormat();
			_out.write("<is><t>" + fmt.getFormat("m/d/yy") + "</t></is>");
		} else if(localValue instanceof Boolean) {
			_out.write("<is><t>" + (Boolean)localValue + "</t></is>");
		} else if(localValue == null) {
			_out.write("<is><t> </t></is>");
		}
		_out.write("</c>");
	}

}
