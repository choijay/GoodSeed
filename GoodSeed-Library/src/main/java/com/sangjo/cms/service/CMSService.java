/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.sangjo.cms.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import goodseed.core.common.GoodseedConfig;
import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.handler.CommonExcelHandler;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.model.factory.ParametersFactory;
import goodseed.core.common.service.GoodseedService;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.date.DateUtil;
import goodseed.core.utility.excel.ExcelColumnComponentInfo;
import goodseed.core.utility.excel.ExcelStylesInfoMap;
import goodseed.core.utility.excel.RowCellRangeAddress;
import goodseed.core.utility.string.StringUtil;

/**
 * The class CMSService
 * <br>
 * CMS와 관련된 공통 Service 로직을 처리
 * <br>
 * 
 * @author jay
 *
 */
@Service("cmsService")
public class CMSService extends GoodseedService {

	private static final Log LOG = LogFactory.getLog(CMSService.class);
	
	private static final String DEFAULT_PATTERN = "yyyyMMdd";
	private static final String CMS_CHARSET = "euc-kr";
	
	private static final String HEADER = "H";
	private static final String DATA = "D";
	private static final String TRAILER = "T";
	
	private static final String MODE_ALPHA = "A";
	private static final String MODE_NUMERIC = "N";
	private static final String MODE_ALPHANUM = "AN";
	private static final String MODE_HANGUL = "H";
	
	private static final String PAD_DIRECTION_LEFT = "L";
	private static final String PAD_DIRECTION_RIGHT = "R";
	
	@Autowired
	private EB13Service eb13Service;
	
	/**
	 * CMS 파일 목록 조회
	 * @param inParams
	 * @return
	 */
	public Parameters getFileList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_fileList", getSqlManager().queryForGoodseedDataset(inParams, "cms.getFileList"));
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT,
					getSqlManager().getTotalCount(inParams, "cms.getFileListTotalCount"));
		}
		
		if(outParams.getGoodseedDataset("ds_fileList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
		
	}
	
	/**
	 * CMS 파일 데이터 조회
	 * @param inParams
	 * @return
	 */
	public Parameters getFileData(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_fileData", getSqlManager().queryForGoodseedDataset(inParams, "cms.getCMSFileData"));
		outParams.setVariable(GoodseedConstants.TOTAL_COUNT, outParams.getGoodseedDataset("ds_fileData").size());
		
		if(outParams.getGoodseedDataset("ds_fileData").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			if (inParams.getVariableAsString("CMS_FILE_NO").startsWith("EB21")) {
				outParams.setVariable("TOTAL_INFO", getSqlManager().queryForObject(inParams, "cms.getCMSFileDataTotalInfo"));
			}
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		return outParams;
		
	}
	
	/**
	 * 오늘 날짜에 생성된 CMS 파일이 있는지 체크
	 * @param inParams
	 *   - FILE_TYPE : EB13, EB21...
	 */
	public void validateWithCreateDt(Parameters inParams) {
		// 오늘 날짜에 생성된 파일이 있는지 체크
		String lastDate = (String) getSqlManager().queryForObject(inParams, "cms.getLastCreateDate");
		if (StringUtils.isNotBlank(lastDate)) {
			String currDate = DateUtil.getDateTime(DEFAULT_PATTERN);
			
			if (currDate.equals(lastDate)) {
				throw new UserHandleException("MSG_COM_ERR_065"); //오늘 날짜에 생성된 파일이 이미 존재합니다.
			}
		}
	}
	
	/**
	 * 유형에 따른 CMS 파일명 생성
	 * @param fileType
	 * @return
	 */
	public String getCmsFileNo(String fileType) {
		if (StringUtils.isBlank(fileType)) {
			throw new UserHandleException("MSG_COM_VAL_001", new String[]{ "CMS 파일유형" });	//{0} 항목은 필수 입력입니다.
		}
		
		return fileType + DateUtil.getDateTime("MMdd"); 
	}
	
	/**
	 * CMS File 파싱해서 db에 저장
	 * @param inParams 
	 *   - FILE_TYPE : 전문번호
	 *   - UPLOADED_DIR_PATH : 파일 경로
	 *   - UPLOADED_FILE_NM : 파일명
	 * @param uploadResult
	 * @return
	 */
	public Parameters saveCMSFile(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		Map<String, String> uploadFileInfo = (Map<String, String>) inParams.getVariable("file");

		BufferedReader br = null;
		
		try {
			
			// CMS 데이터 저장을 위해 파일을 읽어와서 parsing
			File file = new File(GoodseedConfig.getUploadRootDir() + File.separator + uploadFileInfo.get("UPLOADED_DIR_PATH"), uploadFileInfo.get("UPLOADED_FILE_NM"));
			
			if (file.exists()) {
				
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), CMS_CHARSET));
				
				//데이터는 1줄짜리 형식으로 옴.
				String line = null;
				if((line = br.readLine()) == null) {
					throw new UserHandleException("MSG_COM_VAL_056", new String[]{file.getName()});	 //업로드한 파일에 내용이 없습니다. 파일명={0}
				}
				
				// type(H/D/T)별로 전문 자르기
				Map<String, Object> docLines = this.parseLineByType(inParams, line);
				line = null;
				
				//header parse
				inParams.put("TYPE", HEADER);
				Map<String, String> headerFields = this.parseFields(inParams, (String) docLines.get(HEADER));
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("HEADER=" + headerFields);
				}
				
				//header data
				inParams.put("TYPE", DATA);
				List<Map<String, String>> dataList = this.parseDatas(inParams, (List<String>) docLines.get(DATA));
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("DATA=> count=" + dataList.size() + ", =" + dataList);
				}
				
				//header parse
				inParams.put("TYPE", TRAILER);
				Map<String, String> trailerFields = this.parseFields(inParams, (String) docLines.get(TRAILER));
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("TRAILER=" + dataList);
				}
				
				//validation - 불필요할 것 같아 주석처리
