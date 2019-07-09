/*
 * Copyright (c) 2011 CJ OliveNetworks
 * All rights reserved.
 *
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.common.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import goodseed.core.common.GoodseedConfig;
import goodseed.core.common.utility.CharSetUtil;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.file.HtmlFileUploader;

/**
 * The class HtmlUploadController<br>
 *
 * @author jay
 * @version 1.0
 *
 */
@Controller
@RequestMapping("htmlUpload")
public class HtmlUploadController extends BaseController {

	private static final String BIZ_TYPE = "BIZ_TYPE";
	private static final String TEMP_UPLOAD_YN = "TEMP_UPLOAD_YN";
	private static final String FILE_VS_ROW = "filevsrow";
	private static final String EXTENTION = "extention";
	private static final String LIMIT = "limit";

	/**
	 * 파일 업로드<br>
	 * <br>
	 * 데이터 처리 없이 오직 물리적인 파일 업로드만 수행한다.<br>
	 * 
	 * 1. 파일 업로드 수행 시 Multipart인지 확인. multipart가 아닐 경우 코드에 해당하는 config에 설정된 에러 메시지 출력
	 * 2. Multipart인 경우에 업로드 수행, 업무 구분에 따른 설정값을 가져온다.
	 *   - BIZ_TYPE(업무구분), TEMP_UPLOAD_YN(임시업로드유무), FILE_VS_ROW(파일리스트), LIMIT(파일 사이즈 제한), EXTENTION(파일 확장자)
	 * 3. 설정값을 가지고 업로드할 파일들의 유효성 검사를 실시한다
	 * 4. 유효성 검사에 따라 해당하는 에러 메시지 출력
	 * 5. HtmlFileUploader를 이용해 파일을 업로드
	 * 6. 임시파일을 삭제한다.
	 * 
	 * <br>
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	@RequestMapping("/uploadFile")
	public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Check that we have a file upload request
		// Utility method that determines whether the request contains multipart content
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if(!isMultipart) {
			// multipart가 아니면...
			response.setContentType("text/plain");
			response.setCharacterEncoding(CharSetUtil.getDefaultCharSet(request));
			response.getWriter().println(getUploadResult(Config.getString("customVariable.msgComErr060"), null));
		} else {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// Sets the size threshold beyond which files are written directly to disk
			// The default value is 10240 bytes
			//factory.setSizeThreshold(10240);

			// Sets the directory used to temporarily store files that are larger than the configured size threshold
			factory.setRepository(new File(GoodseedConfig.getUploadRootDir()));//yourTempDirectory

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Set overall request size constraint
			// The maximum allowed size, in bytes. The default value of -1 indicates, that there is no limit
			//upload.setSizeMax(10240);//yourMaxRequestSize

			// Sets the maximum allowed size of a single uploaded file
			//upload.setFileSizeMax(10240);

			// Parse the request
			try {
				Map<String, String> uploadConfig = new HashMap<String, String>();
				Map<String, String> rowIdList = new HashMap<String, String>();
				List<FileItem> items = upload.parseRequest(request);

				// 업무구분에 따른 설정 값 가져오기
				// limit, extension
				for(FileItem item : items) {
					if(item.isFormField()) {
						if(item.getFieldName().equalsIgnoreCase(BIZ_TYPE)) {
							uploadConfig.put(BIZ_TYPE, item.getString());
						}
						if(item.getFieldName().equalsIgnoreCase(TEMP_UPLOAD_YN)) {
							uploadConfig.put(TEMP_UPLOAD_YN, item.getString());
						}
						if(item.getFieldName().equalsIgnoreCase(FILE_VS_ROW)) {
							String temp = item.getString();
							String[] tempList = temp.split(",");
							for(String key : tempList) {
								rowIdList.put(key.split("=")[0], key.split("=")[1]);
							}
						}
					}
				}

				String[] configs = GoodseedConfig.getUploadConfig(uploadConfig.get(BIZ_TYPE));

				String result = null;
				if(configs != null) {

					uploadConfig.put(LIMIT, configs[0]);
					uploadConfig.put(EXTENTION, configs[1]);
					
					//설정값을 가지고 업로드할 파일들의 유효성 검사를 실시한다.
					boolean checkExtention = false;
					boolean checkLimit = true;
					for(FileItem item : items) {
						if(!item.isFormField()) {
							String[] extentions =
									org.springframework.util.StringUtils.tokenizeToStringArray(uploadConfig.get(EXTENTION), "|");
							int limit = Integer.parseInt(uploadConfig.get(LIMIT));
							checkExtention = FilenameUtils.isExtension(item.getName(), extentions);
							checkLimit = HtmlFileUploader.fileSizeValidate(item.get(), limit);
						}
					}
					if(!checkExtention) {
						result = getUploadResult(Config.getString("customVariable.msgComErr005"), null);
					} else if(!checkLimit) {
						result = getUploadResult(Config.getString("customVariable.msgComErr006"), null);
					} else {

						List<Map<String, String>> uploadResultList = new ArrayList<Map<String, String>>();
						for(FileItem item : items) {
							if(!item.isFormField()) {
								uploadResultList.add(HtmlFileUploader.upload(item, uploadConfig));
							}
						}
						result = getUploadResult("0", uploadResultList);
					}
				} else {
					// 설정값이 없으므로 업로드할 경로를 찾을 수 없어 에러
					result = getUploadResult(Config.getString("customVariable.msgComErr064"), null);
				}

				// 임시파일 삭제
				for(FileItem item : items) {
					item.delete();
				}
				response.setContentType("text/plain");
				response.setCharacterEncoding(CharSetUtil.getDefaultCharSet(request));
				response.getWriter().println(result);

			} catch(FileUploadException e) {
				throw new UserHandleException(Config.getString("customVariable.msgComErr010"), e);
			}
		}
	}

	/**
	 * <br>
	 * 업로드 파일 정보를 JSON 데이터로 만들어주는 함수<br>
	 * <br>
	 * @param errorCode 에러코드
	 * @param uploadResult 업로드 결과셋, 복수의 map의 리스트 형태
	 * @return JSON 형태의 업로드 파일정보
	 */
	@SuppressWarnings("unchecked")
	private String getUploadResult(String errorCode, List<Map<String, String>> uploadResult) {
		JSONObject result = new JSONObject();
		result.put("ErrorCode", errorCode);
		result.put("data", uploadResult);
		return result.toJSONString();
	}
}
