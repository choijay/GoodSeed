/*
 * Copyright (c) 2011 CJ OliveNetworks
 * All rights reserved.
 *
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.utility.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.fileupload.FileItem;

import goodseed.core.common.GoodseedConfig;
import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.exception.SystemException;
import goodseed.core.exception.UserHandleException;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.process.ExecuteUtil;
import goodseed.core.utility.string.StringUtil;

/**
 * The class HtmlFileUploader
 * 
 * HtmlUploadController에서 넘겨준 업로드 설정 정보를 받아 실제 파일 업로드를 수행하는 class
 * 
 * @author jay
 * @version 1.0
 *
 */
public class HtmlFileUploader {

	private static final String BIZ_TYPE = "BIZ_TYPE";
	private static final String TEMP_UPLOAD_YN = "TEMP_UPLOAD_YN";

	private static final String TEMP_DIR = "temp";
	private static final String RECYCLEB_IN = "recyclebin";
	private static final String SOURCE_FILE_NM = "SOURCE_FILE_NM";
	private static final String FILE_SIZE = "FILE_SIZE";
	private static final String ROW_ID = "rowId";

	private static final String UPLOADED_DIR_PATH = "UPLOADED_DIR_PATH";
	private static final String UPLOADED_FILE_NM = "UPLOADED_FILE_NM";

	/**
	 * 
	 * 업로드 설정 정보를 받아 실제 파일 업로드를 수행
	 * 1. 
	 * 2.
	 * 3.
	 * 4.
	 * 5.
	 * 
	 * @author jay
	 * @version 1.0
	 *
	 */
	public static Map<String, String> upload(FileItem item, Map<String, String> uploadConfig) {
		Map<String, String> uploadResult = new HashMap<String, String>();
		uploadResult.put(SOURCE_FILE_NM, item.getName());
		uploadResult.put(TEMP_UPLOAD_YN, StringUtil.defaultString(uploadConfig.get(TEMP_UPLOAD_YN), "0"));
		
		String uploadPath = null;
		
		String uploadedFileName = null;
		if (GoodseedConstants.CMS_FILE.equals((String)uploadConfig.get(BIZ_TYPE))) {	//전문
			uploadedFileName = item.getName();
		} else {
			uploadedFileName = getRandomFileName();
		}
		//String 
		if("1".equalsIgnoreCase(StringUtil.defaultString(uploadConfig.get(TEMP_UPLOAD_YN), "0"))
				|| "Y".equalsIgnoreCase(StringUtil.defaultString(uploadConfig.get(TEMP_UPLOAD_YN), "N"))) {
			uploadPath = GoodseedConfig.getUploadDir(TEMP_DIR);
		} else {
			uploadPath = getUploadDirDateFormat(uploadConfig.get(BIZ_TYPE));
		}
		String uploadFullPath = GoodseedConfig.getUploadRootDir() + "/" + uploadPath;
		try {
			File uploadDir = new File(uploadFullPath);
			if(!uploadDir.exists()) {
				uploadDir.mkdirs();
				if("/".equals(File.separator) && "true".equals(Config.getString("upload.setDirectoryPermission"))) {
					chmod(uploadDir, Config.getString("upload.directoryPermission"));
					String parentPath = uploadDir.getParent();
					while(parentPath != null) {
						if(parentPath.equals(Config.getString("upload.dir.root"))) {
							break;
						}
						File parentDir = new File(parentPath);
						chmod(parentDir, Config.getString("upload.directoryPermission"));
						parentPath = parentDir.getParent();
					}
				}
			}
			item.write(new File(uploadFullPath, uploadedFileName));
			uploadResult.put(UPLOADED_DIR_PATH, uploadPath);
			uploadResult.put(UPLOADED_FILE_NM, uploadedFileName);
			uploadResult.put(FILE_SIZE, Long.toString(item.getSize()));
		} catch(Exception e) {
			throw new UserHandleException(Config.getString("customVariable.msgComErr010"));
		}

		return uploadResult;
	}

	/**
	 * 업로드 용량제한 확인.
	 *
	 * @param file byte[] file.
	 * @param limit 제한 용량(KB).
	 * @return true/false
	 */
	public static boolean fileSizeValidate(byte[] file, int limit) {
		if(limit <= 0) {
			return true;
		}
		return file.length > (limit * 1024) ? false : true;
	}