//				if (GoodseedConstants.CMS_FILE_TYPE_EB11.equals(inParams.get("FILE_TYPE"))) {
//					if (NumberUtils.toInt(trailerFields.get("TOTAL_DATA_CNT"), 0) != dataList.size()) {
//						throw new UserHandleException("MSG_COM_VAL_057");
//					}
//				}
				
				// CMS 파일 저장
				inParams.put("header", headerFields);
				inParams.put("trailer", trailerFields);
				getSqlManager().insert(inParams, "cms.inserteCMSFile");
				
				// CMS 데이터 저장
				for (Map<String, String> data : dataList) {
					inParams.put("data", data);
					getSqlManager().insert(inParams, "cms.inserteCMSData");
				}
			}
		} catch (DuplicateKeyException e) {
			throw new UserHandleException("MSG_COM_ERR_027", new String[]{uploadFileInfo.get("UPLOADED_FILE_NM")}, e);
		} catch(Exception e) {
			throw new UserHandleException("MSG_COM_ERR_015", e);
		}

		outParams.setMessage("MSG_COM_SUC_012");
		
		return outParams;
	}
	
	/**
	 * Layout Type 별(H/D/T)로 전문을 자른다.
	 * @param inParams
	 * @param documentTxt
	 * @return
	 */
	private Map<String, Object> parseLineByType(Parameters inParams, String documentTxt) throws Exception {
		
		List<Map<String, Object>> list = getSqlManager().queryForList(inParams, "cms.getLayoutGroupByType");
		
		int length = 0;	// H/D/T의 총 길이
		
		int headerLength = 0;	// H의 길이
		int dataLength = 0;		// D의 길이
		int trailerLength = 0;	// T의 길이
		
		for (Map<String, Object> groupData : list) {
			length = ((Long) groupData.get("TOT_LENGTH")).intValue();
			
			switch ((String) groupData.get("TYPE")) {
			case HEADER :
				headerLength = length;
				break;
			case DATA : 
				dataLength = length;
				break;
			case TRAILER :
				trailerLength = length;
				break;
			default:
				break;
			}
		}
		
		byte[] documentBytes = documentTxt.getBytes(CMS_CHARSET);
		int totalLength = documentBytes.length;
		
		Map<String, Object> lineByType = new HashMap<String, Object>();
		lineByType.put(HEADER, new String(documentBytes, 0, headerLength));
		lineByType.put(TRAILER, new String(documentBytes, totalLength-trailerLength, trailerLength));
		
		byte[] dataBytes = new String(documentBytes, headerLength, totalLength - headerLength - trailerLength, CMS_CHARSET).getBytes(CMS_CHARSET);
		int dataCount = dataBytes.length / dataLength;
		
		List<String> datas = new ArrayList<String>(dataCount);
		if (dataCount > 1) {
			
			for (int i=0; i < dataCount; i++) {
				datas.add(new String(dataBytes, i * dataLength, dataLength, CMS_CHARSET));
			}
		} else {
			datas.add(new String(dataBytes, CMS_CHARSET));
		}
		lineByType.put(DATA, datas);
		
		return lineByType;
	}
	
	/**
	 * layout 에 맞춰 Header/Trailer부 데이터를 자른다.
	 * @param inParams
	 * @param line
	 * @return
	 */
	private Map<String, String> parseFields(Parameters inParams, String line) throws Exception {
		
		if (StringUtils.isBlank(line))
			return null;
		
		List<Map<String, String>> layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");

		return split(layouts, line);
	}
	
	/**
	 * layout 에 맞춰 Data부 데이터를 자른다.
	 * @param inParams
	 * @param lines
	 * @return
	 */
	private List<Map<String, String>> parseDatas(Parameters inParams, List<String> lines) throws Exception {
		
		List<Map<String, String>> layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");
		
		List<Map<String, String>> fields = new ArrayList<Map<String, String>>();
		for (String line : lines) {
			if (StringUtils.isBlank(line))
				continue;
			
			fields.add(split(layouts, line));
		}
		
		return fields;
	}
	
	/**
	 * H/D/T layout 형식에 맞춰 해당 행의 데이터를 자른다.
	 * @param layouts
	 * @param line
	 * @return
	 */
	private Map<String, String> split(List<Map<String, String>> layouts, String line) throws Exception {
		
		if (layouts == null || layouts.size() == 0)
			return null;
		
		byte[] lineBytes = line.getBytes(CMS_CHARSET);
		
		Map<String, String> fieldMap = new HashMap<String, String>();
		int length = 0, beginIndex = 0;
		String value = null;
		
		for (Map<String, String> layout : layouts) {
			length = NumberUtils.toInt(layout.get("LENGTH"), 0);
			value = new String(lineBytes, beginIndex, length, CMS_CHARSET);
			
			switch(layout.get("MODE")) {
				case MODE_ALPHA : 
					value = StringUtils.trim(value);
					break;
				case MODE_NUMERIC :
					value = String.valueOf(NumberUtils.toInt(value, 0));
					break;
				case MODE_ALPHANUM :
					value = StringUtils.trim(value);				//앞/뒤 공백제거
					
					if ("BANK_CD".equals(layout.get("NAME"))) {
						value = value.substring(0, 3);
						break;
					}

					if (StringUtils.isNotEmpty(layout.get("PAD_TEXT"))) {
						if (PAD_DIRECTION_LEFT.equals(layout.get("PAD_DIRECTION"))) {
							value = StringUtil.removeLeftPad(value, layout.get("PAD_TEXT"));	//맨앞 pad 제거
						} else {
							value = StringUtil.removeRightPad(value, layout.get("PAD_TEXT"));	//맨앞 pad 제거
						}
					}
					break;
				default :
					break;
			}
			
			fieldMap.put(layout.get("NAME"), value);
			
			beginIndex += length;
		}
		
		return fieldMap;
	}
	
	public void saveApplyFlag(Parameters inParams) {
		getSqlManager().update(inParams, "cms.updateCMSFile");
	}
	
	/**
	 * CMS_FILE_NO 에 해당하는 layout에 맞춰 전문을 생성한다.
	 * @param inParams
	 *   - CMS_FILE_NO : CMS 파일번호
	 *   - FILE_TYPE : CMS 전문번호
	 * @return
	 */
	public Parameters getTelegramDown(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		BufferedWriter bw = null;
		
		File outputFile = null;
		
		try {
			//CMS_FILE 조회
			Map<String, Object> cmsFileMap = (Map<String, Object>) getSqlManager().queryForObject(inParams, "cms.getCMSFile");
			
			String fileDir = GoodseedConfig.getUploadRootDir() + File.separator + cmsFileMap.get("FILE_PATH");
			String fileName = fileDir + File.separator + cmsFileMap.get("FILE_NAME");
			
			outputFile = new File(fileName);
			if (outputFile.exists()) {
				outParams.setVariable("FilePath", fileName);
				return outParams;
			}
			
			File dir = new File(fileDir);
			if (!dir.exists()) {
				//ExecuteUtil.execute("chmod 777 " + file.getAbsolutePath(), 2000);
				dir.mkdirs();
			}
			
			bw = new BufferedWriter(new FileWriterWithEncoding(fileName, CMS_CHARSET));
			
			inParams.put("ORG_CD", Config.getString("company.cms.code"));
			inParams.put("COMP_BANK_CD", Config.getString("company.cms.bankCode"));
			inParams.put("COMP_ACCOUNT_NO", Config.getString("company.cms.accountNo"));
			inParams.put("FILE_NAME", cmsFileMap.get("FILE_NAME"));
			inParams.put("REQ_DT", StringUtils.substring((String)cmsFileMap.get("PROCESS_DT"), 2)); //연도 4자리 중 앞 2자리 제외-6자리
			inParams.put("REQ_DT2", (String)cmsFileMap.get("PROCESS_DT"));//8자리
			
			Map<String, Object> rowData = null;
			List<Map<String, String>> layouts = null;
			
			//1- header부 생성
			inParams.put("TYPE", HEADER);
			layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");
			bw.write(this.appendTelegram(inParams, layouts));
			
			//2- data부 생성
			String bankBookText = Config.getString("company.cms.accountName");
			bankBookText += StringUtil.toFullChar("   ");//총 16자리이므로 2byte씩 8글자를 맞추기 위해 space 3개를 넣음
				
			inParams.put("TYPE", DATA);
			layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");
			
			//CMS_FILE_DATA 조회
			GoodseedDataset ds = null;
			if (GoodseedConstants.CMS_FILE_TYPE_EB13.equals(inParams.get("FILE_TYPE"))) {
				ds = eb13Service.getFileData(inParams).getGoodseedDataset("ds_fileData");
			} else {
				ds = this.getFileData(inParams).getGoodseedDataset("ds_fileData");
			}
			
			for(int index = 0; index < ds.getRowCount(); index++) {
				rowData = ds.getActiveRowData(index);
				rowData.put("ORG_CD", inParams.get("ORG_CD"));
				rowData.put("BANK_BOOK_TEXT", bankBookText);
				
				bw.write(this.appendTelegram(rowData, layouts));
				
				//Trailer에서 사용하기 위해 sum 구하기
				this.addDataCount(inParams, "TOTAL_DATA_CNT", 1);
				
				if (GoodseedConstants.CMS_FILE_TYPE_EB21.equals(inParams.get("FILE_TYPE"))) {
					this.addDataCount(inParams, "REQ_CNT", 1);
					this.addDataCount(inParams, "REQ_AMT", (Integer) rowData.get("PAY_REQ_AMT"));
				} else {
					switch((String) rowData.get("REQ_TYPE")) {
						case "1" : //신규
							this.addDataCount(inParams, "NEW_CNT", 1);
							break;
						case "3" : //해지
							this.addDataCount(inParams, "TERMINATE_CNT", 1);
							break;
						case "7" : //임의해지
							this.addDataCount(inParams, "TMP_TERM_CNT", 1);
							break;
						default :
							this.addDataCount(inParams, "EXCEPTION", 1);
							break;
					}
				}
			}
			
			//3- trailer부 생성
			inParams.put("TYPE", TRAILER);
			layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");
			bw.write(this.appendTelegram(inParams, layouts));
			
			bw.flush();
			
			outParams.setVariable("FilePath", fileName);

		} catch (UserHandleException ue) {
			if (outputFile != null) outputFile.delete();
			throw ue;
		} catch (Exception e) {
			if (outputFile != null) outputFile.delete();
			throw new UserHandleException("MSG_CMS_ERR_003", e);
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					throw new UserHandleException("MSG_CMS_ERR_003", e);
				}
		}
		
		return outParams;
	}

	/**
	 * CMS_FILE_NO 에 해당하는 layout에 맞춰 전문을 생성한다. (EI13 전문생성)
	 * @param inParams
	 *   - CMS_FILE_NO : CMS 파일번호
	 *   - FILE_TYPE : CMS 전문번호
	 * @return
	 */
	public Parameters getTelegramDownForEI13(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		FileOutputStream fos = null;	//raw stream data를 write 해야하므로 BufferedWriter를 사용할 수 없음.
		
		try {
			//CMS_FILE 조회
			Parameters cmsParam = this.getCMSFile(inParams);
			Map<String, Object> cmsFileMap = (Map<String, Object>) cmsParam.getVariable("cmsFile");
			
			String fileDir = GoodseedConfig.getUploadRootDir() + File.separator + cmsFileMap.get("FILE_PATH");
			String fileName = fileDir + File.separator + ((String)cmsFileMap.get("FILE_NAME")).replace("EB13", "EI13");
			outParams.setVariable("FilePath", fileName);
			
			//이미 생성된 파일이 있으면 그 파일을 다운로드
			/*
			if (new File(fileName).exists()) {
				return outParams;
			}
			*/
			
			//파일 생성
			File dir = new File(fileDir);
			if (!dir.exists()) {
				//ExecuteUtil.execute("chmod 777 " + file.getAbsolutePath(), 2000);
				dir.mkdirs();
			}
			
			fos = new FileOutputStream(fileName);
			
			//Common 데이터 set
			inParams.put("ORG_CD", Config.getString("company.cms.code"));
			inParams.put("REQ_DT", (String)cmsFileMap.get("PROCESS_DT"));//8자리
			inParams.put("TOTAL_DATA_CNT", getSqlManager().queryForObject(inParams, "cms.getNewReqCountOfEB13"));
			
			List<Map<String, String>> layouts = null;
			
			//1- header부 생성
			inParams.put("TYPE", HEADER);
			layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");
			fos.write(this.appendTelegram(inParams, layouts).getBytes());
			
			//1- data부 생성
			inParams.put("TYPE", DATA);
			layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");
			
			//CMS_FILE_DATA 조회
			inParams.setVariable("NEW_REQ_TYPE", "Y");
			GoodseedDataset ds = this.getFileData(inParams).getGoodseedDataset("ds_fileData");

			Map<String, Object> rowData = null;
			Map fileInfo = null;			//증빙파일정보
			String attachFileName = null;	//증빙파일명full
			File attachFile = null;			//증빙파일 객체
			int rowBlockCount = 0;			//데이터부 block count(1024 byte가 몇개인지)
			int totalBlockCount = 0;		//데이터부 전체 block count(rowBlockCount의 SUM)
			int bLen = 0;
			
			String rowDataStr = null;		//데이터부 전문 string
			byte[] fileBytes = null;		//증빙파일 데이터 byte array
			
			for(int index = 0; index < ds.getRowCount(); index++) {
				rowData = ds.getActiveRowData(index);
				
				rowData.put("ORG_CD", inParams.get("ORG_CD"));
				rowData.put("REQ_DT", inParams.get("REQ_DT"));
				rowData.put("SEQ", index+1);
				
				//증빙파일정보 조회
				rowData.put("FILE_SEQ", rowData.get("ATTACH_FILE_SEQ"));
				fileInfo = (Map) this.getSqlManager().queryForObject(rowData, "contract.getAttachFile");
				
				attachFileName = GoodseedConfig.getUploadRootDir() + File.separator + (String)fileInfo.get("UPLOADED_DIR_PATH") + File.separator + (String)fileInfo.get("UPLOADED_FILE_NM");
				attachFile = new File(attachFileName);
				
				//증빙파일이 존재하지 않는경우
				if (!attachFile.exists()) {
					throw new UserHandleException("MSG_CMS_ERR_004");
				}
				
				String originalFileName = (String)fileInfo.get("SOURCE_FILE_NM");
				rowData.put("FILE_EXT", originalFileName.substring(originalFileName.lastIndexOf(".") +1));
				rowData.put("FILE_SIZE", fileInfo.get("FILE_SIZE"));
				
				rowDataStr = this.appendTelegram(rowData, layouts);
				fos.write(rowDataStr.getBytes());
				
				//EI13의 파일 데이터 부분
				fileBytes = FileUtils.readFileToByteArray(attachFile);
				fos.write(fileBytes);
				
				//EI13의 파일 FILLER 부분
				bLen = rowDataStr.length() + fileBytes.length;
				rowBlockCount = (int) Math.ceil((double) bLen / 1024);
				fos.write(StringUtils.leftPad("", rowBlockCount * 1024 - bLen, " ").getBytes());
				
				totalBlockCount += rowBlockCount;
			}
			
			//3- trailer부 생성
			inParams.put("TOTAL_BLOCK_CNT", totalBlockCount);
			inParams.put("TYPE", TRAILER);
			layouts = getSqlManager().queryForList(inParams, "cms.getLayoutsOfType");
			fos.write(this.appendTelegram(inParams, layouts).getBytes());
			
			fos.flush();
			
		} catch (UserHandleException ue) {
			throw ue;
		} catch (Exception e) {
			throw new UserHandleException("MSG_CMS_ERR_003", e);
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					throw new UserHandleException("MSG_CMS_ERR_003", e);
				}
		}
		
		return outParams;
	}
	
	/**
	 * 해당 key의 값을 val만큼 증가 시킨다.
	 * @param inParams 저장할 map 객체
	 * @param key 저장할 key 값
	 */
	private void addDataCount(Parameters inParams, String key, int val) {
		if (!inParams.containsKey(key)) {
			inParams.put(key, val);
		} else {
			inParams.put(key, ((Integer)inParams.get(key)) + val);
		}
	}
	
	/**
	 * layout 에 맞춰 전문 문자열을 append
	 * @param sb
	 * @param rowData
	 * @param layouts
	 */
	private String appendTelegram(Map<String, Object> rowData, List<Map<String, String>> layouts) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		
		int rowTotalLength = 0;
		int length = 0;
		String key = null;
		String value = null;
		String padStr = null;
		
		for (Map<String, String> layout : layouts) {
			
			key = layout.get("NAME");
			
			length = NumberUtils.toInt(layout.get("LENGTH"), 0);
			padStr = StringUtils.defaultIfEmpty(layout.get("PAD_TEXT"), "");
			
			if (rowData.containsKey(key)) {
				value = String.valueOf(rowData.get(key));
			} else {
				value = layout.get("DEFAULT_VALUE");
				if (StringUtils.isBlank(value)) {
					value = padStr;
				}
			}
			value = StringUtils.replaceAll(value, "/", "");
			value = StringUtils.replaceAll(value, "-", "");
			value = StringUtils.replaceAll(value, "\\.", "");

			//통장기재내용 한글 부분을 제외하고, 모두 L/R PAD
			if (!(MODE_HANGUL.equals(layout.get("MODE")) && "BANK_BOOK_TEXT".equals(key))) {	
				if (PAD_DIRECTION_LEFT.equals(layout.get("PAD_DIRECTION"))) {
					value = StringUtils.leftPad(value, length, padStr);	
				} else {
					value = StringUtils.rightPad(value, length, padStr);
				}
			}
			
			sb.append(value);
			rowTotalLength += length;
		}
		
		if (rowTotalLength != sb.toString().getBytes(CMS_CHARSET).length) {
			System.err.println("(1):" + rowTotalLength + ",(2):" + sb.toString().getBytes(CMS_CHARSET).length);
			System.err.println(sb.toString());
			throw new UserHandleException("MSG_CMS_ERR_002");
		}
		
		return sb.toString();
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
		CommonExcelHandler excelHandler = new CommonExcelHandler(workbook, inParams.getVariableAsString("FILE_TYPE"), styleInfoMap, columnList, cellRangeAddressList);
		excelHandler.setParams(outParams);

		return getSqlManager().queryForExcelDownload(inParams, excelHandler, "cms.getCMSFileDataForExcel");
	}
	
	public Parameters getCMSFile(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setVariable("cmsFile", getSqlManager().queryForObject(inParams, "cms.getCMSFile"));
		return outParams;
	}
	
	/**
	 * CMS File 테이블의 상태를 반영상태로 변경
	 * @param inParams
	 */
	public void saveApplyStatusOfCMSFile(Parameters inParams) {
		getSqlManager().update(inParams, "cms.updateApplyStatusOfCMSFile");
	}
	
	/**
	 * 고객의 CMS 입금 내역을 조회
	 * @param inParams
	 * @return
	 */
	public Parameters getPaymentList(Parameters inParams) {
		Parameters outParams = ParametersFactory.createParameters(inParams);
		outParams.setGoodseedDataset("ds_paymentList", getSqlManager().queryForGoodseedDataset(inParams, "cms.getPaymentList"));
		
		if("true".equals(inParams.getVariableAsString("DO_COUNTTOT"))) {
			outParams.setVariable(GoodseedConstants.TOTAL_COUNT, outParams.getGoodseedDataset("ds_paymentList").size());
			//getSqlManager().queryForList(inParams, "cms.getPaymentListTotalCount")
		}
		
		if(outParams.getGoodseedDataset("ds_paymentList").isDataSetEmpty()) {
			outParams.setStatusMessage("MSG_COM_ERR_007");
		} else {
			outParams.setStatusMessage("MSG_COM_SUC_011");
		}
		
		return outParams;
	}
	
	/**
	 * 신청내역 삭제
	 * @param inParams
	 * @return
	 */
	public Parameters delete(Parameters inParams) {
		
		Parameters outParams = ParametersFactory.createParameters(inParams);
		
		String applyYn = (String) getSqlManager().queryForObject(inParams, "cms.getApplyYnOfCmsFile");
		Boolean deleteYn = new Boolean(Config.getString(GoodseedConstants.CMS_FILE_DELETE));
		if (!deleteYn && "1".equals(applyYn)) {
			throw new UserHandleException("MSG_CMS_ERR_009"); //이미 처리되어 삭제할 수 없습니다.
		}
		
		getSqlManager().delete(inParams, "cms.deleteCMSFile");
		getSqlManager().delete(inParams, "cms.deleteCMSFileData");
		
		outParams.setMessage("MSG_COM_SUC_006");
		
		return outParams;
	}
	
}
