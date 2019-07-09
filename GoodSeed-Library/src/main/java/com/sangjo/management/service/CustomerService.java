/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.sangjo.management.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.common.syscommon.utility.CodeUtil;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.handler.CommonExcelHandler;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.excel.ExcelColumnComponentInfo;
import goodseed.core.utility.excel.ExcelStylesInfoMap;
import goodseed.core.utility.excel.RowCellRangeAddress;

/**
 * The class CustomerService
 * <br>
 * 고객관리 Service<br>
 * <br>
 * 
 * @author jay
 *
 */
@Service
public class CustomerService extends GoodseedService {

	/**
	 * 고객 리스트 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 06.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getCustomerList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		inParams.setVariable("VISIBLE_PAY_STATUS_09", Config.getString("security.paystatus.09.visible"));
		outParams.setGoodseedDataset("ds_customerList", getSqlManager().queryForGoodseedDataset(inParams, "customer.getCustomerList"));
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "customer.getCustomerListTotalCount"));
		}
		if(outParams.getGoodseedDataset("ds_customerList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}
	
	public Parameters getCustomerListForExcelDown(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		inParams.setVariable("VISIBLE_PAY_STATUS_09", Config.getString("security.paystatus.09.visible"));

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
		
		//bodyCell
		style = workbook.createCellStyle();
		XSSFDataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("#,##0"));
		style.setAlignment(HorizontalAlignment.RIGHT);
		styleInfoMap.setStyle("int", style);
		//엑셀 셀 스타일 정보 세팅 종료 
		
		//엑셀 헤더,칼럼,바디 정보 등록
		List<ExcelColumnComponentInfo> columnList = new ArrayList<ExcelColumnComponentInfo>();

		ExcelColumnComponentInfo excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderRow1ColumnName("고객정보 List");
		excelColumnComponentInfo.setHeaderRow1CellStyleCode("header");
		excelColumnComponentInfo.setHeaderColumnCode("CUST_NO");
		excelColumnComponentInfo.setHeaderRow2ColumnName("고객번호");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CUST_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("고객명");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CUST_BIRTH");
		excelColumnComponentInfo.setHeaderRow2ColumnName("생년월일");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("HP");
		excelColumnComponentInfo.setHeaderRow2ColumnName("대표연락처");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CONT_NO");
		excelColumnComponentInfo.setHeaderRow2ColumnName("회원번호");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CONT_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("계약자명");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CONT_DT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("가입일");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("BANK_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("은행명");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ACCOUNT_NO");
		excelColumnComponentInfo.setHeaderRow2ColumnName("계좌번호");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ACCOUNT_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("예금주");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("TERMS_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("약관명");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CUST_STATUS_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("회원상태");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("CONT_STATUS_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("계약상태");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("PAY_STATUS_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("납부상태");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("LAST_PAY_DT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("최근납부일");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("TOT_PAY_AMT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("총납입금액");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("int");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("REST_AMT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("잔여금액");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("int");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("SHOP_NM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("매장이름");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ZIP_CD");
		excelColumnComponentInfo.setHeaderRow2ColumnName("우편번호");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("ADDR");
		excelColumnComponentInfo.setHeaderRow2ColumnName("주소");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		//머지정보 
		ArrayList<RowCellRangeAddress> cellRangeAddressList = new ArrayList();
		RowCellRangeAddress rowCellRangeAddress = new RowCellRangeAddress(0, columnList.size()-1);
		cellRangeAddressList.add(rowCellRangeAddress);

		//CommonExcelHandler 생성
		CommonExcelHandler excelHandler = new CommonExcelHandler(workbook, "customer", styleInfoMap, columnList, cellRangeAddressList);
		excelHandler.setParams(outParams);

		outParams = getSqlManager().queryForExcelDownload(inParams, excelHandler, "customer.getCustomerListForExcelDown");
		
		return outParams;
	}
	
	/**
	 * 고객 정보 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 19.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters getCustomer(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		Map<String, Object> customer = (Map<String, Object>) getSqlManager().queryForObject(inParams, "customer.getCustomer");
		customer.put("CUST_STATUS_NM", CodeUtil.getCodeName("GS022", (String) customer.get("CUST_STATUS_CD"), GoodseedConstants.LANGUAGECODE)); 
		outParams.setVariable("customer", customer);
		
		return outParams;
	}
	
	/**
	 * 고객 정보 저장
	 *
	 * @ahthor jay
	 * @since 2016. 12. 19.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters saveCustomer(Parameters inParams) {
		
		String custNo = inParams.getVariableAsString("CUST_NO");
		
		if (StringUtils.isBlank(custNo)) {
			//신규 입력
			getSqlManager().insert(inParams, "customer.insertCustomer");
			
		} else {
			//수정
			getSqlManager().update(inParams, "customer.updateCustomer");
			
		}
		
		Parameters outParams = this.getCustomer(inParams);
		outParams.setMessage("MSG_COM_SUC_003");
		return outParams;
	}

	/**
	 * 고객 정보 삭제
	 *
	 * @ahthor jay
	 * @since 2016. 12. 19.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters deleteCustomer(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		String custNo = inParams.getVariableAsString("CUST_NO");
		
		if (StringUtils.isNotBlank(custNo)) {
			
			//계약종료 상태이거나 계약상태가 입력 외의 계약이 있으면 삭제 불가
//			Map<String, Object> custInfo = (Map<String, Object>) this.getCustomer(inParams).getVariable("customer");
//			String custStatusCd = (String) custInfo.get("CUST_STATUS_CD");
//			if ("021001".equals(custStatusCd)) {	//고객 상태가 계약진행인 경우 삭제 불가
//				throw new UserHandleException("MSG_DPS_ERR_003");
//			}
			
			//계약상태가 입력 외의 계약이 있으면 삭제 불가
			int contCount = this.getSqlManager().getTotalCount(inParams, "customer.getInProgressContract");
			if (contCount > 0) {
				throw new UserHandleException("MSG_DPS_ERR_003");
			}
			
			getSqlManager().delete(inParams, "contract.deleteContract");
			getSqlManager().delete(inParams, "customer.deleteCustomer");
			
			outParams.setMessage("MSG_COM_SUC_006");
		} else {
			outParams.setMessage("MSG_COM_ERR_001");
		}
		
		return outParams;
		
	}
	
}
