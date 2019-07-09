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
 * The class EB13Service
 * <br>
 * 계좌신청 EB13 Service
 * <br>
 * 이용기관접수 - 
 * 신청내역(EB13) : 이용기관 → 센터 → 참가기관(은행)
 * 출금동의 증빙자료 (EI13) : 이용기관 → 센터 → 자동이체 통합관리시스템
 * 
 * @author jay
 *
 */
@Service("eb13Service")
public class EB13Service extends GoodseedService {

	@Autowired
	private CMSService cmsService;
	
	/**
	 * eb13 파일 데이터 조회
	 * @param inParams
	 * @return
	 */
	public Parameters getFileData(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_fileData", getSqlManager().queryForGoodseedDataset(inParams, "eb13.getCMSFileData"));
		outParams.setVariable(GoodseedConstants.TOTAL_COUNT, outParams.getGoodseedDataset("ds_fileData").size());
		
		if(outParams.getGoodseedDataset("ds_fileData").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
		
	}
	
	/**
	 * 신규계좌신청 데이터 생성
	 * @param inParams
	 * @param uploadResult
	 * @return
	 */
	public Parameters createData(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		validateWithExistResultFile(inParams);
		
		// 만들 데이터가 존재하는지 체크
		int existsCount = getSqlManager().getTotalCount(inParams, "eb13.existsData");
		if (existsCount > 0) {
			
			//CMS FILE NO 채번
			String cmsFileNo = cmsService.getCmsFileNo(inParams.getVariableAsString("FILE_TYPE"));
			
			//납부상태 ‘신규신청중(01)’과 ‘해지신청중(07)’인 데이터를 조회
			//이때 cms 승인상태가 ‘신규신청(01)’ 또는 ‘해지신청(03)’인 데이터는 이미 신청한 자료이므로 제외한다.
			
			inParams.put("CMS_FILE_NO", cmsFileNo);
			inParams.put("PROCESS_DT", DateUtil.getDateTime("yyyyMMdd"));
			getSqlManager().insert(inParams, "eb13.insertEB13FileData");
			
			inParams.put("FILE_PATH", HtmlFileUploader.getUploadDirDateFormat(GoodseedConstants.CMS_FILE));
			getSqlManager().insert(inParams, "eb13.insertEB13File");
			
			outParams.setMessage("MSG_COM_SUC_009");
		} else {
			outParams.setMessage("MSG_COM_ERR_066");
		}
		
		return outParams;
	}

	/**
	 * 이전에 생성되어 전송되고, 결과를 받지 않은 CMS파일이 있는지 체크
	 * @param inParams
	 *   - FILE_TYPE : EB13, EB21...
	 */
	private void validateWithExistResultFile(Parameters inParams) {
		
		Object flag = getSqlManager().queryForObject(inParams, "eb13.validateWithExistResultFile");
		if (flag != null && "0".equals((String)flag)) {
			throw new UserHandleException("MSG_CMS_ERR_008"); //결과를 반영해야 신청데이터를 생성할 수 있습니다.
		}
	}
	
	/**
	 * 
	 * @param inParams
	 */
	public void updateStatus(Parameters inParams) {
		
		Parameters fileParams = cmsService.getCMSFile(inParams);
		
//		Map<String, Object> fileInfo = (Map<String, Object>)fileParams.getVariable("cmsFile");
//		if ("1".equals(fileInfo.get("APPLY_YN"))) {	//반영됨
//			throw new UserHandleException("MSG_CMS_ERR_005");
//		}
		
		//contract 계약정보에 cms승인상태코드 변경
		//getSqlManager().update(inParams, "eb13.updateStatus");
		
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
		excelColumnComponentInfo.setHeaderRow2ColumnName("회원번호");
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
		excelColumnComponentInfo.setHeaderColumnCode("REQ_TYPE_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("처리구분");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ATTACH_FILE_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("첨부파일명");
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
		CommonExcelHandler excelHandler = new CommonExcelHandler(workbook, inParams.getVariableAsString("CMS_FILE_NO"), styleInfoMap, columnList, cellRangeAddressList);
		excelHandler.setParams(outParams);

		return getSqlManager().queryForExcelDownload(inParams, excelHandler, "eb13.getCMSFileDataForExcel");
	}	
}
