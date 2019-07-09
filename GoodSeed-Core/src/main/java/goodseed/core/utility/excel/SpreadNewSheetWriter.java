package goodseed.core.utility.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.ibatis.session.ResultContext;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import goodseed.core.common.utility.CharSetUtil;
import goodseed.core.exception.SystemException;
import goodseed.core.utility.config.Config;

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
public class SpreadNewSheetWriter implements Spreadsheet {

	/**
	 * XML 인코딩 타입. CharSetUtil에서 넘겨주는 기본 캐릭터셋을 받는다.
	 */
	private static final String XML_ENCODING = CharSetUtil.getDefaultCharSet();

	/**
	 * 문자를 읽어 들이거나 문자 출력 스트림으로 문자를 내보낼 때 버퍼링을 하는 writer
	 */
	private BufferedWriter _out=null;

	private int rownum;
	
	private String colsWidthInfo=null;
	
	private int headerRowLastNum=0;
	
	private XSSFWorkbook commonExcelWorkbook = null;
	
	private XSSFSheet sheet = null;
	
	private String sheetRef = null;
	
	//엑셀 임시 다운로드 경로
	private String tempDirPath = null;
	
	//임시 스프레드xml 데이타 파일
	private File dataXmltmpFile = null;
	
	//생성한 파일명
	private String tempfileName = null;
	
	//임시 템플릿 파일
	private File templateFile = null;
	
	//최종 생성 엑셀 파일
	private File resultExcelFile = null;
	
	//버퍼 스트림 (임시스프페드뒤스 xml)
	private BufferedWriter bufferedWriter = null;
	//엑셀 결과 파일 스트림
	private FileOutputStream resultExcelFileOutStream = null;
	
	//엑셀 셀 스타일 정보
	private Map<String, XSSFCellStyle> styles = null;
		
	//칼럼 정보 리스트
	private ExcelColumnNewComponentInfo columnList = null;
	

