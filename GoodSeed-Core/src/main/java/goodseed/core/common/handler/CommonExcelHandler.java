/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import goodseed.core.common.model.Parameters;
import goodseed.core.common.utility.CharSetUtil;
import goodseed.core.exception.SystemException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.date.DateUtil;
import goodseed.core.utility.excel.ExcelColumnComponentInfo;
import goodseed.core.utility.excel.ExcelStylesInfoMap;
import goodseed.core.utility.excel.ExcelUtil;
import goodseed.core.utility.excel.RowCellRangeAddress;
import goodseed.core.utility.excel.SpreadsheetWriter;

/**
 *  The class CommonExcelHandler<br>
 * <br>
 * 엑셀 로우 핸들러<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 *
 */
public class CommonExcelHandler implements ResultHandler {

	private static final Log LOG = LogFactory.getLog(CommonExcelHandler.class);

	//인코딩 정보
	private static final String XML_ENCODING = CharSetUtil.getDefaultCharSet();

	//엑셀행수
	private int rowNum = 0;

	//임시 스프레드xml 데이타 파일
	private File dataXmltmpFile = null;
	//최종 생성 엑셀 파일
	private File resultExcelFile = null;
	//임시 템플릿 파일
	private File templateFile = null;

	//엑셀 셀 스타일 정보
	private Map<String, XSSFCellStyle> styles = null;
	//칼럼 정보 리스트
	private List<ExcelColumnComponentInfo> columnList = null;
	//헤더 칼럼 머지 정보 리스트
	private ArrayList<RowCellRangeAddress> cellRangeAddressList = null;

	//엑셀 헤더1 존재 여부
	private boolean headerRow1 = false;
	//엑셀 헤더2 존재 여부
	private boolean headerRow2 = false;

	//버퍼 스트림 (임시스프페드뒤스 xml)
	private BufferedWriter bufferedWriter = null;
	//엑셀 결과 파일 스트림
	private FileOutputStream resultExcelFileOutStream = null;
	//스프레드 시트 xml 생성
	private SpreadsheetWriter spreadsheetWriter = null;

	private String sheetRef = null;
	private XSSFWorkbook commonExcelWorkbook = null;

	//아웃 파라미터
	private Parameters outParams;