	/**
	 * 	업로드 경로 문자열 생성<br>
	 *		형식 : /업무명/2012/1212
	 *
	 * @param type 업무구분,업로드구분
	 * @return 업로드 경로 문자열
	 */
	public static String getUploadDirDateFormat(String type) {

		String dayString = new SimpleDateFormat("yyyy/MM").format(new Date());
		return new StringBuilder(type).append("/").append(dayString).toString();

	}

	/**
	 * 	날짜와 임의의 숫자로 이루어진 파일명 문자열을 얻는다.
	 *
	 * @return 파일명
	 */
	public static String getRandomFileName() {
		String dayString = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String randomString = String.valueOf(new Random().nextInt(10000000));
		while(randomString.length() < 8) {
			randomString = "0" + randomString;
		}
		String ret = dayString + randomString;

		return ret;
	}

	/**
	 * <br>
	 * 	해당 데이터셋의 파일들을 "업무pk+파일시퀀스"의 형태로 리네임하여 move시킴<br>
	 * temp방식으로 파일이 업로드되어 있는 경우, 서비스에서 renameTempFile()를 호출하여 기 업로드된 파일의 move 작업을 해 주어야 한다.	
	 * 
	 * 	1. move 대상 디렉토리 생성
	 *  2. move 대상 파일의 생성(빈 파일)
	 *  3. 기존에 존재하는 파일일 경우 초기화
	 *  4. 파일의 물리적 move 작업 수행
	 *  5. 변경된 파일의 용량이 0이면 실패 처리한다.
	 *  6. move 이후의 디렉토리 경로를 데이터셋에 재설정
	 *  7. move 이후의 파일 경로를 데이터셋에 재설정
	 * <br>
	 * @param uploadInfo	파일데이터셋(GoodseedDataset)
	 * @param refKey 업무상pk값
	 * @param bizType 업무구분 (affilliate, reserve, temp ...)
	 * @param i (GoodseedDataset 루프의 row 인덱스)
	 * @param fileSeq (업무pk별 각 파일의 시퀀스값)
	 *
	 * @return 파일 move 성공 여부
	 */
	public static boolean renameTempFile(GoodseedDataset uploadInfo, String refKey, String bizType, int i, String fileSeq) {

		boolean ret = true;
		//업로드된 temp파일의 경로
		String tempUploadedDirPath = uploadInfo.getColumnAsString(i, UPLOADED_DIR_PATH);
		//업로드된 temp파일명
		String tempFileNm = uploadInfo.getColumnAsString(i, UPLOADED_FILE_NM);
		//업로드된 temp파일의 전체경로
		String tempFileFullPath =
				new StringBuilder(GoodseedConfig.getUploadRootDir()).append("/").append(tempUploadedDirPath).append("/")
						.append(tempFileNm).toString();
		//위의 경로정보로 temp파일 획득
		File tempFile = new File(tempFileFullPath);

		//업무pk(16) + 파일시퀀스(4)의 룰을 적용한 새 파일명
		String renamedFileNameWithPk = HtmlFileUploader.getFileNameWithPk(refKey, fileSeq);
		//move 대상 디렉토리
		String renamedFileDirPath = HtmlFileUploader.getUploadDir(bizType, renamedFileNameWithPk);
		//move대상 디렉토리의 전체경로
		String renamedFileDirFullPath =
				new StringBuilder(GoodseedConfig.getUploadRootDir()).append("/").append(renamedFileDirPath).toString();

		File renamedFileDir = new File(renamedFileDirFullPath);

		if(!renamedFileDir.exists()) {
			//move 대상 디렉토리 생성
			renamedFileDir.mkdirs();

			if("/".equals(File.separator) && "true".equals(Config.getString("upload.setDirectoryPermission"))) {
				chmod(renamedFileDir, Config.getString("upload.directoryPermission"));
				String parentPath = renamedFileDir.getParent();
				while(parentPath != null) {
					if(parentPath.equals(Config.getString("upload.dir.root"))) {
						break;
					}
					File parentDir = new File(parentPath);
					chmod(parentDir, Config.getString("upload.directoryPermission"));
					parentPath = parentDir.getParent();
				}
			}
		}

		//move 대상 파일의 생성(아직 빈 파일임)
		File renamedFile = new File(renamedFileDirFullPath + "/", renamedFileNameWithPk);

		renamedFile.setReadable(true, false);

		//기존에 존재하는 파일일 경우 초기화
		if(renamedFile.length() > 0) {
			renamedFile.delete();
			//move 대상 파일의 생성(아직 빈 파일임)
			renamedFile = new File(renamedFileDirFullPath + "/", renamedFileNameWithPk);
		}

		//파일의 물리적 move 작업 수행
		tempFile.renameTo(renamedFile);
		//변경된 파일의 용량이 0이면 실패 처리한다.
		if(renamedFile.length() == 0) {
			ret = false;
		}
		//move 이후의 디렉토리 경로를 데이터셋에 재설정
		uploadInfo.setColumn(i, UPLOADED_DIR_PATH, renamedFileDirPath);
		//move 이후의 파일 경로를 데이터셋에 재설정
		uploadInfo.setColumn(i, UPLOADED_FILE_NM, renamedFileNameWithPk);

		if(!ret) {
			throw new SystemException("failed to move temp file...");
		}

		return ret;
	}

