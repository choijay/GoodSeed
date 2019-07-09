/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.handler;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import goodseed.core.common.model.Parameters;
import goodseed.core.exception.SystemException;
import goodseed.core.utility.excel.ExcelColumnNewComponentInfo;
import goodseed.core.utility.excel.ExcelStylesInfoMap;
import goodseed.core.utility.excel.Spreadsheet;

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

public class CommonNewExcelHandler implements ResultHandler {

	private static final Log LOG = LogFactory.getLog(CommonExcelHandler.class);

	//최종 생성 엑셀 파일
	private File resultExcelFile = null;

	//스프레드 시트 xml 생성
	private Spreadsheet sh = null;

	//아웃 파라미터
	private Parameters outParams;

	/**
	 * 엑셀 로우 핸들러 생성자
	 * @param workbook 엑셀 워크 북
	 * @param fileName 엑셀 파일 명
	 * @param sheetName 엑셀 시트 명
	 * @param styleInfoMap 엑셀 셀 스타일 정보
	 * @param columnList 엑셀 칼럼 정보 리스트
	 * @param sh xml의 을 생성해주는 인터페이스
	 */
	public CommonNewExcelHandler(XSSFWorkbook workbook, String fileName, String sheetName, ExcelStylesInfoMap styleInfoMap,
			ExcelColumnNewComponentInfo columnList, Spreadsheet sh) {

		try {
			this.sh = sh;
			sh.setStyle(styleInfoMap.getStyles());
			sh.setColumnList(columnList);

			sh.setWorkBook(workbook, sheetName);
			this.resultExcelFile = sh.setFile(fileName);
			if(columnList.getExcelSheetColumnWidth() != null) {
				sh.setColsWidth(columnList.getExcelSheetColumnWidth().getColumnWidth());
			}
			sh.beginSheet();
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
	//	public void handleRow(Object arg0) {
	public void handleResult(ResultContext resultContext) {
		sh.runBodyRows(resultContext);
	}

	/**
	 * 엑셀 로우 핸들러 종료
	 */
	public void close() {
		sh.close();
		outParams.setVariable("excelFile", resultExcelFile);
	}

	/**
	 * 엑셀 헤더 생성
	 */
	private void setHeaderRows() {
		sh.runHeaderRows();
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
