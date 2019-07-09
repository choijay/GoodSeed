/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import goodseed.core.common.GoodseedConfig;
import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.Parameters;
import goodseed.core.common.utility.CharSetUtil;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.file.HtmlFileUploader;

/**
 * The class BaseController<br>
 *
 * MultiActionController 를 상속받은 컨트롤러<br>
 * 기본적인 Validation Check 수행

 * @author jay
 * @version 1.0
 * 
 */
public class BaseController  {

	protected static final String EXTENTION = "extention";
	protected static final String LIMIT = "limit";
	protected static final String BIZ_TYPE = "BIZ_TYPE";
	
	protected void parseMultipartContent(HttpServletRequest request, HttpServletResponse response, Parameters inParams) {
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if(!isMultipart) {
			// multipart가 아니면...
			response.setContentType("text/plain");
			response.setCharacterEncoding(CharSetUtil.getDefaultCharSet(request));
			throw new UserHandleException(Config.getString("customVariable.msgComErr060"));
		} else {
			
			try {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setRepository(new File(GoodseedConfig.getUploadRootDir()));//yourTempDirectory, upload.dir
	
				ServletFileUpload upload = new ServletFileUpload(factory);

				Map<String, String> uploadConfig = new HashMap<String, String>();
				
				List<FileItem> items = upload.parseRequest(request);
				
				if (items.size() == 0) {
					throw new UserHandleException("MSG_COM_ERR_004");
				}

				// 업무구분에 따른 설정 값 가져오기
				String bizType = inParams.getVariableAsString(BIZ_TYPE);
				uploadConfig.put(LIMIT, Config.getString(GoodseedConstants.UPLOAD_CONFIG + "." + bizType + ".limit"));
				uploadConfig.put(EXTENTION, Config.getString(GoodseedConstants.UPLOAD_CONFIG + "." + bizType + ".extention"));
				uploadConfig.put(BIZ_TYPE, bizType);
				
				//설정값을 가지고 업로드할 파일들의 유효성 검사를 실시한다.
				boolean isValid = false;
				
				for (FileItem item : items) {
					if (!item.isFormField()) {	//input type이 text인 경우는 formField가 true임.
						
						if (isValid) {
							//확장자 체크
							String[] extentions =
									org.springframework.util.StringUtils.tokenizeToStringArray(uploadConfig.get(EXTENTION), "|");
							boolean checkExtention = FilenameUtils.isExtension(item.getName(), extentions);
							
							if(!checkExtention) {
								throw new UserHandleException(Config.getString("customVariable.msgComErr005"));
							}
							
							int limit = Integer.parseInt(uploadConfig.get(LIMIT));
							boolean checkLimit = true;
							checkLimit = HtmlFileUploader.fileSizeValidate(item.get(), limit);
							
							if(!checkLimit) {
								throw new UserHandleException(Config.getString("customVariable.msgComErr006"));
							}
						}
					} else {
						//파일 element를 제외하고
						inParams.put(item.getFieldName(), item.getString(CharSetUtil.getDefaultCharSet(request)));
					}
				}

				//파일 저장
				Map<String, String> uploadResult = null;
				for (FileItem item : items) {
					if(!item.isFormField()) {
						uploadResult = HtmlFileUploader.upload(item, uploadConfig);
						inParams.put(item.getFieldName(), uploadResult);
						
						//임시파일 삭제
						item.delete();
					}
				}

			} catch(FileUploadException e) {
				throw new UserHandleException(Config.getString("customVariable.msgComErr010"), e);
			} catch(UserHandleException ue) {
				throw ue;
			} catch(Exception e) {
				throw new UserHandleException("MSG_COM_ERR_010", e);
			}
		}
		
	}
}
