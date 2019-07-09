/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.sangjo.cms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.handler.CommonExcelHandler;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.date.DateUtil;
import goodseed.core.utility.excel.ExcelColumnComponentInfo;
import goodseed.core.utility.excel.ExcelStylesInfoMap;
import goodseed.core.utility.excel.RowCellRangeAddress;
import goodseed.core.utility.file.HtmlFileUploader;

/**
 * The class EB21Service
 * <br>
 * 출금의뢰 EB21 Service
 * <br>
 * 출금의뢰내역(EB21) : 이용기관 → 센터 → 참가기관(은행)
 * 
 * @author jay
 *
 */
@Service("eb21Service")
public class EB21Service extends GoodseedService {

	@Autowired
	private CMSService cmsService;
	
	/**
	 * 출금의뢰 데이터 조회
	 * @param inParams
	 * @return
	 */
	public Parameters searchData(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);

		outParams.setGoodseedDataset("ds_fileData", getSqlManager().queryForGoodseedDataset(inParams, "eb21.searchData"));
		
		if(outParams.getGoodseedDataset("ds_fileData").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setVariable("TOTAL_INFO", getSqlManager().queryForObject(inParams, "eb21.getSearchDataTotalInfo"));
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		
		return outParams;
	}
	
	/**
	 * 출금의뢰 데이터 저장
	 * @param inParams
	 * @param uploadResult
	 * @return
	 */
	public Parameters saveData(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		GoodseedDataset goodseedDataset = inParams.getGoodseedDataset("ds_fileData");

		if(goodseedDataset != null) {

			inParams.put("PROCESS_DT", inParams.getVariableAsString("PAY_REQ_DT").replaceAll("-", ""));
			
			//이미 해당 날짜에 만든게 있다면 생성할 수 없다고 말해줘야 함.
			int exists = getSqlManager().getTotalCount(inParams, "eb21.getExistTelegrams");
			if (exists > 0) {
				throw new UserHandleException("MSG_CMS_ERR_013"); //해당날짜의 전문은 이미 생성되었습니다.
			}
			
			inParams.setVariable("FILE_PATH", HtmlFileUploader.getUploadDirDateFormat(GoodseedConstants.CMS_FILE));
			inParams.setVariable("CMS_FILE_NO", inParams.getVariableAsString("FILE_TYPE") + inParams.getVariableAsString("PROCESS_DT").substring(4));
			getSqlManager().insert(inParams, "eb21.insertEB21File");
			
			for(int row = 0; row < goodseedDataset.getRowCount(); row++) {
				goodseedDataset.setActiveRow(row);

				goodseedDataset.setColumn(row, "CMS_FILE_NO", inParams.getVariableAsString("CMS_FILE_NO"));
				goodseedDataset.setColumn(row, "PROCESS_DT", inParams.getVariableAsString("PROCESS_DT"));
				goodseedDataset.setColumn(row, "SEQ", row+1);
				getSqlManager().insert(goodseedDataset, "eb21.insertEB21FileData");
			}

			outParams.setMessage("MSG_COM_SUC_003");
		}
		
		return outParams;
	}
	
	/**
	 * 
	 * @param inParams
	 */
	public void updateStatus(Parameters inParams) {
		
		Parameters fileParams = cmsService.getCMSFile(inParams);
		Map<String, Object> fileInfo = (Map<String, Object>)fileParams.getVariable("cmsFile");
		
//		if ("1".equals(fileInfo.get("APPLY_YN"))) {	//반영됨
//			throw new UserHandleException("MSG_CMS_ERR_005");
//		}
		
		//cms_file cms파일 정보에 처리여부 update
		cmsService.saveApplyStatusOfCMSFile(inParams);
	}	

	public Parameters getExcelDown(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		XSSFWorkbook workbook = new XSSFWorkbook();

		//엑셀 셀스타일 정보 세팅 시작
		//headerCell
		ExcelStylesInfoMap styleInfoMap = new ExcelStylesInfoMap();
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFont);
		styleInfoMap.setStyle("header", style);

		//bodyCell
		style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		styleInfoMap.setStyle("body", style);
		//엑셀 셀 스타일 정보 세팅 종료 

		//엑셀 헤더,칼럼,바디 정보 등록
		List<ExcelColumnComponentInfo> columnList = new ArrayList<ExcelColumnComponentInfo>();

		ExcelColumnComponentInfo excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderRow1ColumnName(inParams.getVariableAsString("FILE_TYPE") + "List");
		excelColumnComponentInfo.setHeaderRow1CellStyleCode("header");
		excelColumnComponentInfo.setHeaderColumnCode("REQ_DT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("신청일자");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CONT_NO");
		excelColumnComponentInfo.setHeaderRow2ColumnName("계약번호");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("PAYER_NO");
		excelColumnComponentInfo.setHeaderRow2ColumnName("납부자번호");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CUST_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("고객명");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("REGULAR_PAY_DAY");
		excelColumnComponentInfo.setHeaderRow2ColumnName("출금일");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("BANK_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("은행");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ACCOUNT_NO");
		excelColumnComponentInfo.setHeaderRow2ColumnName("계좌번호");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ACCOUNT_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("예금주명");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ACCOUNT_BIRTH");
		excelColumnComponentInfo.setHeaderRow2ColumnName("예금주생년월일");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("MONTH_PAY_AMT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("월출금액");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("UNPAID_AMT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("미납액");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("NEXT_TEMP_PAY_AMT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("차기출금액");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("PAY_REQ_AMT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("출금요청액");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("RESULT_CD");
		excelColumnComponentInfo.setHeaderRow2ColumnName("처리결과");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ERROR_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("오류내용");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		//머지정보 
		ArrayList<RowCellRangeAddress> cellRangeAddressList = new ArrayList();
		RowCellRangeAddress rowCellRangeAddress = new RowCellRangeAddress(0, columnList.size()-1);
		cellRangeAddressList.add(rowCellRangeAddress);

		//CommonExcelHandler 생성
		CommonExcelHandler excelHandler = new CommonExcelHandler(workbook, inParams.getVariableAsString("FILE_TYPE"), styleInfoMap, columnList, cellRangeAddressList);
		excelHandler.setParams(outParams);

		return getSqlManager().queryForExcelDownload(inParams, excelHandler, "eb21.getCMSFileDataForExcel");
		
	}
	
	
}