	/**
	 * 해당 데이터셋의 파일들을 recycleBinDirPath로 move시킴
	 * 업로드파일 데이터셋의 삭제된 ROW가 존재할 때 실행되는 함수임
	 * @param uploadInfo 업로드 데이타셋 객체
	 * @param i 컬럼 index
	 */
	public static void moveToRecycleBin(GoodseedDataset uploadInfo, int i) {

		//업로드된 파일의 경로
		String uploadedDirPath = uploadInfo.getDeleteColumnAsString(i, UPLOADED_DIR_PATH);
		//업로드된 파일명
		String uploadedFileName = uploadInfo.getDeleteColumnAsString(i, UPLOADED_FILE_NM);
		//업로드된 파일의 전체경로
		String uploadedFileFullPath =
				new StringBuilder(GoodseedConfig.getUploadRootDir()).append("/").append(uploadedDirPath).append("/")
						.append(uploadedFileName).toString();
		//위의 경로정보로 파일 획득
		File uploadfile = new File(uploadedFileFullPath);

		String recycleBinDirPath = GoodseedConfig.getUploadDir(RECYCLEB_IN);
		//move대상 디렉토리의 전체경로 
		String recycleBinDirFullPath =
				new StringBuilder(GoodseedConfig.getUploadRootDir()).append("/").append(recycleBinDirPath).toString();

		File recycleBinDir = new File(recycleBinDirFullPath);

		if(!recycleBinDir.exists()) {
			//move 대상 디렉토리 생성
			recycleBinDir.mkdirs();

			if("/".equals(File.separator) && "true".equals(Config.getString("upload.setDirectoryPermission"))) {
				chmod(recycleBinDir, Config.getString("upload.directoryPermission"));
				String parentPath = recycleBinDir.getParent();
				while(parentPath != null) {
					if(parentPath.equals(Config.getString("upload.dir.root"))) {
						break;
					}
					File parentDir = new File(parentPath);
					chmod(parentDir, Config.getString("upload.directoryPermission"));
					parentPath = parentDir.getParent();
				}
			}
		}

		//move 대상 파일의 생성(아직 빈 파일임)
		File moveFile = new File(recycleBinDirFullPath + "/", uploadedFileName);

		moveFile.setReadable(true, false);

		//기존에 존재하는 파일일 경우 초기화
		if(moveFile.length() > 0) {
			moveFile.delete();
			//move 대상 파일의 생성(아직 빈 파일임)
			moveFile = new File(recycleBinDirFullPath + "/", uploadedFileName);
		}

		//파일의 물리적 move 작업 수행
		uploadfile.renameTo(moveFile);
		//변경된 파일의 용량이 0이면 실패 처리한다.
		if(moveFile.length() == 0) {
			throw new SystemException("failed to move temp file...");
		}
	}