	/**
	 * 엑셀 로우 핸들러 생성자
	 * @param workbook 엑셀 워크 북
	 * @param sheetName 엑셀 시트 명
	 * @param styleInfoMap 엑셀 셀 스타일 정보
	 * @param columnList 엑셀 칼럼 정보 리스트
	 * @param cellRangeAddressList 엑셀 헤더 머지 정보
	 */
	public CommonExcelHandler(XSSFWorkbook workbook, String sheetName, ExcelStylesInfoMap styleInfoMap,
			List<ExcelColumnComponentInfo> columnList, ArrayList<RowCellRangeAddress> cellRangeAddressList) {

		try {

			//생성한 파일명
			String tempfileName = null;
			XSSFSheet sheet = null;
			//엑셀 임시 다운로드 경로
			String tempDirPath = null;

			this.styles = styleInfoMap.getStyles();
			this.columnList = columnList;
			this.cellRangeAddressList = cellRangeAddressList;
			this.commonExcelWorkbook = workbook;

			// Step 1. Create a template file. Setup sheets and workbook-level objects such as
			// cell styles, number formats, etc.
			sheet = commonExcelWorkbook.createSheet(sheetName);

			//name of the zip entry holding sheet data, e.g. /xl/worksheets/sheet1.xml
			sheetRef = sheet.getPackagePart().getPartName().getName();

			//Step 2. Generate XML file.
			tempDirPath = Config.getString("largeData.tempFilePath");
			File tmpFileDir = new File(tempDirPath);
			if(!tmpFileDir.exists()) {
				tmpFileDir.mkdir();
			}

			dataXmltmpFile = File.createTempFile(sheetName + "_", ".xml", tmpFileDir);
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

			resultExcelFile = new File(tempDirPath + "/excel_" + sheetName + "_" + DateUtil.getDateTime("yyyyMMddHHmmssSSS") + ".xlsx");
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataXmltmpFile), XML_ENCODING));

			resultExcelFileOutStream = new FileOutputStream(resultExcelFile);

			spreadsheetWriter = new SpreadsheetWriter(bufferedWriter);
			spreadsheetWriter.beginSheet();

			Iterator iter = columnList.iterator();
			//헤더1 헤더 2 정보 존재 여부 확인
			while(iter.hasNext()) {
				ExcelColumnComponentInfo excelColumnComponentInfo = (ExcelColumnComponentInfo)iter.next();
				String header1Data = excelColumnComponentInfo.getHeaderRow1ColumnName();
				if(header1Data != null && header1Data.length() > 0) {
					headerRow1 = true;
				}
				String header2Data = excelColumnComponentInfo.getHeaderRow2ColumnName();
				if(header2Data != null && header2Data.length() > 0) {
					headerRow2 = true;
				}
			}

			//헤더 정보 세팅
			setHeaderRows();

		} catch(IOException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
			throw new SystemException(e);
		}
	}

	@Override
	public void handleResult(ResultContext resultContext) {
		Map dataMap = (Map)resultContext.getResultObject();
		LOG.debug("rowNum : " + rowNum);
		try {
			spreadsheetWriter.insertRow(rowNum);
			Iterator iter = columnList.iterator();
			int columnIndex = 0;
			while(iter.hasNext()) {
				ExcelColumnComponentInfo excelColumnComponentInfo = (ExcelColumnComponentInfo)iter.next();
				String headerColumnCode = excelColumnComponentInfo.getHeaderColumnCode();
				Object value = dataMap.get(headerColumnCode);
				String bodyStyle = excelColumnComponentInfo.getBodyCellStyleCode();
				int styleIndex = -1;
				if(bodyStyle != null) {
					styleIndex = styles.get(bodyStyle).getIndex();
				}
				spreadsheetWriter.createCell(commonExcelWorkbook, columnIndex, value, styleIndex);
				columnIndex++;
			}
			spreadsheetWriter.endRow();
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
			throw new SystemException(e);
		}

		rowNum++;
	}

	/**
	 * 엑셀 로우 핸들러 종료
	 */
	public void close() {
		try {
			spreadsheetWriter.endSheet();
			spreadsheetWriter.insertMergeCells(cellRangeAddressList);
			spreadsheetWriter.endworksheet();

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

			outParams.setVariable("excelFile", resultExcelFile);

		} catch(IOException e1) {
			throw new SystemException(e1);
		}
	}

	/**
	 * 엑셀 헤더 생성
	 */
	private void setHeaderRows() {
		try {

			//헤더 정보1 이 있는 경우
			if(headerRow1) {
				spreadsheetWriter.insertRow(rowNum);
				Iterator iter = columnList.iterator();
				int columnIdx = 0;
				while(iter.hasNext()) {
					ExcelColumnComponentInfo excelColumnComponentInfo = (ExcelColumnComponentInfo)iter.next();
					String header1Data = excelColumnComponentInfo.getHeaderRow1ColumnName();
					String header1Style = excelColumnComponentInfo.getHeaderRow1CellStyleCode();
					int styleIndex = -1;
					if(header1Style != null) {
						styleIndex = styles.get(header1Style).getIndex();
					}
					spreadsheetWriter.createCell(commonExcelWorkbook, columnIdx, header1Data, styleIndex);
					columnIdx++;
				}
				spreadsheetWriter.endRow();
				rowNum++;
			}

			//헤더 정보2 이 있는 경우
			if(headerRow2) {
				spreadsheetWriter.insertRow(rowNum);
				Iterator iter = columnList.iterator();
				int columnIdx = 0;
				while(iter.hasNext()) {
					ExcelColumnComponentInfo excelColumnComponentInfo = (ExcelColumnComponentInfo)iter.next();
					String header2Data = excelColumnComponentInfo.getHeaderRow2ColumnName();
					String header2Style = excelColumnComponentInfo.getHeaderRow2CellStyleCode();
					int styleIndex = -1;
					if(header2Style != null) {
						styleIndex = styles.get(header2Style).getIndex();
					}
					spreadsheetWriter.createCell(commonExcelWorkbook, columnIdx, header2Data, styleIndex);
					columnIdx++;
				}
				spreadsheetWriter.endRow();
				rowNum++;
			}

		} catch(IOException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
			throw new SystemException(e);
		}
	}

	/**
	 * ParamMap 등록
	 * @param params
	 */
	public void setParams(Parameters params) {
		this.outParams = params;
	}

	/**
	 * 출력용 Parameters를 반환한다.
	 * @return 출력용 ParamMap 인스턴스
	 */
	public Parameters getOutParams() {
		return this.outParams;
	}
}
