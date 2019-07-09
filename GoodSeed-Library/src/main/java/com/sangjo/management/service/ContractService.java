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
import goodseed.core.utility.excel.ExcelColumnComponentInfo;
import goodseed.core.utility.excel.ExcelStylesInfoMap;
import goodseed.core.utility.excel.RowCellRangeAddress;

/**
 * The class ContractService
 * <br>
 * 계약관리 Service<br>
 * <br>
 * 
 * @author jay
 *
 */
@Service
public class ContractService extends GoodseedService {

	/**
	 * 계약 리스트 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 20.
	 *
	 * @param inParams
	 * @return
	 */
	public Parameters getContractList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_contractList", getSqlManager().queryForGoodseedDataset(inParams, "contract.getContractList"));
		if(outParams.getGoodseedDataset("ds_contractList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
	}
	
	/**
	 * 고객의 계약정보 조회
	 *
	 * @ahthor jay
	 * @since 2016. 12. 20.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters getContract(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		Map<String, Object> contract = (Map<String, Object>) getSqlManager().queryForObject(inParams, "contract.getContract");
		contract.put("PAY_STATUS_NM", CodeUtil.getCodeName("GS023", (String) contract.get("PAY_STATUS_CD"), GoodseedConstants.LANGUAGECODE));
		contract.put("CMS_APPROVE_STATUS_NM", CodeUtil.getCodeName("GS035", (String) contract.get("CMS_APPROVE_STATUS_CD"), GoodseedConstants.LANGUAGECODE));
		outParams.setVariable("contract", contract);
		return outParams;
	}
	
	/**
	 * 계약 정보 저장
	 *
	 * @ahthor jay
	 * @since 2016. 12. 20.
	 * 
	 * @param inParams
	 * @return
	 */
	public Parameters saveContract(Parameters inParams) {
		
		Map<String, String> fileParam = null;
		
		//파일업로드-계약서
		if (inParams.get("CONT_FILE") instanceof Map) {
			fileParam = (Map)inParams.getVariable("CONT_FILE");
			fileParam.put("TYPE", "1");	//계약서
			fileParam.put("g_userId", inParams.getVariableAsString("g_userId"));
			getSqlManager().insert(fileParam, "contract.insertAttachFile");
			inParams.put("CONT_FILE_SEQ", fileParam.get("FILE_SEQ"));
		}
		
		//파일업로드-CMS
		if (inParams.get("CMS_FILE") instanceof Map) {
			fileParam = (Map)inParams.getVariable("CMS_FILE");
			fileParam.put("TYPE", "2");	//CMS
			fileParam.put("g_userId", inParams.getVariableAsString("g_userId"));
			getSqlManager().insert(fileParam, "contract.insertAttachFile");
			inParams.put("CMS_FILE_SEQ", fileParam.get("FILE_SEQ"));
		}
		
		//신규신청하는 경우
		if ("02".equals(inParams.getVariableAsString("CONT_STATUS_CD"))) {
			//은행명, 계좌번호, 예금주명, 예금주생년월일, 증빙파일이 모두 입력되었는지 확인
			if (StringUtils.isBlank(inParams.getVariableAsString("BANK_CD"))
					|| StringUtils.isBlank(inParams.getVariableAsString("ACCOUNT_NO"))
					|| StringUtils.isBlank(inParams.getVariableAsString("ACCOUNT_NM"))
					|| StringUtils.isBlank(inParams.getVariableAsString("ACCOUNT_BIRTH"))
					|| StringUtils.isBlank(inParams.getVariableAsString("CMS_FILE_SEQ"))) 
			{
				throw new UserHandleException("MSG_CMS_ERR_010");
			}
		}
		
		String contNo = inParams.getVariableAsString("CONT_NO");
		
		if (StringUtils.isBlank(contNo)) {	//계약 신규
			
			//set 납부, cms 상태
			if ("02".equals(inParams.getVariableAsString("CONT_STATUS_CD"))) {	//계약상태가 신청 이면
				inParams.setVariable("PAY_STATUS_CD", "01");	//신청중
				inParams.setVariable("CMS_APPROVE_STATUS_CD", "01");	//신규신청
			}
			
			//회원번호를 채번하지 않고, 회원증서 번호를 입력받고 그것을 동일하게 사용하는 것으로 변경함.
			inParams.setVariable("CONT_NO", inParams.getVariableAsString("CONT_DOC_NO"));
			int exists = getSqlManager().getTotalCount(inParams, "contract.existContractNo");
			if (exists > 0) {
				throw new UserHandleException("MSG_DPS_ERR_004");//회원증서 번호가 이미 존재합니다. 다른번호를 넣어주세요.
			}
			
			getSqlManager().insert(inParams, "contract.insertContract");
			
		} else {	//계약 수정
			
			//기존 계약정보를 조회
			Map<String, Object> contractMap = (Map<String, Object>) this.getContract(inParams).getVariable("contract");
			
			//계약상태코드를 변경한 경우
			if (! inParams.getVariableAsString("CONT_STATUS_CD").equals((String) contractMap.get("CONT_STATUS_CD"))) {
				
				//set 납부, cms 상태
				String contStatusCd = inParams.getVariableAsString("CONT_STATUS_CD");
				String payStatusCd = "";
				String cmsApproveStatusCd = "";
				
				//현재 계좌 신규신청/해지신청이 전문으로 전송된 상태이면 상태변경 불가.
				if (("01".equals((String) contractMap.get("CMS_APPROVE_STATUS_CD")) || "03".equals((String) contractMap.get("CMS_APPROVE_STATUS_CD")))
						&& "Y".equals((String) contractMap.get("TELEGRAM_ING"))) {	
					throw new UserHandleException("MSG_CMS_ERR_011");	//현재 계좌신청이 진행중이므로 상태변경을 할 수 없습니다.
				}

				switch(contStatusCd) {
					case "01" :	//입력
						payStatusCd = "";	//없음
						cmsApproveStatusCd = "";	//없음
						break;
					case "02" ://신청
						payStatusCd = "01";	//신청중
						cmsApproveStatusCd = "01";	//신규신청
						break;
					case "03" ://진행중 - 보류에서 진행중으로 변경할 수도 있음.
						if ("04".equals((String)contractMap.get("CMS_APPROVE_STATUS_CD")) ) {	//해지완료 상태라면
							throw new UserHandleException("MSG_CMS_ERR_012"); //CMS 계좌가 해지처리되어 재신청해야 합니다.
						} 
						else if (!"01".equals((String)contractMap.get("CMS_APPROVE_STATUS_CD")) ) {	//신규신청중이 아니라면
							payStatusCd = "02";	//납부중
							cmsApproveStatusCd = "02";	//승인완료
						} 
						break;
					case "04" ://보류
						payStatusCd = "06";	//보류
						break;
					case "05" ://발인해지
					case "06" ://중도해지
						
						//결제상태가 발인후납이면서 납부자번호가 없는 경우
						if ("09".equals((String)contractMap.get("PAY_STATUS_CD")) && StringUtils.isBlank((String)contractMap.get("PAYER_NO"))) {
							payStatusCd = "09";	//발인후납
							cmsApproveStatusCd = "09";	//없음
						} 
						else if (StringUtils.isBlank((String)contractMap.get("PAYER_NO"))) {	//납부자번호가 없는것은 cms 해지를 신청할 필요가 없으므로, 곧장 해지상태 
							payStatusCd = "08";	//해지
							cmsApproveStatusCd = "04";	//해지완료
						}
						else if ("04".equals((String)contractMap.get("CMS_APPROVE_STATUS_CD"))) {	//이미 해지완료되었으면
							payStatusCd = "08";	//해지
							cmsApproveStatusCd = "04";	//해지완료
						} 
						else if ("05".equals((String)contractMap.get("CMS_APPROVE_STATUS_CD")) ) {	//은행접수 해지이면
							payStatusCd = "08";	//해지
							cmsApproveStatusCd = "05";	//은행접수 해지
						}
						else {
							payStatusCd = "07";	//해지신청중
							cmsApproveStatusCd = "03";	//해지신청
						}
						break;
				}
				
				inParams.setVariable("PAY_STATUS_CD", payStatusCd);
				inParams.setVariable("CMS_APPROVE_STATUS_CD", cmsApproveStatusCd);
			}
			
			//계좌 재신청 하는 경우
			if ("Y".equals(inParams.getVariableAsString("REAPPLY_ACCOUNT"))) {
				
				// 1) 계좌정보 변경으로 인해 CMS 해지-신규 신청하는 경우 - , 신규 PAYER_NO 할당
				if ("02".equals((String) contractMap.get("CMS_APPROVE_STATUS_CD"))) {	//이미 승인완료된 계좌를 재신청하는 경우
					
					//이전 PAYER_NO는 해지로 백업
					Parameters beforeAccount = ParametersFactory.createParameters(inParams);
					beforeAccount.setVariable("CONT_NO", (String) contractMap.get("CONT_NO")); 
					beforeAccount.setVariable("CMS_APPROVE_STATUS_CD", "03"); 
					beforeAccount.setVariable("g_userId", inParams.getVariableAsString("g_userId")); 
		            
					getSqlManager().insert(beforeAccount, "contract.insertCMSPayer");
					
					//신규 PAYER_NO 할당
					inParams.setVariable("PAYER_NO", (String) getSqlManager().queryForObject("contract.getPayerNo"));
					
					inParams.setVariable("CONT_STATUS_CD", "02");	//계좌신청
					inParams.setVariable("PAY_STATUS_CD", "01");	//신청중
					inParams.setVariable("CMS_APPROVE_STATUS_CD", "01");	//신규신청
				}
				
				// 2) 기존 CMS승인 오류로 인해 계좌정보를 변경하고 재신청 - 이때는 PAYER_NO를 새로 만들필요가 없음
				else if ("06".equals((String) contractMap.get("CMS_APPROVE_STATUS_CD"))) {
					inParams.setVariable("PAY_STATUS_CD", "01");	//신청중
					inParams.setVariable("CMS_APPROVE_STATUS_CD", "01");	//신규신청
				}
				
				// 3) 기존 해지된 계좌정보를 새롭게 변경해서 진행하고 싶을때 재신청 
				else if ("04".equals((String) contractMap.get("CMS_APPROVE_STATUS_CD"))) {
					//신규 PAYER_NO 할당
					inParams.setVariable("PAYER_NO", (String) getSqlManager().queryForObject("contract.getPayerNo"));
					
					inParams.setVariable("CONT_STATUS_CD", "02");	//계좌신청
					inParams.setVariable("PAY_STATUS_CD", "01");	//신청중
					inParams.setVariable("CMS_APPROVE_STATUS_CD", "01");	//신규신청
				}
			}
			
			//수정
			getSqlManager().update(inParams, "contract.updateContract");
			
		}
		
		//고객의 계좌가 신규 등록/해지됨에 따라 고객상태를 변경
		getSqlManager().update(inParams, "customer.updateCustomerStatus");
		
		Parameters outParams = this.getContract(inParams);
		outParams.setMessage("MSG_COM_SUC_003");
		return outParams;
	}

	private String getPayStatusCd(String contStatusCd) {
		
		String payStatusCd = "";
		
		if (StringUtils.isBlank(contStatusCd))
			return payStatusCd;
		
		switch(contStatusCd) {
			case "01" :	//입력
				payStatusCd = "";	//없음
				break;
			case "02" ://신청
				payStatusCd = "01";	//신청중
				break;
			case "03" ://진행중 - 보류에서 진행중으로 변경할 수도 있음.
				payStatusCd = "02";	//납부중
				break;
			case "04" ://보류
				payStatusCd = "06";	//보류
				break;
			case "05" ://발인해지
			case "06" ://중도해지
				payStatusCd = "07";	//해지신청중
				break;
		}
		
		return payStatusCd;
	}
	
	private String getCmsApproveStatusCd(String contStatusCd) {
		
		String cmsApproveStatusCd = "";
		
		if (StringUtils.isBlank(contStatusCd))
			return cmsApproveStatusCd;
		
		switch(contStatusCd) {
		case "01" :	//입력
			cmsApproveStatusCd = "";	//없음
			break;
		case "02" ://신청
			cmsApproveStatusCd = "01";	//신규신청
			break;
		case "05" ://발인해지
		case "07" ://중도해지
			cmsApproveStatusCd = "03";	//해지신청
			break;
		}
		
		return cmsApproveStatusCd;
	}
	
	public void saveContractStatus(Parameters inParams) {
		
		getSqlManager().insert(inParams, "contract.updateContractStatus");
	}
	
	public Map getFileInfo(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		return (Map) getSqlManager().queryForObject(inParams, "contract.getAttachFile");
	}
	
	public Parameters getPaymentListExcelDown(Parameters inParams) {
		
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
		excelColumnComponentInfo.setHeaderRow1ColumnName("입금내역");
		excelColumnComponentInfo.setHeaderRow1CellStyleCode("header");
		excelColumnComponentInfo.setHeaderColumnCode("ROWNUM");
		excelColumnComponentInfo.setHeaderRow2ColumnName("회차");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("PROCESS_DT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("청구일");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);

		excelColumnComponentInfo = new ExcelColumnComponentInfo();
		excelColumnComponentInfo.setHeaderColumnCode("PAID_AMT");
		excelColumnComponentInfo.setHeaderRow2ColumnName("입금액");
		excelColumnComponentInfo.setHeaderRow2CellStyleCode("body");
		columnList.add(excelColumnComponentInfo);
		
		//머지정보 
		ArrayList<RowCellRangeAddress> cellRangeAddressList = new ArrayList();
		RowCellRangeAddress rowCellRangeAddress = new RowCellRangeAddress(0, columnList.size()-1);
		cellRangeAddressList.add(rowCellRangeAddress);

		//CommonExcelHandler 생성
		CommonExcelHandler excelHandler = new CommonExcelHandler(workbook, "BigGrid", styleInfoMap, columnList, cellRangeAddressList);
		excelHandler.setParams(outParams);

		outParams = getSqlManager().queryForExcelDownload(inParams, excelHandler, "cms.getPaymentListExcelDownload");
		
		return outParams;

		
	}
}