	/**
	 * 	업무pk와 파일시퀀스가 혼합된 형태의 파일명 리턴<br>
	 * <br>
	 * 		업무pk 영역 : 16자리<br>
	 * 		파일시퀀스 영역 : 4자리<br>
	 * 		총 20자리의 문자열 리턴
	 *
	 * @param refKey
	 * @param fileSeq 파일시퀀스
	 * @return
	 */
	public static String getFileNameWithPk(String refKey, String fileSeq) {
		String localRefKey = refKey;
		String localFileSeq = fileSeq;

		if(localRefKey != null && localRefKey.length() > 0) {
			while(localRefKey.length() < 16) {
				localRefKey = "0" + localRefKey;
			}
		}
		if(localFileSeq != null && localFileSeq.length() > 0) {
			while(localFileSeq.length() < 4) {
				localFileSeq = "0" + localFileSeq;
			}
		} else {
			localFileSeq = "";
		}
		String uploadedFileName = localRefKey + localFileSeq;
		while(uploadedFileName.length() < 20) {
			uploadedFileName = "0" + uploadedFileName;
		}

		return uploadedFileName;
	}

	private static void chmod(File f, String perm) {
		ExecuteUtil.execute("chmod " + perm + " " + f.getAbsolutePath(), 2000);
	}

	/**
	 * 업로드 경로를 생성.<br>
	 * <br>
	 *
	 * @param type 업무구분,업로드구분
	 * @param uploadedFileName
	 * @return 업로드 경로
	 */
	public static String getUploadDir(String type, String uploadedFileName) {

		String oneDepth = uploadedFileName.substring(0, 4);
		String twoDepth = uploadedFileName.substring(4, 8);
		String threeDepth = uploadedFileName.substring(8, 11);
		String fourDepth = uploadedFileName.substring(11, 14);
		String fiveDepth = uploadedFileName.substring(14, 17);
		StringBuilder result = new StringBuilder(type);
		result.append("/").append(oneDepth).append("/");
		result.append(twoDepth).append("/").append(threeDepth).append("/");
		result.append(fourDepth).append("/").append(fiveDepth);

		return result.toString();
	}

	/**
	 * EXTJS용 임시파일에서 실제 경로로 파일 이동처리
	 * renameTempFile과 동일한 구조임
	 * 
	 * @param fileFullPath
	 * @param sourceFileName
	 * @param fileSize
	 * @param refKey
	 * @param bizType
	 * @return
	 */
	public static HashMap<String, Object> renameTempFileExtJs(String fileFullPath, String sourceFileName, String fileSize,
			String refKey, String bizType) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		File tempFile = new File(GoodseedConfig.getUploadRootDir() + "/" + fileFullPath);
		String bizPath = getUploadDirDateFormat(bizType);
		String uploadPath = GoodseedConfig.getUploadRootDir() + "/" + bizPath;

		try {
			File uploadDir = new File(uploadPath);
			if(!uploadDir.exists()) {
				uploadDir.mkdirs();
				if("/".equals(File.separator) && "true".equals(Config.getString("upload.setDirectoryPermission"))) {
					chmod(uploadDir, Config.getString("upload.directoryPermission"));
					String parentPath = uploadDir.getParent();
					while(parentPath != null) {
						if(parentPath.equals(Config.getString("upload.dir.root"))) {
							break;
						}
						File parentDir = new File(parentPath);
						chmod(parentDir, Config.getString("upload.directoryPermission"));
						parentPath = parentDir.getParent();
					}
				}
			}
			String renameFileName = getRandomFileName();
			File renamedFile = new File(uploadPath + "/" + renameFileName);
			renamedFile.setReadable(true, false);

			//기존에 존재하는 파일일 경우 초기화
			if(renamedFile.length() > 0) {
				renamedFile.delete();
				renamedFile = new File(uploadDir + "/" + renameFileName);
			}

			//파일의 물리적 move 작업 수행
			tempFile.renameTo(renamedFile);

			//변경된 파일의 용량이 0이면 실패 처리한다.
			if(renamedFile.length() == 0) {
				throw new SystemException("failed to move temp file...");
			} else {
				resultMap.put("SOURCE_FILE_NM", sourceFileName);
				resultMap.put("UPLOADED_DIR_PATH", bizPath);
				resultMap.put("UPLOADED_FILE_NM", renameFileName);
				resultMap.put("FILE_SIZE", renamedFile.length());
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new SystemException("failed to move temp file...");
		}
		return resultMap;
	}

}