	/*public SpreadsheetWriter(BufferedWriter out) {
		_out = out;
	}*/

	
	/**
	 * 스프레드쉬트 XML sheetData 시작<br>
	 * <br>
	 * @throws IOException
	 */
	public void beginSheet() throws IOException {
		_out.write("<?xml version=\"1.0\" encoding=\"" + XML_ENCODING + "\"?>"
				+ "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
		
		if(colsWidthInfo !=null && colsWidthInfo !="" ){
			_out.write(colsWidthInfo);
		}
		
		_out.write("<sheetData>\n");
	}
	
	
	
	public void setColsWidth(HashMap<Integer ,Integer> cellInfo) throws IOException{
		StringBuffer col=new StringBuffer();
		boolean enable=false; 
		col.append("<cols>");	
		  Set key = cellInfo.keySet();		  
		  for (Iterator iterator = key.iterator(); iterator.hasNext();) {
		                   Integer colkey = (Integer) iterator.next();
		                   Integer colWidth = cellInfo.get(colkey);
		               		enable=true;
		                   col.append("<col min=\""+colkey+"\" max=\""+colkey+"\" width=\""+colWidth+"\" customWidth=\""+colWidth+"\"/>");
		  
		  }
		col.append("</cols>");
		if(enable){
			colsWidthInfo=col.toString();	
		}else{
			colsWidthInfo="";
		}
		
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
	
	public void insertMergeCells(ExcelColumnNewComponentInfo columnList) throws IOException {
		StringBuffer merg=new StringBuffer();	
		merg.append("<mergeCells>");

		Iterator iter = this.columnList.getRows().iterator();	
		String mergeCells = "";
		while(iter.hasNext()) {		 
			ExcelColumnHeaderRow excelColumnHeaderRow = (ExcelColumnHeaderRow)iter.next();
			Set  key =excelColumnHeaderRow.getMergeInfo().keySet();	
			  for (Iterator iterator = key.iterator(); iterator.hasNext();) {
                  String colkey = (String) iterator.next();
                  String info = excelColumnHeaderRow.getMergeInfo().get(colkey);	
                  String[] mergeCell=info.split(":");
                  mergeCells = mergeCells + "<mergeCell ref=\"" + mergeCell[0] + ":" + mergeCell[1]+ "\"/>";
                  //merg.append("<mergeCell ref=\"" + mergeCell[0] + ":" + mergeCell[1]+ "\"/>");	
                 
			  }		
			 
			}
		 merg.append(mergeCells);
		 merg.append( "</mergeCells>");
		 if(!mergeCells.trim().equals("")){
			 _out.write(merg.toString()); 
		 }
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
	public void bodyCreateCell(XSSFWorkbook wb, int columnIndex, Object value, int styleIndex) throws IOException {
		Object localValue = value;	
		String ref = new CellReference(rownum, columnIndex).formatAsString();
		_out.write("<c r=\"" + ref + "\" t=\"inlineStr\"");
		if(styleIndex != -1) {
			_out.write(" s=\"" + styleIndex + "\"");
		}
		_out.write(">");

		if(localValue instanceof String) {
			localValue = StringEscapeUtils.escapeXml((String)localValue);	
			_out.write("<is><t>" + localValue + "</t></is>");
		} else if(localValue instanceof Integer) {
			_out.write("<is><t>" + (Integer)localValue + "</t></is>");
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
	
	public void setStyle(Map<String, XSSFCellStyle> styles){
		this.styles=styles;
	}
	
	
	public void headerCreateCell(XSSFWorkbook wb, String cellKey, Object value, int styleIndex) throws IOException {
		Object localValue = value;			
		String ref=cellKey;
		_out.write("<c r=\"" + ref + "\" t=\"inlineStr\"");
		if(styleIndex != -1) {
			_out.write(" s=\"" + styleIndex + "\"");
		}
		_out.write(">");

		if(localValue instanceof String) {
			localValue = StringEscapeUtils.escapeXml((String)localValue);	
			_out.write("<is><t>" + localValue + "</t></is>");
		} else if(localValue instanceof Integer) {
			_out.write("<is><t>" + (Integer)localValue + "</t></is>");
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

	public BufferedWriter get_out() {
		return _out;
	}
	
	public void set_out(BufferedWriter out) {
		this._out=out;
	}
	
	public void runBodyRows(ResultContext resultContext){
		Map dataMap = (Map)resultContext.getResultObject();
		try{
			insertRow(headerRowLastNum);	
			  Iterator iter = this.columnList.getExcelColumnBodyRow().getBodyCell().iterator();	
			  while(iter.hasNext()) {
				  ExcelColumnBodyCell excelColumnBodyCell = (ExcelColumnBodyCell)iter.next();
				   Integer index = excelColumnBodyCell.getIndex();
	               String code = excelColumnBodyCell.getCode();
	               String style=excelColumnBodyCell.getStyle();
	               Object value = dataMap.get(code);
					int styleIndex = -1;
					if(style != null) {
								styleIndex = styles.get(style).getIndex();
					}
					bodyCreateCell(commonExcelWorkbook, index, value, styleIndex);				
			  }			
			  endRow();
	} catch(Exception e) {
		e.printStackTrace();
		throw new SystemException(e);
	}
		headerRowLastNum++;
	}
	
	public void runHeaderRows() {
		try {		
			
			Iterator iter = this.columnList.getRows().iterator();			
			while(iter.hasNext()) {
				ExcelColumnHeaderRow excelColumnHeaderRow = (ExcelColumnHeaderRow)iter.next();
				headerRowLastNum=excelColumnHeaderRow.getRowIndex();
				insertRow(excelColumnHeaderRow.getRowIndex());
				int columnIdx = 0;
				TreeMap<String,String> tm = new TreeMap<String,String>(excelColumnHeaderRow.getCellName());
				TreeMap<String,String> tm2 = new TreeMap<String,String>(excelColumnHeaderRow.getStyle());
				  Set key =tm.keySet();	
				  for (Iterator iterator = key.iterator(); iterator.hasNext();) {
				                   String colkey = (String) iterator.next();	
				                   String cellName = tm.get(colkey);
				                   if(cellName==null || cellName==""){
				                	   cellName="";
				                   }
				                   int styleIndex = -1;
				                   if(tm2.get(colkey) !=null){
				                	 String style=  tm2.get(colkey);
				                	 if(style != null) {
											styleIndex = styles.get(style).getIndex();
				                	 }
				                   };
				                   //System.out.println(colkey+"::"+cellName+"::celllInfo");
				                   headerCreateCell(commonExcelWorkbook, colkey, cellName, styleIndex);
					columnIdx++; 
				 }		
				  endRow();
				
			}
			headerRowLastNum++;
			
		} catch(IOException e) {
			e.printStackTrace();
			throw new SystemException(e);
		}
	}
	
	public void setSheet(XSSFSheet sheet){
		this.sheetRef = sheet.getPackagePart().getPartName().getName();
		this.sheet=sheet;
	}
	
	
	public File setFile(String fileName) throws IOException{
		tempDirPath = Config.getString("largeData.tempFilePath");
		//tempDirPath = Config.getString(fileName+".tempFilePath");
		File tmpFileDir = new File(tempDirPath);
		if(!tmpFileDir.exists()) {
			tmpFileDir.mkdir();
		}
		
		dataXmltmpFile = File.createTempFile("largeData_", ".xml", tmpFileDir);
		//dataXmltmpFile = File.createTempFile(fileName+"_", ".xml", tmpFileDir);
		tempfileName = dataXmltmpFile.getName();
		
		StringTokenizer arrToken = new StringTokenizer(tempfileName, ".");
		int tokenCnt = 0;
		if(arrToken != null) {
			tokenCnt = arrToken.countTokens();
		}
		if(tokenCnt > 0) {
			tempfileName = arrToken.nextToken();
		}
		
		//save the template
		templateFile = new File(tempDirPath + "/template_" + tempfileName + ".xlsx");
		FileOutputStream templateOutputStream = new FileOutputStream(templateFile);
		commonExcelWorkbook.write(templateOutputStream);
		templateOutputStream.close();

		//resultExcelFile = new File(tempDirPath + "/resultExcel_" + tempfileName + ".xlsx");
		resultExcelFile = new File(tempDirPath + "/"+fileName+"_" + tempfileName + ".xlsx");
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataXmltmpFile), XML_ENCODING));

		resultExcelFileOutStream = new FileOutputStream(resultExcelFile);
		set_out(bufferedWriter);
		return resultExcelFile;
	}
	
	
	public void close(){
		try {
			endSheet();
			insertMergeCells(columnList);
			endworksheet();

			bufferedWriter.close();

			//Step 3. Substitute the template entry with the generated data

			ExcelUtil.substitute(templateFile, dataXmltmpFile, sheetRef.substring(1), resultExcelFileOutStream);
			resultExcelFileOutStream.close();

			if(templateFile.exists()) {
				templateFile.delete();
			}

			if(dataXmltmpFile.exists()) {
				dataXmltmpFile.delete();
			}

		} catch(IOException e1) {
			throw new SystemException(e1);
		}
	}
	
	
	public void setColumnList(ExcelColumnNewComponentInfo columnList){
		this.columnList=columnList;
	}
	
	public void setWorkBook(XSSFWorkbook workbook,String sheetName){
		this.commonExcelWorkbook=workbook;
		setSheet(commonExcelWorkbook.createSheet(sheetName));
		
	}
}
